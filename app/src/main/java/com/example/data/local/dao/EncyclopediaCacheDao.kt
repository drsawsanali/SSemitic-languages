package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.CachedEncyclopediaEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EncyclopediaCacheDao {

  @Query("SELECT * FROM cached_encyclopedia_entries ORDER BY cachedAt DESC")
  fun getAllCachedEntries(): Flow<List<CachedEncyclopediaEntryEntity>>

  @Query("SELECT * FROM cached_encyclopedia_entries WHERE isDownloaded = 1 ORDER BY cachedAt DESC")
  fun getDownloadedLibraryEntries(): Flow<List<CachedEncyclopediaEntryEntity>>

  @Query("SELECT * FROM cached_encyclopedia_entries WHERE entryType = :entryType AND isDownloaded = 1 ORDER BY cachedAt DESC")
  fun getDownloadedEntriesByType(entryType: String): Flow<List<CachedEncyclopediaEntryEntity>>

  @Query("SELECT * FROM cached_encyclopedia_entries WHERE id = :id LIMIT 1")
  fun getCachedEntryById(id: String): Flow<CachedEncyclopediaEntryEntity?>

  @Query("SELECT * FROM cached_encyclopedia_entries WHERE originalId = :originalId LIMIT 1")
  fun getCachedEntryByOriginalId(originalId: String): Flow<CachedEncyclopediaEntryEntity?>

  @Query("SELECT * FROM cached_encyclopedia_entries WHERE originalId = :originalId LIMIT 1")
  suspend fun getCachedEntryByOriginalIdDirect(originalId: String): CachedEncyclopediaEntryEntity?

  @Query("SELECT EXISTS(SELECT 1 FROM cached_encyclopedia_entries WHERE originalId = :originalId AND isDownloaded = 1)")
  fun isEntryDownloadedFlow(originalId: String): Flow<Boolean>

  @Query("SELECT EXISTS(SELECT 1 FROM cached_encyclopedia_entries WHERE originalId = :originalId AND isDownloaded = 1)")
  suspend fun isEntryDownloadedDirect(originalId: String): Boolean

  @Query("SELECT originalId FROM cached_encyclopedia_entries WHERE isDownloaded = 1")
  fun getAllDownloadedOriginalIds(): Flow<List<String>>

  @Query("""
    SELECT * FROM cached_encyclopedia_entries
    WHERE isDownloaded = 1 AND (
      :query = '' OR
      titleAr LIKE '%' || :query || '%' OR
      titleEn LIKE '%' || :query || '%' OR
      summaryAr LIKE '%' || :query || '%' OR
      summaryEn LIKE '%' || :query || '%' OR
      categoryOrBranch LIKE '%' || :query || '%' OR
      languageName LIKE '%' || :query || '%' OR
      authorOrRuler LIKE '%' || :query || '%' OR
      fullContentAr LIKE '%' || :query || '%'
    )
    ORDER BY cachedAt DESC
  """)
  fun searchDownloadedLibrary(query: String): Flow<List<CachedEncyclopediaEntryEntity>>

  @Query("""
    SELECT * FROM cached_encyclopedia_entries
    WHERE isDownloaded = 1 
      AND (:entryType = 'ALL' OR entryType = :entryType)
      AND (
        :query = '' OR
        titleAr LIKE '%' || :query || '%' OR
        titleEn LIKE '%' || :query || '%' OR
        summaryAr LIKE '%' || :query || '%' OR
        summaryEn LIKE '%' || :query || '%' OR
        categoryOrBranch LIKE '%' || :query || '%' OR
        languageName LIKE '%' || :query || '%' OR
        authorOrRuler LIKE '%' || :query || '%'
      )
    ORDER BY cachedAt DESC
  """)
  fun searchDownloadedLibraryFiltered(query: String, entryType: String): Flow<List<CachedEncyclopediaEntryEntity>>

  @Query("SELECT COUNT(*) FROM cached_encyclopedia_entries WHERE isDownloaded = 1")
  fun getDownloadedCount(): Flow<Int>

  @Query("SELECT COUNT(*) FROM cached_encyclopedia_entries")
  suspend fun getTotalCountDirect(): Int

  @Query("SELECT SUM(downloadSizeBytes) FROM cached_encyclopedia_entries WHERE isDownloaded = 1")
  fun getTotalStorageSizeBytes(): Flow<Long?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEntry(entry: CachedEncyclopediaEntryEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEntries(entries: List<CachedEncyclopediaEntryEntity>)

  @Update
  suspend fun updateEntry(entry: CachedEncyclopediaEntryEntity)

  @Query("UPDATE cached_encyclopedia_entries SET isDownloaded = :isDownloaded, cachedAt = :timestamp WHERE originalId = :originalId")
  suspend fun setDownloadedStatusByOriginalId(originalId: String, isDownloaded: Boolean, timestamp: Long = System.currentTimeMillis())

  @Query("DELETE FROM cached_encyclopedia_entries WHERE id = :id")
  suspend fun deleteEntryById(id: String)

  @Query("DELETE FROM cached_encyclopedia_entries WHERE originalId = :originalId")
  suspend fun deleteEntryByOriginalId(originalId: String)

  @Query("DELETE FROM cached_encyclopedia_entries")
  suspend fun clearAll()
}
