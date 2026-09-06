package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.BooksSourcesRepository
import com.example.data.repository.SourceRecord
import com.example.ui.theme.LocalCustomColors
import com.example.ui.viewmodel.EncyclopediaCacheViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BooksScreen(
  onNavigateToInscription: (String) -> Unit = {},
  onNavigateToLanguage: (String) -> Unit = {},
  onNavigateToDownloadedLibrary: (() -> Unit)? = null,
  cacheViewModel: EncyclopediaCacheViewModel = viewModel()
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current

  val downloadedIdSet by cacheViewModel.downloadedIdSet.collectAsStateWithLifecycle()
  var sources by remember { mutableStateOf(BooksSourcesRepository.getSources(context)) }
  var searchQuery by remember { mutableStateOf("") }
  var selectedLanguageFilter by remember { mutableStateOf<String?>(null) }
  var activeBookForReader by remember { mutableStateOf<SourceRecord?>(null) }
  var showHtmlViewerModal by remember { mutableStateOf(false) }

  // Languages for filter chips
  val languageChips = listOf(
    "الكل",
    "الفينيقية",
    "الأوغاريتية",
    "السبئية",
    "المؤابية",
    "العربية القديمة",
    "الآرامية"
  )

  val filteredSources = remember(sources, searchQuery, selectedLanguageFilter) {
    sources.filter { source ->
      val matchesFilter = when (selectedLanguageFilter) {
        null, "الكل" -> true
        "الفينيقية" -> source.relatedLanguages.any { it.contains("Phoenician", ignoreCase = true) } || source.language.contains("فينيق")
        "الأوغاريتية" -> source.relatedLanguages.any { it.contains("Ugaritic", ignoreCase = true) } || source.language.contains("أوغاريت")
        "السبئية" -> source.relatedLanguages.any { it.contains("Sabaic", ignoreCase = true) } || source.language.contains("سبئ")
        "المؤابية" -> source.relatedLanguages.any { it.contains("Moabite", ignoreCase = true) } || source.language.contains("مؤاب")
        "العربية القديمة" -> source.relatedLanguages.any { it.contains("Arabic", ignoreCase = true) } || source.language.contains("عرب")
        "الآرامية" -> source.relatedLanguages.any { it.contains("Aramaic", ignoreCase = true) } || source.language.contains("آرام")
        else -> true
      }

      val q = searchQuery.trim().lowercase()
      val matchesQuery = if (q.isEmpty()) true else {
        source.title.lowercase().contains(q) ||
        source.titleAr.lowercase().contains(q) ||
        source.author.lowercase().contains(q) ||
        source.originalFileName.lowercase().contains(q) ||
        source.language.lowercase().contains(q) ||
        source.relatedInscriptions.any { it.lowercase().contains(q) } ||
        source.relatedLanguages.any { it.lowercase().contains(q) }
      }

      matchesFilter && matchesQuery
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "خزانة الكتب والمصادر (PDF)",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "سجلات المصادر وعارض PDF المستقل",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.primary
            )
          }
        },
        actions = {
          if (onNavigateToDownloadedLibrary != null) {
            FilledTonalButton(
              onClick = onNavigateToDownloadedLibrary,
              modifier = Modifier.padding(end = 4.dp)
            ) {
              Icon(Icons.Default.CloudDone, contentDescription = "المكتبة المحمّلة", modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("المكتبة المحمّلة", fontSize = 12.sp)
            }
          }
          FilledTonalButton(
            onClick = { showHtmlViewerModal = true },
            modifier = Modifier.padding(end = 8.dp)
          ) {
            Icon(Icons.Default.Web, contentDescription = "العارض الكامل", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("مكوّن HTML المستقل", fontSize = 12.sp)
          }
        }
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header Banner
      item {
        Card(
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("📚", fontSize = 28.sp)
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "توثيق وفحص الكتب والمصادر الإبيغرافية",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = "مكوّن مستقل لاستخراج بيانات المراجع وربطها بالنقوش مع عارض PDF مباشر دون مغادرة الصفحة.",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Badges breakdown
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              StatBadge("إجمالي المراجع", "${sources.size}", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
              StatBadge("بيانات دقيقة", "موثقة", Color(0xFF2E7D32), Modifier.weight(1f))
              StatBadge("وسم الناقص", "غير معروف", Color(0xFFC62828), Modifier.weight(1f))
            }
          }
        }
      }

      // Search Field
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("sources_search_field"),
          placeholder = { Text("ابحث بالعنوان، اسم الملف، المؤلف، أو النقش المرتبط...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث") },
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
      }

      // Filter Chips
      item {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(languageChips) { chip ->
            val isSelected = (selectedLanguageFilter == null && chip == "الكل") || selectedLanguageFilter == chip
            FilterChip(
              selected = isSelected,
              onClick = {
                selectedLanguageFilter = if (chip == "الكل") null else chip
              },
              label = { Text(chip, fontSize = 12.sp) }
            )
          }
        }
      }

      // Results count
      item {
        Text(
          text = "تم العثور على ${filteredSources.size} سجل مصدر (Source Record):",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.outline
        )
      }

      // Book / Source Cards
      items(filteredSources, key = { it.id }) { source ->
        SourceRecordCard(
          source = source,
          isDownloaded = downloadedIdSet.contains(source.id),
          onDownloadClick = { cacheViewModel.cacheSourceRecord(source) },
          onReadClick = { activeBookForReader = source },
          onExternalOpen = {
            try {
              // Open via chooser or browser
              val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(Uri.parse("file:///android_asset/${source.filePath}"), "application/pdf")
                flags = Intent.FLAG_ACTIVITY_NO_HISTORY
              }
              context.startActivity(Intent.createChooser(intent, "فتح الكتاب عبر:"))
            } catch (e: Exception) {
              // Fallback open HTML viewer
              showHtmlViewerModal = true
            }
          }
        )
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  // In-App PDF Reader Modal Dialog
  activeBookForReader?.let { book ->
    Dialog(
      onDismissRequest = { activeBookForReader = null },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      Surface(
        modifier = Modifier
          .fillMaxSize()
          .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
      ) {
        Column(modifier = Modifier.fillMaxSize()) {
          // Top bar of the reader
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              IconButton(onClick = { activeBookForReader = null }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = book.titleAr,
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "ملف: ${book.originalFileName} | تاريخ: ${book.extractionDate}",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              // Fallback open in external viewer / browser
              IconButton(
                onClick = {
                  try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                      setDataAndType(Uri.parse("file:///android_asset/${book.filePath}"), "application/pdf")
                      flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                    }
                    context.startActivity(Intent.createChooser(intent, "فتح في تطبيق مستقل:"))
                  } catch (e: Exception) {
                    showHtmlViewerModal = true
                  }
                }
              ) {
                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "فتح في تبويب / تطبيق جديد")
              }
              IconButton(onClick = { activeBookForReader = null }) {
                Icon(Icons.Default.Close, contentDescription = "إغلاق")
              }
            }
          }

          // In-App Document Inspection & Content Panel
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(MaterialTheme.colorScheme.surfaceContainerHigh)
              .padding(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              MetadataPill("المؤلف", book.author, book.isAuthorUnknown, Modifier.weight(1f))
              MetadataPill("سنة النشر", book.publicationYear, book.isYearUnknown, Modifier.weight(1f))
              MetadataPill("اللغة", book.language, book.isLanguageUnknown, Modifier.weight(1f))
            }
          }

          // Embedded HTML/PDF Viewer via Android WebView
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
          ) {
            AndroidView(
              factory = { ctx ->
                WebView(ctx).apply {
                  settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = true
                    allowContentAccess = true
                    builtInZoomControls = true
                    displayZoomControls = false
                    useWideViewPort = true
                    loadWithOverviewMode = true
                  }
                  webViewClient = WebViewClient()
                  // Load the standalone in-app reader component
                  loadUrl("file:///android_asset/books_viewer.html")
                }
              },
              modifier = Modifier.fillMaxSize()
            )
          }
        }
      }
    }
  }

  // Full Standalone HTML/JS Component Modal Dialog
  if (showHtmlViewerModal) {
    Dialog(
      onDismissRequest = { showHtmlViewerModal = false },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      Surface(
        modifier = Modifier
          .fillMaxSize()
          .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface
      ) {
        Column(modifier = Modifier.fillMaxSize()) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🌐", fontSize = 20.sp)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "مكوّن HTML وJS المستقل (books_viewer.html)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
            }
            IconButton(onClick = { showHtmlViewerModal = false }) {
              Icon(Icons.Default.Close, contentDescription = "إغلاق")
            }
          }

          AndroidView(
            factory = { ctx ->
              WebView(ctx).apply {
                settings.apply {
                  javaScriptEnabled = true
                  domStorageEnabled = true
                  allowFileAccess = true
                  allowContentAccess = true
                  builtInZoomControls = true
                  displayZoomControls = false
                  useWideViewPort = true
                  loadWithOverviewMode = true
                }
                webViewClient = WebViewClient()
                loadUrl("file:///android_asset/books_viewer.html")
              }
            },
            modifier = Modifier.fillMaxSize()
          )
        }
      }
    }
  }
}

