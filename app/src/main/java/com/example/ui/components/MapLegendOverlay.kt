package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ArchaeologicalSite

/**
 * Chronological periods for Semitic archaeological sites and inscriptions.
 */
enum class ArchaeologicalPeriod(
  val id: String,
  val titleAr: String,
  val titleEn: String,
  val timeRangeAr: String,
  val color: Color,
  val markerHue: Float,
  val descriptionAr: String
) {
  EARLY_BRONZE(
    id = "bronze_early",
    titleAr = "العصر البرونزي المبكر والوسيط",
    titleEn = "Early & Middle Bronze Age",
    timeRangeAr = "2500 – 1600 ق.م",
    color = Color(0xFF1E88E5), // Indigo / Azure
    markerHue = 210f, // HUE_AZURE
    descriptionAr = "بدايات التدوين المسماري الإيبلاوي والأكادي القديم ومناجم الفيروز بالسينائي المبكر في سيناء."
  ),
  LATE_BRONZE(
    id = "bronze_late",
    titleAr = "العصر البرونزي المتأخر",
    titleEn = "Late Bronze Age",
    timeRangeAr = "1600 – 1200 ق.م",
    color = Color(0xFF00ACC1), // Cyan / Teal
    markerHue = 180f, // HUE_CYAN
    descriptionAr = "عصر المراسلات الدبلوماسية وأبجدية أوغاريت المسمارية الثلاثينية وبدايات جبيل الكنعانية."
  ),
  IRON_AGE(
    id = "iron_age",
    titleAr = "العصر الحديدي وعصر الممالك الكبرى",
    titleEn = "Iron Age Kingdoms",
    timeRangeAr = "1200 – 500 ق.م",
    color = Color(0xFFFB8C00), // Amber / Orange
    markerHue = 30f, // HUE_ORANGE
    descriptionAr = "ازدهار الأبجدية الفينيقية الخطية، الآرامية القديمة، مسلة ميشع، شواهد لخيش، ومكتبة نينوى العظمى."
  ),
  CLASSICAL_NABATAEAN(
    id = "classical",
    titleAr = "العصر الهلنستي والنبطي والسبئي",
    titleEn = "Hellenistic & Nabataean Era",
    timeRangeAr = "500 ق.م – 100 م",
    color = Color(0xFF8E24AA), // Violet / Purple
    markerHue = 270f, // HUE_VIOLET
    descriptionAr = "حواضر البتراء والحِجر وقرطاج، وازدهار خط المسند السبئي والزبور والتجارة البخورية عبر الجزيرة."
  ),
  LATE_ANTIQUITY(
    id = "late_antique",
    titleAr = "العصر الكلاسيكي المتأخر وما قبل الإسلام",
    titleEn = "Late Antiquity & Pre-Islamic",
    timeRangeAr = "100 – 650 م",
    color = Color(0xFF2E7D32), // Emerald Green
    markerHue = 120f, // HUE_GREEN
    descriptionAr = "نقوش تدمر، مرويات ملوك أكسوم بالجعزية، النقوش الحميرية المتأخرة، والإرهاصات العربية المبكرة."
  )
}

/**
 * Functional classifications for archaeological sites.
 */
enum class ArchaeologicalSiteCategory(
  val id: String,
  val icon: String,
  val titleAr: String,
  val descriptionAr: String
) {
  CAPITAL(
    id = "capital",
    icon = "🏛️",
    titleAr = "عاصمة إمبراطورية ومقر حكم ملكي",
    descriptionAr = "مراكز القرار السياسي والأرشيفات القانونية والملكية الإمبراطورية (بابل، نينوى، إيبلا، البتراء، أكسوم)."
  ),
  PORT_COMMERCE(
    id = "port",
    icon = "⛵",
    titleAr = "ميناء بحري وتجارة متوسطية",
    descriptionAr = "حواضر التبادل الحضاري والموانئ التجارية وتداول الأبجديات المبكرة (جبيل، أوغاريت، قرطاج)."
  ),
  TEMPLE_SANCTUARY(
    id = "temple",
    icon = "🛕",
    titleAr = "معبد ومحج نذري مقدس",
    descriptionAr = "منشآت العبادة السامية والنذور الإلهية وشواهد السدود العظيمة (مأرب، صرواح، معبد يحا الإثيوبي)."
  ),
  CARAVAN_OASIS(
    id = "caravan",
    icon = "🐪",
    titleAr = "واحة قوافل ونقوش صخرية",
    descriptionAr = "محطات مسارات اللبان والبخور وطرق القوافل الصحراوية والتجارة الدولية (تيماء، الحِجر، تدمر)."
  ),
  MINING_PIONEER(
    id = "mining",
    icon = "⛏️",
    titleAr = "مناجم وأبجديات صوتية رائدة",
    descriptionAr = "مناجم استخراج الفيروز والمعادن ومراكز ولادة الأبجديات الصوتية والشواهد العسكرية (سرابيط الخادم، تل الدوير، قره تبه)."
  )
}

