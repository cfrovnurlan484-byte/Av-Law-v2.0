package com.example.ui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.data.ai.AppealResult
import com.example.data.ai.EvaluationResult
import com.example.ui.components.WheelItem
import com.example.ui.screens.CaseStage
import com.example.ui.screens.PracticalCase
import com.example.ui.screens.TheoryStage

class ExamSessionManager {
    // Theory Exam State
    var theoryStage by mutableStateOf(TheoryStage.WHEEL_SELECTION)
    var selectedTheoryTopic by mutableStateOf<WheelItem?>(null)
    var theoryPreparationTimerMinutes by mutableIntStateOf(10)
    var isTheoryTimerRunning by mutableStateOf(false)
    var theorySecondsRemaining by mutableIntStateOf(10 * 60)
    var theoryTotalSeconds by mutableIntStateOf(10 * 60)
    var theoryTranscript by mutableStateOf("")
    var isTheoryEvaluating by mutableStateOf(false)
    var theoryEvaluationResult by mutableStateOf<EvaluationResult?>(null)
    var theoryLastSavedExamId by mutableStateOf<Long?>(null)
    var showTheoryAppealDialog by mutableStateOf(false)
    var isProcessingTheoryAppeal by mutableStateOf(false)
    var theoryAppealOutcome by mutableStateOf<AppealResult?>(null)

    fun isTheoryExamActive(): Boolean {
        return theoryStage != TheoryStage.WHEEL_SELECTION
    }

    fun resetTheorySession() {
        theoryStage = TheoryStage.WHEEL_SELECTION
        selectedTheoryTopic = null
        isTheoryTimerRunning = false
        theoryPreparationTimerMinutes = 10
        theorySecondsRemaining = 10 * 60
        theoryTotalSeconds = 10 * 60
        theoryTranscript = ""
        isTheoryEvaluating = false
        theoryEvaluationResult = null
        showTheoryAppealDialog = false
        theoryAppealOutcome = null
    }

    // Case Study State
    var caseStage by mutableStateOf(CaseStage.WHEEL)
    var selectedCase by mutableStateOf<PracticalCase?>(null)
    var casePreparationTimerMinutes by mutableIntStateOf(30)
    var isCaseTimerRunning by mutableStateOf(false)
    var caseSecondsRemaining by mutableIntStateOf(30 * 60)
    var caseTotalSeconds by mutableIntStateOf(30 * 60)
    var qualificationText by mutableStateOf("")
    var legalArticlesText by mutableStateOf("")
    var verdictProposalText by mutableStateOf("")
    var isCaseEvaluating by mutableStateOf(false)
    var caseEvaluationResult by mutableStateOf<EvaluationResult?>(null)
    var caseLastSavedCaseId by mutableStateOf<Long?>(null)
    var showCaseAppealDialog by mutableStateOf(false)
    var isProcessingCaseAppeal by mutableStateOf(false)
    var caseAppealOutcome by mutableStateOf<AppealResult?>(null)

    fun isCaseStudyActive(): Boolean {
        return caseStage != CaseStage.WHEEL
    }

    fun resetCaseSession() {
        caseStage = CaseStage.WHEEL
        selectedCase = null
        isCaseTimerRunning = false
        casePreparationTimerMinutes = 30
        caseSecondsRemaining = 30 * 60
        caseTotalSeconds = 30 * 60
        qualificationText = ""
        legalArticlesText = ""
        verdictProposalText = ""
        isCaseEvaluating = false
        caseEvaluationResult = null
        showCaseAppealDialog = false
        caseAppealOutcome = null
    }
}
