package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.ComparativeLexiconRoot
import com.example.data.repository.LexiconAndPhoneticsData
import com.example.ui.theme.LocalCustomColors
import com.example.util.AudioEngine
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparativeLexiconScreen(
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  val audioEngine = remember { AudioEngine(context) }

  var searchQuery by remember { mutableStateOf("") }
  var expandedRoot by remember { mutableStateOf<String?>(null) }

  // Global Audio Studio State
  var activePlayingRoot by remember { mutableStateOf<String?>(null) }
  var activePlayingLang by remember { mutableStateOf<String?>(null) }
  var isSequentialPlaying by remember { mutableStateOf(false) }
  var playbackSpeed by remember { mutableStateOf(0.9f) }

  val roots = remember(searchQuery) {
    LexiconAndPhoneticsData.roots.filter { r ->
      searchQuery.isBlank() ||
        r.root.contains(searchQuery, ignoreCase = true) ||
        r.protoMeaningAr.contains(searchQuery, ignoreCase = true) ||
        r.protoMeaningEn.contains(searchQuery, ignoreCase = true) ||
        r.arabic.contains(searchQuery, ignoreCase = true) ||
        r.hebrew.contains(searchQuery, ignoreCase = true) ||
        r.akkadian.contains(searchQuery, ignoreCase = true)
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      audioEngine.release()
    }
  }

  // Waveform animated phase
  val infiniteTransition = rememberInfiniteTransition(label = "audioWave")
  val wavePhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 6.28f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "wavePhase"
  )

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header & Studio Banner
    Surface(
      color = customColors.cardBackground,
      shadowElevation = 3.dp
    ) {
      Column(modifier = Modifier.padding(14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "المعجم السامي المقارن وكابينة التحقيق الصوتي",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "إعادة البناء الصوتي (Phonological Reconstruction) وتتبع التحول عبر 8 لغات سامية",
              style = MaterialTheme.typography.bodySmall,
              color = customColors.mutedText
            )
          }

          // Active playback badge
          if (activePlayingRoot != null) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFD4AF37).copy(alpha = 0.15f))
                .border(1.dp, Color(0xFFD4AF37), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  Icons.Default.GraphicEq,
                  contentDescription = null,
                  tint = Color(0xFFD4AF37),
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = activePlayingLang ?: "جاري النطق",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFFD4AF37),
                  fontSize = 10.sp
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search & Speed Controls Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("بحث في الجذور (ملك، بيت، سلم، رأس، إله، كتب...)") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
          )

          // Speed selector
          FilterChip(
            selected = true,
            onClick = {
              playbackSpeed = when (playbackSpeed) {
                0.75f -> 0.9f
                0.9f -> 1.15f
                else -> 0.75f
              }
            },
            label = {
              Text(
                text = "${playbackSpeed}x",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            },
            leadingIcon = {
              Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(14.dp))
            }
          )

          if (isSequentialPlaying || activePlayingRoot != null) {
            IconButton(
              onClick = {
                audioEngine.stop()
                isSequentialPlaying = false
                activePlayingRoot = null
                activePlayingLang = null
              },
              modifier = Modifier.size(38.dp)
            ) {
              Icon(Icons.Default.StopCircle, contentDescription = "إيقاف", tint = MaterialTheme.colorScheme.error)
            }
          }
        }
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(roots, key = { it.root }) { item ->
        val isExpanded = expandedRoot == item.root
        val isRootActive = activePlayingRoot == item.root

        RootCard(
          item = item,
          isExpanded = isExpanded,
          isRootActive = isRootActive,
          activeLang = if (isRootActive) activePlayingLang else null,
          playbackSpeed = playbackSpeed,
          wavePhase = wavePhase,
          onToggleExpand = {
            expandedRoot = if (isExpanded) null else item.root
          },
          onSpeakProtoSemitic = { protoWord ->
            audioEngine.stop()
            isSequentialPlaying = false
            activePlayingRoot = item.root
            activePlayingLang = "السامية الأم"
            audioEngine.speakPhoneticReconstruction(
              rawWord = protoWord,
              languageName = "Proto-Semitic",
              speed = playbackSpeed,
              playChimeFirst = true,
              onDone = {
                if (activePlayingLang == "السامية الأم") {
                  activePlayingRoot = null
                  activePlayingLang = null
                }
              }
            )
          },
          onPlaySingleLanguage = { langName, word ->
            audioEngine.stop()
            isSequentialPlaying = false
            activePlayingRoot = item.root
            activePlayingLang = langName
            audioEngine.speakPhoneticReconstruction(
              rawWord = word,
              languageName = langName,
              speed = playbackSpeed,
              playChimeFirst = true,
              onDone = {
                if (activePlayingLang == langName) {
                  activePlayingRoot = null
                  activePlayingLang = null
                }
              }
            )
          },
          onPlaySequential = {
            if (isSequentialPlaying && activePlayingRoot == item.root) {
              audioEngine.stop()
              isSequentialPlaying = false
              activePlayingRoot = null
              activePlayingLang = null
            } else {
              isSequentialPlaying = true
              activePlayingRoot = item.root
              val protoForm = item.phoneticEvolutionIPA.split(">").firstOrNull()?.trim() ?: "*${item.root}"
              val steps = listOf(
                "السامية الأم" to protoForm,
                "الأكادية" to item.akkadian,
                "الأوغاريتية" to item.ugaritic,
                "الفينيقية" to item.phoenician,
                "العبرية" to item.hebrew,
                "الآرامية" to item.aramaic,
                "السبئية" to item.sabaic,
                "الجعزية" to item.geez,
                "العربية" to item.arabic
              )

              audioEngine.playSequentialEvolution(
                steps = steps,
                speed = playbackSpeed,
                onStepChanged = { _, lang, _ ->
                  activePlayingLang = lang
                },
                onComplete = {
                  isSequentialPlaying = false
                  activePlayingRoot = null
                  activePlayingLang = null
                }
              )
            }
          },
          onSpeakExplanation = { text ->
            audioEngine.stop()
            isSequentialPlaying = false
            activePlayingRoot = item.root
            activePlayingLang = "التحليل الفيلولوجي"
            audioEngine.speak(text, speed = playbackSpeed) {
              activePlayingRoot = null
              activePlayingLang = null
            }
          }
        )
      }
    }
  }
}

