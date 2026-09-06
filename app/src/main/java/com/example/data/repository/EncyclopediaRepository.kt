package com.example.data.repository

import com.example.data.models.*

object EncyclopediaRepository {
  fun getAllInscriptions(): List<AcademicInscription> = InscriptionsData.list

  fun getAllLanguages(): List<SemiticLanguage> = LanguagesData.list

  fun getAllChapters(): List<AcademicChapter> = ChaptersData.list

  fun getRoots(): List<ComparativeLexiconRoot> = LexiconAndPhoneticsData.roots

  fun getPhonemes(): List<PhonemeItem> = LexiconAndPhoneticsData.phonemes

  fun getChronology(): List<ChronologyEvent> = LexiconAndPhoneticsData.chronology

  fun getSites(): List<ArchaeologicalSite> = LexiconAndPhoneticsData.sites

  fun getMedia(): List<MediaArtifact> = LexiconAndPhoneticsData.mediaArtifacts

  fun getFlashcards(): List<GrammarCard> = LexiconAndPhoneticsData.flashcards

  fun getBibliography(): List<BibliographyItem> = LexiconAndPhoneticsData.bibliography

  fun getQuizQuestions(): List<QuizQuestion> = LexiconAndPhoneticsData.quizQuestions

  fun getEpigraphicGlossary(): List<EpigraphicGlossaryTerm> = EpigraphicGlossaryData.terms

  fun generateInscriptionsCsv(): String {
    val sb = StringBuilder()
    sb.append("ID,Title_AR,Title_EN,Language,Family,Script,Site,Period,Material,Ruler,Transliteration\n")
    for (item in InscriptionsData.list) {
      val row = listOf(
        item.id,
        "\"${item.titleAr.replace("\"", "\"\"")}\"",
        "\"${item.title.replace("\"", "\"\"")}\"",
        "\"${item.language}\"",
        "\"${item.family}\"",
        "\"${item.script}\"",
        "\"${item.site}\"",
        "\"${item.period}\"",
        "\"${item.material}\"",
        "\"${item.ruler}\"",
        "\"${item.transliteration.replace("\"", "\"\"")}\""
      ).joinToString(",")
      sb.append(row).append("\n")
    }
    return sb.toString()
  }
}
