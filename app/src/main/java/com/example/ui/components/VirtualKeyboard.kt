package com.example.ui.components

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.LocalCustomColors

data class SemiticKey(
  val char: String,
  val transliteration: String,
  val nameAr: String,
  val ipa: String,
  val arabicEquiv: String,
  val numericValue: Int = 0,
  val originalPictogram: String = "",
  val phoneticDescription: String = ""
)

enum class ScriptFamily(val titleAr: String, val icon: String) {
  NORTHWEST("الشمالية الغربية", "𐤀"),
  SOUTH_ARABIAN("العربية والمسند", "𐩱"),
  ARAMAIC_SYRIAC("الآرامية والسريانية", "𐡀"),
  EAST_SEMITIC("الشرقية المسمارية", "𒀭"),
  ETHIOSEMITIC("الإثيوبية (الجعزية)", "ሀ"),
  ACADEMIC("النقحرة والصوتيات", "IPA")
}

enum class SemiticScriptLayout(
  val titleAr: String,
  val titleEn: String,
  val family: ScriptFamily,
  val sampleGlyph: String,
  val description: String
) {
  PHOENICIAN("الفينيقية والكنعانية", "Phoenician / Canaanite", ScriptFamily.NORTHWEST, "𐤀", "أبجدية كنعان ولبنان وقرطاج (22 حرفاً + أرقام وفواصل)"),
  UGARITIC("الأوغاريتية المسمارية", "Ugaritic Cuneiform", ScriptFamily.NORTHWEST, "𐎀", "أبجدية رأس الشمرا المسمارية (30 حرفاً صوتياً كاملاً)"),
  MUSNAD("المسند العربي الجنوبي", "South Arabian Musnad", ScriptFamily.SOUTH_ARABIAN, "𐩱", "خط النقوش الصخرية لممالك سبأ ومعين وقتبان وحضرموت (29 حرفاً + أرقام)"),
  SAFAITIC("الصفائية والثمودية", "Safaitic & Thamudic", ScriptFamily.SOUTH_ARABIAN, "𐪀", "خطوط البادية العربية الشمالية القديمة ونقوش الحَرّات والرمول"),
  ARAMAIC("الآرامية الإمبراطورية", "Imperial Aramaic", ScriptFamily.ARAMAIC_SYRIAC, "𐡀", "الخط الرسمي للإمبراطوريات القديمة والتجارة الدولية والبرديات"),
  SYRIAC("السريانية (الأسطرنجيلي)", "Syriac Estrangela", ScriptFamily.ARAMAIC_SYRIAC, "ܐ", "خط المخطوطات السريانية الكلاسيكية وعصر الرها الذهبي بالحركات"),
  NABATAEAN("النبطية الصخرية", "Nabataean Script", ScriptFamily.ARAMAIC_SYRIAC, "𐢀", "خط حضارة الأنباط في البتراء ومدائن صالح ونقش النمارة"),
  PALMYRENE("التدمرية", "Palmyrene Script", ScriptFamily.ARAMAIC_SYRIAC, "𐡠", "خط نقوش مملكة تدمر وبطولات زنوبيا ونظام التعرفة الجمركية"),
  MANDAIC("المندائية القديمة", "Mandaic Script", ScriptFamily.ARAMAIC_SYRIAC, "ࡀ", "خط نصوص الصابئة المندائيين وكنزا ربا والأحراز"),
  CUNEIFORM("المسماري الأكادي", "Akkadian Cuneiform", ScriptFamily.EAST_SEMITIC, "𒀭", "الرموز المقطعية واللوغوغرامات في بابل وآشور وإيبلا"),
  GEEZ("الجعزية الإثيوبية", "Ethiopic Fidel", ScriptFamily.ETHIOSEMITIC, "ሀ", "أبجدية الحبشة بمقاطعها الصوتية السبعة ومسلات أكسوم"),
  SAMARITAN("السامرية والعبرية القديمة", "Samaritan & Paleo-Hebrew", ScriptFamily.NORTHWEST, "ࠀ", "الخط السامري المحافظ والكنعاني المتأخر والأختام"),
  TRANSLITERATION("النقحرة والأصوات IPA", "Academic Phonetics / IPA", ScriptFamily.ACADEMIC, "šˤ", "الرموز الأكاديمية الدقيقة والحروف ذات الإعجام الفيلولوجي")
}

data class InscriptionPreset(
  val id: String,
  val titleAr: String,
  val titleEn: String,
  val scriptLayout: SemiticScriptLayout,
  val branchAr: String,
  val dateEstimate: String,
  val location: String,
  val text: String,
  val transliteration: String,
  val translationAr: String,
  val philologicalNotes: String = ""
)

