package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AcademicChapter
import com.example.data.repository.ChaptersData
import com.example.ui.theme.LocalCustomColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareDualScreen(
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current

  var leftChapterIdx by remember { mutableStateOf(0) }
  var rightChapterIdx by remember { mutableStateOf(1.coerceAtMost(ChaptersData.list.size - 1)) }
  var compareMode by remember { mutableStateOf(0) } // 0: Side-by-Side, 1: Difference Matrix, 2: Shared Cognates

  val leftChapter = ChaptersData.list[leftChapterIdx]
  val rightChapter = ChaptersData.list[rightChapterIdx]

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header & Mode Tabs
    Surface(
      color = customColors.cardBackground,
      shadowElevation = 2.dp
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = "المقارنة المزدوجة والفيلولوجية بين الفصول واللهجات",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dialect Selectors
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Left Selector
          var leftExpanded by remember { mutableStateOf(false) }
          ExposedDropdownMenuBox(
            expanded = leftExpanded,
            onExpandedChange = { leftExpanded = it },
            modifier = Modifier.weight(1f)
          ) {
            OutlinedTextField(
              value = "الفصل ${leftChapter.chapterNumber}: ${leftChapter.titleAr.take(12)}",
              onValueChange = {},
              readOnly = true,
              label = { Text("الجانب الأيمن (أ)") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = leftExpanded) },
              modifier = Modifier.menuAnchor(),
              shape = RoundedCornerShape(10.dp)
            )
            ExposedDropdownMenu(
              expanded = leftExpanded,
              onDismissRequest = { leftExpanded = false }
            ) {
              ChaptersData.list.forEachIndexed { idx, ch ->
                DropdownMenuItem(
                  text = { Text("فصل ${ch.chapterNumber}: ${ch.titleAr}") },
                  onClick = {
                    leftChapterIdx = idx
                    leftExpanded = false
                  }
                )
              }
            }
          }

          // Right Selector
          var rightExpanded by remember { mutableStateOf(false) }
          ExposedDropdownMenuBox(
            expanded = rightExpanded,
            onExpandedChange = { rightExpanded = it },
            modifier = Modifier.weight(1f)
          ) {
            OutlinedTextField(
              value = "الفصل ${rightChapter.chapterNumber}: ${rightChapter.titleAr.take(12)}",
              onValueChange = {},
              readOnly = true,
              label = { Text("الجانب الأيسر (ب)") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rightExpanded) },
              modifier = Modifier.menuAnchor(),
              shape = RoundedCornerShape(10.dp)
            )
            ExposedDropdownMenu(
              expanded = rightExpanded,
              onDismissRequest = { rightExpanded = false }
            ) {
              ChaptersData.list.forEachIndexed { idx, ch ->
                DropdownMenuItem(
                  text = { Text("فصل ${ch.chapterNumber}: ${ch.titleAr}") },
                  onClick = {
                    rightChapterIdx = idx
                    rightExpanded = false
                  }
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        TabRow(
          selectedTabIndex = compareMode,
          containerColor = Color.Transparent,
          divider = {}
        ) {
          Tab(
            selected = compareMode == 0,
            onClick = { compareMode = 0 },
            text = { Text("العرض المزدوج", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
          )
          Tab(
            selected = compareMode == 1,
            onClick = { compareMode = 1 },
            text = { Text("مصفوفة الفروق الفيلولوجية", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
          )
          Tab(
            selected = compareMode == 2,
            onClick = { compareMode = 2 },
            text = { Text("كاشف الجذور المشتركة", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
          )
        }
      }
    }

    // Body based on compareMode
    when (compareMode) {
      0 -> SideBySideView(leftChapter, rightChapter)
      1 -> DifferenceMatrixView(leftChapter, rightChapter)
      2 -> SharedCognatesView()
    }
  }
}

@Composable
fun SideBySideView(left: AcademicChapter, right: AcademicChapter) {
  val customColors = LocalCustomColors.current

  Row(
    modifier = Modifier
      .fillMaxSize()
      .padding(8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    // Left Pane
    Card(
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight(),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = customColors.cardBackground)
    ) {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          Text(
            text = left.titleAr,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
          Divider(color = customColors.border, modifier = Modifier.padding(vertical = 4.dp))
        }
        items(left.sections) { sec ->
          Text(text = sec.headingAr, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.secondary)
          Text(text = sec.bodyTextAr, style = MaterialTheme.typography.bodySmall, lineHeight = 16.sp)
          Spacer(modifier = Modifier.height(6.dp))
        }
      }
    }

    // Right Pane
    Card(
      modifier = Modifier
        .weight(1f)
        .fillMaxHeight(),
      shape = RoundedCornerShape(12.dp),
      colors = CardDefaults.cardColors(containerColor = customColors.cardBackground)
    ) {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          Text(
            text = right.titleAr,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
          Divider(color = customColors.border, modifier = Modifier.padding(vertical = 4.dp))
        }
        items(right.sections) { sec ->
          Text(text = sec.headingAr, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.secondary)
          Text(text = sec.bodyTextAr, style = MaterialTheme.typography.bodySmall, lineHeight = 16.sp)
          Spacer(modifier = Modifier.height(6.dp))
        }
      }
    }
  }
}

@Composable
fun DifferenceMatrixView(left: AcademicChapter, right: AcademicChapter) {
  val customColors = LocalCustomColors.current

  val matrixRows = listOf(
    MatrixRow("نظام الكتابة والخط", "أبجدي صامت (22 حرفاً)", "خط مسماري / مسند 29 حرفاً"),
    MatrixRow("التحول الكنعاني (*ā > ō)", "متحقق بالكامل (*šalām > šālōm)", "غير متحقق (حفظ الألف الأصلية)"),
    MatrixRow("أداة التعريف", "الهاء مع التشديد (h- + Dagesh)", "لاحقة النون بالمسند (-n) / لاحقة الألف بالآرامية (-ā)"),
    MatrixRow("علامة جمع المذكر السالم", "الميم (-īm / -m)", "النون (-īn) أو الواو (-ū)"),
    MatrixRow("رتبة الجملة الافتراضية", "فعلية (VSO)", "فعلية (VSO) أو SOV في الأكادية"),
    MatrixRow("المحافظة على الصوامت الحلقية", "اندمجت الحاء والخاء، والعين والغين", "حُفظت كاملة (ح، خ، ع، غ، هـ، ء)")
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text(
        text = "مصفوفة الفروق اللغوية والفونولوجية المقارنة",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    items(matrixRows) { row ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = customColors.cardBackground)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = row.feature,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(text = "الجانب أ:", style = MaterialTheme.typography.labelSmall, color = customColors.mutedText)
              Text(text = row.valLeft, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(text = "الجانب ب:", style = MaterialTheme.typography.labelSmall, color = customColors.mutedText)
              Text(text = row.valRight, style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      }
    }
  }
}

@Composable
fun SharedCognatesView() {
  val customColors = LocalCustomColors.current

  val cognates = listOf(
    CognateDiffItem("*m-l-k", "ملك / حكم", "الفينيقية: 𐤌𐤋𐤊 | العبرية: מֶלֶךְ | الأكادية: malku | السبئية: 𐩣𐩡𐩫 | العربية: مَلِك"),
    CognateDiffItem("*b-y-t", "بيت / معبد", "الفينيقية: 𐤁𐤕 (bēt) | العبرية: בַּיִת (bayit) | الأكادية: bītu | الجعزية: ቤት (bēt)"),
    CognateDiffItem("*š-l-m", "سلام / أمان", "الفينيقية: 𐤔𐤋𐤌 (šulūm) | العبرية: שָׁלוֹם (šālôm) | الآرامية: שְׁלָמָא (šəlāmā) | العربية: سَلَام")
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    item {
      Text(
        text = "كاشف الجذور المشتركة والتأصيل المقارن:",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    items(cognates) { item ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = customColors.cardBackground)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "الجذر السامي: ${item.protoRoot}",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = item.meaning,
              style = MaterialTheme.typography.labelSmall,
              color = customColors.mutedText
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = item.branchesAttestation,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 18.sp
          )
        }
      }
    }
  }
}

data class MatrixRow(val feature: String, val valLeft: String, val valRight: String)
data class CognateDiffItem(val protoRoot: String, val meaning: String, val branchesAttestation: String)
