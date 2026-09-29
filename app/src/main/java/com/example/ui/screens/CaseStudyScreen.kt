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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.components.EvaluationResultCard
import com.example.ui.components.VerticalReelPicker
import com.example.ui.components.WheelItem
import com.example.ui.theme.LegalGold
import com.example.ui.theme.LegalGoldDark
import com.example.ui.theme.LegalNavyDark
import com.example.ui.theme.LegalNavyPrimary
import kotlinx.coroutines.launch

data class PracticalCase(
    val id: String,
    val wheelItem: WheelItem,
    val parties: String,
    val facts: String,
    val legalIssue: String
)

val PracticalCasesList = listOf(
    PracticalCase(
        id = "case_1",
        wheelItem = WheelItem("c1", "Notariat Təsdiqsiz Mənzil", "Alqı-satqı müqaviləsinin etibarsızlığı", "Mülki Hüquq", "🏢"),
        parties = "İddiaçı: Ə. Həsənov, Cavabdeh: R. Məmmədov",
        facts = "Tərəflər 2023-cü ildə Bakı şəhərində yerləşən mənzilin 120.000 AZN-ə alqı-satqısı barədə sadə yazılı formada razılaşma imzalayıblar. Alıcı pulu ödəyib və mənzilə köçüb, lakin tərəflər müqaviləni notariat qaydasında təsdiq etməyiblər və daşınmaz əmlakın dövlət reyestrində qeydiyyat aparılmayıb. İndi satıcı mənzilin bazar qiymətinin artdığını bildirərək mənzilin boşaldılmasını tələb edir.",
        legalIssue = "Müqavilə etibarlıdırmı? Mülki Məcəllənin 139, 182 və 337-ci maddələrinə əsasən tərəflərin hüquqi vəziyyətini tövsif edin və məhkəmə qətnaməsinin nəticə hissəsini tərtib edin."
    ),
    PracticalCase(
        id = "case_2",
        wheelItem = WheelItem("c2", "Zəruri Müdafiə və Hədd", "Gecə vaxtı mənzilə qanunsuz daxilolma", "Cinayət Hüququ", "🛡️"),
        parties = "Təqsirləndirilən: T. Qasımov, Zərərçəkmiş: V. Babayev",
        facts = "Gecə saat 03:00 radələrində V. Babayev T. Qasımovun fərdi yaşayış evinə pəncərəni sındıraraq daxil olub. Səsə oyanan ev sahibi qaranlıqda əlinə keçən dəmirlə hücum edən şəxsin başına zərbə endirib, nəticədə V. Babayevin sağlamlığına ağır zərər vurulub. Müstəntiq T. Qasımova qarşı AR CM 126.1 (qəsdən sağlamlığa ağır zərər vurma) ilə ittiham irəli sürüb.",
        legalIssue = "T. Qasımovun hərəkətlərində AR CM 36-cı maddəsinə əsasən zəruri müdafiə vəziyyəti mövcuddurmu, yoxsa zəruri müdafiə həddi aşılmışdır? Müdafiə tərəfinin vəsatətini hazırlayın."
    ),
    PracticalCase(
        id = "case_3",
        wheelItem = WheelItem("c3", "Ştat İxtisarı Mübahisəsi", "Hamilə işçinin hüququ və üstünlük", "Əmək Hüququ", "📑"),
        parties = "İddiaçı: N. Quliyeva, Cavabdeh: 'AzərTech' QSC",
        facts = "'AzərTech' QSC-də ştat ixtisarı aparılarkən 2 yaşlı uşağı olan və eyni zamanda 4 aylıq hamilə olan mütəxəssis N. Quliyevanın əmək müqaviləsinə AR Əmək Məcəlləsinin 70-ci maddəsinin 'b' bəndi ilə xitam verilib. İşəgötürən bildirir ki, həmin ştat tam ləğv edildiyi üçün işçini başqa işə keçirmək mümkün olmayıb.",
        legalIssue = "AR Əmək Məcəlləsinin 78 və 79-cu maddələri baxımından işəgötürənin əmri qanunidirmi? İddia ərizəsinin əsaslandırılmış hüquqi hissəsini yazın."
    ),
    PracticalCase(
        id = "case_4",
        wheelItem = WheelItem("c4", "Yol Qəzası və Mənəvi Zərər", "Yüksək təhlükə mənbəyi və kompensasiya", "Mülki Hüquq", "🚗"),
        parties = "İddiaçı: S. Əliyev, Cavabdeh: K. Mahmudov",
        facts = "K. Mahmudov idarə etdiyi avtomobillə piyada keçidində S. Əliyevi vurub. İddiaçı 2 ay xəstəxanada müalicə alıb və əmək qabiliyyətini qismən itirib. Maddi zərər sığorta şirkəti tərəfindən ödənilib, lakin S. Əliyev keçirdiyi fiziki və mənəvi iztirablara görə sürücüdən əlavə 25.000 AZN mənəvi zərər tələb edir.",
        legalIssue = "Mülki Məcəllənin 1115-ci maddəsi və Ali Məhkəmənin Plenum qərarı işığında mənəvi zərərin ağlabatan məbləğini və təyini meyarlarını əsaslandırın."
    ),
    PracticalCase(
        id = "case_5",
        wheelItem = WheelItem("c5", "Vərəsəlikdə Məcburi Pay", "Vəsiyyətnamənin hüquqi qüvvəsi", "Mülki Hüquq", "📜"),
        parties = "İddiaçı: F. Vəliyev (I qrup əlil), Cavabdeh: G. Vəliyeva",
        facts = "Miras qoyan şəxs bütün əmlakını vəsiyyətnamə ilə ikinci həyat yoldaşı G. Vəliyevaya vəsiyyət edib. Birinci nikahdan olan I qrup əlil oğlu F. Vəliyev isə vəsiyyətnamədən kənarda qalıb. O, məhkəməyə müraciət edərək mirasdan pay tələb edir.",
        legalIssue = "AR Mülki Məcəlləsinin 1193-cü maddəsinə əsasən məcburi pay hüququ necə hesablanır və vəsiyyətnamə tam ləğv edilə bilərmi?"
    ),
    PracticalCase(
        id = "case_6",
        wheelItem = WheelItem("c6", "Protokolsuz İnzibati Cərimə", "İXM m. 52 və prosessual qanunilik", "İnzibati Xətalar", "🚦"),
        parties = "Şikayətçi: E. Muradov, Cavabdeh orqan: İcra Hakimiyyəti",
        facts = "Səlahiyyətli orqan tərəfindən vətəndaş E. Muradov barəsində inzibati xəta haqqında protokol tərtib edilmədən, birbaşa inzibati tənbeh tətbiq etmə haqqında qərar çıxarılaraq 500 AZN cərimə tətbiq olunub.",
        legalIssue = "İnzibati Xətalar Məcəlləsinin 52-ci maddəsinə əsasən protokol tərtib edilməməsi qərarın ləğvi üçün əsasdırmı? Şikayət layihəsini hazırlayın."
    )
)

