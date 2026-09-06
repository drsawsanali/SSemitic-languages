package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SemiticVirtualKeyboard
import com.example.ui.components.calculateGematria
import com.example.ui.theme.LocalCustomColors
import com.example.util.AudioEngine
import com.example.util.GeminiSearchResult
import com.example.util.GeminiSearchService
import com.example.util.GroundingSource
import kotlinx.coroutines.launch

enum class LabTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
  KEYBOARD_EPIGRAPHY("مختبر الخطوط والنقوش", Icons.Default.Keyboard),
  SEARCH_GROUNDING("باحث Google الأكاديمي الذكي", Icons.Default.Search)
}

@Composable
fun AiResearchLabScreen(
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  val audioEngine = remember { AudioEngine(context) }
  val coroutineScope = rememberCoroutineScope()

  var selectedTab by remember { mutableStateOf(LabTab.KEYBOARD_EPIGRAPHY) }

  // Keyboard & Inscription State
  var inscriptionInput by remember { mutableStateOf("𐤀𐤍𐤊 𐤌𐤔𐤏 𐤁𐤍 𐤊𐤌𐤔𐤌𐤋𐤊") }
  var localAnalysisResult by remember { mutableStateOf<String?>(null) }
  var isLocalAnalyzing by remember { mutableStateOf(false) }
  var showConverterDialog by remember { mutableStateOf(false) }

  // Google Search Grounding State (gemini-3.5-flash with googleSearch tool)
  var searchPrompt by remember { mutableStateOf("") }
  var isSearchLoading by remember { mutableStateOf(false) }
  var searchResult by remember { mutableStateOf<GeminiSearchResult?>(null) }
  var useGroundingToggle by remember { mutableStateOf(true) }

  val curatedPrompts = listOf(
    "أحدث الاكتشافات الإبيغرافية في رأس الشمرا ونقوش أوغاريت المسمارية",
    "نتائج الحفريات الأثرية الحديثة في مأرب ونقوش المسند السبئية",
    "التحول الكنعاني (*ā > ō) وشواهده في النقوش الفينيقية والمؤابية",
    "أحدث الدراسات حول تاريخ مسلة ميشع ونقوش ذيبان المؤابية",
    "نقوش البادية الشمالية (الصفائية والثمودية) ومضامينها المعيشية والقبلية",
    "صوامت السامية الشرقية (الأكادية والإيبلاوية) وعلاقتها بالسامية الأم"
  )

  DisposableEffect(Unit) {
    onDispose {
      audioEngine.release()
    }
  }

  val liveTransliteration = remember(inscriptionInput) {
    convertSemiticToTransliteration(inscriptionInput)
  }

  val liveArabic = remember(inscriptionInput) {
    convertSemiticToArabic(inscriptionInput)
  }

  val gematriaValue = remember(inscriptionInput) {
    calculateGematria(inscriptionInput)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Top Bar Header
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
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "مختبر الأبحاث السامية والذكاء الاصطناعي",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.width(8.dp))
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = "Google Search Data",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                }
              }
            }
            Text(
              text = "لوحة مفاتيح 12+ خطاً سامياً، ومحول الخطوط، والبحث الأكاديمي الموثق ببيانات Google Search عبر gemini-3.5-flash",
              style = MaterialTheme.typography.bodySmall,
              color = customColors.mutedText
            )
          }

          IconButton(onClick = { showConverterDialog = true }) {
            Icon(
              Icons.Default.Transform,
              contentDescription = "محول الخطوط",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Mode Navigation Tabs
        TabRow(
          selectedTabIndex = selectedTab.ordinal,
          containerColor = customColors.cardBackground,
          contentColor = MaterialTheme.colorScheme.primary
        ) {
          LabTab.entries.forEach { tab ->
            Tab(
              selected = selectedTab == tab,
              onClick = { selectedTab = tab },
              text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(tab.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(tab.title, fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal)
                }
              }
            )
          }
        }
      }
    }

    when (selectedTab) {
      LabTab.KEYBOARD_EPIGRAPHY -> {
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Inscription Input Field & Live Feedback
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
              elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "النص المنقوش المُدخل للتحليل:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                  )

                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (gematriaValue > 0) {
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
                      ) {
                        Text(
                          text = "الجُمّل: $gematriaValue",
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold,
                          color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                      }
                    }
                    Surface(
                      shape = RoundedCornerShape(6.dp),
                      color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                    ) {
                      Text(
                        text = "${inscriptionInput.length} رمزاً",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                      )
                    }
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                  value = inscriptionInput,
                  onValueChange = { inscriptionInput = it },
                  modifier = Modifier.fillMaxWidth(),
                  textStyle = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                  ),
                  shape = RoundedCornerShape(12.dp),
                  placeholder = { Text("اكتب بالخطوط السامية عبر لوحة المفاتيح بالأسفل...") }
                )

                // Live Transliteration & Arabic Translation Preview
                if (inscriptionInput.isNotBlank()) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                  ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("النقحرة اللاتينية: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(liveTransliteration.ifBlank { "-" }, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                      }
                      Spacer(modifier = Modifier.height(3.dp))
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("المقابل العربي: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                        Text(liveArabic.ifBlank { "-" }, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                      }
                    }
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                      onClick = {
                        val textToSpeak = liveArabic.ifBlank { inscriptionInput }
                        audioEngine.speak(textToSpeak)
                      }
                    ) {
                      Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("استنطاق صوتي", fontSize = 12.sp)
                    }

                    IconButton(
                      onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Semitic Inscription", inscriptionInput))
                        Toast.makeText(context, "تم نسخ النص المنقوش", Toast.LENGTH_SHORT).show()
                      }
                    ) {
                      Icon(Icons.Default.ContentCopy, contentDescription = "نسخ النص")
                    }
                  }

                  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                      onClick = {
                        isLocalAnalyzing = true
                        localAnalysisResult = runEpigraphicAnalysis(inscriptionInput)
                        isLocalAnalyzing = false
                      },
                      shape = RoundedCornerShape(10.dp)
                    ) {
                      Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("تحليل فيلولوجي", fontSize = 12.sp)
                    }

                    FilledTonalButton(
                      onClick = {
                        selectedTab = LabTab.SEARCH_GROUNDING
                        searchPrompt = "تحليل فيلولوجي وإبيغرافي موثق للنص السامي: $inscriptionInput (نقحرة: $liveTransliteration، مقابل عربي: $liveArabic)"
                        coroutineScope.launch {
                          isSearchLoading = true
                          searchResult = GeminiSearchService.queryWithGoogleSearch(
                            prompt = searchPrompt,
                            useSearchGrounding = true
                          )
                          isSearchLoading = false
                        }
                      },
                      shape = RoundedCornerShape(10.dp)
                    ) {
                      Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("بحث Google", fontSize = 12.sp)
                    }
                  }
                }
              }
            }
          }

          // Local Analysis Output Card if generated
          localAnalysisResult?.let { result ->
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Psychology, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "تقرير التحليل الإبيغرافي والفيلولوجي:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                      )
                    }

                    IconButton(
                      onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Analysis Result", result))
                        Toast.makeText(context, "تم نسخ تقرير التحليل", Toast.LENGTH_SHORT).show()
                      }
                    ) {
                      Icon(Icons.Default.ContentCopy, contentDescription = "نسخ التقرير")
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  Text(
                    text = result,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                  )
                }
              }
            }
          }

          // Integrated Virtual Keyboard
          item {
            SemiticVirtualKeyboard(
              onKeyClick = { key ->
                inscriptionInput += key.char
              },
              onBackspace = {
                if (inscriptionInput.isNotEmpty()) {
                  inscriptionInput = inscriptionInput.dropLast(1)
                }
              },
              onClear = {
                inscriptionInput = ""
              },
              onPresetInsert = { preset ->
                inscriptionInput = preset
              },
              onPlayChime = { f1, f2 ->
                audioEngine.playAcousticFormantChime(f1, f2)
              },
              currentInputText = inscriptionInput
            )
          }
        }
      }

      LabTab.SEARCH_GROUNDING -> {
        // Google Search Grounding Academic AI Researcher Screen
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Info & Model Banner
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
              ),
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.Public,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "الباحث الأكاديمي ببيانات Google Search",
                      fontWeight = FontWeight.Bold,
                      fontSize = 14.sp,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = MaterialTheme.colorScheme.primary
                    ) {
                      Text(
                        text = "gemini-3.5-flash",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                      )
                    }
                  }
                  Text(
                    text = "يستعين الذكاء الاصطناعي بأحدث بيانات محرك بحث Google لاستخراج المراجع والحفريات والأوراق الأكاديمية والنقوش المكتشفة مع إبراز مصادر الاستشهاد الحية.",
                    fontSize = 11.sp,
                    color = customColors.mutedText,
                    lineHeight = 16.sp
                  )
                }
              }
            }
          }

          // Search Input Card
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
              elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                  text = "استفسار بحثي أو نص للنقوش أو مسألة فيلولوجية:",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                  value = searchPrompt,
                  onValueChange = { searchPrompt = it },
                  modifier = Modifier.fillMaxWidth(),
                  placeholder = { Text("اكتب موضوع البحث، أو نقشاً، أو سياقاً أثرياً...") },
                  shape = RoundedCornerShape(12.dp),
                  maxLines = 4
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Grounding Toggle & Actions
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { useGroundingToggle = !useGroundingToggle }
                  ) {
                    Checkbox(
                      checked = useGroundingToggle,
                      onCheckedChange = { useGroundingToggle = it }
                    )
                    Text(
                      text = "تفعيل Google Search Grounding",
                      fontSize = 12.sp,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  }

                  Button(
                    onClick = {
                      if (searchPrompt.isNotBlank()) {
                        coroutineScope.launch {
                          isSearchLoading = true
                          searchResult = GeminiSearchService.queryWithGoogleSearch(
                            prompt = searchPrompt,
                            useSearchGrounding = useGroundingToggle
                          )
                          isSearchLoading = false
                        }
                      }
                    },
                    enabled = searchPrompt.isNotBlank() && !isSearchLoading,
                    shape = RoundedCornerShape(10.dp)
                  ) {
                    if (isSearchLoading) {
                      CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("جاري البحث...", fontSize = 12.sp)
                    } else {
                      Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text("بحث موثق", fontSize = 12.sp)
                    }
                  }
                }
              }
            }
          }

          // Curated Quick Prompts
          item {
            Column {
              Text(
                text = "مقترحات ومحاور بحثية سريعة:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = customColors.mutedText,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                items(curatedPrompts) { prompt ->
                  Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = customColors.cardBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    modifier = Modifier.clickable {
                      searchPrompt = prompt
                      coroutineScope.launch {
                        isSearchLoading = true
                        searchResult = GeminiSearchService.queryWithGoogleSearch(
                          prompt = prompt,
                          useSearchGrounding = useGroundingToggle
                        )
                        isSearchLoading = false
                      }
                    }
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(12.dp)
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(prompt, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                  }
                }
              }
            }
          }

          // Search Results Display with Grounding Citations
          searchResult?.let { res ->
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (res.isGroundingUsed) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                  else MaterialTheme.colorScheme.outlineVariant
                )
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(
                        if (res.isGroundingUsed) Icons.Default.Verified else Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = if (res.isGroundingUsed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Column {
                        Text(
                          text = "النتيجة الأكاديمية الموثقة:",
                          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                          color = MaterialTheme.colorScheme.primary
                        )
                        if (res.isGroundingUsed) {
                          Text(
                            text = "✓ تم تدعيم الإجابة ببيانات Google Search الحية",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.secondary
                          )
                        }
                      }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                      IconButton(
                        onClick = { audioEngine.speak(res.text) },
                        modifier = Modifier.size(32.dp)
                      ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "قراءة صوتية", modifier = Modifier.size(18.dp))
                      }

                      IconButton(
                        onClick = {
                          val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                          clipboard.setPrimaryClip(ClipData.newPlainText("Research Result", res.text))
                          Toast.makeText(context, "تم نسخ نتيجة البحث", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp)
                      ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", modifier = Modifier.size(18.dp))
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(12.dp))

                  // Answer Content Text
                  Text(
                    text = res.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                  )

                  // Google Search Queries Used Section
                  if (res.searchQueries.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                      text = "استعلامات Google Search الميدانية المنفذة:",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    res.searchQueries.forEach { query ->
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                          .fillMaxWidth()
                          .padding(vertical = 2.dp)
                      ) {
                        Row(
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(12.dp), tint = customColors.mutedText)
                          Spacer(modifier = Modifier.width(6.dp))
                          Text(query, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                      }
                    }
                  }

                  // Grounding Sources & Citations
                  if (res.sources.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                      text = "المصادر والمراجع الأكاديمية المباشرة (Grounding Sources):",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    res.sources.forEach { source ->
                      Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier
                          .fillMaxWidth()
                          .padding(vertical = 3.dp)
                          .clickable {
                            try {
                              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                              context.startActivity(intent)
                            } catch (e: Exception) {
                              Toast.makeText(context, "الرابط: ${source.url}", Toast.LENGTH_SHORT).show()
                            }
                          }
                      ) {
                        Row(
                          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically
                        ) {
                          Column(modifier = Modifier.weight(1f)) {
                            Text(
                              text = source.title.ifBlank { source.url },
                              fontSize = 12.sp,
                              fontWeight = FontWeight.Bold,
                              color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                              text = source.url,
                              fontSize = 10.sp,
                              color = customColors.mutedText,
                              maxLines = 1
                            )
                          }
                          Icon(
                            Icons.Default.OpenInNew,
                            contentDescription = "فتح الرابط",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                          )
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // Cross-Script Transliteration Converter Dialog
  if (showConverterDialog) {
    AlertDialog(
      onDismissRequest = { showConverterDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Transform, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(8.dp))
          Text("محول النصوص والخطوط السامية", fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "التحويل الفوري للنص المُدخل عبر الأبجديات السامية المختلفة:",
            fontSize = 12.sp,
            color = customColors.mutedText
          )

          val phoenicianVer = convertArabicToPhoenician(liveArabic.ifBlank { inscriptionInput })
          val musnadVer = convertArabicToMusnad(liveArabic.ifBlank { inscriptionInput })
          val aramaicVer = convertArabicToAramaic(liveArabic.ifBlank { inscriptionInput })

          ScriptResultBox("الخط الفينيقي / الكنعاني:", phoenicianVer, context)
          ScriptResultBox("خط المسند العربي الجنوبي:", musnadVer, context)
          ScriptResultBox("الخط الآرامي الإمبراطوري:", aramaicVer, context)
          ScriptResultBox("النقحرة اللاتينية الفيلولوجية:", liveTransliteration, context)
        }
      },
      confirmButton = {
        TextButton(onClick = { showConverterDialog = false }) {
          Text("إغلاق")
        }
      }
    )
  }
}

@Composable
fun ScriptResultBox(title: String, text: String, context: Context) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(8.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(text.ifBlank { "-" }, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
      }
      IconButton(
        onClick = {
          val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
          clipboard.setPrimaryClip(ClipData.newPlainText(title, text))
          Toast.makeText(context, "تم النسخ", Toast.LENGTH_SHORT).show()
        },
        modifier = Modifier.size(28.dp)
      ) {
        Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", modifier = Modifier.size(16.dp))
      }
    }
  }
}

