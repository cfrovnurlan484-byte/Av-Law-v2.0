package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LegalGold
import com.example.ui.theme.LegalGoldDark
import com.example.ui.theme.LegalNavyDark
import com.example.ui.theme.LegalNavyPrimary
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.random.Random

data class WheelItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val iconEmoji: String = "⚖️"
)

@Composable
fun VerticalReelPicker(
    items: List<WheelItem>,
    isSpinning: Boolean = false,
    headerTitle: String = "Təsadüfi Mövzu Seçimi",
    buttonText: String = "Fırlat",
    onSpinStart: () -> Unit = {},
    onItemSelected: (WheelItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val itemHeightDp = 76.dp
    val density = LocalDensity.current
    val itemHeightPx = with(density) { itemHeightDp.toPx() }

    val scrollAnim = remember { Animatable(0f) }
    var spinningState by remember { mutableStateOf(false) }
    var currentSelectedIndex by remember { mutableIntStateOf(0) }
    var lastHapticIndex by remember { mutableIntStateOf(-1) }

    fun triggerTick() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(android.os.VibratorManager::class.java)
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(18)
                }
            }
        } catch (_: Exception) {}
    }

    fun triggerFinishHaptic() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(android.os.VibratorManager::class.java)
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(55, 255))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(55)
                }
            }
        } catch (_: Exception) {}
    }

    // Monitor reel tick while spinning
    val currentValue = scrollAnim.value
    val currentIntItem = floor(currentValue).toInt()
    if (spinningState && currentIntItem != lastHapticIndex) {
        lastHapticIndex = currentIntItem
        triggerTick()
    }

    fun startSpin() {
        if (spinningState || isSpinning || items.isEmpty()) return
        spinningState = true
        onSpinStart()
        triggerTick()

        scope.launch {
            val currentPos = scrollAnim.value
            val currentBase = floor(currentPos).toInt()
            val targetIdx = Random.nextInt(items.size)
            // Roll through multiple full cycles (e.g. 5 rounds = 5 * items.size items) plus delta to targetIdx
            val fullRevolutions = 5 * items.size
            val currentMod = ((currentBase % items.size) + items.size) % items.size
            var delta = targetIdx - currentMod
            if (delta <= 0) {
                delta += items.size
            }
            val finalTarget = (currentBase + fullRevolutions + delta).toFloat()

            scrollAnim.animateTo(
                targetValue = finalTarget,
                animationSpec = tween(
                    durationMillis = 3400,
                    easing = CubicBezierEasing(0.06f, 0.84f, 0.16f, 1f)
                )
            )

            currentSelectedIndex = targetIdx
            spinningState = false
            triggerFinishHaptic()
            onItemSelected(items[targetIdx])
        }
    }

    fun stepItem(direction: Int) {
        if (spinningState) return
        scope.launch {
            val nextIndex = (currentSelectedIndex + direction + items.size) % items.size
            currentSelectedIndex = nextIndex
            scrollAnim.snapTo((floor(scrollAnim.value) + direction))
            triggerTick()
            onItemSelected(items[nextIndex])
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vertical_reel_picker"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Main Reel Container (Slot-machine drum)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF090D15))
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            LegalGold.copy(alpha = 0.5f),
                            Color(0xFF1E2A3A),
                            LegalGold.copy(alpha = 0.5f)
                        )
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
        ) {
            // Center High-End Aperture / Focus Band
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeightDp)
                    .align(Alignment.Center)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0x00D4AF37),
                                Color(0x18D4AF37),
                                Color(0x2AD4AF37),
                                Color(0x18D4AF37),
                                Color(0x00D4AF37)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                LegalGold.copy(alpha = 0.8f),
                                LegalGold,
                                LegalGold.copy(alpha = 0.8f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )
            ) {
                // Left & Right Gold Indicator Calipers
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(LegalGold)
                        .align(Alignment.CenterStart)
                        .offset(x = 10.dp)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(LegalGold)
                        .align(Alignment.CenterEnd)
                        .offset(x = (-10).dp)
                )
            }

            // Reel Content: Display items relative to current fractional scroll
            val scrollVal = scrollAnim.value
            val baseInt = floor(scrollVal).toInt()
            val fraction = scrollVal - baseInt

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Render from slot -2 (above) to slot +2 (below)
                (-2..2).forEach { slotOffset ->
                    val rawIndex = baseInt + slotOffset
                    val safeItemIndex = ((rawIndex % items.size) + items.size) % items.size
                    val item = items[safeItemIndex]

                    val yOffsetPx = (slotOffset - fraction) * itemHeightPx
                    val distFromCenter = abs(slotOffset - fraction)

                    // Visual attributes based on distance from center
                    val isCenter = distFromCenter <= 0.45f
                    val alphaVal = when {
                        distFromCenter <= 0.5f -> 1f
                        distFromCenter <= 1.5f -> (0.42f - (distFromCenter - 0.5f) * 0.22f).coerceIn(0.18f, 0.42f)
                        else -> 0.12f
                    }
                    val scaleVal = when {
                        distFromCenter <= 0.5f -> 1f
                        distFromCenter <= 1.5f -> (0.88f - (distFromCenter - 0.5f) * 0.12f).coerceIn(0.76f, 0.88f)
                        else -> 0.72f
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeightDp)
                            .offset { IntOffset(0, yOffsetPx.roundToInt()) }
                            .scale(scaleVal)
                            .alpha(alphaVal)
                            .padding(horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (isCenter) {
                                    Text(
                                        text = item.iconEmoji,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(end = 6.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isCenter) LegalGold.copy(alpha = 0.22f)
                                            else Color(0xFF1E2838)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = item.category.uppercase(),
                                        fontSize = if (isCenter) 10.sp else 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp,
                                        color = if (isCenter) LegalGold else Color(0xFF8FA1B8)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = item.title,
                                fontSize = if (isCenter) 16.sp else 13.sp,
                                fontWeight = if (isCenter) FontWeight.Black else FontWeight.Medium,
                                color = if (isCenter) Color.White else Color(0xFF9FB2C8),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )

                            if (isCenter) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.subtitle,
                                    fontSize = 11.sp,
                                    color = Color(0xFFBAC7D5),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Top Gradient Shadow Mask (Creates cylinder 3D depth)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF090D15),
                                Color(0xEB090D15),
                                Color(0x66090D15),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Bottom Gradient Shadow Mask
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0x66090D15),
                                Color(0xEB090D15),
                                Color(0xFF090D15)
                            )
                        )
                    )
            )

            // Step arrows on the right side for manual adjustment
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 6.dp)
            ) {
                IconButton(
                    onClick = { stepItem(-1) },
                    enabled = !spinningState,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Əvvəlki",
                        tint = Color(0xFF8FA1B8)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                IconButton(
                    onClick = { stepItem(1) },
                    enabled = !spinningState,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Növbəti",
                        tint = Color(0xFF8FA1B8)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // High-End Spin Button
        Button(
            onClick = { startSpin() },
            enabled = !spinningState && !isSpinning,
            colors = ButtonDefaults.buttonColors(
                containerColor = LegalGold,
                contentColor = LegalNavyDark,
                disabledContainerColor = LegalGoldDark.copy(alpha = 0.45f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(52.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .testTag("spin_wheel_button")
        ) {
            Icon(
                imageVector = if (spinningState) Icons.Default.Refresh else Icons.Default.Casino,
                contentDescription = buttonText,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = if (spinningState) "Təsadüfi Mövzu Seçilir..." else "$buttonText (Slot Seçimi)",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                letterSpacing = 0.4.sp
            )
        }
    }
}
