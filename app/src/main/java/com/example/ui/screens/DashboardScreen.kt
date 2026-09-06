package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalCustomColors
import com.example.util.AudioEngine

data class AcademicCategory(
  val id: String,
  val titleAr: String,
  val icon: String,
  val descriptionAr: String,
  val modules: List<AcademicModuleItem>
)

data class AcademicModuleItem(
  val route: String,
  val titleAr: String,
  val titleEn: String,
  val icon: ImageVector,
  val descriptionAr: String,
  val badge: String? = null,
  val accentColor: Color? = null
)

data class LearningTrack(
  val id: String,
  val titleAr: String,
  val subtitleAr: String,
  val icon: String,
  val route: String,
  val progressPercent: Int,
  val stepCount: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
  onNavigate: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  val audioEngine = remember { AudioEngine(context) }

  DisposableEffect(Unit) {
    onDispose { audioEngine.release() }
  }

  var selectedCategoryFilter by remember { mutableStateOf("all") }

  val learningTracks = remember {
    listOf(
      LearningTrack("track-1", "مسار الإبيغرافيا وفك النقوش", "دراسة المسلات ونقوش اللغات السامية الستة", "🗿", "inscriptions", 85, "12 وحدة"),
      LearningTrack("track-2", "مسار اللسانيات المقارنة والأصوات", "قوانين التحول الصوتي ومصفوفة IPA السامية", "🗣️", "phonetics", 90, "15 درساً"),
      LearningTrack("track-3", "مسار المعجم والتأصيل الاشتقاقي", "تتبع الجذور السامية المشتركة في 8 لغات", "🌿", "etymology", 70, "20 جذراً"),
      LearningTrack("track-4", "مسار القواعد والنحو الإبيغرافي", "بطاقات لايتنر التفاعلية للنحو والصرف", "📐", "flashcards", 65, "25 بطاقة")
    )
  }

  val academicCategories = remember {
    listOf(
      AcademicCategory(
        id = "epigraphy",
        titleAr = "الإبيغرافيا والنقوش واللقى",
        icon = "📜",
        descriptionAr = "دراسة وفحص ونقحرة النقوش الأثرية والمسلات التاريخية",
        modules = listOf(
          AcademicModuleItem("inscriptions", "أرشيف النقوش واللقى", "Inscriptions & Artifacts", Icons.Default.Description, "دراسة 35+ نقشاً ومسلة مع التحليل الفيلولوجي والفحص الطيفي", "35+ نقشاً"),
          AcademicModuleItem("glossary", "المسرد الإبيغرافي واللساني", "Epigraphic Glossary", Icons.Default.MenuBook, "مسرد تفاعلي للمفاهيم الإبيغرافية، القوانين الصوتية، ومواد التدوين الأثرية", "مسرد مصطلحات"),
          AcademicModuleItem("inscription_search", "محرك بحث النقوش (Room DB)", "Room Inscriptions DB", Icons.Default.ManageSearch, "بحث واستعلام النقوش بالاسم والحقبة والإقليم بقاعدة بيانات Room محلية", "قاعدة Room"),
          AcademicModuleItem("ocr", "التعرف الضوئي (OCR)", "Epigraphic OCR Scanner", Icons.Default.DocumentScanner, "فك رموز ونقحرة الخطوط القديمة من عينات الألواح والمسلات", "رؤية حاسوبية"),
          AcademicModuleItem("media", "معرض الوسائط والمخطوطات", "Media & Dossier Archive", Icons.Default.Collections, "الألواح والمخطوطات والخرائط والمسلات مع التصدير التعليمي PDF", "عالي الدقة")
        )
      ),
      AcademicCategory(
        id = "linguistics",
        titleAr = "اللسانيات المقارنة وعلم الأصوات",
        icon = "🗣️",
        descriptionAr = "الصوتيات، التصريف المقارن، ومصفوفة اللغات السامية",
        modules = listOf(
          AcademicModuleItem("languages", "مصفوفة اللغات السامية", "Pan-Semitic Matrix", Icons.Default.Language, "الملفات الأكاديمية لكافة فروع اللغات السامية الستة (35+ لغة)", "35+ لغة"),
          AcademicModuleItem("phonetics", "محاكي علم الأصوات", "Phonetics & IPA Simulator", Icons.Default.GraphicEq, "محاكاة قوانين التحول الصوتي ومصفوفة IPA ومولد النغمات الفيلولوجي", "تفاعلي"),
          AcademicModuleItem("pronunciation", "مدرب النطق الصوتي", "Phonetics Coach & Formants", Icons.Default.Mic, "مختبر النطق الأكاديمي ومحاكي ترددات Formant F1/F2 والموجات الصوتية", "تحقيق صوتي"),
          AcademicModuleItem("lexicon", "المعجم السامي المقارن", "Comparative Semitic Lexicon", Icons.Default.Spellcheck, "تأصيل الجذور السامية المشتركة في 8 لغات مع النطق الصوتي", "جذور سامية"),
          AcademicModuleItem("etymology", "شبكة التأصيل الاشتقاقي", "Etymology Graph Hub", Icons.Default.Hub, "تتبع مسارات التطور الصوتي والدلالي عبر العقد البيانية التفاعلية", "شبكة جذور")
        )
      ),
      AcademicCategory(
        id = "encyclopedia",
        titleAr = "الموسوعة والبحوث الأكاديمية",
        icon = "📚",
        descriptionAr = "المتون العلمية، المقارنة المزدوجة، والمراجع المعتمدة",
        modules = listOf(
          AcademicModuleItem("downloaded_library", "المكتبة المحمّلة دون إنترنت (Room)", "Room Offline Encyclopedia Cache", Icons.Default.CloudDone, "تصفح والوصول الكامل للفصول والكتب والنقوش المخزنة محلياً في Room دون اتصال بالإنترنت", "Room Offline", accentColor = Color(0xFF2E7D32)),
          AcademicModuleItem("universal_search", "البحث الشامل والدلالي", "Universal Semantic Search", Icons.Default.Search, "بحث دلالي فوري في 35+ لغة، الفصول، النقوش، المعجم، والمراجع", "بحث شامل"),
          AcademicModuleItem("books", "خزانة الكتب والمصادر (PDF)", "Epigraphic Books & Sources", Icons.Default.LibraryBooks, "فحص وسجلات مصادر الكتب وعارض PDF المدمج مع رابط تبويب مستقل", "قارئ PDF"),
          AcademicModuleItem("chapters", "القارئ الأكاديمي الموسوعي", "Academic Reader", Icons.Default.MenuBook, "تصفح الفصول الأكاديمية الـ 300+ مع القراءة الصوتية والتصدير الأكاديمي", "300+ فصلاً"),
          AcademicModuleItem("compare", "المقارنة المزدوجة", "Dual Dialect Compare", Icons.Default.CompareArrows, "مقارنة النصوص واللهجات جنباً إلى جنب مع كاشف الجذور", "مقارن حي"),
          AcademicModuleItem("map", "الخريطة والمواقع الأثرية", "Archaeological Map & Atlas", Icons.Default.Map, "استكشاف المواقع الأثرية ومكتشفات النقوش والأطلس التاريخي", "أطلس جغرافي"),
          AcademicModuleItem("index", "الكشاف والفهرس الذكي", "Smart Concordance Index", Icons.Default.FormatListNumbered, "كشاف الأعلام والنقوش والمواقع والقوانين الصوتية وتصدير LaTeX", "فهرسة شاملة"),
          AcademicModuleItem("bibliography", "المراجع والاستشهادات", "Academic Bibliography", Icons.Default.Bookmarks, "توليد استشهادات APA, MLA, Chicago, BibTeX وتصفح المراجع", "توثيق رسمي")
        )
      ),
      AcademicCategory(
        id = "education",
        titleAr = "التعليم والاختبارات والمختبر",
        icon = "🎓",
        descriptionAr = "البطاقات التعليمية، التحديات اليومية، ولوحة المفاتيح",
        modules = listOf(
          AcademicModuleItem("robert_chat", "المساعد الذكي روبرت", "Robert AI Philologist", Icons.Default.SmartToy, "حوار فيلولوجي ذكي مع روبرت لتحليل النقوش والجذور والقوانين الصوتية", "Free AI", accentColor = Color(0xFF1E88E5)),
          AcademicModuleItem("flashcards", "بطاقات القواعد (لايتنر)", "Grammar Leitner Flashcards", Icons.Default.School, "بطاقات تفاعلية لدراسة النحو والصرف بنظام التكرار المتباعد", "تكرار ذكي"),
          AcademicModuleItem("quiz", "وحدة الاختبارات اليومية", "Daily Philological Quiz", Icons.Default.Quiz, "اختبارات فيلولوجية تفاعلية لتقييم الإتقان في الأصوات والنقوش", "تحدي يومي"),
          AcademicModuleItem("lab", "مختبر أبحاث الذكاء الاصطناعي", "AI Script Lab & Keyboards", Icons.Default.Keyboard, "لوحة مفاتيح الخطوط القديمة وتحليل النقوش وفك الطلاسم", "لوحات أصلية")
        )
      )
    )
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Top Academic University Seal Banner
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.linearGradient(
                colors = listOf(
                  MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                  MaterialTheme.colorScheme.secondary.copy(alpha = 0.06f),
                  MaterialTheme.colorScheme.surface
                )
              )
            )
            .padding(18.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                  modifier = Modifier.size(44.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      Icons.Default.School,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(26.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "جامعة صنعاء • كلية الآداب • قسم الآثار",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                  Text(
                    text = "موسوعية في اللغات والنقوش السامية",
                    fontSize = 10.sp,
                    color = customColors.mutedText
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
              ) {
                Text(
                  text = "إصدار أكاديمي معتمد",
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "الموسوعة الشاملة في فقه ونقوش اللغات السامية",
              fontSize = 18.sp,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.onSurface,
              lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text(
                text = "إشراف: أ.د. أحمد فقعس",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = "إعداد: سوسن علي عبدالله الحضوري",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "منظومة رقمية فيلولوجية توثق 35+ لغة ولهجة سامية، مع محاكي الأصوات، الأطلس التاريخي، التعرف الضوئي OCR، وقاعدة بيانات Room المحلية.",
              fontSize = 11.sp,
              color = customColors.mutedText,
              lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Philological Key Metrics Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              StatBadge("6", "فروع سامية", Modifier.weight(1f))
              StatBadge("35+", "لغة ولهجة", Modifier.weight(1f))
              StatBadge("300+", "فصلاً ومبحثاً", Modifier.weight(1f))
              StatBadge("29", "فونيم IPA", Modifier.weight(1f))
              StatBadge("100%", "أوفلاين Room", Modifier.weight(1f))
            }
          }
        }
      }
    }

    // Universal Semantic Search Bar Entry
    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .clickable { onNavigate("universal_search") },
        color = customColors.cardBackground,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              Icons.Default.Search,
              contentDescription = "بحث شامل",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              "البحث الشامل والدلالي في الموسوعة...",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              "35+ لغة • 300+ فصلاً • النقوش والمسلات • المعجم والتأصيل",
              fontSize = 10.sp,
              color = customColors.mutedText
            )
          }
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
          ) {
            Text(
              "بحث ذكي",
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }

    // Robert AI Chat Feature Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(18.dp))
          .clickable { onNavigate("robert_chat") },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                colors = listOf(
                  MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                  MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f),
                  MaterialTheme.colorScheme.surface
                )
              )
            )
            .padding(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    colors = listOf(
                      MaterialTheme.colorScheme.primary,
                      MaterialTheme.colorScheme.tertiary
                    )
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.SmartToy,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  "المساعد الفيلولوجي الذكي (روبرت)",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = MaterialTheme.colorScheme.primaryContainer
                ) {
                  Text(
                    "Free AI",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                "حاور روبرت حول فك النقوش، القوانين الصوتية، والجذور السامية مع دعم التلفظ الصوتي.",
                fontSize = 11.sp,
                color = customColors.mutedText,
                lineHeight = 15.sp
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
              Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }

    // Daily Philological Artifact Feature Showcase
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("✨", fontSize = 16.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                "القطعة الإبيغرافية المميزة اليوم",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFF2E7D32).copy(alpha = 0.15f)
            ) {
              Text(
                "كنعانية جبيل",
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Artifact Quote Preview
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1E1C18)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "𐤀𐤓𐤍 𐤆 𐤐𐤏𐤋 𐤀𐤕𐤁𐤏𐤋 𐤁𐤍 𐤀𐤇𐤓𐤌 𐤌𐤋𐤊 𐤂𐤁𐤋",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE5C158),
                letterSpacing = 2.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "نقش تابوت أحيرام ملك جبيل (ح. 1000 ق.م) — أقدم توثيق مكتمل للأبجدية الفينيقية",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.85f)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            OutlinedButton(
              onClick = {
                audioEngine.playProtoSemiticChime()
                Toast.makeText(context, "تشغيل النطق الصوتي الكنعاني المقدر", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("استماع صوتي", fontSize = 12.sp)
            }

            Button(
              onClick = { onNavigate("inscriptions") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
              Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("فحص النقش في الأرشيف", fontSize = 12.sp)
            }
          }
        }
      }
    }

    // Academic Learning Tracks Horizontal Carousel
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoStories, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              "مسارات التعلم الأكاديمي والتحقيق الفيلولوجي",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          Text(
            "4 مسارات تخصصية",
            fontSize = 11.sp,
            color = customColors.mutedText
          )
        }

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(learningTracks) { track ->
            Card(
              modifier = Modifier
                .width(220.dp)
                .clickable { onNavigate(track.route) },
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(track.icon, fontSize = 22.sp)
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                  ) {
                    Text(
                      track.stepCount,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  track.titleAr,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurface,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )

                Text(
                  track.subtitleAr,
                  fontSize = 10.sp,
                  color = customColors.mutedText,
                  maxLines = 2,
                  lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                  progress = track.progressPercent / 100f,
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                  color = MaterialTheme.colorScheme.primary,
                  trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("نسبة الإنجاز", fontSize = 9.sp, color = customColors.mutedText)
                  Text("${track.progressPercent}%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
              }
            }
          }
        }
      }
    }

    // Category Tabs Filter
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = selectedCategoryFilter == "all",
          onClick = { selectedCategoryFilter = "all" },
          label = { Text("كافة الأقسام الـ 17", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
          leadingIcon = if (selectedCategoryFilter == "all") {
            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
          } else null
        )

        academicCategories.forEach { category ->
          val isSelected = selectedCategoryFilter == category.id
          FilterChip(
            selected = isSelected,
            onClick = { selectedCategoryFilter = category.id },
            label = {
              Text("${category.icon} ${category.titleAr}", fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
            },
            leadingIcon = if (isSelected) {
              { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
            } else null
          )
        }
      }
    }

    // Categorized Modules Display
    val filteredCategories = if (selectedCategoryFilter == "all") {
      academicCategories
    } else {
      academicCategories.filter { it.id == selectedCategoryFilter }
    }

    filteredCategories.forEach { category ->
      item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          // Category Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(category.icon, fontSize = 16.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                category.titleAr,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }
            Text(
              "${category.modules.size} أدوات",
              fontSize = 11.sp,
              color = customColors.mutedText
            )
          }

          // Modules in 2-column Grid Layout
          category.modules.chunked(2).forEach { rowModules ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              rowModules.forEach { module ->
                AcademicModuleCard(
                  module = module,
                  onClick = { onNavigate(module.route) },
                  modifier = Modifier.weight(1f)
                )
              }
              if (rowModules.size == 1) {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
fun AcademicModuleCard(
  module: AcademicModuleItem,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current

  Card(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = module.icon,
            contentDescription = module.titleAr,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }

        module.badge?.let {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
          ) {
            Text(
              text = it,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = module.titleAr,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Text(
        text = module.titleEn,
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.primary,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = module.descriptionAr,
        fontSize = 11.sp,
        color = customColors.mutedText,
        maxLines = 2,
        lineHeight = 15.sp,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
fun StatBadge(value: String, label: String, modifier: Modifier = Modifier) {
  val customColors = LocalCustomColors.current
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(10.dp))
      .background(MaterialTheme.colorScheme.surface)
      .border(1.dp, customColors.border, RoundedCornerShape(10.dp))
      .padding(vertical = 8.dp, horizontal = 4.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = customColors.mutedText,
        maxLines = 1,
        textAlign = TextAlign.Center
      )
    }
  }
}

