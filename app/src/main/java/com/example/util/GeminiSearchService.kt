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

data class GroundingSource(
  val title: String,
  val url: String
)

data class GeminiSearchResult(
  val text: String,
  val searchQueries: List<String> = emptyList(),
  val sources: List<GroundingSource> = emptyList(),
  val isGroundingUsed: Boolean = false,
  val isSuccess: Boolean = true,
  val errorMessage: String? = null
)

object GeminiSearchService {
  private const val MODEL = "gemini-3.5-flash"
  private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

  private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  suspend fun queryWithGoogleSearch(
    prompt: String,
    systemPrompt: String = "أنت بروفيسور خبير ومحقق أثري متخصص في الفيلولوجيا السامية، النقوش الإبيغرافية المقارنة، والآثار الشرق أوسطية القديمة. استخدم بيانات محرك بحث Google لتقديم معلومات دقيقة، تاريخية وموثقة مع الإشارة للمصادر والحفريات.",
    useSearchGrounding: Boolean = true
  ): GeminiSearchResult = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY
    if (apiKey.isNullOrBlank()) {
      return@withContext GeminiSearchResult(
        text = "مفتاح API الخاص بـ Gemini غير مُعدّ حالياً. يمكنك تفعيله عبر لوحة Secrets في AI Studio ليتمكن التطبيق من إجراء بحث جوجل المباشر.",
        isSuccess = false,
        errorMessage = "GEMINI_API_KEY is missing"
      )
    }

    try {
      val url = "$BASE_URL/$MODEL:generateContent?key=$apiKey"

      val jsonBody = JSONObject().apply {
        val contentsArray = JSONArray().apply {
          put(JSONObject().apply {
            put("parts", JSONArray().apply {
              put(JSONObject().put("text", prompt))
            })
          })
        }
        put("contents", contentsArray)

        put("systemInstruction", JSONObject().apply {
          put("parts", JSONArray().apply {
            put(JSONObject().put("text", systemPrompt))
          })
        })

        if (useSearchGrounding) {
          val toolsArray = JSONArray().apply {
            put(JSONObject().apply {
              put("googleSearch", JSONObject())
            })
          }
          put("tools", toolsArray)
        }
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
        val msg = errorJson?.optJSONObject("error")?.optString("message") ?: "HTTP ${response.code}: $responseString"
        return@withContext GeminiSearchResult(
          text = "تعذر استدعاء الذكاء الاصطناعي مع Google Search: $msg",
          isSuccess = false,
          errorMessage = msg
        )
      }

      val json = JSONObject(responseString)
      val candidates = json.optJSONArray("candidates")
      if (candidates == null || candidates.length() == 0) {
        return@withContext GeminiSearchResult(
          text = "لم يتم الحصول على إجابة من النموذج.",
          isSuccess = false,
          errorMessage = "Empty candidates"
        )
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

      val searchQueries = mutableListOf<String>()
      val sources = mutableListOf<GroundingSource>()
      var isGroundingUsed = false

      val groundingMetadata = candidate.optJSONObject("groundingMetadata")
      if (groundingMetadata != null) {
        val webSearchQueries = groundingMetadata.optJSONArray("webSearchQueries")
        if (webSearchQueries != null) {
          for (i in 0 until webSearchQueries.length()) {
            searchQueries.add(webSearchQueries.getString(i))
          }
        }

        val groundingChunks = groundingMetadata.optJSONArray("groundingChunks")
        if (groundingChunks != null) {
          for (i in 0 until groundingChunks.length()) {
            val chunk = groundingChunks.getJSONObject(i)
            val web = chunk.optJSONObject("web")
            if (web != null) {
              val title = web.optString("title", "مرجع ومصدر بحثي")
              val uri = web.optString("uri", "")
              if (uri.isNotBlank()) {
                sources.add(GroundingSource(title = title, url = uri))
              }
            }
          }
        }

        if (searchQueries.isNotEmpty() || sources.isNotEmpty()) {
          isGroundingUsed = true
        }
      }

      GeminiSearchResult(
        text = textBuilder.toString().ifBlank { "تمت معالجة الطلب بنجاح." },
        searchQueries = searchQueries,
        sources = sources.distinctBy { it.url },
        isGroundingUsed = isGroundingUsed,
        isSuccess = true
      )
    } catch (e: Exception) {
      Log.e("GeminiSearchService", "Error in Gemini API call", e)
      GeminiSearchResult(
        text = "حدث خطأ أثناء الاتصال: ${e.localizedMessage}",
        isSuccess = false,
        errorMessage = e.message
      )
    }
  }
}
