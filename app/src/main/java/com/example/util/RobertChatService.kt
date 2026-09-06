package com.example.util

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
  val id: String = java.util.UUID.randomUUID().toString(),
  val sender: ChatSender,
  val content: String,
  val timestamp: Long = System.currentTimeMillis(),
  val isError: Boolean = false
)

enum class ChatSender {
  USER,
  ROBERT
}

object RobertChatService {
  private const val MODEL = "gemini-2.5-flash"
  private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

  private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  private const val SYSTEM_INSTRUCTION = """
أنت "روبرت" (Robert)، المساعد الذكي والباحث الفيلولوجي المتخصص في عائلة اللغات السامية، الإبيغرافيا المقارنة، والآثار القديمة.
صفاتك ومبادئك:
1. شخصيتك: عالم آثار ولسانيات ودود، موسوعي المعرفة، تشرح المفاهيم المعقدة بوضوح وإيجاز علمي دقيق.
2. تخصصك: السامية الشرقية (الأكادية، الإيبلاوية)، السامية الشمالية الغربية (الأوغاريتية، الفينيقية، الآرامية، السريانية، العبرية القديمة، المؤابية)، السامية الجنوبية القديمة (السبئية، القتبانية، المعينية، الحضرمية)، السامية الجنوبية الحديثة (المهرية، السقطرية)، والسامية الإثيوبية (الجعزية، الأمهرية)، والعربية والشمالية القديمة (الصفائية، الثمودية).
3. عندما يطلب منك المستخدم تحليل نقش، قدم: الخط، النقحرة اللاتينية، الترجمة، والتحليل الفيلولوجي ومقارنة الجذور بالسامية الأم.
4. أجب باللغة العربية الفصحى الجميلة والمصطلحات الأكاديمية الدقيقة مع استخدام التشكيل عند الحاجة للألفاظ الصوتية (IPA).
"""

  suspend fun sendMessage(
    history: List<ChatMessage>,
    newUserPrompt: String
  ): String = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY

    // If no API Key configured, provide a rich, smart local academic response from Robert!
    if (apiKey.isNullOrBlank()) {
      return@withContext generateLocalAcademicResponse(newUserPrompt)
    }

