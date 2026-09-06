package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.sin

class AudioEngine(context: Context) : TextToSpeech.OnInitListener {
  private var tts: TextToSpeech? = null
  private var isInitialized = false
  private val mainHandler = Handler(Looper.getMainLooper())
  private val isPlayingSequential = AtomicBoolean(false)
  private var activeSequentialThread: Thread? = null

  init {
    tts = TextToSpeech(context.applicationContext, this)
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      val result = tts?.setLanguage(Locale("ar"))
      if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
        tts?.setLanguage(Locale.US)
      }
      isInitialized = true
    }
  }

  /**
   * Translates academic Semitic transliteration diacritics into speakable phonemes
   * for accurate TTS phonetic articulation.
   */
  fun normalizeSemiticTransliteration(rawText: String): String {
    var text = rawText.trim()
    // Remove reconstructed Proto-Semitic asterisk
    if (text.startsWith("*")) {
      text = text.substring(1)
    }
    // Remove suffixes like trailing hyphen
    if (text.endsWith("-")) {
      text = text.dropLast(1)
    }

    // Replace academic transliteration symbols with phonetic equivalents
    return text
      .replace("ʾ", "ء")
      .replace("ʼ", "ء")
      .replace("’", "ء")
      .replace("ʿ", "ع")
      .replace("ʻ", "ع")
      .replace("‘", "ع")
      .replace("ṭ", "ط")
      .replace("Ṭ", "ط")
      .replace("ṣ", "ص")
      .replace("Ṣ", "ص")
      .replace("ḍ", "ض")
      .replace("Ḍ", "ض")
      .replace("ẓ", "ظ")
      .replace("Ẓ", "ظ")
      .replace("ḥ", "ح")
      .replace("Ḥ", "ح")
      .replace("ḫ", "خ")
      .replace("Ḫ", "خ")
      .replace("x", "خ")
      .replace("ẖ", "خ")
      .replace("š", "ش")
      .replace("Š", "ش")
      .replace("ś", "س")
      .replace("Ś", "س")
      .replace("ġ", "غ")
      .replace("Ġ", "غ")
      .replace("q", "ق")
      .replace("Q", "ق")
      .replace("ḏ", "ذ")
      .replace("Ḏ", "ذ")
      .replace("ṯ", "ث")
      .replace("Ṯ", "ث")
      .replace("ā", "ا")
      .replace("ē", "اي")
      .replace("ī", "ي")
      .replace("ō", "او")
      .replace("ū", "و")
      .replace("â", "ا")
      .replace("ê", "اي")
      .replace("î", "ي")
      .replace("ô", "او")
      .replace("û", "و")
      .replace("/", "")
      .replace("[", "")
      .replace("]", "")
  }

  fun speak(text: String, speed: Float = 1.0f, pitch: Float = 1.0f, onDone: () -> Unit = {}) {
    if (!isInitialized) {
      onDone()
      return
    }
    tts?.setSpeechRate(speed)
    tts?.setPitch(pitch)
    val params = Bundle()
    val utteranceId = "utterance_${System.currentTimeMillis()}"
    params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
      override fun onStart(id: String?) {}
      override fun onDone(id: String?) {
        if (id == utteranceId) {
          mainHandler.post { onDone() }
        }
      }
      @Deprecated("Deprecated in Java")
      override fun onError(id: String?) {
        if (id == utteranceId) {
          mainHandler.post { onDone() }
        }
      }
    })
    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
  }

  /**
   * Dual-engine pronunciation: plays an acoustic resonant formant chime for the language branch,
   * then articulates the normalized phonetic reconstruction.
   */
  fun speakPhoneticReconstruction(
    rawWord: String,
    languageName: String = "Semitic",
    speed: Float = 0.9f,
    pitch: Float = 1.0f,
    playChimeFirst: Boolean = true,
    onDone: () -> Unit = {}
  ) {
    val normalized = normalizeSemiticTransliteration(rawWord)
    val frequency = getFrequencyForLanguage(languageName)

    if (playChimeFirst) {
      playProtoSemiticChime(frequency = frequency, durationMs = 260)
      mainHandler.postDelayed({
        speak(normalized, speed = speed, pitch = pitch, onDone = onDone)
      }, 280)
    } else {
      speak(normalized, speed = speed, pitch = pitch, onDone = onDone)
    }
  }

  /**
   * Sequential Autoplay: Iterates through the historical phonetic evolution across
   * Semitic branches (Proto-Semitic -> Akkadian -> Ugaritic -> Phoenician -> Hebrew -> Aramaic -> Sabaic -> Ge'ez -> Arabic).
   */
  fun playSequentialEvolution(
    steps: List<Pair<String, String>>, // Pair<LanguageName, WordForm>
    speed: Float = 0.9f,
    onStepChanged: (stepIndex: Int, language: String, word: String) -> Unit,
    onComplete: () -> Unit
  ) {
    stop()
    if (steps.isEmpty()) {
      onComplete()
      return
    }

    isPlayingSequential.set(true)
    activeSequentialThread = Thread {
      for (i in steps.indices) {
        if (!isPlayingSequential.get()) break
        val (lang, word) = steps[i]

        mainHandler.post {
          onStepChanged(i, lang, word)
        }

        val stepDone = AtomicBoolean(false)
        mainHandler.post {
          speakPhoneticReconstruction(
            rawWord = word,
            languageName = lang,
            speed = speed,
            pitch = if (lang.contains("Proto", ignoreCase = true)) 0.9f else 1.0f,
            playChimeFirst = true,
            onDone = {
              stepDone.set(true)
            }
          )
        }

        // Wait for utterance with timeout
        val startWait = System.currentTimeMillis()
        while (!stepDone.get() && isPlayingSequential.get() && (System.currentTimeMillis() - startWait < 3000)) {
          try {
            Thread.sleep(80)
          } catch (_: InterruptedException) {
            break
          }
        }

        // Interval pause between language evolution stages
        if (isPlayingSequential.get()) {
          try {
            Thread.sleep(450)
          } catch (_: InterruptedException) {
            break
          }
        }
      }

      mainHandler.post {
        isPlayingSequential.set(false)
        onComplete()
      }
    }
    activeSequentialThread?.start()
  }

  private fun getFrequencyForLanguage(lang: String): Int {
    return when {
      lang.contains("Proto", ignoreCase = true) -> 520
      lang.contains("Akkad", ignoreCase = true) || lang.contains("أكاد", ignoreCase = true) -> 440
      lang.contains("Ugarit", ignoreCase = true) || lang.contains("أوغار", ignoreCase = true) -> 480
      lang.contains("Phoenic", ignoreCase = true) || lang.contains("فينبق", ignoreCase = true) -> 550
      lang.contains("Hebrew", ignoreCase = true) || lang.contains("عبر", ignoreCase = true) -> 580
      lang.contains("Aramaic", ignoreCase = true) || lang.contains("آرام", ignoreCase = true) -> 500
      lang.contains("Sabaic", ignoreCase = true) || lang.contains("سبئ", ignoreCase = true) -> 460
      lang.contains("Ge'ez", ignoreCase = true) || lang.contains("جعز", ignoreCase = true) -> 490
      lang.contains("Arabic", ignoreCase = true) || lang.contains("عرب", ignoreCase = true) -> 530
      else -> 520
    }
  }

  fun stop() {
    isPlayingSequential.set(false)
    activeSequentialThread?.interrupt()
    activeSequentialThread = null
    tts?.stop()
  }

  fun playAcousticFormantChime(f1: Int = 500, f2: Int = 1500, durationMs: Int = 400) {
    Thread {
      try {
        val sampleRate = 44100
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
          val time = i.toDouble() / sampleRate
          val envelope = 1.0 - (i.toDouble() / numSamples) // decay envelope
          val wave = (sin(2.0 * Math.PI * f1 * time) * 0.6 + sin(2.0 * Math.PI * f2 * time) * 0.4) * envelope
          buffer[i] = (wave * Short.MAX_VALUE * 0.5).toInt().toShort()
        }

        val audioTrack = AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_MEDIA)
              .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
              .build()
          )
          .setAudioFormat(
            AudioFormat.Builder()
              .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
              .setSampleRate(sampleRate)
              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
              .build()
          )
          .setBufferSizeInBytes(buffer.size * 2)
          .setTransferMode(AudioTrack.MODE_STATIC)
          .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()
        Thread.sleep(durationMs.toLong())
        audioTrack.release()
      } catch (e: Exception) {
        Log.e("AudioEngine", "Formant chime failed", e)
      }
    }.start()
  }

  fun playProtoSemiticChime(frequency: Int = 520, durationMs: Int = 350) {
    playAcousticFormantChime(f1 = frequency, f2 = (frequency * 1.5).toInt(), durationMs = durationMs)
  }

  fun release() {
    stop()
    tts?.shutdown()
  }
}
