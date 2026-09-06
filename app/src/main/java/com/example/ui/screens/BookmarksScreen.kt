package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entity.BookmarkEntity
import com.example.ui.theme.LocalCustomColors
import com.example.ui.viewmodel.BookmarkViewModel
import com.example.util.AudioEngine
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
  modifier: Modifier = Modifier,
  onBack: (() -> Unit)? = null,
  onNavigateToInscriptions: (() -> Unit)? = null,
  onNavigateToGlossary: (() -> Unit)? = null,
  bookmarkViewModel: BookmarkViewModel = viewModel()
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  val audioEngine = remember { AudioEngine(context) }

  val filteredBookmarks by bookmarkViewModel.filteredBookmarks.collectAsStateWithLifecycle()
  val totalCount by bookmarkViewModel.totalCount.collectAsStateWithLifecycle()
  val inscriptionCount by bookmarkViewModel.inscriptionCount.collectAsStateWithLifecycle()
  val glossaryCount by bookmarkViewModel.glossaryCount.collectAsStateWithLifecycle()
  val filterState by bookmarkViewModel.filterState.collectAsStateWithLifecycle()
  val allCollections by bookmarkViewModel.allCollections.collectAsStateWithLifecycle()

  var selectedBookmarkForDetail by remember { mutableStateOf<BookmarkEntity?>(null) }
  var selectedBookmarkForNotes by remember { mutableStateOf<BookmarkEntity?>(null) }
  var selectedBookmarkForCollection by remember { mutableStateOf<BookmarkEntity?>(null) }
  var showCreateCollectionDialog by remember { mutableStateOf(false) }
  var collectionInputText by remember { mutableStateOf("") }
  var noteTextState by remember { mutableStateOf("") }
  var showClearAllConfirm by remember { mutableStateOf(false) }

  DisposableEffect(Unit) {
    onDispose {
      audioEngine.release()
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      Surface(
        color = customColors.cardBackground,
        shadowElevation = 3.dp
      ) {
        Column(modifier = Modifier.fillMaxWidth().statusBarsPadding()) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (onBack != null) {
                IconButton(onClick = onBack) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "رجوع"
                  )
                }
              }
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Bookmark,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "المحفوظات والمجموعات (Room)",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "مجموعة النقوش والمفاهيم الإبيغرافية المحفوظة محلياً",
                  style = MaterialTheme.typography.bodySmall,
                  color = customColors.mutedText,
                  fontSize = 11.sp
                )
              }
            }

            // Top action buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (filteredBookmarks.isNotEmpty()) {
                IconButton(
                  onClick = {
                    val citationsText = filteredBookmarks.joinToString("\n\n---\n\n") { b ->
                      val typeLabel = if (b.itemType == "INSCRIPTION") "[نقش أثري]" else "[مصطلح إبيغرافي]"
                      "$typeLabel ${b.titleAr} (${b.titleEn})\nالتصنيف: ${b.categoryOrBranch}\n${b.snippetAr}\nالشاهد / المرجع: ${b.citation}"
                    }
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Academic Bookmarks", citationsText))
                    Toast.makeText(context, "تم نسخ توثيق جميع المحفوظات للحافظة", Toast.LENGTH_SHORT).show()
                  }
                ) {
                  Icon(
                    Icons.Default.ContentCopy,
                    contentDescription = "نسخ كل التوثيقات",
                    tint = MaterialTheme.colorScheme.primary
                  )
                }

                IconButton(
                  onClick = { showClearAllConfirm = true }
                ) {
                  Icon(
                    Icons.Default.DeleteSweep,
                    contentDescription = "مسح الكل",
                    tint = MaterialTheme.colorScheme.error
                  )
                }
              }
            }
          }

          // Statistics and Room DB Status row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            BookmarkStatBadge(
              label = "الإجمالي",
              value = totalCount.toString(),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.weight(1f)
            )
            BookmarkStatBadge(
              label = "النقوش",
              value = inscriptionCount.toString(),
              color = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.weight(1f)
            )
            BookmarkStatBadge(
              label = "المسرد",
              value = glossaryCount.toString(),
              color = MaterialTheme.colorScheme.tertiary,
              modifier = Modifier.weight(1f)
            )
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                .padding(horizontal = 8.dp, vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Room SQLite",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          // Search Field
          OutlinedTextField(
            value = filterState.query,
            onValueChange = { bookmarkViewModel.updateQuery(it) },
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 4.dp),
            placeholder = { Text("ابحث في المحفوظات بالاسم، الخط، أو الملاحظة...", fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
              if (filterState.query.isNotEmpty()) {
                IconButton(onClick = { bookmarkViewModel.updateQuery("") }) {
                  Icon(Icons.Default.Clear, contentDescription = "مسح", modifier = Modifier.size(16.dp))
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.surface,
              unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
          )

          // Filter Segmented Row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FilterChip(
              selected = filterState.selectedType == "ALL",
              onClick = { bookmarkViewModel.selectType("ALL") },
              label = { Text("الكل ($totalCount)", fontSize = 11.sp) },
              leadingIcon = {
                if (filterState.selectedType == "ALL") {
                  Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                }
              }
            )

            FilterChip(
              selected = filterState.selectedType == "INSCRIPTION",
              onClick = { bookmarkViewModel.selectType("INSCRIPTION") },
              label = { Text("النقوش والمسلات ($inscriptionCount)", fontSize = 11.sp) },
              leadingIcon = {
                Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(14.dp))
              }
            )

            FilterChip(
              selected = filterState.selectedType == "GLOSSARY",
              onClick = { bookmarkViewModel.selectType("GLOSSARY") },
              label = { Text("المسرد والمفاهيم ($glossaryCount)", fontSize = 11.sp) },
              leadingIcon = {
                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
              }
            )
          }

          // Collections Filter Row
          LazyRow(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            item {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.FolderSpecial,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "المجموعات:",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }

            item {
              FilterChip(
                selected = filterState.selectedCollection == "ALL",
                onClick = { bookmarkViewModel.selectCollection("ALL") },
                label = { Text("الكل", fontSize = 11.sp) }
              )
            }

            items(allCollections) { coll ->
              FilterChip(
                selected = filterState.selectedCollection == coll,
                onClick = { bookmarkViewModel.selectCollection(coll) },
                label = { Text(coll, fontSize = 11.sp) },
                leadingIcon = {
                  Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(12.dp))
                }
              )
            }

            item {
              AssistChip(
                onClick = {
                  collectionInputText = ""
                  showCreateCollectionDialog = true
                },
                label = { Text("+ مجموعة جديدة", fontSize = 11.sp) },
                leadingIcon = {
                  Icon(Icons.Default.CreateNewFolder, contentDescription = null, modifier = Modifier.size(13.dp))
                }
              )
            }
          }
        }
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .background(MaterialTheme.colorScheme.background)
    ) {
      if (filteredBookmarks.isEmpty()) {
        EmptyBookmarksView(
          hasBookmarksAtAll = totalCount > 0,
          onResetFilter = {
            bookmarkViewModel.updateQuery("")
            bookmarkViewModel.selectType("ALL")
          },
          onNavigateToInscriptions = onNavigateToInscriptions,
          onNavigateToGlossary = onNavigateToGlossary
        )
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(14.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(filteredBookmarks, key = { it.id }) { bookmark ->
            BookmarkItemCard(
              bookmark = bookmark,
              onCardClick = { selectedBookmarkForDetail = bookmark },
              onSpeak = {
                val textToSpeak = "${bookmark.titleAr}. ${bookmark.snippetAr}"
                audioEngine.speak(textToSpeak)
              },
              onEditNotes = {
                selectedBookmarkForNotes = bookmark
                noteTextState = bookmark.customNotes
              },
              onMoveCollection = {
                selectedBookmarkForCollection = bookmark
                collectionInputText = bookmark.collectionName
              },
              onDelete = {
                bookmarkViewModel.removeBookmark(bookmark.id)
                Toast.makeText(context, "تم حذف العنصر من المحفوظات", Toast.LENGTH_SHORT).show()
              },
              onCopyCitation = {
                val citation = "${bookmark.titleAr} (${bookmark.titleEn}) - ${bookmark.categoryOrBranch}: ${bookmark.snippetAr}. [مرجع: ${bookmark.citation}]"
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Citation", citation))
                Toast.makeText(context, "تم نسخ التوثيق الأكاديمي", Toast.LENGTH_SHORT).show()
              }
            )
          }

          item {
            Spacer(modifier = Modifier.height(30.dp))
          }
        }
      }
    }
  }

  // Detail Modal
  if (selectedBookmarkForDetail != null) {
    val bookmark = selectedBookmarkForDetail!!
    AlertDialog(
      onDismissRequest = { selectedBookmarkForDetail = null },
      title = {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column {
            Text(
              text = bookmark.titleAr,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
            Text(
              text = bookmark.titleEn,
              fontSize = 12.sp,
              color = customColors.mutedText
            )
          }
          TypeBadge(itemType = bookmark.itemType)
        }
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          if (bookmark.originalScriptText.isNotBlank()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
                .padding(10.dp)
            ) {
              Text(
                text = bookmark.originalScriptText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
              )
            }
          }

          if (bookmark.transliteration.isNotBlank()) {
            Text(
              text = "النقحرة: ${bookmark.transliteration}",
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.secondary
            )
          }

          Text(
            text = bookmark.snippetAr,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
          )

          if (bookmark.siteOrWitness.isNotBlank()) {
            Text(
              text = "الشاهد الأثري / الموقع: ${bookmark.siteOrWitness}",
              fontSize = 11.sp,
              color = customColors.mutedText
            )
          }

          if (bookmark.citation.isNotBlank()) {
            Text(
              text = "التوثيق: ${bookmark.citation}",
              fontSize = 11.sp,
              color = customColors.mutedText
            )
          }

          if (bookmark.customNotes.isNotBlank()) {
            Surface(
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              shape = RoundedCornerShape(8.dp)
            ) {
              Column(modifier = Modifier.padding(8.dp)) {
                Text(
                  text = "ملاحظاتي الخاصة:",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
                Text(
                  text = bookmark.customNotes,
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val textToSpeak = "${bookmark.titleAr}. ${bookmark.snippetAr}"
            audioEngine.speak(textToSpeak)
          }
        ) {
          Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("استماع صوتي")
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedBookmarkForDetail = null }) {
          Text("إغلاق")
        }
      }
    )
  }

  // Edit Notes Dialog
  if (selectedBookmarkForNotes != null) {
    val b = selectedBookmarkForNotes!!
    AlertDialog(
      onDismissRequest = { selectedBookmarkForNotes = null },
      title = {
        Text("ملاحظات الباحث على: ${b.titleAr}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
      },
      text = {
        Column {
          Text(
            text = "أضف ملاحظاتك أو تحليلاتك الفيلولوجية الخاصة حول هذا العنصر ليتم حفظها في قاعدة بيانات Room المحلية:",
            fontSize = 12.sp,
            color = customColors.mutedText,
            lineHeight = 18.sp
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = noteTextState,
            onValueChange = { noteTextState = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("اكتب ملاحظاتك هنا...", fontSize = 12.sp) },
            minLines = 3,
            maxLines = 6
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            bookmarkViewModel.updateNotes(b.id, noteTextState)
            selectedBookmarkForNotes = null
            Toast.makeText(context, "تم حفظ الملاحظة", Toast.LENGTH_SHORT).show()
          }
        ) {
          Text("حفظ الملاحظة")
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedBookmarkForNotes = null }) {
          Text("إلغاء")
        }
      }
    )
  }

  // Move to / Assign Collection Dialog
  if (selectedBookmarkForCollection != null) {
    val b = selectedBookmarkForCollection!!
    AlertDialog(
      onDismissRequest = { selectedBookmarkForCollection = null },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.DriveFileMove, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(8.dp))
          Text("تحديد مجموعة الحفظ")
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "اختر مجموعة بحثية لـ «${b.titleAr}» أو اكتب اسماً جديداً:",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text("المجموعات المقترحة:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            val quickOptions = (allCollections + listOf("العامة", "دراسات مسندية", "أبحاث كنعانية")).distinct().take(3)
            quickOptions.forEach { collName ->
              SuggestionChip(
                onClick = { collectionInputText = collName },
                label = { Text(collName, fontSize = 10.sp) }
              )
            }
          }
          OutlinedTextField(
            value = collectionInputText,
            onValueChange = { collectionInputText = it },
            label = { Text("اسم المجموعة") },
            placeholder = { Text("مثال: نقوش الألواح المسمارية") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val target = collectionInputText.trim().ifBlank { "العامة" }
            bookmarkViewModel.moveToCollection(b.id, target)
            selectedBookmarkForCollection = null
            Toast.makeText(context, "تم نقل العنصر إلى مجموعة: $target", Toast.LENGTH_SHORT).show()
          }
        ) {
          Text("تأكيد النقل")
        }
      },
      dismissButton = {
        TextButton(onClick = { selectedBookmarkForCollection = null }) {
          Text("إلغاء")
        }
      }
    )
  }

  // Create Collection Dialog
  if (showCreateCollectionDialog) {
    AlertDialog(
      onDismissRequest = { showCreateCollectionDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CreateNewFolder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(8.dp))
          Text("إنشاء مجموعة جديدة")
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "اكتب اسم المجموعة لتصفية وعزل النقوش والمصطلحات التابعة لها:",
            fontSize = 12.sp
          )
          OutlinedTextField(
            value = collectionInputText,
            onValueChange = { collectionInputText = it },
            label = { Text("اسم المجموعة الجديدة") },
            placeholder = { Text("مثال: دراسات سبئية ومسندية") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val newCol = collectionInputText.trim()
            if (newCol.isNotBlank()) {
              bookmarkViewModel.selectCollection(newCol)
              showCreateCollectionDialog = false
              Toast.makeText(context, "تم تفعيل التصفية بمجموعة: $newCol", Toast.LENGTH_SHORT).show()
            }
          }
        ) {
          Text("تفعيل المجموعة")
        }
      },
      dismissButton = {
        TextButton(onClick = { showCreateCollectionDialog = false }) {
          Text("إلغاء")
        }
      }
    )
  }

  // Clear All Confirmation Dialog
  if (showClearAllConfirm) {
    AlertDialog(
      onDismissRequest = { showClearAllConfirm = false },
      title = { Text("تأكيد مسح كافة المحفوظات") },
      text = {
        Text("هل أنت متأكد من رغبتك في حذف جميع النقوش والمصطلحات المحفوظة في قاعدة بيانات Room؟ لا يمكن التراجع عن هذا الإجراء.")
      },
      confirmButton = {
        Button(
          onClick = {
            bookmarkViewModel.clearAll()
            showClearAllConfirm = false
            Toast.makeText(context, "تم مسح جميع المحفوظات", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("مسح الكل")
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearAllConfirm = false }) {
          Text("إلغاء")
        }
      }
    )
  }
}

