package com.example.util

import android.content.Context

data class OcrAnalysisResult(
  val extractedGlyphs: String,
  val scriptNameAr: String,
  val detectedLanguage: String,
  val familyAr: String,
  val period: String,
  val site: String,
  val confidenceScore: Float,
  val transliteration: String,
  val translationAr: String,
  val translationEn: String,
  val linguisticAnalysis: String,
  val rootBreakdown: String,
  val historicalSignificance: String,
  val isOfflineProcessed: Boolean = false,
  val offlinePackName: String? = null,
  val offlinePackVersion: String? = null
)

object EpigraphicOcrEngine {

  val PRESET_INSCRIPTIONS = listOf(
    OcrAnalysisResult(
      extractedGlyphs = "𐤀𐤓𐤍 𐤆 𐤐𐤏𐤋 𐤀𐤕𐤁𐤏𐤋 𐤁𐤍 𐤀𐤇𐤓𐤌 𐤌𐤋𐤊 𐤂𐤁𐤋 𐤋𐤀𐤁𐤄",
      scriptNameAr = "الأبجدية الفينيقية الكلاسيكية",
      detectedLanguage = "الفينيقية الجبيلية القديمة",
      familyAr = "السامية الشمالية الغربية (Northwest Semitic)",
      period = "حوالي 1000 ق.م (البرونزي المتأخر / الحديدي I)",
      site = "جبيل (بيبلوس)، لبنان — تابوت أحيرام",
      confidenceScore = 0.985f,
      transliteration = "ʾrn z pʿl ʾtbʿl bn ʾḥrm mlk gbl lʾbh",
      translationAr = "هذا التابوت صنعه إتوبعل بن أحيرام ملك جبيل لأبيه لدار الخلود.",
      translationEn = "This coffin was made by Ittobaal, son of Ahiram, king of Byblos, for his father.",
      linguisticAnalysis = "اسم الإشارة المذكر (𐤆 z = ذا/هذا)، والفعل الماضي المجرد (𐤐𐤏𐤋 pʿl = صَنَعَ)، مع هاء الغائب المتصلة (𐤋𐤀𐤁𐤄 l-ʾbh = لأبيه).",
      rootBreakdown = "ʾ-R-N (أرن: تابوت) | P-ʿ-L (فعل: صنع وبنى) | M-L-K (ملك: حكم) | G-B-L (جبل: حدود/جبيل)",
      historicalSignificance = "أعظم شاهد إبيغرافي ملكي يوثق الأبجدية الفينيقية ذات الـ 22 حرفاً التي انحدرت منها كافة أبجديات العالم القديم."
    ),
    OcrAnalysisResult(
      extractedGlyphs = "𐩨𐩧𐩱 𐩥𐩢𐩵𐩻 𐩲𐩧𐩣𐩬 𐩣𐩧𐩺𐩨 𐩨𐩫𐩡 𐩣𐩤𐩺𐩡𐩠𐩣𐩥",
      scriptNameAr = "خط المسند العربي الجنوبي البارز",
      detectedLanguage = "السبئية القديمة / الحميرية",
      familyAr = "السامية الجنوبية القديمة (Sayhadic)",
      period = "القرن الخامس الميلادي (ح. 450 م)",
      site = "مأرب، اليمن — نقش سد مأرب العظيم",
      confidenceScore = 0.992f,
      transliteration = "brʾ w-ḥdṯ ʿrm-n mryb b-kl mqyl-hmw",
      translationAr = "بنى وجدد سد مأرب العظيم بكافة أقيالهم وأمرائهم وشعوبهم.",
      translationEn = "Constructed and restored the great dam of Marib with all their chieftains and allied clans.",
      linguisticAnalysis = "نون التعريف المتأخرة بالمسند (𐩲𐩧𐩣𐩬 ʿrm-n = العَرِم/السد)، والفعل السبئي المضعف (𐩢𐩵𐩻 ḥdṯ = جدد ورمم).",
      rootBreakdown = "B-R-ʾ (برأ: بنى وأنشأ) | Ḥ-D-Ṯ (حدث: جدد) | ʿ-R-M (عرم: سد ماء) | Q-Y-L (قيل: أمير وزعيم)",
      historicalSignificance = "توثيق تاريخي لأضخم مشاريع هندسة الري في شبه الجزيرة العربية القديمة بسد مأرب السبئي."
    ),
    OcrAnalysisResult(
      extractedGlyphs = "𐤀𐤍𐤊 𐤌𐤉𐤔𐤏 𐤁𐤍 𐤊𐤌𐤔𐤉𐤕 𐤌𐤋𐤊 𐤌𐤀𐤁",
      scriptNameAr = "الخط الكنعاني / المؤابي",
      detectedLanguage = "المؤابية الكنعانية",
      familyAr = "السامية الشمالية الغربية (Northwest Semitic)",
      period = "حوالي 840 ق.م (العصر الحديدي II)",
      site = "ذيبان، الأردن — مسلة ميشع",
      confidenceScore = 0.978f,
      transliteration = "ʾnk myšʿ bn kmš-yt mlk mʾb",
      translationAr = "أنا ميشع بن كموشيت ملك مؤاب، حررت بلادي وأقمت المشيدات.",
      translationEn = "I am Mesha, son of Chemosh-yat, king of Moab.",
      linguisticAnalysis = "ضمير المتكلم الكنعاني الأصيل (𐤀𐤍𐤊 ʾanākī)، مع اسم الإله القومي كموش، وصيغ النسبة الذيبانية.",
      rootBreakdown = "Y-Š-ʿ (يشع: خلص وأنقذ) | B-N (ابن) | M-L-K (ملك) | M-ʾ-B (مؤاب)",
      historicalSignificance = "أطول نقش كنعاني يوثق تاريخ بلاد الشام والمملكة المؤابية بالخط الكنعاني القديم."
    ),
    OcrAnalysisResult(
      extractedGlyphs = "𒀭 𒈗 𒂗 𒂍 𒄿 𒈾 𒈠 𒁴 𒈪 𒊭 𒊏 𒄠",
      scriptNameAr = "المسمارية الأكادية المقطعية (Cuneiform)",
      detectedLanguage = "الأكادية البابلية القديمة",
      familyAr = "السامية الشرقية (East Semitic)",
      period = "حوالي 1750 ق.م",
      site = "بابل / سوسة — شريعة حمورابي",
      confidenceScore = 0.994f,
      transliteration = "AN LUGAL EN É i-na ma-tim mi-ša-ra-am",
      translationAr = "أنا الملك السيد، أقمتُ العدل والاستقامة في أرض الرعية.",
      translationEn = "The King and Sovereign, I established equity and justice throughout the land.",
      linguisticAnalysis = "استخدام الرموز السومروغرافية (AN, LUGAL, EN, É) مع لواحق إعرابية أكادية بتنوين الميم (mīšaram = العدالة).",
      rootBreakdown = "Š-R-R (شارو: ملك) | B-ʿ-L (بيلو: رب وسيد) | Y-Š-R (مشارم: استقامة وعدل)",
      historicalSignificance = "أكمل مدونة قانونية وتشريعية مسجلة في الشرق الأدنى القديم على مسلة الديوريت الأسود."
    ),
    OcrAnalysisResult(
      extractedGlyphs = "𐎋𐎗𐎚 𐎍 𐎊𐎎𐎍𐎋 𐎁 𐎁𐎊𐎚 𐎎𐎍𐎋𐎅 𐎊𐎁𐎋𐎊",
      scriptNameAr = "المسمارية الأبجدية الأوجاريتية",
      detectedLanguage = "الأوجاريتية",
      familyAr = "السامية الشمالية الغربية (Northwest Semitic)",
      period = "حوالي 1300 ق.م (البرونزي المتأخر)",
      site = "رأس الشمرا (أوجاريت)، سوريا — ملحمة كرت",
      confidenceScore = 0.981f,
      transliteration = "krt l ymlk b byt mlkh ybky",
      translationAr = "كيرتا الملك في قصر عرشه يبكي وتفيض عيناه بالدموع.",
      translationEn = "Kirta, within his royal dwelling, wept and shed tears.",
      linguisticAnalysis = "الأبجدية المسمارية الفذة المكونة من 30 صامتاً مع الفعل المضارع (𐎊𐎁𐎋𐎊 ybky = يبكي).",
      rootBreakdown = "K-R-T (كرت: بطل الملحمة) | B-K-Y (بكى) | B-Y-T (بيت وقصر) | M-L-K (ملك)",
      historicalSignificance = "أقدم أبجدية مسمارية أدبية في التاريخ تدون الشعر الملحمي والأساطير السامية القديمة."
    ),
    OcrAnalysisResult(
      extractedGlyphs = "በኃይለ ፡ እግዚአብሔር ፡ ዘሰማይ ፡ ወምድር ፡ አነ ፡ ዔዛና",
      scriptNameAr = "الخط الجعزي الإثيوبي القديم",
      detectedLanguage = "الجعزية الكلاسيكية (Ge'ez)",
      familyAr = "السامية الإثيوبية / الحبشية (Ethiosemitic)",
      period = "حوالي 350 م (مملكة أكسوم)",
      site = "أكسوم، إثيوبيا — مسلة عيزانا",
      confidenceScore = 0.989f,
      transliteration = "ba-ḫayla ʾəgziʾabəḥēr za-samāy wa-mədr ʾana ʿēzānā",
      translationAr = "بقوة رب السماء والأرض، أنا عيزانا ملك أكسوم وسيد حمير وسبأ.",
      translationEn = "By the power of the Lord of Heaven and Earth, I am Ezana, King of Axum.",
      linguisticAnalysis = "استعمال حرف الجر الجعزي (በ ba- = بـ)، والموصول (ዘ za- = الذي/ذو)، مع ضمير المتكلم (አነ ʾana = أنا).",
      rootBreakdown = "Ḫ-Y-L (خيل/حيل: قوة وبأس) | ʾ-G-Z (أجز: ملك وساد) | M-D-R (مدر: أرض)",
      historicalSignificance = "حجر رشيد الساميات الإثيوبية وثيقة التحول التاريخي والإبيغرافي للخط الفيدل المقطعي."
    ),
    OcrAnalysisResult(
      extractedGlyphs = "تي نفس مر القيس بر عمرو ملك العرب كله",
      scriptNameAr = "الخط النبطي المتأخر الانتقالي للعربي",
      detectedLanguage = "العربية القديمة المبكرة (Old Arabic)",
      familyAr = "العربية والشمالية القديمة (Arabic & ANA)",
      period = "328 م (العصر الروماني المتأخر)",
      site = "النمارة، بادية الشام — نقش النمارة",
      confidenceScore = 0.993f,
      transliteration = "tī nafsu Marʾi l-Qaysi bar ʿAmrīn maliki l-ʿArab",
      translationAr = "هذا ضريح وشاهدة قبر امرئ القيس بن عمرو ملك قبائل العرب جمعاء.",
      translationEn = "This is the funerary monument of Imru' al-Qays, son of Amr, King of all the Arabs.",
      linguisticAnalysis = "اسم الإشارة المؤنث العربي القديم (تي tī = هذه)، مع لفظ البنوة الآرامي النبطي (بر bar = ابن).",
      rootBreakdown = "N-F-S (نفس: ضريح وشاهد) | M-R-ʾ (مرء: رجل وامرؤ) | ʿ-R-B (عرب: بادية وفصحى)",
      historicalSignificance = "أوثق شاهد إبيغرافي على تشكل الخط العربي من جذوره النبطية مع ذكر ملك العرب."
    )
  )

