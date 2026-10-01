package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LegalGold
import com.example.ui.theme.LegalGoldDark
import com.example.ui.theme.LegalNavyDark
import com.example.ui.theme.LegalNavyPrimary
import com.example.ui.theme.WarningAmber
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun CountdownTimerView(
    isRunning: Boolean,
    initialMinutes: Int,
    persistedSecondsRemaining: Int? = null,
    persistedTotalSeconds: Int? = null,
    onStartTimer: (minutes: Int) -> Unit,
    onSecondsTick: ((seconds: Int) -> Unit)? = null,
    onTimeExpired: () -> Unit,
    onReadyNow: () -> Unit,
    onOpenLibrary: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMinutes by remember(initialMinutes) { mutableIntStateOf(initialMinutes.coerceIn(10, 60)) }
    var secondsRemaining by remember(persistedSecondsRemaining, selectedMinutes) {
        mutableIntStateOf(persistedSecondsRemaining ?: (selectedMinutes * 60))
    }
    var totalSeconds by remember(persistedTotalSeconds, selectedMinutes) {
        mutableIntStateOf(persistedTotalSeconds ?: (selectedMinutes * 60))
    }

    LaunchedEffect(isRunning, totalSeconds) {
        if (isRunning) {
            while (secondsRemaining > 0) {
                delay(1000L)
                secondsRemaining--
                onSecondsTick?.invoke(secondsRemaining)
            }
            if (secondsRemaining <= 0) {
                onTimeExpired()
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("countdown_timer_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.HourglassEmpty,
                    contentDescription = null,
                    tint = LegalGold,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRunning) "Zaman Təzyiqi Rejimi Aktivdir" else "Tədqiqat Vaxtı Seçimi (10 - 60 Dəq)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!isRunning) {
                // Interactive Configuration Slider (10 to 60 minutes)
                Text(
                    text = "$selectedMinutes Dəqiqə",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = LegalGold
                )
                Text(
                    text = "Mövzunu araşdırmaq və hüquqi bazanı öyrənmək üçün vaxt pəncərəsi",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Slider(
                    value = selectedMinutes.toFloat(),
                    onValueChange = {
                        selectedMinutes = it.toInt()
                        secondsRemaining = selectedMinutes * 60
                        totalSeconds = selectedMinutes * 60
                        onSecondsTick?.invoke(secondsRemaining)
                    },
                    valueRange = 10f..60f,
                    steps = 9, // increments of 5 minutes: 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60
                    colors = SliderDefaults.colors(
                        thumbColor = LegalGold,
                        activeTrackColor = LegalGold,
                        inactiveTrackColor = LegalGold.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("timer_slider")
                )

                // Quick Selection Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(10, 15, 30, 45, 60).forEach { mins ->
                        FilterChip(
                            selected = selectedMinutes == mins,
                            onClick = {
                                selectedMinutes = mins
                                secondsRemaining = mins * 60
                                totalSeconds = mins * 60
                                onSecondsTick?.invoke(secondsRemaining)
                            },
                            label = { Text("${mins}d") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LegalGold,
                                selectedLabelColor = LegalNavyDark
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        secondsRemaining = selectedMinutes * 60
                        totalSeconds = selectedMinutes * 60
                        onStartTimer(selectedMinutes)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LegalNavyPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("start_timer_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hazırlıq Taymerini Başlat ($selectedMinutes dəq)", fontWeight = FontWeight.Bold)
                }

            } else {
                // Running Countdown Mode
                val minutesLeft = secondsRemaining / 60
                val secsLeft = secondsRemaining % 60
                val progress = if (totalSeconds > 0) secondsRemaining.toFloat() / totalSeconds else 0f
                val formattedTime = String.format(Locale.US, "%02d:%02d", minutesLeft, secsLeft)

                Text(
                    text = formattedTime,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    color = if (secondsRemaining < 120) WarningAmber else LegalGold
                )

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (secondsRemaining < 120) WarningAmber else LegalGold,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenLibrary,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("open_sources_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = LegalGold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Mənbələr", maxLines = 1)
                    }

                    Button(
                        onClick = onReadyNow,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LegalGold,
                            contentColor = LegalNavyDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("ready_for_exam_button")
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("İmtahana Başla", fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                }
            }
        }
    }
}