@Composable
private fun BookmarkItemCard(
  bookmark: BookmarkEntity,
  onCardClick: () -> Unit,
  onSpeak: () -> Unit,
  onEditNotes: () -> Unit,
  onMoveCollection: () -> Unit,
  onDelete: () -> Unit,
  onCopyCitation: () -> Unit
) {
  val customColors = LocalCustomColors.current
  val dateFormatted = remember(bookmark.timestamp) {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    sdf.format(Date(bookmark.timestamp))
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onCardClick),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = customColors.cardBackground),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top row: Type badge, Category, Collection badge, and Date
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          TypeBadge(itemType = bookmark.itemType)
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = bookmark.categoryOrBranch,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Collection chip
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f))
              .clickable { onMoveCollection() }
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Folder,
                contentDescription = null,
                modifier = Modifier.size(11.dp),
                tint = MaterialTheme.colorScheme.tertiary
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = bookmark.collectionName.ifBlank { "العامة" },
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
              )
            }
          }
        }

        Text(
          text = dateFormatted,
          fontSize = 10.sp,
          color = customColors.mutedText
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Title & English subtitle
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = bookmark.titleAr,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          if (bookmark.titleEn.isNotBlank()) {
            Text(
              text = bookmark.titleEn,
              style = MaterialTheme.typography.bodySmall,
              color = customColors.mutedText
            )
          }
        }

        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.Default.BookmarkRemove,
            contentDescription = "إزالة من المحفوظات",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      // Original Ancient Script Text (if available)
      if (bookmark.originalScriptText.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, customColors.border, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Text(
            text = bookmark.originalScriptText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // Snippet / Summary text
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = bookmark.snippetAr,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
        lineHeight = 18.sp,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
      )

      // User custom notes indicator if present
      if (bookmark.customNotes.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            Icons.Default.EditNote,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "ملاحظة: ${bookmark.customNotes}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // Actions bottom row
      Spacer(modifier = Modifier.height(8.dp))
      Divider(color = customColors.border.copy(alpha = 0.5f))
      Spacer(modifier = Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          // Audio
          OutlinedButton(
            onClick = onSpeak,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(30.dp)
          ) {
            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("نطق", fontSize = 11.sp)
          }

          // Edit Notes
          OutlinedButton(
            onClick = onEditNotes,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(30.dp)
          ) {
            Icon(Icons.Default.Notes, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (bookmark.customNotes.isBlank()) "ملاحظة +" else "تعديل", fontSize = 11.sp)
          }

          // Move Collection
          OutlinedButton(
            onClick = onMoveCollection,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(30.dp)
          ) {
            Icon(Icons.Default.Folder, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("مجموعة", fontSize = 11.sp)
          }

          // Copy Citation
          IconButton(
            onClick = onCopyCitation,
            modifier = Modifier.size(30.dp)
          ) {
            Icon(
              Icons.Default.ContentCopy,
              contentDescription = "نسخ التوثيق",
              modifier = Modifier.size(16.dp),
              tint = customColors.mutedText
            )
          }
        }

        TextButton(
          onClick = onCardClick,
          contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text("عرض التفاصيل ←", fontSize = 11.sp)
        }
      }
    }
  }
}

