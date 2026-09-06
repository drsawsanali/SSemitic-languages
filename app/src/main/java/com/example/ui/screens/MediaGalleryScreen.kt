package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.MediaArtifact
import com.example.data.repository.LexiconAndPhoneticsData
import com.example.ui.components.MultiSpectralViewer
import com.example.ui.theme.LocalCustomColors

@Composable
fun MediaGalleryScreen(
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current

  var selectedCategory by remember { mutableStateOf("الكل") }
  val selectedItemIds = remember { mutableStateListOf<String>() }
  var activeSpectralArtifact by remember { mutableStateOf<MediaArtifact?>(null) }

  val categories = listOf("الكل", "نقوش ومسلات", "خرائط طبوغرافية", "رسوم بيانية نحوية", "مخطوطات وألواح")

  val filteredArtifacts = remember(selectedCategory) {
    LexiconAndPhoneticsData.mediaArtifacts.filter { item ->
      when (selectedCategory) {
        "الكل" -> true
        "نقوش ومسلات" -> item.category == "inscriptions"
        "خرائط طبوغرافية" -> item.category == "maps"
        "رسوم بيانية نحوية" -> item.category == "charts"
        "مخطوطات وألواح" -> item.category == "manuscripts"
        else -> true
      }
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header & Dossier Action
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
              text = "معرض المخطوطات والوسائط الأثرية",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "المسلات والخرائط الطبوغرافية والمخطوطات مع المعاينة الطيفية",
              style = MaterialTheme.typography.bodySmall,
              color = customColors.mutedText
            )
          }

          FilledTonalButton(
            onClick = {
              val itemsToExport = if (selectedItemIds.isEmpty()) {
                filteredArtifacts
              } else {
                filteredArtifacts.filter { it.id in selectedItemIds }
              }
              val dossierText = buildEducationalDossierText(itemsToExport)
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              clipboard.setPrimaryClip(ClipData.newPlainText("Educational Dossier", dossierText))
              Toast.makeText(context, "تم تجهيز ونسخ الحقيبة التعليمية الأكاديمية بنجاح (${itemsToExport.size} مادة)", Toast.LENGTH_LONG).show()
            }
          ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("تنزيل كملف تعليمي", fontSize = 11.sp)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(categories) { cat ->
            FilterChip(
              selected = selectedCategory == cat,
              onClick = { selectedCategory = cat },
              label = { Text(cat, fontSize = 11.sp) }
            )
          }
        }
      }
    }

    // Modal if spectral inspector is active
    activeSpectralArtifact?.let { art ->
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
              text = "المعاينة الطيفية: ${art.titleAr}",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = { activeSpectralArtifact = null }) {
              Icon(Icons.Default.Close, contentDescription = "إغلاق")
            }
          }
          MultiSpectralViewer(
            imageUrl = art.imageUrl,
            titleAr = art.titleAr,
            titleEn = art.title
          )
        }
      }
    }

    // Grid of Artifact Cards
    LazyVerticalGrid(
      columns = GridCells.Adaptive(minSize = 160.dp),
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(filteredArtifacts, key = { it.id }) { item ->
        val isChecked = item.id in selectedItemIds

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { activeSpectralArtifact = item },
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
            ) {
              AsyncImage(
                model = item.imageUrl,
                contentDescription = item.titleAr,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )

              // Checkbox for Dossier
              Checkbox(
                checked = isChecked,
                onCheckedChange = { checked ->
                  if (checked) selectedItemIds.add(item.id) else selectedItemIds.remove(item.id)
                },
                modifier = Modifier
                  .align(Alignment.TopEnd)
                  .padding(4.dp)
              )

              // Category tag
              Box(
                modifier = Modifier
                  .align(Alignment.BottomStart)
                  .padding(6.dp)
                  .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Text(
                  text = item.datePeriod,
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }

            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = item.titleAr,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
              )
              Text(
                text = item.scriptType,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 10.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = item.museum,
                style = MaterialTheme.typography.bodySmall,
                color = customColors.mutedText,
                fontSize = 10.sp,
                maxLines = 1
              )
            }
          }
        }
      }
    }
  }
}

fun buildEducationalDossierText(items: List<MediaArtifact>): String {
  val sb = StringBuilder()
  sb.append("=================================================================\n")
  sb.append("      الحقيبة التعليمية الأكاديمية - موسوعة اللغات والنقوش السامية\n")
  sb.append("جامعة صنعاء • كلية الآداب • قسم الآثار • إشراف: أ.د. أحمد فقعس • إعداد: سوسن علي عبدالله الحضوري\n")
  sb.append("=================================================================\n\n")

  items.forEachIndexed { idx, item ->
    sb.append("${idx + 1}. ${item.titleAr} (${item.title})\n")
    sb.append("   • النوع والخامة: ${item.category} / ${item.material}\n")
    sb.append("   • نوع الخط: ${item.scriptType}\n")
    sb.append("   • الحقبة الزمنية: ${item.datePeriod}\n")
    sb.append("   • جهة الحفظ والمتحف: ${item.museum}\n")
    sb.append("   • الوصف الأكاديمي: ${item.descriptionAr}\n\n")
  }
  return sb.toString()
}
