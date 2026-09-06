package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.BookmarkDao
import com.example.data.local.dao.EncyclopediaCacheDao
import com.example.data.local.dao.InscriptionDao
import com.example.data.local.entity.BookmarkEntity
import com.example.data.local.entity.CachedEncyclopediaEntryEntity
import com.example.data.local.entity.InscriptionEntity
import com.example.data.repository.BookmarkRoomRepository
import com.example.data.repository.BooksSourcesRepository
import com.example.data.repository.ChaptersData
import com.example.data.repository.InscriptionsData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [InscriptionEntity::class, BookmarkEntity::class, CachedEncyclopediaEntryEntity::class],
  version = 3,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

  abstract fun inscriptionDao(): InscriptionDao
  abstract fun bookmarkDao(): BookmarkDao
  abstract fun encyclopediaCacheDao(): EncyclopediaCacheDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "semitic_encyclopedia_database"
        )
          .fallbackToDestructiveMigration()
          .addCallback(DatabaseCallback(context.applicationContext))
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val context: Context
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Populate database in the background when created
        CoroutineScope(Dispatchers.IO).launch {
          val database = getDatabase(context)
          populateInitialData(database.inscriptionDao())
          BookmarkRoomRepository(database.bookmarkDao()).seedSampleBookmarksIfEmpty()
          seedInitialCachedLibrary(database.encyclopediaCacheDao())
        }
      }

      override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        // Ensure data is seeded if database exists but is empty
        CoroutineScope(Dispatchers.IO).launch {
          val database = getDatabase(context)
          val dao = database.inscriptionDao()
          if (dao.getCount() == 0) {
            populateInitialData(dao)
          }
          BookmarkRoomRepository(database.bookmarkDao()).seedSampleBookmarksIfEmpty()
          val cacheDao = database.encyclopediaCacheDao()
          if (cacheDao.getTotalCountDirect() == 0) {
            seedInitialCachedLibrary(cacheDao)
          }
        }
      }

      private suspend fun populateInitialData(dao: InscriptionDao) {
        val initialEntities = InscriptionsData.list.map {
          InscriptionEntity.fromAcademicInscription(it)
        }
        dao.insertInscriptions(initialEntities)
      }

      private suspend fun seedInitialCachedLibrary(dao: EncyclopediaCacheDao) {
        // Pre-populate chapters and sources so the user has immediate offline access
        val cachedChapters = ChaptersData.list.map {
          CachedEncyclopediaEntryEntity.fromAcademicChapter(it, isDownloaded = true)
        }
        val cachedSources = BooksSourcesRepository.defaultSources.map {
          CachedEncyclopediaEntryEntity.fromSourceRecord(it, isDownloaded = true)
        }
        val sampleInscriptions = InscriptionsData.list.take(5).map {
          CachedEncyclopediaEntryEntity.fromAcademicInscription(it, isDownloaded = true)
        }
        dao.insertEntries(cachedChapters + cachedSources + sampleInscriptions)
      }
    }
  }
}
