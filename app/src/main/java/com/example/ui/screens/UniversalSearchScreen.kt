package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.data.repository.EncyclopediaRepository
import com.example.ui.theme.LocalCustomColors
import com.example.util.AudioEngine
import com.example.util.CitationGenerator

enum class SearchContentType(
  val id: String,
  val labelAr: String,
  val labelEn: String,
  val icon: ImageVector,
  val badgeColor: Color
) {
  ALL("all", "كافة الأقسام", "All Categories", Icons.Default.AllInclusive, Color(0xFF6750A4)),
  INSCRIPTIONS("inscriptions", "📜 النقوش والمسلات", "Inscriptions & Artifacts", Icons.Default.Description, Color(0xFFD48B38)),
  CHAPTERS("chapters", "📚 الفصول والمباحث", "Academic Chapters", Icons.Default.MenuBook, Color(0xFF1976D2)),
  LEXICON("lexicon", "🌿 المعجم والجذور", "Comparative Lexicon", Icons.Default.Spellcheck, Color(0xFF2E7D32)),
  RULES("rules", "📐 القواعد والصوتيات", "Linguistic & Phonetic Rules", Icons.Default.GraphicEq, Color(0xFFC2185B)),
  SITES("sites", "🗺️ المواقع والجغرافيا", "Archaeological Sites", Icons.Default.Map, Color(0xFFE65100)),
  CHRONOLOGY("chronology", "⏳ التسلسل والعصور", "Chronology & Eras", Icons.Default.History, Color(0xFF00897B)),
  BIBLIOGRAPHY("bibliography", "📖 المراجع والتوثيق", "Bibliography & Sources", Icons.Default.Bookmarks, Color(0xFF5E35B1)),
  GLOSSARY("glossary", "📚 المسرد الإبيغرافي", "Epigraphic Glossary", Icons.Default.MenuBook, Color(0xFF00796B))
}

data class UniversalSearchResult(
  val id: String,
  val type: SearchContentType,
  val titleAr: String,
  val titleEn: String,
  val snippetAr: String,
  val originalScriptOrFormula: String = "",
  val branchOrLanguage: String = "",
  val metadataBadge: String = "",
  val targetRoute: String,
  val relevanceScore: Int = 95,
  val citationText: String = "",
  val audioText: String = "",
  val fullData: Any? = null
)

object ArabicTextNormalizer {
  private val diacriticsRegex = Regex("[\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]")
  private val tatweelRegex = Regex("\\u0640")

  fun normalize(input: String): String {
    if (input.isBlank()) return ""
    var result = input.trim().lowercase()
    result = diacriticsRegex.replace(result, "")
    result = tatweelRegex.replace(result, "")
    result = result
      .replace('أ', 'ا')
      .replace('إ', 'ا')
      .replace('آ', 'ا')
      .replace('ٱ', 'ا')
      .replace('ة', 'ه')
      .replace('ى', 'ي')
      .replace('ئ', 'ي')
      .replace('ؤ', 'و')
    return result
  }

