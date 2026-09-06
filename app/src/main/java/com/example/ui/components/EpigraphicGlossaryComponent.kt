package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.models.EpigraphicGlossaryTerm
import com.example.data.repository.EncyclopediaRepository
import com.example.data.repository.EpigraphicGlossaryData
import com.example.ui.theme.LocalCustomColors
import com.example.ui.viewmodel.BookmarkViewModel
import com.example.util.AudioEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpigraphicGlossaryComponent(
  modifier: Modifier = Modifier,
  initialCategory: String = "الكل",
  initialSearchQuery: String = "",
  onAskRobertAi: ((String) -> Unit)? = null,
  onNavigateToInscriptions: (() -> Unit)? = null,
  bookmarkViewModel: BookmarkViewModel = viewModel()
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  val audioEngine = remember { AudioEngine(context) }

  val bookmarkedIdSet by bookmarkViewModel.bookmarkedIdSet.collectAsStateWithLifecycle()

  var searchQuery by remember { mutableStateOf(initialSearchQuery) }
  var selectedCategory by remember { mutableStateOf(initialCategory) }
  var activeDetailTerm by remember { mutableStateOf<EpigraphicGlossaryTerm?>(null) }
  var isSpeaking by remember { mutableStateOf(false) }

  val allTerms = remember { EncyclopediaRepository.getEpigraphicGlossary() }
  val categories = remember { listOf("الكل", "المحفوظات ⭐") + EpigraphicGlossaryData.categoriesAr }

  val filteredTerms = remember(searchQuery, selectedCategory, allTerms, bookmarkedIdSet) {
    allTerms.filter { term ->
      val matchesCategory = when (selectedCategory) {
        "الكل" -> true
        "المحفوظات ⭐" -> bookmarkedIdSet.contains("GLOSSARY_${term.id}")
        else -> term.categoryAr == selectedCategory
      }
      val query = searchQuery.trim().lowercase()
      val matchesQuery = query.isEmpty() ||
        term.termAr.lowercase().contains(query) ||
        term.termEn.lowercase().contains(query) ||
        term.transliteration.lowercase().contains(query) ||
        term.shortDefinitionAr.lowercase().contains(query) ||
        term.shortDefinitionEn.lowercase().contains(query) ||
        term.detailedDefinitionAr.lowercase().contains(query) ||
        term.inscriptionalWitnessesAr.lowercase().contains(query) ||
        term.ancientScriptType.lowercase().contains(query)
      matchesCategory && matchesQuery
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Search Header and Filter Bar
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp)
      ) {
        // Search Input
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          placeholder = {
            Text(
              "ابحث في مصطلحات الإبيغرافيا، القوانين الصوتية، مواد التدوين...",
              fontSize = 13.sp,
              color = customColors.mutedText
            )
          },
          leadingIcon = {
            Icon(
              Icons.Default.Search,
              contentDescription = "بحث",
              tint = MaterialTheme.colorScheme.primary
            )
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "مسح", tint = customColors.mutedText)
              }
            }
          },
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Categories Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          categories.forEach { cat ->
            val isSelected = cat == selectedCategory
            FilterChip(
              selected = isSelected,
              onClick = { selectedCategory = cat },
              label = {
                Text(
                  text = cat,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              leadingIcon = if (isSelected) {
                {
                  Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                }
              } else null,
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
              ),
              shape = RoundedCornerShape(20.dp)
            )
          }
        }
      }
    }

    // Quick Stats Info Strip
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "المصطلحات المعروضة: ${filteredTerms.size} من أصل ${allTerms.size}",
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = customColors.mutedText
      )

      if (searchQuery.isNotEmpty() || selectedCategory != "الكل") {
        TextButton(
          onClick = {
            searchQuery = ""
            selectedCategory = "الكل"
          },
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("إعادة التعيين", fontSize = 11.sp)
        }
      }
    }

    // Terms List
    if (filteredTerms.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = customColors.mutedText.copy(alpha = 0.5f)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "لم يتم العثور على مصطلح يطابق بحثك",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "جرّب البحث باسم المصطلح بالإنجليزية، أو نوع الخط، أو القانون الصوتي",
            fontSize = 13.sp,
            color = customColors.mutedText,
            textAlign = TextAlign.Center
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(filteredTerms, key = { it.id }) { term ->
          val isBookmarked = bookmarkedIdSet.contains("GLOSSARY_${term.id}")
          EpigraphicTermCard(
            term = term,
            isBookmarked = isBookmarked,
            onToggleBookmark = {
              bookmarkViewModel.toggleGlossaryBookmark(term)
              val msg = if (isBookmarked) "تمت الإزالة من المحفوظات" else "تم حفظ المصطلح في المحفوظات!"
              Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            },
            onClick = { activeDetailTerm = term },
            onQuickAudio = {
              if (isSpeaking) {
                audioEngine.stop()
                isSpeaking = false
              } else {
                isSpeaking = true
                val speechText = "${term.termAr}. ${term.shortDefinitionAr}"
                audioEngine.speak(speechText) {
                  isSpeaking = false
                }
              }
            }
          )
        }
      }
    }
  }

  // Deep Detail Modal Sheet
  activeDetailTerm?.let { term ->
    val isBookmarked = bookmarkedIdSet.contains("GLOSSARY_${term.id}")
    EpigraphicTermDetailModal(
      term = term,
      allTerms = allTerms,
      audioEngine = audioEngine,
      isBookmarked = isBookmarked,
      onToggleBookmark = {
        bookmarkViewModel.toggleGlossaryBookmark(term)
        val msg = if (isBookmarked) "تمت الإزالة من المحفوظات" else "تم حفظ المصطلح في المحفوظات!"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
      },
      onDismiss = {
        audioEngine.stop()
        activeDetailTerm = null
      },
      onSelectRelatedTerm = { relatedId ->
        allTerms.find { it.id == relatedId }?.let {
          activeDetailTerm = it
        }
      },
      onAskRobertAi = { prompt ->
        activeDetailTerm = null
        onAskRobertAi?.invoke(prompt)
      },
      onNavigateToInscriptions = {
        activeDetailTerm = null
        onNavigateToInscriptions?.invoke()
      }
    )
  }
}

