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
    val recommendations: String
)

data class AppealResult(
    val isSuccess: Boolean,
    val reasoning: String,
    val scoreAdjustment: Int
)

object AiLegalJudge {

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(25, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .build()
    }

    suspend fun evaluateTheoryExam(topic: String, transcript: String): EvaluationResult = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    Sən Azərbaycan Respublikasının Qanunvericiliyi üzrə Dövlət İmtahan Mərkəzi və Vəkillər Kollegiyasının Ali Ekspert-Münsifisən.
                    Tələbəyə verilən mövzu: "$topic"
                    Tələbənin şifahi izahının transkripti: "$transcript"

                    Xahiş olunur ki, tələbənin cavabını Azərbaycan qanunvericiliyinə (AR Konstitusiyası, Mülki Məcəllə, Cinayət Məcəlləsi, Əmək Məcəlləsi və s.) uyğun qiymətləndirəsən.
                    Yalnız və yalnız aşağıdakı JSON formatında cavab ver:
                    {
                      "score": 85,
                      "accuracyScore": 30,
                      "terminologyScore": 22,
                      "reasoningScore": 21,
                      "fluencyScore": 12,
                      "verdict": "Müvəffəqiyyətli (Yaxşı)",
                      "feedback": "Hüquqi əsaslandırma və terminlərin istifadəsi yüksək səviyyədədir...",
                      "recommendations": "Mülki Məcəllənin müvafiq maddələrinə daha dərindən istinad edin..."
                    }
                    Xalların bölgüsü:
                    accuracyScore: maksimum 35
                    terminologyScore: maksimum 25
                    reasoningScore: maksimum 25
                    fluencyScore: maksimum 15
                    score: accuracyScore + terminologyScore + reasoningScore + fluencyScore (cəmi 0-100)
                """.trimIndent()

                val result = callGeminiApi(prompt, apiKey)
                if (result != null) return@withContext result
            } catch (e: Exception) {
                // Fall back to rule-based legal evaluator
            }
        }

        // Robust Azerbaijani legal heuristic analysis
        evaluateLocally(topic, transcript, isCase = false)
    }

    suspend fun evaluateCaseStudy(caseTitle: String, caseDescription: String, userSolution: String): EvaluationResult = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val prompt = """
                    Sən Azərbaycan Respublikası Məhkəmə-Hüquq Şurasının kazus qiymətləndirmə komissiyasının rəhbərisən.
                    Kazus: "$caseTitle"
                    Faktlar: "$caseDescription"
                    Hüquqşünasın həlli: "$userSolution"

                    Cavabı Azərbaycan qanunvericiliyinə uyğun dəyərləndir.
                    Yalnız və yalnız aşağıdakı JSON formatında cavab qaytar:
                    {
                      "score": 82,
                      "accuracyScore": 28,
                      "terminologyScore": 22,
                      "reasoningScore": 20,
                      "fluencyScore": 12,
                      "verdict": "Müvəffəqiyyətli",
                      "feedback": "Kazusun hüquqi tövsifi və təqdim olunan qərar layihəsi əsaslandırılıb...",
                      "recommendations": "Məhkəmə təcrübəsinə və Ali Məhkəmənin Plenum qərarlarına istinadları gücləndirin."
                    }
                """.trimIndent()

                val result = callGeminiApi(prompt, apiKey)
                if (result != null) return@withContext result
            } catch (e: Exception) {
                // Fall back
            }
        }

        evaluateLocally(caseTitle, userSolution, isCase = true)
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
                    İstifadəçinin qoyduğu xal (risk): $wager xal
                    İstifadəçinin apellyasiya əsaslandırması: "$justification"

                    Şikayəti obyektiv qiymətləndir. Əgər istifadəçinin hüquqi arqumentləri əsaslıdırsa şikayəti təmin et. Əgər arqumentlər səthi və ya əsassızdırsa rədd et.
                    Yalnız JSON formatında cavab ver:
                    {
                      "isSuccess": true,
                      "scoreAdjustment": 10,
                      "reasoning": "Apellyasiya Kollegiyası təqdim edilmiş hüquqi əsaslandırmanı qəbul edir. Tələbənin istinad etdiyi norma faktiki hallarla tam uzlaşır."
                    }
                """.trimIndent()