/**
 * Epigraphic script families.
 */
enum class EpigraphicScriptFamily(
  val id: String,
  val glyph: String,
  val titleAr: String,
  val color: Color,
  val descriptionAr: String
) {
  CUNEIFORM(
    id = "cuneiform",
    glyph = "楔",
    titleAr = "الخط المسماري السامي",
    color = Color(0xFF5D4037),
    descriptionAr = "الخط الأكادي، البابلي، الآشوري، الأوغاريتي الأبجدي، والإيبلاوي."
  ),
  PHOENICIAN(
    id = "phoenician",
    glyph = "𐤀",
    titleAr = "الأبجدية الفينيقية والكنعانية",
    color = Color(0xFF1565C0),
    descriptionAr = "الأبجدية الخطية الكنعانية ذات الـ 22 حرفاً في جبيل وقرطاج ومؤاب ولخيش."
  ),
  MUSNAD(
    id = "musnad",
    glyph = "𐩱",
    titleAr = "خط المسند والزبور العربي الجنوبي",
    color = Color(0xFFC2185B),
    descriptionAr = "نقوش ممالك سبأ ومعين وقتبان وحضرموت وحمير ومحرم بلقيس."
  ),
  NABATAEAN_ARAMAIC(
    id = "nabataean",
    glyph = "𐡀",
    titleAr = "الخط الآرامي والنبطي والتدمري",
    color = Color(0xFF7B1FA2),
    descriptionAr = "خط الإدارة والتجارة الدولي وحواضر البتراء وتدمر والحجر وتيماء."
  ),
  GEEZ(
    id = "geez",
    glyph = "አ",
    titleAr = "الخط الجعزي الإثيوبي (الفيدل)",
    color = Color(0xFF2E7D32),
    descriptionAr = "الخط المقطعي المصوت لمملكة أكسوم ومعبد يحا."
  ),
  PROTO_SINAITIC(
    id = "sinaitic",
    glyph = "𓀀",
    titleAr = "الخط السينائي الأولي",
    color = Color(0xFFE65100),
    descriptionAr = "أقدم أبجدية صوتية مشتقة من الهيروغليفية في سرابيط الخادم."
  )
}

/**
 * Helper to determine a site's archaeological period.
 */
fun getSitePeriod(site: ArchaeologicalSite): ArchaeologicalPeriod {
  return when (site.id) {
    "site-ebla", "site-serabit", "site-babylon" -> ArchaeologicalPeriod.EARLY_BRONZE
    "site-ugarit", "site-byblos" -> ArchaeologicalPeriod.LATE_BRONZE
    "site-dhiban", "site-nineveh", "site-lachish", "site-karatepe", "site-yeha", "site-tayma" -> ArchaeologicalPeriod.IRON_AGE
    "site-petra", "site-carthage", "site-hegra", "site-marib" -> ArchaeologicalPeriod.CLASSICAL_NABATAEAN
    "site-aksum", "site-palmyra" -> ArchaeologicalPeriod.LATE_ANTIQUITY
    else -> {
      if (site.periodAr.contains("2500") || site.periodAr.contains("2000") || site.periodAr.contains("1800")) {
        ArchaeologicalPeriod.EARLY_BRONZE
      } else if (site.periodAr.contains("1400") || site.periodAr.contains("1185") || site.periodAr.contains("البرونزي")) {
        ArchaeologicalPeriod.LATE_BRONZE
      } else if (site.periodAr.contains("التاسع") || site.periodAr.contains("الثامن") || site.periodAr.contains("السادس") || site.periodAr.contains("612") || site.periodAr.contains("840")) {
        ArchaeologicalPeriod.IRON_AGE
      } else if (site.periodAr.contains("الرابع ق.م") || site.periodAr.contains("الأول ق.م") || site.periodAr.contains("الأنباط")) {
        ArchaeologicalPeriod.CLASSICAL_NABATAEAN
      } else {
        ArchaeologicalPeriod.LATE_ANTIQUITY
      }
    }
  }
}

/**
 * Helper to determine a site's category.
 */