@Composable
private fun TypeBadge(itemType: String) {
  val isInscription = itemType == "INSCRIPTION"
  val bgColor = if (isInscription) {
    MaterialTheme.colorScheme.secondaryContainer
  } else {
    MaterialTheme.colorScheme.tertiaryContainer
  }
  val textColor = if (isInscription) {
    MaterialTheme.colorScheme.onSecondaryContainer
  } else {
    MaterialTheme.colorScheme.onTertiaryContainer
  }
  val label = if (isInscription) "📜 نقش أثري" else "📖 مصطلح إبيغرافي"

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(6.dp))
      .background(bgColor)
      .padding(horizontal = 7.dp, vertical = 3.dp)
  ) {
    Text(
      text = label,
      fontSize = 10.sp,
      fontWeight = FontWeight.Bold,
      color = textColor
    )
  }
}

@Composable
private fun BookmarkStatBadge(
  label: String,
  value: String,
  color: androidx.compose.ui.graphics.Color,
  modifier: Modifier = Modifier
) {
  val customColors = LocalCustomColors.current
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    color = customColors.cardBackground,
    border = androidx.compose.foundation.BorderStroke(1.dp, customColors.border)
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = value,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = color
      )
      Text(
        text = label,
        fontSize = 10.sp,
        color = customColors.mutedText
      )
    }
  }
}

