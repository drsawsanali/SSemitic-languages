package com.example.data.repository

import com.example.data.local.dao.BookmarkDao
import com.example.data.local.entity.BookmarkEntity
import com.example.data.models.AcademicInscription
import com.example.data.models.EpigraphicGlossaryTerm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BookmarkRoomRepository(
  private val bookmarkDao: BookmarkDao
) {
  val allBookmarks: Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()
  val inscriptionBookmarks: Flow<List<BookmarkEntity>> = bookmarkDao.getBookmarksByType("INSCRIPTION")
  val glossaryBookmarks: Flow<List<BookmarkEntity>> = bookmarkDao.getBookmarksByType("GLOSSARY")
  val totalCount: Flow<Int> = bookmarkDao.getBookmarkCount()

  // Set of bookmarked IDs for fast UI lookup in lists
  val bookmarkedIdSet: Flow<Set<String>> = allBookmarks.map { list ->
    list.map { it.id }.toSet()
  }

  fun isBookmarked(id: String): Flow<Boolean> = bookmarkDao.isBookmarked(id)

  suspend fun isBookmarkedSync(id: String): Boolean = bookmarkDao.isBookmarkedSync(id)

  suspend fun bookmarkInscription(inscription: AcademicInscription, customNotes: String = "") {
    val entity = BookmarkEntity(
      id = "INSCRIPTION_${inscription.id}",
      itemId = inscription.id,
      itemType = "INSCRIPTION",
      titleAr = inscription.titleAr,
      titleEn = inscription.title,
      categoryOrBranch = "${inscription.language} (${inscription.family})",
      snippetAr = inscription.translationAr.ifBlank { inscription.notesAr },
      transliteration = inscription.transliteration,
      originalScriptText = inscription.originalScriptText,
      siteOrWitness = "${inscription.site} • ${inscription.period}",
      citation = "${inscription.ruler} • ${inscription.corpus}",
      customNotes = customNotes
    )
    bookmarkDao.insertBookmark(entity)
  }

  suspend fun bookmarkGlossaryTerm(term: EpigraphicGlossaryTerm, customNotes: String = "") {
    val entity = BookmarkEntity(
      id = "GLOSSARY_${term.id}",
      itemId = term.id,
      itemType = "GLOSSARY",
      titleAr = term.termAr,
      titleEn = term.termEn,
      categoryOrBranch = term.categoryAr,
      snippetAr = term.shortDefinitionAr,
      transliteration = term.transliteration,
      originalScriptText = term.ancientSampleGlyphs,
      siteOrWitness = term.inscriptionalWitnessesAr,
      citation = term.citation,
      customNotes = customNotes
    )
    bookmarkDao.insertBookmark(entity)
  }

  suspend fun removeBookmark(id: String) {
    bookmarkDao.deleteBookmarkById(id)
  }

  suspend fun removeBookmarkByItem(itemId: String, itemType: String) {
    bookmarkDao.deleteBookmarkByItem(itemId, itemType)
  }

  suspend fun toggleInscriptionBookmark(inscription: AcademicInscription): Boolean {
    val bookmarkId = "INSCRIPTION_${inscription.id}"
    return if (bookmarkDao.isBookmarkedSync(bookmarkId)) {
      bookmarkDao.deleteBookmarkById(bookmarkId)
      false
    } else {
      bookmarkInscription(inscription)
      true
    }
  }

  suspend fun toggleGlossaryBookmark(term: EpigraphicGlossaryTerm): Boolean {
    val bookmarkId = "GLOSSARY_${term.id}"
    return if (bookmarkDao.isBookmarkedSync(bookmarkId)) {
      bookmarkDao.deleteBookmarkById(bookmarkId)
      false
    } else {
      bookmarkGlossaryTerm(term)
      true
    }
  }

  suspend fun updateBookmarkNotes(id: String, notes: String) {
    bookmarkDao.updateBookmarkNotes(id, notes)
  }

  suspend fun updateBookmarkCollection(id: String, collection: String) {
    bookmarkDao.updateBookmarkCollection(id, collection)
  }

  val allCollections: Flow<List<String>> = bookmarkDao.getAllCollections()

  suspend fun clearAllBookmarks() {
    bookmarkDao.clearAllBookmarks()
  }

  suspend fun seedSampleBookmarksIfEmpty() {
    if (bookmarkDao.getCountSync() == 0) {
      // Seed a sample inscription and a sample glossary term so collection view is demonstrated
      val hammurabi = InscriptionsData.list.find { it.id == "insc-01" }
      if (hammurabi != null) {
        bookmarkInscription(hammurabi, "وثيقة محورية لدراسة القانون والأكادية البابلية القديمة.")
      }
      val boustrophedon = EpigraphicGlossaryData.terms.find { it.id == "epig-boustrophedon" }
      if (boustrophedon != null) {
        bookmarkGlossaryTerm(boustrophedon, "نمط تدوين مميز في نقوش المسند السبئي والإغريقي القديم.")
      }
    }
  }
}
