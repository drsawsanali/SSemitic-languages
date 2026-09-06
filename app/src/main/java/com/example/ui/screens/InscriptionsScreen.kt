package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.models.AcademicInscription
import com.example.data.repository.InscriptionsData
import com.example.ui.components.MultiSpectralViewer
import com.example.ui.theme.LocalCustomColors
import com.example.ui.viewmodel.BookmarkViewModel
import com.example.util.AudioEngine
import com.example.util.CitationGenerator

@Composable
fun InscriptionsScreen(
  modifier: Modifier = Modifier,
  bookmarkViewModel: BookmarkViewModel = viewModel()
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  val audioEngine = remember { AudioEngine(context) }

  val bookmarkedIdSet by bookmarkViewModel.bookmarkedIdSet.collectAsStateWithLifecycle()
  val totalBookmarksCount by bookmarkViewModel.totalCount.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableStateOf(0) }
  var searchQuery by remember { mutableStateOf("") }
  var selectedFamily by remember { mutableStateOf("الكل") }
  var expandedInscriptionId by remember { mutableStateOf<String?>(null) }
  var selectedSpectralInscription by remember { mutableStateOf<AcademicInscription?>(null) }

  val families = listOf("الكل", "السامية الشرقية", "السامية الشمالية الغربية", "السامية الجنوبية القديمة", "السامية الإثيوبية")

  val filteredList = remember(searchQuery, selectedFamily) {
    InscriptionsData.list.filter { item ->
      val matchesFamily = when (selectedFamily) {
        "الكل" -> true
        "السامية الشرقية" -> item.family.contains("East") || item.language.contains("Akkadian") || item.language.contains("Babylonian") || item.language.contains("Assyrian")
        "السامية الشمالية الغربية" -> item.family.contains("Northwest") || item.language.contains("Phoenician") || item.language.contains("Moabite") || item.language.contains("Ugaritic") || item.language.contains("Aramaic") || item.language.contains("Hebrew")
        "السامية الجنوبية القديمة" -> item.family.contains("South") || item.language.contains("Sabaic") || item.language.contains("Minaic")
        "السامية الإثيوبية" -> item.family.contains("Ethio") || item.language.contains("Ge'ez")
        else -> true
      }
      val matchesSearch = searchQuery.isBlank() ||
        item.titleAr.contains(searchQuery, ignoreCase = true) ||
        item.title.contains(searchQuery, ignoreCase = true) ||
        item.site.contains(searchQuery, ignoreCase = true) ||
        item.language.contains(searchQuery, ignoreCase = true) ||
        item.originalScriptText.contains(searchQuery, ignoreCase = true) ||
        item.transliteration.contains(searchQuery, ignoreCase = true)

      matchesFamily && matchesSearch
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      audioEngine.release()
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Navigation TabRow
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = customColors.cardBackground,
      contentColor = MaterialTheme.colorScheme.primary
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("المعرض الأثري والمخطوطات", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
          }
        }
      )
      Tab(
        selected = selectedTab == 1,
        onClick = { selectedTab = 1 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ManageSearch, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("محرك بحث Room", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
          }
        }
      )
      Tab(
        selected = selectedTab == 2,
        onClick = { selectedTab = 2 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              if (totalBookmarksCount > 0) "المحفوظات ($totalBookmarksCount)" else "المحفوظات",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      )
    }

    if (selectedTab == 1) {
      InscriptionSearchScreen(modifier = Modifier.fillMaxSize())
    } else if (selectedTab == 2) {
      BookmarksScreen(
        modifier = Modifier.fillMaxSize(),
        onNavigateToInscriptions = { selectedTab = 0 },
        bookmarkViewModel = bookmarkViewModel
      )
    } else {
      // Top Bar Actions (Search & CSV export)
      Surface(
        color = customColors.cardBackground,
        shadowElevation = 2.dp
      ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "أرشيف النقوش واللقى الأثرية",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "عرض ${filteredList.size} من أصل ${InscriptionsData.list.size} نقشاً موثقاً",
              style = MaterialTheme.typography.bodySmall,
              color = customColors.mutedText
            )
          }

          FilledTonalButton(
            onClick = {
              val csv = generateInscriptionsCsv()
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              clipboard.setPrimaryClip(ClipData.newPlainText("Inscriptions CSV", csv))
              Toast.makeText(context, "تم نسخ بيانات النقوش بصيغة CSV إلى الحافظة بنجاح", Toast.LENGTH_LONG).show()
            }
          ) {
            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("تصدير CSV", fontSize = 12.sp)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Input
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          modifier = Modifier.fillMaxWidth(),
          placeholder = { Text("بحث في النقوش (حمورابي، ميشع، أحيرام، صرواح، مأرب...)") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "مسح")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Family Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(families) { fam ->
            FilterChip(
              selected = selectedFamily == fam,
              onClick = { selectedFamily = fam },
              label = { Text(fam, fontSize = 12.sp) }
            )
          }
        }
      }
    }

    // Modal / Overlay if spectral inspection is active
    selectedSpectralInscription?.let { ins ->
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "المعاينة الطيفية: ${ins.titleAr}",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = { selectedSpectralInscription = null }) {
              Icon(Icons.Default.Close, contentDescription = "إغلاق")
            }
          }
          MultiSpectralViewer(
            imageUrl = ins.imageUrl,
            titleAr = ins.titleAr,
            titleEn = ins.title
          )
        }
      }
    }

    // List of Inscriptions
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(filteredList, key = { it.id }) { item ->
        val isExpanded = expandedInscriptionId == item.id
        val isBookmarked = bookmarkedIdSet.contains("INSCRIPTION_${item.id}")

        InscriptionCard(
          item = item,
          isExpanded = isExpanded,
          isBookmarked = isBookmarked,
          onToggleBookmark = {
            bookmarkViewModel.toggleInscriptionBookmark(item)
            val msg = if (isBookmarked) "تمت الإزالة من المحفوظات" else "تم الحفظ في المحفوظات بنجاح!"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
          },
          onToggleExpand = {
            expandedInscriptionId = if (isExpanded) null else item.id
          },
          onOpenSpectral = {
            selectedSpectralInscription = item
          },
          onSpeak = { text ->
            audioEngine.speak(text)
          },
          onCopyCitation = { style ->
            val citation = CitationGenerator.generateInscriptionCitation(item, style)
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Citation $style", citation))
            Toast.makeText(context, "تم نسخ التوثيق بصيغة $style", Toast.LENGTH_SHORT).show()
          }
        )
      }
    }
    }
  }
}

