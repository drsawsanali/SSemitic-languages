package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.models.AcademicChapter
import com.example.data.models.ChapterSection
import com.example.data.repository.ChaptersData
import com.example.ui.components.EpigraphicGlossaryComponent
import com.example.ui.theme.LocalCustomColors
import com.example.ui.viewmodel.EncyclopediaCacheViewModel
import com.example.util.AudioEngine
import com.example.util.CitationGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChaptersReaderScreen(
  initialChapterId: String? = null,
  modifier: Modifier = Modifier,
  cacheViewModel: EncyclopediaCacheViewModel = viewModel()
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  val audioEngine = remember { AudioEngine(context) }

  var selectedChapterIndex by remember {
    val idx = ChaptersData.list.indexOfFirst { it.id == initialChapterId }
    mutableStateOf(if (idx >= 0) idx else 0)
  }

  val chapter = ChaptersData.list.getOrElse(selectedChapterIndex) { ChaptersData.list.first() }

  // Automatically cache loaded chapter in Room persistence for offline reading
  LaunchedEffect(chapter.id) {
    cacheViewModel.cacheChapter(chapter)
  }

  var isSpeaking by remember { mutableStateOf(false) }
  var speechSpeed by remember { mutableStateOf(1.0f) }
  var currentSpeakingSectionIndex by remember { mutableStateOf(-1) }
  var showCitationDialog by remember { mutableStateOf(false) }
  var showGlossarySheet by remember { mutableStateOf(false) }
  var fontSizeSp by remember { mutableStateOf(16) }

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
    // Reader Controls Top Bar
    Surface(
      color = customColors.cardBackground,
      shadowElevation = 2.dp
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        // Chapter selector scrollable row
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          itemsIndexed(ChaptersData.list) { index, item ->
            FilterChip(
              selected = selectedChapterIndex == index,
              onClick = {
                audioEngine.stop()
                isSpeaking = false
                currentSpeakingSectionIndex = -1
                selectedChapterIndex = index
              },
              label = {
                Text(
                  text = "فصل ${item.chapterNumber}: ${item.titleAr.take(18)}...",
                  fontSize = 12.sp,
                  fontWeight = if (selectedChapterIndex == index) FontWeight.Bold else FontWeight.Normal
                )
              }
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Actions & Audio Toolbar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            FilledIconButton(
              onClick = {
                if (isSpeaking) {
                  audioEngine.stop()
                  isSpeaking = false
                  currentSpeakingSectionIndex = -1
                } else {
                  isSpeaking = true
                  val fullText = chapter.sections.joinToString(". ") { it.bodyTextAr }
                  audioEngine.speak(fullText, speechSpeed) {
                    isSpeaking = false
                    currentSpeakingSectionIndex = -1
                  }
                }
              },
              colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (isSpeaking) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
              ),
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = if (isSpeaking) "إيقاف" else "قراءة صوتية",
                modifier = Modifier.size(20.dp)
              )
            }

            // Speed Selector
            TextButton(
              onClick = {
                speechSpeed = when (speechSpeed) {
                  0.75f -> 1.0f
                  1.0f -> 1.25f
                  1.25f -> 1.5f
                  else -> 0.75f
                }
                if (isSpeaking) {
                  audioEngine.stop()
                  val fullText = chapter.sections.joinToString(". ") { it.bodyTextAr }
                  audioEngine.speak(fullText, speechSpeed) {
                    isSpeaking = false
                  }
                }
              },
              contentPadding = PaddingValues(horizontal = 6.dp)
            ) {
              Text("${speechSpeed}x", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            // Font Sizer
            IconButton(
              onClick = { if (fontSizeSp > 13) fontSizeSp -= 2 },
              modifier = Modifier.size(32.dp)
            ) {
              Text("A-", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(
              onClick = { if (fontSizeSp < 24) fontSizeSp += 2 },
              modifier = Modifier.size(32.dp)
            ) {
              Text("A+", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
          }

          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedButton(
              onClick = { showGlossarySheet = true },
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("المسرد الإبيغرافي", fontSize = 11.sp)
            }

            OutlinedButton(
              onClick = { showCitationDialog = true },
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.FormatQuote, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("توثيق واقتباس", fontSize = 11.sp)
            }

            FilledTonalButton(
              onClick = {
                val texContent = "% LaTeX Source for Chapter ${chapter.chapterNumber}\n\\section{${chapter.titleAr}}\n" +
                  chapter.sections.joinToString("\n\n") { "\\subsection{${it.headingAr}}\n${it.bodyTextAr}" }
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("LaTeX Chapter", texContent))
                Toast.makeText(context, "تم نسخ كود LaTeX الأكاديمي للفصل", Toast.LENGTH_LONG).show()
              },
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
              shape = RoundedCornerShape(8.dp)
            ) {
              Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("LaTeX", fontSize = 11.sp)
            }
          }
        }
      }
    }

    // Main Reader Content
    AnimatedContent(
      targetState = selectedChapterIndex,
      transitionSpec = {
        val direction = if (targetState >= initialState) 1 else -1
        (slideInHorizontally(
          initialOffsetX = { fullWidth -> direction * (fullWidth / 6) },
          animationSpec = tween(350, easing = FastOutSlowInEasing)
        ) + fadeIn(
          animationSpec = tween(350, easing = FastOutSlowInEasing)
        )).togetherWith(
          slideOutHorizontally(
            targetOffsetX = { fullWidth -> -direction * (fullWidth / 6) },
            animationSpec = tween(280, easing = FastOutSlowInEasing)
          ) + fadeOut(
            animationSpec = tween(200, easing = FastOutSlowInEasing)
          )
        )
      },
      label = "ChapterReaderTransition",
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
    ) { targetIndex ->
      val activeChapter = ChaptersData.list.getOrElse(targetIndex) { ChaptersData.list.first() }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Chapter Header Card
        item {
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
                  text = "الفصل الأكاديمي ${activeChapter.chapterNumber}",
                  style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
                Text(
                  text = activeChapter.titleEn,
                  style = MaterialTheme.typography.labelSmall,
                  color = customColors.mutedText
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = activeChapter.titleAr,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "إشراف: أ.د. أحمد فقعس",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                  )
                  Text(
                    text = "إعداد: سوسن علي عبدالله الحضوري",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
                Text(
                  text = "جامعة صنعاء • قسم الآثار",
                  style = MaterialTheme.typography.bodySmall,
                  color = customColors.mutedText
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Offline Room Persistence Status & Action
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = Color(0xFF2E7D32).copy(alpha = 0.12f)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      Icons.Default.CloudDone,
                      contentDescription = null,
                      tint = Color(0xFF2E7D32),
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "محفوظ محلياً في Room (جاهز دون اتصال)",
                      fontSize = 11.sp,
                      color = Color(0xFF2E7D32),
                      fontWeight = FontWeight.Bold
                    )
                  }
                }

                FilledTonalButton(
                  onClick = {
                    cacheViewModel.downloadAllChapters()
                  },
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Icon(Icons.Default.DownloadForOffline, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("تنزيل كل الفصول", fontSize = 11.sp)
                }
              }
            }
          }
        }

        // Sections
        itemsIndexed(activeChapter.sections) { index, section ->
          val isCurrentSpeaking = currentSpeakingSectionIndex == index

          SectionCard(
            section = section,
            fontSizeSp = fontSizeSp,
            isHighlight = isCurrentSpeaking,
            onSpeakSection = {
              currentSpeakingSectionIndex = index
              isSpeaking = true
              audioEngine.speak(section.bodyTextAr, speechSpeed) {
                isSpeaking = false
                currentSpeakingSectionIndex = -1
              }
            }
          )
        }

        // Bottom Navigation
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = customColors.cardBackground)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedButton(
                onClick = {
                  if (selectedChapterIndex > 0) {
                    audioEngine.stop()
                    isSpeaking = false
                    currentSpeakingSectionIndex = -1
                    selectedChapterIndex--
                  }
                },
                enabled = selectedChapterIndex > 0,
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("الفصل السابق", fontSize = 12.sp)
              }

              Text(
                text = "${targetIndex + 1} من ${ChaptersData.list.size}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = customColors.mutedText
              )

              Button(
                onClick = {
                  if (selectedChapterIndex < ChaptersData.list.size - 1) {
                    audioEngine.stop()
                    isSpeaking = false
                    currentSpeakingSectionIndex = -1
                    selectedChapterIndex++
                  }
                },
                enabled = selectedChapterIndex < ChaptersData.list.size - 1,
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("الفصل التالي", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
              }
            }
          }
          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    }
  }

  // Citation Modal
  if (showCitationDialog) {
    AlertDialog(
      onDismissRequest = { showCitationDialog = false },
      title = { Text("توثيق واقتباس الفصل الأكاديمي") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          CitationBox("APA (7th Edition)", CitationGenerator.generateChapterCitation(chapter, "APA")) {
            copyTextToClipboard(context, it, "APA")
          }
          CitationBox("MLA (9th Edition)", CitationGenerator.generateChapterCitation(chapter, "MLA")) {
            copyTextToClipboard(context, it, "MLA")
          }
          CitationBox("Chicago (17th Edition)", CitationGenerator.generateChapterCitation(chapter, "Chicago")) {
            copyTextToClipboard(context, it, "Chicago")
          }
          CitationBox("BibTeX Entry", CitationGenerator.generateChapterCitation(chapter, "BibTeX")) {
            copyTextToClipboard(context, it, "BibTeX")
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showCitationDialog = false }) {
          Text("إغلاق")
        }
      }
    )
  }

  // Interactive Epigraphic Glossary Bottom Sheet
  if (showGlossarySheet) {
    ModalBottomSheet(
      onDismissRequest = { showGlossarySheet = false },
      sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
      containerColor = MaterialTheme.colorScheme.surface,
      shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight(0.88f)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "المسرد الإبيغرافي المرجعي",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "راجع المفاهيم الإبيغرافية واللسانية أثناء قراءة الفصل",
              fontSize = 11.sp,
              color = customColors.mutedText
            )
          }
          IconButton(onClick = { showGlossarySheet = false }) {
            Icon(Icons.Default.Close, contentDescription = "إغلاق")
          }
        }
        EpigraphicGlossaryComponent(
          modifier = Modifier.fillMaxSize()
        )
      }
    }
  }
}

