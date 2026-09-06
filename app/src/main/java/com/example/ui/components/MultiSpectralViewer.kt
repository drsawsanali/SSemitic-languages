package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.LocalCustomColors

enum class SpectralFilterMode(val titleAr: String, val titleEn: String, val descriptionAr: String) {
  STANDARD("طبيعي أصلي", "Standard Natural", "الألوان والخامات والأبعاد الأصلية للقطعة الأثرية"),
  HIGH_CONTRAST("حفر بارز عالي التباين", "High Contrast Relief", "إبراز أخاديد الحروف الغائرة وحفر الأسطر بأقصى حدة"),
  INFRARED("أشعة تحت الحمراء MSI", "Infrared MSI", "تصوير طيفي لكشف الحبر والخطوط الباهتة والممسوحة زمنيّاً"),
  LINE_ART("كاشف الحواف والأبجدية", "Line Art Edge", "عزل الخلفية الحجرية الحبيبية وإبراز الهيكل الأبجدي السامي بخطوط متوهجة"),
  HEATMAP("خريطة حرارية للعمق", "Depth Heatmap", "توزيع حراري يعكس كثافة الحفر والعمق الإبيغرافي للنقوش")
}

@Composable
fun MultiSpectralViewer(
  imageUrl: String,
  titleAr: String,
  titleEn: String,
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current
  var selectedMode by remember { mutableStateOf(SpectralFilterMode.STANDARD) }
  var isSplitComparison by remember { mutableStateOf(false) }
  var splitPosition by remember { mutableStateOf(0.5f) }
  var compareModeRight by remember { mutableStateOf(SpectralFilterMode.LINE_ART) }

  Card(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = titleAr,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "المعالجة البصرية الطيفية للألواح والنقوش الأثرية",
            style = MaterialTheme.typography.bodySmall,
            color = customColors.mutedText
          )
        }

        FilterChip(
          selected = isSplitComparison,
          onClick = { isSplitComparison = !isSplitComparison },
          label = { Text(if (isSplitComparison) "المقارنة المنزلقة ◧" else "عرض أحادي ◻", fontSize = 12.sp) },
          leadingIcon = {
            Icon(
              imageVector = if (isSplitComparison) Icons.Default.Compare else Icons.Default.Visibility,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
          }
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Filter Selector Chips
      ScrollableTabRow(
        selectedTabIndex = selectedMode.ordinal,
        edgePadding = 0.dp,
        containerColor = Color.Transparent,
        divider = {}
      ) {
        SpectralFilterMode.values().forEach { mode ->
          Tab(
            selected = selectedMode == mode,
            onClick = { selectedMode = mode },
            text = {
              Text(
                text = mode.titleAr,
                fontSize = 12.sp,
                fontWeight = if (selectedMode == mode) FontWeight.Bold else FontWeight.Normal
              )
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Image Display Area with spectral shader simulation
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(260.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color.Black)
          .border(1.dp, customColors.border, RoundedCornerShape(12.dp))
      ) {
        if (!isSplitComparison) {
          // Single View
          AsyncImage(
            model = imageUrl,
            contentDescription = titleAr,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            colorFilter = getColorFilterForMode(selectedMode)
          )

          // Mode Overlay Tag
          Box(
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(8.dp)
              .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "${selectedMode.titleAr} (${selectedMode.titleEn})",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        } else {
          // Split Comparison Slider View
          BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val totalWidth = maxWidth

            // Left Image (selectedMode)
            Box(
              modifier = Modifier
                .fillMaxHeight()
                .width(totalWidth * splitPosition)
                .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
            ) {
              AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                colorFilter = getColorFilterForMode(selectedMode)
              )
              Text(
                text = selectedMode.titleAr,
                color = Color.Yellow,
                fontSize = 10.sp,
                modifier = Modifier
                  .align(Alignment.TopStart)
                  .padding(6.dp)
                  .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                  .padding(4.dp)
              )
            }

            // Right Image (compareModeRight)
            Box(
              modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(totalWidth * (1f - splitPosition))
                .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
            ) {
              AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                colorFilter = getColorFilterForMode(compareModeRight)
              )
              Text(
                text = compareModeRight.titleAr,
                color = Color.Cyan,
                fontSize = 10.sp,
                modifier = Modifier
                  .align(Alignment.TopEnd)
                  .padding(6.dp)
                  .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                  .padding(4.dp)
              )
            }

            // Splitter Handle
            Box(
              modifier = Modifier
                .fillMaxHeight()
                .width(4.dp)
                .offset(x = totalWidth * splitPosition - 2.dp)
                .background(Color.White)
                .pointerInput(Unit) {
                  detectDragGestures { change, dragAmount ->
                    change.consume()
                    val newPos = (splitPosition + dragAmount.x / size.width).coerceIn(0.1f, 0.9f)
                    splitPosition = newPos
                  }
                }
            ) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .align(Alignment.Center)
                  .offset(x = (-10).dp)
                  .background(Color.White, CircleShape)
                  .border(2.dp, Color.Black, CircleShape)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = selectedMode.descriptionAr,
        style = MaterialTheme.typography.bodySmall,
        color = customColors.mutedText,
        fontSize = 12.sp
      )
    }
  }
}

fun getColorFilterForMode(mode: SpectralFilterMode): ColorFilter? {
  return when (mode) {
    SpectralFilterMode.STANDARD -> null
    SpectralFilterMode.HIGH_CONTRAST -> {
      val contrast = 2.0f
      val scale = contrast
      val translate = (-0.5f * contrast + 0.5f) * 255f
      val matrix = ColorMatrix(
        floatArrayOf(
          scale, 0f, 0f, 0f, translate,
          0f, scale, 0f, 0f, translate,
          0f, 0f, scale, 0f, translate,
          0f, 0f, 0f, 1f, 0f
        )
      )
      matrix.setToSaturation(0f)
      ColorFilter.colorMatrix(matrix)
    }
    SpectralFilterMode.INFRARED -> {
      // Infrared false color
      val matrix = ColorMatrix(
        floatArrayOf(
          0.8f, 0.4f, 0.1f, 0f, 30f,
          0.1f, 0.9f, 0.2f, 0f, 20f,
          0.2f, 0.2f, 1.2f, 0f, 50f,
          0f, 0f, 0f, 1f, 0f
        )
      )
      ColorFilter.colorMatrix(matrix)
    }
    SpectralFilterMode.LINE_ART -> {
      // Inverted negative edge glow
      val matrix = ColorMatrix(
        floatArrayOf(
          -1f, 0f, 0f, 0f, 255f,
          0f, -1f, 0f, 0f, 255f,
          0f, 0f, -1f, 0f, 255f,
          0f, 0f, 0f, 1f, 0f
        )
      )
      ColorFilter.colorMatrix(matrix)
    }
    SpectralFilterMode.HEATMAP -> {
      // Thermal false color (amber/red glow)
      val matrix = ColorMatrix(
        floatArrayOf(
          1.8f, 0.2f, 0f, 0f, 40f,
          0.6f, 1.2f, 0f, 0f, 10f,
          0f, 0.2f, 0.6f, 0f, 0f,
          0f, 0f, 0f, 1f, 0f
        )
      )
      ColorFilter.colorMatrix(matrix)
    }
  }
}
