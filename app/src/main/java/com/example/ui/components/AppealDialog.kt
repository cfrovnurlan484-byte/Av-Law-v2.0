package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.ai.AppealResult
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LegalGold
import com.example.ui.theme.LegalNavyDark
import com.example.ui.theme.LegalNavyPrimary
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber

@Composable
fun AppealDialog(
    userCurrentPoints: Int,
    examTitle: String,
    onDismiss: () -> Unit,
    onSubmitAppeal: (wager: Int, justification: String) -> Unit,
    isProcessing: Boolean,
    appealResult: AppealResult?
) {
    val maxAvailableWager = userCurrentPoints.coerceAtLeast(20)
    var wagerPoints by remember { mutableIntStateOf(25.coerceAtMost(maxAvailableWager)) }
    var justificationText by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "gavel")
    val gavelAngle by infiniteTransition.animateFloat(
        initialValue = -25f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gavel_rotation"
    )

    Dialog(onDismissRequest = { if (!isProcessing) onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("appeal_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top bar with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = LegalGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Apellyasiya Kollegiyası",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (!isProcessing) {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Bağla")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (isProcessing) {
                    // Deliberation in progress
                    Spacer(modifier = Modifier.height(20.dp))
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = LegalGold,
                        modifier = Modifier
                            .size(64.dp)
                            .rotate(gavelAngle)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Hakimlər Kollegiyası İşi Araşdırır...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Təqdim olunmuş hüquqi arqumentlər AR Məcəllələri və məhkəmə presedentləri ilə müqayisə edilir.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                } else if (appealResult != null) {
                    // Result Display
                    val isWon = appealResult.isSuccess
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(if (isWon) SuccessGreen.copy(alpha = 0.15f) else ErrorRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isWon) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (isWon) SuccessGreen else ErrorRed,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isWon) "Şikayət Təmin Olundu!" else "Şikayət Təmin Edilmədi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isWon) SuccessGreen else ErrorRed
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isWon)
                            "Qoyulan $wagerPoints xal bərpa olundu və +${(wagerPoints * 0.5f).toInt()} bonus xal əlavə edildi!"
                        else
                            "Riskə qoyulmuş $wagerPoints xal cərimə olaraq silindi.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "Kollegiyanın Qərarı:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = LegalGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = appealResult.reasoning,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LegalNavyPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Tamamla", fontWeight = FontWeight.Bold)
                    }

                } else {
                    // Appeal Form
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(WarningAmber.copy(alpha = 0.12f))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = WarningAmber)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Diqqət: Sui-istifadənin qarşısını almaq üçün xal riskə qoyulmalıdır. Şikayət təmin edilsə xalınız +50% bonusla qayıdır; rədd edilsə mərc itirilir!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Wager Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Riskə Qoyulan Xal:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$wagerPoints Xal",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = WarningAmber
                        )
                    }

                    Slider(
                        value = wagerPoints.toFloat(),
                        onValueChange = { wagerPoints = it.toInt() },
                        valueRange = 10f..50f.coerceAtMost(maxAvailableWager.toFloat()),
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = WarningAmber,
                            activeTrackColor = WarningAmber
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("appeal_wager_slider")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Justification TextField
                    OutlinedTextField(
                        value = justificationText,
                        onValueChange = { justificationText = it },
                        label = { Text("Apellyasiya Əsaslandırmanız") },
                        placeholder = { Text("Niyə qiymətləndirmə ilə razı deyilsiniz? AR qanunvericiliyinə istinad edin...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("appeal_justification_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LegalGold,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            onSubmitAppeal(wagerPoints, justificationText)
                        },
                        enabled = justificationText.trim().length >= 15,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WarningAmber,
                            contentColor = LegalNavyDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_appeal_button")
                    ) {
                        Icon(Icons.Default.Gavel, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Şikayəti Təqdim Et ($wagerPoints Xal)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
