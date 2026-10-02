package com.example.ui.screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LegalGold
import com.example.ui.theme.LegalGoldDark
import com.example.ui.theme.LegalNavyDark
import com.example.ui.theme.LegalNavyPrimary
import kotlinx.coroutines.delay

import androidx.compose.material3.TextButton

fun checkNetworkConnectivity(context: Context): Boolean {
    return try {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return true

        val activeNetwork = connectivityManager.activeNetwork
        if (activeNetwork != null) {
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            if (capabilities != null) {
                val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                val isWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                val isCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                val isEthernet = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)

                if (isValidated || hasInternet || isWifi || isCellular || isEthernet) {
                    return true
                }
            }
        }

        // Fallback: Check all registered networks for Wi-Fi and Cellular data
        val allNetworks = connectivityManager.allNetworks
        for (network in allNetworks) {
            val caps = connectivityManager.getNetworkCapabilities(network) ?: continue
            if (caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ||
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            ) {
                return true
            }
        }

        // Never let false-negative network checks block the user
        true
    } catch (e: Throwable) {
        true
    }
}

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val context = LocalContext.current
    var isCheckingInternet by remember { mutableStateOf(true) }
    var showOfflineDialog by remember { mutableStateOf(false) }
    var animationVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animationVisible = true
        delay(1200)
        val isConnected = checkNetworkConnectivity(context)
        if (isConnected) {
            isCheckingInternet = false
            delay(300)
            onSplashFinished()
        } else {
            isCheckingInternet = false
            showOfflineDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        LegalNavyDark,
                        Color(0xFF0F1B2B),
                        Color(0xFF050B14)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = animationVisible,
            enter = fadeIn() + scaleIn()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                // Sleek glowing classic balance emblem
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(LegalNavyDark, LegalNavyPrimary)
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                listOf(LegalGold, Color(0xFFFFF4D0), LegalGoldDark)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.avlaw_clean_icon_1790724069727),
                        contentDescription = "Av-Law Emblemi",
                        modifier = Modifier
                            .size(86.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Av-Law",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LegalGold.copy(alpha = 0.2f))
                        .border(1.dp, LegalGold.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "AZƏRBAYCAN QANUNVERİCİLİYİ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp,
                        color = LegalGold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Vəkillər və Hüquqşünaslar üçün İmtahan və Kazus Portalı",
                    fontSize = 13.sp,
                    color = Color(0xFFCAD4DF),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(36.dp))

                if (isCheckingInternet) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = LegalGold,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Şəbəkə bağlantısı yoxlanılır...",
                            color = Color(0xFFA0B0C0),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Bottom credits: Idea by Gülnar & Created by Nurlan
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Idea by Gülnar",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                color = Color(0xFF9EAEC0)
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Created by Nurlan",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
                color = LegalGold.copy(alpha = 0.9f)
            )
        }

        // Offline Alert Dialog
        if (showOfflineDialog) {
            AlertDialog(
                onDismissRequest = {},
                icon = {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = "Oflayn",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(36.dp)
                    )
                },
                title = {
                    Text(
                        text = "İnternet Bağlantısı Yoxdur",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(
                        text = "Av-Law tətbiqindən istifadə etmək, qanunvericilik bazasına və qiymətləndirmə xidmətinə qoşulmaq üçün aktiv internet bağlantısı tələb olunur. Zəhmət olmasa Wi-Fi və ya Mobil şəbəkənizi yoxlayın.",
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            isCheckingInternet = true
                            if (checkNetworkConnectivity(context)) {
                                showOfflineDialog = false
                                isCheckingInternet = false
                                onSplashFinished()
                            } else {
                                isCheckingInternet = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LegalGoldDark,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Yenidən Yoxla")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showOfflineDialog = false
                            onSplashFinished()
                        }
                    ) {
                        Text("Davam Et", color = LegalGold)
                    }
                }
            )
        }
    }
}