  // Semantic concept expander
  fun getSemanticExpansions(query: String): List<String> {
    val norm = normalize(query)
    val expanded = mutableListOf<String>()
    
    if (norm.contains("ميشع") || norm.contains("مواب") || norm.contains("ذيبان")) {
      expanded.addAll(listOf("مؤاب", "ذيبان", "كموش", "عمري", "واو العطف", "moabite", "mesha"))
    }
    if (norm.contains("حمورابي") || norm.contains("بابل") || norm.contains("مسماري") || norm.contains("شريعه")) {
      expanded.addAll(listOf("بابل", "أكادية", "مسمارية", "شريعة", "hammurabi", "cuneiform", "akkadian"))
    }
    if (norm.contains("احيرام") || norm.contains("جبيل") || norm.contains("فينقي") || norm.contains("كنعان")) {
      expanded.addAll(listOf("جبيل", "فينيقية", "تابوت", "أبجدية", "ahiram", "byblos", "phoenician"))
    }
    if (norm.contains("صرواح") || norm.contains("سبا") || norm.contains("مارب") || norm.contains("مسند")) {
      expanded.addAll(listOf("سبأ", "مأرب", "مسند", "صرواح", "شرحبيل", "sabaean", "musnad"))
    }
    if (norm.contains("عيزانا") || norm.contains("اكسوم") || norm.contains("جعزي") || norm.contains("حبش")) {
      expanded.addAll(listOf("أكسوم", "الجعزية", "عيزانا", "فيدل", "ezana", "geez", "axum"))
    }
    if (norm.contains("تحول") || norm.contains("صوت") || norm.contains("كنعاني") || norm.contains("حرك")) {
      expanded.addAll(listOf("التحول الكنعاني", "ā > ō", "الصوتيات", "بجد كفت", "begadkefat", "sound shift"))
    }
    if (norm.contains("فعل") || norm.contains("وزن") || norm.contains("تصريف") || norm.contains("جذع")) {
      expanded.addAll(listOf("binyanim", "الأوزان", "المجرد", "السببي", "المطاوع", "pael", "yiphil", "hiphil"))
    }
    if (norm.contains("اوغاريت") || norm.contains("شمرا") || norm.contains("بعل")) {
      expanded.addAll(listOf("أوغاريت", "رأس الشمرا", "بعل", "مسمارية أبجدية", "ugaritic", "baal"))
    }
    if (norm.contains("تدمر") || norm.contains("بتراء") || norm.contains("نبط") || norm.contains("ارام")) {
      expanded.addAll(listOf("آرامية", "نبطية", "تدمرية", "البتراء", "تدمر", "aramaic", "nabataean", "palmyra"))
    }

    return expanded
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalSearchScreen(
  onNavigate: (String) -> Unit,
  modifier: Modifier = Modifier,
  initialQuery: String = ""
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  val audioEngine = remember { AudioEngine(context) }

  DisposableEffect(Unit) {
    onDispose { audioEngine.release() }
  }

  var searchQuery by remember { mutableStateOf(initialQuery) }
  var selectedType by remember { mutableStateOf(SearchContentType.ALL) }
  var selectedBranchFilter by remember { mutableStateOf("الكل") }
  var isFullTextSearch by remember { mutableStateOf(true) }
  var isSemanticExpansionEnabled by remember { mutableStateOf(true) }
  var selectedResultForDetail by remember { mutableStateOf<UniversalSearchResult?>(null) }

  val branches = listOf("الكل", "السامية الشرقية", "السامية الشمالية الغربية", "العربية والشمالية القديمة", "العربية الجنوبية القديمة", "السامية الإثيوبية", "العربية الجنوبية الحديثة")

  val searchPresets = listOf(
    "مسلة ميشع",
    "التحول الكنعاني",
    "شريعة حمورابي",
    "تابوت أحيرام",
    "نقش صرواح",
    "مسلة عيزانا",
    "سد مأرب",
    "ملحمة جلجامش",
    "قانون بجد كفت",
    "أوزان الأفعال",
    "أوغاريت",
    "رسائل العمارنة"
  )

  // Search Engine Computation
  val searchResults by remember(
    searchQuery,
    selectedType,
    selectedBranchFilter,
    isFullTextSearch,
    isSemanticExpansionEnabled
  ) {
    derivedStateOf {
      val rawQuery = searchQuery.trim()
      if (rawQuery.isBlank()) {
        emptyList()
      } else {
        val normQuery = ArabicTextNormalizer.normalize(rawQuery)
        val expansions = if (isSemanticExpansionEnabled) {
          ArabicTextNormalizer.getSemanticExpansions(rawQuery).map { ArabicTextNormalizer.normalize(it) }
        } else emptyList()

        val allSearchTokens = (listOf(normQuery) + expansions).filter { it.length >= 2 }

        fun matchesText(text: String): Boolean {
          if (text.isBlank()) return false
          val normTarget = ArabicTextNormalizer.normalize(text)
          return allSearchTokens.any { token -> normTarget.contains(token) }
        }

        val results = mutableListOf<UniversalSearchResult>()

        // 1. Inscriptions Search
        if (selectedType == SearchContentType.ALL || selectedType == SearchContentType.INSCRIPTIONS) {
          EncyclopediaRepository.getAllInscriptions().forEach { insc ->
            val branchMatches = selectedBranchFilter == "الكل" || insc.family.contains(selectedBranchFilter)
            if (branchMatches) {
              val titleMatch = matchesText(insc.titleAr) || matchesText(insc.title)
              val contentMatch = isFullTextSearch && (
                matchesText(insc.transliteration) ||
                matchesText(insc.translationAr) ||
                matchesText(insc.notesAr) ||
                matchesText(insc.site) ||
                matchesText(insc.ruler) ||
                matchesText(insc.script)
              )

              if (titleMatch || contentMatch) {
                val score = if (titleMatch) 98 else 88
                results.add(
                  UniversalSearchResult(
                    id = "insc-${insc.id}",
                    type = SearchContentType.INSCRIPTIONS,
                    titleAr = insc.titleAr,
                    titleEn = insc.title,
                    snippetAr = insc.translationAr.ifBlank { insc.notesAr }.take(160) + "...",
                    originalScriptOrFormula = insc.originalScriptText.ifBlank { insc.transliteration },
                    branchOrLanguage = "${insc.family} • ${insc.language}",
                    metadataBadge = "${insc.site} (${insc.period})",
                    targetRoute = "inscriptions",
                    relevanceScore = score,
                    citationText = CitationGenerator.generateInscriptionCitation(insc, "APA"),
                    audioText = insc.transliteration.ifBlank { insc.titleAr },
                    fullData = insc
                  )
                )
              }
            }
          }
        }

        // 2. Chapters Search
        if (selectedType == SearchContentType.ALL || selectedType == SearchContentType.CHAPTERS) {
          EncyclopediaRepository.getAllChapters().forEach { chap ->
            val branchMatches = selectedBranchFilter == "الكل" || chap.languageBranch.ar.contains(selectedBranchFilter)
            if (branchMatches) {
              val titleMatch = matchesText(chap.titleAr) || matchesText(chap.titleEn) || matchesText(chap.unitTitleAr) || matchesText(chap.languageNameAr)
              val bodyMatch = isFullTextSearch && chap.sections.any { sec ->
                matchesText(sec.headingAr) || matchesText(sec.bodyTextAr) || matchesText(sec.transliterationSnippet)
              }

              if (titleMatch || bodyMatch) {
                val matchingSec = chap.sections.firstOrNull { matchesText(it.bodyTextAr) } ?: chap.sections.firstOrNull()
                val score = if (titleMatch) 96 else 85
                results.add(
                  UniversalSearchResult(
                    id = "chap-${chap.id}",
                    type = SearchContentType.CHAPTERS,
                    titleAr = "الفصل ${chap.chapterNumber}: ${chap.titleAr}",
                    titleEn = chap.titleEn,
                    snippetAr = matchingSec?.bodyTextAr?.take(160)?.plus("...") ?: chap.unitTitleAr,
                    originalScriptOrFormula = matchingSec?.transliterationSnippet ?: "",
                    branchOrLanguage = "${chap.languageBranch.ar} • ${chap.languageNameAr}",
                    metadataBadge = "الوحدة ${chap.unitNumber}",
                    targetRoute = "chapters",
                    relevanceScore = score,
                    citationText = "إشراف: أ.د. أحمد فقعس • إعداد: سوسن علي عبدالله الحضوري. (2026). الموسوعة الشاملة في اللغات السامية: ${chap.titleAr}. جامعة صنعاء.",
                    audioText = chap.titleAr,
                    fullData = chap
                  )
                )
              }
            }
          }
        }

        // 3. Lexicon & Roots Search
        if (selectedType == SearchContentType.ALL || selectedType == SearchContentType.LEXICON) {
          EncyclopediaRepository.getRoots().forEach { rootItem ->
            val rootMatch = matchesText(rootItem.root) || matchesText(rootItem.protoMeaningAr) || matchesText(rootItem.protoMeaningEn)
            val langMatch = isFullTextSearch && (
              matchesText(rootItem.akkadian) ||
              matchesText(rootItem.ugaritic) ||
              matchesText(rootItem.phoenician) ||
              matchesText(rootItem.hebrew) ||
              matchesText(rootItem.aramaic) ||
              matchesText(rootItem.arabic) ||
              matchesText(rootItem.sabaic) ||
              matchesText(rootItem.geez) ||
              matchesText(rootItem.soundShiftLawAr)
            )

            if (rootMatch || langMatch) {
              results.add(
                UniversalSearchResult(
                  id = "root-${rootItem.root}",
                  type = SearchContentType.LEXICON,
                  titleAr = "الجذر السامي: *${rootItem.root} (${rootItem.protoMeaningAr})",
                  titleEn = "Root *${rootItem.root} (${rootItem.protoMeaningEn})",
                  snippetAr = "عربية: ${rootItem.arabic} | أكادية: ${rootItem.akkadian} | فينيقية: ${rootItem.phoenician} | سبئية: ${rootItem.sabaic} | جعزية: ${rootItem.geez}. القانون: ${rootItem.soundShiftLawAr}",
                  originalScriptOrFormula = rootItem.phoneticEvolutionIPA,
                  branchOrLanguage = "السامية المشتركة (Proto-Semitic)",
                  metadataBadge = "معجم تأصيلي",
                  targetRoute = "lexicon",
                  relevanceScore = if (rootMatch) 99 else 89,
                  citationText = "Lexicon Entry: Proto-Semitic Root *${rootItem.root}, Semitic Comparative Dictionary, Sana'a University, 2026.",
                  audioText = rootItem.phoneticEvolutionIPA,
                  fullData = rootItem
                )
              )
            }
          }
        }

        // 4. Grammar & Phonetic Rules Search
        if (selectedType == SearchContentType.ALL || selectedType == SearchContentType.RULES) {
          EncyclopediaRepository.getFlashcards().forEach { card ->
            val cardMatch = matchesText(card.titleAr) || matchesText(card.formula) || matchesText(card.summaryAr) || matchesText(card.categoryAr) || matchesText(card.comparativeAnalysisAr)
            if (cardMatch) {
              results.add(
                UniversalSearchResult(
                  id = "card-${card.id}",
                  type = SearchContentType.RULES,
                  titleAr = card.titleAr,
                  titleEn = card.titleEn,
                  snippetAr = "${card.summaryAr} | الشاهد المنقوش: ${card.inscriptionalWitnessAr}",
                  originalScriptOrFormula = card.formula,
                  branchOrLanguage = card.categoryAr,
                  metadataBadge = "قاعدة وصوتيات",
                  targetRoute = "flashcards",
                  relevanceScore = 92,
                  citationText = "قاعدة إبيغرافية: ${card.titleAr} (${card.formula}). موسوعة النحو السامي المقارن.",
                  audioText = card.titleAr,
                  fullData = card
                )
              )
            }
          }

          EncyclopediaRepository.getPhonemes().forEach { phoneme ->
            val phonemeMatch = matchesText(phoneme.arabicLetter) || matchesText(phoneme.ipa) || matchesText(phoneme.categoryAr) || matchesText(phoneme.soundLawNoteAr) || matchesText(phoneme.exampleWord)
            if (phonemeMatch) {
              results.add(
                UniversalSearchResult(
                  id = "phoneme-${phoneme.ipa}",
                  type = SearchContentType.RULES,
                  titleAr = "الفونيم الصوتي: [${phoneme.ipa}] - حرف (${phoneme.arabicLetter})",
                  titleEn = "Phoneme [${phoneme.ipa}] - ${phoneme.categoryEn}",
                  snippetAr = "المخرج: ${phoneme.articulationAr} | قانون التحول: ${phoneme.soundLawNoteAr} | مثال: ${phoneme.exampleWord}",
                  originalScriptOrFormula = phoneme.ancientGlyph,
                  branchOrLanguage = "الأصوات السامية (IPA)",
                  metadataBadge = "${phoneme.f1Hz}/${phoneme.f2Hz} Hz",
                  targetRoute = "phonetics",
                  relevanceScore = 94,
                  citationText = "Phonetic Analysis: Semitic Phoneme [${phoneme.ipa}]. Acoustic Matrix.",
                  audioText = phoneme.exampleWord,
                  fullData = phoneme
                )
              )
            }
          }
        }

        // 5. Archaeological Sites Search
        if (selectedType == SearchContentType.ALL || selectedType == SearchContentType.SITES) {
          EncyclopediaRepository.getSites().forEach { site ->
            val siteMatch = matchesText(site.nameAr) || matchesText(site.nameEn) || matchesText(site.country) || matchesText(site.periodAr) || matchesText(site.descriptionAr)
            if (siteMatch) {
              results.add(
                UniversalSearchResult(
                  id = "site-${site.id}",
                  type = SearchContentType.SITES,
                  titleAr = "الموقع الأثري: ${site.nameAr} (${site.country})",
                  titleEn = site.nameEn,
                  snippetAr = "${site.descriptionAr} | أهم المكتشفات: ${site.primaryInscriptions.joinToString("، ")}",
                  originalScriptOrFormula = "📍 [${site.latitude}, ${site.longitude}]",
                  branchOrLanguage = site.scriptFamilies.joinToString(" • "),
                  metadataBadge = site.periodAr,
                  targetRoute = "map",
                  relevanceScore = 91,
                  citationText = "الموقع التاريخي: ${site.nameAr}، ${site.country}. الأطلس الأثري للغات السامية.",
                  audioText = site.nameAr,
                  fullData = site
                )
              )
            }
          }
        }

        // 6. Chronology Events Search
        if (selectedType == SearchContentType.ALL || selectedType == SearchContentType.CHRONOLOGY) {
          EncyclopediaRepository.getChronology().forEach { event ->
            val eventMatch = matchesText(event.titleAr) || matchesText(event.year) || matchesText(event.descriptionAr) || matchesText(event.branch)
            if (eventMatch) {
              results.add(
                UniversalSearchResult(
                  id = "event-${event.id}",
                  type = SearchContentType.CHRONOLOGY,
                  titleAr = "${event.year}: ${event.titleAr}",
                  titleEn = event.titleEn,
                  snippetAr = "${event.descriptionAr} | تطور الخط: ${event.scriptDevelopmentAr}",
                  originalScriptOrFormula = event.year,
                  branchOrLanguage = event.branch,
                  metadataBadge = "تسلسل زمني",
                  targetRoute = "chapters",
                  relevanceScore = 87,
                  citationText = "التسلسل التاريخي: ${event.titleAr} (${event.year}). موسوعة اللغات السامية.",
                  audioText = event.titleAr,
                  fullData = event
                )
              )
            }
          }
        }

        // 7. Bibliography Search
        if (selectedType == SearchContentType.ALL || selectedType == SearchContentType.BIBLIOGRAPHY) {
          EncyclopediaRepository.getBibliography().forEach { bib ->
            val bibMatch = matchesText(bib.title) || matchesText(bib.authors) || matchesText(bib.summaryAr) || matchesText(bib.publisher)
            if (bibMatch) {
              results.add(
                UniversalSearchResult(
                  id = "bib-${bib.id}",
                  type = SearchContentType.BIBLIOGRAPHY,
                  titleAr = bib.title,
                  titleEn = bib.authors,
                  snippetAr = "${bib.summaryAr} (${bib.publisher}, ${bib.year})",
                  originalScriptOrFormula = "APA: ${bib.citationAPA}",
                  branchOrLanguage = "الناشر: ${bib.publisher}",
                  metadataBadge = bib.year,
                  targetRoute = "bibliography",
                  relevanceScore = 90,
                  citationText = bib.citationAPA,
                  audioText = bib.title,
                  fullData = bib
                )
              )
            }
          }

          // Index Epigraphic Glossary
          if (selectedType == SearchContentType.ALL || selectedType == SearchContentType.GLOSSARY) {
            EncyclopediaRepository.getEpigraphicGlossary().forEach { term ->
              val termMatch = matchesText(term.termAr) ||
                matchesText(term.termEn) ||
                matchesText(term.transliteration) ||
                matchesText(term.shortDefinitionAr) ||
                matchesText(term.shortDefinitionEn) ||
                matchesText(term.detailedDefinitionAr) ||
                matchesText(term.inscriptionalWitnessesAr) ||
                matchesText(term.categoryAr) ||
                matchesText(term.ancientScriptType)
              if (termMatch) {
                val score = when {
                  matchesText(term.termAr) || matchesText(term.termEn) -> 98
                  matchesText(term.shortDefinitionAr) -> 92
                  else -> 86
                }
                results.add(
                  UniversalSearchResult(
                    id = "glossary-${term.id}",
                    type = SearchContentType.GLOSSARY,
                    titleAr = term.termAr,
                    titleEn = "${term.termEn} (${term.transliteration})",
                    snippetAr = term.shortDefinitionAr,
                    originalScriptOrFormula = term.ancientSampleGlyphs,
                    branchOrLanguage = term.categoryAr,
                    metadataBadge = term.ancientScriptType,
                    targetRoute = "glossary",
                    relevanceScore = score,
                    citationText = term.citation,
                    audioText = "${term.termAr}. ${term.shortDefinitionAr}",
                    fullData = term
                  )
                )
              }
            }
          }
        }

        // Sort results by relevance score descending
        results.sortedByDescending { it.relevanceScore }
      }
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Header Card
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                  modifier = Modifier.size(38.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      Icons.Default.Search,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(22.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "محرك البحث الأكاديمي والدلالي الشامل",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "فهرسة 35+ لغة، 300+ فصلاً، النقوش، المعجم، والأطلس",
                    fontSize = 11.sp,
                    color = customColors.mutedText
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF2E7D32).copy(alpha = 0.15f)
              ) {
                Text(
                  text = "دلالي ذكي",
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF2E7D32)
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Text Field
            OutlinedTextField(
              value = searchQuery,
              onValueChange = { searchQuery = it },
              modifier = Modifier.fillMaxWidth(),
              placeholder = { Text("ابحث عن مسلة، جذر، قانون صوتي، فصل، أو موقع...", fontSize = 13.sp) },
              leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              },
              trailingIcon = {
                if (searchQuery.isNotBlank()) {
                  IconButton(onClick = { searchQuery = "" }) {
                    Icon(Icons.Default.Close, contentDescription = "مسح")
                  }
                }
              },
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = customColors.border
              )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Preset Quick Queries
            Text("نماذج بحث ومفاهيم سريعة:", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = customColors.mutedText)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              searchPresets.forEach { preset ->
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                  border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                  modifier = Modifier.clickable { searchQuery = preset }
                ) {
                  Text(
                    text = preset,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
          }
        }
      }

      // Advanced Filters & Settings Row
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text("تصفية حسب نوع المحتوى:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(6.dp))

            // Content Type Filter Chips
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              SearchContentType.values().forEach { type ->
                val isSelected = selectedType == type
                FilterChip(
                  selected = isSelected,
                  onClick = { selectedType = type },
                  label = { Text(type.labelAr, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                  leadingIcon = if (isSelected) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                  } else null
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Semantic & Scope Toggles
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                  checked = isSemanticExpansionEnabled,
                  onCheckedChange = { isSemanticExpansionEnabled = it }
                )
                Text("التوسيع الدلالي الذكي (Semantic Expansion)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                  checked = isFullTextSearch,
                  onCheckedChange = { isFullTextSearch = it }
                )
                Text("بحث بالمتن الكامل", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
              }
            }
          }
        }
      }

      // Results Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ManageSearch, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (searchQuery.isBlank()) "يرجى كتابة كلمة مفتاحية للبحث" else "نتائج البحث (${searchResults.size} نتيجة)",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          if (searchResults.isNotEmpty()) {
            Text(
              text = "مرتبة حسب درجة التطابق",
              fontSize = 10.sp,
              color = customColors.mutedText
            )
          }
        }
      }

      // Empty State
      if (searchQuery.isNotBlank() && searchResults.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                Icons.Default.SearchOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                "لم يتم العثور على نتائج مطابقة لـ \"$searchQuery\"",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                "جرب تفعيل \"التوسيع الدلالي الذكي\" أو البحث بجذر الكلمة أو اختيار \"كافة الأقسام\".",
                fontSize = 12.sp,
                color = customColors.mutedText,
                textAlign = TextAlign.Center
              )
            }
          }
        }
      }

      // Results List
      items(searchResults, key = { it.id }) { result ->
        SearchResultCard(
          result = result,
          onCardClick = { selectedResultForDetail = result },
          onNavigateTarget = { onNavigate(result.targetRoute) },
          onPlayAudio = {
            audioEngine.playProtoSemiticChime()
            Toast.makeText(context, "تشغيل النطق الصوتي لـ: ${result.titleAr}", Toast.LENGTH_SHORT).show()
          },
          onCopyCitation = {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("Citation", result.citationText))
            Toast.makeText(context, "تم نسخ التوثيق الأكاديمي بنجاح", Toast.LENGTH_SHORT).show()
          }
        )
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }

  // Detail Dialog Modal
  selectedResultForDetail?.let { result ->
    AlertDialog(
      onDismissRequest = { selectedResultForDetail = null },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(result.type.icon, contentDescription = null, tint = result.type.badgeColor)
          Spacer(modifier = Modifier.width(8.dp))
          Text(result.titleAr, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(result.titleEn, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
          Spacer(modifier = Modifier.height(8.dp))

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("الفرع اللغوي / الإقليم:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = customColors.mutedText)
              Text(result.branchOrLanguage, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)

              if (result.metadataBadge.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("الموقع / العصر:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = customColors.mutedText)
                Text(result.metadataBadge, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
              }
            }
          }

          if (result.originalScriptOrFormula.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF1E1C18),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = result.originalScriptOrFormula,
                modifier = Modifier.padding(10.dp),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE5C158),
                letterSpacing = 1.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text("الشرح والتحليل الفيلولوجي:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = customColors.mutedText)
          Text(result.snippetAr, fontSize = 12.sp, lineHeight = 17.sp, color = MaterialTheme.colorScheme.onSurface)

          Spacer(modifier = Modifier.height(8.dp))
          Text("التوثيق الأكاديمي:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = customColors.mutedText)
          Text(result.citationText, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, lineHeight = 14.sp)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val route = result.targetRoute
            selectedResultForDetail = null
            onNavigate(route)
          },
          shape = RoundedCornerShape(8.dp)
        ) {
          Text("انتقال فوري للقسم المخصص")
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedResultForDetail = null }) {
          Text("إغلاق")
        }
      }
    )
  }
}

