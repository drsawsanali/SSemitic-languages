package com.example.data.repository

import com.example.data.models.*

object LexiconAndPhoneticsData {
  val roots = listOf(
    ComparativeLexiconRoot(
      root = "*m-l-k (ملك)",
      protoMeaningAr = "المُلْك والسيادة وتولي الأمر والسلطة",
      protoMeaningEn = "To possess sovereignty, rule, royal domain",
      akkadian = "šarru / malku (أمير/مشير)",
      ugaritic = "𐎎𐎍𐎋 (malku / mlk)",
      phoenician = "𐤌𐤋𐤊 (milk / malk)",
      hebrew = "מֶלֶךְ (mĕlĕḵ)",
      aramaic = "מַלְכָּא (malkā)",
      arabic = "مَلِك (malik) / مَلَكَ (malaka)",
      sabaic = "𐩣𐩡𐩫 (mlk)",
      geez = "መልአክ (mal'ak / رئيس) / ንጉሥ (nəguś)",
      phoneticEvolutionIPA = "Proto *malk-u- > Ugaritic /malku/ > Phoenician /molk/ > Hebrew /mɛlɛx/ > Arabic /malik/",
      soundShiftLawAr = "التحول الصوتي الكنعاني فتح العين إلى واو في الفينيقية ثم سيغلة الوزن في العبرية (malk > mɛlɛx).",
      soundShiftLawEn = "Canaanite vowel shift from *a to *o in Phoenician, with subsequent segolation in Tiberian Hebrew."
    ),
    ComparativeLexiconRoot(
      root = "*b-y-t (بيت)",
      protoMeaningAr = "المأوى والمسكن والبيت والمكان والأسرة",
      protoMeaningEn = "House, dwelling, family dynasty, temple",
      akkadian = "bītu (𒂍)",
      ugaritic = "𐎁𐎚 (bt / bêtu)",
      phoenician = "𐤁𐤕 (bēt / bt)",
      hebrew = "בַּיִת (bayiṯ) / בֵּית (bêṯ)",
      aramaic = "בֵּיתָא (baytā / bêtā)",
      arabic = "بَيْت (bayt)",
      sabaic = "𐩨𐩺𐩩 (byt)",
      geez = "ቤት (bēt)",
      phoneticEvolutionIPA = "Proto *bayt-u- > Akkadian /biːtu/ > Phoenician /beːt/ > Arabic /bayt/ > Ge'ez /beːt/",
      soundShiftLawAr = "انكماش المزدوج الصوتي (*ay > ē) في الفينيقية والآرامية والجعزية، وبقاؤه مفتوحاً في العربية والسبئية.",
      soundShiftLawEn = "Monophthongization of diphthong *ay to /eː/ in Northwest Semitic and Ethiosemitic, preserved in Arabic and Epigraphic South Arabian."
    ),
    ComparativeLexiconRoot(
      root = "*š-l-m (سلم)",
      protoMeaningAr = "السلامة والكمال والأمن والتحية والصلح",
      protoMeaningEn = "Wholeness, peace, security, reconciliation",
      akkadian = "šalāmu (šulmu)",
      ugaritic = "𐎌𐎍𐎎 (šalāmu)",
      phoenician = "𐤔𐤋𐤌 (šulūm / šalom)",
      hebrew = "שָׁלוֹם (šālôm)",
      aramaic = "שְׁלָמָא (šəlāmā)",
      arabic = "سَلام (salām) / سَلِمَ (salima)",
      sabaic = "𐩪𐩡𐩣 (slm / salām)",
      geez = "ሰላም (salām)",
      phoneticEvolutionIPA = "Proto *šalām- > Hebrew /ʃaːloːm/ > Phoenician /ʃuluːm/ > Arabic /salaːm/",
      soundShiftLawAr = "التحول الكنعاني النموذجي (*ā > ō) في العبرية والفينيقية، وتحول الشين السامية إلى سين في العربية والمسند.",
      soundShiftLawEn = "Classic Canaanite shift *ā > ō in Hebrew/Phoenician; Proto-Semitic *š realized as /s/ in Central Semitic."
    ),
    ComparativeLexiconRoot(
      root = "*r-ʾ-š (رأس)",
      protoMeaningAr = "الرأس والقمة والأول والزعيم والبداية",
      protoMeaningEn = "Head, top, beginning, summit, leader",
      akkadian = "rēšu (SAG)",
      ugaritic = "𐎗𐎀𐎌 (riʾšu / rīšu)",
      phoenician = "𐤓𐤀𐤔 (rōš / rīš)",
      hebrew = "רֹאשׁ (rōʾš)",
      aramaic = "רֵישָׁא (rēšā)",
      arabic = "رَأْس (raʾs)",
      sabaic = "𐩧𐩱𐩪 (rʾs)",
      geez = "ርእስ (rəʾs)",
      phoneticEvolutionIPA = "Proto *raʾš- > Akkadian /reːʃu/ > Hebrew /roːʃ/ > Aramaic /reːʃaː/ > Arabic /raʔs/",
      soundShiftLawAr = "تسهيل الهمزة والتحول الكنعاني (*aʾ > ō) في العبرية، وانقلابها إلى كسرة ممدودة (*ē) في الأكادية والآرامية.",
      soundShiftLawEn = "Glottal stop assimilation with vowel elongation producing /oː/ in Canaanite and /eː/ in Aramaic/Akkadian."
    ),
    ComparativeLexiconRoot(
      root = "*ʾ-l-h / *ʾ-l (إيل/إله)",
      protoMeaningAr = "الإله والقوة والمطلق والألوهية",
      protoMeaningEn = "God, deity, divinity, divine power",
      akkadian = "ilu (𒀭 DINGIR)",
      ugaritic = "𐎛𐎍 (ʾil) / 𐎛𐎍𐎅 (ʾlh)",
      phoenician = "𐤀𐤋 (ʾl) / 𐤀𐤋𐤍𐤌 (ʾlnm)",
      hebrew = "אֵל (ʾēl) / אֱלוֹהַּ (ʾĕlôah)",
      aramaic = "אֱלָהָא (ʾĕlāhā)",
      arabic = "إِلٰه (ʾilāh) / الله (Allāh)",
      sabaic = "𐩱𐩡 (ʾl) / 𐩱𐩡𐩠𐩬 (ʾlhn)",
      geez = "እግዚአብሔር / አምላክ (ʾamlāk)",
      phoneticEvolutionIPA = "Proto *ʾil- / *ʾilah- > Akkadian /ilu/ > Ugaritic /ʔilu/ > Aramaic /ʔɛlaːhaː/ > Arabic /ʔilaːh/",
      soundShiftLawAr = "تطور صيغة الجمع السامية المشتركة (إيلوهيم / إيلانم / إلهن بالمسند).",
      soundShiftLawEn = "Universal Semitic root for the supreme celestial creator and divinity across all branches."
    ),
    ComparativeLexiconRoot(
      root = "*k-t-b (كتب)",
      protoMeaningAr = "الضم والجمع ثم الخط والتدوين والتسجيل",
      protoMeaningEn = "To tie together, inscribe, write, record",
      akkadian = "šaṭāru (سَطَرَ) / kasāpu",
      ugaritic = "𐎋𐎚𐎁 (ktb)",
      phoenician = "𐤊𐤕𐤁 (ktb)",
      hebrew = "כָּתַב (kāṯaḇ)",
      aramaic = "כְּתַב (kəṯaḇ)",
      arabic = "كَتَبَ (kataba) / كِتَاب",
      sabaic = "𐩫𐩩𐩨 (ktb - حرر عقداً)",
      geez = "መጽሐፍ (maṣḥaf) / ጸሐፈ (ṣaḥafa)",
      phoneticEvolutionIPA = "Proto *katab- > Hebrew /kaːθav/ > Aramaic /kəθav/ > Arabic /kataba/",
      soundShiftLawAr = "إعمال قانون 'بجد كفت' في العبرية والآرامية فتنطق التاء والباء كصوامت احتكاكية (/θ/ و /v/) بعد الصوائت.",
      soundShiftLawEn = "Spirantization via Begadkefat in Northwest Semitic turns stops [t], [b] into fricatives [θ], [v] post-vocalically."
    )
  )

