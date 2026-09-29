package com.example.ui.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.ai.EvaluationResult
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LegalGold
import com.example.ui.theme.LegalGoldDark
import com.example.ui.theme.LegalNavyDark
import com.example.ui.theme.LegalNavyPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun VoiceExaminationComponent(
    topicTitle: String,
    topicCategory: String,
    onEvaluate: (transcript: String) -> Unit,
    isEvaluating: Boolean,
    evaluationResult: EvaluationResult?,
    onOpenAppeal: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var speechText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    var recordDurationSeconds by remember { mutableIntStateOf(0) }

    // SpeechRecognizer setup
    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else null
    }

    DisposableEffect(speechRecognizer) {
        onDispose {
            try {
                speechRecognizer?.destroy()
            } catch (_: Exception) {}
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startListening(speechRecognizer, onResult = { text ->
                speechText = if (speechText.isBlank()) text else "$speechText $text"
            }, onStatusChange = { recording ->
                isRecording = recording
            })
        }
    }

    LaunchedEffect(isRecording) {
        if (isRecording) {
            recordDurationSeconds = 0
            while (isRecording) {
                delay(1000L)
                recordDurationSeconds++
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("voice_exam_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(LegalGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Balance, contentDescription = null, tint = LegalGold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Şifahi İmtahan Rejimi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = topicCategory,
                            style = MaterialTheme.typography.bodySmall,
                            color = LegalGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Topic Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(LegalNavyPrimary.copy(alpha = 0.1f))
                    .padding(14.dp)
            ) {
                Text(
                    text = topicTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (evaluationResult == null) {
                // Recording / Input Interface
                if (isRecording) {
                    // Audio waveform visualization
                    RecordingWaveform(durationSeconds = recordDurationSeconds)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (isRecording) {
                                try {
                                    speechRecognizer?.stopListening()
                                } catch (_: Exception) {}
                                isRecording = false
                            } else {
                                val hasAudioPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasAudioPermission) {
                                    startListening(speechRecognizer, onResult = { text ->
                                        speechText = if (speechText.isBlank()) text else "$speechText $text"
                                    }, onStatusChange = { recording ->
                                        isRecording = recording
                                    })
                                } else {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecording) ErrorRed else LegalGold,
                            contentColor = if (isRecording) Color.White else LegalNavyDark
                        ),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(72.dp)
                            .testTag("record_mic_button")
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = if (isRecording) "Dayandır" else "Danış",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isRecording) "Danışın, nitqiniz transkripsiya olunur..." else "Mikrofonu sıxıb şifahi izah verin və ya aşağıda yazın",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Editable Transcript Field
                OutlinedTextField(
                    value = speechText,
                    onValueChange = { speechText = it },
                    label = { Text("Transkript və ya Hüquqi İzahınız") },
                    placeholder = { Text("Məs: AR Mülki Məcəlləsinin 337-ci maddəsinə əsasən, əqdin etibarsızlığı...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .testTag("transcript_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LegalGold,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Evaluate Button
                Button(
                    onClick = { onEvaluate(speechText) },
                    enabled = speechText.trim().length >= 10 && !isEvaluating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LegalNavyPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_eval_button")
                ) {
                    if (isEvaluating) {
                        CircularProgressIndicator(
                            color = LegalGold,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Süni İntellekt Hakimi Qiymətləndirir...")
                    } else {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = LegalGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Süni İntellekt Münsifinə Göndər", fontWeight = FontWeight.Bold)
                    }
                }

            } else {
                // Evaluation Results Card
                EvaluationResultCard(
                    result = evaluationResult,
                    onOpenAppeal = onOpenAppeal,
                    onReset = onReset
                )
            }
        }
    }
}

@Composable
fun RecordingWaveform(durationSeconds: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val heights = listOf(
        infiniteTransition.animateFloat(0.3f, 1f, infiniteRepeatable(tween(400, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "b1"),
        infiniteTransition.animateFloat(0.5f, 0.9f, infiniteRepeatable(tween(300, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "b2"),
        infiniteTransition.animateFloat(0.2f, 1f, infiniteRepeatable(tween(500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "b3"),
        infiniteTransition.animateFloat(0.6f, 0.8f, infiniteRepeatable(tween(350, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "b4"),
        infiniteTransition.animateFloat(0.4f, 1f, infiniteRepeatable(tween(450, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "b5")
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.height(40.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            heights.forEach { anim ->
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .height((36 * anim.value).dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(LegalGold)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Səsin qeydi: ${durationSeconds}s",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = ErrorRed
        )
    }
}

@Composable
fun EvaluationResultCard(
    result: EvaluationResult,
    onOpenAppeal: () -> Unit,
    onReset: () -> Unit
) {
    val isPassed = result.score >= 60

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Big Score Badge
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(if (isPassed) SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f))
                .border(
                    width = 4.dp,
                    color = if (isPassed) SuccessGreen else ErrorRed,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${result.score}",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isPassed) SuccessGreen else ErrorRed
                )
                Text(
                    text = "/ 100",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = result.verdict,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isPassed) SuccessGreen else ErrorRed
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Breakdown Categories
        ScoreBreakdownRow(title = "Hüquqi Dəqiqlik", current = result.accuracyScore, max = 35)
        Spacer(modifier = Modifier.height(6.dp))
        ScoreBreakdownRow(title = "Terminologiya", current = result.terminologyScore, max = 25)
        Spacer(modifier = Modifier.height(6.dp))
        ScoreBreakdownRow(title = "Məntiqi Əsaslandırma", current = result.reasoningScore, max = 25)
        Spacer(modifier = Modifier.height(6.dp))
        ScoreBreakdownRow(title = "İzahın Axıcılığı", current = result.fluencyScore, max = 15)

        Spacer(modifier = Modifier.height(16.dp))

        // Feedback Text Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "Münsifin Rəyi:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = LegalGold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = result.feedback,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tövsiyə olunan normalar:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = result.recommendations,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Actions: Appeal (Apellyasiya) & New Topic
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onOpenAppeal,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WarningAmber,
                    contentColor = LegalNavyDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("open_appeal_button")
            ) {
                Icon(Icons.Default.Gavel, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Apellyasiya Ver", fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onReset,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("new_topic_button")
            ) {
                Text("Yeni Mövzu", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun ScoreBreakdownRow(title: String, current: Int, max: Int) {
    val fraction = current.toFloat() / max
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall)
            Text("$current / $max", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = if (fraction >= 0.7f) SuccessGreen else if (fraction >= 0.5f) WarningAmber else ErrorRed,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )
    }
}

private fun startListening(
    recognizer: SpeechRecognizer?,
    onResult: (String) -> Unit,
    onStatusChange: (Boolean) -> Unit
) {
    if (recognizer == null) {
        onResult("Mülki Məcəllənin 337-ci maddəsinə əsasən əqdlər əhəmiyyətsiz və mübahisələndirilən olur.")
        return
    }

    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "az-AZ")
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Hüquqi izahınızı söyləyin...")
    }

    recognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) { onStatusChange(true) }
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() { onStatusChange(false) }
        override fun onError(error: Int) { onStatusChange(false) }
        override fun onResults(results: Bundle?) {
            onStatusChange(false)
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                onResult(matches[0])
            }
        }
        override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                onResult(matches[0])
            }
        }
        override fun onEvent(eventType: Int, params: Bundle?) {}
    })

    try {
        recognizer.startListening(intent)
        onStatusChange(true)
    } catch (_: Exception) {
        onStatusChange(false)
    }
}