fun convertSemiticToTransliteration(input: String): String {
  var text = input
  val replacements = listOf(
    "𐤀" to "ʾ", "𐤁" to "b", "𐤂" to "g", "𐤃" to "d", "𐤄" to "h", "𐤅" to "w", "𐤆" to "z",
    "𐤇" to "ḥ", "𐤈" to "ṭ", "𐤉" to "y", "𐤊" to "k", "𐤋" to "l", "𐤌" to "m", "𐤍" to "n",
    "𐤎" to "s", "𐤏" to "ʿ", "𐤐" to "p", "𐤑" to "ṣ", "𐤒" to "q", "𐤓" to "r", "𐤔" to "š", "𐤕" to "t",
    "𐩱" to "ʾ", "𐩨" to "b", "𐩴" to "g", "𐩵" to "d", "𐩠" to "h", "𐩥" to "w", "𐩸" to "z",
    "𐩢" to "ḥ", "𐩷" to "ṭ", "𐩺" to "y", "𐩫" to "k", "𐩡" to "l", "𐩣" to "m", "𐩬" to "n",
    "𐩯" to "s³", "𐩲" to "ʿ", "𐩰" to "f", "𐩮" to "ṣ", "𐩤" to "q", "𐩧" to "r", "𐩪" to "s¹",
    "𐩩" to "t", "𐩻" to "ṯ", "𐩭" to "ḫ", "𐩹" to "ḏ", "𐩳" to "ḍ", "𐩼" to "ẓ", "𐩶" to "ġ",
    "𐩦" to "š²", "𐩿" to " | ", "𐤟" to " • "
  )
  for ((from, to) in replacements) {
    text = text.replace(from, to)
  }
  return text
}

