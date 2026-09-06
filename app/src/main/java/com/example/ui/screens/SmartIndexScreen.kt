package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCustomColors

enum class IndexCategory(val titleAr: String, val titleEn: String) {
  ALL("الكل", "All"),
  PROSOPOGRAPHY("الأعلام والملوك", "Prosopography"),
  EPIGRAPHY("النقوش والوثائق", "Epigraphy"),
  TOPONYMY("المواقع والجغرافيا", "Toponymy"),
  PHONETICS("القوانين الصوتية", "Phonetics & Sound Laws"),
  ROOTS("الجذور السامية", "Comparative Roots")
}

data class IndexEntry(
  val id: String,
  val termAr: String,
  val termEn: String,
  val ancientScript: String = "",
  val category: IndexCategory,
  val occurrencesCount: Int,
  val chaptersReferenced: List<Int>,
  val descriptionAr: String,
  val latexKey: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartIndexScreen(
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current
  val clipboardManager = LocalClipboardManager.current
  var snackbarMessage by remember { mutableStateOf<String?>(null) }

  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf(IndexCategory.ALL) }
  var selectedLetter by remember { mutableStateOf<String?>(null) }

  val arabicAlphabet = remember {
    listOf("الكل", "أ", "ب", "ت", "ث", "ج", "ح", "خ", "د", "ذ", "ر", "ز", "س", "ش", "ص", "ض", "ط", "ظ", "ع", "غ", "ف", "ق", "ك", "ل", "م", "ن", "هـ", "و", "ي")
  }

  val sampleEntries = remember {
    listOf(
      IndexEntry("1", "أحيرام (ملك جبيل)", "Ahiram of Byblos", "𐤀𐤇𐤓𐤌", IndexCategory.PROSOPOGRAPHY, 24, listOf(1, 2, 5, 41), "ملك جبيل الفينيقي، صاحب أقدم نقش أبجدي فينيقي مكتمل على تابوت حجري بازلتي.", "\\index{Ahiram@أحيرام}"),
      IndexEntry("2", "ميشع (ملك مؤاب)", "Mesha of Moab", "𐤌𐤉𐤔𐤏", IndexCategory.PROSOPOGRAPHY, 32, listOf(3, 4, 12, 42), "ملك مؤاب الثائر ضد بيت عمري، صاحب مسلة ميشع الشهيرة في ذيبان (840 ق.م).", "\\index{Mesha@ميشع}"),
      IndexEntry("3", "حمورابي (ملك بابل)", "Hammurabi of Babylon", "𒄩𒄠𒈬𒊏𒁉", IndexCategory.PROSOPOGRAPHY, 45, listOf(1, 6, 8, 43), "سادس ملوك السلالة البابلية الأولى، مدون مسلة الشريعة البابلية الشهيرة.", "\\index{Hammurabi@حمورابي}"),
      IndexEntry("4", "عيزانا (ملك أكسوم)", "Ezana of Aksum", "ዔዛና", IndexCategory.PROSOPOGRAPHY, 18, listOf(31, 32, 45), "ملك أكسوم في القرن الرابع الميلادي، صاحب النقش الثلاثي بالجعزية والمسند واليونانية.", "\\index{Ezana@عيزانا}"),
      IndexEntry("5", "شرحبيل يعفر", "Sharahbil Ya'fur", "𐩦𐩧𐩢𐩨𐩡 𐩺𐩲𐩰𐩧", IndexCategory.PROSOPOGRAPHY, 14, listOf(21, 22, 44), "ملك حمير وسبأ، مجدد ترميم سد مأرب العظيم في نقش صهيد مسندي تاريخي.", "\\index{Sharahbil@شرحبيل يعفر}"),
      IndexEntry("6", "مسلة ميشع المؤابية", "Mesha Stele", "𐤌𐤑𐤁𐤕 𐤌𐤉𐤔𐤏", IndexCategory.EPIGRAPHY, 38, listOf(3, 4, 12, 42), "مسلة حجرية بازلتية من 34 سطراً تؤرخ انتصارات مؤاب على مملكة إسرائيل.", "\\index{MeshaStele@مسلة ميشع}"),
      IndexEntry("7", "تابوت أحيرام الجبيلي", "Ahiram Sarcophagus", "𐤀𐤓𐤍 𐤆 𐤐𐤏𐤋 𐤀𐤕𐤁𐤏𐤋", IndexCategory.EPIGRAPHY, 28, listOf(1, 2, 5, 41), "أقدم نقش فينيقي كنعاني كلاسيكي بجبيل يؤرخ للقرن العاشر ق.م.", "\\index{AhiramSarcophagus@تابوت أحيرام}"),
      IndexEntry("8", "نقش صرواح السبئي الكبير", "Great Sirwah Inscription", "𐩬𐩤𐩦 𐩮𐩧𐩥𐩢", IndexCategory.EPIGRAPHY, 22, listOf(21, 23, 44), "نقش النصر للمكرب السبئي كربئيل وتر الأول في معبد أوعال صرواح.", "\\index{SirwahInscription@نقش صرواح}"),
      IndexEntry("9", "رُقم رأس الشمرا (أوغاريت)", "Ras Shamra Tablets", "𐎍𐎜𐎈 𐎜𐎂𐎗𐎚", IndexCategory.EPIGRAPHY, 40, listOf(2, 6, 15, 43), "الأرشيف الطيني المكتوب بالأبجدية المسمارية في الساحل السوري.", "\\index{RasShamra@رقم رأس الشمرا}"),
      IndexEntry("10", "جبيل (بيبلوس)", "Byblos / Gubla", "𐤂𐤁𐤋", IndexCategory.TOPONYMY, 52, listOf(1, 2, 5, 10, 41), "أعرق الموانئ الفينيقية على البحر المتوسط ومهد الأبجدية الخطية.", "\\index{Byblos@جبيل}"),
      IndexEntry("11", "أوغاريت (رأس الشمرا)", "Ugarit", "𐎜𐎂𐎗𐎚", IndexCategory.TOPONYMY, 48, listOf(2, 6, 15, 20), "مملكة وميناء سوري قديم ازدهر في العصر البرونزي المتأخر.", "\\index{Ugarit@أوغاريت}"),
      IndexEntry("12", "مأرب (عاصمة سبأ)", "Marib", "𐩣𐩧𐩺𐩨", IndexCategory.TOPONYMY, 36, listOf(21, 22, 25), "حاضرة مملكة سبأ الكبرى وموقع السد التاريخي ومحرم بلقيس.", "\\index{Marib@مأرب}"),
      IndexEntry("13", "أكسوم", "Aksum", "አክሱም", IndexCategory.TOPONYMY, 25, listOf(31, 32, 35), "عاصمة الإمبراطورية الأكسومية في المرتفعات الإثيوبية ومهد الحضارة الجعزية.", "\\index{Aksum@أكسوم}"),
      IndexEntry("14", "التحول الكنعاني (ā > ō)", "Canaanite Vowel Shift", "ā > ō", IndexCategory.PHONETICS, 42, listOf(2, 5, 11, 14), "قانون صوتي يتحول فيه الصائت الطويل المفتوح /ā/ إلى /ō/ في الكنعانية والفينيقية والعبرية.", "\\index{CanaaniteShift@التحول الكنعاني}"),
      IndexEntry("15", "قانون بجد كفت (Begadkefat)", "Begadkefat Law", "bgdkpt", IndexCategory.PHONETICS, 30, listOf(11, 13, 16), "تحول الصوامت الانفجارية الستة إلى احتكاكية رخوة بعد حركة صائتية في العبرية والآرامية.", "\\index{Begadkefat@قانون بجد كفت}"),
      IndexEntry("16", "سقوط الحلقيات الأكادي", "Akkadian Pharyngeal Loss", "ʿ, ḥ > e", IndexCategory.PHONETICS, 28, listOf(1, 8, 12), "سقوط أصوات الحلق (العين والحاء والهمزة) وتأثيرها في إمالة الصوائت نحو /e/ بالأكادية.", "\\index{AkkadianPharyngeal@سقوط الحلقيات الأكادي}"),
      IndexEntry("17", "الجذر *m-l-k (ملك/سيادة)", "Root *m-l-k", "√mlk", IndexCategory.ROOTS, 50, listOf(1, 5, 15, 25, 35), "جذر سامي مشترك يفيد التملك والسيادة والحكم عبر كافة الفروع السامية.", "\\index{RootMLK@الجذر ملك}"),
      IndexEntry("18", "الجذر *b-y-t (بيت/مسكن)", "Root *b-y-t", "√byt", IndexCategory.ROOTS, 46, listOf(2, 8, 18, 28, 38), "جذر سامي دال على المسكن والمأوى، ومصدر تسمية حرف الباء (بيت).", "\\index{RootBYT@الجذر بيت}"),
      IndexEntry("19", "الجذر *š-l-m (سلام/كمال)", "Root *š-l-m", "√šlm", IndexCategory.ROOTS, 55, listOf(1, 10, 20, 30, 40), "جذر سامي مشترك يفيد السلامة والوفاء والتحية في جميع لغات الأسرة السامية.", "\\index{RootSLM@الجذر سلم}")
    )
  }

  val filteredEntries = remember(searchQuery, selectedCategory, selectedLetter) {
    sampleEntries.filter { entry ->
      val matchesCategory = selectedCategory == IndexCategory.ALL || entry.category == selectedCategory
      val matchesLetter = selectedLetter == null || selectedLetter == "الكل" || entry.termAr.startsWith(selectedLetter!!)
      val matchesSearch = searchQuery.isBlank() ||
        entry.termAr.contains(searchQuery, ignoreCase = true) ||
        entry.termEn.contains(searchQuery, ignoreCase = true) ||
        entry.descriptionAr.contains(searchQuery, ignoreCase = true) ||
        entry.ancientScript.contains(searchQuery, ignoreCase = true)

      matchesCategory && matchesLetter && matchesSearch
    }
  }

  Scaffold(
    snackbarHost = {
      snackbarMessage?.let { msg ->
        Snackbar(
          modifier = Modifier.padding(16.dp),
          action = {
            TextButton(onClick = { snackbarMessage = null }) {
              Text("حسناً", color = Color.White)
            }
          }
        ) {
          Text(msg)
        }
      }
    }
  ) { paddingValues ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(paddingValues)
        .background(MaterialTheme.colorScheme.background)
    ) {
      // Header & Search
      Surface(
        color = customColors.cardBackground,
        shadowElevation = 2.dp
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = "نظام الفهرسة والكشاف الأكاديمي الذكي (Smart Index)",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "كشاف الأعلام، النقوش، المواقع، القوانين الصوتية، والجذور المقارنة مع دعم LaTeX",
            style = MaterialTheme.typography.bodySmall,
            color = customColors.mutedText
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("بحث في الكشاف (أحيرام، ميشع، جبيل، ā > ō...)") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Category Filter Chips
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(IndexCategory.values()) { category ->
              FilterChip(
                selected = selectedCategory == category,
                onClick = { selectedCategory = category },
                label = { Text(category.titleAr, fontSize = 11.sp) }
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Alphabet Bar
          LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(arabicAlphabet) { letter ->
              val isSelected = (selectedLetter == letter) || (selectedLetter == null && letter == "الكل")
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                  .clickable {
                    selectedLetter = if (letter == "الكل") null else letter
                  }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = letter,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      }

      // Entries List
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(filteredEntries, key = { it.id }) { entry ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = entry.category.titleAr,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                  )
                }

                Text(
                  text = "${entry.occurrencesCount} موضع ورود",
                  style = MaterialTheme.typography.labelSmall,
                  color = customColors.mutedText
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = entry.termAr,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = entry.termEn,
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.mutedText
                  )
                }

                if (entry.ancientScript.isNotBlank()) {
                  Text(
                    text = entry.ancientScript,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                  )
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = entry.descriptionAr,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
              )

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "الفصول المرجعية: ${entry.chaptersReferenced.joinToString(", ") { "ف$it" }}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.secondary
                )

                TextButton(
                  onClick = {
                    clipboardManager.setText(AnnotatedString(entry.latexKey))
                    snackbarMessage = "تم نسخ كود LaTeX: ${entry.latexKey}"
                  }
                ) {
                  Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("نسخ LaTeX", fontSize = 11.sp)
                }
              }
            }
          }
        }
      }
    }
  }
}
