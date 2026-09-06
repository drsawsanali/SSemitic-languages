package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.AudioEngine
import com.example.util.EpigraphicOcrEngine
import com.example.util.OcrAnalysisResult

@Composable
fun EditableDigitalInscriptionSection(
  modifier: Modifier = Modifier,
  capturedBitmap: Bitmap? = null,
  initialText: String = "",
  audioEngine: AudioEngine
) {
  val context = LocalContext.current
  var editableText by remember(initialText) { mutableStateOf(initialText) }
  var analysisResult by remember(editableText) {
    mutableStateOf(EpigraphicOcrEngine.analyzeText(editableText))
  }
  var selectedKeypadScript by remember { mutableStateOf("فينيقي") }
  var isExpandedReport by remember { mutableStateOf(true) }

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Header with Thumbnail if captured
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (capturedBitmap != null) {
            Image(
              bitmap = capturedBitmap.asImageBitmap(),
              contentDescription = "الصورة الملتقطة",
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black)
            )
            Spacer(modifier = Modifier.width(10.dp))
          } else {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
              modifier = Modifier.size(38.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  Icons.Default.EditNote,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(22.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(10.dp))
          }

          Column {
            Text(
              "المتن الرقمي المحرر (Editable Transcript)",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              "يمكنك تحرير الحروف والنقوش يدوياً وإعادة تحليلها فورياً",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Script Detection Badge & Offline Badge
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
          if (analysisResult.isOfflineProcessed) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF2E7D32).copy(alpha = 0.15f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  Icons.Default.CloudDone,
                  contentDescription = null,
                  tint = Color(0xFF2E7D32),
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "أوفلاين",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF2E7D32)
                )
              }
            }
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
          ) {
            Text(
              text = analysisResult.scriptNameAr,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }

      // Editable Text Field
      OutlinedTextField(
        value = editableText,
        onValueChange = {
          editableText = it
          analysisResult = EpigraphicOcrEngine.analyzeText(it, context)
        },
        label = { Text("النص الرقمي الإبيغرافي (قابل للتحرير والنسخ)") },
        modifier = Modifier.fillMaxWidth(),
        minLines = 2,
        maxLines = 5,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
      )

      // Script Virtual Keypad Selector & Insertion Strip
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            "إدراج رموز سريعة للنص:",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )

          val scripts = listOf("فينيقي", "مسند جنوبي", "أوجاريتي", "مسماري", "جعزي")
          Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            scripts.forEach { sc ->
              val isSel = selectedKeypadScript == sc
              FilterChip(
                selected = isSel,
                onClick = { selectedKeypadScript = sc },
                label = { Text(sc, fontSize = 10.sp) },
                modifier = Modifier.height(28.dp)
              )
            }
          }
        }

        // Quick Glyphs Row
        val glyphList = when (selectedKeypadScript) {
          "فينيقي" -> listOf("𐤀", "𐤁", "𐤂", "𐤃", "𐤄", "𐤅", "𐤆", "𐤇", "𐤈", "𐤉", "𐤊", "𐤋", "𐤌", "𐤍", "𐤎", "𐤏", "𐤐", "𐤑", "𐤒", "𐤓", "𐤔", "𐤕", "𐤟")
          "مسند جنوبي" -> listOf("𐩠", "𐩡", "𐩢", "𐩣", "𐩤", "𐩥", "𐩦", "𐩧", "𐩨", "𐩩", "𐩪", "𐩫", "𐩬", "𐩭", "𐩮", "𐩯", "𐩰", "𐩱", "𐩲", "𐩳", "𐩴", "𐩵", "𐩶", "𐩷", "𐩸", "𐩹", "𐩺", "𐩻", "𐩼", "𐩿")
          "أوجاريتي" -> listOf("𐎀", "𐎁", "𐎂", "𐎃", "𐎄", "𐎅", "𐎆", "𐎇", "𐎈", "𐎉", "𐎊", "𐎋", "𐎌", "𐎍", "𐎎", "𐎏", "𐎐", "𐎑", "𐎒", "𐎓", "𐎔", "𐎕", "𐎖", "𐎗", "𐎘", "𐎙", "𐎚")
          "مسماري" -> listOf("𒀭", "𒈗", "𒂗", "𒂍", "𒄿", "𒈾", "𒈠", "𒁴", "𒈪", "𒊭", "𒊏", "𒄠", "𒌑", "𒊭", "𒀊", "𒅆")
          "جعزي" -> listOf("ሀ", "ለ", "ሐ", "መ", "ሠ", "ረ", "ሰ", "ቀ", "በ", "ተ", "ኀ", "ነ", "አ", "ከ", "ወ", "ዐ", "ዘ", "የ", "ደ", "ገ", "ጠ", "ጰ", "ጸ", "ፈ", "ፐ")
          else -> listOf("𐡀", "𐡁", "𐡂", "𐡃", "𐡄", "𐡅", "𐡆", "𐡇", "𐡈", "𐡉", "𐡊", "𐡋", "𐡌", "𐡍", "𐡎", "𐡏", "𐡐", "𐡑", "𐡒", "𐡓", "𐡔", "𐡕")
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          glyphList.forEach { glyph ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.clickable {
                editableText += if (editableText.isNotEmpty() && !editableText.endsWith(" ")) " $glyph" else glyph
                analysisResult = EpigraphicOcrEngine.analyzeText(editableText)
                audioEngine.playProtoSemiticChime()
              }
            ) {
              Text(
                glyph,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }

          // Space separator
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            modifier = Modifier.clickable {
              editableText += " "
            }
          ) {
            Text(
              "مسافة ⎵",
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.primary
            )
          }

          // Backspace
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
            modifier = Modifier.clickable {
              if (editableText.isNotEmpty()) {
                editableText = editableText.dropLast(1)
                analysisResult = EpigraphicOcrEngine.analyzeText(editableText)
              }
            }
          ) {
            Text(
              "حذف ⌫",
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.error
            )
          }
        }
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

      // Action Toolbar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Re-analyze
        FilledTonalButton(
          onClick = {
            analysisResult = EpigraphicOcrEngine.analyzeText(editableText, context)
            audioEngine.playProtoSemiticChime()
            Toast.makeText(context, "تم تحديث التحليل الفيلولوجي للنص الرقمي", Toast.LENGTH_SHORT).show()
          },
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("تحديث التحليل", fontSize = 11.sp)
        }

        // Listen Audio
        FilledTonalButton(
          onClick = {
            audioEngine.playProtoSemiticChime()
            Toast.makeText(context, "نطق صوتي مقدر: ${analysisResult.transliteration}", Toast.LENGTH_SHORT).show()
          },
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("استماع", fontSize = 11.sp)
        }

        // Copy Text
        IconButton(
          onClick = {
            val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("Digital Text", editableText))
            Toast.makeText(context, "تم نسخ النص الرقمي للحافظة", Toast.LENGTH_SHORT).show()
          }
        ) {
          Icon(Icons.Default.ContentCopy, contentDescription = "نسخ النص", tint = MaterialTheme.colorScheme.primary)
        }

        // Share
        IconButton(
          onClick = {
            val sendIntent = Intent().apply {
              action = Intent.ACTION_SEND
              putExtra(Intent.EXTRA_TEXT, "النص الإبيغرافي المحرر:\n$editableText\n\nالنقحرة الصوتية:\n${analysisResult.transliteration}\n\nالترجمة:\n${analysisResult.translationAr}")
              type = "text/plain"
            }
            context.startActivity(Intent.createChooser(sendIntent, "مشاركة النص الإبيغرافي"))
          }
        ) {
          Icon(Icons.Default.Share, contentDescription = "مشاركة", tint = MaterialTheme.colorScheme.primary)
        }
      }

      // Philological Analysis Card
      AnimatedVisibility(visible = isExpandedReport) {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                "التحليل الفيلولوجي واللساني للنص:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                "المطابقة: ${(analysisResult.confidenceScore * 100).toInt()}%",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
              )
            }

            AnalysisRow("اللغة والفرع السامي", "${analysisResult.detectedLanguage} (${analysisResult.familyAr})")
            if (analysisResult.isOfflineProcessed && analysisResult.offlinePackName != null) {
              AnalysisRow("المعالجة الميدانية", "حزمة دون إنترنت: ${analysisResult.offlinePackName} (${analysisResult.offlinePackVersion ?: "v1.0"}) ✓")
            }
            AnalysisRow("النقحرة الصوتية اللاتينية", analysisResult.transliteration)
            AnalysisRow("الترجمة العربية الشارحة", analysisResult.translationAr)
            AnalysisRow("English Translation", analysisResult.translationEn)
            AnalysisRow("التحليل الصرفي والنحوي", analysisResult.linguisticAnalysis)
            AnalysisRow("تفكيك الجذور السامية", analysisResult.rootBreakdown)
            AnalysisRow("الأهمية الإبيغرافية", analysisResult.historicalSignificance)

            Spacer(modifier = Modifier.height(4.dp))

            Button(
              onClick = {
                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val fullReport = """
                  تقرير التحليل الإبيغرافي للنص المحرر:
                  النص الأصلي: $editableText
                  الخط: ${analysisResult.scriptNameAr}
                  اللغة: ${analysisResult.detectedLanguage}
                  الفرع السامي: ${analysisResult.familyAr}
                  النقحرة: ${analysisResult.transliteration}
                  الترجمة العربية: ${analysisResult.translationAr}
                  English: ${analysisResult.translationEn}
                  التحليل الصرفي: ${analysisResult.linguisticAnalysis}
                  الجذور: ${analysisResult.rootBreakdown}
                  الأهمية: ${analysisResult.historicalSignificance}
                """.trimIndent()
                cm.setPrimaryClip(ClipData.newPlainText("Full Epigraphic Dossier", fullReport))
                Toast.makeText(context, "تم نسخ التقرير الأكاديمي الشامل بنجاح", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier.fillMaxWidth(),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("نسخ التقرير الأكاديمي الشامل", fontSize = 12.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun AnalysisRow(title: String, detail: String) {
  Column(modifier = Modifier.padding(vertical = 2.dp)) {
    Text(
      text = title,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.primary
    )
    Text(
      text = detail,
      fontSize = 12.sp,
      color = MaterialTheme.colorScheme.onSurface,
      lineHeight = 17.sp
    )
  }
}