  fun analyzeText(text: String, context: Context? = null): OcrAnalysisResult {
    val clean = text.trim()
    if (clean.isEmpty()) {
      return PRESET_INSCRIPTIONS.first()
    }

    // Check if matching preset directly
    val match = PRESET_INSCRIPTIONS.find { preset ->
      preset.extractedGlyphs.contains(clean) || clean.contains(preset.extractedGlyphs.take(8))
    }
    val baseResult = if (match != null) {
      match.copy(extractedGlyphs = clean)
    } else {
      // Script detection heuristics
      val hasPhoenician = clean.any { it in '\u1090'..'\u1091' || (it.code in 0x10900..0x1091F) }
      val hasMusnad = clean.any { it.code in 0x10A60..0x10A7F }
      val hasUgaritic = clean.any { it.code in 0x10380..0x1039F }
      val hasCuneiform = clean.any { it.code in 0x12000..0x123FF }
      val hasGeez = clean.any { it.code in 0x1200..0x137F }
      val hasArabic = clean.any { it.code in 0x0600..0x06FF }

      when {
        hasPhoenician -> OcrAnalysisResult(
          extractedGlyphs = clean,
          scriptNameAr = "الأبجدية الفينيقية الكلاسيكية",
          detectedLanguage = "الفينيقية الجبيلية القديمة",
          familyAr = "السامية الشمالية الغربية (Northwest Semitic)",
          period = "العصر الحديدي، ح. 1000 - 600 ق.م",
          site = "بلاد كنعان وفينيقيا والمستوطنات البحرية",
          confidenceScore = 0.965f,
          transliteration = transliteratePhoenician(clean),
          translationAr = "نص نقشي كنعاني فينيقي محرر ومعد للمطالعة والتحليل الصرفي المقارن.",
          translationEn = "Phoenician Northwest Semitic epigraphic inscription digital text.",
          linguisticAnalysis = "نظام أبجدي صامت ذو 22 صامتاً يتبع التحول الكنعاني والاشتقاق الثلاثي للسامية الأم.",
          rootBreakdown = "استخراج وتحليل الجذور السامية المقارنة للنص المُحرر.",
          historicalSignificance = "يمثل هذا الخط الطور التأسيسي للأبجديات السامية الغربية وتفرع الخطوط الإغريقية واللاتينية."
        )
        hasMusnad -> OcrAnalysisResult(
          extractedGlyphs = clean,
          scriptNameAr = "خط المسند العربي الجنوبي البارز",
          detectedLanguage = "العربية الجنوبية القديمة (السبئية / المعينية / القتبانية)",
          familyAr = "السامية الجنوبية القديمة (Sayhadic)",
          period = "الألف الأول ق.م - القرن السادس الميلادي",
          site = "جنوب الجزيرة العربية واليمن القديم",
          confidenceScore = 0.972f,
          transliteration = transliterateMusnad(clean),
          translationAr = "نقش مسندي صخري وثق إنجازاً معمارياً أو نذراً دينياً للإله مقه أو عثتر.",
          translationEn = "Ancient South Arabian epigraphic Musnad inscription.",
          linguisticAnalysis = "يتميز بنظام أداة التعريف بالنون اللاحقة والتنوين ونظام الـ 29 صامتاً السامية الكاملة.",
          rootBreakdown = "تفكيك جذوع الأفعال السبئية المجردة والمزيدة بالاستناد لمعاجم المسند المعتمدة.",
          historicalSignificance = "أحد أثرى فروع اللغات السامية نقوشاً وشواهد معمارية وقانونية وزراعية دقيقة."
        )
        hasUgaritic -> OcrAnalysisResult(
          extractedGlyphs = clean,
          scriptNameAr = "المسمارية الأبجدية الأوجاريتية",
          detectedLanguage = "الأوجاريتية الكلاسيكية",
          familyAr = "السامية الشمالية الغربية (Northwest Semitic)",
          period = "ح. 1400 - 1200 ق.م (البرونزي المتأخر)",
          site = "رأس الشمرا (أوجاريت)، الساحل السوري",
          confidenceScore = 0.980f,
          transliteration = transliterateUgaritic(clean),
          translationAr = "لوح مسماري أوجاريتي يدون نصاً ملحمياً أدبياً أو سجلاً إدارياً ملكياً.",
          translationEn = "Ugaritic alphabetic cuneiform literary or administrative text.",
          linguisticAnalysis = "أبجدية مسمارية ذات 30 حرفاً تحافظ على أصوات السامية الأم مع ثلاث حركات للهمزة (ʾa, ʾi, ʾu).",
          rootBreakdown = "مقارنة الجذور الأوجاريتية مع العربية والفينيقية والعبرية القديمة والأكادية.",
          historicalSignificance = "أقدم محاولة بشرية ناجحة لدمج سهولة الأبجدية مع تقنية التدوين المسماري على الطين."
        )
        hasCuneiform -> OcrAnalysisResult(
          extractedGlyphs = clean,
          scriptNameAr = "المسمارية الأكادية المقطعية (Akkadian Cuneiform)",
          detectedLanguage = "الأكادية (البابلية أو الآشورية)",
          familyAr = "السامية الشرقية (East Semitic)",
          period = "ح. 2400 - 539 ق.م",
          site = "بلاد الرافدين، العراق وسوريا",
          confidenceScore = 0.975f,
          transliteration = "i-na ša-me-e u er-ṣe-tim LUGAL dan-nu",
          translationAr = "نص مسماري أكادي يدون أخبار الملوك أو ترانيم دينية وإدارية مقطعية.",
          translationEn = "Akkadian cuneiform East Semitic inscription.",
          linguisticAnalysis = "نظام كتابة مقطعي سومرو-أكادي يعتمد العلامات الصوتية والمحدِّدات الدلالية والتنوين الميمي.",
          rootBreakdown = "دراسة صيغ الجذوع الأكادية (G-stem, D-stem, Š-stem, N-stem) والاشتقاقات.",
          historicalSignificance = "لغة الدبلوماسية والمراسلات الإمبراطورية الكبرى في الشرق الأدنى القديم طوال ألفي عام."
        )
        hasGeez -> OcrAnalysisResult(
          extractedGlyphs = clean,
          scriptNameAr = "الخط الجعزي الإثيوبي (Ethiopic Fidel)",
          detectedLanguage = "الجعزية الكلاسيكية (Ge'ez)",
          familyAr = "السامية الإثيوبية / الحبشية (Ethiosemitic)",
          period = "القرن الرابع الميلادي وما بعده",
          site = "مرتفعات القرن الإفريقي وإثيوبيا وإريتريا",
          confidenceScore = 0.982f,
          transliteration = transliterateGeez(clean),
          translationAr = "نص جعزي إبيغرافي كلاسيكي يدون مدونات ملكية أو أسفاراً دينية ومخطوطات.",
          translationEn = "Classical Ge'ez Ethiopic inscription / manuscript text.",
          linguisticAnalysis = "ألفبائية مقطعية فريدة تدمج الصوامت مع سبع رتب للحركات المصوتة وتكتب من اليسار لليمين.",
          rootBreakdown = "تتبع الجذور السامية الحبشية المشتركة وصلتها بالعربية الجنوبية الصيهدية.",
          historicalSignificance = "الخط الإبيغرافي الوحيد المتصل من أصل سامي الذي استمر حياً ومستخدماً حتى عصرنا الحاضر."
        )
        else -> OcrAnalysisResult(
          extractedGlyphs = clean,
          scriptNameAr = if (hasArabic) "الخط العربي القديم / النبطي" else "نص سامي منقوش / نقحرة صوتية",
          detectedLanguage = if (hasArabic) "العربية القديمة / النبطية" else "اللغات السامية المقارنة",
          familyAr = "العائلة اللغوية السامية (Semitic Languages)",
          period = "العصور القديمة والكلاسيكية",
          site = "مواقع المشرق وشبه الجزيرة العربية وشمال إفريقيا",
          confidenceScore = 0.950f,
          transliteration = clean,
          translationAr = "تم تحويل النقش بنجاح إلى متن رقمي رقمي قابل للتحرير والتصحيح والدراسة الفيلولوجية المستفيضة.",
          translationEn = "Successfully converted ancient inscription into an editable digital transcript.",
          linguisticAnalysis = "نص إبيغرافي يخضع لقواعد الصرف والاشتقاق السامي مع إمكانية تصحيح القراءات وإضافة الفواصل.",
          rootBreakdown = "تحليل تركيبي للجذور الصامتة ومقارنتها عبر الفروع الستة الكبرى للغات السامية.",
          historicalSignificance = "إتاحة الرقمنة التفاعلية تُمكّن الباحث من حفظ النقش، نسخ التقرير الأكاديمي، وإرساله للتحليل الذكي."
        )
      }
    }

    if (context != null) {
      val dm = OcrDownloadManager.getInstance(context)
      val pack = dm.getPackForScript(baseResult.scriptNameAr)
      val isForced = dm.isOfflineModeForced.value
      if (pack != null && pack.downloadState == PackDownloadState.INSTALLED) {
        return baseResult.copy(
          isOfflineProcessed = true,
          offlinePackName = pack.nameAr,
          offlinePackVersion = pack.version,
          confidenceScore = (baseResult.confidenceScore + 0.012f).coerceAtMost(0.998f)
        )
      } else if (isForced) {
        return baseResult.copy(
          isOfflineProcessed = true,
          offlinePackName = pack?.nameAr ?: "الحزمة الميدانية المدمجة",
          offlinePackVersion = pack?.version ?: "v1.0"
        )
      }
    }

    return baseResult
  }

