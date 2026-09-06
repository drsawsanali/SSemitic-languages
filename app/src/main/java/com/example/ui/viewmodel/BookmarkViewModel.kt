package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.BookmarkEntity
import com.example.data.models.AcademicInscription
import com.example.data.models.EpigraphicGlossaryTerm
import com.example.data.repository.BookmarkRoomRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BookmarkFilterState(
  val query: String = "",
  val selectedType: String = "ALL", // "ALL", "INSCRIPTION", "GLOSSARY"
  val selectedCollection: String = "ALL" // "ALL" or collection name
)

@OptIn(ExperimentalCoroutinesApi::class)
class BookmarkViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: BookmarkRoomRepository

  private val _filterState = MutableStateFlow(BookmarkFilterState())
  val filterState: StateFlow<BookmarkFilterState> = _filterState.asStateFlow()

  val allBookmarks: StateFlow<List<BookmarkEntity>>
  val filteredBookmarks: StateFlow<List<BookmarkEntity>>
  val totalCount: StateFlow<Int>
  val inscriptionCount: StateFlow<Int>
  val glossaryCount: StateFlow<Int>
  val bookmarkedIdSet: StateFlow<Set<String>>
  val allCollections: StateFlow<List<String>>

  init {
    val db = AppDatabase.getDatabase(application)
    repository = BookmarkRoomRepository(db.bookmarkDao())

    viewModelScope.launch {
      repository.seedSampleBookmarksIfEmpty()
    }

    allBookmarks = repository.allBookmarks
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    totalCount = repository.totalCount
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    inscriptionCount = repository.inscriptionBookmarks
      .map { it.size }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    glossaryCount = repository.glossaryBookmarks
      .map { it.size }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    bookmarkedIdSet = repository.bookmarkedIdSet
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    allCollections = repository.allCollections
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("العامة"))

    filteredBookmarks = combine(allBookmarks, _filterState) { bookmarks, filter ->
      bookmarks.filter { item ->
        val matchesType = when (filter.selectedType) {
          "INSCRIPTION" -> item.itemType == "INSCRIPTION"
          "GLOSSARY" -> item.itemType == "GLOSSARY"
          else -> true
        }

        val matchesCollection = when (filter.selectedCollection) {
          "ALL" -> true
          else -> item.collectionName.equals(filter.selectedCollection, ignoreCase = true)
        }

        val q = filter.query.trim()
        val matchesQuery = q.isEmpty() ||
          item.titleAr.contains(q, ignoreCase = true) ||
          item.titleEn.contains(q, ignoreCase = true) ||
          item.categoryOrBranch.contains(q, ignoreCase = true) ||
          item.snippetAr.contains(q, ignoreCase = true) ||
          item.transliteration.contains(q, ignoreCase = true) ||
          item.originalScriptText.contains(q, ignoreCase = true) ||
          item.collectionName.contains(q, ignoreCase = true) ||
          item.customNotes.contains(q, ignoreCase = true)

        matchesType && matchesCollection && matchesQuery
      }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  }

  fun updateQuery(query: String) {
    _filterState.update { it.copy(query = query) }
  }

  fun selectType(type: String) {
    _filterState.update { it.copy(selectedType = type) }
  }

  fun selectCollection(collection: String) {
    _filterState.update { it.copy(selectedCollection = collection) }
  }

  fun moveToCollection(id: String, collectionName: String) {
    viewModelScope.launch {
      repository.updateBookmarkCollection(id, collectionName)
    }
  }

  fun toggleInscriptionBookmark(inscription: AcademicInscription) {
    viewModelScope.launch {
      repository.toggleInscriptionBookmark(inscription)
    }
  }

  fun toggleGlossaryBookmark(term: EpigraphicGlossaryTerm) {
    viewModelScope.launch {
      repository.toggleGlossaryBookmark(term)
    }
  }

  fun removeBookmark(id: String) {
    viewModelScope.launch {
      repository.removeBookmark(id)
    }
  }

  fun updateNotes(id: String, notes: String) {
    viewModelScope.launch {
      repository.updateBookmarkNotes(id, notes)
    }
  }

  fun clearAll() {
    viewModelScope.launch {
      repository.clearAllBookmarks()
    }
  }
}