  val phonemes = listOf(
    PhonemeItem(
      ipa = "/ʔ/",
      arabicLetter = "أ (همزة)",
      ancientGlyph = "𐤀 / 𐎀 / 𐩱",
      categoryAr = "حنجري وقفي مجهور",
      categoryEn = "Glottal Stop",
      articulationAr = "أقصى الحلق عند انطباق الوترين الصوتيين",
      articulationEn = "Complete glottal closure",
      f1Hz = 300,
      f2Hz = 1500,
      exampleWord = "*ʾalpu- (ثور / ألف)",
      soundLawNoteAr = "حُفظت في الأوغاريتية بثلاثة أشكال (a, i, u) وسقطت أو سهلت في بعض السياقات الأكادية والعبرية."
    ),
    PhonemeItem(
      ipa = "/ʕ/",
      arabicLetter = "ع (عين)",
      ancientGlyph = "𐤏 / 𐎓 / 𐩲",
      categoryAr = "حلقي احتكاكي مجهور",
      categoryEn = "Voiced Pharyngeal Fricative",
      articulationAr = "وسط الحلق بانقباض عضلات البلعوم",
      articulationEn = "Pharyngeal constriction",
      f1Hz = 650,
      f2Hz = 1200,
      exampleWord = "*ʿaynu- (عين / نبع)",
      soundLawNoteAr = "اندثرت في الأكادية مسببة تحول الحركات (a > e)، وحُفظت بكامل قوتها في العربية والمسند والجعزية."
    ),
    PhonemeItem(
      ipa = "/ħ/",
      arabicLetter = "ح (حاء)",
      ancientGlyph = "𐤇 / 𐎈 / 𐩢",
      categoryAr = "حلقي احتكاكي مهموس",
      categoryEn = "Voiceless Pharyngeal Fricative",
      articulationAr = "وسط الحلق بدون اهتزاز الأوتار الصوتية",
      articulationEn = "Voiceless pharyngeal friction",
      f1Hz = 600,
      f2Hz = 1350,
      exampleWord = "*ḥayy- (حي / حياة)",
      soundLawNoteAr = "اندمجت مع الخاء (*ḫ) في الفينيقية والعبرية في رمز واحد (ח / 𐤇)."
    ),
    PhonemeItem(
      ipa = "/tʼ/",
      arabicLetter = "ط (طاء)",
      ancientGlyph = "𐤈 / 𐎉 / 𐩷",
      categoryAr = "أسناني مطبق / قذفي",
      categoryEn = "Alveolar Ejective / Emphatic Stop",
      articulationAr = "طرف اللسان مع أصول الثنايا العليا مع التفخيم والإطباق أو القذف الحنجري",
      articulationEn = "Dental ejective / pharyngealized stop",
      f1Hz = 400,
      f2Hz = 2100,
      exampleWord = "*ṭābu- (طيّب / طاب)",
      soundLawNoteAr = "تنطق قذفية حنجرية (/tʼ/) في الجعزية والمهرية، ومطبقة بلعومية (/tˤ/) في العربية الفصحى."
    ),
    PhonemeItem(
      ipa = "/t͡sʼ/ or /sˤ/",
      arabicLetter = "ص (صاد)",
      ancientGlyph = "𐤑 / 𐎕 / 𐩮",
      categoryAr = "لثوي صفيري مطبق / قذفي",
      categoryEn = "Alveolar Ejective Affricate / Sibilant",
      articulationAr = "طرف اللسان بين الثنايا العليا والسفلى مع انطباق وتفخيم",
      articulationEn = "Glottalized alveolar affricate",
      f1Hz = 350,
      f2Hz = 2300,
      exampleWord = "*ṣalmu- (صنم / تمثال / صورة)",
      soundLawNoteAr = "كانت في السامية الأم صامتة مزجية قذفية (/t͡sʼ/) وتطورت إلى صفيري مطبق في العربية."
    ),
    PhonemeItem(
      ipa = "/ɬʼ/ or /dˤ/",
      arabicLetter = "ض (ضاد)",
      ancientGlyph = "𐩳 / 𐎕 / ፀ",
      categoryAr = "جانبي احتكاكي قذفي / مطبق",
      categoryEn = "Lateralized Emphatic Fricative",
      articulationAr = "حافة اللسان مع الأضراس العليا (الضاد الجانبية العربية القديمة)",
      articulationEn = "Lateral fricative ejective",
      f1Hz = 450,
      f2Hz = 1600,
      exampleWord = "*ʾarṣ́- (أرض)",
      soundLawNoteAr = "الصوت السامي الشهير المتميز برنينه الجانبي؛ حافظت عليه المهرية والشحرية والجعزية (ፀ)."
    )
  )

  val chronology = listOf(
    ChronologyEvent(
      id = "chrono-1",
      branch = "East Semitic",
      year = "c. 2400 BCE",
      titleAr = "أرشيف القصر الملكي في إيبلا وتأسيس الإمبراطورية الأكدية",
      titleEn = "Royal Archives of Ebla & Sargon's Akkadian Empire",
      descriptionAr = "ازدهار رُقم إيبلا الإدارية وتوحيد سرجون الأكدي لحواضر سومر وأكد في أول إمبراطورية سامية مدونة.",
      descriptionEn = "Discovery of 20,000 cuneiform tablets in Ebla Palace G, alongside Sargon of Akkad establishing the first documented Semitic empire."
    ),
    ChronologyEvent(
      id = "chrono-2",
      branch = "East Semitic",
      year = "c. 1750 BCE",
      titleAr = "تدوين شريعة حمورابي في بابل",
      titleEn = "Promulgation of the Law Code of Hammurabi",
      descriptionAr = "نقش 282 مادة قانونية وتشريعية بأرقى صياغة للأكادية البابلية القديمة على مسلة البازلت الأسود.",
      descriptionEn = "Engraving of 282 judicial laws in Classical Old Babylonian cuneiform on the monumental black basalt stele."
    ),
    ChronologyEvent(
      id = "chrono-3",
      branch = "Northwest Semitic",
      year = "c. 1350–1200 BCE",
      titleAr = "ازدهار الأبجدية المسمارية الأوغاريتية وملاحم رأس الشمرا",
      titleEn = "Ugaritic Cuneiform Alphabet & Ras Shamra Epics",
      descriptionAr = "ابتكار أول أبجدية مسمارية صامتة وتدوين ملاحم بعل وكرت والرسائل الدبلوماسية للساحل الشامي.",
      descriptionEn = "Invention of the 30-letter cuneiform alphabet in Ugarit and composition of the Epic of Baal and Kirta."
    ),
    ChronologyEvent(
      id = "chrono-4",
      branch = "Northwest Semitic",
      year = "c. 1000 BCE",
      titleAr = "انتشار الأبجدية الفينيقية الخطية (تابوت أحيرام بجبيل)",
      titleEn = "Spread of the Linear Phoenician 22-Letter Alphabet",
      descriptionAr = "استقرار حروف الأبجدية الفينيقية الـ 22 التي نقلها بحارة جبيل وصور إلى بلاد اليونان وحوض المتوسط.",
      descriptionEn = "Standardization of the 22-letter linear Phoenician alphabet on Ahiram's sarcophagus, disseminating across the Mediterranean."
    ),
    ChronologyEvent(
      id = "chrono-5",
      branch = "Old South Arabian",
      year = "c. 750–650 BCE",
      titleAr = "عصر مكاربة سبأ وبناء سد مأرب العظيم ونقش صرواح",
      titleEn = "Era of the Mukarribs of Saba & Great Dam of Marib",
      descriptionAr = "تدوين نقوش النصر الكبرى لكربئيل وتر بخط المسند البارز وتوسيع شبكة السدود والري الزراعي باليمن.",
      descriptionEn = "Recording Karibil Watar's res gestae inscription at Sirwah Temple and monumentally fortifying the Great Dam of Marib."
    ),
    ChronologyEvent(
      id = "chrono-6",
      branch = "Northwest Semitic",
      year = "c. 500–300 BCE",
      titleAr = "الآرامية الإمبراطورية لغة الإدارة الدبلوماسية العالمية",
      titleEn = "Imperial Aramaic as Universal Near Eastern Lingua Franca",
      descriptionAr = "اعتماد الآرامية لغة رسمية للإمبراطورية الأخمينية من مصر (برديات إلفنتين) حتى أطراف الهند وبابل.",
      descriptionEn = "Official adoption of Imperial Aramaic across the Achaemenid Empire, from the Elephantine papyri in Egypt to Persepolis."
    ),
    ChronologyEvent(
      id = "chrono-7",
      branch = "Ethiosemitic",
      year = "c. 350 CE",
      titleAr = "مسلات الملك عيزانا الأكسومي وابتكار خط الفيدل الجعزي المنقط",
      titleEn = "Ezana of Aksum & Creation of the Vocalized Ethiopic Fidel",
      descriptionAr = "نقش مسلات النصر بثلاثة خطوط وتطوير الحركات المقطعية السبع الملحقة بصوامت الفيدل الجعزي.",
      descriptionEn = "Erection of King Ezana's trilingual victory monuments in Aksum, marking the transition to vocalized Ethiopic Fidel."
    )
  )