@Composable
fun SearchResultCard(
  result: UniversalSearchResult,
  onCardClick: () -> Unit,
  onNavigateTarget: () -> Unit,
  onPlayAudio: () -> Unit,
  onCopyCitation: () -> Unit,
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable { onCardClick() },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top Row: Category Badge + Match Score
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = result.type.badgeColor.copy(alpha = 0.15f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(result.type.icon, contentDescription = null, tint = result.type.badgeColor, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = result.type.labelAr,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = result.type.badgeColor
              )
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          Text(
            text = result.branchOrLanguage,
            fontSize = 10.sp,
            color = customColors.mutedText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
          Text(
            text = "${result.relevanceScore}% تطابق",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Title
      Text(
        text = result.titleAr,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Text(
        text = result.titleEn,
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.primary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      // Script Snippet
      if (result.originalScriptOrFormula.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF1E1C18),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = result.originalScriptOrFormula,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE5C158),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Text Snippet
      Text(
        text = result.snippetAr,
        fontSize = 11.sp,
        color = customColors.mutedText,
        maxLines = 2,
        lineHeight = 15.sp,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Action Buttons Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          IconButton(
            onClick = onPlayAudio,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Default.VolumeUp, contentDescription = "استماع", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
          }

          IconButton(
            onClick = onCopyCitation,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(Icons.Default.ContentCopy, contentDescription = "نسخ التوثيق", tint = customColors.mutedText, modifier = Modifier.size(18.dp))
          }
        }

        OutlinedButton(
          onClick = onNavigateTarget,
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.height(32.dp)
        ) {
          Text("انتقال للقسم", fontSize = 11.sp)
          Spacer(modifier = Modifier.width(4.dp))
          Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
        }
      }
    }
  }
}
