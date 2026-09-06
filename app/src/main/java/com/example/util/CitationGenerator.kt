package com.example.util

import com.example.data.models.AcademicChapter
import com.example.data.models.AcademicInscription

object CitationGenerator {
  fun generateChapterCitation(chapter: AcademicChapter, style: String): String {
    val author = "Al-Hadhouri, Sawsan Ali & Fag'as, Prof. Ahmed"
    val year = "2024"
    val bookTitle = "Comprehensive Encyclopedia of Semitic Languages and Epigraphy"
    val publisher = "Sanaa University Faculty of Arts Press"

    return when (style.uppercase()) {
      "APA" -> "$author ($year). ${chapter.titleEn} (Chapter ${chapter.chapterNumber}). In $bookTitle (pp. ${chapter.chapterNumber * 15 - 14}-${chapter.chapterNumber * 15}). $publisher."
      "MLA" -> "$author. \"${chapter.titleEn}.\" $bookTitle, $publisher, $year, pp. ${chapter.chapterNumber * 15 - 14}-${chapter.chapterNumber * 15}."
      "CHICAGO" -> "$author. \"${chapter.titleEn}.\" In $bookTitle, ${chapter.chapterNumber * 15 - 14}-${chapter.chapterNumber * 15}. Sanaa: $publisher, $year."
      "BIBTEX" -> """
        @incollection{semitic_chap_${chapter.chapterNumber},
          author    = {${author.replace("&", "and")}},
          title     = {{${chapter.titleEn}}},
          booktitle = {{$bookTitle}},
          publisher = {{$publisher}},
          year      = {$year},
          chapter   = {${chapter.chapterNumber}},
          pages     = {${chapter.chapterNumber * 15 - 14}--${chapter.chapterNumber * 15}}
        }
      """.trimIndent()
      "RIS" -> """
        TY  - CHAP
        AU  - Al-Hadhouri, Sawsan Ali
        AU  - Fag'as, Ahmed
        TI  - ${chapter.titleEn}
        T2  - $bookTitle
        PY  - $year
        PB  - $publisher
        SP  - ${chapter.chapterNumber * 15 - 14}
        EP  - ${chapter.chapterNumber * 15}
        ER  - 
      """.trimIndent()
      else -> "$author ($year). ${chapter.titleEn}. $bookTitle."
    }
  }

  fun generateInscriptionCitation(inscription: AcademicInscription, style: String): String {
    return when (style.uppercase()) {
      "APA" -> "${inscription.title} [${inscription.id}]. In ${inscription.corpus}, found at ${inscription.site}. Semitic Epigraphic Database."
      "MLA" -> "\"${inscription.title}.\" ${inscription.corpus}, ${inscription.site}, ${inscription.period}."
      "CHICAGO" -> "\"${inscription.title}.\" ${inscription.corpus}. Discovered at ${inscription.site} (${inscription.period})."
      "BIBTEX" -> """
        @misc{inscr_${inscription.id.replace("-", "_")},
          title        = {{${inscription.title}}},
          howpublished = {${inscription.corpus}},
          note         = {Site: ${inscription.site}, Period: ${inscription.period}},
          year         = {2024}
        }
      """.trimIndent()
      else -> "${inscription.title} (${inscription.corpus})"
    }
  }
}
