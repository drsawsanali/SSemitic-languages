package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.QuizQuestion
import com.example.data.repository.LexiconAndPhoneticsData
import com.example.ui.theme.LocalCustomColors

@Composable
fun QuizModuleScreen(
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current
  val questions = LexiconAndPhoneticsData.quizQuestions

  var currentQuestionIdx by remember { mutableStateOf(0) }
  var selectedOptionIdx by remember { mutableStateOf<Int?>(null) }
  var isSubmitted by remember { mutableStateOf(false) }
  var score by remember { mutableStateOf(0) }
  var isCompleted by remember { mutableStateOf(false) }

  val question = questions.getOrElse(currentQuestionIdx) { questions.first() }

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
              text = "وحدة الاختبارات والتحديات الفيلولوجية",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "سؤال ${currentQuestionIdx + 1} من أصل ${questions.size}",
              style = MaterialTheme.typography.bodySmall,
              color = customColors.mutedText
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = "النقاط: $score / ${questions.size}",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LinearProgressIndicator(
          progress = (currentQuestionIdx + 1).toFloat() / questions.size.toFloat(),
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
        )
      }
    }

    if (!isCompleted) {
      // Question Card
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
          ) {
            Column(modifier = Modifier.padding(18.dp)) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = question.languageOrBranch,
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = question.questionAr,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp
              )
              Text(
                text = question.questionEn,
                style = MaterialTheme.typography.bodySmall,
                color = customColors.mutedText
              )
            }
          }
        }

        // Options
        items(question.optionsAr.size) { idx ->
          val isSelected = selectedOptionIdx == idx
          val isCorrect = idx == question.correctIndex

          val optionBg = when {
            isSubmitted && isCorrect -> Color(0xFF147E50).copy(alpha = 0.2f)
            isSubmitted && isSelected && !isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
            isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else -> customColors.cardBackground
          }

          val borderColor = when {
            isSubmitted && isCorrect -> Color(0xFF147E50)
            isSubmitted && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
            isSelected -> MaterialTheme.colorScheme.primary
            else -> customColors.border
          }

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable(enabled = !isSubmitted) { selectedOptionIdx = idx }
              .border(1.5.dp, borderColor, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = optionBg)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = ('أ'.code + idx).toChar().toString(),
                  color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Text(
                text = question.optionsAr[idx],
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
              )
            }
          }
        }

        // Explanation if submitted
        if (isSubmitted) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (selectedOptionIdx == question.correctIndex)
                  Color(0xFF147E50).copy(alpha = 0.1f)
                else
                  MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
              )
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                  text = if (selectedOptionIdx == question.correctIndex) "إجابة صحيحة! 🎉" else "إجابة خاطئة ❌",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = if (selectedOptionIdx == question.correctIndex) Color(0xFF147E50) else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = question.explanationAr,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurface,
                  lineHeight = 20.sp
                )
              }
            }
          }
        }

        // Action Buttons
        item {
          if (!isSubmitted) {
            Button(
              onClick = {
                if (selectedOptionIdx != null) {
                  isSubmitted = true
                  if (selectedOptionIdx == question.correctIndex) {
                    score++
                  }
                }
              },
              enabled = selectedOptionIdx != null,
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text("تأكيد الإجابة", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
          } else {
            Button(
              onClick = {
                if (currentQuestionIdx < questions.size - 1) {
                  currentQuestionIdx++
                  selectedOptionIdx = null
                  isSubmitted = false
                } else {
                  isCompleted = true
                }
              },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                if (currentQuestionIdx < questions.size - 1) "السؤال التالي ◀" else "عرض النتيجة النهائية 🏆",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    } else {
      // Completed Score Card
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
          elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              Icons.Default.EmojiEvents,
              contentDescription = null,
              tint = Color(0xFFB3741F),
              modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = "اكتمل الاختبار الفيلولوجي بنجاح!",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "حققت $score من أصل ${questions.size} إجابات صحيحة (${(score * 100) / questions.size}%)",
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
              onClick = {
                currentQuestionIdx = 0
                selectedOptionIdx = null
                isSubmitted = false
                score = 0
                isCompleted = false
              },
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.Refresh, contentDescription = null)
              Spacer(modifier = Modifier.width(6.dp))
              Text("إعادة خوض التحدي")
            }
          }
        }
      }
    }
  }
}
