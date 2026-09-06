package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.CachedEncyclopediaEntryEntity
import com.example.data.models.AcademicChapter
import com.example.data.models.AcademicInscription
import com.example.data.models.EpigraphicGlossaryTerm
import com.example.data.repository.BooksSourcesRepository
import com.example.data.repository.EncyclopediaCacheRepository
import com.example.data.repository.SourceRecord
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class OfflineLibraryFilterState(
  val query: String = "",
  val selectedType: String = "ALL", // "ALL", "CHAPTER", "BOOK_SOURCE", "INSCRIPTION", "GLOSSARY"
  val selectedScriptType: String = "ALL", // "ALL", "Cuneiform", "Phoenician", "Musnad", "Aramaic", "Ge'ez", "Nabataean", "Ugaritic", etc.
  val selectedRegion: String = "ALL", // "ALL", "Mesopotamia", "Levant", "South Arabia", "North Arabia", "Horn of Africa", "Egypt"
  val isOfflineModeSimulated: Boolean = false
)

class EncyclopediaCacheViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: EncyclopediaCacheRepository

  private val _filterState = MutableStateFlow(OfflineLibraryFilterState())
  val filterState: StateFlow<OfflineLibraryFilterState> = _filterState.asStateFlow()

  val downloadedEntries: StateFlow<List<CachedEncyclopediaEntryEntity>>
  val filteredEntries: StateFlow<List<CachedEncyclopediaEntryEntity>>
  val downloadedCount: StateFlow<Int>
  val totalStorageBytes: StateFlow<Long>
  val downloadedIdSet: StateFlow<Set<String>>

  private val _isSyncingAll = MutableStateFlow(false)
  val isSyncingAll: StateFlow<Boolean> = _isSyncingAll.asStateFlow()

  private val _syncMessage = MutableStateFlow<String?>(null)
  val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

  init {
    val db = AppDatabase.getDatabase(application)
    repository = EncyclopediaCacheRepository(db.encyclopediaCacheDao())

    downloadedEntries = repository.downloadedEntries
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    downloadedCount = repository.downloadedCount
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    totalStorageBytes = repository.totalStorageBytes
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    downloadedIdSet = repository.downloadedOriginalIdSet
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    filteredEntries = combine(downloadedEntries, _filterState) { entries, filter ->
      entries.filter { item ->
        val matchesType = when (filter.selectedType) {
          "ALL" -> true
          else -> item.entryType.equals(filter.selectedType, ignoreCase = true)
        }
        val q = filter.query.trim().lowercase()
        val matchesQuery = if (q.isEmpty()) true else {
          item.titleAr.lowercase().contains(q) ||
          item.titleEn.lowercase().contains(q) ||
          item.summaryAr.lowercase().contains(q) ||
          item.categoryOrBranch.lowercase().contains(q) ||
          item.languageName.lowercase().contains(q) ||
          item.authorOrRuler.lowercase().contains(q) ||
          item.fullContentAr.lowercase().contains(q) ||
          item.tags.lowercase().contains(q) ||
          item.sourceOrCitation.lowercase().contains(q) ||
          item.transliterationSnippet.lowercase().contains(q)
        }

        // Script Type filter
        val matchesScript = when (filter.selectedScriptType) {
          "ALL" -> true
          "CUNEIFORM" -> {
            val text = "${item.titleAr} ${item.titleEn} ${item.summaryEn} ${item.fullContentAr} ${item.tags} ${item.languageName}".lowercase()
            text.contains("cuneiform") || text.contains("مسماري") || text.contains("أكدي") || text.contains("akkadian") || text.contains("سومر") || text.contains("sumerian") || text.contains("بابل") || text.contains("آشور")
          }
          "PHOENICIAN" -> {
            val text = "${item.titleAr} ${item.titleEn} ${item.summaryEn} ${item.fullContentAr} ${item.tags} ${item.languageName}".lowercase()
            text.contains("phoenician") || text.contains("فينيقي") || text.contains("كنعاني") || text.contains("canaanite") || text.contains("أحيرام") || text.contains("بيبلوس") || text.contains("بوني") || text.contains("punic")
          }
          "MUSNAD" -> {
            val text = "${item.titleAr} ${item.titleEn} ${item.summaryEn} ${item.fullContentAr} ${item.tags} ${item.languageName}".lowercase()
            text.contains("musnad") || text.contains("مسند") || text.contains("سبئي") || text.contains("حميري") || text.contains("حضرمي") || text.contains("قتباني") || text.contains("sabaic") || text.contains("sabaean") || text.contains("himyar") || text.contains("قحطاني") || text.contains("زبور")
          }
          "ARAMAIC" -> {
            val text = "${item.titleAr} ${item.titleEn} ${item.summaryEn} ${item.fullContentAr} ${item.tags} ${item.languageName}".lowercase()
            text.contains("aramaic") || text.contains("آرامي") || text.contains("سرياني") || text.contains("syriac") || text.contains("تدمر") || text.contains("palmyrene") || text.contains("تلمود")
          }
          "GEEZ" -> {
            val text = "${item.titleAr} ${item.titleEn} ${item.summaryEn} ${item.fullContentAr} ${item.tags} ${item.languageName}".lowercase()
            text.contains("ge'ez") || text.contains("geez") || text.contains("جعزي") || text.contains("إثيوبي") || text.contains("ethiopian") || text.contains("أكسوم") || text.contains("aksum") || text.contains("عيزانا")
          }
          "NABATAEAN" -> {
            val text = "${item.titleAr} ${item.titleEn} ${item.summaryEn} ${item.fullContentAr} ${item.tags} ${item.languageName}".lowercase()
            text.contains("nabataean") || text.contains("نبطي") || text.contains("نبط") || text.contains("البتراء") || text.contains("نمOptionارة") || text.contains("نمارة")
          }
          "UGARITIC" -> {
            val text = "${item.titleAr} ${item.titleEn} ${item.summaryEn} ${item.fullContentAr} ${item.tags} ${item.languageName}".lowercase()
            text.contains("ugaritic") || text.contains("أوغاريتي") || text.contains("أوغاريت") || text.contains("رأس شمرا")
          }
          "ARABIC" -> {
            val text = "${item.titleAr} ${item.titleEn} ${item.summaryEn} ${item.fullContentAr} ${item.tags} ${item.languageName}".lowercase()
            text.contains("عربي") || text.contains("arabic") || text.contains("ثمودي") || text.contains("صفائي") || text.contains("لحياني") || text.contains("safaitic") || text.contains("thamudic") || text.contains("lihyanite")
          }
          else -> true
        }

        // Geographical Region filter
        val matchesRegion = when (filter.selectedRegion) {
          "ALL" -> true
          "MESOPOTAMIA" -> {
            val text = "${item.titleAr} ${item.titleEn} ${item.summaryAr} ${item.summaryEn} ${item.fullContentAr} ${item.categoryOrBranch}".lowercase()
            text.contains("mesopotamia") || text.contains("بلاد الرافدين") || text.contains("العراق") || text.contains("iraq") || text.contains("بابل") || text.contains("babylon") || text.contains("نينوى") || text.contains("أور") || text.contains("سومر") || text.contains("أكد") || text.contains("نيبور") || text.contains("سوسة")
          }
          "LEVANT" -> {
            val text = "${item.titleAr} ${item.titleEn} ${item.summaryAr} ${item.summaryEn} ${item.fullContentAr} ${item.categoryOrBranch}".lowercase()
            text.contains("levant") || text.contains("بلاد الشام") || text.contains("سوريا") || text.contains("لبنان") || text.contains("فلسطين") || text.contains("الأردن") || text.contains("syria") || text.contains("lebanon") || text.contains("palestine") || text.contains("jordan") || text.contains("أوغاريت") || text.contains("بيبلوس") || text.contains("مؤاب") || text.contains("دمشق") || text.contains("ذيبان") || text.contains("السامية الشمالية الغربية")
          }
          "SOUTH_ARABIA" -> {
            val text = "${item.titleAr} ${item.titleEn} ${item.summaryAr} ${item.summaryEn} ${item.fullContentAr} ${item.categoryOrBranch}".lowercase()
            text.contains("south arabia") || text.contains("جنوب الجزيرة") || text.contains("اليمن") || text.contains("yemen") || text.contains("سبأ") || text.contains("حمير") || text.contains("حضرموت") || text.contains("صرواح") || text.contains("مأرب") || text.contains("ظفار") || text.contains("العربية الجنوبية") || text.contains("عُمان") || text.contains("oman")
          }
          "NORTH_ARABIA" -> {
            val text = "${item.titleAr} ${item.titleEn} ${item.summaryAr} ${item.summaryEn} ${item.fullContentAr} ${item.categoryOrBranch}".lowercase()
            text.contains("north arabia") || text.contains("شمال الجزيرة") || text.contains("الحجاز") || text.contains("نجد") || text.contains("العلا") || text.contains("مدائن صالح") || text.contains("تيماء") || text.contains("حائل") || text.contains("قرية الفاو") || text.contains("النمارة") || text.contains("ديدان") || text.contains("لحيان") || text.contains("صفا")
          }
          "HORN_OF_AFRICA" -> {
            val text = "${item.titleAr} ${item.titleEn} ${item.summaryAr} ${item.summaryEn} ${item.fullContentAr} ${item.categoryOrBranch}".lowercase()
            text.contains("horn of africa") || text.contains("القرن الإفريقي") || text.contains("إثيوبيا") || text.contains("إريتريا") || text.contains("ethiopia") || text.contains("eritrea") || text.contains("أكسوم") || text.contains("aksum") || text.contains("يهى") || text.contains("yeha") || text.contains("الحبشة") || text.contains("السامية الإثيوبية")
          }
          "EGYPT_SINAI" -> {
            val text = "${item.titleAr} ${item.titleEn} ${item.summaryAr} ${item.summaryEn} ${item.fullContentAr} ${item.categoryOrBranch}".lowercase()
            text.contains("egypt") || text.contains("مصر") || text.contains("سيناء") || text.contains("sinai") || text.contains("سرابيط الخادم") || text.contains("بروتو-سينائية") || text.contains("العمارنة") || text.contains("فيلا") || text.contains("إلفنتين")
          }
          else -> true
        }

        matchesType && matchesQuery && matchesScript && matchesRegion
      }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  }

  fun setSearchQuery(query: String) {
    _filterState.update { it.copy(query = query) }
  }

  fun setSelectedType(type: String) {
    _filterState.update { it.copy(selectedType = type) }
  }

  fun setSelectedScriptType(scriptType: String) {
    _filterState.update { it.copy(selectedScriptType = scriptType) }
  }

  fun setSelectedRegion(region: String) {
    _filterState.update { it.copy(selectedRegion = region) }
  }

  fun resetAllFilters() {
    _filterState.update {
      it.copy(
        query = "",
        selectedType = "ALL",
        selectedScriptType = "ALL",
        selectedRegion = "ALL"
      )
    }
  }

  fun toggleOfflineSimulation() {
    _filterState.update { it.copy(isOfflineModeSimulated = !it.isOfflineModeSimulated) }
  }

  fun isItemDownloaded(originalId: String): Boolean {
    return downloadedIdSet.value.contains(originalId)
  }

  fun cacheChapter(chapter: AcademicChapter) {
    viewModelScope.launch {
      repository.cacheChapter(chapter, isDownloaded = true)
      _syncMessage.value = "تم حفظ الفصل '${chapter.titleAr.take(20)}...' في المكتبة دون اتصال بنجاح ✓"
    }
  }

  fun cacheSourceRecord(source: SourceRecord) {
    viewModelScope.launch {
      repository.cacheSourceRecord(source, isDownloaded = true)
      _syncMessage.value = "تم تنزيل المرجع '${source.titleAr.take(20)}...' للمكتبة المحلية ✓"
    }
  }

  fun cacheInscription(inscription: AcademicInscription) {
    viewModelScope.launch {
      repository.cacheInscription(inscription, isDownloaded = true)
      _syncMessage.value = "تم حفظ النقش '${inscription.titleAr}' في قاعدة البيانات المحلية ✓"
    }
  }

  fun cacheGlossaryTerm(term: EpigraphicGlossaryTerm) {
    viewModelScope.launch {
      repository.cacheGlossaryTerm(term, isDownloaded = true)
    }
  }

  fun deleteFromDownloadedLibrary(originalId: String) {
    viewModelScope.launch {
      repository.deleteFromLibrary(originalId)
      _syncMessage.value = "تمت إزالة المبحث من المكتبة المحلية"
    }
  }

  fun downloadAllChapters() {
    viewModelScope.launch {
      _isSyncingAll.value = true
      try {
        repository.cacheAllChapters()
        _syncMessage.value = "تم تنزيل وتخزين كافة فصول الموسوعة في Room بنجاح ✓"
      } finally {
        _isSyncingAll.value = false
      }
    }
  }

  fun preloadEntireLibrary(contextSources: List<SourceRecord>? = null) {
    viewModelScope.launch {
      _isSyncingAll.value = true
      try {
        val sources = contextSources ?: BooksSourcesRepository.defaultSources
        repository.preloadEntireLibrary(sources)
        _syncMessage.value = "اكتمل تنزيل المكتبة الموسوعية بالكامل (فصول، مصادر، نقوش) في Room ✓"
      } finally {
        _isSyncingAll.value = false
      }
    }
  }

  fun clearEntireLibrary() {
    viewModelScope.launch {
      repository.clearEntireLibrary()
      _syncMessage.value = "تم تفريغ كافة البيانات المؤقتة من قاعدة بيانات Room"
    }
  }

  fun clearSyncMessage() {
    _syncMessage.value = null
  }
}
