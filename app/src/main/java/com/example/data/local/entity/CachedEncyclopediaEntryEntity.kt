package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.models.AcademicChapter
import com.example.data.models.AcademicInscription
import com.example.data.models.EpigraphicGlossaryTerm
import com.example.data.repository.SourceRecord
import org.json.JSONArray
import org.json.JSONObject

/**
 * Room entity representing cached encyclopedia entries (chapters, books, source records,
 * inscriptions, and glossary terms) stored locally for seamless offline reading.
 */
@Entity(tableName = "cached_encyclopedia_entries")
data class CachedEncyclopediaEntryEntity(
  @PrimaryKey
  val id: String, // format: "CHAP_chap-01", "BOOK_SRC-001", "INSC_PHOEN-001", "GLOSS_001"
  val originalId: String,
  val entryType: String, // "CHAPTER", "BOOK_SOURCE", "INSCRIPTION", "GLOSSARY"
  val titleAr: String,
  val titleEn: String,
  val categoryOrBranch: String, // e.g. "السامية الشمالية الغربية", "مدونة نقوش كلاسيكية"
  val summaryAr: String,
  val summaryEn: String,
  val fullContentAr: String,
  val fullContentEn: String = "",
  val transliterationSnippet: String = "",
  val authorOrRuler: String = "",
  val periodOrDate: String = "",
  val languageName: String = "",
  val sourceOrCitation: String = "",
  val sectionsJson: String = "[]", // Serialized ChapterSections or JSON metadata
  val downloadSizeBytes: Long = 0L,
  val cachedAt: Long = System.currentTimeMillis(),
  val isDownloaded: Boolean = true,
  val isFavorite: Boolean = false,
  val offlineNotes: String = "",
  val tags: String = ""
) {

  companion object {
    fun fromAcademicChapter(chapter: AcademicChapter, isDownloaded: Boolean = true): CachedEncyclopediaEntryEntity {
      val sectionsArray = JSONArray()
      val fullBodyAr = StringBuilder()
      val fullBodyEn = StringBuilder()

      for (section in chapter.sections) {
        val sObj = JSONObject()
        sObj.put("sectionId", section.sectionId)
        sObj.put("headingAr", section.headingAr)
        sObj.put("headingEn", section.headingEn)
        sObj.put("bodyTextAr", section.bodyTextAr)
        sObj.put("bodyTextEn", section.bodyTextEn)
        sObj.put("transliterationSnippet", section.transliterationSnippet)
        sectionsArray.put(sObj)

        fullBodyAr.append("## ").append(section.headingAr).append("\n\n")
        fullBodyAr.append(section.bodyTextAr).append("\n\n")
        if (section.transliterationSnippet.isNotBlank()) {
          fullBodyAr.append("نقحرة: ").append(section.transliterationSnippet).append("\n\n")
        }

        fullBodyEn.append("## ").append(section.headingEn).append("\n\n")
        fullBodyEn.append(section.bodyTextEn).append("\n\n")
      }

      val estimatedBytes = (fullBodyAr.length * 2 + fullBodyEn.length * 2 + sectionsArray.toString().length).toLong() + 1024L

      return CachedEncyclopediaEntryEntity(
        id = "CHAP_${chapter.id}",
        originalId = chapter.id,
        entryType = "CHAPTER",
        titleAr = chapter.titleAr,
        titleEn = chapter.titleEn,
        categoryOrBranch = chapter.languageBranch.ar,
        summaryAr = chapter.unitTitleAr,
        summaryEn = chapter.unitTitleEn,
        fullContentAr = fullBodyAr.toString().trim(),
        fullContentEn = fullBodyEn.toString().trim(),
        transliterationSnippet = chapter.sections.firstOrNull()?.transliterationSnippet ?: "",
        authorOrRuler = "قسم الآثار واللسانيات السامية - جامعة صنعاء",
        periodOrDate = "أبحاث فيلولوجية موثقة",
        languageName = chapter.languageNameAr,
        sourceOrCitation = chapter.primaryCitations.joinToString(" • "),
        sectionsJson = sectionsArray.toString(),
        downloadSizeBytes = estimatedBytes,
        cachedAt = System.currentTimeMillis(),
        isDownloaded = isDownloaded,
        tags = "${chapter.languageBranch.en},${chapter.languageNameEn},Chapter ${chapter.chapterNumber}"
      )
    }

    fun fromSourceRecord(source: SourceRecord, isDownloaded: Boolean = true): CachedEncyclopediaEntryEntity {
      val estimatedBytes = if (source.fileSizeBytes > 0L) source.fileSizeBytes else 1240L

      return CachedEncyclopediaEntryEntity(
        id = "BOOK_${source.id}",
        originalId = source.id,
        entryType = "BOOK_SOURCE",
        titleAr = source.titleAr,
        titleEn = source.title,
        categoryOrBranch = source.sourceType,
        summaryAr = source.summary,
        summaryEn = "Source Record from Epigraphic Archives: ${source.originalFileName}",
        fullContentAr = "${source.summary}\n\nبيانات المصدر:\n- المؤلف: ${source.author}\n- سنة النشر: ${source.publicationYear}\n- اللغة: ${source.language}\n- المسار: ${source.filePath}\n- الحالة: ${source.status}\n- ملاحظات الفحص: ${source.notes}",
        fullContentEn = "Original File: ${source.originalFileName}\nAuthor: ${source.author}\nYear: ${source.publicationYear}\nLanguage: ${source.language}\nNotes: ${source.notes}",
        transliterationSnippet = source.relatedInscriptions.joinToString(", "),
        authorOrRuler = source.author,
        periodOrDate = source.publicationYear,
        languageName = source.language,
        sourceOrCitation = "${source.titleAr} (${source.publicationYear})",
        sectionsJson = "[]",
        downloadSizeBytes = estimatedBytes,
        cachedAt = System.currentTimeMillis(),
        isDownloaded = isDownloaded,
        tags = source.relatedLanguages.joinToString(",")
      )
    }

    fun fromAcademicInscription(inscription: AcademicInscription, isDownloaded: Boolean = true): CachedEncyclopediaEntryEntity {
      val fullContent = """
        ${inscription.titleAr} (${inscription.title})
        الموقع: ${inscription.site} | الإقليم: ${inscription.region} | الحقبة: ${inscription.period}
        الحاكم / العهد: ${inscription.ruler} | مادة النقش: ${inscription.material}
        
        النص الأصلي / المنقحر:
        ${inscription.transliteration}
        
        الترجمة العربية:
        ${inscription.translationAr}
        
        الشروح الفيلولوجية:
        ${inscription.notesAr}
      """.trimIndent()

      return CachedEncyclopediaEntryEntity(
        id = "INSC_${inscription.id}",
        originalId = inscription.id,
        entryType = "INSCRIPTION",
        titleAr = inscription.titleAr,
        titleEn = inscription.title,
        categoryOrBranch = inscription.family,
        summaryAr = "${inscription.site} • ${inscription.period} • ${inscription.ruler}",
        summaryEn = "${inscription.language} (${inscription.script}) - ${inscription.site}",
        fullContentAr = fullContent,
        fullContentEn = inscription.translationEn,
        transliterationSnippet = inscription.transliteration,
        authorOrRuler = inscription.ruler,
        periodOrDate = inscription.period,
        languageName = inscription.language,
        sourceOrCitation = inscription.sourceUrl.ifBlank { inscription.corpus },
        sectionsJson = "[]",
        downloadSizeBytes = 2048L,
        cachedAt = System.currentTimeMillis(),
        isDownloaded = isDownloaded,
        tags = inscription.tags.joinToString(",")
      )
    }

    fun fromEpigraphicGlossaryTerm(term: EpigraphicGlossaryTerm, isDownloaded: Boolean = true): CachedEncyclopediaEntryEntity {
      val fullContent = """
        المصطلح: ${term.termAr} (${term.termEn})
        النقحرة: ${term.transliteration}
        التصنيف: ${term.categoryAr}
        التعريف المختصر: ${term.shortDefinitionAr}
        
        التعريف الفيلولوجي المفصل:
        ${term.detailedDefinitionAr}
        
        الشواهد الإبيغرافية والنقشية:
        ${term.inscriptionalWitnessesAr}
        
        الأهمية اللغوية المقارنة:
        ${term.philologicalImportanceAr}
      """.trimIndent()

      return CachedEncyclopediaEntryEntity(
        id = "GLOSS_${term.id}",
        originalId = term.id,
        entryType = "GLOSSARY",
        titleAr = term.termAr,
        titleEn = term.termEn,
        categoryOrBranch = term.categoryAr,
        summaryAr = term.shortDefinitionAr,
        summaryEn = term.shortDefinitionEn,
        fullContentAr = fullContent,
        fullContentEn = term.detailedDefinitionEn,
        transliterationSnippet = term.transliteration,
        authorOrRuler = term.ancientScriptType,
        periodOrDate = "اصطلاحات نقدية وإبيغرافية",
        languageName = term.categoryAr,
        sourceOrCitation = term.citation,
        sectionsJson = "[]",
        downloadSizeBytes = 1536L,
        cachedAt = System.currentTimeMillis(),
        isDownloaded = isDownloaded,
        tags = term.relatedTermIds.joinToString(",")
      )
    }
  }
}
