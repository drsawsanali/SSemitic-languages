package com.example.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.LocalCustomColors
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val titleAr: String, val icon: ImageVector) {
  object Dashboard : Screen("dashboard", "الرئيسية", Icons.Default.Home)
  object RobertChat : Screen("robert_chat", "المساعد الذكي روبرت", Icons.Default.SmartToy)
  object Inscriptions : Screen("inscriptions", "النقوش واللقى", Icons.Default.Description)
  object Languages : Screen("languages", "مصفوفة اللغات", Icons.Default.Language)
  object Chapters : Screen("chapters", "الموسوعة الأكاديمية", Icons.Default.MenuBook)
  object Lexicon : Screen("lexicon", "المعجم السامي", Icons.Default.Spellcheck)
  object Etymology : Screen("etymology", "شبكة التأصيل", Icons.Default.Hub)
  object Phonetics : Screen("phonetics", "محاكي الأصوات", Icons.Default.GraphicEq)
  object Pronunciation : Screen("pronunciation", "مدرب النطق", Icons.Default.Mic)
  object Compare : Screen("compare", "المقارنة المزدوجة", Icons.Default.CompareArrows)
  object Map : Screen("map", "الخرائط الأثرية", Icons.Default.Map)
  object Ocr : Screen("ocr", "التعرف الضوئي OCR", Icons.Default.DocumentScanner)
  object Media : Screen("media", "معرض الوسائط", Icons.Default.Collections)
  object Flashcards : Screen("flashcards", "بطاقات القواعد", Icons.Default.School)
  object InscriptionSearch : Screen("inscription_search", "بحث النقوش (Room)", Icons.Default.ManageSearch)
  object Index : Screen("index", "الكشاف الذكي", Icons.Default.FormatListNumbered)
  object Bibliography : Screen("bibliography", "المراجع والتوثيق", Icons.Default.Bookmarks)
  object Quiz : Screen("quiz", "الاختبارات الفيلولوجية", Icons.Default.Quiz)
  object Lab : Screen("lab", "مختبر الخطوط القديمة", Icons.Default.Keyboard)
  object Glossary : Screen("glossary", "المسرد الإبيغرافي", Icons.Default.MenuBook)
  object Bookmarks : Screen("bookmarks", "المحفوظات (Room)", Icons.Default.Bookmark)
  object UniversalSearch : Screen("universal_search", "البحث الشامل والدلالي", Icons.Default.Search)
  object Books : Screen("books", "خزانة الكتب والمصادر (PDF)", Icons.Default.LibraryBooks)
  object DownloadedLibrary : Screen("downloaded_library", "المكتبة المحمّلة دون إنترنت (Room)", Icons.Default.CloudDone)
}

