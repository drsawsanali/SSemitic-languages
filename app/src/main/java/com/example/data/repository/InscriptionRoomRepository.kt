package com.example.data.repository

import com.example.data.local.dao.InscriptionDao
import com.example.data.local.entity.InscriptionEntity
import kotlinx.coroutines.flow.Flow

class InscriptionRoomRepository(
  private val inscriptionDao: InscriptionDao
) {

  val allInscriptions: Flow<List<InscriptionEntity>> = inscriptionDao.getAllInscriptions()

  val distinctPeriods: Flow<List<String>> = inscriptionDao.getDistinctPeriods()

  val distinctRegions: Flow<List<String>> = inscriptionDao.getDistinctRegions()

  val distinctLanguages: Flow<List<String>> = inscriptionDao.getDistinctLanguages()

  val favoriteInscriptions: Flow<List<InscriptionEntity>> = inscriptionDao.getFavoriteInscriptions()

  fun searchByNamePeriodRegion(
    nameQuery: String,
    periodQuery: String,
    regionQuery: String
  ): Flow<List<InscriptionEntity>> {
    return inscriptionDao.searchInscriptionsByNamePeriodRegion(
      nameQuery = nameQuery.trim(),
      periodQuery = if (periodQuery == "الكل" || periodQuery == "All") "" else periodQuery.trim(),
      regionQuery = if (regionQuery == "الكل" || regionQuery == "All") "" else regionQuery.trim()
    )
  }

  fun searchComprehensive(
    query: String,
    period: String,
    region: String,
    family: String
  ): Flow<List<InscriptionEntity>> {
    return inscriptionDao.searchInscriptionsComprehensive(
      query = query.trim(),
      period = if (period == "الكل" || period == "All") "" else period.trim(),
      region = if (region == "الكل" || region == "All") "" else region.trim(),
      family = if (family == "الكل" || family == "All") "" else family.trim()
    )
  }

  fun getInscriptionById(id: String): Flow<InscriptionEntity?> {
    return inscriptionDao.getInscriptionById(id)
  }

  suspend fun toggleFavorite(id: String, currentStatus: Boolean) {
    inscriptionDao.updateFavoriteStatus(id, !currentStatus)
  }

  suspend fun insertInscription(inscription: InscriptionEntity) {
    inscriptionDao.insertInscription(inscription)
  }

  suspend fun updateInscription(inscription: InscriptionEntity) {
    inscriptionDao.updateInscription(inscription)
  }

  suspend fun deleteInscription(id: String) {
    inscriptionDao.deleteInscriptionById(id)
  }

  suspend fun seedIfEmpty() {
    if (inscriptionDao.getCount() == 0) {
      val entities = InscriptionsData.list.map { InscriptionEntity.fromAcademicInscription(it) }
      inscriptionDao.insertInscriptions(entities)
    }
  }
}