  val sites = listOf(
    ArchaeologicalSite(
      id = "site-babylon",
      nameAr = "بابل الأثرية",
      nameEn = "Ancient Babylon",
      country = "العراق (محافظة بابل)",
      periodAr = "2000–500 ق.م",
      scriptFamilies = listOf("المسماري البابلي الأكادي"),
      latitude = 32.5422,
      longitude = 44.4211,
      primaryInscriptions = listOf("مسلة شريعة حمورابي", "نقوش نبوخذ نصر التأسيسية", "بوابة عشتار"),
      descriptionAr = "عاصمة الإمبراطورية البابلية الكبرى ومركز التدوين القانوني والأدبي والفلكي الأكادي.",
      descriptionEn = "Metropolis of the Babylonian Empire and focal center of legal, literary, and astronomical cuneiform corpora."
    ),
    ArchaeologicalSite(
      id = "site-ugarit",
      nameAr = "أوغاريت (رأس الشمرا)",
      nameEn = "Ras Shamra (Ancient Ugarit)",
      country = "سوريا (اللاذقية)",
      periodAr = "1400–1185 ق.م",
      scriptFamilies = listOf("المسماري الأوغاريتي الأبجدي", "المسماري الأكادي"),
      latitude = 35.6025,
      longitude = 35.8569,
      primaryInscriptions = listOf("ملحمة بعل وعنات", "أسطورة كرت الملكي", "رسائل القصر الملكي الدبلوماسية"),
      descriptionAr = "الميناء الكنعاني الشهير الذي أهدى الإنسانية أول أبجدية مسمارية صامتة تضم ثلاثين حرفاً.",
      descriptionEn = "Famed Late Bronze Levant trading port, birthplace of the 30-sign alphabetic cuneiform script."
    ),
    ArchaeologicalSite(
      id = "site-byblos",
      nameAr = "جبيل (بيبلوس)",
      nameEn = "Byblos (Jbeil / Gubla)",
      country = "لبنان",
      periodAr = "3000 ق.م–العصر الروماني",
      scriptFamilies = listOf("الأبجدية الفينيقية الخطية", "الهيروغليفية الكنعانية المبكرة"),
      latitude = 34.1233,
      longitude = 35.6514,
      primaryInscriptions = listOf("تابوت أحيرام ملك جبيل", "نقش يحيملك", "مسلة أبيبعل وإيلباعل"),
      descriptionAr = "أقدم الموانئ الفينيقية المصدرة للبردي والأخشاب وحاضنة نشوء الأبجدية الفينيقية ذات الـ 22 حرفاً.",
      descriptionEn = "Earliest Phoenician harbor, commercial papyrus hub, and birthplace of the 22-letter linear alphabet."
    ),
    ArchaeologicalSite(
      id = "site-marib",
      nameAr = "مأرب وصرواح",
      nameEn = "Marib & Sirwah (Kingdom of Saba)",
      country = "اليمن (محافظة مأرب)",
      periodAr = "1000 ق.م–600 م",
      scriptFamilies = listOf("خط المسند العربي الجنوبي", "خط الزبور التراثي"),
      latitude = 15.4217,
      longitude = 45.3467,
      primaryInscriptions = listOf("نقش صرواح الكبير للمكرب كربئيل وتر", "نقوش محرم بلقيس (معبد أوام)", "نقوش سد مأرب"),
      descriptionAr = "قلب حضارة سبأ وحمير، موطن سد مأرب العظيم ومعبد أوام الزاخر بآلاف نقوش المسند النذرية والملكية.",
      descriptionEn = "Heart of Sabaean civilization, home of the Great Dam and the Awwam Temple containing thousands of votive Musnad texts."
    ),
    ArchaeologicalSite(
      id = "site-hegra",
      nameAr = "الحِجْر (مدائن صالح / العلا)",
      nameEn = "Hegra (Mada'in Salih / AlUla)",
      country = "المملكة العربية السعودية (العلا)",
      periodAr = "القرن الأول ق.م–القرن الثاني م",
      scriptFamilies = listOf("الخط النبطي الجداري", "الخط الديداني واللحياني"),
      latitude = 26.8000,
      longitude = 37.9500,
      primaryInscriptions = listOf("نقوش واجهات مدافن قصر الفريد", "نقوش الأوقاف النبطية", "نقش حيان بن كوزا"),
      descriptionAr = "حاضرة الأنباط الجنوبية الكبرى؛ وثقت نقوشها الصخرية انتقال الخط النبطي نحو الخط العربي القديم.",
      descriptionEn = "Southern capital of the Nabataean Kingdom with monumental tomb façades charting the transition to Arabic script."
    ),
    ArchaeologicalSite(
      id = "site-aksum",
      nameAr = "أكسوم الملكية",
      nameEn = "Royal City of Aksum",
      country = "إثيوبيا (إقليم تيغراي)",
      periodAr = "100–900 م",
      scriptFamilies = listOf("الخط الجعزي المقطعي (الفيدل)", "خط المسند الجنوبي", "اليونانية"),
      latitude = 14.1311,
      longitude = 38.7208,
      primaryInscriptions = listOf("مسلة الملك عيزانا ثلاثية اللغات", "نقوش المسلات الملكية العملاقة", "أناجيل أبا غاريما"),
      descriptionAr = "حاضرة إمبراطورية أكسوم التي ربطت تجارة البحر الأحمر بالمرتفعات الإثيوبية وشهدت ولادة خط الفيدل الجعزي.",
      descriptionEn = "Metropolis of the Aksumite Empire, bridging Red Sea trade routes and witnessing the genesis of vocalized Ge'ez Fidel."
    ),
    ArchaeologicalSite(
      id = "site-nineveh",
      nameAr = "نينوى الأثرية",
      nameEn = "Nineveh (Kouyunjik)",
      country = "العراق (الموصل)",
      periodAr = "1800–612 ق.م",
      scriptFamilies = listOf("المسماري الآشوري الحديث"),
      latitude = 36.3590,
      longitude = 43.1528,
      primaryInscriptions = listOf("مكتبة آشوربانيبال الكبرى", "منشور سنحاريب (تايلور)", "ملحمة جلجامش الطوفانية"),
      descriptionAr = "عاصمة الإمبراطورية الآشورية الحديثة وأعظم حاضرة للألواح المسمارية الأدبية والعلمية في التاريخ.",
      descriptionEn = "Imperial capital of the Neo-Assyrian Empire, home to Ashurbanipal's legendary royal cuneiform library."
    ),
    ArchaeologicalSite(
      id = "site-ebla",
      nameAr = "إيبلا (تل مرديخ)",
      nameEn = "Ancient Ebla (Tell Mardikh)",
      country = "سوريا (إدلب)",
      periodAr = "2500–1600 ق.م",
      scriptFamilies = listOf("المسماري الإيبلاوي السامي المبكر"),
      latitude = 35.7981,
      longitude = 36.7989,
      primaryInscriptions = listOf("أرشيف القصر الملكي الإيبلاوي (أكثر من 20 ألف رقيم)", "المعجم اللغوي السومري-الإيبلاوي ثنائي اللغة"),
      descriptionAr = "حاضرة سامية شرقية كبرى كشفت رُقمها الطينية عن أقدم لغة سامية مدونة في بلاد الشام تعود للألف الثالث ق.م.",
      descriptionEn = "Major Early Bronze Age Semitic center whose archives yielded the oldest recorded Semitic dialect of the Levant."
    ),
    ArchaeologicalSite(
      id = "site-dhiban",
      nameAr = "ذيبان (مملكة مؤاب)",
      nameEn = "Dhiban (Ancient Moab)",
      country = "الأردن (مأدبا)",
      periodAr = "القرن التاسع ق.م",
      scriptFamilies = listOf("الخط الفينيقي / المؤابي الكنعاني القديم"),
      latitude = 31.5000,
      longitude = 35.7778,
      primaryInscriptions = listOf("مسلة ميشع ملك مؤاب (حجر مؤاب الأثري)", "نقش القلعة الملكية"),
      descriptionAr = "عاصمة مملكة مؤاب الكنعانية وموقع اكتشاف مسلة الملك ميشع الشهيرة المؤرخة بنحو 840 ق.م المحفوظة باللوفر.",
      descriptionEn = "Royal capital of the Kingdom of Moab where the famous 34-line Mesha Inscription was unearthed in 1868."
    ),
    ArchaeologicalSite(
      id = "site-petra",
      nameAr = "البتراء (رقيم الأنباط)",
      nameEn = "Petra (Raqmu)",
      country = "الأردن (معان)",
      periodAr = "القرن الرابع ق.م–القرن الثاني م",
      scriptFamilies = listOf("الخط النبطي الجداري والتذكاري"),
      latitude = 30.3285,
      longitude = 35.4444,
      primaryInscriptions = listOf("نقوش مقابر الدير والخزنة", "نقش عنشو الأخ الملكي", "نقش تركمان النبطي القانوني"),
      descriptionAr = "عاصمة الأنباط الوردية المنحوتة في الصخور ومفترق طرق القوافل التجارية ونقوش الخط النبطي.",
      descriptionEn = "Rose-red rock-carved capital of the Nabataean Kingdom, crossroads of trans-Arabian incense caravans."
    ),
    ArchaeologicalSite(
      id = "site-palmyra",
      nameAr = "تدمر (عروس البادية)",
      nameEn = "Palmyra (Tadmor)",
      country = "سوريا (حمص)",
      periodAr = "القرن الأول ق.م–القرن الثالث م",
      scriptFamilies = listOf("الخط الآرامي التدمرى التذكاري والمدني"),
      latitude = 34.5516,
      longitude = 38.2672,
      primaryInscriptions = listOf("التعريفة الجمركية التدمرية الكبرى", "نقوش أعمدة معبد بل", "شواهد أضرحة تدمر الجنائزية"),
      descriptionAr = "واحة القوافل التجارية الدولية وحاضنة الخط الآرامي التدمري ونقوش الملكة زنوبيا الشهيرة.",
      descriptionEn = "Legendary Silk Road desert oasis featuring thousands of monumentally inscribed bilingual Palmyrene texts."
    ),
    ArchaeologicalSite(
      id = "site-carthage",
      nameAr = "قرطاج (قَرْت حَدَشْت)",
      nameEn = "Carthage (Qart-Ḥadašt)",
      country = "تونس (تونس العاصمة)",
      periodAr = "814–146 ق.م",
      scriptFamilies = listOf("الخط البونيقي والنوبونيقي"),
      latitude = 36.8528,
      longitude = 10.3233,
      primaryInscriptions = listOf("نقوش توفت قرطاج النذرية للآلهة تنّيت", "تعريفة مرسيليا وقرطاج القربانية", "نقوش صلب ملقرت"),
      descriptionAr = "حاضرة الفينيقيين الغربيين في شمال إفريقيا ومركز ازدهار اللغة البونيقية والأدب الملاحي القرطاجي.",
      descriptionEn = "Great Western Phoenician metropolis in North Africa, center of Punic epigraphy, religion, and naval trade."
    ),
    ArchaeologicalSite(
      id = "site-serabit",
      nameAr = "سرابيط الخادم",
      nameEn = "Serabit el-Khadim",
      country = "مصر (شبه جزيرة سيناء)",
      periodAr = "1800–1500 ق.م",
      scriptFamilies = listOf("الخط السنائي الأولي (Proto-Sinaitic)"),
      latitude = 28.9950,
      longitude = 33.4589,
      primaryInscriptions = listOf("نقوش مناجم الفيروز السامية", "تمثال أبو الهول الإهدائي (لبعلت)"),
      descriptionAr = "مهد الأبجدية الصوتية الأولى حيث طوّر العمال الساميون الحروف الهجائية بالاستعارة من الهيروغليفية المصرية.",
      descriptionEn = "Birthplace of the Proto-Sinaitic alphabet, the earliest recorded ancestral ancestor of all Semitic scripts."
    ),
    ArchaeologicalSite(
      id = "site-tayma",
      nameAr = "تيماء القديمة",
      nameEn = "Ancient Tayma Oasis",
      country = "المملكة العربية السعودية (تبوك)",
      periodAr = "الألف الأول ق.م–القرن السادس ق.م",
      scriptFamilies = listOf("الخط التيمائي", "الخط الآرامي التيمائي"),
      latitude = 27.6289,
      longitude = 38.5433,
      primaryInscriptions = listOf("حجر تيماء الآرامي الشهير (متحف اللوفر)", "نقوش قصر الحمراء ونبونيد البابلي"),
      descriptionAr = "واحة تجارية عريقة استقر بها آخر ملوك بابل (نبونيد) وحفظت نقوشاً صخرية بالخط التيمائي والآرامي.",
      descriptionEn = "Strategic Arabian trade oasis where King Nabonidus resided, yielding monumental Taymanitic and Aramaic stelae."
    ),
    ArchaeologicalSite(
      id = "site-lachish",
      nameAr = "تل الدوير (لخيش القديمة)",
      nameEn = "Tel Lachish (Tell ed-Duweir)",
      country = "فلسطين",
      periodAr = "القرن السادس ق.م",
      scriptFamilies = listOf("الخط العبري الكنعاني القديم"),
      latitude = 31.5653,
      longitude = 34.8489,
      primaryInscriptions = listOf("رسائل لخيش الفخارية (أوستراكا لخيش)", "أبجدية الدوير الفخارية"),
      descriptionAr = "مدينة كنعانية عبرية قديمة عُثر في بوابتها على 21 شقفة فخارية مدونة بالحبر ترصد حصار نبوخذ نصر.",
      descriptionEn = "Ancient fortified city yielding the famed 21 Lachish Ostraca letters detailing the final days of the Babylonian siege."
    ),
    ArchaeologicalSite(
      id = "site-karatepe",
      nameAr = "قره تبه (كاراتبه)",
      nameEn = "Karatepe-Aslantaş",
      country = "تركيا (قيليقية / عثمانية)",
      periodAr = "القرن الثامن ق.م",
      scriptFamilies = listOf("الخط الفينيقي الملكي", "الهيروغليفية الحثية اللوفية"),
      latitude = 37.2947,
      longitude = 36.2522,
      primaryInscriptions = listOf("نقش أزيتوادا ثنائي اللغة (الفينيقي واللوفي الحثي)"),
      descriptionAr = "قلعة قيليقية حاسمة في تاريخ الفيلولوجيا وفك رموز الهيروغليفية اللوفية عبر نص الملك أزيتوادا الفينيقي.",
      descriptionEn = "Key fortress yielding the bilingual Phoenician-Luwian inscription that unlocked Anatolian hieroglyphs."
    ),
    ArchaeologicalSite(
      id = "site-yeha",
      nameAr = "يحا (معبد ألمقه)",
      nameEn = "Yeha (Temple of Almaqah)",
      country = "إثيوبيا (إقليم تيغراي)",
      periodAr = "القرن الثامن–السابع ق.م",
      scriptFamilies = listOf("خط المسند السبئي", "السامية الإثيوبية المبكرة"),
      latitude = 14.2889,
      longitude = 39.0194,
      primaryInscriptions = listOf("نقوش معبد المقه العظيم", "نقوش مملكة دعمت (D'mt) السبئية الأكسومية"),
      descriptionAr = "أقدم منشأة حجرية قائمة في إثيوبيا جنوب الصحراء تضم نقوش المسند الجنوبي وشواهد التأثير السبئي في بلاد الحبشة.",
      descriptionEn = "Oldest standing stone structure in Sub-Saharan Africa, containing Sabaean Musnad and early Ethiosemitic texts."
    )
  )