data class DrawerSection(
  val title: String,
  val screens: List<Screen>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
  onToggleTheme: () -> Unit = {}
) {
  val navController = rememberNavController()
  val customColors = LocalCustomColors.current
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()

  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Dashboard.route

  val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Inscriptions,
    Screen.Languages,
    Screen.Chapters,
    Screen.Map
  )

  val drawerSections = listOf(
    DrawerSection(
      title = "🏛️ الإبيغرافيا والآثار",
      screens = listOf(
        Screen.Bookmarks,
        Screen.Inscriptions,
        Screen.InscriptionSearch,
        Screen.Glossary,
        Screen.Ocr,
        Screen.Media
      )
    ),
    DrawerSection(
      title = "🗣️ اللسانيات المقارنة والأصوات",
      screens = listOf(
        Screen.Languages,
        Screen.Phonetics,
        Screen.Pronunciation,
        Screen.Lexicon,
        Screen.Etymology
      )
    ),
    DrawerSection(
      title = "📚 الموسوعة والأبحاث",
      screens = listOf(
        Screen.DownloadedLibrary,
        Screen.UniversalSearch,
        Screen.Books,
        Screen.Chapters,
        Screen.Compare,
        Screen.Map,
        Screen.Index,
        Screen.Bibliography
      )
    ),
    DrawerSection(
      title = "🎓 التعليم والمختبر الذكي",
      screens = listOf(
        Screen.RobertChat,
        Screen.Flashcards,
        Screen.Quiz,
        Screen.Lab
      )
    )
  )

  val allScreens = listOf(
    Screen.Dashboard,
    Screen.DownloadedLibrary,
    Screen.RobertChat,
    Screen.UniversalSearch,
    Screen.Books,
    Screen.Inscriptions,
    Screen.Languages,
    Screen.Chapters,
    Screen.Lexicon,
    Screen.Etymology,
    Screen.Phonetics,
    Screen.Pronunciation,
    Screen.Compare,
    Screen.Map,
    Screen.Ocr,
    Screen.Media,
    Screen.Flashcards,
    Screen.InscriptionSearch,
    Screen.Index,
    Screen.Bibliography,
    Screen.Quiz,
    Screen.Lab,
    Screen.Glossary,
    Screen.Bookmarks
  )

  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      ModalDrawerSheet(
        modifier = Modifier
          .width(320.dp)
          .fillMaxHeight(),
        drawerContainerColor = customColors.cardBackground
      ) {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ) {
          // Drawer Header
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.verticalGradient(
                  colors = listOf(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                  )
                )
              )
              .padding(18.dp)
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(42.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      Icons.Default.School,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "موسوعة اللغات السامية",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                  )
                  Text(
                    text = "جامعة صنعاء • قسم الآثار",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.mutedText
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(8.dp)) {
                  Text(
                    text = "إشراف: أ.د. أحمد فقعس",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "إعداد: سوسن علي عبدالله الحضوري",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
          }

          Divider()

          // Dashboard Item
          NavigationDrawerItem(
            icon = { Icon(Screen.Dashboard.icon, contentDescription = Screen.Dashboard.titleAr) },
            label = { Text(Screen.Dashboard.titleAr, fontWeight = FontWeight.Bold) },
            selected = currentRoute == Screen.Dashboard.route,
            onClick = {
              scope.launch { drawerState.close() }
              navController.navigate(Screen.Dashboard.route) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
              }
            },
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
          )

          // Categorized Sections
          drawerSections.forEach { section ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = section.title,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            section.screens.forEach { screen ->
              NavigationDrawerItem(
                icon = { Icon(screen.icon, contentDescription = screen.titleAr, modifier = Modifier.size(20.dp)) },
                label = { Text(screen.titleAr, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) },
                selected = currentRoute == screen.route,
                onClick = {
                  scope.launch { drawerState.close() }
                  navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                  }
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  ) {
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Column {
              Text(
                text = "موسوعة النقوش واللغات السامية",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = allScreens.find { it.route == currentRoute }?.titleAr ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
              )
            }
          },
          navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
              Icon(Icons.Default.Menu, contentDescription = "القائمة")
            }
          },
          actions = {
            IconButton(onClick = { navController.navigate(Screen.DownloadedLibrary.route) }) {
              Icon(Icons.Default.CloudDone, contentDescription = "المكتبة المحمّلة دون إنترنت", tint = Color(0xFF2E7D32))
            }
            IconButton(onClick = { navController.navigate(Screen.RobertChat.route) }) {
              Icon(Icons.Default.SmartToy, contentDescription = "المساعد روبرت", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = { navController.navigate(Screen.UniversalSearch.route) }) {
              Icon(Icons.Default.Search, contentDescription = "البحث الشامل")
            }
            IconButton(onClick = { navController.navigate(Screen.InscriptionSearch.route) }) {
              Icon(Icons.Default.ManageSearch, contentDescription = "بحث Room")
            }
            IconButton(onClick = { navController.navigate(Screen.Lab.route) }) {
              Icon(Icons.Default.Keyboard, contentDescription = "لوحة المفاتيح")
            }
            IconButton(onClick = onToggleTheme) {
              Icon(Icons.Default.Palette, contentDescription = "تغيير المظهر")
            }
          },
          colors = TopAppBarDefaults.topAppBarColors(
            containerColor = customColors.cardBackground
          )
        )
      },
      floatingActionButton = {
        if (currentRoute != Screen.RobertChat.route) {
          ExtendedFloatingActionButton(
            onClick = {
              navController.navigate(Screen.RobertChat.route) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
              }
            },
            icon = { Icon(Icons.Default.SmartToy, contentDescription = null) },
            text = { Text("المساعد روبرت", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
          )
        }
      },
      bottomBar = {
        NavigationBar(
          containerColor = customColors.cardBackground,
          tonalElevation = 6.dp
        ) {
          bottomNavItems.forEach { screen ->
            NavigationBarItem(
              icon = { Icon(screen.icon, contentDescription = screen.titleAr) },
              label = { Text(screen.titleAr, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) },
              selected = currentRoute == screen.route,
              onClick = {
                navController.navigate(screen.route) {
                  popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                  launchSingleTop = true
                  restoreState = true
                }
              }
            )
          }
        }
      }
    ) { innerPadding ->
      NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = Modifier.padding(innerPadding),
        enterTransition = {
          fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
          slideInHorizontally(animationSpec = tween(350, easing = FastOutSlowInEasing), initialOffsetX = { 50 })
        },
        exitTransition = {
          fadeOut(animationSpec = tween(250, easing = FastOutSlowInEasing)) +
          slideOutHorizontally(animationSpec = tween(300, easing = FastOutSlowInEasing), targetOffsetX = { -50 })
        },
        popEnterTransition = {
          fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
          slideInHorizontally(animationSpec = tween(350, easing = FastOutSlowInEasing), initialOffsetX = { -50 })
        },
        popExitTransition = {
          fadeOut(animationSpec = tween(250, easing = FastOutSlowInEasing)) +
          slideOutHorizontally(animationSpec = tween(300, easing = FastOutSlowInEasing), targetOffsetX = { 50 })
        }
      ) {
        composable(Screen.Dashboard.route) {
          DashboardScreen(
            onNavigate = { route ->
              navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
              }
            }
          )
        }
        composable(Screen.RobertChat.route) { RobertChatScreen() }
        composable(Screen.UniversalSearch.route) {
          UniversalSearchScreen(
            onNavigate = { route ->
              navController.navigate(route) {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
              }
            }
          )
        }
        composable(Screen.Inscriptions.route) { InscriptionsScreen() }
        composable(Screen.Languages.route) { LanguagesScreen() }
        composable(Screen.Chapters.route) { ChaptersReaderScreen() }
        composable(Screen.Lexicon.route) { ComparativeLexiconScreen() }
        composable(Screen.Etymology.route) { EtymologyGraphScreen() }
        composable(Screen.Phonetics.route) { PhoneticsSimulatorScreen() }
        composable(Screen.Pronunciation.route) { PronunciationCoachScreen() }
        composable(Screen.Compare.route) { CompareDualScreen() }
        composable(Screen.Map.route) { MapExplorerScreen() }
        composable(Screen.Ocr.route) { OcrScannerScreen() }
        composable(Screen.Media.route) { MediaGalleryScreen() }
        composable(Screen.Flashcards.route) { FlashcardsScreen() }
        composable(Screen.InscriptionSearch.route) { InscriptionSearchScreen() }
        composable(Screen.Index.route) { SmartIndexScreen() }
        composable(Screen.Bibliography.route) { InteractiveBibliographyScreen() }
        composable(Screen.Quiz.route) { QuizModuleScreen() }
        composable(Screen.Lab.route) { AiResearchLabScreen() }
        composable(Screen.Glossary.route) {
          GlossaryScreen(
            onBack = { navController.popBackStack() },
            onOpenDrawer = { scope.launch { drawerState.open() } },
            onNavigateToRobertChat = { prompt ->
              navController.navigate(Screen.RobertChat.route)
            },
            onNavigateToInscriptions = {
              navController.navigate(Screen.Inscriptions.route)
            }
          )
        }
        composable(Screen.Bookmarks.route) {
          BookmarksScreen(
            onBack = { navController.popBackStack() },
            onNavigateToInscriptions = {
              navController.navigate(Screen.Inscriptions.route)
            },
            onNavigateToGlossary = {
              navController.navigate(Screen.Glossary.route)
            }
          )
        }
        composable(Screen.Books.route) {
          BooksScreen(
            onNavigateToInscription = { id ->
              navController.navigate(Screen.Inscriptions.route)
            },
            onNavigateToLanguage = { lang ->
              navController.navigate(Screen.Languages.route)
            },
            onNavigateToDownloadedLibrary = {
              navController.navigate(Screen.DownloadedLibrary.route)
            }
          )
        }
        composable(Screen.DownloadedLibrary.route) {
          DownloadedLibraryScreen(
            onBack = { navController.popBackStack() },
            onNavigateToChapter = { id ->
              navController.navigate(Screen.Chapters.route)
            },
            onNavigateToSource = {
              navController.navigate(Screen.Books.route)
            }
          )
        }
      }
    }
  }
}