@Composable
private fun SourceRecordCard(
  source: SourceRecord,
  isDownloaded: Boolean = false,
  onDownloadClick: () -> Unit = {},
  onReadClick: () -> Unit,
  onExternalOpen: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("source_card_${source.id}"),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    shape = RoundedCornerShape(12.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Offline Status Pill & Category Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        ) {
          Text(
            text = source.sourceType,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        if (isDownloaded) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFF2E7D32).copy(alpha = 0.12f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("محفوظ دون اتصال (Room)", color = Color(0xFF2E7D32), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Header: Arabic and original titles
      Text(
        text = source.titleAr,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
      )
      if (source.title != source.titleAr) {
        Text(
          text = source.title,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.outline
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Key metadata pills (Author, Year, Language) with strict "غير معروف" styling
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MetadataPill("المؤلف", source.author, source.isAuthorUnknown, Modifier.weight(1f))
        MetadataPill("سنة النشر", source.publicationYear, source.isYearUnknown, Modifier.weight(1f))
        MetadataPill("اللغة", source.language, source.isLanguageUnknown, Modifier.weight(1f))
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Dedicated Source Record Box (سجل المصدر)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
          .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
          .padding(10.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.InsertDriveFile, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(6.dp))
            Text("الملف الأصلي:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              source.originalFileName,
              style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(6.dp))
            Text("تاريخ الاستخراج:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Text(source.extractionDate, style = MaterialTheme.typography.bodySmall)
          }

          if (source.relatedInscriptions.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
              Spacer(modifier = Modifier.width(6.dp))
              Text("النقوش المرتبطة:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                source.relatedInscriptions.joinToString(", "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
              )
            }
          }

          if (source.relatedLanguages.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.tertiary)
              Spacer(modifier = Modifier.width(6.dp))
              Text("اللغات المرتبطة:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                source.relatedLanguages.joinToString("، "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Summary
      Text(
        text = source.summary,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Button(
          onClick = onReadClick,
          modifier = Modifier
            .weight(1.3f)
            .testTag("btn_read_${source.id}"),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("قراءة مباشرة بالعارض", fontSize = 12.sp)
        }

        OutlinedButton(
          onClick = onExternalOpen,
          modifier = Modifier
            .weight(1f)
            .testTag("btn_external_${source.id}")
        ) {
          Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("تبويب جديد", fontSize = 12.sp)
        }

        FilledTonalIconButton(
          onClick = onDownloadClick,
          modifier = Modifier
            .size(40.dp)
            .testTag("btn_offline_cache_${source.id}")
        ) {
          Icon(
            imageVector = if (isDownloaded) Icons.Default.CloudDone else Icons.Default.CloudDownload,
            contentDescription = if (isDownloaded) "محفوظ في الذاكرة دون اتصال" else "حفظ للمكتبة دون اتصال",
            tint = if (isDownloaded) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun MetadataPill(
  label: String,
  value: String,
  isUnknown: Boolean,
  modifier: Modifier = Modifier
) {
  val bgColor = if (isUnknown) Color(0x22D32F2F) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
  val textColor = if (isUnknown) Color(0xFFD32F2F) else MaterialTheme.colorScheme.onSecondaryContainer

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(bgColor)
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    Column {
      Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
      Text(
        text = if (isUnknown) "غير معروف" else value,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
        color = textColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
private fun StatBadge(
  title: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
    shape = RoundedCornerShape(8.dp)
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = color)
      Text(title, style = MaterialTheme.typography.labelSmall, color = color)
    }
  }
}
