package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.outlined.StarBorder
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
import com.example.data.local.entity.InscriptionEntity
import com.example.data.models.AcademicInscription
import com.example.ui.components.MultiSpectralViewer
import com.example.ui.theme.LocalCustomColors
import com.example.ui.viewmodel.InscriptionSearchViewModel
import com.example.util.AudioEngine
import com.example.util.CitationGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InscriptionSearchScreen(
  modifier: Modifier = Modifier,
  viewModel: InscriptionSearchViewModel = viewModel()
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  val audioEngine = remember { AudioEngine(context) }

  val filterState by viewModel.filterState.collectAsStateWithLifecycle()
  val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
  val totalCount by viewModel.totalDatabaseCount.collectAsStateWithLifecycle()
  val distinctPeriods by viewModel.distinctPeriods.collectAsStateWithLifecycle()
  val distinctRegions by viewModel.distinctRegions.collectAsStateWithLifecycle()

  var expandedItemId by remember { mutableStateOf<String?>(null) }
  var selectedSpectralInscription by remember { mutableStateOf<AcademicInscription?>(null) }
  var citationInscription by remember { mutableStateOf<AcademicInscription?>(null) }
  var showAddDialog by remember { mutableStateOf(false) }

  val predefinedPeriods = remember(distinctPeriods) {
    listOf("الكل") + (distinctPeriods.takeIf { it.isNotEmpty() } ?: listOf(
      "Akkadian Period", "Old Babylonian", "Neo-Assyrian", "Bronze Age", "Iron Age II", "Persian / Achaemenid", "Hellenistic / Nabataean", "Sabaean Kingdom", "Axumite Period"
    ))
  }

  val predefinedRegions = remember(distinctRegions) {
    listOf("الكل") + (distinctRegions.takeIf { it.isNotEmpty() } ?: listOf(
      "Mesopotamia (Iraq)", "Levant (Syria/Lebanon)", "South Arabia (Yemen)", "Horn of Africa (Ethiopia)", "North Arabia (Hijaz)", "Carthage (Tunisia)"
    ))
  }

  val families = listOf("الكل", "السامية الشرقية", "السامية الشمالية الغربية", "السامية الجنوبية القديمة", "السامية الإثيوبية")

  DisposableEffect(Unit) {
    onDispose { audioEngine.release() }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showAddDialog = true },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Add, contentDescription = "إضافة نقش")
          Spacer(modifier = Modifier.width(8.dp))
          Text("إضافة نقش جديد (Room)", fontWeight = FontWeight.Bold)
        }
      }
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(8.dp))
        // Header Banner
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.Storage,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  "محرك بحث النقوش (Room Database)",
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }

              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
              ) {
                Text(
                  "$totalCount نقش مسجل",
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
              "ابحث واستعلم في قاعدة البيانات المحلية للنقوش السامية بحسب اسم النقش، العصر الزمني، أو الإقليم الجغرافي مع التحديث اللحظي عبر Room Flow.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 18.sp
            )
          }
        }
      }

      // Search Inputs Section
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // 1. Search by Name / Keywords
            OutlinedTextField(
              value = filterState.nameQuery,
              onValueChange = { viewModel.updateNameQuery(it) },
              modifier = Modifier.fillMaxWidth(),
              label = { Text("بحث بالاسم أو الحاكم أو النص (Name / Ruler)") },
              placeholder = { Text("مثال: حمورابي، ميشع، سد مأرب، عيزانا، أحيرام...") },
              leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              },
              trailingIcon = {
                if (filterState.nameQuery.isNotBlank()) {
                  IconButton(onClick = { viewModel.updateNameQuery("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "مسح")
                  }
                }
              },
              singleLine = true,
              shape = RoundedCornerShape(12.dp)
            )

            // 2. Period Filter Chips
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  "تصفية حسب الحقبة والعصر (Period):",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.primary
                )
                if (filterState.selectedPeriod != "الكل") {
                  Text(
                    "إلغاء التحديد",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.clickable { viewModel.updateSelectedPeriod("الكل") }
                  )
                }
              }

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                predefinedPeriods.take(10).forEach { period ->
                  val isSelected = filterState.selectedPeriod == period
                  FilterChip(
                    selected = isSelected,
                    onClick = {
                      viewModel.updateSelectedPeriod(if (isSelected) "الكل" else period)
                    },
                    label = {
                      Text(
                        period,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                      )
                    },
                    leadingIcon = if (isSelected) {
                      { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null
                  )
                }
              }
            }

            // 3. Region Filter Chips
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  "تصفية حسب الموقع والإقليم (Region / Site):",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.primary
                )
                if (filterState.selectedRegion != "الكل") {
                  Text(
                    "إلغاء التحديد",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.clickable { viewModel.updateSelectedRegion("الكل") }
                  )
                }
              }

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                predefinedRegions.take(8).forEach { region ->
                  val isSelected = filterState.selectedRegion == region
                  FilterChip(
                    selected = isSelected,
                    onClick = {
                      viewModel.updateSelectedRegion(if (isSelected) "الكل" else region)
                    },
                    label = {
                      Text(
                        region,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                      )
                    },
                    leadingIcon = if (isSelected) {
                      { Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null
                  )
                }
              }
            }

            // 4. Family & Favorite Toggles
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                modifier = Modifier
                  .weight(1f)
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                families.forEach { family ->
                  val isSelected = filterState.selectedFamily == family
                  FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.updateSelectedFamily(if (isSelected) "الكل" else family) },
                    label = { Text(family, fontSize = 11.sp) }
                  )
                }
              }

              IconButton(
                onClick = { viewModel.toggleFavoritesOnly() },
                modifier = Modifier
                  .background(
                    if (filterState.favoritesOnly) customColors.gold.copy(alpha = 0.2f) else Color.Transparent,
                    CircleShape
                  )
              ) {
                Icon(
                  if (filterState.favoritesOnly) Icons.Default.Star else Icons.Outlined.StarBorder,
                  contentDescription = "المفضلة",
                  tint = if (filterState.favoritesOnly) customColors.gold else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }

      // Stats Bar & Quick Reset
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            "نتائج البحث: ${searchResults.size} نقش مطابق (من أصل $totalCount)",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          if (filterState.nameQuery.isNotBlank() || filterState.selectedPeriod != "الكل" || filterState.selectedRegion != "الكل" || filterState.selectedFamily != "الكل" || filterState.favoritesOnly) {
            TextButton(onClick = { viewModel.resetFilters() }) {
              Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("إعادة ضبط الفلاتر", fontSize = 12.sp)
            }
          }
        }
      }

      // Empty State or Results List
      if (searchResults.isEmpty()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Icon(
                Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(54.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                "لم يتم العثور على نقوش مطابقة للبحث",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                "جرب تعديل اسم النقش أو اختيار عصر/إقليم مختلف من فلاتر البحث أعلاه.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(16.dp))
              Button(
                onClick = { viewModel.resetFilters() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
              ) {
                Text("عرض جميع النقوش المسجلة")
              }
            }
          }
        }
      } else {
        items(searchResults, key = { it.id }) { entity ->
          val isExpanded = expandedItemId == entity.id
          val academicItem = remember(entity) { entity.toAcademicInscription() }

          InscriptionResultCard(
            inscription = entity,
            isExpanded = isExpanded,
            onToggleExpand = {
              expandedItemId = if (isExpanded) null else entity.id
            },
            onToggleFavorite = {
              viewModel.toggleFavorite(entity)
            },
            onPlayAudio = {
              audioEngine.playProtoSemiticChime()
            },
            onOpenSpectral = {
              selectedSpectralInscription = academicItem
            },
            onOpenCitation = {
              citationInscription = academicItem
            },
            onCopyText = {
              val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val text = "${entity.titleAr}\n${entity.originalScriptText}\n${entity.transliteration}\n${entity.translationAr}"
              cm.setPrimaryClip(ClipData.newPlainText("Inscription", text))
              Toast.makeText(context, "تم نسخ بيانات النقش بنجاح", Toast.LENGTH_SHORT).show()
            }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(80.dp))
      }
    }
  }

  // MultiSpectral Viewer Modal
  selectedSpectralInscription?.let { item ->
    AlertDialog(
      onDismissRequest = { selectedSpectralInscription = null },
      title = {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(item.titleAr, fontSize = 16.sp, fontWeight = FontWeight.Bold)
          IconButton(onClick = { selectedSpectralInscription = null }) {
            Icon(Icons.Default.Close, contentDescription = "إغلاق")
          }
        }
      },
      text = {
        MultiSpectralViewer(
          imageUrl = item.imageUrl,
          titleAr = item.titleAr,
          titleEn = item.title
        )
      },
      confirmButton = {
        TextButton(onClick = { selectedSpectralInscription = null }) {
          Text("إغلاق")
        }
      }
    )
  }

  // Citation Generator Modal
  citationInscription?.let { item ->
    CitationModal(
      inscription = item,
      onDismiss = { citationInscription = null }
    )
  }

  // Add Custom Inscription Dialog
  if (showAddDialog) {
    AddInscriptionDialog(
      onDismiss = { showAddDialog = false },
      onSave = { newEntity ->
        viewModel.insertCustomInscription(newEntity)
        showAddDialog = false
        Toast.makeText(context, "تم حفظ النقش الجديد في قاعدة البيانات بنجاح!", Toast.LENGTH_SHORT).show()
      }
    )
  }
}