@Composable
private fun EmptyBookmarksView(
  hasBookmarksAtAll: Boolean,
  onResetFilter: () -> Unit,
  onNavigateToInscriptions: (() -> Unit)?,
  onNavigateToGlossary: (() -> Unit)?
) {
  val customColors = LocalCustomColors.current

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(72.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.BookmarkBorder,
        contentDescription = null,
        modifier = Modifier.size(36.dp),
        tint = MaterialTheme.colorScheme.primary
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
      text = if (hasBookmarksAtAll) "لا توجد عناصر تطابق معايير البحث" else "لا توجد محفوظات حتى الآن",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = if (hasBookmarksAtAll)
        "جرب تعديل كلمات البحث أو تبديل التصنيف لعرض باقي العناصر."
      else
        "يمكنك حفظ أي نقش أثري أو مصطلح إبيغرافي بسهولة أثناء تصفح المعرض الأثري أو المسرد للرجوع السريع إليه لاحقاً، وحفظه في قاعدة بيانات Room المحلية.",
      style = MaterialTheme.typography.bodySmall,
      textAlign = TextAlign.Center,
      color = customColors.mutedText,
      lineHeight = 20.sp
    )

    Spacer(modifier = Modifier.height(20.dp))

    if (hasBookmarksAtAll) {
      Button(onClick = onResetFilter) {
        Text("إعادة ضبط الفلاتر")
      }
    } else {
      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        if (onNavigateToInscriptions != null) {
          Button(onClick = onNavigateToInscriptions) {
            Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("تصفح النقوش")
          }
        }

        if (onNavigateToGlossary != null) {
          OutlinedButton(onClick = onNavigateToGlossary) {
            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("تصفح المسرد")
          }
        }
      }
    }
  }
}
