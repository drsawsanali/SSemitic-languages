package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AcademicInscription
import com.example.data.models.ArchaeologicalSite
import com.example.data.repository.InscriptionsData
import com.example.data.repository.LexiconAndPhoneticsData
import com.example.ui.components.ArchaeologicalPeriod
import com.example.ui.components.ArchaeologicalSiteCategory
import com.example.ui.components.EpigraphicScriptFamily
import com.example.ui.components.InteractiveMapLegendOverlay
import com.example.ui.components.LegendFilter
import com.example.ui.components.MapLegendTriggerPill
import com.example.ui.components.getSiteCategory
import com.example.ui.components.getSitePeriod
import com.example.ui.components.getSiteScriptFamily
import com.example.ui.theme.LocalCustomColors
import com.example.util.AudioEngine
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@Composable
fun MapExplorerScreen(
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val customColors = LocalCustomColors.current
  val coroutineScope = rememberCoroutineScope()
  val audioEngine = remember { AudioEngine(context) }

  var searchQuery by remember { mutableStateOf("") }
  var selectedSite by remember { mutableStateOf<ArchaeologicalSite?>(LexiconAndPhoneticsData.sites.firstOrNull()) }
  var selectedRegionFilter by remember { mutableStateOf("الكل") }
  var mapType by remember { mutableStateOf(MapType.NORMAL) }
  var isMapExpanded by remember { mutableStateOf(false) }
  var isLegendOpen by remember { mutableStateOf(false) }
  var activeLegendFilter by remember { mutableStateOf<LegendFilter?>(null) }

  // Default camera centering the Ancient Semitic world (Mesopotamia, Levant, Arabia, Horn of Africa)
  val defaultInitialCenter = remember { LatLng(26.0, 39.0) }
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(defaultInitialCenter, 4.3f)
  }

  val regions = listOf(
    "الكل",
    "بلاد الرافدين",
    "بلاد الشام",
    "شبه الجزيرة واليمن",
    "إثيوبيا والحبشة",
    "مصر وسيناء",
    "شمال إفريقيا"
  )

  fun matchesRegion(site: ArchaeologicalSite, region: String): Boolean {
    return when (region) {
      "بلاد الرافدين" -> site.country.contains("العراق")
      "بلاد الشام" -> site.country.contains("سوريا") || site.country.contains("لبنان") || site.country.contains("الأردن") || site.country.contains("فلسطين") || site.country.contains("تركيا")
      "شبه الجزيرة واليمن" -> site.country.contains("السعودية") || site.country.contains("اليمن")
      "إثيوبيا والحبشة" -> site.country.contains("إثيوبيا")
      "مصر وسيناء" -> site.country.contains("مصر")
      "شمال إفريقيا" -> site.country.contains("تونس")
      else -> true
    }
  }

  val filteredSites = remember(searchQuery, selectedRegionFilter, activeLegendFilter) {
    LexiconAndPhoneticsData.sites.filter { s ->
      val matchesSearch = searchQuery.isBlank() ||
        s.nameAr.contains(searchQuery, ignoreCase = true) ||
        s.nameEn.contains(searchQuery, ignoreCase = true) ||
        s.country.contains(searchQuery, ignoreCase = true) ||
        s.primaryInscriptions.any { it.contains(searchQuery, ignoreCase = true) } ||
        s.scriptFamilies.any { it.contains(searchQuery, ignoreCase = true) }

      val matchesReg = matchesRegion(s, selectedRegionFilter)

      val matchesLegend = when (val filter = activeLegendFilter) {
        null -> true
        is LegendFilter.Period -> getSitePeriod(s) == filter.period
        is LegendFilter.Category -> getSiteCategory(s) == filter.category
        is LegendFilter.Script -> getSiteScriptFamily(s) == filter.script
      }

      matchesSearch && matchesReg && matchesLegend
    }
  }

  val mapHeight by animateDpAsState(
    targetValue = when {
      isLegendOpen -> 490.dp
      isMapExpanded -> 440.dp
      else -> 280.dp
    },
    label = "MapHeightAnimation"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header
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
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "الأطلس الأثري ومراكز النقوش السامية",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "خريطة تفاعلية عبر Google Maps لتحديد مواقع المكتشفات الإبيغرافية",
              style = MaterialTheme.typography.bodySmall,
              color = customColors.mutedText
            )
          }

          IconButton(
            onClick = {
              coroutineScope.launch {
                cameraPositionState.animate(
                  CameraUpdateFactory.newLatLngZoom(defaultInitialCenter, 4.3f),
                  800
                )
              }
            }
          ) {
            Icon(
              Icons.Default.Public,
              contentDescription = "عرض شامل للمنطقة السامية",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          modifier = Modifier.fillMaxWidth(),
          placeholder = { Text("بحث في المواقع والنقوش (بابل، أوغاريت، جبيل، مأرب، تيماء...)") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Close, contentDescription = "مسح البحث")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Region Filter Chips
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(regions) { region ->
            val isSelected = selectedRegionFilter == region
            FilterChip(
              selected = isSelected,
              onClick = { selectedRegionFilter = region },
              label = { Text(region, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }
    }

    // Google Maps Section
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(mapHeight)
        .padding(horizontal = 12.dp, vertical = 6.dp)
        .clip(RoundedCornerShape(16.dp))
        .border(1.dp, customColors.border, RoundedCornerShape(16.dp))
    ) {
      val uiSettings = remember {
        MapUiSettings(
          zoomControlsEnabled = false,
          compassEnabled = true,
          myLocationButtonEnabled = false,
          mapToolbarEnabled = true
        )
      }

      val properties = remember(mapType) {
        MapProperties(
          mapType = mapType,
          isBuildingEnabled = true
        )
      }

      GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings,
        onMapClick = {
          // Keep selection
        }
      ) {
        filteredSites.forEach { site ->
          val sitePeriod = remember(site) { getSitePeriod(site) }
          val siteCategory = remember(site) { getSiteCategory(site) }
          val markerState = rememberMarkerState(
            key = site.id,
            position = LatLng(site.latitude, site.longitude)
          )

          Marker(
            state = markerState,
            title = "${siteCategory.icon} ${site.nameAr}",
            snippet = "${site.country} • ${sitePeriod.timeRangeAr}",
            icon = BitmapDescriptorFactory.defaultMarker(sitePeriod.markerHue),
            onClick = {
              selectedSite = site
              coroutineScope.launch {
                cameraPositionState.animate(
                  CameraUpdateFactory.newLatLngZoom(
                    LatLng(site.latitude, site.longitude),
                    8.5f
                  ),
                  700
                )
              }
              it.showInfoWindow()
              true
            }
          )
        }
      }

      // Interactive Map Legend Trigger Pill (Top Start)
      MapLegendTriggerPill(
        isExpanded = isLegendOpen,
        activeFilter = activeLegendFilter,
        matchingCount = filteredSites.size,
        onClick = { isLegendOpen = !isLegendOpen },
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(8.dp)
      )

      // Interactive Map Legend Overlay Card (Top Center)
      androidx.compose.animation.AnimatedVisibility(
        visible = isLegendOpen,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(top = 44.dp, start = 8.dp, end = 8.dp)
      ) {
        InteractiveMapLegendOverlay(
          allSites = LexiconAndPhoneticsData.sites,
          activeFilter = activeLegendFilter,
          onFilterChange = { filter ->
            activeLegendFilter = filter
            if (filter != null) {
              val matching = LexiconAndPhoneticsData.sites.filter { s ->
                when (filter) {
                  is LegendFilter.Period -> getSitePeriod(s) == filter.period
                  is LegendFilter.Category -> getSiteCategory(s) == filter.category
                  is LegendFilter.Script -> getSiteScriptFamily(s) == filter.script
                }
              }
              matching.firstOrNull()?.let { firstSite ->
                selectedSite = firstSite
                coroutineScope.launch {
                  cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                      LatLng(firstSite.latitude, firstSite.longitude),
                      6.0f
                    ),
                    700
                  )
                }
              }
            }
          },
          onClose = { isLegendOpen = false }
        )
      }

      // Map Overlay Controls
      Column(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        // Expand/Collapse Map
        FilledTonalIconButton(
          onClick = { isMapExpanded = !isMapExpanded },
          shape = CircleShape,
          modifier = Modifier.size(38.dp)
        ) {
          Icon(
            if (isMapExpanded) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
            contentDescription = if (isMapExpanded) "تصغير الخريطة" else "توسيع الخريطة",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }

        // Map Type Switcher
        FilledTonalIconButton(
          onClick = {
            mapType = when (mapType) {
              MapType.NORMAL -> MapType.TERRAIN
              MapType.TERRAIN -> MapType.SATELLITE
              MapType.SATELLITE -> MapType.NORMAL
              else -> MapType.NORMAL
            }
          },
          shape = CircleShape,
          modifier = Modifier.size(38.dp)
        ) {
          Icon(
            Icons.Default.Layers,
            contentDescription = "تبديل نوع الخريطة",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }

        // Zoom In
        FilledTonalIconButton(
          onClick = {
            coroutineScope.launch {
              cameraPositionState.animate(CameraUpdateFactory.zoomIn(), 400)
            }
          },
          shape = CircleShape,
          modifier = Modifier.size(38.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = "تكبير", modifier = Modifier.size(20.dp))
        }

        // Zoom Out
        FilledTonalIconButton(
          onClick = {
            coroutineScope.launch {
              cameraPositionState.animate(CameraUpdateFactory.zoomOut(), 400)
            }
          },
          shape = CircleShape,
          modifier = Modifier.size(38.dp)
        ) {
          Icon(Icons.Default.Remove, contentDescription = "تصغير", modifier = Modifier.size(20.dp))
        }
      }

      // Map Type & Sites Counter Badge + Active Filter Reset
      Surface(
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
        shadowElevation = 2.dp
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            Icons.Default.Place,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${filteredSites.size} موقع أثري • ${
              when (mapType) {
                MapType.NORMAL -> "خريطة قياسية"
                MapType.TERRAIN -> "تضاريس طبوغرافية"
                MapType.SATELLITE -> "صور فضائية"
                else -> "طبيعي"
              }
            }",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )

          if (activeLegendFilter != null) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              onClick = { activeLegendFilter = null },
              shape = RoundedCornerShape(6.dp),
              color = activeLegendFilter!!.color.copy(alpha = 0.18f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "تصفية: ${activeLegendFilter!!.titleAr}",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = activeLegendFilter!!.color
                )
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                  Icons.Default.Close,
                  contentDescription = "إلغاء التصفية",
                  tint = activeLegendFilter!!.color,
                  modifier = Modifier.size(12.dp)
                )
              }
            }
          }
        }
      }
    }

    // Sites Quick Carousel
    LazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      items(filteredSites) { site ->
        val isSelected = selectedSite?.id == site.id
        val sitePeriod = remember(site) { getSitePeriod(site) }
        val siteCategory = remember(site) { getSiteCategory(site) }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = if (isSelected) MaterialTheme.colorScheme.primary else customColors.cardBackground,
          shadowElevation = if (isSelected) 3.dp else 1.dp,
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .border(
              1.dp,
              if (isSelected) MaterialTheme.colorScheme.primary else customColors.border,
              RoundedCornerShape(10.dp)
            )
            .clickable {
              selectedSite = site
              coroutineScope.launch {
                cameraPositionState.animate(
                  CameraUpdateFactory.newLatLngZoom(
                    LatLng(site.latitude, site.longitude),
                    8.5f
                  ),
                  700
                )
              }
            }
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color.White else sitePeriod.color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = siteCategory.icon,
              fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column {
              Text(
                text = site.nameAr,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${site.country} • ${sitePeriod.timeRangeAr}",
                fontSize = 9.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.85f) else customColors.mutedText
              )
            }
          }
        }
      }
    }

    // Selected Site Details Sheet
    selectedSite?.let { site ->
      val matchingInscriptions = remember(site) {
        InscriptionsData.list.filter { inscr ->
          inscr.site.contains(site.nameEn, ignoreCase = true) ||
            inscr.site.contains(site.nameAr, ignoreCase = true) ||
            site.primaryInscriptions.any { pri -> inscr.titleAr.contains(pri, ignoreCase = true) || pri.contains(inscr.title, ignoreCase = true) } ||
            (site.id == "site-babylon" && inscr.id.startsWith("BAB-")) ||
            (site.id == "site-nineveh" && inscr.id.startsWith("ASS-")) ||
            (site.id == "site-ugarit" && inscr.id.startsWith("UGA-")) ||
            (site.id == "site-byblos" && inscr.id.startsWith("PHO-")) ||
            (site.id == "site-dhiban" && inscr.id.startsWith("MOA-")) ||
            (site.id == "site-marib" && inscr.id.startsWith("SAB-")) ||
            (site.id == "site-hegra" && inscr.id.startsWith("NAB-")) ||
            (site.id == "site-aksum" && inscr.id.startsWith("GEEZ-")) ||
            (site.id == "site-palmyra" && inscr.id.startsWith("PALM-")) ||
            (site.id == "site-carthage" && inscr.id.startsWith("PUN-")) ||
            (site.id == "site-serabit" && inscr.id.startsWith("SINAITIC-"))
        }
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Site Overview Card
        item {
          val sitePeriod = remember(site) { getSitePeriod(site) }
          val siteCategory = remember(site) { getSiteCategory(site) }
          val siteScript = remember(site) { getSiteScriptFamily(site) }

          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = siteCategory.icon,
                      fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = site.nameAr,
                      style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  }
                  Text(
                    text = "${site.nameEn} • ${site.country}",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.mutedText
                  )
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = sitePeriod.color.copy(alpha = 0.18f),
                  border = androidx.compose.foundation.BorderStroke(1.dp, sitePeriod.color.copy(alpha = 0.5f))
                ) {
                  Text(
                    text = sitePeriod.timeRangeAr,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = sitePeriod.color,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Badges row: Period, Category, Script
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = sitePeriod.color.copy(alpha = 0.12f)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(sitePeriod.color)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = sitePeriod.titleAr,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = sitePeriod.color
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                  Text(
                    text = siteCategory.titleAr,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                  )
                }

                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = siteScript.color.copy(alpha = 0.12f)
                ) {
                  Text(
                    text = "${siteScript.glyph} ${siteScript.titleAr}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = siteScript.color,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = site.descriptionAr,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
              )

              Spacer(modifier = Modifier.height(10.dp))

              // Coordinates and Navigation button
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    Icons.Default.Explore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "الإحداثيات: ${String.format("%.4f", site.latitude)}° N, ${String.format("%.4f", site.longitude)}° E",
                    style = MaterialTheme.typography.labelSmall,
                    color = customColors.mutedText
                  )
                }

                OutlinedButton(
                  onClick = {
                    coroutineScope.launch {
                      cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(
                          LatLng(site.latitude, site.longitude),
                          10.5f
                        ),
                        800
                      )
                    }
                  },
                  contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Icon(Icons.Default.CenterFocusStrong, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("تركيز الكاميرا", fontSize = 11.sp)
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Script Families
              Text(
                text = "أنظمة الخطوط الإبيغرافية:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                site.scriptFamilies.forEach { script ->
                  AssistChip(
                    onClick = {},
                    label = { Text(script, fontSize = 10.sp) },
                    leadingIcon = {
                      Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                    }
                  )
                }
              }
            }
          }
        }

        // Section: Inscriptions Discovered at this Site
        item {
          Text(
            text = "النقوش والمكتشفات الإبيغرافية في هذا الموقع (${site.primaryInscriptions.size}):",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 4.dp)
          )
        }

        // Detailed matched academic inscriptions
        if (matchingInscriptions.isNotEmpty()) {
          items(matchingInscriptions) { inscr ->
            AcademicInscriptionCard(
              inscription = inscr,
              onSpeak = { text ->
                audioEngine.speak(text)
              },
              onCopy = { text ->
                clipboardManager.setText(AnnotatedString(text))
              }
            )
          }
        }

        // Primary inscriptions list (fallback/complement)
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, customColors.border)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "سجل اللقى والشواهد المسجلة في ${site.nameAr}:",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(6.dp))

              site.primaryInscriptions.forEach { inscrText ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.AutoMirrored.Filled.Article,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = inscrText,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                  )
                  IconButton(
                    onClick = {
                      clipboardManager.setText(AnnotatedString(inscrText))
                    },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(
                      Icons.Default.ContentCopy,
                      contentDescription = "نسخ الاسم",
                      tint = customColors.mutedText,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                }
              }
            }
          }
        }

        item {
          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    }
  }
}