fun getSiteCategory(site: ArchaeologicalSite): ArchaeologicalSiteCategory {
  return when (site.id) {
    "site-babylon", "site-nineveh", "site-ebla", "site-petra", "site-aksum", "site-dhiban" ->
      ArchaeologicalSiteCategory.CAPITAL
    "site-byblos", "site-ugarit", "site-carthage" ->
      ArchaeologicalSiteCategory.PORT_COMMERCE
    "site-marib", "site-yeha" ->
      ArchaeologicalSiteCategory.TEMPLE_SANCTUARY
    "site-hegra", "site-tayma", "site-palmyra" ->
      ArchaeologicalSiteCategory.CARAVAN_OASIS
    "site-serabit", "site-lachish", "site-karatepe" ->
      ArchaeologicalSiteCategory.MINING_PIONEER
    else -> ArchaeologicalSiteCategory.CAPITAL
  }
}

/**
 * Helper to determine a site's primary script family.
 */
fun getSiteScriptFamily(site: ArchaeologicalSite): EpigraphicScriptFamily {
  return when {
    site.id == "site-serabit" -> EpigraphicScriptFamily.PROTO_SINAITIC
    site.id in listOf("site-babylon", "site-nineveh", "site-ebla") -> EpigraphicScriptFamily.CUNEIFORM
    site.id == "site-ugarit" -> EpigraphicScriptFamily.CUNEIFORM
    site.id in listOf("site-byblos", "site-dhiban", "site-carthage", "site-lachish", "site-karatepe") -> EpigraphicScriptFamily.PHOENICIAN
    site.id in listOf("site-marib", "site-yeha") -> EpigraphicScriptFamily.MUSNAD
    site.id == "site-aksum" -> EpigraphicScriptFamily.GEEZ
    site.id in listOf("site-petra", "site-hegra", "site-palmyra", "site-tayma") -> EpigraphicScriptFamily.NABATAEAN_ARAMAIC
    else -> EpigraphicScriptFamily.PHOENICIAN
  }
}

/**
 * Filter state for interactive map legend.
 */
sealed class LegendFilter {
  data class Period(val period: ArchaeologicalPeriod) : LegendFilter()
  data class Category(val category: ArchaeologicalSiteCategory) : LegendFilter()
  data class Script(val script: EpigraphicScriptFamily) : LegendFilter()

  val titleAr: String
    get() = when (this) {
      is Period -> period.titleAr
      is Category -> "${category.icon} ${category.titleAr}"
      is Script -> "${script.glyph} ${script.titleAr}"
    }

  val color: Color
    get() = when (this) {
      is Period -> period.color
      is Category -> Color(0xFFD97706)
      is Script -> script.color
    }
}

/**
 * Compact floating button placed on the map to trigger or collapse the interactive legend.
 */
@Composable
fun MapLegendTriggerPill(
  isExpanded: Boolean,
  activeFilter: LegendFilter?,
  matchingCount: Int,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    onClick = onClick,
    shape = RoundedCornerShape(24.dp),
    color = if (activeFilter != null) {
      activeFilter.color.copy(alpha = 0.92f)
    } else {
      MaterialTheme.colorScheme.surface.copy(alpha = 0.90f)
    },
    shadowElevation = 4.dp,
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (activeFilter != null) Color.White.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    ),
    modifier = modifier.testTag("btn_toggle_map_legend")
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Box(
        modifier = Modifier
          .size(10.dp)
          .clip(CircleShape)
          .background(
            if (activeFilter != null) Color.White else MaterialTheme.colorScheme.primary
          )
      )

      Text(
        text = if (activeFilter != null) {
          "${activeFilter.titleAr} (${matchingCount})"
        } else {
          "دليل الرموز والحقب"
        },
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = if (activeFilter != null) Color.White else MaterialTheme.colorScheme.onSurface
      )

      Icon(
        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.MenuBook,
        contentDescription = if (isExpanded) "إخفاء الدليل" else "فتح الدليل التفاعلي",
        tint = if (activeFilter != null) Color.White else MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}

/**
 * Full interactive legend overlay explaining symbols and time periods of archaeological inscriptions.
 */
