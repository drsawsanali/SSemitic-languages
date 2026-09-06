package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.InscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InscriptionDao {

  @Query("SELECT * FROM inscriptions ORDER BY id ASC")
  fun getAllInscriptions(): Flow<List<InscriptionEntity>>

  @Query("""
    SELECT * FROM inscriptions
    WHERE (
      :nameQuery = '' OR
      title LIKE '%' || :nameQuery || '%' OR
      titleAr LIKE '%' || :nameQuery || '%' OR
      ruler LIKE '%' || :nameQuery || '%' OR
      corpus LIKE '%' || :nameQuery || '%' OR
      transliteration LIKE '%' || :nameQuery || '%' OR
      originalScriptText LIKE '%' || :nameQuery || '%' OR
      notesAr LIKE '%' || :nameQuery || '%'
    )
    AND (:periodQuery = '' OR period LIKE '%' || :periodQuery || '%')
    AND (:regionQuery = '' OR region LIKE '%' || :regionQuery || '%' OR site LIKE '%' || :regionQuery || '%')
    ORDER BY id ASC
  """)
  fun searchInscriptionsByNamePeriodRegion(
    nameQuery: String,
    periodQuery: String,
    regionQuery: String
  ): Flow<List<InscriptionEntity>>

  @Query("""
    SELECT * FROM inscriptions
    WHERE (
      :query = '' OR
      title LIKE '%' || :query || '%' OR
      titleAr LIKE '%' || :query || '%' OR
      ruler LIKE '%' || :query || '%' OR
      transliteration LIKE '%' || :query || '%' OR
      originalScriptText LIKE '%' || :query || '%'
    )
    AND (:period = '' OR period LIKE '%' || :period || '%')
    AND (:region = '' OR region LIKE '%' || :region || '%' OR site LIKE '%' || :region || '%')
    AND (:family = '' OR family LIKE '%' || :family || '%' OR language LIKE '%' || :family || '%')
    ORDER BY id ASC
  """)
  fun searchInscriptionsComprehensive(
    query: String,
    period: String,
    region: String,
    family: String
  ): Flow<List<InscriptionEntity>>

  @Query("SELECT DISTINCT period FROM inscriptions WHERE period != '' ORDER BY period ASC")
  fun getDistinctPeriods(): Flow<List<String>>

  @Query("SELECT DISTINCT region FROM inscriptions WHERE region != '' ORDER BY region ASC")
  fun getDistinctRegions(): Flow<List<String>>

  @Query("SELECT DISTINCT language FROM inscriptions WHERE language != '' ORDER BY language ASC")
  fun getDistinctLanguages(): Flow<List<String>>

  @Query("SELECT * FROM inscriptions WHERE id = :id LIMIT 1")
  fun getInscriptionById(id: String): Flow<InscriptionEntity?>

  @Query("SELECT * FROM inscriptions WHERE isFavorite = 1 ORDER BY id ASC")
  fun getFavoriteInscriptions(): Flow<List<InscriptionEntity>>

  @Query("SELECT COUNT(*) FROM inscriptions")
  suspend fun getCount(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertInscriptions(inscriptions: List<InscriptionEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertInscription(inscription: InscriptionEntity)

  @Update
  suspend fun updateInscription(inscription: InscriptionEntity)

  @Query("UPDATE inscriptions SET isFavorite = :isFavorite WHERE id = :id")
  suspend fun updateFavoriteStatus(id: String, isFavorite: Boolean)

  @Query("DELETE FROM inscriptions WHERE id = :id")
  suspend fun deleteInscriptionById(id: String)
}
