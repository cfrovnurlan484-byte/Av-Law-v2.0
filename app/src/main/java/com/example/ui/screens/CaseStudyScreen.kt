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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.data.firestore.FirestoreRepository
import com.example.data.firestore.model.FirebaseUserModel
import com.example.data.local.model.ExamResultEntity
import com.example.data.repository.LegalRepository
import com.example.ui.components.AppealDialog
import com.example.ui.components.CountdownTimerView
import com.example.ui.components.EvaluationResultCard
import com.example.ui.components.VerticalReelPicker
import com.example.ui.components.WheelItem
import com.example.ui.navigation.ExamSessionManager
import com.example.ui.theme.LegalGold
import com.example.ui.theme.LegalGoldDark
import com.example.ui.theme.LegalNavyDark
import com.example.ui.theme.LegalNavyPrimary
import kotlinx.coroutines.delay
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
        wheelItem = WheelItem("c3", "Ştat İxtisarı Mübahisəsi", "Hamilə işçinin hüququ və üstünlük", "Əmək Hüququ", "💼"),
        parties = "İddiaçı: N. Quliyeva, Cavabdeh: 'AzərTech' QSC",
        facts = "'AzərTech' QSC-də ştat ixtisarı aparılarkən 2 yaşlı uşağı olan və eyni zamanda 4 aylıq hamilə olan mütəxəssis N. Quliyevanın əmək müqaviləsinə AR Əmək Məcəlləsinin 70-ci maddəsinin 'b' bəndi ilə xitam verilib. İşəgötürən bildirir ki, həmin ştat tam ləğv edildiyi üçün işçini başqa işə keçirmək mümkün olmayıb.",
        legalIssue = "AR Əmək Məcəlləsinin 78 və 79-cu maddələri baxımından işəgötürənin əmri qanunidirmi? İddia ərizəsinin əsaslandırılmış hüquqi hissəsini yazın."
    ),
    PracticalCase(
        id = "case_4",
        wheelItem = WheelItem("c4", "Yol Qəzası və Mənəvi Zərər", "Yüksək təhlükə mənbəyi və kompensasiya", "Mülki Hüquq", "🚗"),
        parties = "İddiaçı: S. Vəliyev, Cavabdeh: 'Ekspress Logistika' MMC",
        facts = "'Ekspress Logistika' MMC-nin sürücüsü idarə etdiyi yük maşını ilə nizamlanmayan piyada keçidində piyada S. Vəliyevi vurub. Piyada orta dərəcəli bədən xəsarəti alıb və 45 gün stasionar müalicə olunub. İddiaçı çəkilmiş 4.500 AZN müalicə xərcləri ilə yanaşı, fiziki və mənəvi iztirablara görə 15.000 AZN mənəvi zərər tələb edir.",
        legalIssue = "AR MM 1111 və 1115-ci maddələri baxımından yüksək təhlükə mənbəyinin vurduğu zərərin ödənilməsi və mənəvi zərərin ağlabatan məbləğdə təyin edilməsi qaydalarını əsaslandırın."
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
    firestoreRepository: FirestoreRepository,
    currentUser: FirebaseUserModel?,
    sessionManager: ExamSessionManager,
    onNavigateToLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

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
                    text = "${currentUser?.points ?: 0} Xal",
                    fontWeight = FontWeight.Bold,
                    color = LegalGoldDark,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Timeout Penalty Warning Banner
        if (isPenaltyActive) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Cəza",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "10 Dəqiqəlik Cəza Məhdudiyyəti Aktivdir!",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Əvvəlki cavabınız boş və ya tamamilə aidiyyətsiz olduğu üçün 0 bal almışsınız. Qalan gözləmə müddəti: ${penaltyMinutes} dəqiqə ${penaltySecs} saniyə.",
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Active Case Study Session Banner
        if (sessionManager.isCaseStudyActive()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LegalNavyPrimary.copy(alpha = 0.12f)
                )
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
                            tint = LegalGoldDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aktiv Kazus Sessiyası Qorunur",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LegalNavyDark
                        )
                    }
                    OutlinedButton(
                        onClick = onNavigateToLibrary,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mənbələrə Bax", fontSize = 11.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        when (sessionManager.caseStage) {
            CaseStage.WHEEL -> {
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val reelItems = PracticalCasesList.map { it.wheelItem }
                        VerticalReelPicker(
                            items = reelItems,
                            buttonText = if (isPenaltyActive) "Məhdudiyyət Aktivdir" else "Fırlat",
                            headerTitle = "Məhkəmə Kazusları",
                            onItemSelected = { landed ->
                                if (!isPenaltyActive) {
                                    sessionManager.selectedCase = PracticalCasesList.find { it.wheelItem.id == landed.id }
                                }
                            }
                        )
                    }
                }

                AnimatedVisibility(visible = sessionManager.selectedCase != null) {
                    sessionManager.selectedCase?.let { caseItem ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("selected_case_card"),
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = LegalGold.copy(alpha = 0.12f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = caseItem.wheelItem.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = LegalNavyPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = caseItem.parties,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = LegalGoldDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = caseItem.facts,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = {
                                            if (!isPenaltyActive) {
                                                sessionManager.caseStage = CaseStage.TIMER_PREP
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("start_case_prep_button"),
                                        enabled = !isPenaltyActive,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = LegalNavyPrimary,
                                            contentColor = Color.White
                                        )
                                    ) {
                                        Text("Kazusun Təhlilinə Başla")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            CaseStage.TIMER_PREP -> {
                sessionManager.selectedCase?.let { caseItem ->
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
                                text = "Kazusun Təhlili Taymeri",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Text(
                                        text = caseItem.wheelItem.title,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = caseItem.facts,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Hüquqi Sual: ${caseItem.legalIssue}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = LegalGoldDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            CountdownTimerView(
                                isRunning = sessionManager.isCaseTimerRunning,
                                initialMinutes = sessionManager.casePreparationTimerMinutes,
                                persistedSecondsRemaining = sessionManager.caseSecondsRemaining,
                                persistedTotalSeconds = sessionManager.caseTotalSeconds,
                                onStartTimer = { mins ->
                                    sessionManager.casePreparationTimerMinutes = mins
                                    sessionManager.caseTotalSeconds = mins * 60
                                    sessionManager.caseSecondsRemaining = mins * 60
                                    sessionManager.isCaseTimerRunning = true
                                },
                                onSecondsTick = { secs ->
                                    sessionManager.caseSecondsRemaining = secs
                                },
                                onTimeExpired = {
                                    sessionManager.isCaseTimerRunning = false
                                    sessionManager.caseStage = CaseStage.SOLUTION_SUBMISSION
                                },
                                onReadyNow = {
                                    sessionManager.isCaseTimerRunning = false
                                    sessionManager.caseStage = CaseStage.SOLUTION_SUBMISSION
                                },
                                onOpenLibrary = onNavigateToLibrary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { sessionManager.caseStage = CaseStage.WHEEL },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Geri Qayıt")
                                }
                                Button(
                                    onClick = { sessionManager.caseStage = CaseStage.SOLUTION_SUBMISSION },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = LegalGoldDark,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Həlli Yaz")
                                }
                            }
                        }
                    }
                }
            }

            CaseStage.SOLUTION_SUBMISSION -> {
                sessionManager.selectedCase?.let { caseItem ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Case facts review
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = caseItem.wheelItem.title,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = caseItem.facts,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Solution Form
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Kazusun Hüquqi Həlli",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                OutlinedTextField(
                                    value = sessionManager.qualificationText,
                                    onValueChange = { sessionManager.qualificationText = it },
                                    label = { Text("Faktların Hüquqi Tövsifi") },
                                    placeholder = { Text("Tərəflərin hərəkətlərinə hüquqi qiymət verin...") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("case_qualification_input"),
                                    minLines = 3,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = LegalGold,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = sessionManager.legalArticlesText,
                                    onValueChange = { sessionManager.legalArticlesText = it },
                                    label = { Text("İstinad Edilən Qanunvericilik Maddələri") },
                                    placeholder = { Text("məs: AR Mülki Məcəlləsi m. 139, 337...") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("case_articles_input"),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = LegalGold,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = sessionManager.verdictProposalText,
                                    onValueChange = { sessionManager.verdictProposalText = it },
                                    label = { Text("Məhkəmə Qərarı / Qətnamə Layihəsi") },
                                    placeholder = { Text("İddianın təmin və ya rədd edilməsi barədə nəticə...") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("case_verdict_input"),
                                    minLines = 3,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = LegalGold,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        val combined = """
                                            Hüquqi Tövsif: ${sessionManager.qualificationText}
                                            Qanunvericilik maddələri: ${sessionManager.legalArticlesText}
                                            Qərar layihəsi: ${sessionManager.verdictProposalText}
                                        """.trimIndent()

                                        sessionManager.isCaseEvaluating = true
                                        scope.launch {
                                            val result = AiLegalJudge.evaluateCaseStudy(
                                                caseTitle = caseItem.wheelItem.title,
                                                caseDescription = caseItem.facts,
                                                userSolution = combined
                                            )
                                            sessionManager.caseEvaluationResult = result
                                            sessionManager.isCaseEvaluating = false

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
                                                    examType = "CASE",
                                                    title = caseItem.wheelItem.title,
                                                    topicCategory = caseItem.wheelItem.category,
                                                    userAnswerText = combined,
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
                                            sessionManager.caseLastSavedCaseId = savedId
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("submit_case_solution_button"),
                                    enabled = !sessionManager.isCaseEvaluating,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = LegalNavyPrimary,
                                        contentColor = Color.White
                                    )
                                ) {
                                    if (sessionManager.isCaseEvaluating) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Gemini AI Qiymətləndirir...")
                                    } else {
                                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Həlli Təqdim Et")
                                    }
                                }
                            }
                        }

                        // Evaluation Result Card
                        sessionManager.caseEvaluationResult?.let { result ->
                            Spacer(modifier = Modifier.height(16.dp))
                            EvaluationResultCard(
                                result = result,
                                onOpenAppeal = { sessionManager.showCaseAppealDialog = true },
                                onReset = { sessionManager.resetCaseSession() }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = { sessionManager.resetCaseSession() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Yeni Kazus Seç")
                            }
                        }
                    }
                }
            }
        }

        // Appeal Dialog
        if (sessionManager.showCaseAppealDialog && sessionManager.caseEvaluationResult != null) {
            val userPts = currentUser?.points?.toInt() ?: 100
            AppealDialog(
                userCurrentPoints = userPts,
                examTitle = sessionManager.selectedCase?.wheelItem?.title ?: "Praktiki Kazus",
                isProcessing = sessionManager.isProcessingCaseAppeal,
                appealResult = sessionManager.caseAppealOutcome,
                onSubmitAppeal = { wager, justification ->
                    sessionManager.isProcessingCaseAppeal = true
                    scope.launch {
                        val appealResult = AiLegalJudge.reviewAppeal(
                            examTitle = sessionManager.selectedCase?.wheelItem?.title ?: "Praktiki Kazus",
                            userAnswer = "${sessionManager.qualificationText}\n${sessionManager.legalArticlesText}",
                            wager = wager,
                            justification = justification
                        )
                        sessionManager.caseAppealOutcome = appealResult
                        sessionManager.isProcessingCaseAppeal = false

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

                        sessionManager.caseLastSavedCaseId?.let { id ->
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
                    sessionManager.showCaseAppealDialog = false
                    sessionManager.caseAppealOutcome = null
                }
            )
        }
    }
}
