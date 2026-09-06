package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.GrammarCard
import com.example.data.repository.LexiconAndPhoneticsData
import com.example.ui.theme.LocalCustomColors

@Composable
fun FlashcardsScreen(
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current
  val cards = LexiconAndPhoneticsData.flashcards

  var currentCardIndex by remember { mutableStateOf(0) }
  var isFlipped by remember { mutableStateOf(false) }
  var masteredCount by remember { mutableStateOf(0) }

  val card = cards.getOrElse(currentCardIndex) { cards.first() }

  val rotation by animateFloatAsState(
    targetValue = if (isFlipped) 180f else 0f,
    animationSpec = tween(durationMillis = 400),
    label = "cardFlip"
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
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "بطاقات القواعد الإبيغرافية (نظام لايتنر)",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "البطاقة ${currentCardIndex + 1} من أصل ${cards.size}",
              style = MaterialTheme.typography.bodySmall,
              color = customColors.mutedText
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Text(
              text = "أتقنت: $masteredCount",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress bar
        LinearProgressIndicator(
          progress = (currentCardIndex + 1).toFloat() / cards.size.toFloat(),
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
        )
      }
    }

    // Flip Card Area
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(16.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .height(340.dp)
          .graphicsLayer {
            rotationY = rotation
            cameraDistance = 12f * density
          }
          .clickable { isFlipped = !isFlipped },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
      ) {
        if (rotation <= 90f) {
          // FRONT OF CARD
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = card.categoryAr,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = card.titleAr,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
              )
              Text(
                text = card.titleEn,
                style = MaterialTheme.typography.bodySmall,
                color = customColors.mutedText,
                textAlign = TextAlign.Center
              )

              Spacer(modifier = Modifier.height(14.dp))

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(MaterialTheme.colorScheme.surface)
                  .border(1.dp, customColors.border, RoundedCornerShape(10.dp))
                  .padding(12.dp)
              ) {
                Text(
                  text = card.formula,
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary,
                  textAlign = TextAlign.Center
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = card.summaryAr,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
              )
            }

            Text(
              text = "🔄 انقر على البطاقة لقلبها واستعراض الشاهد الإبيغرافي",
              style = MaterialTheme.typography.labelSmall,
              color = customColors.mutedText,
              modifier = Modifier.align(Alignment.CenterHorizontally)
            )
          }
        } else {
          // BACK OF CARD (Rotated back)
          Column(
            modifier = Modifier
              .fillMaxSize()
              .graphicsLayer { rotationY = 180f }
              .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "الشاهد الإبيغرافي والتحليل المقارن:",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                text = "• الشاهد المنقوش:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary
              )
              Text(
                text = card.inscriptionalWitnessAr,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
              )

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = "• التحليل الفيلولوجي المقارن:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.secondary
              )
              Text(
                text = card.comparativeAnalysisAr,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
              )
            }

            Text(
              text = "🔄 انقر للعودة لوجه البطاقة",
              style = MaterialTheme.typography.labelSmall,
              color = customColors.mutedText,
              modifier = Modifier.align(Alignment.CenterHorizontally)
            )
          }
        }
      }
    }

    // Navigation and Mastery Buttons
    Surface(
      color = customColors.cardBackground,
      shadowElevation = 4.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = {
            if (currentCardIndex > 0) {
              isFlipped = false
              currentCardIndex--
            }
          },
          enabled = currentCardIndex > 0
        ) {
          Icon(Icons.Default.ArrowForward, contentDescription = null)
          Spacer(modifier = Modifier.width(4.dp))
          Text("السابق")
        }

        FilledTonalButton(
          onClick = {
            masteredCount++
            if (currentCardIndex < cards.size - 1) {
              isFlipped = false
              currentCardIndex++
            }
          },
          colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = Color(0xFF147E50).copy(alpha = 0.15f),
            contentColor = Color(0xFF147E50)
          )
        ) {
          Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("أتقنتها ✅")
        }

        Button(
          onClick = {
            if (currentCardIndex < cards.size - 1) {
              isFlipped = false
              currentCardIndex++
            }
          },
          enabled = currentCardIndex < cards.size - 1
        ) {
          Text("التالي")
          Spacer(modifier = Modifier.width(4.dp))
          Icon(Icons.Default.ArrowBack, contentDescription = null)
        }
      }
    }
  }
}
