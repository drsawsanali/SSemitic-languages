package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.AudioEngine
import com.example.util.OcrDownloadManager
import com.example.util.OcrLanguagePack
import com.example.util.PackDownloadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrOfflinePacksSheet(
  onDismiss: () -> Unit,
  audioEngine: AudioEngine? = null
) {
  val context = LocalContext.current
  val downloadManager = remember { OcrDownloadManager.getInstance(context) }
  val packs by downloadManager.packsState.collectAsState()
  val isOfflineModeForced by downloadManager.isOfflineModeForced.collectAsState()

  var selectedFilter by remember { mutableStateOf("الكل") } // "الكل", "المثبتة", "المتاحة"
  var expandedPackId by remember { mutableStateOf<String?>(null) }

  val installedCount = packs.count { it.downloadState == PackDownloadState.INSTALLED }
  val totalStorageUsedMb = packs.filter { it.downloadState == PackDownloadState.INSTALLED }.sumOf { it.sizeMb }
  val totalAvailableStorageMb = packs.sumOf { it.sizeMb }
  val storagePercentage = (totalStorageUsedMb / totalAvailableStorageMb).toFloat().coerceIn(0f, 1f)

  val filteredPacks = when (selectedFilter) {
    "المثبتة" -> packs.filter { it.downloadState == PackDownloadState.INSTALLED }
    "المتاحة" -> packs.filter { it.downloadState != PackDownloadState.INSTALLED }
    else -> packs
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 28.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            modifier = Modifier.size(44.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Default.CloudDownload,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              "مدير الحزم اللغوية الميدانية (Offline OCR)",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              "حمّل نماذج الذكاء الاصطناعي والقواميس للعمل الميداني دون إنترنت",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "إغلاق")
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Status and Storage Summary Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        )
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Force Offline Mode Switch
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                if (isOfflineModeForced) Icons.Default.WifiOff else Icons.Default.Wifi,
                contentDescription = null,
                tint = if (isOfflineModeForced) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  "وضع العمل الميداني الكامل (Offline Field Mode)",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  if (isOfflineModeForced) "مُفعل: المعالجة تعتمد كلياً على النماذج المحلية في الجهاز"
                  else "تلقائي: استخدام النماذج المحلية مع إمكانية التحسين عبر الشبكة",
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Switch(
              checked = isOfflineModeForced,
              onCheckedChange = { forced ->
                downloadManager.toggleForceOfflineMode(forced)
                audioEngine?.playProtoSemiticChime()
                Toast.makeText(
                  context,
                  if (forced) "تم تفعيل وضع العمل الميداني الكامل دون إنترنت"
                  else "تم تفعيل الوضع التلقائي المتصل",
                  Toast.LENGTH_SHORT
                ).show()
              }
            )
          }

          Spacer(modifier = Modifier.height(10.dp))
          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
          Spacer(modifier = Modifier.height(10.dp))

          // Storage usage indicator
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              "المساحة المشغولة محلياً:",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              "%.1f ميجابايت من %.1f ميجابايت (%d/%d حزم)".format(
                totalStorageUsedMb,
                totalAvailableStorageMb,
                installedCount,
                packs.size
              ),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }

          Spacer(modifier = Modifier.height(6.dp))
          LinearProgressIndicator(
            progress = { storagePercentage },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Action buttons: Download All / Free Storage
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FilledTonalButton(
              onClick = {
                downloadManager.downloadAllPacks()
                audioEngine?.playProtoSemiticChime()
                Toast.makeText(context, "بدء تحميل وتثبيت جميع الحزم المحلية...", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                contentColor = MaterialTheme.colorScheme.primary
              )
            ) {
              Icon(Icons.Default.DownloadForOffline, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("تحميل جميع الحزم", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
              onClick = {
                downloadManager.deleteAllPacks()
                audioEngine?.playProtoSemiticChime()
                Toast.makeText(context, "تم إخلاء المساحة وحذف الحزم المحملة", Toast.LENGTH_SHORT).show()
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
              )
            ) {
              Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("إخلاء المساحة", fontSize = 11.sp)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Filter Chips Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf("الكل", "المثبتة", "المتاحة").forEach { filterName ->
          val count = when (filterName) {
            "المثبتة" -> installedCount
            "المتاحة" -> packs.size - installedCount
            else -> packs.size
          }
          val isSelected = selectedFilter == filterName
          FilterChip(
            selected = isSelected,
            onClick = { selectedFilter = filterName },
            label = { Text("$filterName ($count)", fontSize = 11.sp) },
            shape = RoundedCornerShape(8.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Packs List
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f, fill = false)
          .heightIn(max = 420.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(filteredPacks, key = { it.id }) { pack ->
          LanguagePackCard(
            pack = pack,
            isExpanded = expandedPackId == pack.id,
            onToggleExpand = {
              expandedPackId = if (expandedPackId == pack.id) null else pack.id
            },
            onDownload = {
              downloadManager.downloadPack(pack.id)
              audioEngine?.playProtoSemiticChime()
            },
            onCancel = {
              downloadManager.cancelDownload(pack.id)
            },
            onDelete = {
              downloadManager.deletePack(pack.id)
              Toast.makeText(context, "تم حذف حزمة: ${pack.nameAr}", Toast.LENGTH_SHORT).show()
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Archaeological Field Note
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFD4AF37).copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD4AF37).copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            Icons.Default.VerifiedUser,
            contentDescription = null,
            tint = Color(0xFFD4AF37),
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            "كافة الحزم المذكورة تعمل محلياً بنسبة 100%، وتتيح فك وتصنيف شواهد النقوش والمخطوطات في الكهوف والمواقع الصحراوية النائية دون حاجة للاتصال بالإنترنت.",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 15.sp
          )
        }
      }
    }
  }
}

@Composable
private fun LanguagePackCard(
  pack: OcrLanguagePack,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  onDownload: () -> Unit,
  onCancel: () -> Unit,
  onDelete: () -> Unit
) {
  val isInstalled = pack.downloadState == PackDownloadState.INSTALLED
  val isDownloading = pack.downloadState == PackDownloadState.DOWNLOADING

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isInstalled) {
        MaterialTheme.colorScheme.surface
      } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
      }
    ),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (isInstalled) Color(0xFF2E7D32).copy(alpha = 0.35f)
      else MaterialTheme.colorScheme.outlineVariant
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isInstalled) 2.dp else 0.dp)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      // Top row: Title, badges, size
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = pack.nameAr,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${pack.nameEn} • ${pack.scriptFamilyAr}",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Size and Version
        Column(horizontalAlignment = Alignment.End) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
          ) {
            Text(
              "${pack.sizeMb} MB",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            pack.version,
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Sample Glyphs Strip
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        pack.sampleGlyphs.forEach { glyph ->
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(28.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                glyph,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        }
      }

      // Download Progress Indicator if Downloading
      if (isDownloading) {
        Spacer(modifier = Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              "جارِ التحميل وفك النماذج: ${(pack.downloadProgress * 100).toInt()}%",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              pack.downloadSpeed,
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          LinearProgressIndicator(
            progress = { pack.downloadProgress },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action and Details Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Expand details button
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onToggleExpand() }
            .padding(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            if (isExpanded) "إخفاء التفاصيل" else "تفاصيل الحزمة والوحدات",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
          )
          Icon(
            if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
          )
        }

        // Action Buttons based on state
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          when {
            isDownloading -> {
              OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(30.dp)
              ) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إلغاء", fontSize = 10.sp)
              }
            }

            isInstalled -> {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF2E7D32).copy(alpha = 0.15f)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    "مثبت محلياً ✓",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                  )
                }
              }

              IconButton(
                onClick = onDelete,
                modifier = Modifier.size(30.dp)
              ) {
                Icon(
                  Icons.Default.DeleteOutline,
                  contentDescription = "حذف الحزمة",
                  tint = MaterialTheme.colorScheme.error,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            else -> {
              Button(
                onClick = onDownload,
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
              ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("تحميل (${pack.sizeMb} MB)", fontSize = 10.sp)
              }
            }
          }
        }
      }

      // Expandable Details Section
      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .background(
              MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
              RoundedCornerShape(8.dp)
            )
            .padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = pack.descriptionAr,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 16.sp
          )

          HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

          Text(
            text = "المكونات والنماذج المضمنة داخل الحزمة:",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )

          pack.modules.forEach { mod ->
            Row(verticalAlignment = Alignment.Top) {
              Text("• ", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
              Text(
                mod,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 14.sp
              )
            }
          }

          if (pack.installedDate != null) {
            Text(
              "تاريخ التثبيت المحلي: ${pack.installedDate}",
              fontSize = 9.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }
  }
}
