package com.example.data.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class EvaluationResult(
    val score: Int,
    val verdict: String,
    val accuracyScore: Int,
    val terminologyScore: Int,
    val reasoningScore: Int,
    val fluencyScore: Int,
    val feedback: String,
    val recommendations: String,
    val isZeroPenalty: Boolean = false
)

data class AppealResult(
    val isSuccess: Boolean,
    val reasoning: String,
    val scoreAdjustment: Int
)

object AiLegalJudge {

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    suspend fun evaluateTheoryExam(topic: String, transcript: String): EvaluationResult = withContext(Dispatchers.IO) {
        val trimmed = transcript.trim()
        if (trimmed.isBlank() || trimmed.length < 5) {
            return@withContext buildZeroResult("Cavab təqdim edilməmişdir və ya boşdur. Azərbaycan qanunvericiliyinə dair heç bir hüquqi əsaslandırma tapılmadı.")
        }

        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    Sən Azərbaycan Respublikasının Qanunvericiliyi üzrə Dövlət İmtahan Mərkəzi və Vəkillər Kollegiyasının Ali Ekspert-Münsifisən.
                    Tələbəyə verilən mövzu: "$topic"
                    Tələbənin cavabı: "$trimmed"

                    QİYMƏTLƏNDİRMƏ VƏ CƏZA TƏLƏBLƏRİ:
                    1. Əgər təqdim edilən cavab mövzuya tamamilə aidiyyətsizdirsə, boşdursa, qeyri-ciddidirsə və ya cəfəngiyatdırsa (məs: "asdf", "bilmirəm", "salam", "123", təsadüfi sözlər), DƏRHAL VƏ MÜTLƏQ DƏQİQ 0 XAL VER. Heç bir təsəlli və ya standart keçid balı vermə!
                    2. Əgər cavab mövzuya aiddirsə, onun hüquqi məntiqini, arqumentasiyasını, AR Konstitusiyası və müvafiq Məcəllələrə uyğunluğunu qiymətləndir (0-100 aralığında).

                    Yalnız və yalnız aşağıdakı JSON formatında cavab ver:
                    {
                      "score": 0,
                      "accuracyScore": 0,
                      "terminologyScore": 0,
                      "reasoningScore": 0,
                      "fluencyScore": 0,
                      "verdict": "Qeyri-kafi (0 Xal)",
                      "feedback": "Cavab mövzuya tamamilə aidiyyətsizdir...",
                      "recommendations": "Mövzunu AR Qanunvericiliyi üzrə dərindən təkrarlayın.",
                      "isZeroPenalty": true
                    }
                    Qeyd: Xallar: accuracyScore (0-35), terminologyScore (0-25), reasoningScore (0-25), fluencyScore (0-15).
                    Cəm score = accuracyScore + terminologyScore + reasoningScore + fluencyScore.
                    Əgər score == 0 olarsa, isZeroPenalty true olmalıdır.
                """.trimIndent()

                val result = callGeminiApi(prompt, apiKey)
                if (result != null) return@withContext result
            } catch (e: Exception) {
                // Fall back to rigorous legal evaluator
            }
        }

        evaluateLocally(topic, trimmed, isCase = false)
    }

    suspend fun evaluateCaseStudy(caseTitle: String, caseDescription: String, userSolution: String): EvaluationResult = withContext(Dispatchers.IO) {
        val trimmed = userSolution.trim()
        if (trimmed.isBlank() || trimmed.length < 5) {
            return@withContext buildZeroResult("Kazusun həlli təqdim edilməyib və ya boşdur.")
        }

        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    Sən Azərbaycan Respublikası Məhkəmə-Hüquq Şurasının kazus qiymətləndirmə komissiyasının rəhbərisən.
                    Kazus: "$caseTitle"
                    Faktlar: "$caseDescription"
                    Hüquqşünasın həlli: "$trimmed"

                    QİYMƏTLƏNDİRMƏ TƏLƏBİ:
                    1. Cavab aidiyyətsiz, boş, mənasız və ya qeyri-ciddidirsə DƏQİQ 0 XAL VER (score: 0).
                    2. Cavab uyğundursa AR qanunvericiliyinə (maddələr, prosessual qaydalar, məhkəmə presedentləri) əsaslanaraq 0-100 bal ver.

                    Yalnız aşağıdakı JSON formatında cavab qaytar:
                    {
                      "score": 82,
                      "accuracyScore": 28,
                      "terminologyScore": 22,
                      "reasoningScore": 20,
                      "fluencyScore": 12,
                      "verdict": "Müvəffəqiyyətli",
                      "feedback": "...",
                      "recommendations": "...",
                      "isZeroPenalty": false
                    }
                """.trimIndent()

                val result = callGeminiApi(prompt, apiKey)
                if (result != null) return@withContext result
            } catch (e: Exception) {
                // Fall back
            }
        }

        evaluateLocally(caseTitle, trimmed, isCase = true)
    }

    suspend fun reviewAppeal(
        examTitle: String,
        userAnswer: String,
        wager: Int,
        justification: String
    ): AppealResult = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    Sən Azərbaycan Respublikası Vəkillər Kollegiyasının Müstəqil Apellyasiya Kollegiyasının sədrisən.
                    Məsələ: "$examTitle"
                    İlkin cavab: "$userAnswer"
                    Qoyulan mərc: $wager xal
                    Apellyasiya əsaslandırması: "$justification"

                    Şikayəti obyektiv qiymətləndir. Əsaslıdırsa təmin et, əsassızdırsa rədd et.
                    Yalnız JSON formatında cavab ver:
                    {
                      "isSuccess": true,
                      "scoreAdjustment": 10,
                      "reasoning": "..."
                    }
                """.trimIndent()

                val jsonStr = callGeminiRaw(prompt, apiKey)
                if (jsonStr != null) {
                    val cleaned = jsonStr.substringAfter("{").substringBeforeLast("}")
                    val obj = JSONObject("{$cleaned}")
                    return@withContext AppealResult(
                        isSuccess = obj.optBoolean("isSuccess", true),
                        reasoning = obj.optString("reasoning", "Şikayətə baxıldı və qərar qəbul edildi."),
                        scoreAdjustment = obj.optInt("scoreAdjustment", 10)
                    )
                }
            } catch (e: Exception) {
                // Fall back
            }
        }

        val words = justification.trim().split("\\s+".toRegex()).size
        val hasLegalTerms = justification.contains("maddə", ignoreCase = true) ||
                justification.contains("məcəllə", ignoreCase = true) ||
                justification.contains("hüquq", ignoreCase = true) ||
                justification.contains("qanun", ignoreCase = true)

        val isSuccess = words >= 12 && hasLegalTerms
        AppealResult(
            isSuccess = isSuccess,
            reasoning = if (isSuccess) {
                "Apellyasiya Kollegiyası təqdim olunan $wager xallıq mərci və hüquqi əsaslandırmanı təmin etdi. Qoyulan mərc qaytarıldı və əlavə xal qazanıldı."
            } else {
                "Apellyasiya Kollegiyası şikayəti əsassız hesab etdi: yetərli qanunvericilik norması göstərilməyib. Qoyulan $wager xal silindi."
            },
            scoreAdjustment = if (isSuccess) 10 else 0
        )
    }

    private fun buildZeroResult(reason: String): EvaluationResult {
        return EvaluationResult(
            score = 0,
            verdict = "Qeyri-kafi (0 Xal)",
            accuracyScore = 0,
            terminologyScore = 0,
            reasoningScore = 0,
            fluencyScore = 0,
            feedback = reason,
            recommendations = "Mənasız, boş və ya mövzuya tamamilə aidiyyətsiz cavab verildiyi üçün 10 dəqiqəlik cəza məhdudiyyəti tətbiq olunmuşdur.",
            isZeroPenalty = true
        )
    }

    private fun evaluateLocally(title: String, text: String, isCase: Boolean): EvaluationResult {
        val trimmed = text.trim()
        val words = trimmed.split("\\s+".toRegex()).filter { it.isNotBlank() }

        val legalKeywords = listOf(
            "maddə", "məcəllə", "qanun", "konstitusiya", "əqd", "öhdəlik", "zərər",
            "mülkiyyət", "iddia", "məhkəmə", "cinayət", "inzibati", "hüquq", "təqsir",
            "restitusiya", "notariat", "icraat", "zəruri müdafiə", "əmək müqaviləsi",
            "ştat ixtisarı", "plenum", "sübut", "vicdanlı", "əsassız varlanma", "tövsif"
        )

        var matchedKeywords = 0
        for (kw in legalKeywords) {
            if (trimmed.contains(kw, ignoreCase = true)) {
                matchedKeywords++
            }
        }

        // Strict 0-point detection: if no legal keywords and fewer than 10 words, or nonsense text
        if (matchedKeywords == 0 && words.size < 12) {
            return buildZeroResult("Təqdim edilən mətndə heç bir hüquqi termin və ya qanunvericilik anlayışı aşkar edilmədi. Cavab qeyri-kafi və aidiyyətsizdir.")
        }

        val accuracy = when {
            matchedKeywords >= 6 -> 32
            matchedKeywords >= 4 -> 26
            matchedKeywords >= 2 -> 20
            matchedKeywords >= 1 -> 14
            else -> 6
        }

        val terminology = when {
            matchedKeywords >= 5 -> 23
            matchedKeywords >= 3 -> 18
            matchedKeywords >= 1 -> 13
            else -> 5
        }

        val reasoning = when {
            words.size >= 60 -> 23
            words.size >= 35 -> 18
            words.size >= 15 -> 13
            else -> 6
        }

        val fluency = when {
            words.size >= 40 -> 14
            words.size >= 20 -> 11
            words.size >= 10 -> 8
            else -> 4
        }

        val totalScore = (accuracy + terminology + reasoning + fluency).coerceIn(0, 96)
        if (totalScore <= 20 && matchedKeywords == 0) {
            return buildZeroResult("Cavab hüquqi baxımdan əsassızdır və mövzuya uyğun deyil.")
        }

        val verdict = when {
            totalScore >= 85 -> "Müvəffəqiyyətli (Yüksək Dərəcə)"
            totalScore >= 70 -> "Müvəffəqiyyətli (Yaxşı)"
            totalScore >= 55 -> "Kafi (Keçid Balı)"
            else -> "Qeyri-kafi"
        }

        val feedback = when {
            totalScore >= 80 -> "Cavabınız AR qanunvericiliyinə yüksək uyğunluq nümayiş etdirir. Hüquqi kateqoriyalar, anlayışlar və tənzimləmə mexanizmləri dəqiq ifadə olunub."
            totalScore >= 60 -> "Mövzunun ümumi mahiyyəti düzgün qavranılıb, lakin konkret qanunvericilik normalarına və maddələrə daha aydın istinadlar edilməsi tövsiyə olunur."
            else -> "Cavabda hüquqi terminologiya və AR qanunvericiliyinin normalarına əsaslandırma zəifdir. Mövzunu yenidən nəzərdən keçirin."
        }

        val recommendations = if (isCase) {
            "Məhkəmə təcrübəsində analoji kazuslara baxılma qaydasını, xüsusilə Ali Məhkəmənin Plenum qərarlarındakı izahları nəzərə alın."
        } else {
            "AR Mülki və Cinayət Məcəllələrinin ümumi və xüsusi hissələrindəki əsas normaları cavabınızda daha qabarıq göstərin."
        }

        return EvaluationResult(
            score = totalScore,
            verdict = verdict,
            accuracyScore = accuracy,
            terminologyScore = terminology,
            reasoningScore = reasoning,
            fluencyScore = fluency,
            feedback = feedback,
            recommendations = recommendations,
            isZeroPenalty = totalScore == 0
        )
    }

    private fun callGeminiApi(prompt: String, apiKey: String): EvaluationResult? {
        val raw = callGeminiRaw(prompt, apiKey) ?: return null
        return try {
            val jsonPart = raw.substringAfter("{").substringBeforeLast("}")
            val json = JSONObject("{$jsonPart}")
            val score = json.optInt("score", 0)
            val isPenalty = json.optBoolean("isZeroPenalty", score == 0)
            EvaluationResult(
                score = score,
                verdict = json.optString("verdict", if (score == 0) "Qeyri-kafi (0 Xal)" else "Müvəffəqiyyətli"),
                accuracyScore = json.optInt("accuracyScore", 0),
                terminologyScore = json.optInt("terminologyScore", 0),
                reasoningScore = json.optInt("reasoningScore", 0),
                fluencyScore = json.optInt("fluencyScore", 0),
                feedback = json.optString("feedback", if (score == 0) "Cavab mövzuya uyğun deyil." else "Hüquqi izah təqdim edildi."),
                recommendations = json.optString("recommendations", "Qanunvericilik maddələrini mütəmadi təkrarlayın."),
                isZeroPenalty = isPenalty || score == 0
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun callGeminiRaw(prompt: String, apiKey: String): String? {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val jsonPayload = JSONObject().apply {
            put("contents", org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", org.json.JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return null
            val body = response.body?.string() ?: return null
            val respJson = JSONObject(body)
            val candidates = respJson.getJSONArray("candidates")
            val first = candidates.getJSONObject(0)
            val content = first.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            parts.getJSONObject(0).getString("text")
        } catch (e: Exception) {
            null
        }
    }
}
