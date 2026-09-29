package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AiLegalJudge
import com.example.data.ai.AppealResult
import com.example.data.ai.EvaluationResult
import com.example.data.local.model.ExamResultEntity
import com.example.data.local.model.UserProfile
import com.example.data.repository.LegalRepository
import com.example.ui.components.AppealDialog
import com.example.ui.components.CountdownTimerView
import com.example.ui.components.VerticalReelPicker
import com.example.ui.components.VoiceExaminationComponent
import com.example.ui.components.WheelItem
import com.example.ui.theme.LegalGold
import com.example.ui.theme.LegalGoldDark
import com.example.ui.theme.LegalNavyDark
import com.example.ui.theme.LegalNavyPrimary
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
    userProfile: UserProfile?,
    onNavigateToLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var stage by remember { mutableStateOf(TheoryStage.WHEEL_SELECTION) }
    var selectedTopic by remember { mutableStateOf<WheelItem?>(null) }
    var isTimerRunning by remember { mutableStateOf(false) }

    var isEvaluating by remember { mutableStateOf(false) }
    var evaluationResult by remember { mutableStateOf<EvaluationResult?>(null) }
    var lastSavedExamId by remember { mutableStateOf<Long?>(null) }
    var lastTranscript by remember { mutableStateOf("") }

    // Appeal dialog state
    var showAppealDialog by remember { mutableStateOf(false) }
    var isProcessingAppeal by remember { mutableStateOf(false) }
    var appealOutcome by remember { mutableStateOf<AppealResult?>(null) }

    Column(
        modifier = modifier
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
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Şaquli karusel, zaman təzyiqi və səsli imtahan",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // User Score Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(LegalGold.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${userProfile?.totalPoints ?: 450} Xal",
                    fontWeight = FontWeight.Bold,
                    color = LegalGoldDark,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (stage) {
            TheoryStage.WHEEL_SELECTION -> {
                // Minimalist Vertical Text Carousel (Slot Reel Style)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Nəzəriyyə Mövzuları Karuseli",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "AR qanunvericiliyi üzrə şaquli slot seçimi üçün 'Fırlat' düyməsini sıxın",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        VerticalReelPicker(
                            items = TheoryWheelTopics,
                            buttonText = "Fırlat",
                            headerTitle = "Hüquq Nəzəriyyəsi",
                            onItemSelected = { landedItem ->
                                selectedTopic = landedItem
                            }
                        )
                    }
                }

                // Show Selected Topic Card
                AnimatedVisibility(visible = selectedTopic != null) {
                    selectedTopic?.let { topic ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("selected_topic_card"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = LegalGold.copy(alpha = 0.15f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, LegalGold)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = topic.category,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = LegalGoldDark
                                        )
                                        Text(text = topic.iconEmoji, fontSize = 24.sp)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = topic.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = topic.subtitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = {
                                            stage = TheoryStage.TIMER_PREPARATION
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = LegalNavyPrimary,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("proceed_to_timer_button")
                                    ) {
                                        Text("Zaman Təzyiqini Təyin Et (10-60 dəq)", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            TheoryStage.TIMER_PREPARATION -> {
                // Countdown Timer Stage
                CountdownTimerView(
                    isRunning = isTimerRunning,
                    initialMinutes = 20,
                    onStartTimer = { isTimerRunning = true },
                    onTimeExpired = {
                        isTimerRunning = false
                        stage = TheoryStage.VOICE_EXAMINATION
                    },
                    onReadyNow = {
                        isTimerRunning = false
                        stage = TheoryStage.VOICE_EXAMINATION
                    },
                    onOpenLibrary = onNavigateToLibrary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Topic Info Box
                selectedTopic?.let { topic ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Seçilmiş Mövzu Fokus Nöqtələri:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = LegalGold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("1. Qanunvericilik bazası və müvafiq Məcəllə maddələri", style = MaterialTheme.typography.bodySmall)
                            Text("2. Məhkəmə təcrübəsi və Ali Məhkəmə Plenum qərarları", style = MaterialTheme.typography.bodySmall)
                            Text("3. Praktiki tətbiq və hüquq münasibətlərinin müdafiəsi", style = MaterialTheme.typography.bodySmall)

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = {
                                    isTimerRunning = false
                                    stage = TheoryStage.WHEEL_SELECTION
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.RestartAlt, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Başqa Mövzu Seç")
                            }
                        }
                    }
                }
            }

            TheoryStage.VOICE_EXAMINATION -> {
                // Oral Examination Stage
                selectedTopic?.let { topic ->
                    VoiceExaminationComponent(
                        topicTitle = topic.title,
                        topicCategory = topic.category,
                        onEvaluate = { transcript ->
                            lastTranscript = transcript
                            isEvaluating = true
                            scope.launch {
                                val result = AiLegalJudge.evaluateTheoryExam(topic.title, transcript)
                                evaluationResult = result
                                isEvaluating = false

                                // Save to Room DB
                                val entity = ExamResultEntity(
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
                                    appealStatus = "NONE"
                                )
                                lastSavedExamId = repository.saveExamResult(entity)
                            }
                        },
                        isEvaluating = isEvaluating,
                        evaluationResult = evaluationResult,
                        onOpenAppeal = {
                            appealOutcome = null
                            showAppealDialog = true
                        },
                        onReset = {
                            evaluationResult = null
                            selectedTopic = null
                            stage = TheoryStage.WHEEL_SELECTION
                        }
                    )
                }
            }
        }
    }

    // Appeal Dialog
    if (showAppealDialog) {
        AppealDialog(
            userCurrentPoints = userProfile?.totalPoints ?: 450,
            examTitle = selectedTopic?.title ?: "Nəzəriyyə Yoxlaması",
            onDismiss = {
                showAppealDialog = false
            },
            onSubmitAppeal = { wager, justification ->
                isProcessingAppeal = true
                scope.launch {
                    val outcome = AiLegalJudge.reviewAppeal(
                        examTitle = selectedTopic?.title ?: "Nəzəriyyə",
                        userAnswer = lastTranscript,
                        wager = wager,
                        justification = justification
                    )
                    appealOutcome = outcome
                    isProcessingAppeal = false

                    lastSavedExamId?.let { examId ->
                        repository.submitAppeal(
                            examId = examId,
                            wager = wager,
                            justification = justification,
                            isSuccess = outcome.isSuccess,
                            appealFeedback = outcome.reasoning
                        )
                    }
                }
            },
            isProcessing = isProcessingAppeal,
            appealResult = appealOutcome
        )
    }
}