@Composable
fun InscriptionCard(
  item: AcademicInscription,
  isExpanded: Boolean,
  isBookmarked: Boolean,
  onToggleBookmark: () -> Unit,
  onToggleExpand: () -> Unit,
  onOpenSpectral: () -> Unit,
  onSpeak: (String) -> Unit,
  onCopyCitation: (String) -> Unit
) {
  val customColors = LocalCustomColors.current

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: Language Family Tag, Discovery Site, and Bookmark Icon
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = item.language,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }

          Text(
            text = "${item.site} • ${item.period}",
            style = MaterialTheme.typography.labelSmall,
            color = customColors.mutedText
          )
        }

        IconButton(
          onClick = onToggleBookmark,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
            contentDescription = if (isBookmarked) "إزالة من المحفوظات" else "حفظ في المحفوظات",
            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else customColors.mutedText
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Title & Image Preview Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        AsyncImage(
          model = item.imageUrl,
          contentDescription = item.titleAr,
          modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, customColors.border, RoundedCornerShape(10.dp))
            .clickable { onOpenSpectral() }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = item.titleAr,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = item.title,
            style = MaterialTheme.typography.labelSmall,
            color = customColors.mutedText
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "الخامة: ${item.material} • الحاكم: ${item.ruler}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 11.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Ancient Original Script Text Banner
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(MaterialTheme.colorScheme.surface)
          .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
          .padding(10.dp)
      ) {
        Text(
          text = item.originalScriptText,
          style = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
          ),
          color = MaterialTheme.colorScheme.primary
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Quick Actions Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          OutlinedButton(
            onClick = onOpenSpectral,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("فحص طيفي", fontSize = 11.sp)
          }

          OutlinedButton(
            onClick = { onSpeak(item.translationAr) },
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            shape = RoundedCornerShape(8.dp)
          ) {
            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("استماع", fontSize = 11.sp)
          }
        }

        TextButton(onClick = onToggleExpand) {
          Text(if (isExpanded) "طي التحليل ▲" else "التحليل الفيلولوجي ▼", fontSize = 12.sp)
        }
      }

      // Expanded Detail Section
      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 10.dp)) {
          Divider(color = customColors.border)
          Spacer(modifier = Modifier.height(8.dp))

          // Latin Transliteration
          DetailSection("النقحرة الصوتية اللاتينية (Transliteration)", item.transliteration)

          // Arabic Translation
          DetailSection("الترجمة العربية الفصحى الشارحة", item.translationAr)

          // Historical and Linguistic Commentary
          DetailSection("السياق التاريخي والإبيغرافي", item.notesAr)

          // Corpus Reference & Museum
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "المدونة: ${item.corpus}", style = MaterialTheme.typography.bodySmall, color = customColors.mutedText)
            Text(text = "الحالة / الموقع: ${item.status}", style = MaterialTheme.typography.bodySmall, color = customColors.mutedText)
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Citation Actions
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "توثيق واقتباس:", style = MaterialTheme.typography.labelSmall, color = customColors.mutedText)
            SuggestionChip(onClick = { onCopyCitation("APA") }, label = { Text("APA", fontSize = 10.sp) })
            SuggestionChip(onClick = { onCopyCitation("MLA") }, label = { Text("MLA", fontSize = 10.sp) })
            SuggestionChip(onClick = { onCopyCitation("BibTeX") }, label = { Text("BibTeX", fontSize = 10.sp) })
          }
        }
      }
    }
  }
}

@Composable
fun DetailSection(title: String, content: String) {
  val customColors = LocalCustomColors.current
  Column(modifier = Modifier.padding(vertical = 4.dp)) {
    Text(
      text = title,
      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = content,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurface,
      lineHeight = 20.sp
    )
  }
}

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
