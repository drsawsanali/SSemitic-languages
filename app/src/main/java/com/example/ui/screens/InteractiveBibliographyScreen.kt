package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.BibliographyItem
import com.example.data.repository.LexiconAndPhoneticsData
import com.example.ui.theme.LocalCustomColors

@Composable
fun InteractiveBibliographyScreen(
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  val bibliographyList = LexiconAndPhoneticsData.bibliography

  var searchQuery by remember { mutableStateOf("") }

  val filteredBib = remember(searchQuery) {
    bibliographyList.filter { b ->
      searchQuery.isBlank() ||
        b.title.contains(searchQuery, ignoreCase = true) ||
        b.authors.contains(searchQuery, ignoreCase = true) ||
        b.summaryAr.contains(searchQuery, ignoreCase = true) ||
        b.publisher.contains(searchQuery, ignoreCase = true)
    }
  }

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
          Column {
            Text(
              text = "المراجع والأبحاث الأكاديمية التفاعلية",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "مدونات KAI, DNWSI ومعاجم اللغات السامية مع مولد الاقتباسات",
              style = MaterialTheme.typography.bodySmall,
              color = customColors.mutedText
            )
          }

          FilledTonalButton(
            onClick = {
              val bibTex = bibliographyList.joinToString("\n\n") { it.citationBibTeX }
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              clipboard.setPrimaryClip(ClipData.newPlainText("BibTeX Corpus", bibTex))
              Toast.makeText(context, "تم نسخ حزمة المراجع بصيغة BibTeX (.bib) بنجاح", Toast.LENGTH_LONG).show()
            }
          ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("تصدير .bib", fontSize = 11.sp)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          modifier = Modifier.fillMaxWidth(),
          placeholder = { Text("بحث في المراجع (KAI, Huehnergard, Donner, Brill...)") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
          singleLine = true,
          shape = RoundedCornerShape(12.dp)
        )
      }
    }

    // List of bibliography cards
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(filteredBib, key = { it.id }) { item ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = item.title,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = "${item.authors} (${item.year}) • ${item.publisher}",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = item.summaryAr,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface,
              lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Divider(color = customColors.border)

            Spacer(modifier = Modifier.height(8.dp))

            // Copy Citation Quick Buttons
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "نسخ الاستشهاد:", style = MaterialTheme.typography.labelSmall, color = customColors.mutedText)

              SuggestionChip(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  clipboard.setPrimaryClip(ClipData.newPlainText("APA Citation", item.citationAPA))
                  Toast.makeText(context, "تم نسخ استشهاد APA", Toast.LENGTH_SHORT).show()
                },
                label = { Text("APA", fontSize = 10.sp) }
              )

              SuggestionChip(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  clipboard.setPrimaryClip(ClipData.newPlainText("MLA Citation", item.citationMLA))
                  Toast.makeText(context, "تم نسخ استشهاد MLA", Toast.LENGTH_SHORT).show()
                },
                label = { Text("MLA", fontSize = 10.sp) }
              )

              SuggestionChip(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  clipboard.setPrimaryClip(ClipData.newPlainText("Chicago Citation", item.citationChicago))
                  Toast.makeText(context, "تم نسخ استشهاد Chicago", Toast.LENGTH_SHORT).show()
                },
                label = { Text("Chicago", fontSize = 10.sp) }
              )

              SuggestionChip(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  clipboard.setPrimaryClip(ClipData.newPlainText("BibTeX Citation", item.citationBibTeX))
                  Toast.makeText(context, "تم نسخ كود BibTeX", Toast.LENGTH_SHORT).show()
                },
                label = { Text("BibTeX", fontSize = 10.sp) }
              )
            }
          }
        }
      }
    }
  }
}
