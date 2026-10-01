package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AiLegalJudge
import com.example.data.firestore.FirestoreRepository
import com.example.data.firestore.model.FirebaseUserModel
import com.example.data.local.model.ExamResultEntity
import com.example.data.repository.LegalRepository
import com.example.ui.components.AppealDialog
import com.example.ui.components.CountdownTimerView
import com.example.ui.components.VerticalReelPicker
import com.example.ui.components.VoiceExaminationComponent
import com.example.ui.components.WheelItem
import com.example.ui.navigation.ExamSessionManager
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.LuxuryGoldDark
import com.example.ui.theme.LuxuryGoldLight
import com.example.ui.theme.LuxuryTextHighContrast
import com.example.ui.theme.LuxuryTextMuted
import com.example.ui.theme.MidnightNavyBackground
import com.example.ui.theme.MidnightNavyBackgroundGradientEnd
import com.example.ui.theme.MidnightNavyBorder
import com.example.ui.theme.MidnightNavySurface
import com.example.ui.theme.MidnightNavySurfaceElevated
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val TheoryWheelTopics = listOf(
    WheelItem(
        id = "theory_1",
        title = "Mülki: Əqdlərin Etibarsızlığı",
        subtitle = "Əhəmiyyətsiz və mübahisələndirilən əqdlər, restitusiya",
        category = "Mülki Hüquq",
        iconEmoji = "📜"
    ),
    WheelItem(
        id = "theory_2",
        title = "Cinayət: Zəruri Müdafiə",
        subtitle = "AR CM 36-cı maddə, müdafiə hədləri və təhlükə nisbəti",
        category = "Cinayət Hüququ",
        iconEmoji = "⚖️"
    ),
    WheelItem(
        id = "theory_3",
        title = "Konstitusiya: Hüquq Təminatı",
        subtitle = "Konstitusiya maddə 60, məhkəmə müdafiəsi və dövlət məqsədi",
        category = "Konstitusiya Hüququ",
        iconEmoji = "🏛️"
    ),
    WheelItem(
        id = "theory_4",
        title = "Əmək: Ştat İxtisarı Qaydaları",
        subtitle = "Əmək Məcəlləsi m. 78-79, üstünlük hüququ və ləğv qadağası",
        category = "Əmək Hüququ",
        iconEmoji = "💼"
    ),
    WheelItem(
        id = "theory_5",
        title = "İnzibati: Protokol Tələbləri",
        subtitle = "İXM maddə 52, vəzifəli şəxsin səlahiyyətləri və icraat",
        category = "İnzibati Xətalar",
        iconEmoji = "📑"
    ),
    WheelItem(
        id = "theory_6",
        title = "Ailə: Birgə Əmlak Bölgüsü",
        subtitle = "Nikah dövründə əldə edilən əmlakın hüquqi statusu",
        category = "Ailə Hüququ",
        iconEmoji = "💍"
    ),
    WheelItem(
        id = "theory_7",
        title = "Prosessual: Sübutetmə Yükü",
        subtitle = "İddiaçının və cavabdehin sübut etmə vəzifələri",
        category = "Mülki Prosessual",
        iconEmoji = "🔍"
    ),
    WheelItem(
        id = "theory_8",
        title = "Öhdəlik: Şəraitin Dəyişməsi",
        subtitle = "Mülki Məcəllə m. 422, fors-major və müqaviləyə xitam",
        category = "Mülki Hüquq",
        iconEmoji = "🤝"
    )
)

enum class TheoryStage {
    WHEEL_SELECTION,
    TIMER_PREPARATION,
    VOICE_EXAMINATION
}