  val mediaArtifacts = listOf(
    MediaArtifact(
      id = "art-01",
      title = "Stele of Hammurabi",
      titleAr = "مسلة شريعة حمورابي البابلية",
      category = "inscriptions",
      scriptType = "Old Babylonian Cuneiform",
      material = "Black Basalt Monolith",
      museum = "Louvre Museum, Paris (Department of Near Eastern Antiquities)",
      datePeriod = "c. 1750 BCE",
      imageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=800&q=80",
      descriptionAr = "المسلة القانونية البابلية الخالدة المنقوشة بـ 282 بنداً تشريعياً بالخط المسماري البابلي الكلاسيكي.",
      descriptionEn = "Monumental 2.25m diorite stele bearing the 282 codified laws of King Hammurabi in Classical Babylonian."
    ),
    MediaArtifact(
      id = "art-02",
      title = "Mesha Victory Stele",
      titleAr = "مسلة ميشع ملك مؤاب (حجر مؤاب)",
      category = "inscriptions",
      scriptType = "Paleo-Hebrew / Moabite Alphabet",
      material = "Black Basalt",
      museum = "Louvre Museum, Paris",
      datePeriod = "c. 840 BCE",
      imageUrl = "https://images.unsplash.com/photo-1599739291060-4578e77dac5d?auto=format&fit=crop&w=800&q=80",
      descriptionAr = "أهم وثيقة إبيغرافية في بلاد الشام توثق انتصارات مملكة مؤاب ولغتها الكنعانية الأصيلة في 34 سطراً.",
      descriptionEn = "Pivotal 34-line basalt inscription discovered in Dhiban celebrating the Moabite king's triumph over Israel."
    ),
    MediaArtifact(
      id = "art-03",
      title = "Semitic Languages Geographic Heatmap",
      titleAr = "خريطة التوزيع والانتشار الجغرافي للغات السامية",
      category = "maps",
      scriptType = "Cartography & Isoglosses",
      material = "Digital Philological Atlas",
      museum = "Semitic Epigraphic Institute",
      datePeriod = "3000 BCE – Present",
      imageUrl = "https://images.unsplash.com/photo-1524661135-423995f22d0b?auto=format&fit=crop&w=800&q=80",
      descriptionAr = "أطلس طبوغرافي يبين مراكز الإشعاع اللغوي وحركة الخطوط والأبجديات السامية عبر الشرق الأدنى وحوض المتوسط.",
      descriptionEn = "Spatial diffusion map tracking Semitic branch dispersals and epigraphic centers across Arabia and the Levant."
    ),
    MediaArtifact(
      id = "art-04",
      title = "Pan-Semitic Alphabet Family Tree",
      titleAr = "شجرة النسب الفيلولوجية وتطور الأبجديات السامية",
      category = "charts",
      scriptType = "Comparative Epigraphy Chart",
      material = "Scholarly Epigraphic Matrix",
      museum = "Academic Open Access Corpus",
      datePeriod = "Comparative Typology",
      imageUrl = "https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?auto=format&fit=crop&w=800&q=80",
      descriptionAr = "مخطط بياني يوضح انحدار الأبجدية السينائية المبكرة والفينيقية إلى الآرامية والمسند والعربي والإغريقي.",
      descriptionEn = "Systematic genealogical chart illustrating the linear descent of Semitic scripts from Proto-Sinaitic to modern scripts."
    ),
    MediaArtifact(
      id = "art-05",
      title = "Abba Garima Golden Gospel Manuscript",
      titleAr = "مخطوطة أناجيل أبا غاريما الجعزية المذهبة",
      category = "manuscripts",
      scriptType = "Ethiopic Fidel on Vellum",
      material = "Illuminated Goat Skin Vellum",
      museum = "Abba Garima Monastery, Tigray, Ethiopia",
      datePeriod = "c. 390–650 CE",
      imageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=800&q=80",
      descriptionAr = "أقدم مخطوطة إنجيلية متكاملة ومذهبة في العالم مدونة بخط الفيدل الجعزي المتقن على الرقوق الجلدية.",
      descriptionEn = "Earliest surviving illuminated Christian Gospel manuscript in the world, written in Classical Ge'ez on vellum."
    )
  )