@Composable
fun SectionCard(
  section: ChapterSection,
  fontSizeSp: Int,
  isHighlight: Boolean,
  onSpeakSection: () -> Unit
) {
  val customColors = LocalCustomColors.current

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(
        width = if (isHighlight) 2.dp else 1.dp,
        color = if (isHighlight) MaterialTheme.colorScheme.primary else customColors.border,
        shape = RoundedCornerShape(12.dp)
      ),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isHighlight) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else customColors.cardBackground
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = section.headingAr,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )

        IconButton(onClick = onSpeakSection, modifier = Modifier.size(28.dp)) {
          Icon(Icons.Default.VolumeUp, contentDescription = "استماع للمقطع", modifier = Modifier.size(16.dp))
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = section.bodyTextAr,
        style = MaterialTheme.typography.bodyLarge.copy(
          fontSize = fontSizeSp.sp,
          lineHeight = (fontSizeSp * 1.6).sp
        ),
        color = MaterialTheme.colorScheme.onSurface
      )

      // Interactive Table if available
      if (section.comparisonTableData.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        InteractiveMorphologyTable(section.comparisonTableData)
      }
    }
  }
}

@Composable
fun InteractiveMorphologyTable(tableData: List<List<String>>) {
  val customColors = LocalCustomColors.current
  if (tableData.isEmpty()) return

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(8.dp)) {
      Text(
        text = "جدول مقارن وتصريف مورفولوجي",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
      )
      Spacer(modifier = Modifier.height(6.dp))

      tableData.forEachIndexed { rowIndex, row ->
        val isHeader = rowIndex == 0
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(if (isHeader) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
            .padding(vertical = 4.dp, horizontal = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          row.forEach { cell ->
            Text(
              text = cell,
              style = if (isHeader) MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodySmall,
              color = if (isHeader) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.weight(1f),
              fontSize = 11.sp
            )
          }
        }
        if (rowIndex < tableData.size - 1) {
          Divider(color = customColors.border.copy(alpha = 0.5f))
        }
      }
    }
  }
}

@Composable
fun CitationBox(styleTitle: String, citationText: String, onCopy: (String) -> Unit) {
  val customColors = LocalCustomColors.current
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(8.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = styleTitle, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
        IconButton(onClick = { onCopy(citationText) }, modifier = Modifier.size(24.dp)) {
          Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", modifier = Modifier.size(14.dp))
        }
      }
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = citationText, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
    }
  }
}

fun copyTextToClipboard(context: Context, text: String, label: String) {
  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
  Toast.makeText(context, "تم نسخ التوثيق بنجاح", Toast.LENGTH_SHORT).show()
}