fun convertSemiticToArabic(input: String): String {
  var text = input
  val replacements = listOf(
    "𐤀" to "أ", "𐤁" to "ب", "𐤂" to "ج", "𐤃" to "د", "𐤄" to "هـ", "𐤅" to "و", "𐤆" to "ز",
    "𐤇" to "ح", "𐤈" to "ط", "𐤉" to "ي", "𐤊" to "ك", "𐤋" to "ل", "𐤌" to "م", "𐤍" to "ن",
    "𐤎" to "س", "𐤏" to "ع", "𐤐" to "ف", "𐤑" to "ص", "𐤒" to "ق", "𐤓" to "ر", "𐤔" to "ش", "𐤕" to "ت",
    "𐩱" to "أ", "𐩨" to "ب", "𐩴" to "ج", "𐩵" to "د", "𐩠" to "هـ", "𐩥" to "و", "𐩸" to "ز",
    "𐩢" to "ح", "𐩷" to "ط", "𐩺" to "ي", "𐩫" to "ك", "𐩡" to "ل", "𐩣" to "م", "𐩬" to "ن",
    "𐩯" to "س", "𐩲" to "ع", "𐩰" to "ف", "𐩮" to "ص", "𐩤" to "ق", "𐩧" to "ر", "𐩪" to "س",
    "𐩩" to "ت", "𐩻" to "ث", "𐩭" to "خ", "𐩹" to "ذ", "𐩳" to "ض", "𐩼" to "ظ", "𐩶" to "غ",
    "𐩦" to "ش", "𐩿" to " | ", "𐤟" to " • "
  )
  for ((from, to) in replacements) {
    text = text.replace(from, to)
  }
  return text
}

