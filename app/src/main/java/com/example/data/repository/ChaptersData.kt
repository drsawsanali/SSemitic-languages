package com.example.data.repository

import com.example.data.models.*

object ChaptersData {
  val list = listOf(
    // UNIT 1: Origins & Historical Geography (Chapters 1-10)
    AcademicChapter(
      id = "chap-01",
      chapterNumber = 1,
      unitNumber = 1,
      unitTitleAr = "الوحدة الأولى: النشأة، الجغرافيا التاريخية، الممالك والحواضر، والبانثيون الديني",
      unitTitleEn = "Unit I: Origins, Historical Geography, Kingdoms & Pantheon",
      titleAr = "الفصل 1: الموطن الأصلي للساميين وتصنيف فروع اللغات السامية المقارنة",
      titleEn = "Chapter 1: The Proto-Semitic Homeland & Comparative Branch Classification",
      languageBranch = SemiticBranch.NORTHWEST_SEMITIC,
      languageNameAr = "السامية المقارنة والسامية الأم",
      languageNameEn = "Comparative Semitic & Proto-Semitic",
      sections = listOf(
        ChapterSection(
          sectionId = "sec-1-1",
          headingAr = "إشكالية الموطن الأصلي (Urheimat) للغات السامية",
          headingEn = "The Urheimat Question of Semitic Languages",
          bodyTextAr = "تعددت النظريات الفيلولوجية والأثرية حول المهد الأول للشعوب واللغات السامية؛ وتتراوح الفرضيات الكبرى بين: شبه الجزيرة العربية بوصفها مركز الانتشار الرعوي، وبلاد الرافدين والهلال الخصيب كحواضر تدوين مبكرة، ومنطقة شرق المتوسط وبلاد الشام بحسب معطيات أوجاريت وإيبلا، وشمال شرق إفريقيا بالنظر إلى التنوع اللغوي الإثيوبي الكوشي.",
          bodyTextEn = "Scholarly debates on the Proto-Semitic homeland (Urheimat) center upon four principal hypotheses: the Arabian Peninsula as the pastoral dispersal reservoir, Mesopotamia and the Fertile Crescent, the Levant/Syria corridor evidenced by Ebla and Ugarit, and Northeast Africa via Afroasiatic phylogenetic depth.",
          transliterationSnippet = "*bayt- (بيت), *malk- (ملك), *šalām- (سلام), *yamīn- (يمين)",
          comparisonTableData = listOf(
            listOf("الفرع اللغوي", "أبرز اللغات", "الخط الكتابي", "الحقبة الزمنية"),
            listOf("السامية الشرقية", "الأكادية، البابلية، الآشورية، إيبلا", "مسماري مقطعي", "2500–500 ق.م"),
            listOf("السامية الشمالية الغربية", "الأوغاريتية، الفينيقية، العبرية، الآرامية", "أبجدي خطي ومسماري", "1400 ق.م–الآن"),
            listOf("العربية والشمالية القديمة", "العربية الفصحى، الصفائية، الثمودية", "أبجدي عربي ونحتي", "القرن 6 ق.م–الآن"),
            listOf("السامية الجنوبية القديمة", "السبئية، المعينية، القتبانية، الحضرمية", "المسند والزبور", "1000 ق.م–القرن 6 م"),
            listOf("السامية الإثيوبية", "الجعزية، الأمهرية، التجرينية", "الفيدل المقطعي", "القرن 4 م–الآن")
          ),
          footnotesAr = listOf(
            "Moscati, S. et al. (1980), An Introduction to the Comparative Grammar of the Semitic Languages, Harrassowitz.",
            "Lipiński, E. (2001), Semitic Languages: Outline of a Comparative Grammar, Peeters Publishers."
          )
        ),
        ChapterSection(
          sectionId = "sec-1-2",
          headingAr = "شجرة النسب الفيلولوجية وتوزيع الصوامت السامية الـ 29",
          headingEn = "Phylogenetic Tree & The 29 Proto-Semitic Consonants",
          bodyTextAr = "تتميز السامية الأم (Proto-Semitic) بنظام صوتي غني يتألف من 29 صامتاً، حافظت العربية الجنوبية (المسند) واللغات العربية الحديثة على العدد الأوفر منها، بينما طرأت تغيرات واندماجات في السامية الشمالية الغربية التي اختزلتها إلى 22 صامتاً في الأبجدية الفينيقية الكنعانية.",
          bodyTextEn = "Proto-Semitic possessed 29 phonemic consonants. Old South Arabian and Classical Arabic retained almost the full inventory, whereas Northwest Semitic merged several dentals and sibilants, stabilizing at 22 consonant phonemes in linear Phoenician.",
          transliterationSnippet = "Proto-Semitic: *ḏ, *ṯ, *ṱ, *ś, *ṣ́, *ġ, *ḫ, *ḥ, *ʿ",
          footnotesAr = listOf("Huehnergard, J. (2019), An Introduction to Ugaritic, Hendrickson Publishers.")
        )
      ),
      primaryCitations = listOf("KAI - Kanaanäische und Aramäische Inschriften", "DNWSI - Dictionary of North-West Semitic Inscriptions")
    ),

    AcademicChapter(
      id = "chap-02",
      chapterNumber = 2,
      unitNumber = 1,
      unitTitleAr = "الوحدة الأولى: النشأة، الجغرافيا التاريخية، الممالك والحواضر، والبانثيون الديني",
      unitTitleEn = "Unit I: Origins, Historical Geography, Kingdoms & Pantheon",
      titleAr = "الفصل 2: الجغرافيا السياسية وحواضر الشرق الأدنى القديم وشبه الجزيرة العربية",
      titleEn = "Chapter 2: Geopolitics & Major Urban Centers of Ancient Near East and Arabia",
      languageBranch = SemiticBranch.EAST_SEMITIC,
      languageNameAr = "الأكادية والسبئية والكنعانية",
      languageNameEn = "Akkadian, Sabaic & Canaanite Centers",
      sections = listOf(
        ChapterSection(
          sectionId = "sec-2-1",
          headingAr = "شبكة المدن والموانئ التجارية ومسارات القوافل الكبرى",
          headingEn = "Urban Networks, Maritime Ports & The Incense Caravan Route",
          bodyTextAr = "شكلت الحواضر السامية مراكز إشعاع حضاري كبرى: ففي بلاد الرافدين ازدهرت بابل ونينوى وأور وأكد كحواضر إمبراطورية؛ وعلى ساحل الشام شكلت جبيل وصور وصيدا وأوغاريت بوابات الملاحة البحرية الكنعانية الفينيقية نحو قرطاج؛ وفي جنوب الجزيرة العربية قادت مأرب وصرواح وشبوة وتمنة تجارة اللبان والبخور العالمية المرتبطة بواحات العلا وتيماء والبتراء وتدمر.",
          bodyTextEn = "Major Semitic metropolises functioned as imperial and commercial hubs: Babylon, Nineveh, and Ur in Mesopotamia; Byblos, Tyre, Sidon, and Ugarit as Mediterranean ports; Marib, Sirwah, and Shabwa in South Arabia anchoring the Frankincense route to Petra and Palmyra.",
          transliterationSnippet = "Mlk-m l-ḥmyt gbl / Mkrbt sbʾ w-ḥmyr",
          comparisonTableData = listOf(
            listOf("المدينة / الحاضرة", "الموقع الجغرافي", "اللغة السائدة", "أبرز الشواهد الأثرية"),
            listOf("بابل (Bābilu)", "وسط العراق", "الأكادية البابلية", "مسلة حمورابي، بوابة عشتار"),
            listOf("أوغاريت (Ugarit)", "الساحل السوري", "الأوغاريتية", "ألواح الملاحم ورسائل القصر الملكي"),
            listOf("جبيل (Gubla)", "ساحل لبنان", "الفينيقية", "تابوت أحيرام ونقوش ملوك بيبلوس"),
            listOf("مأرب (Maryab)", "اليمن (صرواح)", "السبئية", "سد مأرب العظيم، معبد أوام ومحرم بلقيس"),
            listOf("البتراء (Raqmu)", "جنوب الأردن", "النبطية الآرامية", "الخزنة، الدير، ونقوش المقابر الصخرية"),
            listOf("أكسوم (Aksum)", "المرتفعات الإثيوبية", "الجعزية والمسندية", "مسلات الملك عيزانا العملاقة")
          )
        )
      ),
      primaryCitations = listOf("CIS - Corpus Inscriptionum Semiticarum", "DASI - Digital Archive for the Study of pre-Islamic Arabian Inscriptions")
    ),

    AcademicChapter(
      id = "chap-03",
      chapterNumber = 3,
      unitNumber = 1,
      unitTitleAr = "الوحدة الأولى: النشأة، الجغرافيا التاريخية، الممالك والحواضر، والبانثيون الديني",
      unitTitleEn = "Unit I: Origins, Historical Geography, Kingdoms & Pantheon",
      titleAr = "الفصل 3: مجمع الآلهة (البانثيون) والطقوس الدينية في النصوص السامية",
      titleEn = "Chapter 3: The Semitic Pantheon & Ritual Liturgy in Epigraphic Texts",
      languageBranch = SemiticBranch.NORTHWEST_SEMITIC,
      languageNameAr = "مجمع الآلهة السامي المشترك",
      languageNameEn = "Pan-Semitic Pantheon",
      sections = listOf(
        ChapterSection(
          sectionId = "sec-3-1",
          headingAr = "الإله 'إيل' ومجمع الآلهة المقارن (بعل، عشتار/عثتر، إلمقه، وكموش)",
          headingEn = "El & The Divine Assembly: Baal, Ashtart/Attar, Almaqah & Chemosh",
          bodyTextAr = "تتقاطع المعتقدات السامية القديمة في تقديس الإله الأكبر 'إيل' (ʾIl / ʾĒl / Ilu) كرب للأرباب وأب للبشرية، وإلى جانبه إله الخصب والعواصف (بعل / هدد / أدد)، وآلهة الكواكب والجمال والحروب (عشتار / عشتروت / عثتر)، وآلهة الممالك القومية مثل 'إلمقه' في سبأ، و'كموش' في مؤاب، و'قوس' في أدوم، و'ملكوم' في عمون، و'ذو الشرى' في النبط.",
          bodyTextEn = "Semitic religious texts venerate ʾIl (El/Ilu) as the patriarch of the divine council, flanked by the storm/fertility deity Baal/Hadad, astral deities Ashtar/Ashtart, and national protectors: Almaqah (Saba), Chemosh (Moab), Qos (Edom), Milkom (Ammon), and Dushara (Nabataea).",
          transliterationSnippet = "ʾl d-pʾd (إيل ذو الشفقة) / bʿl ʿly (بعل العلي) / ʾlmqh ṯhb (إلمقه ثاوب)",
          comparisonTableData = listOf(
            listOf("الإله السامي", "المجال والرمز", "الفرع اللغوي", "الشاهد الإبيغرافي"),
            listOf("إيل (ʾĒl / Ilu)", "كبير الآلهة وخالق الخلائق", "سامي عام (أكادي، أوغاريتي، عربي)", "ألواح كرت ومسلات رأس الشمرا"),
            listOf("بعل / هدد (Baʿal)", "سيد الرعد والمطر والخصب", "شمالي غربي (كنعاني، آرامي)", "ملحمة بعل الأوغاريتية ومسلة ميشع"),
            listOf("عثتر / عشتار (ʿAṯtar)", "الزهرة والخصب والحرب", "سامي عام (مسند، أكادي، صفاة)", "نقش صرواح ونقوش حرة الشام"),
            listOf("إلمقه (ʾAlmaqah)", "حامي سبأ وإله الزراعة والقمر", "العربية الجنوبية القديمة", "نقوش محرم بلقيس وسد مأرب"),
            listOf("كموش (Kamōš)", "الإله القومي لمملكة مؤاب", "الكنعانية المؤابية", "مسلة ميشع بالسطر الثالث")
          )
        )
      ),
      primaryCitations = listOf("KTU - Die keilalphabetischen Texte aus Ugarit", "RES - Répertoire d'Épigraphie Sémitique")
    ),

    // UNIT 2: Phonology & Sound Shifts (Chapters 11-20)
    AcademicChapter(
      id = "chap-11",
      chapterNumber = 11,
      unitNumber = 2,
      unitTitleAr = "الوحدة الثانية: النظام الفونولوجي، الأصوات الحلقية، المطبقة والقذفية، والتحولات الصوتية",
      unitTitleEn = "Unit II: Phonology, Emphatics, Gutturals & Historical Sound Shifts",
      titleAr = "الفصل 11: النظام الصوتي للسامية الأم ومصفوفة الصوامت الـ 29",
      titleEn = "Chapter 11: The Proto-Semitic Phonological Matrix & 29 Consonants",
      languageBranch = SemiticBranch.NORTHWEST_SEMITIC,
      languageNameAr = "علم الأصوات السامي المقارن",
      languageNameEn = "Comparative Semitic Phonology",
      sections = listOf(
        ChapterSection(
          sectionId = "sec-11-1",
          headingAr = "الصوامت الحلقية، اللهوية، والمطبقة (Emphatic & Guttural Phonemes)",
          headingEn = "Guttural, Uvular, and Emphatic Phonemes",
          bodyTextAr = "تتميز العائلة السامية بمجموعة فريدة من الأصوات الحلقية (ʾ, h, ʿ, ḥ, ġ, ḫ) والأصوات المفخمة/المطبقة أو القذفية (ṭ, ṣ, q, ṱ, ṣ́). بينما تطورت في العربية إلى أصوات مطبقة ذات رنين بلعومي، حافظت اللغات الإثيوبية (كالجعزية والأمهرية) والسامية الجنوبية الحديثة (المهرية) على نطقها كصوامت قذفية حنجرية (Ejectives: /tʼ/, /t͡sʼ/, /kʼ/, /ɬʼ/).",
          bodyTextEn = "Semitic is renowned for gutturals (ʾ, h, ʿ, ḥ, ġ, ḫ) and emphatics (ṭ, ṣ, q, ṱ, ṣ́). While Central Semitic (Arabic) developed pharyngealized velarization, Ethiosemitic (Ge'ez) and Modern South Arabian (Mehri) preserve archaic glottalic ejectives (/tʼ/, /t͡sʼ/, /kʼ/).",
          transliterationSnippet = "IPA: /tʼ/, /t͡sʼ/, /kʼ/, /ɬʼ/, /ħ/, /ʕ/, /ɣ/, /x/",
          comparisonTableData = listOf(
            listOf("الصوت السامي الأم", "IPA", "الأكادية", "الأوغاريتية", "الفينيقية", "العبرية", "الآرامية", "العربية", "السبئية", "الجعزية"),
            listOf("*ḏ (ذ)", "/ð/", "z", "ḏ (𐎏)", "z (𐤆)", "z (ז)", "d (ד)", "ḏ (ذ)", "ḏ (𐩹)", "z (ዘ)"),
            listOf("*ṯ (ث)", "/θ/", "š", "ṯ (𐎘)", "š (𐤔)", "š (שׁ)", "t (ת)", "ṯ (ث)", "ṯ (𐩻)", "s (ሰ)"),
            listOf("*ṣ́ (ض)", "/ɬʼ/", "ṣ", "ṣ (𐎕)", "ṣ (𐤑)", "ṣ (צ)", "ʿ/q (ע/ק)", "ḍ (ض)", "ḍ (𐩳)", "ṣ́ (ፀ)"),
            listOf("*ṱ (ظ)", "/θʼ/", "ṣ", "ẓ (𐎑)", "ṣ (𐤑)", "ṣ (צ)", "ṭ (ט)", "ẓ (ظ)", "ẓ (𐩼)", "ṣ (ጸ)"),
            listOf("*ġ (غ)", "/ɣ/", "—", "ġ (𐎙)", "ʿ (𐤏)", "ʿ (ע)", "ʿ (ע)", "ġ (غ)", "ġ (𐩶)", "—"),
            listOf("*ḫ (خ)", "/x/", "ḫ", "ḫ (𐎃)", "ḥ (𐤇)", "ḥ (ח)", "ḥ (ח)", "ḫ (خ)", "ḫ (𐩭)", "ḫ (ኀ)")
          )
        )
      ),
      primaryCitations = listOf("Brockelmann, C., Grundriss der vergleichenden Grammatik der semitischen Sprachen")
    ),

    AcademicChapter(
      id = "chap-12",
      chapterNumber = 12,
      unitNumber = 2,
      unitTitleAr = "الوحدة الثانية: النظام الفونولوجي، الأصوات الحلقية، المطبقة والقذفية، والتحولات الصوتية",
      unitTitleEn = "Unit II: Phonology, Emphatics, Gutturals & Historical Sound Shifts",
      titleAr = "الفصل 12: التحول الصوتي الكنعاني (*ā > ō) وقانون بجد كفت (Begadkefat)",
      titleEn = "Chapter 12: The Canaanite Vowel Shift (*ā > ō) & Begadkefat Spirantization",
      languageBranch = SemiticBranch.NORTHWEST_SEMITIC,
      languageNameAr = "الكنعانية والفينيقية والعبرية والآرامية",
      languageNameEn = "Canaanite, Phoenician, Hebrew & Aramaic",
      sections = listOf(
        ChapterSection(
          sectionId = "sec-12-1",
          headingAr = "قانون التحول الكنعاني (*ā > ō) وانكماش المزدوجات الصوتية",
          headingEn = "The Canaanite Vowel Shift (*ā > ō) & Diphthong Monophthongization",
          bodyTextAr = "يمثل 'التحول الكنعاني' (Canaanite Shift) المائز الفونولوجي الأبرز للغات الكنعانية (الفينيقية، البونية، العبرية، المؤابية)؛ حيث تحولت الألف الممدودة المشبعة (*ā) إلى واو مضمومة (*ō)؛ مثل: *šalām- > šālōm / šulūm، و*raʾš- > rōš / rūš، و*ʿālam- > ʿōlām. كما انكمشت المزدوجات الصوتية: (*ay > ē) مثل *bayt- > bēt، و(*aw > ō) مثل *yawm- > yōm.",
          bodyTextEn = "The Canaanite Vowel Shift (*ā > ō) is the primary isogloss separating Canaanite (Phoenician, Punic, Hebrew, Moabite) from Ugaritic and Aramaic. Long stressed *ā became *ō (e.g. *šalām- > šālōm). Concurrently, diphthongs monophthongized (*ay > ē in bēt; *aw > ō in yōm).",
          transliterationSnippet = "*šalām- → šālōm (Hebrew) / šulūm (Phoenician) / slāmā (Aramaic) / salām (Arabic)",
          comparisonTableData = listOf(
            listOf("الجذر السامي الأم", "السامية الأم", "الأوغاريتية", "الفينيقية", "العبرية", "الآرامية", "العربية", "السبئية"),
            listOf("*š-l-m (سلام)", "*šalām-", "šlm (šalāmu)", "šlm (šulūm)", "šālōm", "šəlāmā", "salām", "slm (salām)"),
            listOf("*r-ʾ-š (رأس)", "*raʾš-", "riʾšu", "rōš / rīš", "rōʾš", "rēšā", "raʾs", "rʾs (raʾs)"),
            listOf("*b-y-t (بيت)", "*bayt-", "bt (bêtu)", "bt (bēt)", "bayit / bēt", "baytā / bētā", "bayt", "byt (bayt)"),
            listOf("*y-w-m (يوم)", "*yawm-", "ym (yômu)", "ym (yōm)", "yōm", "yōmā", "yawm", "ywm (yawm)")
          )
        )
      ),
      primaryCitations = listOf("Garr, W. R. (2004), Dialect Geography of Syria-Palestine, 1000-586 B.C.E., Eisenbrauns.")
    ),

    // UNIT 3: Morphology & Verb Stems (Chapters 21-30)
    AcademicChapter(
      id = "chap-21",
      chapterNumber = 21,
      unitNumber = 3,
      unitTitleAr = "الوحدة الثالثة: الصرف، المورفولوجيا، تصريف الأفعال، الأعداد، واشتقاق الأسماء",
      unitTitleEn = "Unit III: Morphology, Root & Pattern, Verb Stems (Binyanim) & Numerals",
      titleAr = "الفصل 21: منظومة الجذوع والأوزان الفعلية الكبرى (G, D, C, N, t-stems)",
      titleEn = "Chapter 21: Semitic Verbal System: Major Stems (G, D, C/Š/H, N & t-infixes)",
      languageBranch = SemiticBranch.NORTHWEST_SEMITIC,
      languageNameAr = "الصرف السامي المقارن",
      languageNameEn = "Comparative Semitic Morphology",
      sections = listOf(
        ChapterSection(
          sectionId = "sec-21-1",
          headingAr = "أوزان الأفعال السامية الكبرى وتفرعاتها المعنوية",
          headingEn = "The Five Major Stem Classes and Semantic Functions",
          bodyTextAr = "تعتمد اللغات السامية نظام الجذر والوزن (Root & Pattern). وتتفرع الأفعال إلى خمسة جذوع رئيسية: 1. الجذع المجرد (G - Grundstamm / فَعَلَ)، 2. الجذع المكثف بتضعيف العين (D - Doppelungsstamm / فَعَّلَ)، 3. الجذع السببي التعدوي (C - Causative: بالسين Š في الأكادية، والهاء H في العبرية والسبئية، والهمزة ʾ/Y في الفينيقية والعربية والسريانية)، 4. الجذع المطاوع بالنون (N - Niphʿal / انْفَعَلَ)، 5. الجذوع المطاوعة بتاء الافتعال (t-stems / افْتَعَلَ وتَفَعَّلَ).",
          bodyTextEn = "Semitic verbal morphology operates on root consonantal templates. The five primary stems are: G (Simple), D (Factitive/Intensive with doubled medial consonant), C (Causative with Š- in Akkadian, H- in Hebrew/Sabaic, ʾ-/Y- in Arabic/Phoenician/Syriac), N (Passive/Reflexive with prefixed n-), and t-stems (Reciprocal/Reflexive with infixed or prefixed t).",
          transliterationSnippet = "G: *qatal- / D: *qattil- / C: *šaqtil-, *haqtil-, *ʾaqtil- / N: *naqtal-",
          comparisonTableData = listOf(
            listOf("الجذع", "الوظيفة الصرفية", "الأكادية", "الأوغاريتية", "الفينيقية", "العبرية", "الآرامية", "العربية", "السبئية", "الجعزية"),
            listOf("G (المجرد)", "الفعل البسيط", "iprus (G)", "qatala", "paʿal (𐤐𐤏𐤋)", "Qal (קָטַל)", "Pəʿal (פְּעַל)", "فَعَلَ (faʿala)", "fʿl (𐩰𐩲𐩡)", "qatala (ቀተለ)"),
            listOf("D (المكثف)", "التعدية والتكثير", "uparris (D)", "qattala", "piʿʿel (𐤐𐤏𐤋)", "Piʿʿēl (קִטֵּל)", "Paʿʿēl (פַּעֵל)", "فَعَّلَ (faʿʿala)", "fʿl (مشدد)", "qattala (ቀተለ)"),
            listOf("C (السببي)", "الإنشاء والسببية", "uśapris (Š)", "šaqtila", "yiphʿil (𐤉𐤐𐤏𐤋)", "Hiphʿīl (הִקְטִיל)", "ʾAphʿēl/Haphʿēl", "أَفْعَلَ (ʾafʿala)", "hafʿala (𐩠𐩰𐩲𐩡)", "ʾaqtala (አቀተለ)"),
            listOf("N (المطاوع)", "الانفعال والمطاوعة", "ipparis (N)", "naqtala", "niphʿal (𐤍𐤐𐤏𐤋)", "Niphʿāl (נִקְטַל)", "— (استعيض عنه)", "انْفَعَلَ (infaʿala)", "nfʿl (𐩬𐩰𐩲𐩡)", "—"),
            listOf("t-stems", "المطاوعة والمشاركة", "iptaras (Gt)", "yithpaʿal", "yithpaʿʿal (𐤉𐤕𐤐𐤏𐤋)", "Hithpaʿʿēl (הִתְקַטֵּל)", "Ithpəʿēl (אֶתְפְּעֵל)", "تَفَعَّلَ / افْتَعَلَ", "tfʿl / ftʿl", "taqatala (ተቀተለ)")
          )
        )
      ),
      primaryCitations = listOf("Huehnergard, J. & Pat-El, N. (2019), The Semitic Languages (2nd ed.), Routledge.")
    ),

    // UNIT 4: Syntax & Poetic Metrics (Chapters 31-40)
    AcademicChapter(
      id = "chap-31",
      chapterNumber = 31,
      unitNumber = 4,
      unitTitleAr = "الوحدة الرابعة: النحو، الإعراب، بناء الجملة، التراكيب، والأسلوبية الشعرية",
      unitTitleEn = "Unit IV: Syntax, Case Endings, Sentence Typology & Poetic Metrics",
      titleAr = "الفصل 31: نظام الإعراب السامي بالحركات الثلاث والتنوين والميمية (Mimation & Nunation)",
      titleEn = "Chapter 31: The Semitic Triptotic Case System, Mimation & Nunation",
      languageBranch = SemiticBranch.NORTHWEST_SEMITIC,
      languageNameAr = "النحو السامي والإعراب",
      languageNameEn = "Semitic Case Syntax & Inflexion",
      sections = listOf(
        ChapterSection(
          sectionId = "sec-31-1",
          headingAr = "حالات الإعراب الثلاث: الرفع (-u)، النصب (-a)، والجر (-i)",
          headingEn = "The Three Functional Cases: Nominative (-u), Accusative (-a), Genitive (-i)",
          bodyTextAr = "تشاركت اللغات السامية القديمة نظام إعراب ثلاثي الحركات: 1. الرفع بالضمة (-u) للفاعل والمبتدأ، 2. النصب بالفتحة (-a) للمفعول به والحال، 3. الجر بالكسرة (-i) بعد حروف الجر والمضاف إليه. ويُلحق بالاسم في حالة التنكير تنوين ميمي في الأكادية والسبئية المبكرة (-um, -am, -im)، أو تنوين نوني في العربية والسبئية الكلاسيكية والجعزية (-un, -an, -in).",
          bodyTextEn = "Proto-Semitic operated on a triptotic declension: Nominative in -u, Accusative in -a, and Genitive in -i. Indefinite nouns took mimation (-um, -am, -im) in Old Akkadian and Early Sabaic, or nunation (-un, -an, -in) in Classical Arabic and later Sayhadic.",
          transliterationSnippet = "Nom: *malk-u-m / Acc: *malk-a-m / Gen: *malk-i-m",
          comparisonTableData = listOf(
            listOf("حالة الإعراب", "السامية الأم", "الأكادية", "الأوغاريتية", "العربية الفصحى", "السبئية (المسند)", "الجعزية"),
            listOf("الرفع (Nominative)", "*malk-u-m", "šarr-um", "mal-ku", "مَلِكٌ (malik-un)", "𐩣𐩡𐩫𐩬 (mlk-n)", "nəguś (نهاية صفرية)"),
            listOf("النصب (Accusative)", "*malk-a-m", "šarr-am", "mal-ka", "مَلِكاً (malik-an)", "𐩣𐩡𐩫𐩬 (mlk-n)", "nəguś-a (حركة نصب عامة)"),
            listOf("الجر (Genitive)", "*malk-i-m", "šarr-im", "mal-ki", "مَلِكٍ (malik-in)", "𐩣𐩡𐩫𐩬 (mlk-n)", "nəguś")
          )
        )
      ),
      primaryCitations = listOf("Versteegh, K. (2014), The Arabic Language (2nd ed.), Edinburgh University Press.")
    ),

    // UNIT 5: Epigraphy, Decipherment & Dictionaries (Chapters 41-50)
    AcademicChapter(
      id = "chap-41",
      chapterNumber = 41,
      unitNumber = 5,
      unitTitleAr = "الوحدة الخامسة: الإبيغرافيا، مواد التدوين، النصوص الملكية والملحمية، وفك الرموز والمعاجم",
      unitTitleEn = "Unit V: Epigraphy, Paleography, Inscriptions Decipherment & Lexica",
      titleAr = "الفصل 41: تاريخ فك رموز الخطوط السامية (المسماري، الفينيقي، المسند، والجعزي)",
      titleEn = "Chapter 41: History of Semitic Epigraphic Decipherment & Paleographic Methods",
      languageBranch = SemiticBranch.NORTHWEST_SEMITIC,
      languageNameAr = "الإبيغرافيا وفك الرموز",
      languageNameEn = "Epigraphy & Decipherment History",
      sections = listOf(
        ChapterSection(
          sectionId = "sec-41-1",
          headingAr = "محطات فك رموز الخطوط القديمة من بارتيليمي وغروتفند إلى هلبرخت وإدوارد غلازر",
          headingEn = "Milestones of Decipherment: Barthélemy, Grotefend, Rawlinson, Glaser & Halévy",
          bodyTextAr = "بدأ فك رموز الأبجدية الفينيقية على يد الأب جان جاك بارتيليمي عام 1758 عبر نصوص مالطا ثنائية اللغة؛ وتلاه فك رموز المسماري الأكادي في بيستون بواسطة رولنسون وهينكس؛ وفك خط المسند الحميري والسبئي عبر رحلات إدوارد غلازر وجوزيف هاليفي في اليمن؛ وصولاً إلى اكتشاف رُقم أوغاريت في رأس الشمرا عام 1929 وفك أبجديتها المسمارية على يد شارل فيرولو وهانز باور ودورم.",
          bodyTextEn = "Decipherment breakthroughs began with Abbé Barthélemy solving Phoenician in 1758 via Maltese bilingual cippi; Rawlinson and Hincks deciphering Akkadian cuneiform from the Behistun Rock; Glaser and Halévy documenting thousands of South Arabian Musnad inscriptions in Yemen; and Virolleaud, Bauer, and Dhorme deciphering the Ugaritic 30-letter cuneiform in 1929 within months of discovery.",
          transliterationSnippet = "Bilingual Cippi: 'L-ʾdnn l-mlqrt bʿl ṣr' / 'Dionysio kai Serapioni Tyriois'",
          comparisonTableData = listOf(
            listOf("نظام الخط", "العالم المفكك", "تاريخ الإنجاز", "الشاهد التأسيسي"),
            listOf("الأبجدية الفينيقية", "جان جاك بارتيليمي (Barthélemy)", "1758 م", "قنديل مالطا ثنائي اللغة (فينيقي/يوناني)"),
            listOf("المسماري الأكادي", "هنري رولنسون، إدوارد هينكس", "1851–1857 م", "نقش بيستون ثلاثي اللغات ونقوش نينوى"),
            listOf("المسند العربي الجنوبي", "إميل رويجر، فيلهلم غيزينيوس", "1841 م", "نقوش حصن الغراب وبلاغات رحالة اليمن"),
            listOf("المسماري الأوغاريتي", "هانز باور، إدوار دورم، شارل فيرولو", "1929–1930 م", "رُقيمات رأس الشمرا (سلسلة RS)")
          )
        )
      ),
      primaryCitations = listOf("Daniels, P. T. & Bright, W. (1996), The World's Writing Systems, Oxford University Press.")
    )
  )
}
