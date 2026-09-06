package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.SemiticBranch
import com.example.data.models.SemiticLanguage
import com.example.data.repository.EncyclopediaRepository
import com.example.ui.theme.LocalCustomColors

@Composable
fun LanguagesScreen(
  modifier: Modifier = Modifier,
  onLanguageSelected: (SemiticLanguage) -> Unit = {}
) {
  val customColors = LocalCustomColors.current
  val allLanguages = remember { EncyclopediaRepository.getAllLanguages() }

  var searchQuery by remember { mutableStateOf("") }
  var selectedBranch by remember { mutableStateOf<SemiticBranch?>(null) }
  var expandedLanguageId by remember { mutableStateOf<String?>(null) }

  val filteredList = remember(searchQuery, selectedBranch) {
    allLanguages.filter { lang ->
      val matchesBranch = selectedBranch == null || lang.branch == selectedBranch
      val matchesSearch = searchQuery.isBlank() ||
        lang.ar.contains(searchQuery, ignoreCase = true) ||
        lang.en.contains(searchQuery, ignoreCase = true) ||
        lang.geographyAr.contains(searchQuery, ignoreCase = true) ||
        lang.descriptionAr.contains(searchQuery, ignoreCase = true) ||
        lang.scriptAr.contains(searchQuery, ignoreCase = true)

      matchesBranch && matchesSearch
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header & Filters
    Surface(
      color = customColors.cardBackground,
      shadowElevation = 2.dp
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = "مصفوفة وتصنيف اللغات السامية (35+ لغة)",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "الفروع الستة الكبرى، الأبجديات، الخصائص الفونولوجية، والمذكرات الإبيغرافية",
          style = MaterialTheme.typography.bodySmall,
          color = customColors.mutedText
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          modifier = Modifier.fillMaxWidth(),
          placeholder = { Text("بحث في اللغات (أكادية، فينيقية، سبئية، سريانية...)") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Branch Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          item {
            FilterChip(
              selected = selectedBranch == null,
              onClick = { selectedBranch = null },
              label = { Text("الكل (${allLanguages.size})", fontSize = 11.sp) }
            )
          }
          items(SemiticBranch.values()) { branch ->
            FilterChip(
              selected = selectedBranch == branch,
              onClick = { selectedBranch = branch },
              label = { Text(branch.ar, fontSize = 11.sp) }
            )
          }
        }
      }
    }

    // Languages List
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(filteredList, key = { it.id }) { lang ->
        val isExpanded = expandedLanguageId == lang.id

        LanguageCard(
          lang = lang,
          isExpanded = isExpanded,
          onToggleExpand = {
            expandedLanguageId = if (isExpanded) null else lang.id
          },
          onNavigateToReader = {
            onLanguageSelected(lang)
          }
        )
      }
    }
  }
}

@Composable
fun LanguageCard(
  lang: SemiticLanguage,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  onNavigateToReader: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = lang.branch.ar,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
        }

        Text(
          text = lang.periodAr,
          style = MaterialTheme.typography.labelSmall,
          color = customColors.mutedText
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = lang.ar,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${lang.en} • ${lang.scriptAr}",
            style = MaterialTheme.typography.bodySmall,
            color = customColors.mutedText
          )
        }

        if (lang.sampleGlyphs.isNotBlank()) {
          Text(
            text = lang.sampleGlyphs,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "الموطن الجغرافي: ${lang.geographyAr}",
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.secondary
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = lang.descriptionAr,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 20.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        TextButton(onClick = onToggleExpand) {
          Text(if (isExpanded) "إخفاء التفاصيل الفيلولوجية ▲" else "الملف الفيلولوجي الشامل ▼", fontSize = 12.sp)
        }
      }

      AnimatedVisibility(visible = isExpanded) {
        Column(modifier = Modifier.padding(top = 8.dp)) {
          Divider(color = customColors.border)
          Spacer(modifier = Modifier.height(8.dp))

          // Consonants and Vowels details
          LanguageDetailSection("نظام الكتابة والخط الأصيل", "${lang.scriptAr} (${lang.scriptEn})")
          LanguageDetailSection("الامتداد الزمني", "${lang.periodAr} • ${lang.periodEn}")
          LanguageDetailSection("الجغرافيا والمراكز الحضارية", "${lang.geographyAr} (${lang.geographyEn})")
          LanguageDetailSection("الوصف الأكاديمي الموسع", lang.descriptionEn)
        }
      }
    }
  }
}

@Composable
fun LanguageDetailSection(label: String, content: String) {
  Column(modifier = Modifier.padding(vertical = 4.dp)) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.primary
    )
    Text(
      text = content,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurface,
      lineHeight = 18.sp
    )
  }
}