@Composable
fun InteractiveMapLegendOverlay(
  allSites: List<ArchaeologicalSite>,
  activeFilter: LegendFilter?,
  onFilterChange: (LegendFilter?) -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("الحقب الزمنية", "رموز المواقع", "عائلات الخطوط")

  Surface(
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
    shadowElevation = 8.dp,
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
    modifier = modifier
      .fillMaxWidth()
      .padding(8.dp)
      .testTag("interactive_map_legend_overlay")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp)
    ) {
      // Header Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Explore,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = "دليل الرموز والحقب الأثرية",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          if (activeFilter != null) {
            TextButton(
              onClick = { onFilterChange(null) },
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text("عرض الكل", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }

          IconButton(
            onClick = onClose,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              Icons.Default.Close,
              contentDescription = "إغلاق الدليل",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Text(
        text = "انقر على أي حقبة زمنية أو رمز أثري لتصفية الخريطة وإبراز المكتشفات المرتبطة بها.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 16.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Tab selector
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = { HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant) }
      ) {
        tabs.forEachIndexed { index, tabTitle ->
          Tab(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            text = {
              Text(
                text = tabTitle,
                fontSize = 11.sp,
                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
              )
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Tab Content
      Box(modifier = Modifier.heightIn(max = 240.dp)) {
        when (selectedTab) {
          0 -> PeriodsLegendTab(
            allSites = allSites,
            activeFilter = activeFilter,
            onFilterChange = onFilterChange
          )
          1 -> CategoriesLegendTab(
            allSites = allSites,
            activeFilter = activeFilter,
            onFilterChange = onFilterChange
          )
          2 -> ScriptsLegendTab(
            allSites = allSites,
            activeFilter = activeFilter,
            onFilterChange = onFilterChange
          )
        }
      }
    }
  }
}

/**
 * Tab 1: Time periods and epochs.
 */
@Composable
private fun PeriodsLegendTab(
  allSites: List<ArchaeologicalSite>,
  activeFilter: LegendFilter?,
  onFilterChange: (LegendFilter?) -> Unit
) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(6.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    items(ArchaeologicalPeriod.values()) { period ->
      val count = allSites.count { getSitePeriod(it) == period }
      val isSelected = activeFilter is LegendFilter.Period && activeFilter.period == period

      Surface(
        onClick = {
          if (isSelected) {
            onFilterChange(null)
          } else {
            onFilterChange(LegendFilter.Period(period))
          }
        },
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) period.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(
          if (isSelected) 1.5.dp else 0.5.dp,
          if (isSelected) period.color else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Color pill indicator
          Box(
            modifier = Modifier
              .size(16.dp)
              .clip(CircleShape)
              .background(period.color),
            contentAlignment = Alignment.Center
          ) {
            if (isSelected) {
              Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = period.titleAr,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = period.color.copy(alpha = 0.2f)
              ) {
                Text(
                  text = period.timeRangeAr,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = period.color,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
              }
            }

            Text(
              text = period.descriptionAr,
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 14.sp
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          // Sites badge
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) period.color else MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = "$count",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }
    }
  }
}

/**
 * Tab 2: Site categories and symbols.
 */
@Composable
private fun CategoriesLegendTab(
  allSites: List<ArchaeologicalSite>,
  activeFilter: LegendFilter?,
  onFilterChange: (LegendFilter?) -> Unit
) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(6.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    items(ArchaeologicalSiteCategory.values()) { category ->
      val count = allSites.count { getSiteCategory(it) == category }
      val isSelected = activeFilter is LegendFilter.Category && activeFilter.category == category

      Surface(
        onClick = {
          if (isSelected) {
            onFilterChange(null)
          } else {
            onFilterChange(LegendFilter.Category(category))
          }
        },
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(
          if (isSelected) 1.5.dp else 0.5.dp,
          if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = category.icon,
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
          )

          Spacer(modifier = Modifier.width(6.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = category.titleAr,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = category.descriptionAr,
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 14.sp
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = "$count",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }
    }
  }
}

/**
 * Tab 3: Epigraphic script families.
 */
@Composable
private fun ScriptsLegendTab(
  allSites: List<ArchaeologicalSite>,
  activeFilter: LegendFilter?,
  onFilterChange: (LegendFilter?) -> Unit
) {
  LazyColumn(
    verticalArrangement = Arrangement.spacedBy(6.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    items(EpigraphicScriptFamily.values()) { script ->
      val count = allSites.count { getSiteScriptFamily(it) == script }
      val isSelected = activeFilter is LegendFilter.Script && activeFilter.script == script

      Surface(
        onClick = {
          if (isSelected) {
            onFilterChange(null)
          } else {
            onFilterChange(LegendFilter.Script(script))
          }
        },
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) script.color.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = androidx.compose.foundation.BorderStroke(
          if (isSelected) 1.5.dp else 0.5.dp,
          if (isSelected) script.color else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = script.color.copy(alpha = 0.2f),
            modifier = Modifier.size(28.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = script.glyph,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = script.color
              )
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = script.titleAr,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = script.descriptionAr,
              fontSize = 10.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 14.sp
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) script.color else MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = "$count",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }
    }
  }
}
