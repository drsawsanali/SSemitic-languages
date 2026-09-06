package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entity.CachedEncyclopediaEntryEntity
import com.example.data.repository.BooksSourcesRepository
import com.example.ui.theme.LocalCustomColors
import com.example.ui.viewmodel.EncyclopediaCacheViewModel
import com.example.util.AudioEngine
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadedLibraryScreen(
  modifier: Modifier = Modifier,
  onBack: (() -> Unit)? = null,
  onNavigateToChapter: ((String) -> Unit)? = null,
  onNavigateToSource: (() -> Unit)? = null,
  cacheViewModel: EncyclopediaCacheViewModel = viewModel()
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  val audioEngine = remember { AudioEngine(context) }

  val filteredEntries by cacheViewModel.filteredEntries.collectAsStateWithLifecycle()
  val totalCount by cacheViewModel.downloadedCount.collectAsStateWithLifecycle()
  val totalStorageBytes by cacheViewModel.totalStorageBytes.collectAsStateWithLifecycle()
  val filterState by cacheViewModel.filterState.collectAsStateWithLifecycle()
  val isSyncingAll by cacheViewModel.isSyncingAll.collectAsStateWithLifecycle()
  val syncMessage by cacheViewModel.syncMessage.collectAsStateWithLifecycle()

  var selectedEntryForReading by remember { mutableStateOf<CachedEncyclopediaEntryEntity?>(null) }
  var showClearAllConfirm by remember { mutableStateOf(false) }

  // Sync snackbar / toast trigger
  LaunchedEffect(syncMessage) {
    syncMessage?.let {
      Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
      cacheViewModel.clearSyncMessage()
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      audioEngine.release()
    }
  }

  val typeFilters = listOf(
    "ALL" to "الكل",
    "CHAPTER" to "فصول الموسوعة",
    "BOOK_SOURCE" to "الكتب والمصادر PDF",
    "INSCRIPTION" to "النقوش والمسلات",
    "GLOSSARY" to "المسرد الإبيغرافي"
  )

  val scriptFilters = listOf(
    "ALL" to "جميع الخطوط",
    "CUNEIFORM" to "مسماري (أكدي/سومري)",
    "PHOENICIAN" to "فينيقي / كنعاني",
    "MUSNAD" to "خط المسند العربي الجنوبي",
    "ARAMAIC" to "آرامي قديم وسرياني",
    "NABATAEAN" to "نبطي (البتراء/الحجر)",
    "GEEZ" to "جعزي (أكسومي وحبشي)",
    "UGARITIC" to "أوغاريتي أبجدي",
    "ARABIC" to "عربي مبكر وثمودي/صفائي"
  )

  val regionFilters = listOf(
    "ALL" to "جميع الأقاليم",
    "MESOPOTAMIA" to "بلاد الرافدين (العراق)",
    "LEVANT" to "بلاد الشام (سوريا/لبنان/الأردن/فلسطين)",
    "SOUTH_ARABIA" to "جنوب الجزيرة العربية (اليمن وعُمان)",
    "NORTH_ARABIA" to "شمال وغرب الجزيرة (الحجاز ونجد والعلا)",
    "HORN_OF_AFRICA" to "القرن الإفريقي (إثيوبيا وإريتريا)",
    "EGYPT_SINAI" to "مصر وسيناء"
  )

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      Surface(
        color = customColors.cardBackground,
        shadowElevation = 3.dp
      ) {
        Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (onBack != null) {
                IconButton(onClick = onBack) {
                  Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                }
              }
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.CloudDone,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "المكتبة المحمّلة دون إنترنت",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "قاعدة بيانات Room المحلية المستقلة • وصول فوري",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              if (totalCount > 0) {
                IconButton(
                  onClick = { showClearAllConfirm = true },
                  modifier = Modifier.testTag("clear_all_offline_cache_btn")
                ) {
                  Icon(
                    Icons.Default.DeleteSweep,
                    contentDescription = "تفريغ الذاكرة المحلية",
                    tint = MaterialTheme.colorScheme.outline
                  )
                }
              }
            }
          }
        }
      }
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 14.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      item { Spacer(modifier = Modifier.height(4.dp)) }

      // Offline Simulation & Status Banner
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (filterState.isOfflineModeSimulated) {
              Color(0xFF2E7D32).copy(alpha = 0.12f)
            } else {
              MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)
            }
          ),
          border = if (filterState.isOfflineModeSimulated) {
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32))
          } else null,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                  imageVector = if (filterState.isOfflineModeSimulated) Icons.Default.WifiOff else Icons.Default.OfflinePin,
                  contentDescription = null,
                  tint = if (filterState.isOfflineModeSimulated) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = if (filterState.isOfflineModeSimulated) "وضع العمل الميداني دون اتصال (نشط)" else "ذاكرة التخزين المحلية (Room Persistence)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = if (filterState.isOfflineModeSimulated) {
                      "يتم استرجاع النصوص والمصادر كاملة من القرص المحلي دون الحاجة للشبكة."
                    } else {
                      "كافة المباحث المفتوحة تُحفظ تلقائياً في SQLite للوصول الدائم دون إنترنت."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.mutedText
                  )
                }
              }

              // Toggle simulation switch
              Switch(
                checked = filterState.isOfflineModeSimulated,
                onCheckedChange = { cacheViewModel.toggleOfflineSimulation() },
                modifier = Modifier.testTag("simulate_offline_toggle")
              )
            }
          }
        }
      }

      // Storage & Stats Badges
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val sizeInKb = (totalStorageBytes / 1024L).coerceAtLeast(1L)
          val sizeDisplay = if (sizeInKb > 1024L) "${sizeInKb / 1024L} MB" else "$sizeInKb KB"

          OfflineStatCard(
            title = "المباحث المحفوظة",
            value = "$totalCount مبحث",
            icon = Icons.Default.MenuBook,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
          )
          OfflineStatCard(
            title = "حجم الذاكرة المستغلة",
            value = sizeDisplay,
            icon = Icons.Default.Storage,
            color = Color(0xFFE65100),
            modifier = Modifier.weight(1f)
          )
          OfflineStatCard(
            title = "جاهزية عدم الاتصال",
            value = if (totalCount > 0) "جاهزة 100%" else "فارغة",
            icon = Icons.Default.CheckCircle,
            color = Color(0xFF2E7D32),
            modifier = Modifier.weight(1f)
          )
        }
      }

      // One-Click Download Entire Encyclopedia Action
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "📥 تنزيل المكتبة الموسوعية بالكامل",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "تحميل وتخزين كافة الفصول (300+ مبحث)، المصادر الإبيغرافية، والمسرد في Room دفعة واحدة.",
                style = MaterialTheme.typography.bodySmall,
                color = customColors.mutedText
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                val sources = BooksSourcesRepository.getSources(context)
                cacheViewModel.preloadEntireLibrary(sources)
              },
              enabled = !isSyncingAll,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("download_all_encyclopedia_btn")
            ) {
              if (isSyncingAll) {
                CircularProgressIndicator(
                  modifier = Modifier.size(16.dp),
                  color = MaterialTheme.colorScheme.onPrimary,
                  strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("جارِ الحفظ...", fontSize = 12.sp)
              } else {
                Icon(Icons.Default.DownloadForOffline, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("تنزيل الكل", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // Search Field & Filters
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = filterState.query,
            onValueChange = { cacheViewModel.setSearchQuery(it) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("offline_library_search_field"),
            placeholder = { Text("ابحث في المباحث والنقوش والمراجع محلياً...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "بحث") },
            trailingIcon = {
              if (filterState.query.isNotEmpty()) {
                IconButton(onClick = { cacheViewModel.setSearchQuery("") }) {
                  Icon(Icons.Default.Clear, contentDescription = "مسح")
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
          )

          // Active filter badges & Reset button
          val hasActiveFilters = filterState.selectedType != "ALL" || 
                                 filterState.selectedScriptType != "ALL" || 
                                 filterState.selectedRegion != "ALL" || 
                                 filterState.query.isNotEmpty()

          if (hasActiveFilters) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "النتائج المطابقة: ${filteredEntries.size} من أصل $totalCount",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
              )
              TextButton(
                onClick = { cacheViewModel.resetAllFilters() },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.height(28.dp)
              ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إعادة ضبط الفلاتر", fontSize = 11.sp)
              }
            }
          }
        }
      }

      // Content Type Filter Chips
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Category, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("نوع المبحث:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
          }
          Spacer(modifier = Modifier.height(4.dp))
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(typeFilters) { (typeKey, labelAr) ->
              val isSelected = filterState.selectedType == typeKey
              FilterChip(
                selected = isSelected,
                onClick = { cacheViewModel.setSelectedType(typeKey) },
                label = { Text(labelAr, fontSize = 12.sp) },
                leadingIcon = if (isSelected) {
                  { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                } else null
              )
            }
          }
        }
      }

      // Script Type Filter Chips
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Translate, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("نوع الخط والكتابة:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
          }
          Spacer(modifier = Modifier.height(4.dp))
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(scriptFilters) { (scriptKey, labelAr) ->
              val isSelected = filterState.selectedScriptType == scriptKey
              FilterChip(
                selected = isSelected,
                onClick = { cacheViewModel.setSelectedScriptType(scriptKey) },
                label = { Text(labelAr, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                  selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                leadingIcon = if (isSelected) {
                  { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                } else null
              )
            }
          }
        }
      }

      // Geographical Region Filter Chips
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Public, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("الإقليم الجغرافي:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
          }
          Spacer(modifier = Modifier.height(4.dp))
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(regionFilters) { (regionKey, labelAr) ->
              val isSelected = filterState.selectedRegion == regionKey
              FilterChip(
                selected = isSelected,
                onClick = { cacheViewModel.setSelectedRegion(regionKey) },
                label = { Text(labelAr, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                  selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                leadingIcon = if (isSelected) {
                  { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                } else null
              )
            }
          }
        }
      }

      // List of Cached Entries
      if (filteredEntries.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 36.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.FolderOff,
                contentDescription = null,
                tint = customColors.mutedText,
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = if (filterState.query.isNotEmpty()) "لا توجد نتائج مطابقة لبحثك في المكتبة المحلية" else "لا توجد مباحث موسوعية محمّلة حالياً",
                style = MaterialTheme.typography.bodyMedium,
                color = customColors.mutedText
              )
              Spacer(modifier = Modifier.height(12.dp))
              FilledTonalButton(
                onClick = {
                  val sources = BooksSourcesRepository.getSources(context)
                  cacheViewModel.preloadEntireLibrary(sources)
                }
              ) {
                Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("تنزيل المحتوى الموسوعي الآن")
              }
            }
          }
        }
      } else {
        items(filteredEntries, key = { it.id }) { entry ->
          CachedEntryCard(
            entry = entry,
            onReadClick = { selectedEntryForReading = entry },
            onDeleteClick = { cacheViewModel.deleteFromDownloadedLibrary(entry.originalId) },
            onCopyContent = {
              val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText("مبحث موسوعي", "${entry.titleAr}\n\n${entry.fullContentAr}")
              cm.setPrimaryClip(clip)
              Toast.makeText(context, "تم نسخ نص المبحث بنجاح", Toast.LENGTH_SHORT).show()
            }
          )
        }
      }

      item { Spacer(modifier = Modifier.height(24.dp)) }
    }
  }

  // Reading Modal Dialog for Offline Entry
  selectedEntryForReading?.let { entry ->
    Dialog(
      onDismissRequest = {
        audioEngine.stop()
        selectedEntryForReading = null
      },
      properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
      Surface(
        modifier = Modifier
          .fillMaxSize()
          .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
      ) {
        Column(modifier = Modifier.fillMaxSize()) {
          // Top Bar of Dialog
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              IconButton(onClick = {
                audioEngine.stop()
                selectedEntryForReading = null
              }) {
                Icon(Icons.Default.Close, contentDescription = "إغلاق")
              }
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text(
                  text = entry.titleAr,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = "${entry.categoryOrBranch} • وضع القراءة دون اتصال",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              // Audio reading
              IconButton(onClick = {
                audioEngine.speak(entry.fullContentAr)
                Toast.makeText(context, "بدء القراءة الصوتية للمبحث...", Toast.LENGTH_SHORT).show()
              }) {
                Icon(Icons.Default.VolumeUp, contentDescription = "قراءة صوتية")
              }

              // Copy
              IconButton(onClick = {
                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("مبحث موسوعي", "${entry.titleAr}\n\n${entry.fullContentAr}")
                cm.setPrimaryClip(clip)
                Toast.makeText(context, "تم نسخ النص كاملاً", Toast.LENGTH_SHORT).show()
              }) {
                Icon(Icons.Default.ContentCopy, contentDescription = "نسخ")
              }
            }
          }

          // Content body scrollable
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = entry.titleEn,
                  style = MaterialTheme.typography.bodyMedium,
                  color = customColors.mutedText
                )
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFF2E7D32).copy(alpha = 0.15f)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("محفوظ محلياً في Room", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                  }
                }
              }
            }

            if (entry.summaryAr.isNotBlank()) {
              item {
                Card(
                  shape = RoundedCornerShape(12.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                      text = "الملخص الأكاديمي:",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = entry.summaryAr,
                      style = MaterialTheme.typography.bodyMedium,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }

            if (entry.transliterationSnippet.isNotBlank()) {
              item {
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                  border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                      text = "شاهد نقحري / نص مسند:",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = entry.transliterationSnippet,
                      fontFamily = FontFamily.Monospace,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Medium,
                      color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                  }
                }
              }
            }

            // Full text content
            item {
              Text(
                text = entry.fullContentAr,
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 26.sp),
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            if (entry.sourceOrCitation.isNotBlank()) {
              item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                  text = "المصادر والتوثيق المعتمد:",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
                Text(
                  text = entry.sourceOrCitation,
                  style = MaterialTheme.typography.bodySmall,
                  color = customColors.mutedText
                )
              }
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
          }
        }
      }
    }
  }

  // Clear all confirmation dialog
  if (showClearAllConfirm) {
    AlertDialog(
      onDismissRequest = { showClearAllConfirm = false },
      icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
      title = { Text("تفريغ المكتبة المحلية المحمّلة؟") },
      text = { Text("هل تود حذف جميع المباحث والمصادر المحفوظة في قاعدة بيانات Room المحلية؟ يمكنك إعادة تنزيلها لاحقاً بضغطة واحدة.") },
      confirmButton = {
        Button(
          onClick = {
            cacheViewModel.clearEntireLibrary()
            showClearAllConfirm = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("تفريغ الذاكرة")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showClearAllConfirm = false }) {
          Text("إلغاء")
        }
      }
    )
  }
}

