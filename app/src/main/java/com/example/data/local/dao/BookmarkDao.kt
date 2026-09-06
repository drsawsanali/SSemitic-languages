package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {

  @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
  fun getAllBookmarks(): Flow<List<BookmarkEntity>>

  @Query("SELECT * FROM bookmarks WHERE itemType = :type ORDER BY timestamp DESC")
  fun getBookmarksByType(type: String): Flow<List<BookmarkEntity>>

  @Query("SELECT * FROM bookmarks WHERE id = :id LIMIT 1")
  fun getBookmarkById(id: String): Flow<BookmarkEntity?>

  @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :id)")
  fun isBookmarked(id: String): Flow<Boolean>

  @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :id)")
  suspend fun isBookmarkedSync(id: String): Boolean

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBookmark(bookmark: BookmarkEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBookmarks(bookmarks: List<BookmarkEntity>)

  @Query("DELETE FROM bookmarks WHERE id = :id")
  suspend fun deleteBookmarkById(id: String)

  @Query("DELETE FROM bookmarks WHERE itemId = :itemId AND itemType = :itemType")
  suspend fun deleteBookmarkByItem(itemId: String, itemType: String)

  @Query("UPDATE bookmarks SET customNotes = :notes WHERE id = :id")
  suspend fun updateBookmarkNotes(id: String, notes: String)

  @Query("UPDATE bookmarks SET collectionName = :collection WHERE id = :id")
  suspend fun updateBookmarkCollection(id: String, collection: String)

  @Query("SELECT DISTINCT collectionName FROM bookmarks WHERE collectionName != '' ORDER BY collectionName ASC")
  fun getAllCollections(): Flow<List<String>>

  @Query("SELECT COUNT(*) FROM bookmarks")
  fun getBookmarkCount(): Flow<Int>

  @Query("SELECT COUNT(*) FROM bookmarks")
  suspend fun getCountSync(): Int

  @Query("DELETE FROM bookmarks")
  suspend fun clearAllBookmarks()
}
