package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PhonemeItem
import com.example.data.repository.LexiconAndPhoneticsData
import com.example.ui.theme.LocalCustomColors
import com.example.util.AudioEngine

data class SoundLawCardData(
  val titleAr: String,
  val formula: String,
  val explanationAr: String,
  val examples: List<String>,
  val audioSample: String
)

@Composable
fun PhoneticsSimulatorScreen(
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  val audioEngine = remember { AudioEngine(context) }

  var selectedTab by remember { mutableStateOf(0) } // 0: Sound Laws, 1: IPA Matrix, 2: Syllable Sandbox
  var selectedPhoneme by remember { mutableStateOf<PhonemeItem?>(null) }
  var sandboxInputText by remember { mutableStateOf("مَلِك") }

  DisposableEffect(Unit) {
    onDispose {
      audioEngine.release()
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
        Text(
          text = "محاكي علم الأصوات والتحولات الصوتية السامية",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "محاكاة تفاعلية للقوانين الصوتية ومصفوفة مخارج الحروف IPA والترددات السمعية",
          style = MaterialTheme.typography.bodySmall,
          color = customColors.mutedText
        )

        Spacer(modifier = Modifier.height(10.dp))

        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color.Transparent,
          divider = {}
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = { Text("قوانين التحول الصوتي", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = { Text("مصفوفة IPA والنغمات", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
          )
          Tab(
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            text = { Text("مختبر تحليل الكلمات والنبر", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
          )
        }
      }
    }

    // Content based on tab
    when (selectedTab) {
      0 -> SoundLawsTab(audioEngine)
      1 -> IpaMatrixTab(
        phonemes = LexiconAndPhoneticsData.phonemes,
        selectedPhoneme = selectedPhoneme,
        onSelectPhoneme = { ph ->
          selectedPhoneme = ph
          audioEngine.playAcousticFormantChime(ph.f1Hz, ph.f2Hz)
        }
      )
      2 -> SyllableSandboxTab(
        inputText = sandboxInputText,
        onInputChange = { sandboxInputText = it },
        audioEngine = audioEngine
      )
    }
  }
}

@Composable
fun SoundLawsTab(audioEngine: AudioEngine) {
  val customColors = LocalCustomColors.current

  val soundLaws = listOf(
    SoundLawCardData(
      titleAr = "التحول الصوتي الكنعاني (Canaanite Shift)",
      formula = "*ā → /ō/ (or /ū/ in Phoenician)",
      explanationAr = "تحول الألف الممدودة الموقعة تحت النبر إلى واو مضمومة في الفروع الكنعانية (الفينيقية، العبرية، المؤابية).",
      examples = listOf(
        "السامية الأم: *šalām- (سلام)",
        "العبرية التوراتية: שָׁלוֹם (šālôm)",
        "الفينيقية: 𐤔𐤋𐤌 (šulūm)",
        "العربية الفصحى (حافظت على الألف): سَلَام (salām)"
      ),
      audioSample = "شالوم... سلام"
    ),
    SoundLawCardData(
      titleAr = "قانون بجد كفت (Begadkefat Spirantization)",
      formula = "b, g, d, k, p, t → [v], [ɣ], [ð], [x], [f], [θ] post-vocalically",
      explanationAr = "تحول الصوامت الانفجارية الستة إلى صوامت احتكاكية لينة إذا وقعت مباشرة بعد حركة صوتية.",
      examples = listOf(
        "مطلق: كَتَبَ [kataba]",
        "في العبرية والآرامية بعد حركة: יִכְתֹּב [jixtov] (انقلاب الكاف خاءً والباء فاءً/v)"
      ),
      audioSample = "يختوف... كتب"
    ),
    SoundLawCardData(
      titleAr = "انكماش المزدوجات الصوتية (Monophthongization)",
      formula = "*ay → /ē/  &  *aw → /ō/",
      explanationAr = "اندماج الواو والياء بعد الفتحة في حركة طويلة ممتدة في الأكادية والفينيقية والجعزية والآرامية.",
      examples = listOf(
        "السامية الأم: *bayt- (بَيْت)",
        "الفينيقية والجعزية والآرامية: bēt (بِيتْ / 𐤁𐤕)",
        "العربية والمسند (حفظ المزدوج): بَيْت (bayt)"
      ),
      audioSample = "بيت... بيت"
    )
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    items(soundLaws) { law ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = law.titleAr,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            IconButton(
              onClick = { audioEngine.speak(law.audioSample) },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(Icons.Default.VolumeUp, contentDescription = "استماع", modifier = Modifier.size(18.dp))
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Formula box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.surface)
              .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
              .padding(8.dp)
          ) {
            Text(
              text = law.formula,
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.secondary
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = law.explanationAr,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
          )

          Spacer(modifier = Modifier.height(8.dp))

          law.examples.forEach { ex ->
            Text(
              text = "• $ex",
              style = MaterialTheme.typography.bodySmall,
              color = customColors.mutedText,
              modifier = Modifier.padding(vertical = 2.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun IpaMatrixTab(
  phonemes: List<PhonemeItem>,
  selectedPhoneme: PhonemeItem?,
  onSelectPhoneme: (PhonemeItem) -> Unit
) {
  val customColors = LocalCustomColors.current

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    Text(
      text = "انقر على أي فونيم لتوليد رنين الفورمانت الحنجري (F1 / F2 Formant Chime) واستعراض مخارجه:",
      style = MaterialTheme.typography.bodySmall,
      color = customColors.mutedText
    )

    Spacer(modifier = Modifier.height(10.dp))

    LazyVerticalGrid(
      columns = GridCells.Adaptive(minSize = 90.dp),
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(phonemes) { item ->
        val isSelected = selectedPhoneme?.ipa == item.ipa

        Card(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelectPhoneme(item) }
            .border(
              width = if (isSelected) 2.dp else 1.dp,
              color = if (isSelected) MaterialTheme.colorScheme.primary else customColors.border,
              shape = RoundedCornerShape(12.dp)
            ),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else customColors.cardBackground
          )
        ) {
          Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = item.ipa,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary,
              textAlign = TextAlign.Center
            )
            Text(
              text = item.arabicLetter,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "${item.f1Hz} / ${item.f2Hz} Hz",
              fontSize = 9.sp,
              color = customColors.mutedText
            )
          }
        }
      }
    }

    // Selected Phoneme Detail Panel
    selectedPhoneme?.let { ph ->
      Spacer(modifier = Modifier.height(10.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "الفونيم: ${ph.ipa} (${ph.arabicLetter})",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = "الرموز: ${ph.ancientGlyph}",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.secondary
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "المخرج والصفة: ${ph.categoryAr} (${ph.categoryEn})",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "التموضع الفيزيولوجي: ${ph.articulationAr}",
            style = MaterialTheme.typography.bodySmall,
            color = customColors.mutedText
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "ملاحظة تاريخية: ${ph.soundLawNoteAr}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

@Composable
fun SyllableSandboxTab(
  inputText: String,
  onInputChange: (String) -> Unit,
  audioEngine: AudioEngine
) {
  val customColors = LocalCustomColors.current

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    Text(
      text = "مختبر تقطيع المقاطع والنبر الصوتي السامي:",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurface
    )

    OutlinedTextField(
      value = inputText,
      onValueChange = onInputChange,
      modifier = Modifier.fillMaxWidth(),
      label = { Text("أدخل كلمة سامية أو كنعانية للتحليل") },
      shape = RoundedCornerShape(12.dp)
    )

    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "التحليل المقطعي الفيلولوجي المقدر:",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "• البنية المقطعية: CV-CVC (مقطع قصير مفتوح + مقطع طويل مغلق)", style = MaterialTheme.typography.bodyMedium)
        Text(text = "• موقع النبر السامي الأساسي (Stress): على المقطع قبل الأخير (Penult)", style = MaterialTheme.typography.bodyMedium)
        Text(text = "• التدوين الصوتي الدولي (IPA): /ˈmal.ku/ أو /ˈma.lik/", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)

        Spacer(modifier = Modifier.height(14.dp))

        Button(
          onClick = { audioEngine.speak(inputText) },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.VolumeUp, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text("استماع لنطق الكلمة بمحرك الصوتيات")
        }
      }
    }
  }
}