  val flashcards = listOf(
    GrammarCard(
      id = "fc-01",
      categoryAr = "الصوتيات والقوانين الفونولوجية",
      categoryEn = "Phonology & Sound Laws",
      titleAr = "التحول الكنعاني (*ā > ō)",
      titleEn = "The Canaanite Vowel Shift (*ā > ō)",
      formula = "Proto-Semitic *ā → Canaanite /ō/ (or /ū/ in Phoenician)",
      summaryAr = "تحول الألف الممدودة الموقعة تحت النبر إلى واو مضمومة في جميع اللغات الكنعانية (الفينيقية، العبرية، المؤابية).",
      summaryEn = "Long stressed Proto-Semitic *ā shifted to /ō/ in Canaanite dialects (e.g. *šalām- > šālōm).",
      inscriptionalWitnessAr = "مسلة ميشع السطر 1: 'šlšn št' (شلوشن شت = ثلاثين سنة) مقابل العربية 'ثلاثين'.",
      inscriptionalWitnessEn = "Mesha Stele line 1: 'šlšn št' (thirty years) contrasting with Arabic 'thalāthīn'.",
      comparativeAnalysisAr = "يميز هذا القانون الفرع الكنعاني عن اللغتين الأوغاريتية والآرامية اللتين حافظتا على الألف (*ā)."
    ),
    GrammarCard(
      id = "fc-02",
      categoryAr = "صرف الأفعال وبناء الجذوع",
      categoryEn = "Verbal Stems (Binyanim)",
      titleAr = "الجذع السببي (Causative C-stem)",
      titleEn = "The Semitic Causative Prefix (Š / H / ʾ)",
      formula = "Akkadian: Š-stem | Hebrew/Sabaic: H-stem | Arabic/Aramaic: ʾ-stem / Yiphʿil",
      summaryAr = "اشتقاق وزن التعدية والسببية ببادئة صامتة: السين (Š) في الأكادية والأوغاريتية، والهاء (H) في العبرية والمسند، والهمزة (ʾ) في العربية.",
      summaryEn = "Causative formation employs Š- in East Semitic, H- in Hebrew/Sayhadic, and ʾ- in Arabic/Classical Aramaic.",
      inscriptionalWitnessAr = "نقش صرواح: 'hqny' (أهدى وأنشأ) من الجذر 'قني' بصيغة السببي المسندية (H-stem).",
      inscriptionalWitnessEn = "Sirwah inscription: 'hqny' (he dedicated) from root q-n-y in Sabaic H-causative stem.",
      comparativeAnalysisAr = "يبرز تدرج بادئة السببية من السين المسمارية القديمة إلى الهاء ثم إلى الهمزة في اللغات الأحدث."
    ),
    GrammarCard(
      id = "fc-03",
      categoryAr = "التراكيب والنحو السردي",
      categoryEn = "Syntax & Narrative Typology",
      titleAr = "واو العطف السردية التتابعية (Waw Consecutive)",
      titleEn = "Waw Consecutive / Wayyiqtol Narrative Construction",
      formula = "w- + yiqtol prefix conjugation with past narrative value (وَيَفْعَلْ = فَعَلَ)",
      summaryAr = "استخدام حرف الواو متبوعاً بصيغة المضارع المجزوم للتعبير عن الماضي التتابعي السردي في الكنعانية والمؤابية والعبرية.",
      summaryEn = "Narrative past sequence formed by prefixing waw to prefix-conjugation verbs (Wayyiqtol) in Canaanite/Moabite.",
      inscriptionalWitnessAr = "مسلة ميشع: 'w-ʾʿś h-bmt zt' (وصنعتُ هذا المرتفع) و 'w-yʾmr ly kmš' (وقال لي كموش).",
      inscriptionalWitnessEn = "Mesha Stele: 'w-ʾʿś h-bmt' (and I built this high place) and 'w-yʾmr ly' (and Chemosh said).",
      comparativeAnalysisAr = "تطابق تام في البنية السردية بين مسلة ميشع المؤابية ونثر العهد القديم بالعبرية."
    )
  )