@Composable
fun TheoryCheckScreen(
    repository: LegalRepository,
    firestoreRepository: FirestoreRepository,
    currentUser: FirebaseUserModel?,
    sessionManager: ExamSessionManager,
    onNavigateToLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Real-time clock for 10-minute penalty timeout countdown
    var currentClockMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentClockMillis = System.currentTimeMillis()
        }
    }

    val timeoutUntil = currentUser?.timeoutUntilMillis ?: 0L
    val isPenaltyActive = timeoutUntil > currentClockMillis
    val remainingPenaltySeconds = if (isPenaltyActive) ((timeoutUntil - currentClockMillis) / 1000).coerceAtLeast(0) else 0
    val penaltyMinutes = remainingPenaltySeconds / 60
    val penaltySecs = remainingPenaltySeconds % 60

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MidnightNavyBackground,
                        MidnightNavyBackgroundGradientEnd,
                        Color(0xFF030716)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Nəzəriyyə Yoxlaması",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = LuxuryTextHighContrast
                    )
                    Text(
                        text = "Şaquli karusel, zaman təzyiqi və Gemini AI qiymətləndirməsi",
                        style = MaterialTheme.typography.bodySmall,
                        color = LuxuryTextMuted
                    )
                }

                // Real User Score Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(LuxuryGold.copy(alpha = 0.18f))
                        .border(1.dp, LuxuryGold.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${currentUser?.points ?: 0} Xal",
                        fontWeight = FontWeight.Bold,
                        color = LuxuryGold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timeout Penalty Warning Banner
            if (isPenaltyActive) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ErrorRed.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF280B10)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Cəza",
                            tint = ErrorRed,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "10 Dəqiqəlik Cəza Məhdudiyyəti Aktivdir!",
                                fontWeight = FontWeight.Bold,
                                color = LuxuryTextHighContrast,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Əvvəlki cavabınız boş və ya tamamilə aidiyyətsiz olduğu üçün 0 bal almışsınız. Qalan gözləmə müddəti: ${penaltyMinutes} dəqiqə ${penaltySecs} saniyə.",
                                color = LuxuryTextMuted,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Active Session Navigation Helper Banner
            if (sessionManager.isTheoryExamActive()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.8.dp, MidnightNavyBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MidnightNavySurfaceElevated
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HourglassBottom,
                                contentDescription = null,
                                tint = LuxuryGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Aktiv İmtahan Sessiyası Qorunur",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LuxuryTextHighContrast
                            )
                        }
                        OutlinedButton(
                            onClick = onNavigateToLibrary,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, LuxuryGold.copy(alpha = 0.6f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = LuxuryGold)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Qanunlara Bax", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            when (sessionManager.theoryStage) {
                TheoryStage.WHEEL_SELECTION -> {
                    // Minimalist Vertical Text Carousel (Slot Reel Style)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.8.dp, MidnightNavyBorder, RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MidnightNavySurface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Nəzəriyyə Mövzuları Karuseli",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = LuxuryTextHighContrast
                            )
                            Text(
                                text = "AR qanunvericiliyi üzrə şaquli slot seçimi üçün 'Fırlat' düyməsini sıxın",
                                style = MaterialTheme.typography.bodySmall,
                                color = LuxuryTextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            VerticalReelPicker(
                                items = TheoryWheelTopics,
                                buttonText = if (isPenaltyActive) "Məhdudiyyət Aktivdir" else "Fırlat",
                                headerTitle = "Hüquq Nəzəriyyəsi",
                                onItemSelected = { landedItem ->
                                    if (!isPenaltyActive) {
                                        sessionManager.selectedTheoryTopic = landedItem
                                    }
                                }
                            )
                        }
                    }

                    // Show Selected Topic Card
                    AnimatedVisibility(visible = sessionManager.selectedTheoryTopic != null) {
                        sessionManager.selectedTheoryTopic?.let { topic ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.2.dp, LuxuryGold.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                                        .testTag("selected_topic_card"),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MidnightNavySurfaceElevated
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(18.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(LuxuryGold.copy(alpha = 0.18f))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = topic.category,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = LuxuryGold,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Text(text = topic.iconEmoji, fontSize = 22.sp)
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = topic.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = LuxuryTextHighContrast
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = topic.subtitle,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = LuxuryTextMuted
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Button(
                                            onClick = {
                                                if (!isPenaltyActive) {
                                                    sessionManager.theoryStage = TheoryStage.TIMER_PREPARATION
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                                .testTag("start_preparation_button"),
                                            enabled = !isPenaltyActive,
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = LuxuryGold,
                                                contentColor = Color(0xFF0A1128),
                                                disabledContainerColor = MidnightNavyBorder,
                                                disabledContentColor = LuxuryTextMuted
                                            )
                                        ) {
                                            Text("Hazırlıq Mərhələsinə Başla", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                TheoryStage.TIMER_PREPARATION -> {
                    // Preparation with Countdown Timer
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.8.dp, MidnightNavyBorder, RoundedCornerShape(22.dp)),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MidnightNavySurface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Düşünmə və Hazırlıq Taymeri",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = LuxuryTextHighContrast
                            )
                            Text(
                                text = "Zaman təzyiqi altında mövzunu təhlil edin (10-60 dəqiqə)",
                                style = MaterialTheme.typography.bodySmall,
                                color = LuxuryTextMuted
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Selected Topic Reminder Banner
                            sessionManager.selectedTheoryTopic?.let { topic ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MidnightNavySurfaceElevated)
                                        .border(0.8.dp, MidnightNavyBorder, RoundedCornerShape(12.dp))
                                        .padding(14.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Mövzu: ${topic.title}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = LuxuryTextHighContrast
                                        )
                                        Text(
                                            text = topic.subtitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = LuxuryTextMuted
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            CountdownTimerView(
                                isRunning = sessionManager.isTheoryTimerRunning,
                                initialMinutes = sessionManager.theoryPreparationTimerMinutes,
                                persistedSecondsRemaining = sessionManager.theorySecondsRemaining,
                                persistedTotalSeconds = sessionManager.theoryTotalSeconds,
                                onStartTimer = { mins ->
                                    sessionManager.theoryPreparationTimerMinutes = mins
                                    sessionManager.theoryTotalSeconds = mins * 60
                                    sessionManager.theorySecondsRemaining = mins * 60
                                    sessionManager.isTheoryTimerRunning = true
                                },
                                onSecondsTick = { secs ->
                                    sessionManager.theorySecondsRemaining = secs
                                },
                                onTimeExpired = {
                                    sessionManager.isTheoryTimerRunning = false
                                    sessionManager.theoryStage = TheoryStage.VOICE_EXAMINATION
                                },
                                onReadyNow = {
                                    sessionManager.isTheoryTimerRunning = false
                                    sessionManager.theoryStage = TheoryStage.VOICE_EXAMINATION
                                },
                                onOpenLibrary = onNavigateToLibrary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        sessionManager.theoryStage = TheoryStage.WHEEL_SELECTION
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(0.8.dp, MidnightNavyBorder),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LuxuryTextMuted)
                                ) {
                                    Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Mövzunu Dəyiş")
                                }

                                Button(
                                    onClick = {
                                        sessionManager.theoryStage = TheoryStage.VOICE_EXAMINATION
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = LuxuryGold,
                                        contentColor = Color(0xFF0A1128)
                                    )
                                ) {
                                    Text("Cavab Mərhələsinə Keç", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                TheoryStage.VOICE_EXAMINATION -> {
                    // Voice or Text Examination and Gemini AI Evaluation
                    sessionManager.selectedTheoryTopic?.let { topic ->
                        VoiceExaminationComponent(
                            topicTitle = topic.title,
                            topicCategory = topic.category,
                            onEvaluate = { transcript ->
                                sessionManager.theoryTranscript = transcript
                                sessionManager.isTheoryEvaluating = true
                                scope.launch {
                                    val result = AiLegalJudge.evaluateTheoryExam(topic.title, transcript)
                                    sessionManager.theoryEvaluationResult = result
                                    sessionManager.isTheoryEvaluating = false

                                    val uid = currentUser?.id ?: ""
                                    if (result.score == 0 || result.isZeroPenalty) {
                                        val penaltyUntil = System.currentTimeMillis() + 10 * 60 * 1000
                                        if (uid.isNotBlank()) {
                                            firestoreRepository.setUserTimeout(uid, penaltyUntil)
                                        }
                                    } else {
                                        if (uid.isNotBlank()) {
                                            firestoreRepository.updateUserPoints(uid, result.score.toLong(), isExamWin = result.score >= 60)
                                        }
                                    }

                                    val savedId = repository.saveExamResult(
                                        ExamResultEntity(
                                            examType = "THEORY",
                                            title = topic.title,
                                            topicCategory = topic.category,
                                            userAnswerText = transcript,
                                            score = result.score,
                                            verdict = result.verdict,
                                            accuracyScore = result.accuracyScore,
                                            terminologyScore = result.terminologyScore,
                                            reasoningScore = result.reasoningScore,
                                            fluencyScore = result.fluencyScore,
                                            feedback = result.feedback,
                                            recommendations = result.recommendations,
                                            appealStatus = "NONE",
                                            createdAt = System.currentTimeMillis()
                                        )
                                    )
                                    sessionManager.theoryLastSavedExamId = savedId
                                }
                            },
                            isEvaluating = sessionManager.isTheoryEvaluating,
                            evaluationResult = sessionManager.theoryEvaluationResult,
                            onOpenAppeal = {
                                sessionManager.showTheoryAppealDialog = true
                            },
                            onReset = {
                                sessionManager.resetTheorySession()
                            }
                        )
                    }
                }
            }

            // Appeal Dialog
            if (sessionManager.showTheoryAppealDialog && sessionManager.theoryEvaluationResult != null) {
                val userPts = currentUser?.points?.toInt() ?: 100
                AppealDialog(
                    userCurrentPoints = userPts,
                    examTitle = sessionManager.selectedTheoryTopic?.title ?: "Hüquq İmtahanı",
                    isProcessing = sessionManager.isProcessingTheoryAppeal,
                    appealResult = sessionManager.theoryAppealOutcome,
                    onSubmitAppeal = { wager, justification ->
                        sessionManager.isProcessingTheoryAppeal = true
                        scope.launch {
                            val appealResult = AiLegalJudge.reviewAppeal(
                                examTitle = sessionManager.selectedTheoryTopic?.title ?: "Hüquq İmtahanı",
                                userAnswer = sessionManager.theoryTranscript,
                                wager = wager,
                                justification = justification
                            )
                            sessionManager.theoryAppealOutcome = appealResult
                            sessionManager.isProcessingTheoryAppeal = false

                            val uid = currentUser?.id ?: ""
                            if (appealResult.isSuccess) {
                                if (uid.isNotBlank()) {
                                    firestoreRepository.updateUserPoints(uid, (wager * 0.5).toLong() + appealResult.scoreAdjustment, isExamWin = true)
                                }
                            } else {
                                if (uid.isNotBlank()) {
                                    firestoreRepository.updateUserPoints(uid, -wager.toLong(), isExamWin = false)
                                }
                            }

                            sessionManager.theoryLastSavedExamId?.let { id ->
                                repository.submitAppeal(
                                    examId = id,
                                    wager = wager,
                                    justification = justification,
                                    isSuccess = appealResult.isSuccess,
                                    appealFeedback = appealResult.reasoning
                                )
                            }
                        }
                    },
                    onDismiss = {
                        sessionManager.showTheoryAppealDialog = false
                        sessionManager.theoryAppealOutcome = null
                    }
                )
            }
        }
    }
}
