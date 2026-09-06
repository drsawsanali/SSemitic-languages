package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.models.AcademicInscription

@Entity(tableName = "inscriptions")
data class InscriptionEntity(
  @PrimaryKey
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
  val tags: String = "", // Comma-separated tags
  val isFavorite: Boolean = false,
  val customNotes: String = ""
) {
  fun toAcademicInscription(): AcademicInscription {
    return AcademicInscription(
      id = id,
      title = title,
      titleAr = titleAr,
      language = language,
      family = family,
      script = script,
      textType = textType,
      site = site,
      region = region,
      period = period,
      material = material,
      status = status,
      ruler = ruler,
      corpus = corpus,
      transliteration = transliteration,
      originalScriptText = originalScriptText,
      translationAr = translationAr,
      translationEn = translationEn,
      notesAr = notesAr,
      notesEn = notesEn,
      imageUrl = imageUrl,
      sourceUrl = sourceUrl,
      tags = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() }
    )
  }

  companion object {
    fun fromAcademicInscription(item: AcademicInscription, isFavorite: Boolean = false, customNotes: String = ""): InscriptionEntity {
      return InscriptionEntity(
        id = item.id,
        title = item.title,
        titleAr = item.titleAr,
        language = item.language,
        family = item.family,
        script = item.script,
        textType = item.textType,
        site = item.site,
        region = item.region,
        period = item.period,
        material = item.material,
        status = item.status,
        ruler = item.ruler,
        corpus = item.corpus,
        transliteration = item.transliteration,
        originalScriptText = item.originalScriptText,
        translationAr = item.translationAr,
        translationEn = item.translationEn,
        notesAr = item.notesAr,
        notesEn = item.notesEn,
        imageUrl = item.imageUrl,
        sourceUrl = item.sourceUrl,
        tags = item.tags.joinToString(","),
        isFavorite = isFavorite,
        customNotes = customNotes
      )
    }
  }
}