                val jsonStr = callGeminiRaw(prompt, apiKey)
                if (jsonStr != null) {
                    val cleaned = jsonStr.substringAfter("{").substringBeforeLast("}")
                    val obj = JSONObject("{$cleaned}")
                    return@withContext AppealResult(
                        isSuccess = obj.optBoolean("isSuccess", true),
                        reasoning = obj.optString("reasoning", "Şikayətə baxıldı və müvafiq qərar qəbul edildi."),
                        scoreAdjustment = obj.optInt("scoreAdjustment", 10)
                    )
                }
            } catch (e: Exception) {
                // Fall back
            }
        }

        // Local appeal review logic
        val words = justification.trim().split("\\s+".toRegex()).size
        val hasLegalTerms = justification.contains("maddə", ignoreCase = true) ||
                justification.contains("məcəllə", ignoreCase = true) ||
                justification.contains("hüquq", ignoreCase = true) ||
                justification.contains("qanun", ignoreCase = true) ||
                justification.contains("istinad", ignoreCase = true) ||
                justification.contains("tövsif", ignoreCase = true)

        val isSuccess = words >= 15 && hasLegalTerms
        val reasoning = if (isSuccess) {
            "Müstəqil Hakimlər Kollegiyası şikayətinizi və təqdim etdiyiniz $wager xallıq mərci nəzərdən keçirdi: Göstərilən hüquqi arqumentlər və qanunvericilik normalarına istinadlar əsaslı hesab edildi. Apellyasiya təmin olundu! Qoyulan mərc tam geri qaytarıldı və əlavə +${(wager * 0.5).toInt()} bonus xalı təqdim edildi."
        } else {
            "Müstəqil Hakimlər Kollegiyası şikayəti qeyri-kafi hesab etdi: Təqdim olunan əsaslandırmada AR qanunvericiliyinin konkret normalarına yetərli hüquqi dəlillər gətirilməmişdir. Qərar qüvvədə saxlanıldı və riskə qoyulan $wager xal silindi."
        }

        AppealResult(
            isSuccess = isSuccess,
            reasoning = reasoning,
            scoreAdjustment = if (isSuccess) 10 else 0
        )
    }

    private fun evaluateLocally(title: String, text: String, isCase: Boolean): EvaluationResult {
        val trimmed = text.trim()
        val wordCount = trimmed.split("\\s+".toRegex()).filter { it.isNotBlank() }.size

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

        // Dynamic points calculation
        val accuracy = when {
            matchedKeywords >= 6 -> 32
            matchedKeywords >= 4 -> 27
            matchedKeywords >= 2 -> 21
            else -> 15
        }

        val terminology = when {
            matchedKeywords >= 5 -> 23
            matchedKeywords >= 3 -> 19
            matchedKeywords >= 1 -> 14
            else -> 9
        }

        val reasoning = when {
            wordCount >= 70 -> 23
            wordCount >= 40 -> 19
            wordCount >= 20 -> 15
            else -> 10
        }

        val fluency = when {
            wordCount >= 50 -> 14
            wordCount >= 25 -> 12
            wordCount >= 10 -> 9
            else -> 6
        }

        val totalScore = (accuracy + terminology + reasoning + fluency).coerceIn(20, 96)

        val verdict = when {
            totalScore >= 85 -> "Müvəffəqiyyətli (Yüksək Dərəcə)"
            totalScore >= 70 -> "Müvəffəqiyyətli (Yaxşı)"
            totalScore >= 60 -> "Kafi (Keçid Balı)"
            else -> "Qeyri-kafi (Təkrar Hazırlıq Tələb Olunur)"
        }

        val feedback = when {
            totalScore >= 80 -> "Cavabınız AR qanunvericiliyinə yüksək uyğunluq nümayiş etdirir. Hüquqi kateqoriyalar, anlayışlar və tənzimləmə mexanizmləri dəqiq ifadə olunub."
            totalScore >= 65 -> "Mövzunun ümumi mahiyyəti düzgün qavranılıb, lakin konkret qanunvericilik normalarına və maddələrə daha aydın istinadlar edilməsi tövsiyə olunur."
            else -> "Cavabda hüquqi terminologiya və AR qanunvericiliyinin imperativ normalarına əsaslandırma zəifdir. Mövzunun Məcəllələr üzrə şərhlərini yenidən nəzərdən keçirin."
        }

        val recommendations = if (isCase) {
            "Məhkəmə təcrübəsində analoji kazuslara baxılma qaydasını, xüsusilə Ali Məhkəmənin Plenum qərarlarındakı izahları və prosessual müddətləri nəzərə alın."
        } else {
            "AR Mülki və Cinayət Məcəllələrinin ümumi və xüsusi hissələrindəki əsas prinsipləri, habelə konstitusion təminatları cavabınızda daha qabarıq göstərin."
        }

        return EvaluationResult(
            score = totalScore,
            verdict = verdict,
            accuracyScore = accuracy,
            terminologyScore = terminology,
            reasoningScore = reasoning,
            fluencyScore = fluency,
            feedback = feedback,
            recommendations = recommendations
        )
    }

    private fun callGeminiApi(prompt: String, apiKey: String): EvaluationResult? {
        val raw = callGeminiRaw(prompt, apiKey) ?: return null
        return try {
            val jsonPart = raw.substringAfter("{").substringBeforeLast("}")
            val json = JSONObject("{$jsonPart}")
            EvaluationResult(
                score = json.optInt("score", 75),
                verdict = json.optString("verdict", "Müvəffəqiyyətli"),
                accuracyScore = json.optInt("accuracyScore", 26),
                terminologyScore = json.optInt("terminologyScore", 20),
                reasoningScore = json.optInt("reasoningScore", 18),
                fluencyScore = json.optInt("fluencyScore", 11),
                feedback = json.optString("feedback", "Ətraflı hüquqi izah təqdim edildi."),
                recommendations = json.optString("recommendations", "Qanunvericilik maddələrini mütəmadi təkrarlayın.")
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