@Composable
fun SemiticVirtualKeyboard(
  onKeyClick: (SemiticKey) -> Unit,
  onBackspace: () -> Unit,
  onClear: () -> Unit,
  onPresetInsert: (String) -> Unit,
  onPlayChime: (f1: Int, f2: Int) -> Unit = { _, _ -> },
  currentInputText: String = "",
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  var selectedFamily by remember { mutableStateOf(ScriptFamily.NORTHWEST) }
  var currentScript by remember { mutableStateOf(SemiticScriptLayout.PHOENICIAN) }
  var selectedKeyDetail by remember { mutableStateOf<SemiticKey?>(null) }
  var isExpandedMode by remember { mutableStateOf(false) }
  var geezVowelOrder by remember { mutableStateOf(1) } // 1 to 7
  var showAllPresetsModal by remember { mutableStateOf(false) }

  val scriptsInFamily = remember(selectedFamily) {
    SemiticScriptLayout.values().filter { it.family == selectedFamily }
  }

  // Ensure currentScript matches selected family
  LaunchedEffect(selectedFamily) {
    if (currentScript.family != selectedFamily) {
      currentScript = scriptsInFamily.firstOrNull() ?: SemiticScriptLayout.PHOENICIAN
    }
  }

  val keys = remember(currentScript, geezVowelOrder) {
    getKeysForScript(currentScript, geezVowelOrder)
  }

  val allPresets = remember { getComprehensiveInscriptionPresets() }

  // Calculate total Gematria value if current input text is present
  val gematriaValue = remember(currentInputText) {
    calculateGematria(currentInputText)
  }

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {

      // Header Row with Title, Expansion Toggle & Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Text(currentScript.sampleGlyph, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "لوحة مفاتيح اللغات والنقوش السامية",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${currentScript.titleAr} (${keys.size} رمزاً إبيغرافياً)",
              fontSize = 10.sp,
              color = customColors.mutedText
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
          // Gematria Badge
          if (gematriaValue > 0) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
            ) {
              Text(
                text = "جُمّل: $gematriaValue",
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiaryContainer
              )
            }
          }

          IconButton(
            onClick = { isExpandedMode = !isExpandedMode },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              if (isExpandedMode) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
              contentDescription = "تبديل الحجم",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
          }

          IconButton(onClick = onBackspace, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Backspace, contentDescription = "حذف", modifier = Modifier.size(18.dp))
          }
          IconButton(onClick = onClear, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.DeleteOutline, contentDescription = "مسح", modifier = Modifier.size(18.dp))
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // 1. Script Family Selector Tabs
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        ScriptFamily.values().forEach { family ->
          val isSelected = selectedFamily == family
          FilterChip(
            selected = isSelected,
            onClick = { selectedFamily = family },
            label = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(family.icon, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(family.titleAr, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
              }
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
              selectedLabelColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(8.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      // 2. Specific Script Layout Sub-tabs
      ScrollableTabRow(
        selectedTabIndex = scriptsInFamily.indexOf(currentScript).coerceAtLeast(0),
        edgePadding = 0.dp,
        containerColor = Color.Transparent,
        divider = {}
      ) {
        scriptsInFamily.forEach { script ->
          val isSelected = currentScript == script
          Tab(
            selected = isSelected,
            onClick = { currentScript = script },
            text = {
              Text(
                text = "${script.sampleGlyph} ${script.titleAr}",
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary else customColors.mutedText
              )
            }
          )
        }
      }

      // 3. Ge'ez Fidel Vowel Order Selector (If Ge'ez selected)
      if (currentScript == SemiticScriptLayout.GEEZ) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("رُتبة الحركات الجعزية (Orders):", fontSize = 10.sp, fontWeight = FontWeight.Bold)
          val orders = listOf(
            1 to "١. جِعِز (ä)",
            2 to "٢. كَعِب (u)",
            3 to "٣. سالِس (i)",
            4 to "٤. رابِع (ā)",
            5 to "٥. خامِس (ē)",
            6 to "٦. سادِس (ə/Ø)",
            7 to "٧. سابِع (o)"
          )
          Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            orders.forEach { (ord, label) ->
              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .clickable { geezVowelOrder = ord },
                color = if (geezVowelOrder == ord) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
              ) {
                Text(
                  text = "$ord",
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (geezVowelOrder == ord) MaterialTheme.colorScheme.onPrimary else customColors.mutedText
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // 4. Inscription Preset Quick Bar with "All Inscriptions (40+)" Button
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Open Modal Button
        Button(
          onClick = { showAllPresetsModal = true },
          shape = RoundedCornerShape(6.dp),
          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("أرشيف كل النقوش (${allPresets.size})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }

        allPresets.take(12).forEach { preset ->
          SuggestionChip(
            onClick = {
              onPresetInsert(preset.text)
              currentScript = preset.scriptLayout
              selectedFamily = preset.scriptLayout.family
            },
            label = { Text(preset.titleAr, fontSize = 10.sp) },
            shape = RoundedCornerShape(6.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // 5. Special Punctuation & Word Divider Quick Strip
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        val quickDividers = listOf(
          SemiticKey(" ", " ", "مسافة", "-", "مسافة"),
          SemiticKey("𐤟", "•", "فاصل فينيقي", "-", "•"),
          SemiticKey("𐩿", "|", "فاصل المسند", "-", "|"),
          SemiticKey("𐎟", "•", "فاصل أوغاريتي", "-", "•"),
          SemiticKey("܁", ".", "نقطة سريانية", "-", "."),
          SemiticKey(":", ":", "نقطتان", "-", ":")
        )
        Text("فواصل ونقوش:", fontSize = 9.sp, color = customColors.mutedText)
        quickDividers.forEach { divKey ->
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .clickable {
                onPlayChime(700, 1500)
                onKeyClick(divKey)
              },
            color = customColors.cardBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, customColors.border),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = if (divKey.char == " ") "مسافة ␣" else divKey.char,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // 6. Interactive Keys Grid
      val gridHeight = if (isExpandedMode) 300.dp else 190.dp
      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 46.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(gridHeight),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        items(keys) { key ->
          Box(
            modifier = Modifier
              .aspectRatio(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(customColors.cardBackground)
              .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
              .clickable {
                selectedKeyDetail = key
                val formantFreq = getFormantForCharacter(key.char)
                onPlayChime(formantFreq.first, formantFreq.second)
                onKeyClick(key)
              },
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Text(
                text = key.char,
                fontSize = if (key.char.length > 2) 13.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
              )
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
              ) {
                Text(
                  text = key.arabicEquiv,
                  fontSize = 9.sp,
                  color = customColors.mutedText,
                  textAlign = TextAlign.Center
                )
                if (key.numericValue > 0) {
                  Text(
                    text = " (${key.numericValue})",
                    fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.secondary
                  )
                }
              }
            }
          }
        }
      }

      // 7. Interactive Phonetic & Epigraphic Inspector Bar
      selectedKeyDetail?.let { key ->
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "${key.char}  [${key.nameAr}]",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "المقابل العربي: ${key.arabicEquiv}",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.primary
                )
                if (key.numericValue > 0) {
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "• الجُمّل: ${key.numericValue}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.tertiary
                  )
                }
              }
              Text(
                text = "IPA: ${key.ipa}  •  النقحرة: (${key.transliteration})${if (key.originalPictogram.isNotEmpty()) "  •  الأصل التصويري: ${key.originalPictogram}" else ""}",
                fontSize = 10.sp,
                color = customColors.mutedText
              )
            }

            IconButton(
              onClick = {
                val formant = getFormantForCharacter(key.char)
                onPlayChime(formant.first, formant.second)
              },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                Icons.Default.VolumeUp,
                contentDescription = "استماع صوتي",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }
  }

  // Comprehensive Inscription Presets Catalog Dialog
  if (showAllPresetsModal) {
    ComprehensiveInscriptionsCatalogModal(
      presets = allPresets,
      onSelectPreset = { preset ->
        onPresetInsert(preset.text)
        currentScript = preset.scriptLayout
        selectedFamily = preset.scriptLayout.family
        showAllPresetsModal = false
      },
      onDismiss = { showAllPresetsModal = false }
    )
  }
}

@Composable
fun ComprehensiveInscriptionsCatalogModal(
  presets: List<InscriptionPreset>,
  onSelectPreset: (InscriptionPreset) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  var searchQuery by remember { mutableStateOf("") }
  var selectedBranchFilter by remember { mutableStateOf("الكل") }

  val branches = listOf(
    "الكل",
    "السامية الشرقية",
    "الكنعانية والفينيقية",
    "الأوغاريتية",
    "الآرامية والسريانية",
    "الأنباط وتدمر",
    "المسند والعربية الجنوبية",
    "العربية والبادية القديمة",
    "السامية الإثيوبية"
  )

  val filteredPresets = remember(searchQuery, selectedBranchFilter) {
    presets.filter { preset ->
      val matchesBranch = selectedBranchFilter == "الكل" || preset.branchAr == selectedBranchFilter
      val matchesQuery = searchQuery.isBlank() ||
        preset.titleAr.contains(searchQuery, ignoreCase = true) ||
        preset.text.contains(searchQuery, ignoreCase = true) ||
        preset.transliteration.contains(searchQuery, ignoreCase = true) ||
        preset.translationAr.contains(searchQuery, ignoreCase = true) ||
        preset.location.contains(searchQuery, ignoreCase = true)
      matchesBranch && matchesQuery
    }
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.92f),
      shape = RoundedCornerShape(20.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              Icons.Default.MenuBook,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "موسوعة النماذج والنقوش السامية الأثرية",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "اختر أي نقش أثري لإدراجه فورياً في لوحة المفاتيح والتحليل الفيلولوجي",
                style = MaterialTheme.typography.bodySmall,
                color = customColors.mutedText
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "إغلاق")
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("ابحث في أسماء النقوش، المواقع، النصوص أو الترجمات...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "مسح")
              }
            }
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Branch Filters Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          branches.forEach { branch ->
            val isSelected = selectedBranchFilter == branch
            FilterChip(
              selected = isSelected,
              onClick = { selectedBranchFilter = branch },
              label = { Text(branch, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
              shape = RoundedCornerShape(8.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "عدد النقوش المطابقة: ${filteredPresets.size}",
          fontSize = 11.sp,
          color = customColors.mutedText,
          fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Inscriptions List
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(filteredPresets, key = { it.id }) { preset ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
              border = androidx.compose.foundation.BorderStroke(1.dp, customColors.border)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                // Inscription Header
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = preset.titleAr,
                      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = "${preset.branchAr} • ${preset.dateEstimate} • ${preset.location}",
                      fontSize = 10.sp,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                  ) {
                    Text(
                      text = preset.scriptLayout.titleAr,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Original Text in Authentic Script
                Surface(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                  Text(
                    text = preset.text,
                    modifier = Modifier.padding(10.dp),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    lineHeight = 22.sp
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Transliteration
                Text(
                  text = "النقحرة الفيلولوجية: ${preset.transliteration}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = customColors.mutedText
                )

                // Arabic Translation
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "الترجمة العربية: ${preset.translationAr}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurface
                )

                if (preset.philologicalNotes.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "ملاحظات: ${preset.philologicalNotes}",
                    fontSize = 10.sp,
                    color = customColors.mutedText
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons Row
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.End,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  OutlinedButton(
                    onClick = {
                      val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                      val clip = ClipData.newPlainText("Semitic Inscription", "${preset.titleAr}\n${preset.text}\n${preset.transliteration}\n${preset.translationAr}")
                      cm.setPrimaryClip(clip)
                      Toast.makeText(context, "تم نسخ التوثيق الكامل للنقش", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                  ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("نسخ", fontSize = 11.sp)
                  }

                  Spacer(modifier = Modifier.width(8.dp))

                  Button(
                    onClick = { onSelectPreset(preset) },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                  ) {
                    Icon(Icons.Default.Input, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إدراج في اللوحة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

// Sound formants for acoustic feedback
fun getFormantForCharacter(char: String): Pair<Int, Int> {
  val hash = char.hashCode()
  val f1 = 400 + (kotlin.math.abs(hash) % 400)
  val f2 = 1200 + (kotlin.math.abs(hash / 3) % 1000)
  return Pair(f1, f2)
}

// Calculate Gematria / Abjad numeric value of text
fun calculateGematria(text: String): Int {
  var sum = 0
  val charValues = mapOf(
    "𐤀" to 1, "𐤁" to 2, "𐤂" to 3, "𐤃" to 4, "𐤄" to 5, "𐤅" to 6, "𐤆" to 7, "𐤇" to 8, "𐤈" to 9,
    "𐤉" to 10, "𐤊" to 20, "𐤋" to 30, "𐤌" to 40, "𐤍" to 50, "𐤎" to 60, "𐤏" to 70, "𐤐" to 80, "𐤑" to 90,
    "𐤒" to 100, "𐤓" to 200, "𐤔" to 300, "𐤕" to 400,
    "𐩱" to 1, "𐩨" to 2, "𐩴" to 3, "𐩵" to 4, "𐩠" to 5, "𐩥" to 6, "𐩸" to 7, "𐩢" to 8, "𐩷" to 9,
    "𐩺" to 10, "𐩫" to 20, "𐩡" to 30, "𐩣" to 40, "𐩬" to 50, "𐩯" to 60, "𐩲" to 70, "𐩰" to 80, "𐩮" to 90,
    "𐩤" to 100, "𐩧" to 200, "𐩪" to 300, "𐩩" to 400, "𐩻" to 500, "𐩭" to 600, "𐩹" to 700, "𐩳" to 800, "𐩼" to 900, "𐩶" to 1000,
    "أ" to 1, "ب" to 2, "ج" to 3, "د" to 4, "ه" to 5, "و" to 6, "ز" to 7, "ح" to 8, "ط" to 9,
    "ي" to 10, "ك" to 20, "ل" to 30, "م" to 40, "ن" to 50, "س" to 60, "ع" to 70, "ف" to 80, "ص" to 90,
    "ق" to 100, "ر" to 200, "ش" to 300, "ت" to 400, "ث" to 500, "خ" to 600, "ذ" to 700, "ض" to 800, "ظ" to 900, "غ" to 1000
  )
  for (c in text) {
    val s = c.toString()
    sum += charValues[s] ?: 0
  }
  return sum
}

fun getComprehensiveInscriptionPresets(): List<InscriptionPreset> {
  return listOf(
    // 1. East Semitic
    InscriptionPreset(
      id = "ch_1",
      titleAr = "شريعة حمورابي (CH I)",
      titleEn = "Code of Hammurabi",
      scriptLayout = SemiticScriptLayout.CUNEIFORM,
      branchAr = "السامية الشرقية",
      dateEstimate = "1750 ق.م",
      location = "بابل (محفوظة بمتحف اللوفر)",
      text = "𒀭 𒈗 𒂗 𒂍 𒆳 𒆠 𒈬 𒍣 𒌉 𒊩 𒀀 𒀊",
      transliteration = "inu Anum ṣīrum lugal Anunnaki Enlil bēl šamê u erṣetim",
      translationAr = "عندما عيَّن الإله آنو السامي والرب إنليل حمورابي ليرعى العدل في البلاد",
      philologicalNotes = "تعتمد الأكادية البابلية القديمة نظام المقاطع واللوغوغرامات السومرية."
    ),
    InscriptionPreset(
      id = "gilgamesh_11",
      titleAr = "ملحمة جلجامش ولوح الطوفان (XI)",
      titleEn = "Epic of Gilgamesh Tablet XI",
      scriptLayout = SemiticScriptLayout.CUNEIFORM,
      branchAr = "السامية الشرقية",
      dateEstimate = "1200 ق.م",
      location = "مكتبة آشوربانيبال - نينوى",
      text = "𒊭 𒈾 𒀝 𒁀 𒄿 𒈬 𒊒 𒅖 𒁲 𒈠 𒀀 𒋾",
      transliteration = "ša naqba īmuru išdi māti",
      translationAr = "هو الذي رأى كل شيء، وأدرك أسرار الكون وأساس البلاد",
      philologicalNotes = "أهم أثر ملحمي في تاريخ الشرق الأدنى القديم."
    ),
    InscriptionPreset(
      id = "ebla_treaty",
      titleAr = "معاهدة إيبلا وأبارسال (TM.75.G.2420)",
      titleEn = "Ebla Treaty with Abarsal",
      scriptLayout = SemiticScriptLayout.CUNEIFORM,
      branchAr = "السامية الشرقية",
      dateEstimate = "2350 ق.م",
      location = "تل مرديخ - إيبلا (سوريا)",
      text = "𒂗 𒂍 𒈠 𒌈 𒅗 𒆷 𒈠",
      transliteration = "en é ma-tum ka-la-ma",
      translationAr = "سيد بيت البلاد قاطبة ومعاهدة التجارة والتحالف",
      philologicalNotes = "أقدم لغة سامية موثقة في الأرشيفات الإدارية الملكية."
    ),
    InscriptionPreset(
      id = "sennacherib_prism",
      titleAr = "حوليات سنحاريب الآشورية (منشور تايلور)",
      titleEn = "Annals of Sennacherib",
      scriptLayout = SemiticScriptLayout.CUNEIFORM,
      branchAr = "السامية الشرقية",
      dateEstimate = "691 ق.م",
      location = "نينوى (العراق)",
      text = "𒈗 𒃲 𒈗 𒆗 𒉡 𒈗 𒋙 𒈗 𒆳 𒀸 𒋩",
      transliteration = "šarru rabû šarru dannu šar kiššati šar māt Aššur",
      translationAr = "الملك العظيم، الملك القوي، ملك الكون، ملك أرض آشور",
      philologicalNotes = "الأسلوب الآشوري الإمبراطوري المتأخر."
    ),

    // 2. Canaanite & Phoenician
    InscriptionPreset(
      id = "mesha_stele",
      titleAr = "مسلة ميشع المؤابية (KAI 181)",
      titleEn = "Mesha Stele of Moab",
      scriptLayout = SemiticScriptLayout.PHOENICIAN,
      branchAr = "الكنعانية والفينيقية",
      dateEstimate = "840 ق.م",
      location = "ذيبان - الأردن (متحف اللوفر)",
      text = "𐤀𐤍𐤊 𐤌𐤔𐤏 𐤁𐤍 𐤊𐤌𐤔𐤌𐤋𐤊 𐤌𐤋𐤊 𐤌𐤀𐤁 𐤄𐤃𐤉𐤁𐤍𐤉 𐤀𐤁𐤉 𐤌𐤋𐤊 𐤏𐤋 𐤌𐤀𐤁 𐤔𐤋𐤔𐤍 𐤔𐤕",
      transliteration = "ʾnk mšʿ bn kmšmlk mlk mʾb hdəybny ʾby mlk ʿl mʾb šlšn št",
      translationAr = "أنا ميشع بن كموش ملك، ملك مؤاب الذيباني، ملك أبي على مؤاب ثلاثين عاماً",
      philologicalNotes = "أطول نقش كنعاني يوضح واو العطف السردية والتأنيث بالتاء."
    ),
    InscriptionPreset(
      id = "ahiram_sarcophagus",
      titleAr = "تابوت أحيرام ملك جبيل (KAI 1)",
      titleEn = "Ahiram Sarcophagus",
      scriptLayout = SemiticScriptLayout.PHOENICIAN,
      branchAr = "الكنعانية والفينيقية",
      dateEstimate = "1000 ق.م",
      location = "جبيل - لبنان (متحف بيروت)",
      text = "𐤀𐤓𐤍 𐤆 𐤐𐤏𐤋 𐤀𐤕𐤁𐤏𐤋 𐤁𐤍 𐤀𐤇𐤓𐤌 𐤌𐤋𐤊 𐤂𐤁𐤋 𐤋𐤀𐤇𐤓𐤌 𐤀𐤁𐤄",
      transliteration = "ʾrn z pʿl ʾtbʿl bn ʾḥrm mlk gbl l-ʾḥrm ʾbh",
      translationAr = "هذا التابوت صنعه إيتبعل بن أحيرام ملك جبيل لأبيه أحيرام",
      philologicalNotes = "شاهد التأسيس الكلاسيكي للأبجدية الفينيقية ذات الـ 22 حرفاً."
    ),
    InscriptionPreset(
      id = "karatepe_bilingual",
      titleAr = "مسلة كاراتبه (KAI 26)",
      titleEn = "Karatepe Inscription",
      scriptLayout = SemiticScriptLayout.PHOENICIAN,
      branchAr = "الكنعانية والفينيقية",
      dateEstimate = "720 ق.م",
      location = "قيليقية (تركيا)",
      text = "𐤀𐤍𐤊 𐤀𐤆𐤕𐤅𐤃 𐤄𐤁𐤓𐤊 𐤁𐤏𐤋 𐤏𐤁𐤃 𐤁𐤏𐤋 𐤀𐤔 𐤀𐤃𐤓 𐤀𐤅𐤓𐤊 𐤌𐤋𐤊 𐤃𐤍𐤍𐤉𐤌",
      transliteration = "ʾnk ʾztwd hbrk bʿl ʿbd bʿl ʾš ʾdr ʾwrk mlk dnnym",
      translationAr = "أنا أزيتوادا مبارك الإله بعل، عبد بعل الذي عظمه أوريك ملك الدنانيين",
      philologicalNotes = "نص ثنائي اللغة (فينيقي وهيروغليفي لوفي)."
    ),
    InscriptionPreset(
      id = "kilamuwa_stele",
      titleAr = "مسلة كيلاموا ملك شمأل (KAI 24)",
      titleEn = "Kilamuwa Stele",
      scriptLayout = SemiticScriptLayout.PHOENICIAN,
      branchAr = "الكنعانية والفينيقية",
      dateEstimate = "825 ق.م",
      location = "زنجرلي - تركيا (متحف برلين)",
      text = "𐤀𐤍𐤊 𐤊𐤋𐤌𐤅 𐤁𐤓 𐤇𐤉𐤀 𐤌𐤋𐤊 𐤂𐤁𐤓 𐤏𐤋 𐤉𐤀𐤃𐤉 𐤅𐤁𐤋 𐤐𐤏𐤋",
      transliteration = "ʾnk klmw br ḥyʾ mlk gbr ʿl yʾdy w-bl pʿl",
      translationAr = "أنا كيلاموا بن حيا، ملك غبار على يادي ولم ينجز شيئاً فحققت الرخاء",
      philologicalNotes = "تمزج الفينيقية بفصحى الآرامية الشمالية في الأسلوب الأدبي."
    ),
    InscriptionPreset(
      id = "tabnit_sidon",
      titleAr = "تابوت تبنيت كاهن عشتروت (KAI 13)",
      titleEn = "Tabnit Sarcophagus of Sidon",
      scriptLayout = SemiticScriptLayout.PHOENICIAN,
      branchAr = "الكنعانية والفينيقية",
      dateEstimate = "500 ق.م",
      location = "صيدا - لبنان (متحف إسطنبول)",
      text = "𐤀𐤍𐤊 𐤕𐤁𐤍𐤕 𐤊𐤄𐤍 𐤏𐤔𐤕𐤓𐤕 𐤌𐤋𐤊 𐤑𐤃𐤍𐤌 𐤁𐤍 𐤀𐤔𐤌𐤍𐤏𐤆𐤓",
      transliteration = "ʾnk tbnt khn ʿštrt mlk ṣdnm bn ʾšmnʿzr",
      translationAr = "أنا تبنيت كاهن عشتروت، ملك الصيدونيين بن أشمون عازر",
      philologicalNotes = "نقش جنائزي صيدوني كلاسيكي يحذر من فتح القبر."
    ),
    InscriptionPreset(
      id = "eshmunazar_sarco",
      titleAr = "تابوت أشمون عازر الثاني (KAI 14)",
      titleEn = "Eshmunazar II Sarcophagus",
      scriptLayout = SemiticScriptLayout.PHOENICIAN,
      branchAr = "الكنعانية والفينيقية",
      dateEstimate = "475 ق.م",
      location = "صيدا (متحف اللوفر)",
      text = "𐤁𐤉𐤓𐤇 𐤁𐤋 𐤁𐤔𐤍𐤕 𐤏𐤎𐤓 𐤅𐤀𐤓𐤁𐤏 𐤋𐤌𐤋𐤊𐤉 𐤌𐤋𐤊 𐤀𐤔𐤌𐤍𐤏𐤆𐤓",
      transliteration = "byrḥ bl bšnt ʿsr w-ʾrbʿ l-mlky mlk ʾšmnʿzr",
      translationAr = "في شهر بُل، في السنة الرابعة عشرة لملكي، أنا الملك أشمون عازر",
      philologicalNotes = "يوثق معابد عشتروت وأشمون وبعل صيدا وتوسيع الحدود."
    ),
    InscriptionPreset(
      id = "siloam_inscription",
      titleAr = "نقش نفق سلوام بالعبرية القديمة (KAI 189)",
      titleEn = "Siloam Tunnel Inscription",
      scriptLayout = SemiticScriptLayout.PHOENICIAN,
      branchAr = "الكنعانية والفينيقية",
      dateEstimate = "701 ق.م",
      location = "القدس (متحف إسطنبول)",
      text = "𐤕𐤌𐤄 𐤄𐤍𐤒𐤁𐤄 𐤅𐤆𐤄 𐤄𐤉𐤄 𐤃𐤁𐤓 𐤄𐤍𐤒𐤁𐤄 𐤁𐤏𐤅𐤃 𐤄𐤇𐤑𐤁𐤌 𐤌𐤍𐤐𐤉𐤌 𐤄𐤂𐤓𐤆𐤍",
      transliteration = "tmh hnqbh wzh hyh dbr hnqbh bʿwd hḥṣbm mnpym hgrzn",
      translationAr = "تم النقب! وكان هذا شأن النقب: بينما الفؤوس تضرب ويلتقي العمال",
      philologicalNotes = "أبرز نموذج للعبرية القديمة في العصر الحديدي الثاني."
    ),
    InscriptionPreset(
      id = "tel_dan_stele",
      titleAr = "نقش تل القاضي / دان (بيت دافيد)",
      titleEn = "Tel Dan Stele",
      scriptLayout = SemiticScriptLayout.ARAMAIC,
      branchAr = "الآرامية والسريانية",
      dateEstimate = "840 ق.م",
      location = "شمال الجليل",
      text = "𐡅𐡒𐡕𐡋𐡕 𐡌𐡋𐡊 𐡉𐡔𐡓𐡀𐡋 𐡅𐡒𐡕𐡋𐡕 𐡁𐡉𐡕 𐡃𐡅𐡃",
      transliteration = "w-qtlt mlk yšrʾl w-qtlt bytdwd",
      translationAr = "وقتلت ملك إسرائيل وقتلت من بيت داود",
      philologicalNotes = "نقش آرامي يخلد انتصارات حزائيل ملك دمشق."
    ),

    // 3. Ugaritic Cuneiform
    InscriptionPreset(
      id = "ugarit_baal_epic",
      titleAr = "ملحمة صراع بعل وموت (KTU 1.1)",
      titleEn = "Ugaritic Baal Epic",
      scriptLayout = SemiticScriptLayout.UGARITIC,
      branchAr = "الأوغاريتية",
      dateEstimate = "1350 ق.م",
      location = "رأس الشمرا - سوريا",
      text = "𐎎𐎍𐎋 𐎓𐎍𐎎 𐎁𐎓𐎍 𐎊𐎎𐎍𐎋 𐎊𐎘𐎁 𐎍𐎋𐎎 𐎁𐎓𐎍 𐎉𐎁 𐎍𐎀𐎎𐎗",
      transliteration = "mlk ʿlm bʿl ymlk yṯb l-km bʿl ṭb l-ʾmr",
      translationAr = "ملك الأبد، الإله بعل يملك ويجلس على عرشه العالي",
      philologicalNotes = "أبجدية مسمارية تحتوي على 30 حرفاً صوتياً دقيقاً."
    ),
    InscriptionPreset(
      id = "ugarit_keret",
      titleAr = "أسطورة كيرت الملكية (KTU 1.14)",
      titleEn = "Legend of King Keret",
      scriptLayout = SemiticScriptLayout.UGARITIC,
      branchAr = "الأوغاريتية",
      dateEstimate = "1300 ق.م",
      location = "رأس الشمرا (اللوفر)",
      text = "𐎁𐎊𐎚 𐎎𐎍𐎋 𐎊𐎓𐎚𐎐 𐎋𐎗𐎚 𐎙𐎍𐎎 𐎛𐎍 𐎐𐎓𐎎𐎐",
      transliteration = "byt mlk yʿtn krt ġlm ʾil nʿmn",
      translationAr = "بيت الملك أُعطي لكيرت، فتى الإله إيل المحبوب",
      philologicalNotes = "نصوص أدبية كنعانية ميثولوجية متقدمة."
    ),

    // 4. Old South Arabian Musnad
    InscriptionPreset(
      id = "sirwah_karibil",
      titleAr = "نقش صرواح الكبير لكربئيل وتر (RES 3945)",
      titleEn = "Great Sirwah Inscription",
      scriptLayout = SemiticScriptLayout.MUSNAD,
      branchAr = "المسند والعربية الجنوبية",
      dateEstimate = "685 ق.م",
      location = "معبد أوعال - صرواح (مأرب)",
      text = "𐩫𐩧𐩨𐩱𐩡 𐩥𐩩𐩧 𐩣𐩫𐩧𐩨 𐩪𐩨𐩱 𐩨𐩬 𐩵𐩣𐩧𐩲𐩡𐩺 𐩥𐩤𐩰 𐩡𐩱𐩡𐩣𐩤𐩠 𐩥𐩡𐩪𐩨𐩱",
      transliteration = "krbʾl wtr mkrb sbʾ bn ḏmrʿly wqf l-ʾlmqh w-l-sbʾ",
      translationAr = "كربئيل وتر مكرب سبأ بن ذمار علي أوقف ونذر للإله إلمقه ولقبائل سبأ",
      philologicalNotes = "أطول نقش حربي موحد لممالك اليمن القديم بخط المسند السبئي البارز."
    ),
    InscriptionPreset(
      id = "awam_temple",
      titleAr = "نقش محرم بلقيس / معبد أوام (Ja 576)",
      titleEn = "Mahram Bilqis / Awam Temple",
      scriptLayout = SemiticScriptLayout.MUSNAD,
      branchAr = "المسند والعربية الجنوبية",
      dateEstimate = "القرن الخامس ق.م",
      location = "مأرب - اليمن",
      text = "𐩱𐩡𐩣𐩤𐩠 𐩻𐩠𐩥 𐩨𐩲𐩡 𐩱𐩥𐩣 𐩵𐩣𐩧𐩺 𐩲𐩨𐩵𐩠𐩥 𐩨𐩬 𐩨𐩧𐩱 𐩣𐩲𐩣𐩧",
      transliteration = "ʾlmqh ṯhw bʿl ʾwm ḏmry ʿbdhw bn brʾ mʿmr",
      translationAr = "إلمقه ثهو سيد معبد أوام يحمي عبيده الذين شيدوا هذا الصرح المبارك",
      philologicalNotes = "يوضح البناء اللغوي التوحيدي السبئي ونظام التنوين بالميم."
    ),
    InscriptionPreset(
      id = "marib_dam_restoration",
      titleAr = "نقش سد مأرب وترميمه لشرحبيل يعفر (CIH 540)",
      titleEn = "Marib Dam Restoration Inscription",
      scriptLayout = SemiticScriptLayout.MUSNAD,
      branchAr = "المسند والعربية الجنوبية",
      dateEstimate = "450م",
      location = "سد مأرب - اليمن",
      text = "𐩨𐩧𐩱 𐩥𐩣𐩫𐩧 𐩲𐩧𐩣𐩬 𐩣𐩧𐩨 𐩨𐩫𐩡 𐩲𐩪𐩫𐩧 𐩪𐩨𐩱 𐩥𐩢𐩣𐩺𐩧",
      transliteration = "brʾ w-mkr ʿrmn mryb b-kl ʿskr sbʾ w-ḥmyr",
      translationAr = "بنى ورمم سد مأرب العظيم بجيوش وتحالف قبائل سبأ وحِميَر وحضرموت",
      philologicalNotes = "يبرز التحولات الصرفية في السبئية المتأخرة والعهد الحميري."
    ),
    InscriptionPreset(
      id = "baraqish_minaic",
      titleAr = "نقش براقش المعيني لتجارة اللبان (M 247)",
      titleEn = "Baraqish Minaean Inscription",
      scriptLayout = SemiticScriptLayout.MUSNAD,
      branchAr = "المسند والعربية الجنوبية",
      dateEstimate = "350 ق.م",
      location = "يثل / براقش - الجوف (اليمن)",
      text = "𐩥𐩤𐩰 𐩲𐩻𐩩𐩧 𐩵𐩤𐩨𐩵𐩣 𐩨𐩣𐩪𐩫𐩬 𐩥𐩡𐩨𐩬𐩬 𐩥𐩣𐩰𐩲𐩡𐩩",
      transliteration = "wqf ʿṯtr ḏ-qbḍm b-mskn w-lbnn w-mfʿlt",
      translationAr = "أوقف لعثتر ذو قبض بالمسك واللبان وحماية قوافل طريق البخور نحو الشام ومصر",
      philologicalNotes = "المعينية القديمة وريادتها في مصطلحات التجارة والملاحة البرية."
    ),
    InscriptionPreset(
      id = "timna_market",
      titleAr = "قانون سوق تمنع القتباني (RES 4337)",
      titleEn = "Timna Market Regulations",
      scriptLayout = SemiticScriptLayout.MUSNAD,
      branchAr = "المسند والعربية الجنوبية",
      dateEstimate = "200 ق.م",
      location = "وادي بيحان - شبوة (اليمن)",
      text = "𐩵𐩬 𐩣𐩴𐩨𐩺 𐩥𐩦𐩧𐩲 𐩦𐩥𐩤𐩬 𐩨𐩩𐩣𐩬𐩲 𐩡𐩫𐩡 𐩣𐩫𐩧𐩺 𐩥𐩩𐩴𐩧",
      transliteration = "ḏn mgby w-šrʿ šwqn b-tmnʿ l-kl mkry w-tgr",
      translationAr = "هذا مرسوم وتشريع السوق في تمنع لكافة البائعين والتجار والضرائب المفروضة",
      philologicalNotes = "القتبانية ومصطلحاتها القانونية والتنظيمية المتميزة."
    ),

    // 5. Nabataean, Palmyrene & Early Arabic
    InscriptionPreset(
      id = "namara_inscription",
      titleAr = "نقش النمارة لمرؤ القيس (328م)",
      titleEn = "Namara Inscription of Imru' al-Qais",
      scriptLayout = SemiticScriptLayout.NABATAEAN,
      branchAr = "العربية والبادية القديمة",
      dateEstimate = "328م",
      location = "حوران - النمارة (متحف اللوفر)",
      text = "𐢀𐢍 𐢕𐢐𐢛 𐢑𐢛𐢀 𐢀𐢄𐢎𐢅 𐢁𐢛 𐢏𐢑𐢛𐢅 𐢑𐢄𐢎 𐢀𐢄𐢏𐢛𐢁 𐢎𐢄𐢄𐢀",
      transliteration = "ty nfs mrʾ l-qys br ʿmrw mlk ʾl-ʿrb klhʾ",
      translationAr = "تي نفس مرؤ القيس بن عمرو ملك العرب كلهم الذي تقلد التاج وأخضع القبائل",
      philologicalNotes = "حلقة الوصل الكبرى بين الخط النبطي المتأخر والخط العربي الكلاسيكي."
    ),
    InscriptionPreset(
      id = "safaitic_caravan",
      titleAr = "نقوش الصفاة البازلتية ورسوم القوافل",
      titleEn = "Safaitic Harrah Inscription",
      scriptLayout = SemiticScriptLayout.SAFAITIC,
      branchAr = "العربية والبادية القديمة",
      dateEstimate = "القرن الأول الميلادي",
      location = "حرة الشام - بادية الأردن وسوريا",
      text = "𐪀𐪁 𐪒𐪅𐪘 𐪁𐪘 𐪋𐪝𐪇 𐪚𐪎𐪄𐪒 𐪂𐪚 𐪀𐪡𐪍",
      transliteration = "l bġḍ bn s¹ʿd w-ḥwy ʿl ʾbh w-rʿy h-ʾbl",
      translationAr = "لـ بَغْض بن سعد، وقد حزن على أبيه ورعى الإبل وطلب النجاة من الإله رُضا",
      philologicalNotes = "تمثل لهجات البادية العربية الشمالية القديمة (الصفائية)."
    ),
    InscriptionPreset(
      id = "palmyra_tariff",
      titleAr = "قانون تعرفة تدمر الجمركي",
      titleEn = "Palmyrene Tariff Inscription",
      scriptLayout = SemiticScriptLayout.PALMYRENE,
      branchAr = "الأنباط وتدمر",
      dateEstimate = "137م",
      location = "تدمر - سوريا (متحف الإرميتاج)",
      text = "𐡃𐡂𐡎 𐡃𐡌𐡊𐡎 𐡃𐡕𐡃𐡌𐡅𐡓 𐡅𐡍𐡌𐡅𐡎 𐡌𐡊𐡎𐡀",
      transliteration = "dgs dmks d-tdmwr w-nmws mksʾ",
      translationAr = "مرسوم ضريبة جمارك تدمر وقانون تحصيل رسوم القوافل والشحنات",
      philologicalNotes = "الخط التدمري المعتدل والآرامية التدميرية التجارية."
    ),

    // 6. Syriac & Mandaic
    InscriptionPreset(
      id = "peshitta_matthew",
      titleAr = "البشيطتا السريانية بالإسطرنجيلي",
      titleEn = "Syriac Peshitta Gospel",
      scriptLayout = SemiticScriptLayout.SYRIAC,
      branchAr = "الآرامية والسريانية",
      dateEstimate = "القرن الخامس الميلادي",
      location = "الرها - بلاد الشام",
      text = "ܐܰܒܽܘܢ ܕ݁ܒ݂ܰܫܡܰܝܳܐ ܢܶܬ݂ܩܰܕ݁ܰܫ ܫܡܳܟ݂ ܬ݁ܺܐܬ݂ܶܐ ܡܰܠܟ݁ܽܘܬ݂ܳܟ݂",
      transliteration = "ʾabūn d-ba-šmayyā netqaddaš šmāḵ tēṯē malkūṯāḵ",
      translationAr = "أبانا الذي في السماوات ليتقدس اسمك، لتأت ملكوتك كما في السماء كذلك على الأرض",
      philologicalNotes = "السريانية الكلاسيكية (لغة الرها) بخط الأسطرنجيلي المضبوط بالحركات."
    ),
    InscriptionPreset(
      id = "ginza_rabba_opening",
      titleAr = "فاتحة كنزا ربا المندائي",
      titleEn = "Ginza Rabba Opening Hymn",
      scriptLayout = SemiticScriptLayout.MANDAIC,
      branchAr = "الآرامية والسريانية",
      dateEstimate = "القرن الثالث الميلادي",
      location = "ميسان وسهل البطائح (العراق)",
      text = "ࡁࡔࡅࡌࡀࡉࡄࡅࡍ ࡖࡄࡉࡉࡀ ࡓࡁࡉࡀ ࡎࡕࡅࡕ ࡄࡉࡉࡀ ࡒࡃࡌࡀࡉࡉࡀ",
      transliteration = "b-šumayhun d-hiyi rbiya stwt hiyi qdmayi",
      translationAr = "باسم الحي العظيم، التسبيح للحي الأول الخالق الحي الأزلي",
      philologicalNotes = "الآرامية المندائية الشرقية المتأخرة ذات النظام الصوتي الخاص."
    ),

    // 7. Ethiosemitic (Ge'ez)
    InscriptionPreset(
      id = "ezana_stele",
      titleAr = "مسلة عيزانا الثلاثية الأكسومية (RIE 185)",
      titleEn = "Ezana Trilingual Stele",
      scriptLayout = SemiticScriptLayout.GEEZ,
      branchAr = "السامية الإثيوبية",
      dateEstimate = "350م",
      location = "أكسوم - إثيوبيا",
      text = "ዐዘነ ንጉሠ አክሱም ወሐመረ ወራይዳን ወስባ ወሐበሠት",
      transliteration = "ʿzn ngś ʾksm w-ḥmr w-rydn w-sbʾ w-ḥbśt",
      translationAr = "عيزانا ملك أكسوم وحِمْيَر وريدان وسبأ وحبشت وسيد الأقاليم",
      philologicalNotes = "حجر رشيد الساميات الإثيوبية (مدون بالجعزية والمسندية واليونانية)."
    ),
    InscriptionPreset(
      id = "garima_gospels",
      titleAr = "أناجيل أبا غاريما الجعزية",
      titleEn = "Abba Garima Ge'ez Manuscript",
      scriptLayout = SemiticScriptLayout.GEEZ,
      branchAr = "السامية الإثيوبية",
      dateEstimate = "450م",
      location = "دير أبا غاريما - إثيوبيا",
      text = "በስመ አብ ወወልድ ወመንፈስ ቅዱስ አሐዱ አምላክ",
      transliteration = "b-sma ʾb w-wld w-mnfəs qdus ʾḥdu ʾmlak",
      translationAr = "باسم الآب والابن والروح القدس الإله الواحد",
      philologicalNotes = "أقدم مخطوطة إنجيلية جعزية مكتملة ومزخرفة على الرقوق الجلدية."
    )
  )
}

fun getKeysForScript(script: SemiticScriptLayout, geezOrder: Int = 1): List<SemiticKey> {
  return when (script) {
    SemiticScriptLayout.PHOENICIAN -> listOf(
      SemiticKey("𐤀", "ʾ", "ألف", "/ʔ/", "أ", 1, "رأس ثور 𓃾", "صامت حنجري انفجاري"),
      SemiticKey("𐤁", "b", "بيت", "/b/", "ب", 2, "مخطط بيت 𓉐", "صامت شفوي مجهور"),
      SemiticKey("𐤂", "g", "جيمل", "/ɡ/", "ج", 3, "عصا رمي / سنام", "صامت طبقي مجهور"),
      SemiticKey("𐤃", "d", "دالت", "/d/", "د", 4, "باب / سمكة 𓆛", "صامت أسناني مجهور"),
      SemiticKey("𐤄", "h", "هي", "/h/", "هـ", 5, "رجل يصلي / نافذة 𓀠", "صامت حنجري مهموس"),
      SemiticKey("𐤅", "w", "واو", "/w/", "و", 6, "وتد / خطاف 𓏲", "شبه صامت شفوي طبقي"),
      SemiticKey("𐤆", "z", "زاين", "/z/", "ز", 7, "سلاح / خنجر", "صامت لثوي صفيري مجهور"),
      SemiticKey("𐤇", "ḥ", "حيت", "/ħ/", "ح", 8, "سور / سياج 𓉹", "صامت حلقي احتكاكي مهموس"),
      SemiticKey("𐤈", "ṭ", "طيت", "/tʼ/", "ط", 9, "عجلة / سلة", "صامت أسناني مطبق/قذفي"),
      SemiticKey("𐤉", "y", "يود", "/j/", "ي", 10, "ذراع ويد 𓂝", "شبه صامت حنكي"),
      SemiticKey("𐤊", "k", "كاف", "/k/", "ك", 20, "كف اليد 𓂧", "صامت طبقي مهموس"),
      SemiticKey("𐤋", "l", "لامد", "/l/", "ل", 30, "عصا الراعي / منخاس", "صامت لثوي جانبي"),
      SemiticKey("𐤌", "m", "ميم", "/m/", "م", 40, "أمواج الماء 𓈖", "صامت شفوي أنفي"),
      SemiticKey("𐤍", "n", "نون", "/n/", "ن", 50, "أفعى / حية 𓆓", "صامت لثوي أنفي"),
      SemiticKey("𐤎", "s", "سامخ", "/s/", "س", 60, "عمود سمك / مسند", "صامت لثوي احتكاكي"),
      SemiticKey("𐤏", "ʿ", "عين", "/ʕ/", "ع", 70, "عين بشرية 𓁹", "صامت حلقي احتكاكي مجهور"),
      SemiticKey("𐤐", "p", "بي", "/p/", "ف", 80, "فم 𓂋", "صامت شفوي مهموس"),
      SemiticKey("𐤑", "ṣ", "صادي", "/sˤ/", "ص", 90, "نبات / سنارة صيد", "صامت لثوي صفيري مطبق"),
      SemiticKey("𐤒", "q", "قوف", "/q/", "ق", 100, "ثقب إبرة / قرد 𓃻", "صامت لهوي انفجاري"),
      SemiticKey("𐤓", "r", "ريش", "/r/", "ر", 200, "رأس بشري 𓁶", "صامت لثوي تكراري"),
      SemiticKey("𐤔", "š", "شين", "/ʃ/", "ش", 300, "أسنان 𓌉", "صامت غاري احتكاكي"),
      SemiticKey("𐤕", "t", "تاو", "/t/", "ت", 400, "علامة الصليب 𓏴", "صامت أسناني مهموس"),
      // Phoenician Numerals & Signs
      SemiticKey("𐤖", "1", "واحد فينيقي", "1", "١", 1),
      SemiticKey("𐤗", "10", "عشرة فينيقية", "10", "١٠", 10),
      SemiticKey("𐤘", "20", "عشرون فينيقية", "20", "٢٠", 20),
      SemiticKey("𐤙", "100", "مائة فينيقية", "100", "١٠٠", 100),
      SemiticKey("𐤟", "•", "فاصل كنعاني", "-", "•")
    )

    SemiticScriptLayout.UGARITIC -> listOf(
      SemiticKey("𐎀", "ʾa", "ألف مفتوحة", "/ʔa/", "أَ", 1, "مسمار ألف مع فتحة"),
      SemiticKey("𐎁", "b", "بيتا", "/b/", "ب", 2),
      SemiticKey("𐎂", "g", "غامل", "/ɡ/", "ج", 3),
      SemiticKey("𐎃", "ḫ", "خا", "/x/", "خ", 600),
      SemiticKey("𐎄", "d", "دالتا", "/d/", "د", 4),
      SemiticKey("𐎅", "h", "هي", "/h/", "هـ", 5),
      SemiticKey("𐎆", "w", "واو", "/w/", "و", 6),
      SemiticKey("𐎇", "z", "زيتا", "/z/", "ز", 7),
      SemiticKey("𐎈", "ḥ", "حوتا", "/ħ/", "ح", 8),
      SemiticKey("𐎉", "ṭ", "طيتا", "/tʼ/", "ط", 9),
      SemiticKey("𐎊", "y", "يودا", "/j/", "ي", 10),
      SemiticKey("𐎋", "k", "كاف", "/k/", "ك", 20),
      SemiticKey("𐎌", "š", "شين", "/ʃ/", "ش", 300),
      SemiticKey("𐎍", "l", "لامدا", "/l/", "ل", 30),
      SemiticKey("𐎎", "m", "ميما", "/m/", "م", 40),
      SemiticKey("𐎏", "ḏ", "ذال", "/ð/", "ذ", 700),
      SemiticKey("𐎐", "n", "نونا", "/n/", "ن", 50),
      SemiticKey("𐎑", "ẓ", "ظا", "/ðʼ/", "ظ", 900),
      SemiticKey("𐎒", "s", "سامكا", "/s/", "س", 60),
      SemiticKey("𐎓", "ʿ", "عين", "/ʕ/", "ع", 70),
      SemiticKey("𐎔", "p", "بو", "/p/", "ف", 80),
      SemiticKey("𐎕", "ṣ", "صادي", "/sˤ/", "ص", 90),
      SemiticKey("𐎖", "q", "قوبا", "/q/", "ق", 100),
      SemiticKey("𐎗", "r", "راشا", "/r/", "ر", 200),
      SemiticKey("𐎘", "ṯ", "ثاء", "/θ/", "ث", 500),
      SemiticKey("𐎙", "ġ", "غين", "/ɣ/", "غ", 1000),
      SemiticKey("𐎚", "t", "تو", "/t/", "ت", 400),
      SemiticKey("𐎛", "ʾi", "ألف مكسورة", "/ʔi/", "إِ"),
      SemiticKey("𐎜", "ʾu", "ألف مضمومة", "/ʔu/", "أُ"),
      SemiticKey("𐎝", "ś", "شين جانبية", "/ɬ/", "س"),
      SemiticKey("𐎟", "•", "فاصل مسماري", "-", "•")
    )

    SemiticScriptLayout.MUSNAD -> listOf(
      SemiticKey("𐩠", "h", "هي", "/h/", "هـ", 5),
      SemiticKey("𐩡", "l", "لام", "/l/", "ل", 30),
      SemiticKey("𐩢", "ḥ", "حاء", "/ħ/", "ح", 8),
      SemiticKey("𐩣", "m", "ميم", "/m/", "م", 40),
      SemiticKey("𐩤", "q", "قاف", "/q/", "ق", 100),
      SemiticKey("𐩥", "w", "واو", "/w/", "و", 6),
      SemiticKey("𐩦", "š²", "شين جانبية", "/ɬ/", "ش"),
      SemiticKey("𐩧", "r", "راء", "/r/", "ر", 200),
      SemiticKey("𐩨", "b", "باء", "/b/", "ب", 2),
      SemiticKey("𐩩", "t", "تاء", "/t/", "ت", 400),
      SemiticKey("𐩪", "s¹", "سين", "/s/", "س", 60),
      SemiticKey("𐩫", "k", "كاف", "/k/", "ك", 20),
      SemiticKey("𐩬", "n", "نون", "/n/", "ن", 50),
      SemiticKey("𐩭", "ḫ", "خاء", "/x/", "خ", 600),
      SemiticKey("𐩮", "ṣ", "صاد", "/sˤ/", "ص", 90),
      SemiticKey("𐩯", "s³", "سامخ", "/s/", "س"),
      SemiticKey("𐩰", "f", "فاء", "/f/", "ف", 80),
      SemiticKey("𐩱", "ʾ", "ألف", "/ʔ/", "أ", 1),
      SemiticKey("𐩲", "ʿ", "عين", "/ʕ/", "ع", 70),
      SemiticKey("𐩳", "ḍ", "ضاد جانبية", "/ɬʼ/", "ض", 800),
      SemiticKey("𐩴", "g", "جيم", "/ɡ/", "ج", 3),
      SemiticKey("𐩵", "d", "دال", "/d/", "د", 4),
      SemiticKey("𐩶", "ġ", "غين", "/ɣ/", "غ", 1000),
      SemiticKey("𐩷", "ṭ", "طاء", "/tʼ/", "ط", 9),
      SemiticKey("𐩸", "z", "زاي", "/z/", "ز", 7),
      SemiticKey("𐩹", "ḏ", "ذال", "/ð/", "ذ", 700),
      SemiticKey("𐩺", "y", "ياء", "/j/", "ي", 10),
      SemiticKey("𐩻", "ṯ", "ثاء", "/θ/", "ث", 500),
      SemiticKey("𐩼", "ẓ", "ظاء", "/ðʼ/", "ظ", 900),
      // Musnad Numbers
      SemiticKey("𐩽", "1", "واحد مسندي", "1", "١", 1),
      SemiticKey("𐩾", "50", "خمسون مسندية", "50", "٥٠", 50),
      SemiticKey("𐩿", "|", "فاصل المسند", "-", "|")
    )

    SemiticScriptLayout.SAFAITIC -> listOf(
      SemiticKey("𐪀", "ʾ", "ألف صفائية", "/ʔ/", "أ"),
      SemiticKey("𐪁", "b", "باء صفائية", "/b/", "ب"),
      SemiticKey("𐪂", "t", "تاء صفائية", "/t/", "ت"),
      SemiticKey("𐪃", "ṯ", "ثاء صفائية", "/θ/", "ث"),
      SemiticKey("𐪄", "g", "جيم صفائية", "/ɡ/", "ج"),
      SemiticKey("𐪅", "ḥ", "حاء صفائية", "/ħ/", "ح"),
      SemiticKey("𐪆", "ḫ", "خاء صفائية", "/x/", "خ"),
      SemiticKey("𐪇", "d", "دال صفائية", "/d/", "د"),
      SemiticKey("𐪈", "ḏ", "ذال صفائية", "/ð/", "ذ"),
      SemiticKey("𐪉", "r", "راء صفائية", "/r/", "ر"),
      SemiticKey("𐪊", "z", "زاي صفائية", "/z/", "ز"),
      SemiticKey("𐪋", "s¹", "سين صفائية", "/s/", "س"),
      SemiticKey("𐪌", "s²", "شين صفائية", "/ɬ/", "ش"),
      SemiticKey("𐪍", "ṣ", "صاد صفائية", "/sˤ/", "ص"),
      SemiticKey("𐪎", "ḍ", "ضاد صفائية", "/ɬʼ/", "ض"),
      SemiticKey("𐪏", "ṭ", "طاء صفائية", "/tʼ/", "ط"),
      SemiticKey("𐪐", "ẓ", "ظاء صفائية", "/ðʼ/", "ظ"),
      SemiticKey("𐪑", "ʿ", "عين صفائية", "/ʕ/", "ع"),
      SemiticKey("𐪒", "ġ", "غين صفائية", "/ɣ/", "غ"),
      SemiticKey("𐪓", "f", "فاء صفائية", "/f/", "ف"),
      SemiticKey("𐪔", "q", "قاف صفائية", "/q/", "ق"),
      SemiticKey("𐪕", "k", "كاف صفائية", "/k/", "ك"),
      SemiticKey("𐪖", "l", "لام صفائية", "/l/", "ل"),
      SemiticKey("𐪗", "m", "ميم صفائية", "/m/", "م"),
      SemiticKey("𐪘", "n", "نون صفائية", "/n/", "ن"),
      SemiticKey("𐪙", "h", "هاء صفائية", "/h/", "هـ"),
      SemiticKey("𐪚", "w", "واو صفائية", "/w/", "و"),
      SemiticKey("𐪛", "y", "ياء صفائية", "/j/", "ي")
    )

    SemiticScriptLayout.ARAMAIC -> listOf(
      SemiticKey("𐡀", "ʾ", "أولف", "/ʔ/", "أ", 1),
      SemiticKey("𐡁", "b", "بيث", "/b/", "ب", 2),
      SemiticKey("𐡂", "g", "غامل", "/ɡ/", "ج", 3),
      SemiticKey("𐡃", "d", "دالث", "/d/", "د", 4),
      SemiticKey("𐡄", "h", "هي", "/h/", "هـ", 5),
      SemiticKey("𐡅", "w", "واو", "/w/", "و", 6),
      SemiticKey("𐡆", "z", "زاين", "/z/", "ز", 7),
      SemiticKey("𐡇", "ḥ", "حيث", "/ħ/", "ح", 8),
      SemiticKey("𐡈", "ṭ", "طيث", "/tʼ/", "ط", 9),
      SemiticKey("𐡉", "y", "يود", "/j/", "ي", 10),
      SemiticKey("𐡊", "k", "كاف", "/k/", "ك", 20),
      SemiticKey("𐡋", "l", "لامد", "/l/", "ل", 30),
      SemiticKey("𐡌", "m", "ميم", "/m/", "م", 40),
      SemiticKey("𐡍", "n", "نون", "/n/", "ن", 50),
      SemiticKey("𐡎", "s", "سمكث", "/s/", "س", 60),
      SemiticKey("𐡏", "ʿ", "عين", "/ʕ/", "ع", 70),
      SemiticKey("𐡐", "p", "بي", "/p/", "ف", 80),
      SemiticKey("𐡑", "ṣ", "صادي", "/sˤ/", "ص", 90),
      SemiticKey("𐡒", "q", "قوف", "/q/", "ق", 100),
      SemiticKey("𐡓", "r", "ريش", "/r/", "ر", 200),
      SemiticKey("𐡔", "š", "شين", "/ʃ/", "ش", 300),
      SemiticKey("𐡕", "t", "تاو", "/t/", "ت", 400),
      // Aramaic Numerals
      SemiticKey("𐡘", "1", "واحد آرامي", "1", "١", 1),
      SemiticKey("𐡙", "2", "اثنان آرامي", "2", "٢", 2),
      SemiticKey("𐡚", "3", "ثلاثة آرامي", "3", "٣", 3),
      SemiticKey("𐡛", "10", "عشرة آرامية", "10", "١٠", 10),
      SemiticKey("𐡜", "20", "عشرون آرامية", "20", "٢٠", 20),
      SemiticKey("𐡝", "100", "مائة آرامية", "100", "١٠٠", 100)
    )

    SemiticScriptLayout.SYRIAC -> listOf(
      SemiticKey("ܐ", "ʾ", "أولف", "/ʔ/", "أ", 1),
      SemiticKey("ܒ", "b", "بيث", "/b/", "ب", 2),
      SemiticKey("ܓ", "g", "غومل", "/ɡ/", "ج", 3),
      SemiticKey("ܕ", "d", "دولث", "/d/", "د", 4),
      SemiticKey("ܗ", "h", "هي", "/h/", "هـ", 5),
      SemiticKey("ܘ", "w", "واو", "/w/", "و", 6),
      SemiticKey("ܙ", "z", "زاين", "/z/", "ز", 7),
      SemiticKey("ܚ", "ḥ", "حيث", "/ħ/", "ح", 8),
      SemiticKey("ܛ", "ṭ", "طيث", "/tʼ/", "ط", 9),
      SemiticKey("ܝ", "y", "يود", "/j/", "ي", 10),
      SemiticKey("ܟ", "k", "كوف", "/k/", "ك", 20),
      SemiticKey("ܠ", "l", "لومد", "/l/", "ل", 30),
      SemiticKey("ܡ", "m", "ميم", "/m/", "م", 40),
      SemiticKey("ܢ", "n", "نون", "/n/", "ن", 50),
      SemiticKey("ܣ", "s", "سمكث", "/s/", "س", 60),
      SemiticKey("ܥ", "ʿ", "عي", "/ʕ/", "ع", 70),
      SemiticKey("ܦ", "p", "في", "/p/", "ف", 80),
      SemiticKey("ܨ", "ṣ", "صودي", "/sˤ/", "ص", 90),
      SemiticKey("ܩ", "q", "قوف", "/q/", "ق", 100),
      SemiticKey("ܪ", "r", "ريش", "/r/", "ر", 200),
      SemiticKey("ܫ", "š", "شين", "/ʃ/", "ش", 300),
      SemiticKey("ܬ", "t", "تو", "/t/", "ت", 400),
      // Syriac Points & Vowels
      SemiticKey("ܰ", "a", "فتاحا (فتحة)", "/a/", "َ"),
      SemiticKey("ܳ", "ā", "زقافا (ألف)", "/oː/", "ٰ"),
      SemiticKey("ܶ", "e", "رباصا (كسرة)", "/e/", "ِ"),
      SemiticKey("ܺ", "i", "حباgroup (ياء)", "/iː/", "ِي"),
      SemiticKey("ܽ", "u", "عصّاصا (واو)", "/uː/", "ُو"),
      SemiticKey("܁", ".", "نقطة سريانية", "-", ".")
    )

    SemiticScriptLayout.NABATAEAN -> listOf(
      SemiticKey("𐢀", "ʾ", "ألف نبطية", "/ʔ/", "أ", 1),
      SemiticKey("𐢁", "b", "باء نبطية", "/b/", "ب", 2),
      SemiticKey("𐢂", "g", "جيم نبطية", "/ɡ/", "ج", 3),
      SemiticKey("𐢃", "d", "دال نبطية", "/d/", "د", 4),
      SemiticKey("𐢄", "h", "هاء نبطية", "/h/", "هـ", 5),
      SemiticKey("𐢅", "w", "واو نبطية", "/w/", "و", 6),
      SemiticKey("𐢆", "z", "زاي نبطية", "/z/", "ز", 7),
      SemiticKey("𐢇", "ḥ", "حاء نبطية", "/ħ/", "ح", 8),
      SemiticKey("𐢈", "ṭ", "طاء نبطية", "/tʼ/", "ط", 9),
      SemiticKey("𐢉", "y", "ياء نبطية", "/j/", "ي", 10),
      SemiticKey("𐢊", "k", "كاف نبطية", "/k/", "ك", 20),
      SemiticKey("𐢋", "l", "لام نبطية", "/l/", "ل", 30),
      SemiticKey("𐢌", "m", "ميم نبطية", "/m/", "م", 40),
      SemiticKey("𐢍", "n", "نون نبطية", "/n/", "ن", 50),
      SemiticKey("𐢎", "s", "سين نبطية", "/s/", "س", 60),
      SemiticKey("𐢏", "ʿ", "عين نبطية", "/ʕ/", "ع", 70),
      SemiticKey("𐢐", "p", "فاء نبطية", "/p/", "ف", 80),
      SemiticKey("𐢑", "ṣ", "صاد نبطية", "/sˤ/", "ص", 90),
      SemiticKey("𐢒", "q", "قاف نبطية", "/q/", "ق", 100),
      SemiticKey("𐢓", "r", "راء نبطية", "/r/", "ر", 200),
      SemiticKey("𐢔", "š", "شين نبطية", "/ʃ/", "ش", 300),
      SemiticKey("𐢕", "t", "تاء نبطية", "/t/", "ت", 400)
    )

    SemiticScriptLayout.PALMYRENE -> listOf(
      SemiticKey("𐡠", "ʾ", "ألف تدمرية", "/ʔ/", "أ", 1),
      SemiticKey("𐡡", "b", "باء تدمرية", "/b/", "ب", 2),
      SemiticKey("𐡢", "g", "جيم تدمرية", "/ɡ/", "ج", 3),
      SemiticKey("𐡣", "d", "دال تدمرية", "/d/", "د", 4),
      SemiticKey("𐡤", "h", "هاء تدمرية", "/h/", "هـ", 5),
      SemiticKey("𐡥", "w", "واو تدمرية", "/w/", "و", 6),
      SemiticKey("𐡦", "z", "زاي تدمرية", "/z/", "ز", 7),
      SemiticKey("𐡧", "ḥ", "حاء تدمرية", "/ħ/", "ح", 8),
      SemiticKey("𐡨", "ṭ", "طاء تدمرية", "/tʼ/", "ط", 9),
      SemiticKey("𐡩", "y", "ياء تدمرية", "/j/", "ي", 10),
      SemiticKey("𐡪", "k", "كاف تدمرية", "/k/", "ك", 20),
      SemiticKey("𐡫", "l", "لام تدمرية", "/l/", "ل", 30),
      SemiticKey("𐡬", "m", "ميم تدمرية", "/m/", "م", 40),
      SemiticKey("𐡭", "n", "نون تدمرية", "/n/", "ن", 50),
      SemiticKey("𐡮", "s", "سين تدمرية", "/s/", "س", 60),
      SemiticKey("𐡯", "ʿ", "عين تدمرية", "/ʕ/", "ع", 70),
      SemiticKey("𐡰", "p", "فاء تدمرية", "/p/", "ف", 80),
      SemiticKey("𐡱", "ṣ", "صاد تدمرية", "/sˤ/", "ص", 90),
      SemiticKey("𐡲", "q", "قاف تدمرية", "/q/", "ق", 100),
      SemiticKey("𐡳", "r", "راء تدمرية", "/r/", "ر", 200),
      SemiticKey("𐡴", "š", "شين تدمرية", "/ʃ/", "ش", 300),
      SemiticKey("𐡵", "t", "تاء تدمرية", "/t/", "ت", 400)
    )

    SemiticScriptLayout.MANDAIC -> listOf(
      SemiticKey("ࡀ", "a", "ألف مندائية (هالقا)", "/a/", "أ"),
      SemiticKey("ࡁ", "b", "آب", "/b/", "ب"),
      SemiticKey("ࡂ", "g", "غا", "/ɡ/", "ج"),
      SemiticKey("ࡃ", "d", "دا", "/d/", "د"),
      SemiticKey("ࡄ", "h", "ها", "/h/", "هـ"),
      SemiticKey("ࡅ", "u/w", "وا", "/w/", "و"),
      SemiticKey("ࡆ", "z", "زا", "/z/", "ز"),
      SemiticKey("ࡇ", "ḥ", "إيه", "/ħ/", "ح"),
      SemiticKey("ࡈ", "ṭ", "طا", "/tʼ/", "ط"),
      SemiticKey("ࡉ", "i/y", "يا", "/j/", "ي"),
      SemiticKey("ࡊ", "k", "كا", "/k/", "ك"),
      SemiticKey("ࡋ", "l", "لا", "/l/", "ل"),
      SemiticKey("ࡌ", "m", "ما", "/m/", "م"),
      SemiticKey("ࡍ", "n", "نا", "/n/", "ن"),
      SemiticKey("ࡎ", "s", "سا", "/s/", "س"),
      SemiticKey("ࡏ", "ʿ", "عين (إي)", "/ʕ/", "ع"),
      SemiticKey("ࡐ", "p", "فا", "/p/", "ف"),
      SemiticKey("ࡑ", "ṣ", "صا", "/sˤ/", "ص"),
      SemiticKey("ࡒ", "q", "قا", "/q/", "ق"),
      SemiticKey("ࡓ", "r", "را", "/r/", "ر"),
      SemiticKey("ࡔ", "š", "شا", "/ʃ/", "ش"),
      SemiticKey("ࡕ", "t", "تا", "/t/", "ت"),
      SemiticKey("ࡖ", "ḏ", "آدو (أداة وصل)", "/ð/", "د/ذ")
    )

    SemiticScriptLayout.CUNEIFORM -> listOf(
      SemiticKey("𒀭", "AN / DINGIR", "آن (إله / سماء)", "/ʔilu/", "إيل / إله"),
      SemiticKey("𒈗", "LUGAL / šarru", "لوغال (ملك)", "/ʃarru/", "ملك"),
      SemiticKey("𒂗", "EN / bēlu", "إن (سيد / رب)", "/beːlu/", "بعل / سيد"),
      SemiticKey("𒂍", "É / bītu", "إي (بيت / معبد)", "/biːtu/", "بيت"),
      SemiticKey("𒀀", "A / mû", "آ (ماء)", "/muː/", "ماء"),
      SemiticKey("𒆳", "KUR / mātu", "كور (أرض / جبل)", "/maːtu/", "أرض / بلاد"),
      SemiticKey("𒆠", "KI / erṣetu", "كي (أرض / مكان)", "/ʔersˤetu/", "أرض"),
      SemiticKey("𒈬", "MU / šumu", "مو (اسم / سنة)", "/ʃumu/", "اسم"),
      SemiticKey("𒍣", "ZI / napištu", "زي (نفس / حياة)", "/napiʃtu/", "نَفْس"),
      SemiticKey("𒌉", "DUMU / māru", "دومو (ابن)", "/maːru/", "ابن"),
      SemiticKey("𒊩", "MUNUS / sinništu", "مونوس (امرأة)", "/sinnistu/", "امرأة"),
      SemiticKey("ાગ", "AG / epēšu", "أغ (فعل / صنع)", "/ʔepeːʃu/", "فعل"),
      SemiticKey("𒀊", "AB / abu", "أب (أب)", "/ʔabu/", "أب"),
      SemiticKey("𒋗", "ŠU / qātu", "شو (يد)", "/qaːtu/", "يد"),
      SemiticKey("𒄈", "GÍR / patru", "غير (سيف / خنجر)", "/patru/", "سيف"),
      SemiticKey("𒌓", "UD / ūmu", "أود (يوم / شمس)", "/uːmu/", "يوم"),
      // Akkadian Syllabic Signs
      SemiticKey("𒁀", "ba", "با", "/ba/", "با"),
      SemiticKey("𒁉", "bi", "بي", "/bi/", "بي"),
      SemiticKey("𒁍", "bu", "بو", "/bu/", "بو"),
      SemiticKey("𒁕", "da", "دا", "/da/", "دا"),
      SemiticKey("𒁲", "di", "دي", "/di/", "دي"),
      SemiticKey("𒁺", "du", "دو", "/du/", "دو"),
      SemiticKey("𒈠", "ma", "ما", "/ma/", "ما"),
      SemiticKey("𒈨", "me", "مي", "/me/", "مي"),
      SemiticKey("𒈪", "mi", "مي", "/mi/", "مي"),
      SemiticKey("𒈬", "mu", "مو", "/mu/", "مو"),
      SemiticKey("𒈾", "na", "نا", "/na/", "نا"),
      SemiticKey("𒉌", "ni", "ني", "/ni/", "ني"),
      SemiticKey("𒉡", "nu", "نو", "/nu/", "نو"),
      SemiticKey("𒊏", "ra", "را", "/ra/", "را"),
      SemiticKey("𒊑", "ri", "ري", "/ri/", "ري"),
      SemiticKey("𒊒", "ru", "رو", "/ru/", "رو"),
      SemiticKey("𒊭", "ša", "شا", "/ʃa/", "شا"),
      SemiticKey("𒅆", "ši", "شي", "/ʃi/", "شي"),
      SemiticKey("𒋗", "šu", "شو", "/ʃu/", "شو")
    )

    SemiticScriptLayout.GEEZ -> {
      // 7 vowel orders in Ethiopic: 1=Ge'ez (ä), 2=Ka'eb (u), 3=Sals (i), 4=Rabe (ā), 5=Hams (ē), 6=Sads (ə), 7=Sab'e (o)
      val baseConsonants = listOf(
        listOf("ሀ", "ሁ", "ሂ", "ሃ", "ሄ", "ህ", "ሆ") to SemiticKey("ሀ", "h", "هوي", "/h/", "هـ"),
        listOf("ለ", "ሉ", "ሊ", "ላ", "ሌ", "ል", "ሎ") to SemiticKey("ለ", "l", "لاوي", "/l/", "ل"),
        listOf("ሐ", "ሑ", "ሒ", "ሓ", "ሔ", "ሕ", "ሖ") to SemiticKey("ሐ", "ḥ", "حوت", "/ħ/", "ح"),
        listOf("መ", "ሙ", "ሚ", "ማ", "ሜ", "ም", "ሞ") to SemiticKey("መ", "m", "ماي", "/m/", "م"),
        listOf("ሠ", "ሡ", "ሢ", "ሣ", "ሤ", "ሥ", "ሦ") to SemiticKey("ሠ", "ś", "شوت", "/ɬ/", "س"),
        listOf("ረ", "ሩ", "ሪ", "ራ", "ሬ", "ር", "ሮ") to SemiticKey("ረ", "r", "رأس", "/r/", "ر"),
        listOf("ሰ", "ሱ", "ሲ", "ሳ", "ሴ", "ስ", "ሶ") to SemiticKey("ሰ", "s", "سات", "/s/", "س"),
        listOf("ቀ", "ቁ", "ቂ", "ቃ", "ቄ", "ቅ", "ቆ") to SemiticKey("ቀ", "q", "قاف", "/qʼ/", "ق"),
        listOf("በ", "ቡ", "ቢ", "ባ", "ቤ", "ብ", "ቦ") to SemiticKey("በ", "b", "بيت", "/b/", "ب"),
        listOf("ተ", "ቱ", "ቲ", "ታ", "ቴ", "ት", "ቶ") to SemiticKey("ተ", "t", "تاو", "/t/", "ت"),
        listOf("ኀ", "ኁ", "ኂ", "ኃ", "ኄ", "ኅ", "ኆ") to SemiticKey("ኀ", "ḫ", "خرم", "/x/", "خ"),
        listOf("ነ", "ኑ", "ኒ", "ና", "ኔ", "ን", "ኖ") to SemiticKey("ነ", "n", "نحاس", "/n/", "ن"),
        listOf("አ", "ኡ", "ኢ", "ኣ", "ኤ", "እ", "ኦ") to SemiticKey("አ", "ʾ", "ألف", "/ʔ/", "أ"),
        listOf("ከ", "ኩ", "ኪ", "ካ", "ኬ", "ክ", "ኮ") to SemiticKey("ከ", "k", "كاف", "/k/", "ك"),
        listOf("ወ", "ዉ", "ዊ", "ዋ", "ዌ", "ው", "ዎ") to SemiticKey("ወ", "w", "واوي", "/w/", "و"),
        listOf("ዐ", "ዑ", "ዒ", "ዓ", "ዔ", "ዕ", "ዖ") to SemiticKey("ዐ", "ʿ", "عين", "/ʕ/", "ع"),
        listOf("ዘ", "ዙ", "ዚ", "ዛ", "ዜ", "ዝ", "ዞ") to SemiticKey("ዘ", "z", "زاي", "/z/", "ز"),
        listOf("የ", "ዩ", "ዪ", "ያ", "ዬ", "ይ", "ዮ") to SemiticKey("የ", "y", "يمن", "/j/", "ي"),
        listOf("ደ", "ዱ", "ዲ", "ዳ", "ዴ", "ድ", "ዶ") to SemiticKey("ደ", "d", "دنت", "/d/", "د"),
        listOf("ገ", "ጉ", "ጊ", "ጋ", "ጌ", "ግ", "ጎ") to SemiticKey("ገ", "g", "جمل", "/ɡ/", "ج"),
        listOf("ጠ", "ጡ", "ጢ", "ጣ", "ጤ", "ጥ", "ጦ") to SemiticKey("ጠ", "ṭ", "طيت", "/tʼ/", "ط"),
        listOf("ጰ", "ጱ", "ጲ", "ጳ", "ጴ", "ጵ", "ጶ") to SemiticKey("ጰ", "pʼ", "بايت", "/pʼ/", "ف"),
        listOf("ጸ", "ጹ", "ጺ", "ጻ", "ጼ", "ጽ", "ጾ") to SemiticKey("ጸ", "ṣ", "صداي", "/t͡sʼ/", "ص"),
        listOf("ፀ", "ፁ", "ፂ", "ፃ", "ፄ", "ፅ", "ፆ") to SemiticKey("ፀ", "ṣ́", "ضبا", "/ɬʼ/", "ض"),
        listOf("ፈ", "ፉ", "ፊ", "ፋ", "ፌ", "ፍ", "ፎ") to SemiticKey("ፈ", "f", "أف", "/f/", "ف")
      )

      val idx = (geezOrder - 1).coerceIn(0, 6)
      val vowelSuffix = listOf("ä", "u", "i", "ā", "ē", "ə", "o")[idx]
      val consonantsKeys = baseConsonants.map { (orders, baseKey) ->
        val glyph = orders[idx]
        SemiticKey(
          char = glyph,
          transliteration = "${baseKey.transliteration}$vowelSuffix",
          nameAr = "${baseKey.nameAr} (${idx + 1})",
          ipa = "${baseKey.ipa.removeSuffix("/")}$vowelSuffix/",
          arabicEquiv = baseKey.arabicEquiv
        )
      }

      val geezNumerals = listOf(
        SemiticKey("፩", "1", "واحد", "1", "١", 1),
        SemiticKey("፪", "2", "اثنان", "2", "٢", 2),
        SemiticKey("፫", "3", "ثلاثة", "3", "٣", 3),
        SemiticKey("፬", "4", "أربعة", "4", "٤", 4),
        SemiticKey("፭", "5", "خمسة", "5", "٥", 5),
        SemiticKey("፲", "10", "عشرة", "10", "١٠", 10),
        SemiticKey("፳", "20", "عشرون", "20", "٢٠", 20),
        SemiticKey("፻", "100", "مائة", "100", "١٠٠", 100),
        SemiticKey("፡", "•", "نقطتا فصل", "-", ":")
      )

      consonantsKeys + geezNumerals
    }

    SemiticScriptLayout.SAMARITAN -> listOf(
      SemiticKey("ࠀ", "ʾ", "ألف سامرية", "/ʔ/", "أ", 1),
      SemiticKey("ࠁ", "b", "بيت سامرية", "/b/", "ب", 2),
      SemiticKey("ࠂ", "g", "جيمل سامرية", "/ɡ/", "ج", 3),
      SemiticKey("ࠃ", "d", "دالت سامرية", "/d/", "د", 4),
      SemiticKey("ࠄ", "h", "إي سامرية", "/h/", "هـ", 5),
      SemiticKey("ࠅ", "w", "واو سامرية", "/w/", "و", 6),
      SemiticKey("ࠆ", "z", "زاين سامرية", "/z/", "ز", 7),
      SemiticKey("ࠇ", "ḥ", "إيت سامرية", "/ħ/", "ح", 8),
      SemiticKey("ࠈ", "ṭ", "طيت سامرية", "/tʼ/", "ط", 9),
      SemiticKey("ࠉ", "y", "يود سامرية", "/j/", "ي", 10),
      SemiticKey("ࠊ", "k", "كاف سامرية", "/k/", "ك", 20),
      SemiticKey("ࠋ", "l", "لابد سامرية", "/l/", "ل", 30),
      SemiticKey("ࠌ", "m", "ميم سامرية", "/m/", "م", 40),
      SemiticKey("ࠍ", "n", "نون سامرية", "/n/", "ن", 50),
      SemiticKey("ࠎ", "s", "سنكاث سامرية", "/s/", "س", 60),
      SemiticKey("ࠏ", "ʿ", "عين سامرية", "/ʕ/", "ع", 70),
      SemiticKey("ࠐ", "p", "في سامرية", "/p/", "ف", 80),
      SemiticKey("ࠑ", "ṣ", "صادي سامرية", "/sˤ/", "ص", 90),
      SemiticKey("ࠒ", "q", "قوف سامرية", "/q/", "ق", 100),
      SemiticKey("ࠓ", "r", "ريش سامرية", "/r/", "ر", 200),
      SemiticKey("ࠔ", "š", "شان سامرية", "/ʃ/", "ش", 300),
      SemiticKey("ࠕ", "t", "تاف سامرية", "/t/", "ت", 400)
    )

    SemiticScriptLayout.TRANSLITERATION -> listOf(
      SemiticKey("ʾ", "ʾ", "همزة حنجرية", "/ʔ/", "ء"),
      SemiticKey("ʿ", "ʿ", "عين حلقية", "/ʕ/", "ع"),
      SemiticKey("ḥ", "ḥ", "حاء حلقية", "/ħ/", "ح"),
      SemiticKey("ḫ", "ḫ", "خاء طبقية", "/x/", "خ"),
      SemiticKey("ṭ", "ṭ", "طاء مطبقة", "/tʼ/", "ط"),
      SemiticKey("ṣ", "ṣ", "صاد مطبقة", "/sˤ/", "ص"),
      SemiticKey("ṣ́", "ṣ́", "ضاد جانبية قديمة", "/ɬʼ/", "ض"),
      SemiticKey("ḍ", "ḍ", "ضاد معيارية", "/dˤ/", "ض"),
      SemiticKey("ġ", "ġ", "غين لهوية", "/ɣ/", "غ"),
      SemiticKey("š", "š", "شين غارية", "/ʃ/", "ش"),
      SemiticKey("ś", "ś", "شين جانبية", "/ɬ/", "س"),
      SemiticKey("ṯ", "ṯ", "ثاء بين أسنانية", "/θ/", "ث"),
      SemiticKey("ḏ", "ḏ", "ذال بين أسنانية", "/ð/", "ذ"),
      SemiticKey("ẓ", "ẓ", "ظاء مطبقة", "/ðʼ/", "ظ"),
      SemiticKey("ā", "ā", "ألف مد طويلة", "/aː/", "آ"),
      SemiticKey("ī", "ī", "ياء مد طويلة", "/iː/", "ي"),
      SemiticKey("ū", "ū", "واو مد طويلة", "/uː/", "و"),
      SemiticKey("ē", "ē", "إي كنعانية مائلة", "/eː/", "ـيـ"),
      SemiticKey("ō", "ō", "أو التحول الكنعاني", "/oː/", "ـوـ"),
      SemiticKey("ʔ", "ʔ", "صوت حنجري IPA", "/ʔ/", "ء"),
      SemiticKey("ʕ", "ʕ", "صوت حلقي IPA", "/ʕ/", "ع"),
      SemiticKey("ɬʼ", "ɬʼ", "صوت ضاد المسند IPA", "/ɬʼ/", "ض"),
      SemiticKey("sˤ", "sˤ", "صوت صاد مطبق IPA", "/sˤ/", "ص"),
      SemiticKey("tʼ", "tʼ", "صوت طاء قذفي IPA", "/tʼ/", "ط")
    )
  }
}