  val bibliography = listOf(
    BibliographyItem(
      id = "bib-01",
      title = "KAI - Kanaanäische und Aramäische Inschriften (5. Auflage, 3 Bände)",
      authors = "Donner, Herbert & Röllig, Wolfgang",
      year = "2002",
      publisher = "Otto Harrassowitz Verlag, Wiesbaden",
      summaryAr = "المرجع الإبيغرافي القياسي والمدونة الشاملة لكافة النقوش الكنعانية والفينيقية والبونية والآرامية والمؤابية مع التحقيقات الفيلولوجية والترجمة الألمانية الشارحة.",
      summaryEn = "The gold-standard academic corpus of Canaanite, Phoenician, Punic, Moabite, and Aramaic inscriptions with philological apparatus.",
      url = "https://www.harrassowitz-verlag.de/",
      citationAPA = "Donner, H., & Röllig, W. (2002). Kanaanäische und Aramäische Inschriften (5th ed., Vols. 1–3). Harrassowitz Verlag.",
      citationMLA = "Donner, Herbert, and Wolfgang Röllig. Kanaanäische und Aramäische Inschriften. 5th ed., 3 vols., Harrassowitz Verlag, 2002.",
      citationChicago = "Donner, Herbert, and Wolfgang Röllig. 2002. Kanaanäische und Aramäische Inschriften. 5th ed. 3 vols. Wiesbaden: Harrassowitz Verlag.",
      citationBibTeX = "@book{kai2002,\n  author = {Donner, Herbert and R{\\\"o}llig, Wolfgang},\n  title = {Kanaan{\\\"a}ische und Aram{\\\"a}ische Inschriften},\n  edition = {5},\n  volumes = {3},\n  publisher = {Otto Harrassowitz Verlag},\n  address = {Wiesbaden},\n  year = {2002}\n}"
    ),
    BibliographyItem(
      id = "bib-02",
      title = "The Semitic Languages (Routledge Language Family Series, 2nd Edition)",
      authors = "Huehnergard, John & Pat-El, Na'ama (eds.)",
      year = "2019",
      publisher = "Routledge, London & New York",
      summaryAr = "أشمل مرجع فيلولوجي حديث يغطي النحو، والصرف، والفونولوجيا، والتاريخ المقارن لكافة لغات وشعب العائلة السامية الـ 35+ بأقلام كبار الباحثين في العالم.",
      summaryEn = "Authoritative comprehensive reference volume covering grammar, phonology, and historical comparative morphology of all Semitic branches.",
      url = "https://www.routledge.com/The-Semitic-Languages/Huehnergard-Pat-El/p/book/9780415731959",
      citationAPA = "Huehnergard, J., & Pat-El, N. (Eds.). (2019). The Semitic Languages (2nd ed.). Routledge.",
      citationMLA = "Huehnergard, John, and Na'ama Pat-El, editors. The Semitic Languages. 2nd ed., Routledge, 2019.",
      citationChicago = "Huehnergard, John, and Na'ama Pat-El, eds. 2019. The Semitic Languages. 2nd ed. London: Routledge.",
      citationBibTeX = "@book{huehnergard2019semitic,\n  editor = {Huehnergard, John and Pat-El, Na'ama},\n  title = {The Semitic Languages},\n  edition = {2},\n  publisher = {Routledge},\n  address = {London and New York},\n  year = {2019}\n}"
    ),
    BibliographyItem(
      id = "bib-03",
      title = "Dictionary of North-West Semitic Inscriptions (DNWSI, 2 Volumes)",
      authors = "Hoftijzer, Jacob & Jongeling, Karel",
      year = "1995",
      publisher = "E.J. Brill, Leiden",
      summaryAr = "المعجم الموسوعي لجذور ومفردات النقوش السامية الشمالية الغربية (الفينيقية، البونية، المؤابية، العمونية، الآدومية، والآرامية بمختلف مراحلها).",
      summaryEn = "Comprehensive monumental two-volume lexicon cataloging every lexical attestation in Northwest Semitic epigraphy.",
      url = "https://brill.com/display/title/1545",
      citationAPA = "Hoftijzer, J., & Jongeling, K. (1995). Dictionary of North-West Semitic Inscriptions (Vols. 1–2). E.J. Brill.",
      citationMLA = "Hoftijzer, Jacob, and Karel Jongeling. Dictionary of North-West Semitic Inscriptions. 2 vols., E.J. Brill, 1995.",
      citationChicago = "Hoftijzer, Jacob, and Karel Jongeling. 1995. Dictionary of North-West Semitic Inscriptions. 2 vols. Leiden: E.J. Brill.",
      citationBibTeX = "@book{dnwsi1995,\n  author = {Hoftijzer, Jacob and Jongeling, Karel},\n  title = {Dictionary of North-West Semitic Inscriptions},\n  volumes = {2},\n  publisher = {E.J. Brill},\n  address = {Leiden},\n  year = {1995}\n}"
    ),
    BibliographyItem(
      id = "bib-04",
      title = "A Grammar of the Phoenician Language (Phoenician-Punic Grammar)",
      authors = "Krahmalkov, Charles R.",
      year = "2001",
      publisher = "E.J. Brill, Leiden (Handbook of Oriental Studies / Handbuch der Orientalistik)",
      summaryAr = "المرجع النحوي والمعجمي الأساسي للغة الفينيقية ولهجاتها المتوسطية ولهجة قرطاج البونية والبونية الحديثة.",
      summaryEn = "Comprehensive reference grammar and syntax of Phoenician and Punic epigraphic corpora.",
      url = "https://brill.com/display/title/356",
      citationAPA = "Krahmalkov, C. R. (2001). A Grammar of the Phoenician Language. E.J. Brill.",
      citationMLA = "Krahmalkov, Charles R. A Grammar of the Phoenician Language. E.J. Brill, 2001.",
      citationChicago = "Krahmalkov, Charles R. 2001. A Grammar of the Phoenician Language. Leiden: E.J. Brill.",
      citationBibTeX = "@book{krahmalkov2001phoenician,\n  author = {Krahmalkov, Charles R.},\n  title = {A Grammar of the Phoenician Language},\n  series = {Handbuch der Orientalistik},\n  publisher = {E.J. Brill},\n  address = {Leiden},\n  year = {2001}\n}"
    ),
    BibliographyItem(
      id = "bib-05",
      title = "A Grammar of Akkadian (Harvard Semitic Studies 45, 3rd Edition)",
      authors = "Huehnergard, John",
      year = "2011",
      publisher = "Eisenbrauns / Penn State University Press",
      summaryAr = "المرجع الأكاديمي القياسي لقواعد وصرف ونحو ونصوص اللغة الأكادية بلهجتيها البابلية القديمة والآشورية.",
      summaryEn = "The premier standard pedagogical and reference grammar for Old Babylonian and Akkadian cuneiform philology.",
      url = "https://www.eisenbrauns.org/",
      citationAPA = "Huehnergard, J. (2011). A Grammar of Akkadian (3rd ed.). Eisenbrauns.",
      citationMLA = "Huehnergard, John. A Grammar of Akkadian. 3rd ed., Eisenbrauns, 2011.",
      citationChicago = "Huehnergard, John. 2011. A Grammar of Akkadian. 3rd ed. Winona Lake: Eisenbrauns.",
      citationBibTeX = "@book{huehnergard2011akkadian,\n  author = {Huehnergard, John},\n  title = {A Grammar of Akkadian},\n  edition = {3},\n  series = {Harvard Semitic Studies 45},\n  publisher = {Eisenbrauns},\n  year = {2011}\n}"
    ),
    BibliographyItem(
      id = "bib-06",
      title = "The Chicago Assyrian Dictionary (CAD, 21 Volumes / 26 Parts)",
      authors = "Gelb, Ignace J., Landsberger, B., Oppenheim, A. Leo, et al. (Oriental Institute of Chicago)",
      year = "1956–2011",
      publisher = "The Oriental Institute of the University of Chicago",
      summaryAr = "أعظم موسوعة معجمية للغة الأكادية ونصوص بلاد الرافدين المسمارية، استغرقت كتابتها قرابة قرن كامل في 26 مجلداً.",
      summaryEn = "The monumental 26-volume dictionary of the Akkadian language of Mesopotamia spanning centuries of cuneiform texts.",
      url = "https://isac.uchicago.edu/research/publications/assyrian-dictionary-oriental-institute-university-chicago",
      citationAPA = "Gelb, I. J., Landsberger, B., & Oppenheim, A. L. (Eds.). (1956–2011). The Assyrian Dictionary of the Oriental Institute of the University of Chicago (Vols. 1–21). The Oriental Institute.",
      citationMLA = "Gelb, Ignace J., et al., editors. The Chicago Assyrian Dictionary. 21 vols., The Oriental Institute of the University of Chicago, 1956–2011.",
      citationChicago = "Gelb, Ignace J., Benno Landsberger, and A. Leo Oppenheim, eds. 1956–2011. The Chicago Assyrian Dictionary. 21 vols. Chicago: Oriental Institute.",
      citationBibTeX = "@book{cad1956_2011,\n  editor = {Gelb, Ignace J. and Landsberger, Benno and Oppenheim, A. Leo},\n  title = {The Chicago Assyrian Dictionary},\n  volumes = {21},\n  publisher = {The Oriental Institute of the University of Chicago},\n  year = {1956--2011}\n}"
    ),
    BibliographyItem(
      id = "bib-07",
      title = "Dictionnaire Sabéen / Sabaic Dictionary (English - French - Arabic)",
      authors = "Beeston, A. F. L., Ghul, M. A., Müller, W. W., & Ryckmans, J.",
      year = "1982",
      publisher = "Éditions Peeters, Louvain-la-Neuve / Librairie du Liban, Beirut",
      summaryAr = "المعجم السبئي الثلاثي المعتمد دولياً في قراءة وفك نقوش خط المسند في حضارة سبأ واليمن القديم.",
      summaryEn = "The international tripartite (English-French-Arabic) standard lexicon for Epigraphic South Arabian / Sabaic inscriptions.",
      url = "https://www.peeters-leuven.be/",
      citationAPA = "Beeston, A. F. L., Ghul, M. A., Müller, W. W., & Ryckmans, J. (1982). Sabaic Dictionary (Dictionnaire Sabéen). Peeters.",
      citationMLA = "Beeston, A. F. L., et al. Sabaic Dictionary (Dictionnaire Sabéen). Peeters, 1982.",
      citationChicago = "Beeston, A. F. L., M. A. Ghul, W. W. Müller, and J. Ryckmans. 1982. Sabaic Dictionary. Louvain-la-Neuve: Peeters.",
      citationBibTeX = "@book{beeston1982sabaic,\n  author = {Beeston, A. F. L. and Ghul, M. A. and M{\\\"u}ller, W. W. and Ryckmans, J.},\n  title = {Sabaic Dictionary (Dictionnaire Sab{\\'e}en)},\n  publisher = {Peeters and Librairie du Liban},\n  address = {Louvain-la-Neuve and Beirut},\n  year = {1982}\n}"
    ),
    BibliographyItem(
      id = "bib-08",
      title = "A Ugaritic Grammar (Handbook of Oriental Studies / Handbuch der Orientalistik)",
      authors = "Sivan, Daniel",
      year = "1997",
      publisher = "E.J. Brill, Leiden",
      summaryAr = "قواعد اللغة الأوغاريتية المسمارية ونصوص ملاحم رأس الشمرا مع التحليل الصرفي والنحوي المقارن.",
      summaryEn = "Comprehensive grammatical handbook of Ugaritic cuneiform alphabetic inscriptions and mythological poetic texts.",
      url = "https://brill.com/display/title/1546",
      citationAPA = "Sivan, D. (1997). A Ugaritic Grammar. E.J. Brill.",
      citationMLA = "Sivan, Daniel. A Ugaritic Grammar. E.J. Brill, 1997.",
      citationChicago = "Sivan, Daniel. 1997. A Ugaritic Grammar. Leiden: E.J. Brill.",
      citationBibTeX = "@book{sivan1997ugaritic,\n  author = {Sivan, Daniel},\n  title = {A Ugaritic Grammar},\n  publisher = {E.J. Brill},\n  address = {Leiden},\n  year = {1997}\n}"
    ),
    BibliographyItem(
      id = "bib-09",
      title = "Comparative Semitic Linguistics: A Manual",
      authors = "Lipinski, Edward",
      year = "2001",
      publisher = "Peeters Publishers, Leuven (Orientalia Lovaniensia Analecta 80)",
      summaryAr = "دليل علم اللسانيات السامية المقارنة، وتاريخ الأصوات والصرف والنحو واشتقاق الجذور السامية القديمة.",
      summaryEn = "Essential textbook and manual detailing comparative Semitic historical phonology, morphology, and script evolution.",
      url = "https://www.peeters-leuven.be/detail.php?search_key=9789042909052",
      citationAPA = "Lipiński, E. (2001). Comparative Semitic Linguistics: A Manual (2nd ed.). Peeters Publishers.",
      citationMLA = "Lipiński, Edward. Comparative Semitic Linguistics: A Manual. 2nd ed., Peeters Publishers, 2001.",
      citationChicago = "Lipiński, Edward. 2001. Comparative Semitic Linguistics: A Manual. 2nd ed. Leuven: Peeters Publishers.",
      citationBibTeX = "@book{lipinski2001comparative,\n  author = {Lipi{\\\'n}ski, Edward},\n  title = {Comparative Semitic Linguistics: A Manual},\n  edition = {2},\n  series = {Orientalia Lovaniensia Analecta 80},\n  publisher = {Peeters Publishers},\n  address = {Leuven},\n  year = {2001}\n}"
    ),
    BibliographyItem(
      id = "bib-10",
      title = "Comparative Dictionary of Ge'ez (Classical Ethiopic)",
      authors = "Leslau, Wolf",
      year = "1987",
      publisher = "Otto Harrassowitz Verlag, Wiesbaden",
      summaryAr = "المعجم المقارن للغة الجعزية الكلاسيكية، وتتبع جذورها الفيلولوجية في السامية الجنوبية والشرقية والشمالية الغربية.",
      summaryEn = "Definitive comparative etymological lexicon of Classical Ge'ez cross-referenced with all Semitic languages.",
      url = "https://www.harrassowitz-verlag.de/",
      citationAPA = "Leslau, W. (1987). Comparative Dictionary of Ge'ez (Classical Ethiopic). Harrassowitz Verlag.",
      citationMLA = "Leslau, Wolf. Comparative Dictionary of Ge'ez (Classical Ethiopic). Harrassowitz Verlag, 1987.",
      citationChicago = "Leslau, Wolf. 1987. Comparative Dictionary of Ge'ez (Classical Ethiopic). Wiesbaden: Harrassowitz Verlag.",
      citationBibTeX = "@book{leslau1987geez,\n  author = {Leslau, Wolf},\n  title = {Comparative Dictionary of Ge'ez (Classical Ethiopic)},\n  publisher = {Otto Harrassowitz Verlag},\n  address = {Wiesbaden},\n  year = {1987}\n}"
    ),
    BibliographyItem(
      id = "bib-11",
      title = "Textbook of Syrian Semitic Inscriptions (TSSI, 3 Volumes)",
      authors = "Gibson, John C. L.",
      year = "1971–1982",
      publisher = "Oxford University Press, Clarendon Press",
      summaryAr = "مدونة النقوش السامية السورية الكلاسيكية، متضمنة نقوش عبرية ومؤابية وفينيقية وآرامية قديمة مع ترجمة وهوامش تحليلية.",
      summaryEn = "Authoritative 3-volume critical edition of Syrian Semitic inscriptions including Hebrew, Moabite, Phoenician, and Aramaic.",
      url = "https://global.oup.com/",
      citationAPA = "Gibson, J. C. L. (1971–1982). Textbook of Syrian Semitic Inscriptions (Vols. 1–3). Clarendon Press.",
      citationMLA = "Gibson, John C. L. Textbook of Syrian Semitic Inscriptions. 3 vols., Clarendon Press, 1971–1982.",
      citationChicago = "Gibson, John C. L. 1971–1982. Textbook of Syrian Semitic Inscriptions. 3 vols. Oxford: Clarendon Press.",
      citationBibTeX = "@book{gibson1971tssi,\n  author = {Gibson, John C. L.},\n  title = {Textbook of Syrian Semitic Inscriptions},\n  volumes = {3},\n  publisher = {Clarendon Press},\n  address = {Oxford},\n  year = {1971--1982}\n}"
    ),
    BibliographyItem(
      id = "bib-12",
      title = "فقه اللغات السامية (المقارن والتاريخي)",
      authors = "بروكلمان، كارل (Carl Brockelmann) - ترجمة د. رمضان عبد التواب",
      year = "1977",
      publisher = "جامعة الرياض / مطابع الخانجي بالقاهرة",
      summaryAr = "الترجمة العربية المعتمدة للمصنف الرائد في فقه اللغات السامية المقارن وتصنيف الأصوات والصرف وعلاقات القرابة اللغوية.",
      summaryEn = "Classical foundational manual on comparative Semitic philology, phonological shifts, and morphological structures.",
      url = "https://archive.org/",
      citationAPA = "Brockelmann, C. (1977). Fiqh al-Lughāt al-Sāmiyyah [Comparative Semitic Philology] (R. Abdel-Tawwab, Trans.). Maktabat al-Khanji.",
      citationMLA = "Brockelmann, Carl. Fiqh al-Lughāt al-Sāmiyyah. Translated by Ramadan Abdel-Tawwab, Maktabat al-Khanji, 1977.",
      citationChicago = "Brockelmann, Carl. 1977. Fiqh al-Lughāt al-Sāmiyyah. Translated by Ramadan Abdel-Tawwab. Cairo: Maktabat al-Khanji.",
      citationBibTeX = "@book{brockelmann1977ar,\n  author = {Brockelmann, Carl},\n  translator = {Abdel-Tawwab, Ramadan},\n  title = {Fiqh al-Lugh{\\=a}t al-S{\\=a}miyyah},\n  publisher = {Maktabat al-Khanji},\n  address = {Cairo},\n  year = {1977}\n}"
    ),
    BibliographyItem(
      id = "bib-13",
      title = "المفصل في تاريخ العرب قبل الإسلام (10 أجزاء)",
      authors = "العلي، د. جواد (Jawad Ali)",
      year = "1993",
      publisher = "دار الساقي / جامعة بغداد",
      summaryAr = "الموسوعة المرجعية الأضخم لتاريخ العرب وحضارات اليمن القديم، ونقوش المسند والخطوط الشمالية واللحيانية والصفوية والثمودية.",
      summaryEn = "Comprehensive 10-volume historical and epigraphical encyclopedia of Pre-Islamic Arabia and ancient Semitic inscriptions.",
      url = "https://darsaqi.com/",
      citationAPA = "Ali, J. (1993). Al-Mufaṣṣal fī Tārīkh al-ʿArab Qabl al-Islām [The Detailed History of the Arabs Before Islam] (Vols. 1–10). Dār al-Sāqī.",
      citationMLA = "Ali, Jawad. Al-Mufaṣṣal fī Tārīkh al-ʿArab Qabl al-Islām. 10 vols., Dār al-Sāqī, 1993.",
      citationChicago = "Ali, Jawad. 1993. Al-Mufaṣṣal fī Tārīkh al-ʿArab Qabl al-Islām. 10 vols. Beirut: Dār al-Sāqī.",
      citationBibTeX = "@book{ali1993mufassal,\n  author = {Ali, Jawad},\n  title = {Al-Mufa{\\d{s}}{\\d{s}}al f{\\=i} T{\\=a}r{\\=i}kh al-ʿArab Qabl al-Isl{\\=a}m},\n  volumes = {10},\n  publisher = {D{\\=a}r al-S{\\=a}q{\\=i}},\n  address = {Beirut},\n  year = {1993}\n}"
    ),
    BibliographyItem(
      id = "bib-14",
      title = "A Comprehensive Aramaic Lexicon (CAL Project)",
      authors = "Kaufman, Stephen A., Sokoloff, Michael, & Fitzmyer, Joseph A.",
      year = "2005–Present",
      publisher = "Hebrew Union College - Jewish Institute of Religion, Cincinnati",
      summaryAr = "المعجم الآرامي الرقمي الشامل المعتمد عالمياً لتوثيق نصوص ونقوش ومخطوطات كافة مراحل اللغة الآرامية والسريانية والمندائية.",
      summaryEn = "Comprehensive academic lexical database and lexicon covering all dialects and historical periods of Aramaic.",
      url = "https://cal.huc.edu/",
      citationAPA = "Kaufman, S. A., Sokoloff, M., & Fitzmyer, J. A. (2005). The Comprehensive Aramaic Lexicon (CAL). Hebrew Union College.",
      citationMLA = "Kaufman, Stephen A., et al. The Comprehensive Aramaic Lexicon (CAL). Hebrew Union College, 2005.",
      citationChicago = "Kaufman, Stephen A., Michael Sokoloff, and Joseph A. Fitzmyer. 2005. The Comprehensive Aramaic Lexicon (CAL). Cincinnati: Hebrew Union College.",
      citationBibTeX = "@online{cal2005,\n  author = {Kaufman, Stephen A. and Sokoloff, Michael and Fitzmyer, Joseph A.},\n  title = {The Comprehensive Aramaic Lexicon},\n  publisher = {Hebrew Union College},\n  year = {2005},\n  url = {https://cal.huc.edu/}\n}"
    )
  )