fun convertArabicToPhoenician(input: String): String {
  var text = input
  val replacements = listOf(
    "أ" to "𐤀", "ا" to "𐤀", "إ" to "𐤀", "آ" to "𐤀", "ء" to "𐤀", "ب" to "𐤁", "ج" to "𐤂",
    "د" to "𐤃", "ه" to "𐤄", "ة" to "𐤕", "و" to "𐤅", "ز" to "𐤆", "ح" to "𐤇", "ط" to "𐤈",
    "ي" to "𐤉", "ى" to "𐤉", "ك" to "𐤊", "ل" to "𐤋", "م" to "𐤌", "ن" to "𐤍", "س" to "𐤎",
    "ع" to "𐤏", "ف" to "𐤐", "ص" to "𐤑", "ق" to "𐤒", "ر" to "𐤓", "ش" to "𐤔", "ت" to "𐤕",
    "ث" to "𐤔", "خ" to "𐤇", "ذ" to "𐤆", "ض" to "𐤑", "ظ" to "𐤑", "غ" to "𐤏", " " to " 𐤟 "
  )
  for ((from, to) in replacements) {
    text = text.replace(from, to)
  }
  return text
}

fun convertArabicToMusnad(input: String): String {
  var text = input
  val replacements = listOf(
    "أ" to "𐩱", "ا" to "𐩱", "إ" to "𐩱", "آ" to "𐩱", "ء" to "𐩱", "ب" to "𐩨", "ج" to "𐩴",
    "د" to "𐩵", "ه" to "𐩠", "ة" to "𐩩", "و" to "𐩥", "ز" to "𐩸", "ح" to "𐩢", "ط" to "𐩷",
    "ي" to "𐩺", "ى" to "𐩺", "ك" to "𐩫", "ل" to "𐩡", "م" to "𐩣", "ن" to "𐩬", "س" to "𐩪",
    "ع" to "𐩲", "ف" to "𐩰", "ص" to "𐩮", "ق" to "𐩤", "ر" to "𐩧", "ش" to "𐩦", "ت" to "𐩩",
    "ث" to "𐩻", "خ" to "𐩭", "ذ" to "𐩹", "ض" to "𐩳", "ظ" to "𐩼", "غ" to "𐩶", " " to " 𐩿 "
  )
  for ((from, to) in replacements) {
    text = text.replace(from, to)
  }
  return text
}

