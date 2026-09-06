package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EpigraphicGlossaryComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlossaryScreen(
  onBack: () -> Unit = {},
  onOpenDrawer: () -> Unit = {},
  onNavigateToRobertChat: (String) -> Unit = {},
  onNavigateToInscriptions: () -> Unit = {}
) {
  var showInfoDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "المسرد الإبيغرافي والمفاهيم اللغوية",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Epigraphic & Linguistic Glossary",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onOpenDrawer) {
            Icon(Icons.Default.Menu, contentDescription = "القائمة")
          }
        },
        actions = {
          IconButton(onClick = { showInfoDialog = true }) {
            Icon(Icons.Default.Info, contentDescription = "عن المسرد")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      EpigraphicGlossaryComponent(
        modifier = Modifier.fillMaxSize(),
        onAskRobertAi = { prompt ->
          onNavigateToRobertChat(prompt)
        },
        onNavigateToInscriptions = onNavigateToInscriptions
      )
    }

    if (showInfoDialog) {
      AlertDialog(
        onDismissRequest = { showInfoDialog = false },
        title = {
          Text(
            text = "المسرد الإبيغرافي الأكاديمي",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
        },
        text = {
          Text(
            text = "يضم هذا المسرد أهم المصطلحات التخصصية في علم النقوش والكتابات القديمة (Epigraphy)، وقوانين التحول الصوتي الفونولوجي (Sound Shifts)، والمورفولوجيا والنحو المقارن، ومواد التدوين الأثرية في الحضارات واللغات السامية القديمة.\n\nيمكنك البحث بأي كلمة، والاستماع للنطق والتعريف الصوتي، ومناقشة تفاصيل أي مصطلح مباشرة مع المساعد الذكي روبرت.",
            fontSize = 14.sp,
            lineHeight = 22.sp
          )
        },
        confirmButton = {
          Button(onClick = { showInfoDialog = false }) {
            Text("حسناً")
          }
        }
      )
    }
  }
}