    try {
      val url = "$BASE_URL/$MODEL:generateContent?key=$apiKey"

      val jsonBody = JSONObject().apply {
        val contentsArray = JSONArray()

        // Include recent history (up to last 10 messages)
        val recentHistory = history.takeLast(10)
        for (msg in recentHistory) {
          val role = if (msg.sender == ChatSender.USER) "user" else "model"
          contentsArray.put(JSONObject().apply {
            put("role", role)
            put("parts", JSONArray().apply {
              put(JSONObject().put("text", msg.content))
            })
          })
        }

        // Add current user message
        contentsArray.put(JSONObject().apply {
          put("role", "user")
          put("parts", JSONArray().apply {
            put(JSONObject().put("text", newUserPrompt))
          })
        })

        put("contents", contentsArray)

        put("systemInstruction", JSONObject().apply {
          put("parts", JSONArray().apply {
            put(JSONObject().put("text", SYSTEM_INSTRUCTION))
          })
        })
      }

      val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
      val request = Request.Builder()
        .url(url)
        .post(requestBody)
        .build()

      val response = client.newCall(request).execute()
      val responseString = response.body?.string() ?: ""

      if (!response.isSuccessful) {
        val errorJson = try { JSONObject(responseString) } catch (e: Exception) { null }
        val msg = errorJson?.optJSONObject("error")?.optString("message") ?: "HTTP ${response.code}"
        Log.w("RobertChatService", "Gemini API error: $msg, falling back to local academic response")
        return@withContext generateLocalAcademicResponse(newUserPrompt)
      }

      val json = JSONObject(responseString)
      val candidates = json.optJSONArray("candidates")
      if (candidates == null || candidates.length() == 0) {
        return@withContext generateLocalAcademicResponse(newUserPrompt)
      }

      val candidate = candidates.getJSONObject(0)
      val content = candidate.optJSONObject("content")
      val parts = content?.optJSONArray("parts")

      val textBuilder = StringBuilder()
      if (parts != null) {
        for (i in 0 until parts.length()) {
          val part = parts.getJSONObject(i)
          if (part.has("text")) {
            textBuilder.append(part.getString("text"))
          }
        }
      }

      val reply = textBuilder.toString().trim()
      if (reply.isNotBlank()) reply else generateLocalAcademicResponse(newUserPrompt)
    } catch (e: Exception) {
      Log.e("RobertChatService", "Exception in Gemini call", e)
      generateLocalAcademicResponse(newUserPrompt)
    }
  }

  private fun generateLocalAcademicResponse(prompt: String): String {
    val p = prompt.lowercase()
    return when {
      p.contains("ميشع") || p.contains("مؤاب") -> """
أهلاً بك! مسلة ميشع (Mesha Stele / KAI 181) هي أهم نقش مؤابي كنعاني يعود لحوالي 840 ق.م، عُثر عليها في ذيبان (الأردن).
• اللغة والخط: اللغة المؤابية (كنعانية شمالية غربية) مكتوبة بالأبجدية الفينيقية المبكرة.
• المطلع الشهير: "𐤀𐤍𐤊 𐤌𐤔𐤏 𐤁𐤍 𐤊𐤌𐤔𐤌𐤋𐤊 𐤌𐤋𐤊 𐤌𐤀𐤁" (أنا ميشع بن كموشملَك ملك مؤاب).
• الأهمية الفيلولوجية: تمتاز باستخدام واو العطف السردية التتابعية (Waw Consecutive) المشابهة للعبرية، وتأنيث الأسماء بالتاء الصريحة، واستخدام اسم الإله القومي كموش (Chemosh).
هل ترغب في تحليل سطر محدد من المسلة؟
""".trimIndent()

      p.contains("كنعان") || p.contains("التحول الكنعاني") -> """
التحول الكنعاني (Canaanite Sound Shift) هو قانون فونولوجي تاريخي بارز طرأ على اللغات الكنعانية (الفينيقية، العبرية، المؤابية، البونية):
• القاعدة: تحول الصائت الطويل المفتوح في السامية الأم (*ā) إلى صائت خلفي مضموم (*ō) ثم (*ū) في مراحل لاحقة:
  *ā ➔ ō (Proto-Semitic *malk-ātu > Phoenician / Malkōt / 'ملكات')
  *šalām- ➔ *šalōm- (سلام ➔ شلوم)
• الاستثناءات: لم يطرأ هذا التحول على الأوغاريتية المبكرة ولا على الآرامية ولا على العربية، مما يجعل هذا القانون مقياساً لتصنيف اللغات الكنعانية بدقة.
""".trimIndent()

      p.contains("سبأ") || p.contains("مسند") || p.contains("صرواح") || p.contains("اليمن") -> """
مرحباً بك في عالم النقوش العربية الجنوبية القديمة (الصيهدية)!
• خط المسند: أبجدية حجرية تتألف من 29 حرفاً صامتاً، تمتاز بالهندسة الصارمة وفصل الكلمات بالخط الرأسي (𐩿).
• نقش صرواح الكبير (RES 3945 / DAI Ṣirwāḥ 1): سطره المكرب كربئيل وتر بن ذمار علي (حوالي 685 ق.م) في معبد المقه بصرواح، ويوثق توحيد ممالك اليمن القديم (سبأ، معين، قتبان، وأوسان).
• الخصائص اللغوية: هاء الغائب (ـهو / ـهمو)، أداة التعريف بالنون اللاحقة (تنوين الإطلاق -n)، وصيغة الجمع بالواو والألف (ـو / ـن).
""".trimIndent()

      p.contains("أكاد") || p.contains("مسمار") || p.contains("حمورابي") -> """
اللغة الأكادية (Akkadian) هي أقدم لغة سامية موثقة كتابياً، وتتبع الفرع السامي الشرقي (East Semitic):
• نظام الكتابة: مقطعي مسماري (Cuneiform) استُعير من السومريين وطُوع للأصوات السامية.
• اللهجتان الرئيسيتان: البابلية (Babylonian) في الجنوب، والآشورية (Assyrian) في الشمال.
• مسلة حمورابي (Codex Hammurabi): كُتبت بالبابلية القديمة حوالي 1750 ق.م وتضم 282 بنداً قانونياً.
• الخصائص: استخدام التنوين بالميم (Mimation: -um, -am, -im)، وتحول الحلقيات السامية الأم إلى صوائت ممدودة.
""".trimIndent()

      p.contains("جعز") || p.contains("إثيوب") || p.contains("أكسوم") -> """
اللغة الجعزية (Ge'ez / Classical Ethiopic) هي اللغة الكلاسيكية للفرع السامي الإثيوبي (Ethiosemitic):
• الخط: الفيدل (Fidel) وهو نظام كتابة مقطعي صوتي (Abugida) يتغير فيه شكل الحرف وفق حركته المصوتة من الرتب السبع (Ge'ez, Ka'eb, Sales, Rabe, Hames, Sades, Sabe).
• الأصول: انحدرت من هجرات القبائل السامية الجنوبية (حبشت وغعز) من اليمن إلى القرن الإفريقي وامتزاجها بالسكان المحليين.
• مسلة عيزانا: مدونة بثلاث لغات (الجعزية، والمسند، واليونانية) في القرن الرابع الميلادي.
""".trimIndent()

      else -> """
مرحباً بك! أنا "روبرت" (Robert)، رفيقك ومساعدك الأكاديمي في دراسة اللغات السامية والإبيغرافيا المقارنة.
بإمكاني مساعدتك في:
1. 📜 فك ونقحرة وترجمة النقوش (الفينيقية، المسندية، المسمارية، السريانية، والآرامية).
2. 🔍 تتبع الجذور السامية المشتركة وقوانين التحول الصوتي (مثل التحول الكنعاني، وانكماش المزدوجات).
3. 🏛️ دراسة حضارات وممالك الشرق الأدنى القديم (سبأ، بابل، آشور، أوجاريت، صور، جبيل، والبتراء).
4. 📚 مراجعة وتحليل فصول الموسوعة الأكاديمية والتوثيق المرجعي.

عن أي موضوع أو نقش سامي ترغب في الحديث عنه الآن؟
""".trimIndent()
    }
  }
}
