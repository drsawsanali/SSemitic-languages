package com.example.data.models

enum class LanguageDisplayMode {
  BILINGUAL,
  ARABIC,
  ENGLISH
}

enum class SemiticBranch(val en: String, val ar: String) {
  EAST_SEMITIC("East Semitic", "السامية الشرقية"),
  NORTHWEST_SEMITIC("Northwest Semitic", "السامية الشمالية الغربية"),
  ANCIENT_NORTH_ARABIAN("Ancient North Arabian & Arabic", "العربية والعربية الشمالية القديمة"),
  OLD_SOUTH_ARABIAN("Old South Arabian / Sayhadic", "العربية الجنوبية القديمة / الصيهدية"),
  MODERN_SOUTH_ARABIAN("Modern South Arabian", "العربية الجنوبية الحديثة"),
  ETHIOSEMITIC("Ethiopic Semitic / Ethiosemitic", "السامية الإثيوبية / الحبشية")
}

data class SemiticLanguage(
  val id: String,
  val en: String,
  val ar: String,
  val branch: SemiticBranch,
  val scriptEn: String,
  val scriptAr: String,
  val periodEn: String,
  val periodAr: String,
  val geographyEn: String,
  val geographyAr: String,
  val descriptionEn: String,
  val descriptionAr: String,
  val sampleGlyphs: String = "",
  val chapterCount: Int = 50
)

data class AcademicInscription(
  val id: String,
  val title: String,
  val titleAr: String,
  val language: String,
  val family: String,
  val script: String,
  val textType: String,
  val site: String,
  val region: String,
  val period: String,
  val material: String,
  val status: String,
  val ruler: String,
  val corpus: String,
  val transliteration: String,
  val originalScriptText: String = "",
  val translationAr: String,
  val translationEn: String,
  val notesAr: String,
  val notesEn: String,
  val imageUrl: String,
  val sourceUrl: String,
  val tags: List<String> = emptyList()
)

data class ChronologyEvent(
  val id: String,
  val branch: String,
  val year: String,
  val titleAr: String,
  val titleEn: String,
  val descriptionAr: String,
  val descriptionEn: String,
  val scriptDevelopmentAr: String = "",
  val scriptDevelopmentEn: String = ""
)

data class GlossaryTerm(
  val id: String,
  val language: String,
  val term: String,
  val transliteration: String = "",
  val root: String = "",
  val meaningAr: String,
  val meaningEn: String,
  val philologicalNoteAr: String,
  val philologicalNoteEn: String
)

data class EpigraphicGlossaryTerm(
  val id: String,
  val termAr: String,
  val termEn: String,
  val transliteration: String = "",
  val categoryAr: String,
  val categoryEn: String,
  val shortDefinitionAr: String,
  val shortDefinitionEn: String,
  val detailedDefinitionAr: String,
  val detailedDefinitionEn: String,
  val inscriptionalWitnessesAr: String,
  val inscriptionalWitnessesEn: String,
  val ancientSampleGlyphs: String = "",
  val ancientScriptType: String = "",
  val philologicalImportanceAr: String,
  val philologicalImportanceEn: String,
  val relatedTermIds: List<String> = emptyList(),
  val citation: String = ""
)

data class ComparativeLexiconRoot(
  val root: String,
  val protoMeaningAr: String,
  val protoMeaningEn: String,
  val akkadian: String,
  val ugaritic: String,
  val phoenician: String,
  val hebrew: String,
  val aramaic: String,
  val arabic: String,
  val sabaic: String,
  val geez: String,
  val phoneticEvolutionIPA: String,
  val soundShiftLawAr: String,
  val soundShiftLawEn: String
)

data class PhonemeItem(
  val ipa: String,
  val arabicLetter: String,
  val ancientGlyph: String,
  val categoryAr: String,
  val categoryEn: String,
  val articulationAr: String,
  val articulationEn: String,
  val f1Hz: Int,
  val f2Hz: Int,
  val exampleWord: String,
  val soundLawNoteAr: String
)

data class ArchaeologicalSite(
  val id: String,
  val nameAr: String,
  val nameEn: String,
  val country: String,
  val periodAr: String,
  val scriptFamilies: List<String>,
  val latitude: Double,
  val longitude: Double,
  val primaryInscriptions: List<String>,
  val descriptionAr: String,
  val descriptionEn: String
)

data class MediaArtifact(
  val id: String,
  val title: String,
  val titleAr: String,
  val category: String, // "inscriptions", "maps", "manuscripts", "charts", "coins"
  val scriptType: String,
  val material: String,
  val museum: String,
  val datePeriod: String,
  val imageUrl: String,
  val descriptionAr: String,
  val descriptionEn: String,
  val associatedRecordId: String? = null
)

data class GrammarCard(
  val id: String,
  val categoryAr: String,
  val categoryEn: String,
  val titleAr: String,
  val titleEn: String,
  val formula: String,
  val summaryAr: String,
  val summaryEn: String,
  val inscriptionalWitnessAr: String,
  val inscriptionalWitnessEn: String,
  val comparativeAnalysisAr: String,
  var leitnerBox: Int = 1 // 1: New, 2: Review, 3: Mastered
)

data class BibliographyItem(
  val id: String,
  val title: String,
  val authors: String,
  val year: String,
  val publisher: String,
  val summaryAr: String,
  val summaryEn: String,
  val url: String,
  val citationAPA: String,
  val citationMLA: String,
  val citationChicago: String,
  val citationBibTeX: String
)

data class QuizQuestion(
  val id: String,
  val languageOrBranch: String,
  val questionAr: String,
  val questionEn: String,
  val optionsAr: List<String>,
  val optionsEn: List<String>,
  val correctIndex: Int,
  val explanationAr: String,
  val explanationEn: String
)

data class AcademicChapter(
  val id: String,
  val chapterNumber: Int,
  val unitNumber: Int,
  val unitTitleAr: String,
  val unitTitleEn: String,
  val titleAr: String,
  val titleEn: String,
  val languageBranch: SemiticBranch,
  val languageNameAr: String,
  val languageNameEn: String,
  val sections: List<ChapterSection>,
  val primaryCitations: List<String>
)

data class ChapterSection(
  val sectionId: String,
  val headingAr: String,
  val headingEn: String,
  val bodyTextAr: String,
  val bodyTextEn: String,
  val transliterationSnippet: String = "",
  val comparisonTableData: List<List<String>> = emptyList(),
  val footnotesAr: List<String> = emptyList()
)