enum class CaseStage {
    WHEEL,
    TIMER_PREP,
    SOLUTION_SUBMISSION
}

@Composable
fun CaseStudyScreen(
    repository: LegalRepository,
    userProfile: UserProfile?,
    onNavigateToLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var stage by remember { mutableStateOf(CaseStage.WHEEL) }
    var selectedCase by remember { mutableStateOf<PracticalCase?>(null) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // Solution inputs
    var qualificationText by remember { mutableStateOf("") }
    var legalArticlesText by remember { mutableStateOf("") }
    var verdictProposalText by remember { mutableStateOf("") }

    var isEvaluating by remember { mutableStateOf(false) }
    var evaluationResult by remember { mutableStateOf<EvaluationResult?>(null) }
    var lastSavedCaseId by remember { mutableStateOf<Long?>(null) }
    var combinedSolution by remember { mutableStateOf("") }

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
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Praktiki Kazuslar",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "AR məhkəmə təcrübəsi üzrə real hüquqi kazuslar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(LegalGold.copy(alpha = 0.2f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${userProfile?.casesSolved ?: 8} Kazus Həll Olunub",
                    fontWeight = FontWeight.Bold,
                    color = LegalGoldDark,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (stage) {
            CaseStage.WHEEL -> {
                // Minimalist Vertical Text Carousel (Slot Reel Style)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Praktiki Kazuslar Karuseli",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Məhkəmə təcrübəsi üzrə şaquli slot seçimi üçün 'Fırlat' düyməsini sıxın",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        VerticalReelPicker(
                            items = PracticalCasesList.map { it.wheelItem },
                            buttonText = "Fırlat",
                            headerTitle = "Praktiki Kazus",
                            onItemSelected = { landed ->
                                selectedCase = PracticalCasesList.find { it.wheelItem.id == landed.id }
                            }
                        )
                    }
                }

                // Selected Case Presentation
                AnimatedVisibility(visible = selectedCase != null) {
                    selectedCase?.let { caseItem ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("selected_case_card"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, LegalGold)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = caseItem.wheelItem.category,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = LegalGoldDark
                                        )
                                        Text(text = caseItem.wheelItem.iconEmoji, fontSize = 22.sp)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = caseItem.wheelItem.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = caseItem.parties,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = LegalNavyPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = caseItem.facts,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = { stage = CaseStage.TIMER_PREP },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = LegalNavyPrimary,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("proceed_case_timer_button")
                                    ) {
                                        Text("Kazusu Araşdır (10-60 dəq Taymer)", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            CaseStage.TIMER_PREP -> {
                CountdownTimerView(
                    isRunning = isTimerRunning,
                    initialMinutes = 25,
                    onStartTimer = { isTimerRunning = true },
                    onTimeExpired = {
                        isTimerRunning = false
                        stage = CaseStage.SOLUTION_SUBMISSION
                    },
                    onReadyNow = {
                        isTimerRunning = false
                        stage = CaseStage.SOLUTION_SUBMISSION
                    },
                    onOpenLibrary = onNavigateToLibrary
                )

                Spacer(modifier = Modifier.height(16.dp))

                selectedCase?.let { caseItem ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Kazusun Hüquqi Tələbi və Sual:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = LegalGold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(caseItem.legalIssue, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = {
                                    isTimerRunning = false
                                    stage = CaseStage.WHEEL
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.RestartAlt, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Karuselə Qayıt")
                            }
                        }
                    }
                }
            }

            CaseStage.SOLUTION_SUBMISSION -> {
                if (evaluationResult == null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("case_solution_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Kazusun Həlli və Hüquqi Tövsif",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Məhkəmə qətnaməsi standartlarına uyğun əsaslandırma daxil edin",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = qualificationText,
                                onValueChange = { qualificationText = it },
                                label = { Text("1. Faktların Hüquqi Tövsifi") },
                                placeholder = { Text("Məs: Tərəflər arasında bağlanmış əqd AR MM 139-cu maddəsinin tələblərinə zidd olaraq...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .testTag("case_qualification_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LegalGold)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = legalArticlesText,
                                onValueChange = { legalArticlesText = it },
                                label = { Text("2. Qanunvericilik Maddələrinə İstinad") },
                                placeholder = { Text("Məs: AR Mülki Məcəlləsi m. 182, 337.1; AR Konstitusiyası m. 60...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .testTag("case_articles_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LegalGold)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = verdictProposalText,
                                onValueChange = { verdictProposalText = it },
                                label = { Text("3. Nəticə və Məhkəmə Qətnaməsi Təklifi") },
                                placeholder = { Text("Məs: İddia təmin edilsin, mənzil üzərində alqı-satqı əhəmiyyətsiz hesab edilsin...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .testTag("case_verdict_input"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LegalGold)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            val isReady = qualificationText.trim().length >= 10 && verdictProposalText.trim().length >= 10

                            Button(
                                onClick = {
                                    val full = "Hüquqi Tövsif: $qualificationText\nİstinadlar: $legalArticlesText\nQərar Təklifi: $verdictProposalText"
                                    combinedSolution = full
                                    isEvaluating = true
                                    scope.launch {
                                        val caseObj = selectedCase ?: PracticalCasesList[0]
                                        val eval = AiLegalJudge.evaluateCaseStudy(
                                            caseTitle = caseObj.wheelItem.title,
                                            caseDescription = caseObj.facts,
                                            userSolution = full
                                        )
                                        evaluationResult = eval
                                        isEvaluating = false

                                        // Save to DB
                                        val entity = ExamResultEntity(
                                            examType = "CASE",
                                            title = caseObj.wheelItem.title,
                                            topicCategory = caseObj.wheelItem.category,
                                            userAnswerText = full,
                                            score = eval.score,
                                            verdict = eval.verdict,
                                            accuracyScore = eval.accuracyScore,
                                            terminologyScore = eval.terminologyScore,
                                            reasoningScore = eval.reasoningScore,
                                            fluencyScore = eval.fluencyScore,
                                            feedback = eval.feedback,
                                            recommendations = eval.recommendations
                                        )
                                        lastSavedCaseId = repository.saveExamResult(entity)
                                    }
                                },
                                enabled = isReady && !isEvaluating,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LegalNavyPrimary,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("submit_case_button")
                            ) {
                                if (isEvaluating) {
                                    CircularProgressIndicator(
                                        color = LegalGold,
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Hakim Kazusu Qiymətləndirir...")
                                } else {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = LegalGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Kazus Həllini Təqdim Et", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // Evaluation Result Display
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            evaluationResult?.let { res ->
                                EvaluationResultCard(
                                    result = res,
                                    onOpenAppeal = {
                                        appealOutcome = null
                                        showAppealDialog = true
                                    },
                                    onReset = {
                                        evaluationResult = null
                                        selectedCase = null
                                        qualificationText = ""
                                        legalArticlesText = ""
                                        verdictProposalText = ""
                                        stage = CaseStage.WHEEL
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Appeal Dialog for Case
    if (showAppealDialog) {
        AppealDialog(
            userCurrentPoints = userProfile?.totalPoints ?: 450,
            examTitle = selectedCase?.wheelItem?.title ?: "Kazus Həlli",
            onDismiss = { showAppealDialog = false },
            onSubmitAppeal = { wager, justification ->
                isProcessingAppeal = true
                scope.launch {
                    val outcome = AiLegalJudge.reviewAppeal(
                        examTitle = selectedCase?.wheelItem?.title ?: "Kazus",
                        userAnswer = combinedSolution,
                        wager = wager,
                        justification = justification
                    )
                    appealOutcome = outcome
                    isProcessingAppeal = false

                    lastSavedCaseId?.let { caseId ->
                        repository.submitAppeal(
                            examId = caseId,
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