@Composable
fun InscriptionResultCard(
  inscription: InscriptionEntity,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  onToggleFavorite: () -> Unit,
  onPlayAudio: () -> Unit,
  onOpenSpectral: () -> Unit,
  onOpenCitation: () -> Unit,
  onCopyText: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .border(
        width = 1.dp,
        color = if (inscription.isFavorite) customColors.gold.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant,
        shape = RoundedCornerShape(16.dp)
      ),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Top row: ID, Badges, Favorite
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
          ) {
            Text(
              inscription.id,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = customColors.gold.copy(alpha = 0.15f)
          ) {
            Text(
              inscription.language,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = customColors.gold
            )
          }
        }

        IconButton(
          onClick = onToggleFavorite,
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            if (inscription.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
            contentDescription = "تفضيل",
            tint = if (inscription.isFavorite) customColors.gold else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Arabic Title & English Title
      Text(
        inscription.titleAr,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        inscription.title,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Period & Region Badges
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(4.dp))
          Text(inscription.period, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(14.dp), tint = customColors.gold)
          Spacer(modifier = Modifier.width(4.dp))
          Text(inscription.region, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
        }
      }

      // Ancient Script Text Display
      if (inscription.originalScriptText.isNotBlank()) {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              inscription.originalScriptText,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              letterSpacing = 2.sp
            )

            IconButton(
              onClick = onPlayAudio,
              modifier = Modifier.size(32.dp)
            ) {
              Icon(Icons.Default.VolumeUp, contentDescription = "استماع", tint = MaterialTheme.colorScheme.primary)
            }
          }
        }
      }

      // Transliteration & Translation preview
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        inscription.transliteration,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.primary,
        lineHeight = 16.sp
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        inscription.translationAr,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 18.sp,
        maxLines = if (isExpanded) Int.MAX_VALUE else 2
      )

      // Expanded Details
      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier.padding(top = 12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          HorizontalDivider()

          // Ruler & Site
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
              Text("الحاكم / الملك:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(inscription.ruler.ifBlank { "غير محدد" }, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Column(modifier = Modifier.weight(1f)) {
              Text("الموقع الأثري (Site):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(inscription.site.ifBlank { "غير محدد" }, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
          }

          // Script & Material
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
              Text("الخط الأبجدي (Script):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(inscription.script, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Column(modifier = Modifier.weight(1f)) {
              Text("الخامة (Material):", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(inscription.material, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
          }

          // English Translation
          if (inscription.translationEn.isNotBlank()) {
            Column {
              Text("English Translation:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(inscription.translationEn, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
            }
          }

          // Philological Notes
          if (inscription.notesAr.isNotBlank()) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text("الملاحظات الفيلولوجية والنحوية:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(inscription.notesAr, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 16.sp)
              }
            }
          }

          // Corpus / Reference
          if (inscription.corpus.isNotBlank()) {
            Text("المرجع الإبيغرافي: ${inscription.corpus}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }

          // Action Toolbar
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Button(
              onClick = onOpenSpectral,
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
            ) {
              Icon(Icons.Default.FilterFrames, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("الفحص الطيفي", fontSize = 11.sp)
            }

            OutlinedButton(
              onClick = onOpenCitation,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(8.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
            ) {
              Icon(Icons.Default.FormatQuote, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("اقتباس (Citation)", fontSize = 11.sp)
            }

            IconButton(
              onClick = onCopyText,
              modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            ) {
              Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", modifier = Modifier.size(18.dp))
            }
          }
        }
      }

      // Expand/Collapse Toggle Button
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onToggleExpand() }
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          if (isExpanded) "إخفاء التفاصيل" else "عرض التفاصيل والتحليل الفيلولوجي",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.primary
        )
        Icon(
          if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

@Composable
fun CitationModal(
  inscription: AcademicInscription,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val styles = listOf("APA", "MLA", "CHICAGO", "BIBTEX")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.FormatQuote, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text("توثيق واقتباس النقش الأكاديمي", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(inscription.titleAr, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

        HorizontalDivider()

        styles.forEach { style ->
          val citationText = CitationGenerator.generateInscriptionCitation(inscription, style)
          Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(style, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                IconButton(
                  onClick = {
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText(style, citationText))
                    Toast.makeText(context, "تم نسخ اقتباس $style", Toast.LENGTH_SHORT).show()
                  },
                  modifier = Modifier.size(24.dp)
                ) {
                  Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", modifier = Modifier.size(14.dp))
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(citationText, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text("إغلاق")
      }
    }
  )
}

@Composable
fun AddInscriptionDialog(
  onDismiss: () -> Unit,
  onSave: (InscriptionEntity) -> Unit
) {
  var id by remember { mutableStateOf("USER-INS-${System.currentTimeMillis() % 10000}") }
  var titleAr by remember { mutableStateOf("") }
  var titleEn by remember { mutableStateOf("") }
  var language by remember { mutableStateOf("Akkadian") }
  var family by remember { mutableStateOf("السامية الشرقية") }
  var script by remember { mutableStateOf("مسماري") }
  var period by remember { mutableStateOf("Old Babylonian, c. 1750 BCE") }
  var region by remember { mutableStateOf("Mesopotamia (Iraq)") }
  var site by remember { mutableStateOf("بابل") }
  var originalScriptText by remember { mutableStateOf("") }
  var transliteration by remember { mutableStateOf("") }
  var translationAr by remember { mutableStateOf("") }
  var ruler by remember { mutableStateOf("") }
  var notesAr by remember { mutableStateOf("") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.PostAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text("إضافة نقش جديد (Room Database)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 450.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          OutlinedTextField(
            value = titleAr,
            onValueChange = { titleAr = it },
            label = { Text("عنوان النقش بالعربية *") },
            modifier = Modifier.fillMaxWidth()
          )
        }
        item {
          OutlinedTextField(
            value = titleEn,
            onValueChange = { titleEn = it },
            label = { Text("Title (English)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
        item {
          OutlinedTextField(
            value = ruler,
            onValueChange = { ruler = it },
            label = { Text("الحاكم / الملك (Ruler)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
        item {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
              value = period,
              onValueChange = { period = it },
              label = { Text("الحقبة الزمنية (Period)") },
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = region,
              onValueChange = { region = it },
              label = { Text("الإقليم (Region)") },
              modifier = Modifier.weight(1f)
            )
          }
        }
        item {
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
              value = language,
              onValueChange = { language = it },
              label = { Text("اللغة") },
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = script,
              onValueChange = { script = it },
              label = { Text("الخط الأبجدي") },
              modifier = Modifier.weight(1f)
            )
          }
        }
        item {
          OutlinedTextField(
            value = originalScriptText,
            onValueChange = { originalScriptText = it },
            label = { Text("نص النقش بالخط القديم (Original Glyphs)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
        item {
          OutlinedTextField(
            value = transliteration,
            onValueChange = { transliteration = it },
            label = { Text("النقحرة الصوتية (Transliteration)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
        item {
          OutlinedTextField(
            value = translationAr,
            onValueChange = { translationAr = it },
            label = { Text("الترجمة العربية الشارحة *") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
          )
        }
        item {
          OutlinedTextField(
            value = notesAr,
            onValueChange = { notesAr = it },
            label = { Text("ملاحظات فيلولوجية") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (titleAr.isNotBlank()) {
            val entity = InscriptionEntity(
              id = id,
              title = titleEn.ifBlank { titleAr },
              titleAr = titleAr,
              language = language,
              family = family,
              script = script,
              textType = "User Custom Inscription",
              site = site,
              region = region,
              period = period,
              material = "Stone / Clay",
              status = "Field Registration",
              ruler = ruler,
              corpus = "User Local Corpus",
              transliteration = transliteration,
              originalScriptText = originalScriptText,
              translationAr = translationAr,
              translationEn = "",
              notesAr = notesAr,
              notesEn = "",
              imageUrl = "https://images.unsplash.com/photo-1599739291060-4578e77dac5d?auto=format&fit=crop&w=800&q=80",
              sourceUrl = "",
              tags = "User,Custom,RoomDB",
              isFavorite = true
            )
            onSave(entity)
          }
        },
        enabled = titleAr.isNotBlank()
      ) {
        Text("حفظ في Room DB")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("إلغاء")
      }
    }
  )
}