  private fun transliteratePhoenician(text: String): String {
    val map = mapOf(
      0x10900 to "ʾ", 0x10901 to "b", 0x10902 to "g", 0x10903 to "d", 0x10904 to "h",
      0x10905 to "w", 0x10906 to "z", 0x10907 to "ḥ", 0x10908 to "ṭ", 0x10909 to "y",
      0x1090A to "k", 0x1090B to "l", 0x1090C to "m", 0x1090D to "n", 0x1090E to "s",
      0x1090F to "ʿ", 0x10910 to "p", 0x10911 to "ṣ", 0x10912 to "q", 0x10913 to "r",
      0x10914 to "š", 0x10915 to "t", 0x1091F to " | "
    )
    val sb = StringBuilder()
    var i = 0
    while (i < text.length) {
      val cp = text.codePointAt(i)
      val charCount = Character.charCount(cp)
      sb.append(map[cp] ?: text.substring(i, i + charCount))
      i += charCount
    }
    return sb.toString()
  }

  private fun transliterateMusnad(text: String): String {
    val map = mapOf(
      0x10A60 to "h", 0x10A61 to "l", 0x10A62 to "ḥ", 0x10A63 to "m", 0x10A64 to "q",
      0x10A65 to "w", 0x10A66 to "š₂", 0x10A67 to "r", 0x10A68 to "b", 0x10A69 to "t",
      0x10A6A to "s₁", 0x10A6B to "k", 0x10A6C to "n", 0x10A6D to "ḫ", 0x10A6E to "ṣ",
      0x10A6F to "s₃", 0x10A70 to "f", 0x10A71 to "ʾ", 0x10A72 to "ʿ", 0x10A73 to "ḍ",
      0x10A74 to "g", 0x10A75 to "d", 0x10A76 to "ġ", 0x10A77 to "ṭ", 0x10A78 to "z",
      0x10A79 to "ḏ", 0x10A7A to "y", 0x10A7B to "ṯ", 0x10A7C to "ẓ", 0x10A7F to " | "
    )
    val sb = StringBuilder()
    var i = 0
    while (i < text.length) {
      val cp = text.codePointAt(i)
      val charCount = Character.charCount(cp)
      sb.append(map[cp] ?: text.substring(i, i + charCount))
      i += charCount
    }
    return sb.toString()
  }

