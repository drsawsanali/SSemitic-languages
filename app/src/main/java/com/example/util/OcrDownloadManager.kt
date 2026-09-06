package com.example.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class PackDownloadState {
  IDLE,
  DOWNLOADING,
  INSTALLED,
  PAUSED
}

data class OcrLanguagePack(
  val id: String,
  val nameAr: String,
  val nameEn: String,
  val scriptFamilyAr: String,
  val version: String,
  val sizeMb: Double,
  val totalGlyphs: Int,
  val sampleGlyphs: List<String>,
  val descriptionAr: String,
  val modules: List<String>,
  val accuracyScore: String,
  val downloadState: PackDownloadState = PackDownloadState.IDLE,
  val downloadProgress: Float = 0f,
  val downloadSpeed: String = "",
  val installedDate: String? = null,
  val targetScriptKey: String // Key to match OCR detected script
)

class OcrDownloadManager private constructor(context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("ocr_offline_packs_prefs", Context.MODE_PRIVATE)

  private val scope = CoroutineScope(Dispatchers.Main)
  private val activeJobs = mutableMapOf<String, Job>()

  private val _packsState = MutableStateFlow<List<OcrLanguagePack>>(emptyList())
  val packsState: StateFlow<List<OcrLanguagePack>> = _packsState.asStateFlow()

  private val _isOfflineModeForced = MutableStateFlow(
    prefs.getBoolean("pref_force_offline_mode", false)
  )
  val isOfflineModeForced: StateFlow<Boolean> = _isOfflineModeForced.asStateFlow()

  init {
    initializePacks()
  }

  private fun initializePacks() {
    val initialPacks = listOf(
      OcrLanguagePack(
        id = "pack_phoenician_canaanite",
        nameAr = "حزمة الأبجدية الفينيقية والكنعانية القديمة",
        nameEn = "Phoenician & Canaanite Epigraphic Pack",
        scriptFamilyAr = "السامية الشمالية الغربية (Northwest Semitic)",
        version = "v2.8.4",
        sizeMb = 24.5,
        totalGlyphs = 22,
        sampleGlyphs = listOf("𐤀", "𐤁", "𐤂", "𐤃", "𐤄", "𐤅", "𐤆", "𐤇", "𐤈", "𐤉", "𐤟"),
        descriptionAr = "نموذج تصنيف الرؤية الإبيغرافية للخطوط الكنعانية (جبيل، صور، صيدا، قرطاج البونية، موآب، وعمون). يشمل شبكة CNN مدربة على 8,500 نقش حجري ومعدني.",
        modules = listOf(
          "أوزان شبكة العصبونات التلافيفية للخط الفينيقي (CNN Weights)",
          "معجم الجذور الكنعانية المقارنة (14,200 مدخل)",
          "محرك القوانين الصوتية والتحول الكنعاني (Sound Shifts Engine)",
          "جداول التقطيع المقطعي والنقحرة اللاتينية الفيلولوجية"
        ),
        accuracyScore = "99.6%",
        targetScriptKey = "فينيقي"
      ),
      OcrLanguagePack(
        id = "pack_musnad_sayhadic",
        nameAr = "حزمة خط المسند والسامية الجنوبية القديمة",
        nameEn = "Musnad & Sayhadic Epigraphic Pack",
        scriptFamilyAr = "السامية الجنوبية القديمة (Sayhadic / South Arabian)",
        version = "v3.1.2",
        sizeMb = 31.2,
        totalGlyphs = 29,
        sampleGlyphs = listOf("𐩠", "𐩡", "𐩢", "𐩣", "𐩤", "𐩥", "𐩦", "𐩧", "𐩨", "𐩩", "𐩿"),
        descriptionAr = "حزمة استخلاص وتحليل نقوش المسند البارز والغائر بخطوط سبأ، معين، قتبان، وحضرموت، مع دعم كامل لعلامات الفصل النذرية (𐩿) والتراكيب النحوية السبئية.",
        modules = listOf(
          "نموذج فك تشفير الأخاديد الحجرية لمسند مأرب وصنعاء",
          "قاموس المفردات السبئية والمعينية المحققة (Biella & Beeston)",
          "محلل علامات التنوين ونون التعريف المتأخرة (-ن)",
          "محاكي الترددات الصوتية للأصوات الجانبية والمطبقة"
        ),
        accuracyScore = "99.4%",
        targetScriptKey = "مسند"
      ),
      OcrLanguagePack(
        id = "pack_akkadian_cuneiform",
        nameAr = "حزمة المسمارية الأكادية والبابلية والآشورية",
        nameEn = "Akkadian Cuneiform & Syllabary Pack",
        scriptFamilyAr = "السامية الشرقية (East Semitic)",
        version = "v4.0.1",
        sizeMb = 48.7,
        totalGlyphs = 650,
        sampleGlyphs = listOf("𒀭", "𒈗", "𒂗", "𒂍", "𒄿", "𒈾", "𒈠", "𒁴", "𒈪", "𒊭"),
        descriptionAr = "المحرك الأعمق لمعالجة الرُقم الطينية البابلية والآشورية والمسلات البازلتية، يشتمل على كاشف المقاطع الصوتية (Syllabary) والعلامات الدالة (Determinatives).",
        modules = listOf(
          "قاعدة بيانات الرموز المسمارية الشاملة (Labat / Borger)",
          "محرك تفكيك العلامات الدالة (Dingir, Lú, KI, KUR)",
          "قاموس شيكاغو الآشوري الكامل (CAD Offline Lexicon Subset)",
          "خوارزميات ضبط تباين الإضاءة المائلة لأخاديد الرُقم الطينية"
        ),
        accuracyScore = "98.9%",
        targetScriptKey = "مسماري"
      ),
      OcrLanguagePack(
        id = "pack_ugaritic_cuneiform",
        nameAr = "حزمة الأبجدية المسمارية الأوجاريتية",
        nameEn = "Ugaritic Alphabetic Cuneiform Pack",
        scriptFamilyAr = "السامية الشمالية الغربية (Northwest Semitic)",
        version = "v2.2.0",
        sizeMb = 18.4,
        totalGlyphs = 30,
        sampleGlyphs = listOf("𐎀", "𐎁", "𐎂", "𐎃", "𐎄", "𐎅", "𐎆", "𐎇", "𐎈", "𐎉"),
        descriptionAr = "مخصصة لرُقم رأس الشمرا الساحلية الأوجاريتية التي تمثل أقدم أبجدية مسمارية مدونة ذات ثلاثين حرفاً تشمل مصوتات الألف الثلاثة (ʾa, ʾi, ʾu).",
        modules = listOf(
          "نموذج التعرف على الأسافين الأبجدية الثلاثين",
          "معجم أوجاريت المقارن (Del Olmo Lete & Sanmartín)",
          "محلل نصوص ملاحم كرت ودانيال وألواح البعل",
          "جداول المقابلة السامية الفورية مع العربية والأكادية"
        ),
        accuracyScore = "99.5%",
        targetScriptKey = "أوجاريتي"
      ),
      OcrLanguagePack(
        id = "pack_ethiopic_geez",
        nameAr = "حزمة الخط الجعزي الإثيوبي (الفيدل)",
        nameEn = "Ethiopic & Ge'ez Fidel Epigraphic Pack",
        scriptFamilyAr = "السامية الإثيوبية (Ethiosemitic)",
        version = "v2.5.3",
        sizeMb = 26.8,
        totalGlyphs = 260,
        sampleGlyphs = listOf("ሀ", "ለ", "ሐ", "መ", "ሠ", "ረ", "ሰ", "ቀ", "በ", "ተ"),
        descriptionAr = "حزمة استقراء المخطوطات والمسلات الإثيوبية القديمة (مسلات عيزانا الملكية، مخطوطات أبا غاريما، ونقوش أكسوم الحجرية) بدقة عالية لطبقات الحركات السبع.",
        modules = listOf(
          "محلل الصوائت السبع الملحقة بالحروف الجعزية (Fidel Orders)",
          "معجم دلمن الجعزي الكلاسيكي (Dillmann Lexicon Core)",
          "محرك قراءة المسلات الأكسومية ثلاثية الخطوط",
          "نظام تصحيح اهتراء خطوط الرقوق الجلدية القديمة"
        ),
        accuracyScore = "99.3%",
        targetScriptKey = "جعزي"
      ),
      OcrLanguagePack(
        id = "pack_aramaic_nabataean",
        nameAr = "حزمة الخطوط الآرامية والنبطية والتدمرية",
        nameEn = "Imperial Aramaic & Nabataean Pack",
        scriptFamilyAr = "الفرع الآرامي (Aramaic & Nabataean)",
        version = "v2.6.1",
        sizeMb = 22.4,
        totalGlyphs = 24,
        sampleGlyphs = listOf("𐡀", "𐡁", "𐡂", "𐡃", "𐡄", "𐡅", "𐡆", "𐡇", "𐡈", "𐡉"),
        descriptionAr = "محرك متخصص لفك نقوش واجهات البتراء ومدائن صالح النبطية، وشواهد تدمر التذكارية، ووثائق برديات إلفنتين بالآرامية الرسمية.",
        modules = listOf(
          "كاشف التحولات الخطية من الآرامية الإمبراطورية إلى النبطية",
          "قاموس المفردات الآرامية والنبطية المشتركة (DNWSI)",
          "محلل الصيغ الملكية والنذرية لملوك الأنباط الحارث وعبادة",
          "جداول تطور أشكال الحروف النبطية نحو الخط العربي المبكر"
        ),
        accuracyScore = "99.1%",
        targetScriptKey = "آرامي"
      ),
      OcrLanguagePack(
        id = "pack_ancient_north_arabian",
        nameAr = "حزمة العربية الشمالية القديمة (الصفائية والثمودية واللحيانية)",
        nameEn = "Ancient North Arabian Rock Art Pack",
        scriptFamilyAr = "العربية الشمالية القديمة (ANA)",
        version = "v2.1.8",
        sizeMb = 19.6,
        totalGlyphs = 28,
        sampleGlyphs = listOf("𐪀", "𐪁", "𐪂", "𐪃", "𐪄", "𐪅", "𐪆", "𐪇", "𐪈", "𐪉"),
        descriptionAr = "مخصصة لنقوش البادية والصخور البركانية بحرة الشام ووادي رم ومواقع العلا والديدان، تتضمن خوارزميات لقراءة الخطوط المعكوسة والحلزونية.",
        modules = listOf(
          "كاشف اتجاهات الكتابة البوستروفيدونية والحلزونية الصخرية",
          "معجم النقوش الصفائية والثمودية (OCIANA Dataset Core)",
          "محلل أسماء الأعلام والقبائل والأنساب البدوية القديمة",
          "مرشح عزل الخدوش الجيولوجية الطبيعية عن الحفر الحقيقي"
        ),
        accuracyScore = "99.2%",
        targetScriptKey = "صفائي"
      ),
      OcrLanguagePack(
        id = "pack_syriac_mandaic",
        nameAr = "حزمة السريانية الكلاسيكية والمندائية",
        nameEn = "Classical Syriac & Mandaic Pack",
        scriptFamilyAr = "الآرامية الشرقية (Eastern Aramaic)",
        version = "v2.0.4",
        sizeMb = 21.0,
        totalGlyphs = 26,
        sampleGlyphs = listOf("ܐ", "ܒ", "ܓ", "ܕ", "ܗ", "ܘ", "ܙ", "ܚ", "ܛ", "ܝ"),
        descriptionAr = "حزمة قراءة المخطوطات السريانية بالخط الأسطرنجيلي والسرطا، ولفائف كنزا ربا المندائية بالخط المتصل العتيق.",
        modules = listOf(
          "محلل الخط السرياني الإسطرنجيلي المتصل والمنفصل",
          "معجم بروكلمان السرياني المقارن (Brockelmann)",
          "مفكك النقوش الليتورجية والمندائية والتعاويذ الطينية",
          "محاكي النطق الحلقي واللثوي الشرقي"
        ),
        accuracyScore = "99.0%",
        targetScriptKey = "سرياني"
      )
    )

    // Load persistent state from SharedPreferences
    val updatedList = initialPacks.map { pack ->
      val isInstalled = prefs.getBoolean("pack_installed_${pack.id}", pack.id == "pack_phoenician_canaanite" || pack.id == "pack_musnad_sayhadic")
      val date = prefs.getString("pack_installed_date_${pack.id}", if (isInstalled) "2026-08-15" else null)
      if (isInstalled) {
        pack.copy(
          downloadState = PackDownloadState.INSTALLED,
          downloadProgress = 1f,
          installedDate = date ?: "2026-08-15"
        )
      } else {
        pack
      }
    }

    _packsState.value = updatedList
  }

  fun downloadPack(packId: String) {
    val pack = _packsState.value.find { it.id == packId } ?: return
    if (pack.downloadState == PackDownloadState.DOWNLOADING || pack.downloadState == PackDownloadState.INSTALLED) return

    // Cancel existing job if any
    activeJobs[packId]?.cancel()

    activeJobs[packId] = scope.launch {
      // Set to downloading
      updatePack(packId) {
        it.copy(
          downloadState = PackDownloadState.DOWNLOADING,
          downloadProgress = 0.05f,
          downloadSpeed = "1.8 MB/s"
        )
      }

      val steps = 20
      for (step in 1..steps) {
        delay(120) // Fast and smooth simulation
        val progress = (step / steps.toFloat())
        val speed = when {
          step < 5 -> "2.4 MB/s"
          step < 15 -> "3.8 MB/s"
          else -> "4.2 MB/s"
        }
        updatePack(packId) {
          it.copy(
            downloadProgress = progress,
            downloadSpeed = speed
          )
        }
      }

      // Mark installed
      val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
      prefs.edit()
        .putBoolean("pack_installed_$packId", true)
        .putString("pack_installed_date_$packId", dateStr)
        .apply()

      updatePack(packId) {
        it.copy(
          downloadState = PackDownloadState.INSTALLED,
          downloadProgress = 1f,
          downloadSpeed = "",
          installedDate = dateStr
        )
      }
      activeJobs.remove(packId)
    }
  }

  fun cancelDownload(packId: String) {
    activeJobs[packId]?.cancel()
    activeJobs.remove(packId)
    updatePack(packId) {
      it.copy(
        downloadState = PackDownloadState.IDLE,
        downloadProgress = 0f,
        downloadSpeed = ""
      )
    }
  }

  fun deletePack(packId: String) {
    activeJobs[packId]?.cancel()
    activeJobs.remove(packId)

    prefs.edit()
      .remove("pack_installed_$packId")
      .remove("pack_installed_date_$packId")
      .apply()

    updatePack(packId) {
      it.copy(
        downloadState = PackDownloadState.IDLE,
        downloadProgress = 0f,
        downloadSpeed = "",
        installedDate = null
      )
    }
  }

  fun downloadAllPacks() {
    _packsState.value.forEach { pack ->
      if (pack.downloadState != PackDownloadState.INSTALLED && pack.downloadState != PackDownloadState.DOWNLOADING) {
        downloadPack(pack.id)
      }
    }
  }

  fun deleteAllPacks() {
    _packsState.value.forEach { pack ->
      deletePack(pack.id)
    }
  }

  fun toggleForceOfflineMode(forced: Boolean) {
    prefs.edit().putBoolean("pref_force_offline_mode", forced).apply()
    _isOfflineModeForced.value = forced
  }

  fun setForceOfflineMode(forced: Boolean) = toggleForceOfflineMode(forced)

  fun getPackForScript(scriptNameOrKey: String): OcrLanguagePack? {
    val packs = _packsState.value
    return packs.firstOrNull { pack ->
      scriptNameOrKey.contains(pack.targetScriptKey, ignoreCase = true) ||
        pack.nameAr.contains(scriptNameOrKey, ignoreCase = true) ||
        pack.scriptFamilyAr.contains(scriptNameOrKey, ignoreCase = true)
    }
  }

  fun isPackInstalledForScript(scriptNameOrKey: String): Boolean {
    val packs = _packsState.value
    return packs.any { pack ->
      pack.downloadState == PackDownloadState.INSTALLED &&
        (scriptNameOrKey.contains(pack.targetScriptKey, ignoreCase = true) ||
          pack.nameAr.contains(scriptNameOrKey, ignoreCase = true) ||
          pack.scriptFamilyAr.contains(scriptNameOrKey, ignoreCase = true))
    }
  }

  fun getInstalledPackForScript(scriptNameOrKey: String): OcrLanguagePack? {
    val packs = _packsState.value
    return packs.firstOrNull { pack ->
      pack.downloadState == PackDownloadState.INSTALLED &&
        (scriptNameOrKey.contains(pack.targetScriptKey, ignoreCase = true) ||
          pack.nameAr.contains(scriptNameOrKey, ignoreCase = true) ||
          pack.scriptFamilyAr.contains(scriptNameOrKey, ignoreCase = true))
    }
  }

  fun getInstalledPacksCount(): Int {
    return _packsState.value.count { it.downloadState == PackDownloadState.INSTALLED }
  }

  fun getTotalStorageUsedMb(): Double {
    return _packsState.value
      .filter { it.downloadState == PackDownloadState.INSTALLED }
      .sumOf { it.sizeMb }
  }

  fun getTotalAvailablePacksStorageMb(): Double {
    return _packsState.value.sumOf { it.sizeMb }
  }

  private fun updatePack(packId: String, transform: (OcrLanguagePack) -> OcrLanguagePack) {
    _packsState.value = _packsState.value.map {
      if (it.id == packId) transform(it) else it
    }
  }

  companion object {
    @Volatile
    private var instance: OcrDownloadManager? = null

    fun getInstance(context: Context): OcrDownloadManager {
      return instance ?: synchronized(this) {
        instance ?: OcrDownloadManager(context.applicationContext).also { instance = it }
      }
    }
  }
}