@Composable
fun AcademicInscriptionCard(
  inscription: AcademicInscription,
  onSpeak: (String) -> Unit,
  onCopy: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current
  var isExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ),
    border = androidx.compose.foundation.BorderStroke(1.dp, customColors.border)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = inscription.titleAr,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${inscription.language} (${inscription.family}) • ${inscription.script}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
          )
        }

        Row {
          IconButton(
            onClick = { onSpeak(inscription.translationAr) },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              Icons.AutoMirrored.Filled.VolumeUp,
              contentDescription = "استماع للترجمة",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
          }

          IconButton(
            onClick = {
              onCopy("${inscription.titleAr}\n${inscription.transliteration}\n${inscription.translationAr}")
            },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              Icons.Default.ContentCopy,
              contentDescription = "نسخ النص",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
          }

          IconButton(
            onClick = { isExpanded = !isExpanded },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
              contentDescription = if (isExpanded) "طي" else "توسيع",
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      // Original Script Text if available
      if (inscription.originalScriptText.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = inscription.originalScriptText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }
      }

      // Transliteration
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "النقحرة الصوتية: ${inscription.transliteration}",
        style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
        color = customColors.mutedText,
        lineHeight = 16.sp
      )

      // Arabic Translation
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "الترجمة العربية: ${inscription.translationAr}",
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 18.sp
      )

      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 8.dp)) {
          HorizontalDivider(color = customColors.border, thickness = 0.5.dp)
          Spacer(modifier = Modifier.height(6.dp))

          if (inscription.ruler.isNotBlank()) {
            Text(
              text = "الحاكم / الملك المرتبط: ${inscription.ruler}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.secondary
            )
          }
          if (inscription.material.isNotBlank()) {
            Text(
              text = "المادة والخامة: ${inscription.material}",
              style = MaterialTheme.typography.labelSmall,
              color = customColors.mutedText
            )
          }
          if (inscription.corpus.isNotBlank()) {
            Text(
              text = "المدونة / المرجع: ${inscription.corpus}",
              style = MaterialTheme.typography.labelSmall,
              color = customColors.mutedText
            )
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "الملاحظات الفيلولوجية: ${inscription.notesAr}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 18.sp
          )
        }
      }
    }
  }
}

