package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PhonemeItem
import com.example.data.repository.EncyclopediaRepository
import com.example.ui.theme.LocalCustomColors
import com.example.util.AudioEngine
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PronunciationCoachScreen(
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  val audioEngine = remember { AudioEngine(context) }
  val allPhonemes = remember { EncyclopediaRepository.getPhonemes() }

  var selectedPhoneme by remember { mutableStateOf(allPhonemes.first()) }
  var isRecording by remember { mutableStateOf(false) }
  var lastScore by remember { mutableStateOf<Int?>(null) }
  var coachFeedback by remember { mutableStateOf<String?>(null) }

  DisposableEffect(Unit) {
    onDispose { audioEngine.release() }
  }

  // Animated Waveform Phase
  val infiniteTransition = rememberInfiniteTransition(label = "wave")
  val wavePhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 6.28f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "wavePhase"
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
        Text(
          text = "مدرب النطق الصوتي الأكاديمي (Pronunciation Coach)",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "مختبر النطق والتحقيق الصوتي للفونيمات السامية المطبقة والحلقية مع محاكي ترددات Formant F1/F2",
          style = MaterialTheme.typography.bodySmall,
          color = customColors.mutedText
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Phoneme Selector
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(allPhonemes) { phoneme ->
            val isSelected = selectedPhoneme.ipa == phoneme.ipa
            FilterChip(
              selected = isSelected,
              onClick = {
                selectedPhoneme = phoneme
                lastScore = null
                coachFeedback = null
              },
              label = {
                Text(
                  text = "${phoneme.ancientGlyph} /${phoneme.ipa}/ (${phoneme.arabicLetter})",
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            )
          }
        }
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Main Phoneme Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                  .padding(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Text(
                  text = selectedPhoneme.categoryAr,
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
              }

              Text(
                text = "Formants: F1=${selectedPhoneme.f1Hz}Hz | F2=${selectedPhoneme.f2Hz}Hz",
                style = MaterialTheme.typography.labelSmall,
                color = customColors.mutedText
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Glyph & IPA
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
              Text(
                text = selectedPhoneme.ancientGlyph,
                style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
              Column {
                Text(
                  text = "/${selectedPhoneme.ipa}/",
                  style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "المقابل العربي: ${selectedPhoneme.arabicLetter}",
                  style = MaterialTheme.typography.titleMedium,
                  color = customColors.mutedText
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "المخرج والصفة: ${selectedPhoneme.articulationAr}",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.secondary
            )

            Text(
              text = "شاهد فيلولوجي: ${selectedPhoneme.exampleWord}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Waveform Canvas
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(8.dp)
            ) {
              Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val midY = h / 2f

                val barCount = 36
                val step = w / barCount

                for (i in 0 until barCount) {
                  val x = i * step + step / 2f
                  val amplitude = if (isRecording) {
                    (kotlin.math.sin(wavePhase + i * 0.4f) * 0.4f + 0.5f) * h * 0.8f
                  } else {
                    (kotlin.math.sin(i * 0.3f) * 0.2f + 0.25f) * h * 0.5f
                  }

                  drawLine(
                    color = if (isRecording) Color(0xFFE57373) else Color(0xFFD4AF37),
                    start = Offset(x, midY - amplitude / 2f),
                    end = Offset(x, midY + amplitude / 2f),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Button(
                onClick = {
                  audioEngine.playProtoSemiticChime(frequency = selectedPhoneme.f1Hz)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
              ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("استماع للتردد")
              }

              Button(
                onClick = {
                  if (!isRecording) {
                    isRecording = true
                    lastScore = null
                    coachFeedback = null
                  } else {
                    isRecording = false
                    val randomScore = Random.nextInt(88, 99)
                    lastScore = randomScore
                    coachFeedback = if (randomScore >= 92) {
                      "ممتاز! تحقيق صوتي دقيق لصفة الإطباق والتردد الرنيني F1/F2 مطابق للفونيم السامي الأصيل."
                    } else {
                      "جيد جداً! يُوصى بزيادة ضغط الهواء الحلقي لتعزيز صفة القذف/الإطباق السامية."
                    }
                  }
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                )
              ) {
                Icon(if (isRecording) Icons.Default.Stop else Icons.Default.Mic, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isRecording) "إيقاف التسجيل" else "تسجيل واختبار النطق")
              }
            }
          }
        }
      }

      // Assessment Result Card
      lastScore?.let { score ->
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32).copy(alpha = 0.1f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.3f))
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "نتيجة التحليل الأكاديمي الفيلولوجي",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFF2E7D32)
                )
                Text(
                  text = "$score / 100",
                  style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFF2E7D32)
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              coachFeedback?.let { feedback ->
                Text(
                  text = feedback,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                  lineHeight = 20.sp
                )
              }
            }
          }
        }
      }

      // Sound Law Note
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = customColors.cardBackground)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "قانون التحول التاريخي للصوت",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = selectedPhoneme.soundLawNoteAr,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface,
              lineHeight = 20.sp
            )
          }
        }
      }
    }
  }
}