  val quizQuestions = listOf(
    QuizQuestion(
      id = "q-01",
      languageOrBranch = "السامية المقارنة",
      questionAr = "ما هو المائز الفونولوجي الرئيسي الذي يفصل اللغات الكنعانية (الفينيقية، العبرية، المؤابية) عن الأوغاريتية والآرامية؟",
      questionEn = "What is the primary phonological isogloss distinguishing Canaanite from Ugaritic and Aramaic?",
      optionsAr = listOf(
        "التحول الصوتي الكنعاني (*ā > ō)",
        "سقوط الصوامت الحلقية بالكامل",
        "تحول رتبة الجملة إلى SOV",
        "استعمال التنوين الميمي حصراً"
      ),
      optionsEn = listOf(
        "The Canaanite Vowel Shift (*ā > ō)",
        "Complete loss of pharyngeal consonants",
        "Transition to strict SOV word order",
        "Exclusive usage of mimation"
      ),
      correctIndex = 0,
      explanationAr = "التحول الكنعاني (*ā > ō) هو القانون الصوتي المميز حيث تحولت الألف الممدودة إلى واو مضمومة مثل: *šalām- > šālōm.",
      explanationEn = "The Canaanite vowel shift *ā > ō is the definitive diagnostic marker of Canaanite languages."
    ),
    QuizQuestion(
      id = "q-02",
      languageOrBranch = "السامية الشرقية (الأكادية)",
      questionAr = "ما هي رتبة الكلمات النحوية الأساسية (Word Order) في الجملة الأكادية الكلاسيكية؟",
      questionEn = "What is the canonical word order in Classical Akkadian clauses?",
      optionsAr = listOf(
        "فاعل - مفعول - فعل (SOV)",
        "فعل - فاعل - مفعول (VSO)",
        "فاعل - فعل - مفعول (SVO)",
        "فعل - مفعول - فاعل (VOS)"
      ),
      optionsEn = listOf(
        "Subject - Object - Verb (SOV)",
        "Verb - Subject - Object (VSO)",
        "Subject - Verb - Object (SVO)",
        "Verb - Object - Subject (VOS)"
      ),
      correctIndex = 0,
      explanationAr = "تأثرت الأكادية بالطبقة السومرية التحتية فأصبحت رتبة الجملة SOV (الفعل دائماً في نهاية الجملة) بخلاف السامية الغربية (VSO).",
      explanationEn = "Due to Sumerian contact, Akkadian adopted final verb placement (SOV), contrasting with West Semitic VSO."
    ),
    QuizQuestion(
      id = "q-03",
      languageOrBranch = "العربية الجنوبية القديمة (المسند)",
      questionAr = "كم عدد الحروف الصامتة في خط المسند العربي الجنوبي (السبئي والمعيني والقتباني)؟",
      questionEn = "How many consonant letters comprise the monumental South Arabian Musnad alphabet?",
      optionsAr = listOf(
        "29 حرفاً صامتاً",
        "22 حرفاً صامتاً",
        "30 حرفاً مسمارياً",
        "26 حرفاً مقطعياً"
      ),
      optionsEn = listOf(
        "29 consonants",
        "22 linear letters",
        "30 cuneiform signs",
        "26 syllabic graphs"
      ),
      correctIndex = 0,
      explanationAr = "خط المسند اليمني يضم 29 حرفاً صامتاً، محتفظاً بكافة أصوات السامية الأم التسعة والعشرين.",
      explanationEn = "The South Arabian Musnad alphabet preserves all 29 Proto-Semitic consonantal phonemes."
    ),
    QuizQuestion(
      id = "q-04",
      languageOrBranch = "السامية الإثيوبية (الجعزية)",
      questionAr = "ما الذي يميز نظام كتابة الفيدل (Fidel) الجعزي عن الأبجديات السامية الشمالية الصامتة (Abjads)؟",
      questionEn = "How does the Ethiopic Fidel writing system differ fundamentally from Northwest Semitic abjads?",
      optionsAr = listOf(
        "نظام مقطعي (Abugida) تُلحق فيه حركات الصوائت السبع بجسم الصامت نفسه",
        "كتابة تصويرية لوغوغرافية بحتة بدون أصوات",
        "كتابة من اليمين إلى اليسار فقط بدون فواصل",
        "خلوه التام من الأصوات القذفية والحلقية"
      ),
      optionsEn = listOf(
        "It is an Abugida where 7 vowel qualities are inscribed by altering the consonant sign",
        "It is purely ideographic logograms without sound values",
        "Written strictly right-to-left without word dividers",
        "Lacks all ejectives and pharyngeal consonants"
      ),
      correctIndex = 0,
      explanationAr = "الفيدل نظام مقطعي (Abugida) يُكتب من اليسار إلى اليمين وتتعدل بنية الحرف الصامت وفق 7 رتب صوتية حركية.",
      explanationEn = "Ethiopic Fidel is an Abugida written left-to-right with 7 vowel orders modifying the base consonant glyph."
    )
  )
}
