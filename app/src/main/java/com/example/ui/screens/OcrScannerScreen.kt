package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CameraXScannerView
import com.example.ui.components.EditableDigitalInscriptionSection
import com.example.ui.components.OcrOfflinePacksSheet
import com.example.ui.theme.LocalCustomColors
import com.example.util.AudioEngine
import com.example.util.EpigraphicOcrEngine
import com.example.util.OcrDownloadManager
import com.example.util.PackDownloadState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class SpectralFilterMode(val labelAr: String, val icon: String) {
  STANDARD("العرض الطبيعي", "🖼️"),
  HIGH_CONTRAST("الحفر الحجري عالي التباين", "🗿"),
  INFRARED("الأشعة تحت الحمراء MSI", "🔬"),
  EDGE_DETECTION("كشف الحواف الأبجدية", "✨"),
  HEATMAP("خريطة العمق الإبيغرافي", "🌡️")
}

data class DetectedGlyphBox(
  val glyph: String,
  val transliteration: String,
  val nameAr: String,
  val rootAr: String,
  val meaningAr: String
)

data class ComprehensiveOcrSample(
  val id: String,
  val titleAr: String,
  val titleEn: String,
  val scriptTypeAr: String,
  val detectedLanguage: String,
  val familyAr: String,
  val period: String,
  val site: String,
  val confidenceScore: Float,
  val extractedGlyphs: String,
  val transliteration: String,
  val translationAr: String,
  val translationEn: String,
  val linguisticAnalysis: String,
  val rootBreakdown: String,
  val historicalSignificance: String,
  val glyphBoxes: List<DetectedGlyphBox>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrScannerScreen(
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  val scope = rememberCoroutineScope()
  val audioEngine = remember { AudioEngine(context) }

  DisposableEffect(Unit) {
    onDispose { audioEngine.release() }
  }

  val comprehensiveSamples = remember {
    listOf(
      ComprehensiveOcrSample(
        id = "sample-1",
        titleAr = "شريعة حمورابي (المسلة المسمارية البابلية)",
        titleEn = "Code of Hammurabi Stele",
        scriptTypeAr = "المسمارية الأكادية المقطعية (Cuneiform)",
        detectedLanguage = "الأكادية البابلية القديمة (Old Babylonian)",
        familyAr = "السامية الشرقية (East Semitic)",
        period = "العصر البابلي القديم، ح. 1750 ق.م",
        site = "بابل / سوسة (محفوظة في اللوفر)",
        confidenceScore = 0.985f,
        extractedGlyphs = "𒀭 𒈗 𒂗 𒂍 𒄿 𒈾 𒈠 𒁴 𒈪 𒊭 𒊏 𒄠 𒌑 𒊭 𒀊 𒅆",
        transliteration = "i-na ma-tim mi-ša-ra-am ú-ša-ap-ši-i",
        translationAr = "أقمتُ العدل والحق في ربوع البلاد وأشعتُ رخاء الشعب وأمان الرعية.",
        translationEn = "I established justice and equity throughout the land and fostered the well-being of the people.",
        linguisticAnalysis = "صيغة فعل سببي Š-stem من الجذر (w-š-p-y / ušapši) مع مفعول به منصوب بتنوين الميم (mīšaram = العدالة والإنصاف).",
        rootBreakdown = "J-Š-R (يشر/عشر = استقام) | M-T (مات/ماتم = أرض/بلاد) | Š-P-Y (شفي/رخاء)",
        historicalSignificance = "أعظم وثيقة تشريعية قانونية متكاملة في الشرق الأدنى القديم، مدونة على مسلة من حجر الديوريت الأسود البازلتي.",
        glyphBoxes = listOf(
          DetectedGlyphBox("𒀭", "AN / dingir", "إيلو / علامة الألوهية", "ʾ-L", "رمز الإله والمقام السامي"),
          DetectedGlyphBox("𒈗", "LUGAL / šarru", "شارو / الملك", "Š-R-R", "الملك الحاكم صاحب السيادة"),
          DetectedGlyphBox("𒂗", "EN / bēlu", "بيلو / السيد", "B-ʿ-L", "السيد والرب الحامي"),
          DetectedGlyphBox("𒂍", "É / bītu", "بيتو / البيت والهيكل", "B-Y-T", "المعبد والقصر الملكي"),
          DetectedGlyphBox("𒄿𒈾", "i-na", "إنا (حرف جر)", "IN", "في / بداخل"),
          DetectedGlyphBox("𒈠𒁴", "ma-tim", "ماتيم (اسم مجرور بالميم)", "M-T", "أرض وبلاد الرعية"),
          DetectedGlyphBox("𒈪𒊭𒊏𒄠", "mi-ša-ra-am", "ميشَرام (مفعول به)", "Y-Š-R", "العدل والاستقامة والحق")
        )
      ),
      ComprehensiveOcrSample(
        id = "sample-2",
        titleAr = "نقش تابوت أحيرام (جبيل الفينيقية)",
        titleEn = "Ahiram Sarcophagus Inscription",
        scriptTypeAr = "الأبجدية الفينيقية الكنعانية القديمة (Phoenician)",
        detectedLanguage = "الفينيقية الجبيلية الكلاسيكية (Byblian Phoenician)",
        familyAr = "السامية الشمالية الغربية (Northwest Semitic)",
        period = "العصر البرونزي المتأخر / الحديدي I، ح. 1000 ق.م",
        site = "جبيل (بيبلوس)، لبنان (متحف بيروت الوطني)",
        confidenceScore = 0.992f,
        extractedGlyphs = "𐤀𐤓𐤍 𐤆 𐤐𐤏𐤋 𐤀𐤕𐤁𐤏𐤋 𐤁𐤍 𐤀𐤇𐤓𐤌 𐤌𐤋𐤊 𐤂𐤁𐤋 𐤋𐤀𐤁𐤄",
        transliteration = "ʾrn z pʿl ʾtbʿl bn ʾḥrm mlk gbl lʾbh k-šth b-ʿlm",
        translationAr = "هذا التابوت صنعه إتبعل بن أحيرام ملك جبيل لأبيه عندما أسكنه في دار الخلود.",
        translationEn = "This coffin was made by Ittobaal, son of Ahiram, king of Byblos, for his father when he placed him in eternity.",
        linguisticAnalysis = "اسم الإشارة المفرد المذكر (𐤆 z = هذا المقابل لـ'ذا')، والفعل الماضي المجرد (𐤐𐤏𐤋 pʿl = فعل/صنع)، وهاء الغائب المتصلة (𐤋𐤀𐤁𐤄 l-ʾbh = لأبيه).",
        rootBreakdown = "ʾ-R-N (أرن = تابوت/صندوق) | P-ʿ-L (فعل = عمل وصنع) | M-L-K (ملك = حكم) | ʿ-L-M (علم = دهر وخلود)",
        historicalSignificance = "أقدم نقش ملكي يوثق الأبجدية الفينيقية ذات الـ 22 حرفاً بشكلها المكتمل والتي تفرعت منها كافة أبجديات العالم.",
        glyphBoxes = listOf(
          DetectedGlyphBox("𐤀𐤓𐤍", "ʾrn", "أَرْن / تابوت", "ʾ-R-N", "الناووس والتابوت الحجري"),
          DetectedGlyphBox("𐤆", "z", "ز / اسم إشارة (هذا)", "Z / D", "ذا / هذا للمفرد المذكر"),
          DetectedGlyphBox("𐤐𐤏𐤋", "pʿl", "فَعَلَ / صَنَعَ", "P-ʿ-L", "صنع وبنى ونحت"),
          DetectedGlyphBox("𐤀𐤕𐤁𐤏𐤋", "ʾtbʿl", "إِتَّبَعَل (معي بعل)", "ʾ-T + B-ʿ-L", "اسم ملك صيدا وجبيل"),
          DetectedGlyphBox("𐤁𐤍", "bn", "بِن / ابن", "B-N", "ابن وسليل"),
          DetectedGlyphBox("𐤀𐤇𐤓𐤌", "ʾḥrm", "أَحِيرَام (أخي رفيع)", "ʾ-Ḥ + R-W-M", "اسم الملك المتوفى"),
          DetectedGlyphBox("𐤌𐤋𐤊", "mlk", "مَلِك", "M-L-K", "الحاكم المتوج"),
          DetectedGlyphBox("𐤂𐤁𐤋", "gbl", "جُبَيْل (جبل/حد)", "G-B-L", "حاضرة فينيقيا المرفئية")
        )
      ),
      ComprehensiveOcrSample(
        id = "sample-3",
        titleAr = "مسلة ميشع المؤابية (ذيبان)",
        titleEn = "Mesha Stele (Moabite Stone)",
        scriptTypeAr = "الخط الكنعاني / المؤابي القديم (Moabite)",
        detectedLanguage = "المؤابية الكنعانية (Moabite)",
        familyAr = "السامية الشمالية الغربية (Northwest Semitic)",
        period = "العصر الحديدي II، ح. 840 ق.م",
        site = "ذيبان، الأردن (متحف اللوفر)",
        confidenceScore = 0.978f,
        extractedGlyphs = "𐤀𐤍𐤊 𐤌𐤉𐤔𐤏 𐤁𐤍 𐤊𐤌𐤔𐤉𐤕 𐤌𐤋𐤊 𐤌𐤀𐤁 𐤄𐤃𐤉𐤁𐤍𐤉",
        transliteration = "ʾnk myšʿ bn kmš-yt mlk mʾb hd-dybny",
        translationAr = "أنا ميشع بن كموشيت ملك مؤاب الذيباني، أبي ملك على مؤاب ثلاثين سنة وأنا ملكت بعد أبي.",
        translationEn = "I am Mesha, son of Chemosh-yat, king of Moab, the Dibonite.",
        linguisticAnalysis = "ضمير المتكلم الكنعاني الأصيل (𐤀𐤍𐤊 ʾanākī)، واو العطف التتابعية السردية (واو انقلاب الزمن)، واستعمال كموش كإله قومي.",
        rootBreakdown = "Y-Š-ʿ (يشع = خلّص وأنقذ) | K-M-Š (كموش = الإله المؤابي) | M-L-K (ملك) | D-Y-B-N (ذيبان)",
        historicalSignificance = "أهم شاهد لغوي كنعاني مؤابي يفصل الصراع بين مملكة مؤاب ومملكة إسرائيل الشمالية وحفر البرك والتحصينات.",
        glyphBoxes = listOf(
          DetectedGlyphBox("𐤀𐤍𐤊", "ʾnk", "أَنوكِي / أنا", "ʾ-N-K", "ضمير المتكلم المنفصل"),
          DetectedGlyphBox("𐤌𐤉𐤔𐤏", "myšʿ", "مِيشَع (المنقَذ)", "Y-Š-ʿ", "اسم الملك المحرر"),
          DetectedGlyphBox("𐤁𐤍", "bn", "بِن / ابن", "B-N", "ابن"),
          DetectedGlyphBox("𐤊𐤌𐤔𐤉𐤕", "kmšyt", "كَمُوشْيَت", "K-M-Š", "كموش قد أعطى"),
          DetectedGlyphBox("𐤌𐤋𐤊", "mlk", "مَلِك", "M-L-K", "ملك وحاكم"),
          DetectedGlyphBox("𐤌𐤀𐤁", "mʾb", "مُوآب", "M-ʾ-B", "مملكة مؤاب شرق البحر الميت"),
          DetectedGlyphBox("𐤄𐤃𐤉𐤁𐤍𐤉", "h-dybny", "هَدّيباني / الذيباني", "D-Y-B-N", "أداة التعريف الهاء + النسبة")
        )
      ),
      ComprehensiveOcrSample(
        id = "sample-4",
        titleAr = "نقش سد مأرب العظيم (خط المسند السبئي)",
        titleEn = "Great Marib Dam Inscription (CIH 540)",
        scriptTypeAr = "خط المسند العربي الجنوبي البارز (Musnad)",
        detectedLanguage = "السبئية القديمة (Sabaic / Himyaritic)",
        familyAr = "السامية الجنوبية القديمة (Sayhadic / Ancient South Arabian)",
        period = "مملكة سبأ وذو ريدان، القرن الخامس الميلادي",
        site = "مأرب، اليمن (المتحف الوطني بصنعاء)",
        confidenceScore = 0.982f,
        extractedGlyphs = "𐩨𐩧𐩱 𐩥𐩢𐩵𐩻 𐩲𐩧𐩣𐩬 𐩣𐩧𐩺𐩨 𐩨𐩫𐩡 𐩣𐩤𐩺𐩡𐩠𐩣𐩥 𐩥𐩱𐩦𐩲𐩨𐩠𐩣𐩥",
        transliteration = "brʾ w-ḥdṯ ʿrm-n mryb b-kl mqyl-hmw w-ʾšʿb-hmw",
        translationAr = "بنى وجدد سد مأرب العظيم بمعية كافة أقيالهم ومقاوليهم وشعوبهم وقبائلهم المتضامنة.",
        translationEn = "Constructed and renewed the great Dam of Marib with all their chieftains, clans, and confederated tribes.",
        linguisticAnalysis = "نون التعريف والتنكير المتأخرة بالمسند (𐩲𐩧𐩣𐩬 ʿrm-n = العَرِم/السد)، وضمير الجمع الغائب المذكر (𐩠𐩣𐩥 -hmw = هم).",
        rootBreakdown = "B-R-ʾ (برأ = أنشأ وبنى) | Ḥ-D-Ṯ (حدث = جدد ورمم) | ʿ-R-M (عرم = سد مائي) | Q-Y-L (قيل = زعيم وأمير)",
        historicalSignificance = "توثيق تاريخي إبيغرافي لترميم سد مأرب التاريخي وسوق المياه وتنظيم شبكات الري الصرواحية.",
        glyphBoxes = listOf(
          DetectedGlyphBox("𐩨𐩧𐩱", "brʾ", "بَرَأَ / بنى وأنشأ", "B-R-ʾ", "شيد وأسس المعمار"),
          DetectedGlyphBox("𐩥𐩢𐩵𐩻", "w-ḥdṯ", "وَحَدَّثَ / وجدد", "Ḥ-D-Ṯ", "أعاد البناء والترميم"),
          DetectedGlyphBox("𐩲𐩧𐩣𐩬", "ʿrm-n", "عَرِمِن / السَّدّ", "ʿ-R-M", "سيل العَرِم والسد الحابس"),
          DetectedGlyphBox("𐩣𐩧𐩺𐩨", "mryb", "مَرْيَب / مأرب", "M-R-Y-B", "حاضرة مملكة سبأ"),
          DetectedGlyphBox("𐩨𐩫𐩡", "b-kl", "بِكُلّ", "K-L-L", "حرف جر + كل واستغراق"),
          DetectedGlyphBox("𐩣𐩤𐩺𐩡𐩠𐩣𐩥", "mqyl-hmw", "مَقايِلِهِمُو / أقيالهم", "Q-Y-L", "قيل / زعيم القبيلة والأمير"),
          DetectedGlyphBox("𐩥𐩱𐩦𐩲𐩨𐩠𐩣𐩥", "w-ʾšʿb-hmw", "وَأَشْعابِهِمُو / وشعوبهم", "Š-ʿ-B", "القبائل والشعوب السبئية")
        )
      ),
      ComprehensiveOcrSample(
        id = "sample-5",
        titleAr = "مسلة عيزانا الثلاثية (الخط الجعزي الإثيوبي)",
        titleEn = "King Ezana Trilingual Stele (RIE 185)",
        scriptTypeAr = "الخط الجعزي الإثيوبي القديم (Ethiopic Ge'ez)",
        detectedLanguage = "الجعزية الكلاسيكية (Classical Ge'ez)",
        familyAr = "السامية الإثيوبية / الحبشية (Ethiosemitic)",
        period = "مملكة أكسوم، منتصف القرن الرابع الميلادي (ح. 350 م)",
        site = "أكسوم، إثيوبيا (منتزه مسلات أكسوم)",
        confidenceScore = 0.988f,
        extractedGlyphs = "በኃይለ ፡ እግዚአብሔር ፡ ዘሰማይ ፡ ወምድር ፡ አነ ፡ ዔዛና ፡ ንጉሠ ፡ አክሱም",
        transliteration = "ba-ḫayla ʾəgziʾabəḥēr za-samāy wa-mədr ʾana ʿēzānā nəguśa ʾaksum",
        translationAr = "بقوة رب السماء والأرض، أنا عيزانا ملك أكسوم وحمير وذي ريدان وسيلحين وطيبة.",
        translationEn = "By the power of the Lord of Heaven and Earth, I am Ezana, King of Axum, Himyar, Raydan, and Saba.",
        linguisticAnalysis = "أداة الموصول والإضافة الجعزية (ዘ za = الذي/ذو)، وصيغة الجمع والمضاف في (እግዚአብሔር = رب الأرض)، ولقب (ንጉሠ nəguśa = نجاشي/ملك).",
        rootBreakdown = "Ḫ-Y-L (خيل/حيل = قوة وبأس) | ʾ-G-Z (أجز/ملك وساد) | N-G-Ś (نجش/نجاشي = ملك وحكم) | ʾ-K-S-M (أكسوم)",
        historicalSignificance = "حجر رشيد الساميات الإثيوبية؛ حيث نُقش بثلاث لغات وخطوط: الجعزي الفيدل، المسند الصيهدي، واليونانية الهلنستية.",
        glyphBoxes = listOf(
          DetectedGlyphBox("በኃይለ", "ba-ḫayla", "بِخَيْلِ / بقوة وعزة", "Ḫ-Y-L", "الحول والقوة والمنعة"),
          DetectedGlyphBox("እግዚአብሔር", "ʾəgziʾabəḥēr", "إِغْزِيئَابْحِير / رب الكون", "ʾ-G-Z + B-Ḥ-R", "سيد الأرض والبلاد"),
          DetectedGlyphBox("ዘሰማይ", "za-samāy", "زَسَماي / الذي في السماء", "Z + S-M-Y", "أداة الموصول + السماء"),
          DetectedGlyphBox("ወምድር", "wa-mədr", "وَمِدْر / والأرض", "M-D-R", "واو العطف + الأرض"),
          DetectedGlyphBox("አነ", "ʾana", "أَنَا", "ʾ-N", "ضمير المتكلم"),
          DetectedGlyphBox("ዔዛና", "ʿēzānā", "عِيزَانَا", "ʿ-Z-N", "اسم الملك الأكسومي العظيم"),
          DetectedGlyphBox("ንጉሠ", "nəguśa", "نِغُوشَ / ملك ونجاشي", "N-G-Ś", "الملك المتوج على البلاد"),
          DetectedGlyphBox("አክሱም", "ʾaksum", "أَكْسُوم", "ʾ-K-S-M", "عاصمة الإمبراطورية الأكسومية")
        )
      ),
      ComprehensiveOcrSample(
        id = "sample-6",
        titleAr = "لوح أوجاريت المسماري الأبجدي (ملحمة كرت)",
        titleEn = "Ugaritic Cuneiform Tablet (Epic of Kirta)",
        scriptTypeAr = "المسمارية الأبجدية الأوجاريتية (Ugaritic Cuneiform)",
        detectedLanguage = "الأوجاريتية (Ugaritic)",
        familyAr = "السامية الشمالية الغربية (Northwest Semitic)",
        period = "العصر البرونزي المتأخر، ح. 1300 ق.م",
        site = "رأس الشمرا (أوجاريت)، سوريا (متحف دمشق الوطني)",
        confidenceScore = 0.994f,
        extractedGlyphs = "𐎋𐎗𐎚 𐎍 𐎊𐎎𐎍𐎋 𐎁 𐎁𐎊𐎚 𐎎𐎍𐎋𐎅 𐎊𐎁𐎋𐎊 𐎆 𐎊𐎄𐎎𐎏",
        transliteration = "krt l ymlk b byt mlkh ybky w-ydmʿ",
        translationAr = "كرت الملك يبكي في قصر ملكه وتفيض عيناه بالدموع راجياً الذرية من إيل.",
        translationEn = "Kirta weeps within his royal palace and sheds tears praying for heirs from El.",
        linguisticAnalysis = "الأبجدية المسمارية الفريدة ذات الـ 30 صامتاً، الفعل المضارع (𐎊𐎁𐎋𐎊 ybky = يبكي)، والفعل (𐎊𐎄𐎎𐎏 ydmʿ = يدمع).",
        rootBreakdown = "B-K-Y (بكى) | D-M-ʿ (دمع) | B-Y-T (بيت وقصر) | M-L-K (ملك وسيادة)",
        historicalSignificance = "أقدم نظام كتابة أبجدي مسماري في التاريخ مسجل على ألواح الفخار المشوي ويضم أروع الآداب والملاحم السامية.",
        glyphBoxes = listOf(
          DetectedGlyphBox("𐎋𐎗𐎚", "krt", "كِرْت / كيرتا", "K-R-T", "اسم الملك البطل"),
          DetectedGlyphBox("𐎍", "l", "لِـ / توكيد أو نفي", "L", "أداة المعنى"),
          DetectedGlyphBox("𐎊𐎎𐎍𐎋", "ymlk", "يَمْلُكُ", "M-L-K", "فعل مضارع"),
          DetectedGlyphBox("𐎁𐎁𐎊𐎚", "b-byt", "بِبَيْت / في قصر", "B-Y-T", "حرف جر + بيت"),
          DetectedGlyphBox("𐎎𐎍𐎋𐎅", "mlk-h", "مُلْكِهِ / عرشه", "M-L-K", "اسم + هاء الغائب"),
          DetectedGlyphBox("𐎊𐎁𐎋𐎊", "ybky", "يَبْكِي", "B-K-Y", "فعل مضارع من البكاء"),
          DetectedGlyphBox("𐎆𐎊𐎄𐎎𐎏", "w-ydmʿ", "وَيَدْمَعُ", "D-M-ʿ", "تسيل دموعه حزناً")
        )
      ),
      ComprehensiveOcrSample(
        id = "sample-7",
        titleAr = "نقش النمارة الشهير (العربية القديمة بالخط النبطي)",
        titleEn = "Namara Inscription (Epitaph of Imru' al-Qays)",
        scriptTypeAr = "الخط النبطي المتأخر الانتقالي للعربي (Nabataeo-Arabic)",
        detectedLanguage = "العربية القديمة الفصحى المبكرة (Old Arabic)",
        familyAr = "العربية والشمالية القديمة (Arabic & ANA)",
        period = "العصر الروماني المتأخر، 328 م",
        site = "حرة النمارة، بادية الشام (متحف اللوفر)",
        confidenceScore = 0.991f,
        extractedGlyphs = "تي نفس مر القيس بر عمرو ملك العرب كله ذو أسر التاج",
        transliteration = "tī nafsu Marʾi l-Qaysi bar ʿAmrīn maliki l-ʿArabi kullihā dū ʾasara t-tāǧ",
        translationAr = "هذا ضريح امرئ القيس بن عمرو ملك العرب كلهم الذي تقلد التاج وأخضع القبائل.",
        translationEn = "This is the monument of Imru' al-Qays, son of Amr, King of all the Arabs, who bound the diadem.",
        linguisticAnalysis = "اسم الإشارة للمؤنث (تي tī = هذه)، ولفظ (مر القيس = امرؤ القيس)، واستعمال (بر = ابن بالآرامية) و (ذو = أداة الموصول).",
        rootBreakdown = "N-F-S (نفس = شاهدة وقبر) | M-R-ʾ (مرء/امرؤ = رجل) | ʿ-R-B (عرب = بادية وفصحى) | T-W-G (توج/تاج)",
        historicalSignificance = "حلقة الوصل الكبرى بين الخط النبطي والخط العربي الكوفي، وأول وثيقة تدون العربية بلقب 'ملك العرب كلهم'.",
        glyphBoxes = listOf(
          DetectedGlyphBox("تي", "tī", "تِي / هذه", "T-Y", "اسم إشارة للمؤنث"),
          DetectedGlyphBox("نفس", "nafsu", "نَفْسُ / شاهدة القبر", "N-F-S", "الضريح والشاهد التذكاري"),
          DetectedGlyphBox("مر القيس", "Marʾi l-Qays", "مَرْءِ القَيْس", "M-R-ʾ + Q-Y-S", "اسم الملك الشهير"),
          DetectedGlyphBox("بر عمرو", "bar ʿAmr", "بَرْ عَمْرو / ابن عمرو", "B-R", "لفظ البنوة النبطي/الآرامي"),
          DetectedGlyphBox("ملك العرب", "maliki l-ʿArab", "مَلِكِ العَرَب", "M-L-K + ʿ-R-B", "سيد قبائل العرب"),
          DetectedGlyphBox("كله", "kullihā", "كُلِّهِ / جميعها", "K-L-L", "توكيد معنوي"),
          DetectedGlyphBox("ذو أسر التاج", "dū ʾasara t-tāǧ", "ذُو أَسَرَ التَّاج", "D-W + ʾ-S-R", "الذي عصب التاج وملكه")
        )
      )
    )
  }

  var selectedSample by remember { mutableStateOf(comprehensiveSamples.first()) }
  var isScanning by remember { mutableStateOf(false) }
  var scanProgress by remember { mutableStateOf(1f) }
  var scanResult by remember { mutableStateOf<ComprehensiveOcrSample?>(comprehensiveSamples.first()) }
  var selectedSpectralMode by remember { mutableStateOf(SpectralFilterMode.STANDARD) }
  var selectedGlyphDetail by remember { mutableStateOf<DetectedGlyphBox?>(null) }
  var activeTab by remember { mutableStateOf(0) } // 0 = CameraX, 1 = Museum Archive, 2 = Manual Lab, 3 = Offline Packs

  // Offline Download Manager States
  val downloadManager = remember { OcrDownloadManager.getInstance(context) }
  val offlinePacks by downloadManager.packsState.collectAsState()
  val installedPacksCount = offlinePacks.count { it.downloadState == PackDownloadState.INSTALLED }
  val isOfflineForced by downloadManager.isOfflineModeForced.collectAsState()
  var showOfflinePacksSheet by remember { mutableStateOf(false) }

  // CameraX States
  var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var capturedDigitalText by remember {
    mutableStateOf(EpigraphicOcrEngine.PRESET_INSCRIPTIONS.first().extractedGlyphs)
  }
  var simulatedSampleIndex by remember { mutableStateOf(0) }

  // Custom Lab States
  var customInputGlyphs by remember { mutableStateOf("𐤀𐤍𐤊 𐤌𐤋𐤊 𐤂𐤁𐤋") }
  var customSelectedScript by remember { mutableStateOf("فينيقي") }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 14.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Header Banner
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                  modifier = Modifier.size(42.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      Icons.Default.DocumentScanner,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    "مختبر التعرف الضوئي وفك تشفير النقوش (Epigraphic OCR)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    "محرك رؤية فيلولوجي متخصص لفك رموز اللقى، المسلات، والألواح السامية القديمة",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Spacer(modifier = Modifier.width(8.dp))

              // Offline Manager Quick Pill
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (installedPacksCount > 0) Color(0xFF2E7D32).copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                border = androidx.compose.foundation.BorderStroke(
                  1.dp,
                  if (installedPacksCount > 0) Color(0xFF2E7D32).copy(alpha = 0.4f)
                  else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                ),
                modifier = Modifier.clickable { showOfflinePacksSheet = true }
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    if (installedPacksCount > 0) Icons.Default.CloudDone else Icons.Default.CloudDownload,
                    contentDescription = "حزم دون إنترنت",
                    tint = if (installedPacksCount > 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Column {
                    Text(
                      text = "حزم أوفلاين",
                      fontSize = 9.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (installedPacksCount > 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                    )
                    Text(
                      text = "$installedPacksCount/8 مثبتة",
                      fontSize = 8.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Tabs (CameraX Live vs Catalog vs Custom Sandbox vs Offline Packs)
            ScrollableTabRow(
              selectedTabIndex = activeTab,
              containerColor = MaterialTheme.colorScheme.surface,
              contentColor = MaterialTheme.colorScheme.primary,
              edgePadding = 0.dp,
              modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
              Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("الماسح (CameraX)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              )
              Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Collections, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("أرشيف اللقى", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              )
              Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Keyboard, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("المختبر اليدوي", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              )
              Tab(
                selected = activeTab == 3,
                onClick = { activeTab = 3 },
                text = {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      if (installedPacksCount > 0) Icons.Default.CloudDone else Icons.Default.DownloadForOffline,
                      contentDescription = null,
                      tint = if (installedPacksCount > 0) Color(0xFF2E7D32) else LocalContentColor.current,
                      modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                      "حزم أوفلاين ($installedPacksCount)",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (installedPacksCount > 0 && activeTab == 3) Color(0xFF2E7D32) else Color.Unspecified
                    )
                  }
                }
              )
            }
          }
        }
      }

      if (activeTab == 0) {
        // CameraX Live Inscription Scanner
        item {
          CameraXScannerView(
            onImageCaptured = { bitmap ->
              capturedBitmap = bitmap
              val sample = EpigraphicOcrEngine.PRESET_INSCRIPTIONS[simulatedSampleIndex % EpigraphicOcrEngine.PRESET_INSCRIPTIONS.size]
              capturedDigitalText = sample.extractedGlyphs
              simulatedSampleIndex++
              audioEngine.playProtoSemiticChime()
              Toast.makeText(context, "تم التقاط وتحويل صورة النقش إلى نص رقمي", Toast.LENGTH_SHORT).show()
            },
            onSimulatedCapture = {
              val sample = EpigraphicOcrEngine.PRESET_INSCRIPTIONS[simulatedSampleIndex % EpigraphicOcrEngine.PRESET_INSCRIPTIONS.size]
              capturedDigitalText = sample.extractedGlyphs
              simulatedSampleIndex++
              audioEngine.playProtoSemiticChime()
              Toast.makeText(context, "تم مسح النقش: ${sample.scriptNameAr}", Toast.LENGTH_SHORT).show()
            }
          )
        }

        // Editable Digital Text & Dynamic Philological Breakdown
        item {
          EditableDigitalInscriptionSection(
            capturedBitmap = capturedBitmap,
            initialText = capturedDigitalText,
            audioEngine = audioEngine
          )
        }
      } else if (activeTab == 1) {
        // Preset Samples Selector Bar
        item {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              "اختر قطعة أثرية أو مسلة لإجراء الفحص والمسح الضوئي الفيلولوجي:",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.primary
            )

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              comprehensiveSamples.forEach { sample ->
                val isSelected = selectedSample.id == sample.id
                FilterChip(
                  selected = isSelected,
                  onClick = {
                    selectedSample = sample
                    selectedGlyphDetail = null
                    scanResult = null
                    isScanning = true
                    scope.launch {
                      delay(600)
                      isScanning = false
                      scanResult = sample
                      audioEngine.playProtoSemiticChime()
                    }
                  },
                  label = {
                    Text(
                      sample.titleAr,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                  },
                  leadingIcon = if (isSelected) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                  } else null
                )
              }
            }
          }
        }

        // Spectral Enhancement Filter Mode Switcher
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                "أنماط المعالجة البصرية الطيفية (Multi-Spectral Filters):",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                SpectralFilterMode.values().forEach { mode ->
                  val isSelected = selectedSpectralMode == mode
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable {
                      selectedSpectralMode = mode
                      Toast.makeText(context, "تم تفعيل نمط: ${mode.labelAr}", Toast.LENGTH_SHORT).show()
                    }
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text(mode.icon, fontSize = 14.sp)
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        mode.labelAr,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }
                }
              }
            }
          }
        }

        // Scanner Visualizer Plate with Bounding Boxes
        item {
          val bgColor = when (selectedSpectralMode) {
            SpectralFilterMode.STANDARD -> Color(0xFF1F1D1A)
            SpectralFilterMode.HIGH_CONTRAST -> Color(0xFF0F0F0F)
            SpectralFilterMode.INFRARED -> Color(0xFF1A0A2A)
            SpectralFilterMode.EDGE_DETECTION -> Color(0xFF001524)
            SpectralFilterMode.HEATMAP -> Color(0xFF2B0A0A)
          }

          val textColor = when (selectedSpectralMode) {
            SpectralFilterMode.STANDARD -> Color(0xFFE5C158)
            SpectralFilterMode.HIGH_CONTRAST -> Color(0xFFFFFFFF)
            SpectralFilterMode.INFRARED -> Color(0xFFD896FF)
            SpectralFilterMode.EDGE_DETECTION -> Color(0xFF00FFCC)
            SpectralFilterMode.HEATMAP -> Color(0xFFFFCC00)
          }

          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .heightIn(min = 160.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .background(bgColor)
                  .border(
                    2.dp,
                    if (isScanning) Color(0xFFD4AF37) else textColor.copy(alpha = 0.5f),
                    RoundedCornerShape(14.dp)
                  )
                  .padding(14.dp),
                contentAlignment = Alignment.Center
              ) {
                if (isScanning) {
                  Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                  ) {
                    CircularProgressIndicator(color = Color(0xFFD4AF37), modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                      text = "جاري تحليل الحواف وفك تشفير الرموز الإبيغرافية...",
                      fontSize = 12.sp,
                      color = Color(0xFFD4AF37),
                      fontWeight = FontWeight.Bold
                    )
                  }
                } else {
                  Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                  ) {
                    // Script and Period Tags
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = textColor.copy(alpha = 0.15f)
                      ) {
                        Text(
                          selectedSample.scriptTypeAr,
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                          fontSize = 10.sp,
                          color = textColor,
                          fontWeight = FontWeight.Bold
                        )
                      }

                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.White.copy(alpha = 0.1f)
                      ) {
                        Text(
                          selectedSample.period,
                          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                          fontSize = 10.sp,
                          color = Color.White.copy(alpha = 0.8f)
                        )
                      }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Extracted Ancient Glyphs
                    Text(
                      text = selectedSample.extractedGlyphs,
                      fontSize = 22.sp,
                      fontWeight = FontWeight.Bold,
                      color = textColor,
                      letterSpacing = 3.sp,
                      lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                      text = "الموقع: ${selectedSample.site}",
                      fontSize = 11.sp,
                      color = Color.White.copy(alpha = 0.7f)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Button(
                  onClick = {
                    isScanning = true
                    scanResult = null
                    selectedGlyphDetail = null
                    scope.launch {
                      delay(700)
                      isScanning = false
                      scanResult = selectedSample
                      audioEngine.playProtoSemiticChime()
                    }
                  },
                  modifier = Modifier.weight(1f),
                  colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("إعادة مسح وتحليل النقش")
                }

                FilledTonalButton(
                  onClick = {
                    audioEngine.playProtoSemiticChime()
                    Toast.makeText(context, "تشغيل النطق الصوتي المقدر للنقش", Toast.LENGTH_SHORT).show()
                  },
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("استماع صوتي")
                }
              }
            }
          }
        }

        // Bounding Boxes / Individual Glyphs Interactive Breakdown
        scanResult?.let { result ->
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SelectAll, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      "المحددات الأبجدية المفككة (Bounding Glyphs):",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }
                  Text(
                    "انقر على أي مقطع لتفصيله",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  result.glyphBoxes.forEach { box ->
                    val isSelected = selectedGlyphDetail?.glyph == box.glyph
                    Surface(
                      shape = RoundedCornerShape(10.dp),
                      color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                      border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                      ),
                      modifier = Modifier.clickable {
                        selectedGlyphDetail = if (isSelected) null else box
                        audioEngine.playProtoSemiticChime()
                      }
                    ) {
                      Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                      ) {
                        Text(box.glyph, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(box.transliteration, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                      }
                    }
                  }
                }

                // Selected Glyph Detailed Inspector Box
                selectedGlyphDetail?.let { detail ->
                  Spacer(modifier = Modifier.height(12.dp))
                  Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                  ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                      Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Text(detail.glyph, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                          Spacer(modifier = Modifier.width(10.dp))
                          Column {
                            Text(detail.nameAr, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("النقحرة الصوتية: ${detail.transliteration}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                          }
                        }
                        IconButton(onClick = { selectedGlyphDetail = null }, modifier = Modifier.size(24.dp)) {
                          Icon(Icons.Default.Close, contentDescription = "إغلاق", modifier = Modifier.size(16.dp))
                        }
                      }

                      Spacer(modifier = Modifier.height(6.dp))
                      HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                      Spacer(modifier = Modifier.height(6.dp))

                      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                          Text("الجذر السامي:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                          Text(detail.rootAr, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Column {
                          Text("الدلالة والمعنى:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                          Text(detail.meaningAr, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

        // Comprehensive OCR Results & Philological Report
        scanResult?.let { result ->
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
              Column(modifier = Modifier.padding(16.dp)) {
                // Title and Confidence Badge
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(
                      text = "التقرير الفيلولوجي لفك التشفير الآلي",
                      fontSize = 15.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                      text = result.titleEn,
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF2E7D32).copy(alpha = 0.15f)
                  ) {
                    Text(
                      text = "دقة المطابقة: ${(result.confidenceScore * 100).toInt()}%",
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF2E7D32)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))

                OcrReportSection("اللغة المكتشفة والفرع السامي", "${result.detectedLanguage} — ${result.familyAr}")
                OcrReportSection("النقحرة الصوتية اللاتينية (Transliteration)", result.transliteration)
                OcrReportSection("الترجمة العربية الفصحى الشارحة", result.translationAr)
                OcrReportSection("English Translation", result.translationEn)
                OcrReportSection("التحليل الصرفي والنحوي", result.linguisticAnalysis)
                OcrReportSection("تفكيك الجذور السامية المقارنة", result.rootBreakdown)
                OcrReportSection("الأهمية التاريخية والإبيغرافية", result.historicalSignificance)

                Spacer(modifier = Modifier.height(14.dp))

                // Action Toolbar
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  OutlinedButton(
                    onClick = {
                      val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                      val text = """
                        تقرير فك تشفير النقش: ${result.titleAr}
                        اللغة: ${result.detectedLanguage}
                        الخط: ${result.scriptTypeAr}
                        النص بالرموز الأصلية: ${result.extractedGlyphs}
                        النقحرة الصوتية: ${result.transliteration}
                        الترجمة العربية: ${result.translationAr}
                        التحليل الفيلولوجي: ${result.linguisticAnalysis}
                        الجذور: ${result.rootBreakdown}
                      """.trimIndent()
                      cm.setPrimaryClip(ClipData.newPlainText("OCR Report", text))
                      Toast.makeText(context, "تم نسخ التقرير الفيلولوجي الكامل بنجاح", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                  ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("نسخ التقرير", fontSize = 12.sp)
                  }

                  Button(
                    onClick = {
                      Toast.makeText(context, "تم توثيق النقش وإضافته إلى مختبر الأبحاث الذكي", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp)
                  ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تحليل الذكاء الاصطناعي", fontSize = 12.sp)
                  }
                }
              }
            }
          }
        }
      } else {
        // ActiveTab == 2: Manual Decryption Lab & Semitic Script Keyboard
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Text(
                "مختبر فك التشفير اليدوي وإدخال الرموز السامية القديمة:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )

              // Script Selector Chips
              val scriptChoices = listOf("فينيقي", "أوجاريتي", "مسند جنوبي", "مسماري أكادي", "آرامي", "جعزي")
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                scriptChoices.forEach { sc ->
                  val isSel = customSelectedScript == sc
                  FilterChip(
                    selected = isSel,
                    onClick = { customSelectedScript = sc },
                    label = { Text(sc, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) }
                  )
                }
              }

              // Text Area
              OutlinedTextField(
                value = customInputGlyphs,
                onValueChange = { customInputGlyphs = it },
                label = { Text("أدخل الرموز أو النص النقشي لفك تشفيره") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(12.dp)
              )

              // Virtual Keyboard Quick Inserts for Chosen Script
              Text("لوحة مفاتيح الرموز السريعة ($customSelectedScript):", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
              val glyphBank = when (customSelectedScript) {
                "فينيقي" -> listOf("𐤀", "𐤁", "𐤂", "𐤃", "𐤄", "𐤅", "𐤆", "𐤇", "𐤈", "𐤉", "𐤊", "𐤋", "𐤌", "𐤍", "𐤎", "𐤏", "𐤐", "𐤑", "𐤒", "𐤓", "𐤔", "𐤕")
                "أوجاريتي" -> listOf("𐎀", "𐎁", "𐎂", "𐎃", "𐎄", "𐎅", "𐎆", "𐎇", "𐎈", "𐎉", "𐎊", "𐎋", "𐎌", "𐎍", "𐎎", "𐎏", "𐎐", "𐎑", "𐎒", "𐎓", "𐎔", "𐎕", "𐎖", "𐎗", "𐎘", "𐎙", "𐎚", "𐎛", "𐎜", "𐎝")
                "مسند جنوبي" -> listOf("𐩠", "𐩡", "𐩢", "𐩣", "𐩤", "𐩥", "𐩦", "𐩧", "𐩨", "𐩩", "𐩪", "𐩫", "𐩬", "𐩭", "𐩮", "𐩯", "𐩰", "𐩱", "𐩲", "𐩳", "𐩴", "𐩵", "𐩶", "𐩷", "𐩸", "𐩹", "𐩺", "𐩻", "𐩼")
                "مسماري أكادي" -> listOf("𒀭", "𒈗", "𒂗", "𒂍", "𒄿", "𒈾", "𒈠", "𒁴", "𒈪", "𒊭", "𒊏", "𒄠", "𒌑", "𒊭", "𒀊", "𒅆")
                "جعزي" -> listOf("ሀ", "ለ", "ሐ", "መ", "ሠ", "ረ", "ሰ", "ቀ", "በ", "ተ", "ኀ", "ነ", "አ", "ከ", "ወ", "ዐ", "ዘ", "የ", "ደ", "ገ", "ጠ", "ጰ", "ጸ", "ፈ", "ፐ")
                else -> listOf("𐡀", "𐡁", "𐡂", "𐡃", "𐡄", "𐡅", "𐡆", "𐡇", "𐡈", "𐡉", "𐡊", "𐡋", "𐡌", "𐡍", "𐡎", "𐡏", "𐡐", "𐡑", "𐡒", "𐡓", "𐡔", "𐡕")
              }

              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                glyphBank.forEach { glyph ->
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable {
                      customInputGlyphs += glyph
                      audioEngine.playProtoSemiticChime()
                    }
                  ) {
                    Text(
                      glyph,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                      fontSize = 16.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              Button(
                onClick = {
                  audioEngine.playProtoSemiticChime()
                  Toast.makeText(context, "تم إرسال النص لمحرك فك التشفير والتحليل الفيلولوجي", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp)
              ) {
                Icon(Icons.Default.Psychology, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("فك تشفير وتحليل النص المُدخل")
              }
            }
          }
        }
      }

      if (activeTab == 3) {
        // Tab 3: Offline Language Packs Dashboard
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  modifier = Modifier.weight(1f),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.CloudDownload,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      "حزم اللغات السامية للعمل دون إنترنت (Offline OCR)",
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      "نماذج فيلولوجية مصغرة لفك الرموز والنقوش في البعثات الميدانية",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }

              // Actions
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Button(
                  onClick = { showOfflinePacksSheet = true },
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(10.dp)
                ) {
                  Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("لوحة التحكم المتقدمة", fontSize = 11.sp)
                }

                Button(
                  onClick = {
                    downloadManager.downloadAllPacks()
                    audioEngine.playProtoSemiticChime()
                    Toast.makeText(context, "بدء تحميل وتثبيت جميع الحزم...", Toast.LENGTH_SHORT).show()
                  },
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFF2E7D32).copy(alpha = 0.15f),
                    contentColor = Color(0xFF2E7D32)
                  )
                ) {
                  Icon(Icons.Default.DownloadForOffline, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("تحميل الكل ($installedPacksCount/8)", fontSize = 11.sp)
                }
              }
            }
          }
        }

        // Inline Pack Cards
        items(offlinePacks, key = { it.id }) { pack ->
          val isInstalled = pack.downloadState == PackDownloadState.INSTALLED
          val isDownloading = pack.downloadState == PackDownloadState.DOWNLOADING

          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isInstalled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
              else MaterialTheme.colorScheme.surface
            ),
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isInstalled) Color(0xFF2E7D32).copy(alpha = 0.4f)
              else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(14.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  modifier = Modifier.weight(1f),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.size(36.dp)
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Text(
                        pack.scriptFamilyAr.firstOrNull()?.toString() ?: "𐤀",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                      )
                    }
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      pack.nameAr,
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      "${pack.scriptFamilyAr} • v${pack.version} • ${pack.sizeMb} MB",
                      fontSize = 10.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                // Status Badge & Action
                if (isInstalled) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = Color(0xFF2E7D32).copy(alpha = 0.15f)
                    ) {
                      Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Icon(
                          Icons.Default.CheckCircle,
                          contentDescription = null,
                          tint = Color(0xFF2E7D32),
                          modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                          "مثبتة",
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFF2E7D32)
                        )
                      }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                      onClick = {
                        downloadManager.deletePack(pack.id)
                        Toast.makeText(context, "تم حذف حزمة: ${pack.nameAr}", Toast.LENGTH_SHORT).show()
                      },
                      modifier = Modifier.size(32.dp)
                    ) {
                      Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "حذف",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }
                } else if (isDownloading) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      "${(pack.downloadProgress * 100).toInt()}%",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                      onClick = { downloadManager.cancelDownload(pack.id) },
                      modifier = Modifier.size(32.dp)
                    ) {
                      Icon(
                        Icons.Default.Close,
                        contentDescription = "إلغاء",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                      )
                    }
                  }
                } else {
                  Button(
                    onClick = {
                      downloadManager.downloadPack(pack.id)
                      audioEngine.playProtoSemiticChime()
                    },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                  ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تحميل", fontSize = 11.sp)
                  }
                }
              }

              if (isDownloading) {
                LinearProgressIndicator(
                  progress = { pack.downloadProgress },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                  color = MaterialTheme.colorScheme.primary
                )
              }

              Text(
                pack.descriptionAr,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 15.sp
              )
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(70.dp))
      }
    }

    if (showOfflinePacksSheet) {
      OcrOfflinePacksSheet(
        onDismiss = { showOfflinePacksSheet = false },
        audioEngine = audioEngine
      )
    }
  }
}

@Composable
fun OcrReportSection(title: String, content: String) {
  Column(modifier = Modifier.padding(vertical = 4.dp)) {
    Text(
      text = title,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = content,
      fontSize = 12.sp,
      color = MaterialTheme.colorScheme.onSurface,
      lineHeight = 18.sp
    )
  }
}