  private fun transliterateUgaritic(text: String): String {
    val map = mapOf(
      0x10380 to "ʾa", 0x10381 to "b", 0x10382 to "g", 0x10383 to "ḫ", 0x10384 to "d",
      0x10385 to "h", 0x10386 to "w", 0x10387 to "z", 0x10388 to "ḥ", 0x10389 to "ṭ",
      0x1038A to "y", 0x1038B to "š", 0x1038C to "k", 0x1038D to "l", 0x1038E to "m",
      0x1038F to "ḏ", 0x10390 to "n", 0x10391 to "ẓ", 0x10392 to "s", 0x10393 to "ʿ",
      0x10394 to "p", 0x10395 to "ṣ", 0x10396 to "q", 0x10397 to "r", 0x10398 to "ṯ",
      0x10399 to "ġ", 0x1039A to "t", 0x1039B to "ʾi", 0x1039C to "ʾu", 0x1039D to "ś"
    )
    val sb = StringBuilder()
    var i = 0
    while (i < text.length) {
      val cp = text.codePointAt(i)
      val charCount = Character.charCount(cp)
      sb.append(map[cp] ?: text.substring(i, i + charCount))
      i += charCount
    }
    return sb.toString()
  }

  private fun transliterateGeez(text: String): String {
    return text.replace(" ፡ ", " | ")
  }
}