@Composable
fun EpigraphicTermCard(
  term: EpigraphicGlossaryTerm,
  isBookmarked: Boolean,
  onToggleBookmark: () -> Unit,
  onClick: () -> Unit,
  onQuickAudio: () -> Unit
) {
  val customColors = LocalCustomColors.current

  val categoryColor = when (term.categoryAr) {
    "الإبيغرافيا والخطوط" -> Color(0xFF1E88E5)
    "علم الأصوات السامية" -> Color(0xFF9C27B0)
    "الصرف والمورفولوجيا" -> Color(0xFF2E7D32)
    "الآثار ومواد التدوين" -> Color(0xFFD84315)
    else -> Color(0xFF00897B)
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable { onClick() },
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      // Top row: Category Badge & Glyph Preview & Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Surface(
            color = categoryColor.copy(alpha = 0.12f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = term.categoryAr,
              color = categoryColor,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }

          if (term.ancientSampleGlyphs.isNotEmpty()) {
            Surface(
              color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = term.ancientSampleGlyphs.take(18),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
          IconButton(
            onClick = onQuickAudio,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.VolumeUp,
              contentDescription = "استماع للنطق",
              modifier = Modifier.size(18.dp),
              tint = MaterialTheme.colorScheme.primary
            )
          }

          IconButton(
            onClick = onToggleBookmark,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
              contentDescription = if (isBookmarked) "إزالة من المحفوظات" else "حفظ في المحفوظات",
              modifier = Modifier.size(18.dp),
              tint = if (isBookmarked) MaterialTheme.colorScheme.primary else customColors.mutedText
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Term Title (Arabic & English)
      Text(
        text = term.termAr,
        fontSize = 17.sp,
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = term.termEn,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.primary
        )
        if (term.transliteration.isNotEmpty()) {
          Text(
            text = "• ${term.transliteration}",
            fontSize = 12.sp,
            fontStyle = FontStyle.Italic,
            color = customColors.mutedText
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Short definition
      Text(
        text = term.shortDefinitionAr,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Bottom Row: Witness Preview & Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            Icons.Default.HistoryEdu,
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            tint = customColors.mutedText
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = term.ancientScriptType,
            fontSize = 11.sp,
            color = customColors.mutedText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          IconButton(
            onClick = onQuickAudio,
            modifier = Modifier.size(34.dp)
          ) {
            Icon(
              Icons.Default.VolumeUp,
              contentDescription = "استماع",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
          }

          FilledTonalButton(
            onClick = onClick,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
            modifier = Modifier.height(32.dp),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text("تحقيق وتفصيل", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.ChevronLeft, contentDescription = null, modifier = Modifier.size(14.dp))
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpigraphicTermDetailModal(
  term: EpigraphicGlossaryTerm,
  allTerms: List<EpigraphicGlossaryTerm>,
  audioEngine: AudioEngine,
  isBookmarked: Boolean,
  onToggleBookmark: () -> Unit,
  onDismiss: () -> Unit,
  onSelectRelatedTerm: (String) -> Unit,
  onAskRobertAi: (String) -> Unit,
  onNavigateToInscriptions: () -> Unit
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  var isPlayingAudio by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .padding(bottom = 36.dp)
    ) {
      // Header badge and close & bookmark
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = MaterialTheme.colorScheme.primaryContainer,
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = term.categoryAr,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onToggleBookmark) {
            Icon(
              imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
              contentDescription = if (isBookmarked) "إزالة من المحفوظات" else "حفظ في المحفوظات",
              tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "إغلاق")
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Titles
      Text(
        text = term.termAr,
        fontSize = 22.sp,
        fontWeight = FontWeight.Black,
        color = MaterialTheme.colorScheme.onSurface
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(
          text = term.termEn,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
        if (term.transliteration.isNotEmpty()) {
          Text(
            text = "[ ${term.transliteration} ]",
            fontSize = 14.sp,
            fontStyle = FontStyle.Italic,
            color = customColors.mutedText
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Ancient Script Visual Banner
      if (term.ancientSampleGlyphs.isNotEmpty()) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "الشاهد الأبجدي / النمط الخطي",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = customColors.mutedText
              )
              Text(
                text = term.ancientScriptType,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = term.ancientSampleGlyphs,
              fontSize = 20.sp,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.fillMaxWidth(),
              textAlign = TextAlign.Center
            )
          }
        }
        Spacer(modifier = Modifier.height(14.dp))
      }

      // Audio & Citation Action Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = {
            if (isPlayingAudio) {
              audioEngine.stop()
              isPlayingAudio = false
            } else {
              isPlayingAudio = true
              val textToSpeak = "${term.termAr}. ${term.detailedDefinitionAr}"
              audioEngine.speak(textToSpeak) {
                isPlayingAudio = false
              }
            }
          },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isPlayingAudio) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
          )
        ) {
          Icon(
            if (isPlayingAudio) Icons.Default.Stop else Icons.Default.VolumeUp,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            if (isPlayingAudio) "إيقاف القراءة" else "قراءة صوتية للمصطلح",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }

        OutlinedButton(
          onClick = {
            audioEngine.playAcousticFormantChime(f1 = 600, f2 = 1800, durationMs = 500)
          },
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.GraphicEq, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("رنين فيلولوجي", fontSize = 11.sp)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Section 1: Detailed Definition (Arabic & English)
      Text(
        text = "📖 التعريف الأكاديمي الشامل",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = term.detailedDefinitionAr,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = term.detailedDefinitionEn,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        color = customColors.mutedText,
        fontStyle = FontStyle.Italic
      )

      Spacer(modifier = Modifier.height(14.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
      Spacer(modifier = Modifier.height(14.dp))

      // Section 2: Inscriptional Witnesses
      Text(
        text = "🏛️ الشواهد الأثرية والنقشية في المتاحف",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = term.inscriptionalWitnessesAr,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        color = MaterialTheme.colorScheme.onSurface
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = term.inscriptionalWitnessesEn,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        color = customColors.mutedText
      )

      Spacer(modifier = Modifier.height(14.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
      Spacer(modifier = Modifier.height(14.dp))

      // Section 3: Philological Importance
      Text(
        text = "🔬 الأهمية في فك الرموز والتحقيق اللغوي",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = term.philologicalImportanceAr,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Section 4: Related Terms Chips
      if (term.relatedTermIds.isNotEmpty()) {
        Text(
          text = "🔗 مصطلحات ذات صلة",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          term.relatedTermIds.forEach { relId ->
            val relTerm = allTerms.find { it.id == relId }
            val label = relTerm?.termAr ?: relId
            SuggestionChip(
              onClick = { onSelectRelatedTerm(relId) },
              label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
              icon = {
                Icon(
                  Icons.Default.ArrowOutward,
                  contentDescription = null,
                  modifier = Modifier.size(13.dp)
                )
              }
            )
          }
        }
        Spacer(modifier = Modifier.height(14.dp))
      }

      // Academic Citation & Copy
      if (term.citation.isNotEmpty()) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "المراجع والمصادر الأكاديمية",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = customColors.mutedText
              )
              Text(
                text = term.citation,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            IconButton(
              onClick = {
                val citationText = "${term.termAr} (${term.termEn}): ${term.shortDefinitionAr} [المصدر: ${term.citation}]"
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Epigraphic Term", citationText)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "تم نسخ توثيق المصطلح الحافظة", Toast.LENGTH_SHORT).show()
              }
            ) {
              Icon(
                Icons.Default.ContentCopy,
                contentDescription = "نسخ التوثيق",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(16.dp))
      }

      // Bottom Primary Action: Ask Robert AI Philologist!
      FilledTonalButton(
        onClick = {
          val prompt = "حلل لي المصطلح الإبيغرافي التالي بالتفصيل الفيلولوجي وشواهده في اللغات السامية: '${term.termAr} (${term.termEn})' - ${term.shortDefinitionAr}"
          onAskRobertAi(prompt)
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
      ) {
        Icon(Icons.Default.SmartToy, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "مناقشة وتحليل المصطلح مع المساعد روبرت AI",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
      }
    }
  }
}