@Composable
fun RootCard(
  item: ComparativeLexiconRoot,
  isExpanded: Boolean,
  isRootActive: Boolean,
  activeLang: String?,
  playbackSpeed: Float,
  wavePhase: Float,
  onToggleExpand: () -> Unit,
  onSpeakProtoSemitic: (String) -> Unit,
  onPlaySingleLanguage: (String, String) -> Unit,
  onPlaySequential: () -> Unit,
  onSpeakExplanation: (String) -> Unit
) {
  val customColors = LocalCustomColors.current
  val protoSemiticForm = remember(item.phoneticEvolutionIPA, item.root) {
    item.phoneticEvolutionIPA.split(">").firstOrNull()?.trim() ?: "*${item.root}"
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .then(
        if (isRootActive) {
          Modifier.border(1.5.dp, Color(0xFFD4AF37), RoundedCornerShape(16.dp))
        } else {
          Modifier
        }
      ),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isRootActive) 4.dp else 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Root & Proto-Semitic Reconstruction
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "الجذر السامي: ${item.root}",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "الدلالة: ${item.protoMeaningAr} (${item.protoMeaningEn})",
            style = MaterialTheme.typography.bodySmall,
            color = customColors.mutedText
          )
        }

        // Dedicated Proto-Semitic Audio Reconstruction Button
        Button(
          onClick = { onSpeakProtoSemitic(protoSemiticForm) },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (activeLang == "السامية الأم") Color(0xFFD4AF37) else MaterialTheme.colorScheme.primaryContainer,
            contentColor = if (activeLang == "السامية الأم") Color.Black else MaterialTheme.colorScheme.onPrimaryContainer
          ),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = protoSemiticForm,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            fontSize = 12.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Phonological Audio Studio Banner Controls
      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, customColors.border)
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Sequential Autoplay Button
            FilledTonalButton(
              onClick = onPlaySequential,
              colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = if (isRootActive && activeLang != null && activeLang != "السامية الأم" && activeLang != "التحليل الفيلولوجي") {
                  Color(0xFFD4AF37)
                } else {
                  MaterialTheme.colorScheme.secondaryContainer
                }
              ),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(
                if (isRootActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                if (isRootActive) "إيقاف التتابع الصوتي" else "تشغيل تتابع النطق الصوتي (9 مراحل)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }

            if (isRootActive) {
              Text(
                text = "جاري نطق: $activeLang",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFFD4AF37),
                fontSize = 11.sp
              )
            }
          }

          // Live animated audio waveform
          if (isRootActive) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                .padding(horizontal = 8.dp)
            ) {
              Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val midY = h / 2f
                val barCount = 28
                val step = w / barCount

                for (i in 0 until barCount) {
                  val x = i * step + step / 2f
                  val amplitude = (sin(wavePhase + i * 0.45f) * 0.4f + 0.5f) * h * 0.85f

                  drawLine(
                    color = Color(0xFFD4AF37),
                    start = Offset(x, midY - amplitude / 2f),
                    end = Offset(x, midY + amplitude / 2f),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 8-Language Cognate Table with Individual Audio Controls
      Text(
        text = "المفردات المقارنة وأدوات التحقيق الصوتي:",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = customColors.mutedText
      )

      Spacer(modifier = Modifier.height(4.dp))

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(MaterialTheme.colorScheme.surface)
          .border(1.dp, customColors.border, RoundedCornerShape(10.dp))
          .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        InteractiveCognateRow(
          langName = "الأكادية (Akkadian)",
          wordForm = item.akkadian,
          isActive = isRootActive && activeLang == "الأكادية",
          onPlay = { onPlaySingleLanguage("الأكادية", item.akkadian) }
        )
        InteractiveCognateRow(
          langName = "الأوغاريتية (Ugaritic)",
          wordForm = item.ugaritic,
          isActive = isRootActive && activeLang == "الأوغاريتية",
          onPlay = { onPlaySingleLanguage("الأوغاريتية", item.ugaritic) }
        )
        InteractiveCognateRow(
          langName = "الفينيقية (Phoenician)",
          wordForm = item.phoenician,
          isActive = isRootActive && activeLang == "الفينيقية",
          onPlay = { onPlaySingleLanguage("الفينيقية", item.phoenician) }
        )
        InteractiveCognateRow(
          langName = "العبرية (Hebrew)",
          wordForm = item.hebrew,
          isActive = isRootActive && activeLang == "العبرية",
          onPlay = { onPlaySingleLanguage("العبرية", item.hebrew) }
        )
        InteractiveCognateRow(
          langName = "الآرامية (Aramaic)",
          wordForm = item.aramaic,
          isActive = isRootActive && activeLang == "الآرامية",
          onPlay = { onPlaySingleLanguage("الآرامية", item.aramaic) }
        )
        InteractiveCognateRow(
          langName = "العربية (Arabic)",
          wordForm = item.arabic,
          isActive = isRootActive && activeLang == "العربية",
          onPlay = { onPlaySingleLanguage("العربية", item.arabic) }
        )
        InteractiveCognateRow(
          langName = "السبئية (Sabaic)",
          wordForm = item.sabaic,
          isActive = isRootActive && activeLang == "السبئية",
          onPlay = { onPlaySingleLanguage("السبئية", item.sabaic) }
        )
        InteractiveCognateRow(
          langName = "الجعزية (Ge'ez)",
          wordForm = item.geez,
          isActive = isRootActive && activeLang == "الجعزية",
          onPlay = { onPlaySingleLanguage("الجعزية", item.geez) }
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Trajectory & Law
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "مسار التطور الصوتي (IPA):",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.secondary
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { onSpeakExplanation("${item.phoneticEvolutionIPA}. ${item.soundShiftLawAr}") },
            modifier = Modifier.size(30.dp)
          ) {
            Icon(Icons.Default.VolumeUp, contentDescription = "استماع للشرح", modifier = Modifier.size(16.dp))
          }

          TextButton(onClick = onToggleExpand) {
            Text(if (isExpanded) "طي القوانين ▲" else "قوانين التحول ▼", fontSize = 11.sp)
          }
        }
      }

      Text(
        text = item.phoneticEvolutionIPA,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.primary
      )

      if (isExpanded) {
        Spacer(modifier = Modifier.height(8.dp))
        Divider(color = customColors.border)
        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "التحليل الفيلولوجي والقانون الصوتي الحاكم:",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )
        Text(
          text = item.soundShiftLawAr,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface,
          lineHeight = 18.sp
        )
      }
    }
  }
}

@Composable
fun InteractiveCognateRow(
  langName: String,
  wordForm: String,
  isActive: Boolean,
  onPlay: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .then(
        if (isActive) {
          Modifier
            .background(Color(0xFFD4AF37).copy(alpha = 0.12f))
            .border(1.dp, Color(0xFFD4AF37), RoundedCornerShape(8.dp))
        } else {
          Modifier.background(Color.Transparent)
        }
      )
      .padding(horizontal = 8.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = langName,
      style = MaterialTheme.typography.labelSmall,
      color = if (isActive) MaterialTheme.colorScheme.primary else customColors.mutedText,
      fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
      fontSize = 11.sp
    )

    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Text(
        text = wordForm,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
        color = if (isActive) Color(0xFFD4AF37) else MaterialTheme.colorScheme.onSurface,
        fontSize = 11.sp
      )

      IconButton(
        onClick = onPlay,
        modifier = Modifier.size(28.dp)
      ) {
        Icon(
          if (isActive) Icons.Default.GraphicEq else Icons.Default.VolumeUp,
          contentDescription = "استماع $langName",
          tint = if (isActive) Color(0xFFD4AF37) else MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(15.dp)
        )
      }
    }
  }
}

