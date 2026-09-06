package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ComparativeLexiconRoot
import com.example.data.repository.EncyclopediaRepository
import com.example.ui.theme.LocalCustomColors
import com.example.util.AudioEngine

data class EtymologyNode(
  val language: String,
  val languageAr: String,
  val form: String,
  val ipa: String,
  val meaningAr: String,
  val soundLaw: String,
  val xRatio: Float,
  val yRatio: Float,
  val colorHex: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EtymologyGraphScreen(
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val density = LocalDensity.current
  val customColors = LocalCustomColors.current
  val audioEngine = remember { AudioEngine(context) }
  val lexiconRoots = remember { EncyclopediaRepository.getRoots() }

  var selectedRootIndex by remember { mutableStateOf(0) }
  val activeRoot = lexiconRoots.getOrElse(selectedRootIndex) { lexiconRoots.first() }

  DisposableEffect(Unit) {
    onDispose { audioEngine.release() }
  }

  val nodes = remember(activeRoot) {
    listOf(
      EtymologyNode(
        language = "Proto-Semitic",
        languageAr = "السامية الأم",
        form = "*${activeRoot.root}",
        ipa = activeRoot.phoneticEvolutionIPA,
        meaningAr = activeRoot.protoMeaningAr,
        soundLaw = "الأصل المعجمي المقارن",
        xRatio = 0.5f,
        yRatio = 0.12f,
        colorHex = 0xFFD4AF37
      ),
      EtymologyNode(
        language = "Akkadian",
        languageAr = "الأكادية (السامية الشرقية)",
        form = activeRoot.akkadian,
        ipa = "[ak: ${activeRoot.akkadian}]",
        meaningAr = "سقوط الحلقيات وتحول الصوائت",
        soundLaw = "vocalization and loss of pharyngeals",
        xRatio = 0.18f,
        yRatio = 0.35f,
        colorHex = 0xFFE57373
      ),
      EtymologyNode(
        language = "Ugaritic",
        languageAr = "الأوغاريتية (الساحل السوري)",
        form = activeRoot.ugaritic,
        ipa = "[ug: ${activeRoot.ugaritic}]",
        meaningAr = "المحافظة على 27 صامتاً",
        soundLaw = "archaic alphabetic cuneiform inventory",
        xRatio = 0.5f,
        yRatio = 0.38f,
        colorHex = 0xFF64B5F6
      ),
      EtymologyNode(
        language = "Phoenician",
        languageAr = "الفينيقية والكنعانية",
        form = activeRoot.phoenician,
        ipa = "[ph: ${activeRoot.phoenician}]",
        meaningAr = "التحول الكنعاني ā > ō",
        soundLaw = activeRoot.soundShiftLawAr,
        xRatio = 0.82f,
        yRatio = 0.35f,
        colorHex = 0xFF81C784
      ),
      EtymologyNode(
        language = "Aramaic",
        languageAr = "الآرامية والسريانية",
        form = activeRoot.aramaic,
        ipa = "[ar: ${activeRoot.aramaic}]",
        meaningAr = "التحول الأسناني d > d, t > t",
        soundLaw = "interdental shifts to plosives",
        xRatio = 0.2f,
        yRatio = 0.68f,
        colorHex = 0xFFFFB74D
      ),
      EtymologyNode(
        language = "Hebrew",
        languageAr = "العبرية القديمة",
        form = activeRoot.hebrew,
        ipa = "[he: ${activeRoot.hebrew}]",
        meaningAr = "الإبدال والإمالة الطبرية",
        soundLaw = "Canaanite shift & Tiberian pointing",
        xRatio = 0.5f,
        yRatio = 0.68f,
        colorHex = 0xFFBA68C8
      ),
      EtymologyNode(
        language = "Arabic",
        languageAr = "العربية الفصحى",
        form = activeRoot.arabic,
        ipa = "[ar: ${activeRoot.arabic}]",
        meaningAr = "أقصى درجات المحافظة الصوتية",
        soundLaw = "retention of 28 Proto-Semitic consonants",
        xRatio = 0.8f,
        yRatio = 0.68f,
        colorHex = 0xFF4DB6AC
      ),
      EtymologyNode(
        language = "Sabaic",
        languageAr = "السبئية (المسند الجنوبي)",
        form = activeRoot.sabaic,
        ipa = "[sa: ${activeRoot.sabaic}]",
        meaningAr = "الميمية ونظام الصوامت المسندية",
        soundLaw = "Epigraphic South Arabian retention & mimation",
        xRatio = 0.35f,
        yRatio = 0.90f,
        colorHex = 0xFFFF8A65
      ),
      EtymologyNode(
        language = "Ge'ez",
        languageAr = "الجعزية (الإثيوبية القديمة)",
        form = activeRoot.geez,
        ipa = "[gz: ${activeRoot.geez}]",
        meaningAr = "القذفية والترتيب الصوتي الحنجري",
        soundLaw = "Ethiopic ejective consonants and vowel system",
        xRatio = 0.65f,
        yRatio = 0.90f,
        colorHex = 0xFFA1887F
      )
    )
  }

  var selectedNode by remember { mutableStateOf<EtymologyNode?>(null) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header & Root Selector
    Surface(
      color = customColors.cardBackground,
      shadowElevation = 2.dp
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = "شبكة التأصيل الاشتقاقي (Etymology Graph)",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "تتبع مسارات التطور المعجمي والصوتي للجذور السامية عبر العقد البيانية",
          style = MaterialTheme.typography.bodySmall,
          color = customColors.mutedText
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(lexiconRoots.indices.toList()) { index ->
            val rootItem = lexiconRoots[index]
            val isSelected = selectedRootIndex == index
            FilterChip(
              selected = isSelected,
              onClick = {
                selectedRootIndex = index
                selectedNode = null
              },
              label = {
                Text(
                  text = "√ ${rootItem.root} (${rootItem.protoMeaningAr})",
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            )
          }
        }
      }
    }

    // Interactive Graph Canvas Area
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(8.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(customColors.cardBackground)
        .border(1.dp, customColors.border, RoundedCornerShape(16.dp))
    ) {
      Canvas(
        modifier = Modifier
          .fillMaxSize()
          .pointerInput(nodes) {
            detectDragGestures { _, _ -> }
          }
      ) {
        val w = size.width
        val h = size.height

        val rootNode = nodes[0]
        val rootOffset = Offset(rootNode.xRatio * w, rootNode.yRatio * h)

        // Draw connection lines from Proto-Semitic to branches
        for (i in 1 until nodes.size) {
          val targetNode = nodes[i]
          val targetOffset = Offset(targetNode.xRatio * w, targetNode.yRatio * h)

          drawLine(
            color = Color(0xFFD4AF37).copy(alpha = 0.4f),
            start = rootOffset,
            end = targetOffset,
            strokeWidth = 3.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
          )
        }
      }

      // Render Nodes as interactive Composable items
      BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxHeightPx = constraints.maxHeight.toFloat()

        nodes.forEach { node ->
          val isSelected = selectedNode?.language == node.language

          Box(
            modifier = Modifier
              .offset(
                x = (node.xRatio * maxWidthPx / density.density).dp - 45.dp,
                y = (node.yRatio * maxHeightPx / density.density).dp - 30.dp
              )
              .width(90.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) MaterialTheme.colorScheme.primary else Color(node.colorHex))
              .clickable {
                selectedNode = node
                audioEngine.playProtoSemiticChime()
              }
              .padding(6.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = node.language,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                color = Color.White,
                maxLines = 1
              )
              Text(
                text = node.form,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                color = Color.White,
                maxLines = 1
              )
            }
          }
        }
      }
    }

    // Node Detail Card
    selectedNode?.let { node ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(8.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "${node.languageAr} (${node.language})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = "الصيغة: ${node.form} • ${node.ipa}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            IconButton(
              onClick = { audioEngine.playProtoSemiticChime() },
              modifier = Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
            ) {
              Icon(Icons.Default.VolumeUp, contentDescription = "استماع", tint = MaterialTheme.colorScheme.primary)
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "الدلالة والملاحظة الفيلولوجية: ${node.meaningAr}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "قانون التحول الصوتي: ${node.soundLaw}",
            style = MaterialTheme.typography.bodySmall,
            color = customColors.mutedText
          )
        }
      }
    }
  }
}
