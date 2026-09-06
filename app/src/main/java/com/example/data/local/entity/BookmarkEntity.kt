package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
  @PrimaryKey
  val id: String, // format: "INSCRIPTION_${itemId}" or "GLOSSARY_${itemId}"
  val itemId: String,
  val itemType: String, // "INSCRIPTION" or "GLOSSARY"
  val titleAr: String,
  val titleEn: String,
  val categoryOrBranch: String, // e.g. "فينيقية (كنعانية)" or "الإبيغرافيا والخطوط"
  val snippetAr: String, // short definition or translation snippet
  val transliteration: String = "",
  val originalScriptText: String = "",
  val siteOrWitness: String = "",
  val citation: String = "",
  val customNotes: String = "",
  val collectionName: String = "العامة",
  val timestamp: Long = System.currentTimeMillis()
)
