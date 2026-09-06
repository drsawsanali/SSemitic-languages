package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.ui.theme.LocalCustomColors
import com.example.util.AudioEngine
import com.example.util.ChatMessage
import com.example.util.ChatSender
import com.example.util.RobertChatService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RobertChatScreen(
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val customColors = LocalCustomColors.current
  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()
  val audioEngine = remember { AudioEngine(context) }

  DisposableEffect(Unit) {
    onDispose { audioEngine.release() }
  }

  var inputText by remember { mutableStateOf("") }
  var isLoading by remember { mutableStateOf(false) }
  var showClearConfirm by remember { mutableStateOf(false) }
  var showSemiticKeyboard by remember { mutableStateOf(false) }

  val messages = remember {
    mutableStateListOf(
      ChatMessage(
        sender = ChatSender.ROBERT,
        content = """
أهلاً بك! أنا "روبرت" (Robert) 🤖، رفيقك الأكاديمي والباحث الذكي المتخصص في علوم اللغات السامية، الإبيغرافيا، وفك رموز النقوش القديمة.

كيف يمكنني مساعدتك اليوم في أبحاثك؟
• تحليل ونقحرة النقوش القديمة (مسلة ميشع، نقش صرواح، مسلة حمورابي...).
• القوانين الصوتية وتطور الجذور السامية المشتركة.
• تاريخ الممالك والمدن السامية في الشرق الأدنى القديم.
""".trimIndent()
      )
    )
  }

  val suggestedPrompts = remember {
    listOf(
      "حلل لي مطلع مسلة ميشع المؤابية",
      "ما هي شروط حدوث التحول الكنعاني؟",
      "اشرح خصائص خط المسند السبئي",
      "ما الفرق بين البابلية والآشورية؟",
      "تتبع جذر كلمة 'ملك' في اللغات السامية",
      "ما هي أهم مميزات اللغة الجعزية؟"
    )
  }

  val semiticQuickGlyphs = remember {
    listOf(
      "𐤀" to "ألف فينيقي",
      "𐤁" to "بيت فينيقي",
      "𐤌" to "ميم فينيقي",
      "𐤔" to "شين فينيقي",
      "𐤟" to "فاصل فينيقي",
      "𐩠" to "هاء مسند",
      "𐩡" to "لام مسند",
      "𐩣" to "ميم مسند",
      "𐩿" to "فاصل مسند",
      "𐎀" to "ألف أوغاريتي",
      "𐎎" to "ميم أوغاريتي",
      "𐡀" to "ألف آرامي",
      "𒀭" to "دينجر مسماري"
    )
  }

  fun sendUserMessage(text: String) {
    val trimmed = text.trim()
    if (trimmed.isBlank() || isLoading) return

    val userMsg = ChatMessage(
      sender = ChatSender.USER,
      content = trimmed
    )
    messages.add(userMsg)
    inputText = ""
    isLoading = true

    coroutineScope.launch {
      listState.animateScrollToItem(messages.size - 1)
      val reply = RobertChatService.sendMessage(
        history = messages.toList(),
        newUserPrompt = trimmed
      )
      messages.add(
        ChatMessage(
          sender = ChatSender.ROBERT,
          content = reply
        )
      )
      isLoading = false
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header Banner
    Surface(
      color = customColors.cardBackground,
      shadowElevation = 3.dp
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(44.dp)
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
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "المساعد الذكي روبرت (Robert)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.primaryContainer
              ) {
                Text(
                  text = "Free AI",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(if (isLoading) MaterialTheme.colorScheme.tertiary else Color(0xFF4CAF50))
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isLoading) "روبرت يحلل ويكتب الإجابة..." else "متصل ومستعد للمحادثة الفيلولوجية",
                fontSize = 11.sp,
                color = customColors.mutedText
              )
            }
          }
        }

        IconButton(onClick = { showClearConfirm = true }) {
          Icon(
            Icons.Default.DeleteOutline,
            contentDescription = "مسح المحادثة",
            tint = customColors.mutedText
          )
        }
      }
    }

    // Suggested Quick Prompts Bar
    Surface(
      color = customColors.cardBackground.copy(alpha = 0.6f),
      modifier = Modifier.fillMaxWidth()
    ) {
      LazyRow(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(suggestedPrompts) { prompt ->
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.clickable { sendUserMessage(prompt) }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = prompt,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }

    // Chat Message List
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
      contentPadding = PaddingValues(vertical = 12.dp)
    ) {
      items(messages, key = { it.id }) { msg ->
        ChatMessageItem(
          message = msg,
          onSpeak = { text -> audioEngine.speak(text) },
          onCopy = { text ->
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Robert AI Reply", text))
            Toast.makeText(context, "تم نسخ النص", Toast.LENGTH_SHORT).show()
          },
          onShare = { text ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(Intent.EXTRA_TEXT, text)
            }
            context.startActivity(Intent.createChooser(shareIntent, "مشاركة إجابة روبرت"))
          }
        )
      }

      if (isLoading) {
        item {
          Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.SmartToy,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            CircularProgressIndicator(
              modifier = Modifier.size(16.dp),
              strokeWidth = 2.dp,
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "روبرت يستحضر المعطيات الفيلولوجية...",
              fontSize = 12.sp,
              color = customColors.mutedText
            )
          }
        }
      }
    }

    // Quick Semitic Glyphs Toolbar
    Surface(
      color = customColors.cardBackground,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column {
        LazyRow(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          items(semiticQuickGlyphs) { (glyph, desc) ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              modifier = Modifier.clickable {
                inputText += glyph
              }
            ) {
              Text(
                text = glyph,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

        // Bottom Input Row
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Toggle Semitic Virtual Keyboard
          IconButton(
            onClick = { showSemiticKeyboard = !showSemiticKeyboard },
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(
                if (showSemiticKeyboard) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
              )
          ) {
            Icon(
              Icons.Default.Keyboard,
              contentDescription = "لوحة مفاتيح الخطوط السامية القديمة",
              tint = if (showSemiticKeyboard) MaterialTheme.colorScheme.primary else customColors.mutedText,
              modifier = Modifier.size(22.dp)
            )
          }

          Spacer(modifier = Modifier.width(6.dp))

          OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            placeholder = { Text("اسأل روبرت عن أي نقش أو لغة أو جذر...") },
            modifier = Modifier
              .weight(1f)
              .padding(end = 8.dp),
            shape = RoundedCornerShape(24.dp),
            maxLines = 4,
            textStyle = MaterialTheme.typography.bodyMedium
          )

          IconButton(
            onClick = { sendUserMessage(inputText) },
            enabled = inputText.isNotBlank() && !isLoading,
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(
                if (inputText.isNotBlank() && !isLoading) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant
              )
          ) {
            Icon(
              Icons.Default.Send,
              contentDescription = "إرسال",
              tint = if (inputText.isNotBlank() && !isLoading) Color.White else customColors.mutedText,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        // Full Semitic Virtual Keyboard Panel
        AnimatedVisibility(
          visible = showSemiticKeyboard,
          enter = expandVertically() + fadeIn(),
          exit = shrinkVertically() + fadeOut()
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .heightIn(max = 380.dp)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            SemiticVirtualKeyboard(
              onKeyClick = { key ->
                inputText += key.char
              },
              onBackspace = {
                if (inputText.isNotEmpty()) {
                  inputText = inputText.dropLast(1)
                }
              },
              onClear = {
                inputText = ""
              },
              onPresetInsert = { preset ->
                inputText = preset
              },
              onPlayChime = { f1, f2 ->
                audioEngine.playAcousticFormantChime(f1, f2)
              },
              currentInputText = inputText
            )
          }
        }
      }
    }
  }

  // Clear Confirmation Dialog
  if (showClearConfirm) {
    AlertDialog(
      onDismissRequest = { showClearConfirm = false },
      title = { Text("مسح سجل المحادثة") },
      text = { Text("هل ترغب في بدء محادثة جديدة مع روبرت ومسح الرسائل الحالية؟") },
      confirmButton = {
        TextButton(
          onClick = {
            messages.clear()
            messages.add(
              ChatMessage(
                sender = ChatSender.ROBERT,
                content = "أهلاً بك مجدداً! أنا روبرت، جاهز لأي استفسار أو تحليل نقش أثري جديد."
              )
            )
            showClearConfirm = false
          }
        ) {
          Text("مسح", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { showClearConfirm = false }) {
          Text("إلغاء")
        }
      }
    )
  }
}

@Composable
fun ChatMessageItem(
  message: ChatMessage,
  onSpeak: (String) -> Unit,
  onCopy: (String) -> Unit,
  onShare: (String) -> Unit
) {
  val customColors = LocalCustomColors.current
  val isUser = message.sender == ChatSender.USER
  val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
  val timeString = remember(message.timestamp) { timeFormatter.format(Date(message.timestamp)) }

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
  ) {
    if (!isUser) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.Default.SmartToy,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
    }

    Column(
      horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
      modifier = Modifier.widthIn(max = 310.dp)
    ) {
      Surface(
        shape = RoundedCornerShape(
          topStart = 16.dp,
          topEnd = 16.dp,
          bottomStart = if (isUser) 16.dp else 4.dp,
          bottomEnd = if (isUser) 4.dp else 16.dp
        ),
        color = if (isUser) MaterialTheme.colorScheme.primary
        else customColors.cardBackground,
        border = if (!isUser) androidx.compose.foundation.BorderStroke(
          1.dp,
          MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        ) else null,
        shadowElevation = 2.dp
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = message.content,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            lineHeight = 22.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(3.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = timeString,
          fontSize = 9.sp,
          color = customColors.mutedText
        )

        if (!isUser) {
          IconButton(
            onClick = { onSpeak(message.content) },
            modifier = Modifier.size(22.dp)
          ) {
            Icon(
              Icons.Default.VolumeUp,
              contentDescription = "استماع",
              tint = customColors.mutedText,
              modifier = Modifier.size(14.dp)
            )
          }

          IconButton(
            onClick = { onCopy(message.content) },
            modifier = Modifier.size(22.dp)
          ) {
            Icon(
              Icons.Default.ContentCopy,
              contentDescription = "نسخ",
              tint = customColors.mutedText,
              modifier = Modifier.size(14.dp)
            )
          }

          IconButton(
            onClick = { onShare(message.content) },
            modifier = Modifier.size(22.dp)
          ) {
            Icon(
              Icons.Default.Share,
              contentDescription = "مشاركة",
              tint = customColors.mutedText,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }
    }

    if (isUser) {
      Spacer(modifier = Modifier.width(8.dp))
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.Default.Person,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSecondaryContainer,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