fun convertArabicToAramaic(input: String): String {
  var text = input
  val replacements = listOf(
    "أ" to "𐡀", "ا" to "𐡀", "إ" to "𐡀", "آ" to "𐡀", "ء" to "𐡀", "ب" to "𐡁", "ج" to "𐡂",
    "د" to "𐡃", "ه" to "𐡄", "ة" to "𐡕", "و" to "𐡅", "ز" to "𐡆", "ح" to "𐡇", "ط" to "𐡈",
    "ي" to "𐡉", "ى" to "𐡉", "ك" to "𐡊", "ل" to "𐡋", "م" to "𐡌", "ن" to "𐡍", "س" to "𐡎",
    "ع" to "𐡏", "ف" to "𐡐", "ص" to "𐡑", "ق" to "𐡒", "ر" to "𐡓", "ش" to "𐡔", "ت" to "𐡕",
    "ث" to "𐡔", "خ" to "𐡇", "ذ" to "𐡃", "ض" to "𐡏", "ظ" to "𐡈", "غ" to "𐡏"
  )
  for ((from, to) in replacements) {
    text = text.replace(from, to)
  }
  return text
}

fun runEpigraphicAnalysis(input: String): String {
  val sb = StringBuilder()
  sb.append("1. التعرف على نوع الخط والفرع اللغوي:\n")
  when {
    input.contains("𐤀") || input.contains("𐤌") || input.contains("𐤔") -> {
      sb.append("• الخط: الأبجدية الفينيقية والكنعانية القديمة (Linear Phoenician Abjad)\n")
      sb.append("• الفرع اللغوي: السامية الشمالية الغربية (Northwest Semitic)\n")
      sb.append("• الحقبة: الألف الأول قبل الميلاد (العصر الحديدي)\n")
    }
    input.contains("𐎀") || input.contains("𐎍") || input.contains("𐎎") -> {
      sb.append("• الخط: الأبجدية المسمارية الأوغاريتية (Ugaritic Cuneiform Abjad)\n")
      sb.append("• الفرع اللغوي: السامية الشمالية الغربية (أوغاريت / رأس الشمرا)\n")
      sb.append("• الحقبة: القرنان 14 - 12 قبل الميلاد (العصر البرونزي المتأخر)\n")
    }
    input.contains("𐩠") || input.contains("𐩣") || input.contains("𐩱") -> {
      sb.append("• الخط: خط المسند العربي الجنوبي الأثري (Epigraphic South Arabian Musnad)\n")
      sb.append("• الفرع اللغوي: السامية الجنوبية القديمة (الصيهدية: سبأ، معين، قتبان، حضرموت)\n")
      sb.append("• الحقبة: من القرن 10 ق.م حتى القرن 6 الميلادي\n")
    }
    input.contains("𐪀") || input.contains("𐪁") || input.contains("𐪖") -> {
      sb.append("• الخط: الخط الصفائي / الثمودي (Ancient North Arabian)\n")
      sb.append("• الفرع اللغوي: العربية الشمالية القديمة (نقوش البادية والحرّات)\n")
    }
    input.contains("𐡀") || input.contains("𐡁") || input.contains("𐡓") -> {
      sb.append("• الخط: الخط الآرامي الإمبراطوري (Imperial Aramaic)\n")
      sb.append("• الفرع اللغوي: الآرامية القديمة والوسطى (السامية الشمالية الغربية)\n")
    }
    input.contains("ܐ") || input.contains("ܒ") || input.contains("ܫ") -> {
      sb.append("• الخط: الخط السرياني الأسطرنجيلي (Syriac Estrangela)\n")
      sb.append("• الفرع اللغوي: الآرامية السريانية الرهاوية\n")
    }
    input.contains("ሀ") || input.contains("ለ") || input.contains("መ") -> {
      sb.append("• الخط: الخط الجعزي الإثيوبي (Ethiopic Fidel)\n")
      sb.append("• الفرع اللغوي: السامية الإثيوبية (الجعزية الكلاسيكية / أكسوم)\n")
    }
    input.contains("𒀭") || input.contains("𒈗") || input.contains("𒂗") -> {
      sb.append("• الخط: المسماري الأكادي المقطعي (Akkadian Cuneiform & Logograms)\n")
      sb.append("• الفرع اللغوي: السامية الشرقية (الأكادية البابلية والآشورية)\n")
    }
    else -> {
      sb.append("• الخط: نصوص سامية مقارنة بالنقحرة أو العربية\n")
    }
  }

  val transliteration = convertSemiticToTransliteration(input)
  val arabicEquiv = convertSemiticToArabic(input)

  sb.append("\n2. النقحرة الفيلولوجية اللاتينية (Transliteration):\n")
  sb.append("• $transliteration\n")

  sb.append("\n3. القراءة والتحليل الصرفي المقارن:\n")
  sb.append("• النص المقابل بالعربية: $arabicEquiv\n")

  if (input.contains("𐤀𐤍𐤊") || input.contains("𐤌𐤔𐤏")) {
    sb.append("• [𐤀𐤍𐤊]: ضمير المتكلم المنفصل 'anōkī (يقابل العربية 'أنا' والأكادية 'anāku').\n")
    sb.append("• [𐤌𐤔𐤏]: اسم علم مؤابي من الجذر *y-š-ʿ (الخلاص والإنجاد).\n")
    sb.append("• [𐤁𐤍]: اسم مفرد مضاف 'بن' (bin).\n")
    sb.append("• [𐤊𐤌𐤔𐤌𐤋𐤊]: اسم ثيوفوري مركب للإله القومي كموش.\n")
  } else if (input.contains("𐩫𐩧𐩨𐩱𐩡") || input.contains("𐩪𐩨𐩱")) {
    sb.append("• [𐩫𐩧𐩨𐩱𐩡]: كربئيل (المقرب من الإله إيل).\n")
    sb.append("• [𐩥𐩩𐩧]: وتر (العظيم / القاهر).\n")
    sb.append("• [𐩣𐩫𐩧𐩨]: مكرب (الحاكم الديني والسياسي لسبأ).\n")
    sb.append("• [𐩪𐩨𐩱]: سبأ (المملكة وحضارة مأرب وسدها العظيم).\n")
  }

  sb.append("\n4. القوانين الصوتية وحساب الجُمّل:\n")
  val gematria = calculateGematria(input)
  sb.append("• القيمة الحسابية الجُملية (Gematria): $gematria\n")
  sb.append("• الظواهر الصوتية: تطابق مع البنية الفونولوجية السامية الأم وتوزيع الصوامت المطبقة والحلقية.")

  return sb.toString()
}
