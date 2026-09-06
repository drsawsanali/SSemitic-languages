package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.InscriptionEntity
import com.example.data.repository.InscriptionRoomRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchFilterState(
  val nameQuery: String = "",
  val selectedPeriod: String = "الكل",
  val selectedRegion: String = "الكل",
  val selectedFamily: String = "الكل",
  val favoritesOnly: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class InscriptionSearchViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: InscriptionRoomRepository

  private val _filterState = MutableStateFlow(SearchFilterState())
  val filterState: StateFlow<SearchFilterState> = _filterState.asStateFlow()

  val allInscriptions: StateFlow<List<InscriptionEntity>>
  val searchResults: StateFlow<List<InscriptionEntity>>
  val distinctPeriods: StateFlow<List<String>>
  val distinctRegions: StateFlow<List<String>>
  val totalDatabaseCount: StateFlow<Int>

  init {
    val database = AppDatabase.getDatabase(application)
    repository = InscriptionRoomRepository(database.inscriptionDao())

    viewModelScope.launch {
      repository.seedIfEmpty()
    }

    allInscriptions = repository.allInscriptions
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    distinctPeriods = repository.distinctPeriods
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    distinctRegions = repository.distinctRegions
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    totalDatabaseCount = allInscriptions
      .map { it.size }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    searchResults = _filterState
      .flatMapLatest { filter ->
        if (filter.favoritesOnly) {
          repository.favoriteInscriptions.map { list ->
            filterList(list, filter)
          }
        } else {
          repository.searchByNamePeriodRegion(
            nameQuery = filter.nameQuery,
            periodQuery = filter.selectedPeriod,
            regionQuery = filter.selectedRegion
          ).map { list ->
            if (filter.selectedFamily != "الكل") {
              list.filter { item ->
                item.family.contains(filter.selectedFamily, ignoreCase = true) ||
                item.language.contains(filter.selectedFamily, ignoreCase = true)
              }
            } else {
              list
            }
          }
        }
      }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  }

  private fun filterList(list: List<InscriptionEntity>, filter: SearchFilterState): List<InscriptionEntity> {
    return list.filter { item ->
      val matchesName = filter.nameQuery.isBlank() ||
        item.title.contains(filter.nameQuery, ignoreCase = true) ||
        item.titleAr.contains(filter.nameQuery, ignoreCase = true) ||
        item.ruler.contains(filter.nameQuery, ignoreCase = true) ||
        item.transliteration.contains(filter.nameQuery, ignoreCase = true) ||
        item.originalScriptText.contains(filter.nameQuery, ignoreCase = true)

      val matchesPeriod = filter.selectedPeriod == "الكل" ||
        item.period.contains(filter.selectedPeriod, ignoreCase = true)

      val matchesRegion = filter.selectedRegion == "الكل" ||
        item.region.contains(filter.selectedRegion, ignoreCase = true) ||
        item.site.contains(filter.selectedRegion, ignoreCase = true)

      val matchesFamily = filter.selectedFamily == "الكل" ||
        item.family.contains(filter.selectedFamily, ignoreCase = true) ||
        item.language.contains(filter.selectedFamily, ignoreCase = true)

      matchesName && matchesPeriod && matchesRegion && matchesFamily
    }
  }

  fun updateNameQuery(query: String) {
    _filterState.update { it.copy(nameQuery = query) }
  }

  fun updateSelectedPeriod(period: String) {
    _filterState.update { it.copy(selectedPeriod = period) }
  }

  fun updateSelectedRegion(region: String) {
    _filterState.update { it.copy(selectedRegion = region) }
  }

  fun updateSelectedFamily(family: String) {
    _filterState.update { it.copy(selectedFamily = family) }
  }

  fun toggleFavoritesOnly() {
    _filterState.update { it.copy(favoritesOnly = !it.favoritesOnly) }
  }

  fun resetFilters() {
    _filterState.value = SearchFilterState()
  }

  fun toggleFavorite(inscription: InscriptionEntity) {
    viewModelScope.launch {
      repository.toggleFavorite(inscription.id, inscription.isFavorite)
    }
  }

  fun insertCustomInscription(inscription: InscriptionEntity) {
    viewModelScope.launch {
      repository.insertInscription(inscription)
    }
  }

  fun deleteInscription(id: String) {
    viewModelScope.launch {
      repository.deleteInscription(id)
    }
  }
}