@Composable
private fun OfflineStatCard(
  title: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.outline,
        textAlign = TextAlign.Center,
        fontSize = 10.sp
      )
    }
  }
}

@Composable
private fun CachedEntryCard(
  entry: CachedEncyclopediaEntryEntity,
  onReadClick: () -> Unit,
  onDeleteClick: () -> Unit,
  onCopyContent: () -> Unit
) {
  val customColors = LocalCustomColors.current

  val (typeLabel, typeColor) = when (entry.entryType) {
    "CHAPTER" -> "فصل موسوعي" to Color(0xFF1976D2)
    "BOOK_SOURCE" -> "كتاب / مصدر PDF" to Color(0xFFD48B38)
    "INSCRIPTION" -> "نقش ومسلة" to Color(0xFF7B1FA2)
    "GLOSSARY" -> "مسرد إبيغرافي" to Color(0xFF00796B)
    else -> "مبحث" to MaterialTheme.colorScheme.primary
  }

  val dateStr = remember(entry.cachedAt) {
    val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    sdf.format(Date(entry.cachedAt))
  }

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Badges row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = typeColor.copy(alpha = 0.15f)
          ) {
            Text(
              text = typeLabel,
              color = typeColor,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFF2E7D32).copy(alpha = 0.12f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text("متوفر دون إنترنت", color = Color(0xFF2E7D32), fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
          }
        }

        Text(
          text = dateStr,
          style = MaterialTheme.typography.labelSmall,
          color = customColors.mutedText
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Title
      Text(
        text = entry.titleAr,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      if (entry.titleEn.isNotBlank() && entry.titleEn != entry.titleAr) {
        Text(
          text = entry.titleEn,
          style = MaterialTheme.typography.bodySmall,
          color = customColors.mutedText,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Summary
      Text(
        text = entry.summaryAr,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Bottom bar with actions
      if (entry.languageName.isNotBlank() || entry.authorOrRuler.isNotBlank()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (entry.languageName.isNotBlank()) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
            ) {
              Text(
                text = "اللغة: ${entry.languageName}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                fontSize = 10.sp
              )
            }
          }
          if (entry.authorOrRuler.isNotBlank()) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
            ) {
              Text(
                text = entry.authorOrRuler.take(28),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                fontSize = 10.sp
              )
            }
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "الفئة: ${entry.categoryOrBranch.take(24)}",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          maxLines = 1
        )

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          IconButton(onClick = onCopyContent, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", modifier = Modifier.size(16.dp))
          }
          IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.DeleteOutline, contentDescription = "حذف من المحلي", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
          }
          Button(
            onClick = onReadClick,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("قراءة دون اتصال", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
