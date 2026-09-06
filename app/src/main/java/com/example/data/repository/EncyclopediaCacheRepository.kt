package com.example.data.repository

import com.example.data.local.dao.EncyclopediaCacheDao
import com.example.data.local.entity.CachedEncyclopediaEntryEntity
import com.example.data.models.AcademicChapter
import com.example.data.models.AcademicInscription
import com.example.data.models.EpigraphicGlossaryTerm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EncyclopediaCacheRepository(
  private val dao: EncyclopediaCacheDao
) {

  val downloadedEntries: Flow<List<CachedEncyclopediaEntryEntity>> = dao.getDownloadedLibraryEntries()

  val downloadedCount: Flow<Int> = dao.getDownloadedCount()

  val totalStorageBytes: Flow<Long> = dao.getTotalStorageSizeBytes().map { it ?: 0L }

  val downloadedOriginalIds: Flow<List<String>> = dao.getAllDownloadedOriginalIds()

  val downloadedOriginalIdSet: Flow<Set<String>> = downloadedOriginalIds.map { it.toSet() }

  fun isEntryDownloaded(originalId: String): Flow<Boolean> = dao.isEntryDownloadedFlow(originalId)

  fun getEntryById(id: String): Flow<CachedEncyclopediaEntryEntity?> = dao.getCachedEntryById(id)

  fun getEntryByOriginalId(originalId: String): Flow<CachedEncyclopediaEntryEntity?> = dao.getCachedEntryByOriginalId(originalId)

  fun searchLibrary(query: String, entryType: String = "ALL"): Flow<List<CachedEncyclopediaEntryEntity>> {
    return if (entryType == "ALL") {
      dao.searchDownloadedLibrary(query.trim())
    } else {
      dao.searchDownloadedLibraryFiltered(query.trim(), entryType)
    }
  }

  suspend fun cacheChapter(chapter: AcademicChapter, isDownloaded: Boolean = true) {
    val entity = CachedEncyclopediaEntryEntity.fromAcademicChapter(chapter, isDownloaded)
    dao.insertEntry(entity)
  }

  suspend fun cacheSourceRecord(source: SourceRecord, isDownloaded: Boolean = true) {
    val entity = CachedEncyclopediaEntryEntity.fromSourceRecord(source, isDownloaded)
    dao.insertEntry(entity)
  }

  suspend fun cacheInscription(inscription: AcademicInscription, isDownloaded: Boolean = true) {
    val entity = CachedEncyclopediaEntryEntity.fromAcademicInscription(inscription, isDownloaded)
    dao.insertEntry(entity)
  }

  suspend fun cacheGlossaryTerm(term: EpigraphicGlossaryTerm, isDownloaded: Boolean = true) {
    val entity = CachedEncyclopediaEntryEntity.fromEpigraphicGlossaryTerm(term, isDownloaded)
    dao.insertEntry(entity)
  }

  suspend fun toggleDownload(originalId: String, currentStatus: Boolean) {
    dao.setDownloadedStatusByOriginalId(originalId, !currentStatus)
  }

  suspend fun deleteFromLibrary(originalId: String) {
    dao.deleteEntryByOriginalId(originalId)
  }

  suspend fun clearEntireLibrary() {
    dao.clearAll()
  }

  suspend fun cacheAllChapters() {
    val chapters = ChaptersData.list.map {
      CachedEncyclopediaEntryEntity.fromAcademicChapter(it, isDownloaded = true)
    }
    dao.insertEntries(chapters)
  }

  suspend fun cacheAllSources(sources: List<SourceRecord> = BooksSourcesRepository.defaultSources) {
    val sourceEntities = sources.map {
      CachedEncyclopediaEntryEntity.fromSourceRecord(it, isDownloaded = true)
    }
    dao.insertEntries(sourceEntities)
  }

  suspend fun preloadEntireLibrary(allSources: List<SourceRecord> = BooksSourcesRepository.defaultSources) {
    val chapters = ChaptersData.list.map {
      CachedEncyclopediaEntryEntity.fromAcademicChapter(it, isDownloaded = true)
    }
    val sources = allSources.map {
      CachedEncyclopediaEntryEntity.fromSourceRecord(it, isDownloaded = true)
    }
    val inscriptions = InscriptionsData.list.map {
      CachedEncyclopediaEntryEntity.fromAcademicInscription(it, isDownloaded = true)
    }
    val glossaryTerms = EpigraphicGlossaryData.terms.take(20).map {
      CachedEncyclopediaEntryEntity.fromEpigraphicGlossaryTerm(it, isDownloaded = true)
    }
    dao.insertEntries(chapters + sources + inscriptions + glossaryTerms)
  }
}
