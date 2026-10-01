package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.firestore.FirestoreRepository
import com.example.data.firestore.model.FirebaseUserModel
import com.example.data.local.model.ExamResultEntity
import com.example.data.repository.LegalRepository
import com.example.ui.theme.BurgundyBackground
import com.example.ui.theme.BurgundyBackgroundGradientEnd
import com.example.ui.theme.BurgundyBorder
import com.example.ui.theme.BurgundySurface
import com.example.ui.theme.BurgundySurfaceElevated
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.LuxuryGoldDark
import com.example.ui.theme.LuxuryGoldLight
import com.example.ui.theme.LuxuryTextHighContrast
import com.example.ui.theme.LuxuryTextMuted
import com.example.ui.theme.SuccessGreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    repository: LegalRepository,
    firestoreRepository: FirestoreRepository,
    currentUser: FirebaseUserModel?,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val allExams by repository.allExams.collectAsState(initial = emptyList())

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedExamDetail by remember { mutableStateOf<ExamResultEntity?>(null) }
    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var adminPassphraseInput by remember { mutableStateOf("") }
    var adminError by remember { mutableStateOf<String?>(null) }

    val isAdmin = currentUser?.role == "admin"
    val rankTitle = when {
        isAdmin -> "Baş Hüquqşünas & Administrator"
        (currentUser?.points ?: 0) >= 300 -> "Baş Hüquqşünas"
        (currentUser?.points ?: 0) >= 150 -> "Təcrübəli Vəkil"
        (currentUser?.points ?: 0) >= 50 -> "Kiçik Hüquqşünas"
        else -> "Stajor Hüquqşünas"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        BurgundyBackground,
                        BurgundyBackgroundGradientEnd,
                        Color(0xFF0A0204)
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
            // Executive Top Profile Card in Very Dark Burgundy & Wine-Red
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BurgundyBorder, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BurgundySurface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(82.dp)
                            .clip(CircleShape)
                            .background(BurgundySurfaceElevated)
                            .border(1.5.dp, LuxuryGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profil",
                            tint = LuxuryGold,
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = currentUser?.nickname?.ifBlank { "Hüquqşünas" } ?: "Hüquqşünas",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.4.sp,
                        color = LuxuryTextHighContrast
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(LuxuryGold.copy(alpha = 0.18f))
                                .border(1.dp, LuxuryGold.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = rankTitle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = LuxuryGold
                            )
                        }

                        if (currentUser?.email?.isNotBlank() == true) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentUser.email,
                                fontSize = 11.sp,
                                color = LuxuryTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Real Stats Grid with Polished Gold Accents
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(BurgundySurfaceElevated)
                            .border(0.8.dp, BurgundyBorder.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStatItem(
                            title = "Ümumi Xal",
                            value = "${currentUser?.points ?: 0}",
                            icon = Icons.Default.EmojiEvents,
                            tint = LuxuryGold
                        )
                        ProfileStatItem(
                            title = "Uğur Seriyası",
                            value = "${currentUser?.streak ?: 0} gün",
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            tint = SuccessGreen
                        )
                        ProfileStatItem(
                            title = "Həll Olunan",
                            value = "${allExams.size}",
                            icon = Icons.Default.Gavel,
                            tint = LuxuryGoldLight
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Actions: Admin Mode & Logout
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (!isAdmin) {
                            OutlinedButton(
                                onClick = { showAdminLoginDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = LuxuryGold
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.5f))
                            ) {
                                Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Admin Açarı", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                Firebase.auth.signOut()
                                onSignOut()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = LuxuryTextMuted
                            ),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, BurgundyBorder)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Çıxış Et", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tabs: İmtahan Tarixçəsi vs Nailiyyətlər
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BurgundySurface,
                contentColor = LuxuryGold,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(0.8.dp, BurgundyBorder, RoundedCornerShape(12.dp)),
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = LuxuryGold
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Real İmtahan Tarixçəsi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedTab == 0) LuxuryGold else LuxuryTextMuted
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Nailiyyətlər",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedTab == 1) LuxuryGold else LuxuryTextMuted
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    // Real Exams History
                    if (allExams.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(BurgundySurface)
                                .border(0.8.dp, BurgundyBorder, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    tint = LuxuryGold.copy(alpha = 0.7f),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Hələ heç bir imtahan verilməyib",
                                    color = LuxuryTextHighContrast,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Nəzəriyyə və ya Kazus bölməsindən ilk testinizi tamamlayın",
                                    color = LuxuryTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            allExams.forEach { exam ->
                                RealExamHistoryCard(
                                    exam = exam,
                                    onClick = { selectedExamDetail = exam }
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // Real Achievements
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        AchievementCard(
                            title = "İlk Hüquqi Addım",
                            description = "İlk nəzəriyyə və ya kazus imtahanını uğurla tamamla",
                            isUnlocked = allExams.isNotEmpty(),
                            progress = if (allExams.isNotEmpty()) "Tamamlandı" else "0/1"
                        )
                        AchievementCard(
                            title = "Mülki Hüquq Mütəxəssisi",
                            description = "Mülki Məcəllə üzrə 3 imtahanı 70+ balla bitir",
                            isUnlocked = allExams.count { it.topicCategory.contains("Mülki") && it.score >= 70 } >= 3,
                            progress = "${allExams.count { it.topicCategory.contains("Mülki") && it.score >= 70 }}/3"
                        )
                        AchievementCard(
                            title = "Ədalət Zirvəsi",
                            description = "100 bal toplayaraq 'Təcrübəli Vəkil' dərəcəsinə yüksəl",
                            isUnlocked = (currentUser?.points ?: 0) >= 100,
                            progress = "${currentUser?.points ?: 0}/100"
                        )
                    }
                }
            }
        }
    }

    // Admin Passphrase Dialog
    if (showAdminLoginDialog) {
        AlertDialog(
            onDismissRequest = {
                showAdminLoginDialog = false
                adminError = null
                adminPassphraseInput = ""
            },
            containerColor = BurgundySurfaceElevated,
            titleContentColor = LuxuryTextHighContrast,
            textContentColor = LuxuryTextMuted,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = LuxuryGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Admin Girişi", fontWeight = FontWeight.Bold, color = LuxuryTextHighContrast)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Qanunvericilik materiallarını birbaşa idarə etmək üçün Master Parolu daxil edin.",
                        fontSize = 12.sp,
                        color = LuxuryTextMuted
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = adminPassphraseInput,
                        onValueChange = {
                            adminPassphraseInput = it
                            adminError = null
                        },
                        label = { Text("Master Parol") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LuxuryGold,
                            unfocusedBorderColor = BurgundyBorder,
                            focusedTextColor = LuxuryTextHighContrast,
                            unfocusedTextColor = LuxuryTextHighContrast,
                            focusedLabelColor = LuxuryGold
                        ),
                        isError = adminError != null
                    )
                    if (adminError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = adminError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (adminPassphraseInput.trim() == "AvLawAdmin2026!MasterKey" || adminPassphraseInput.trim() == "AvLawAdmin2026") {
                            scope.launch {
                                val uid = currentUser?.id ?: ""
                                if (uid.isNotBlank()) {
                                    firestoreRepository.setUserRole(uid, "admin")
                                }
                                showAdminLoginDialog = false
                                adminPassphraseInput = ""
                                Toast.makeText(context, "Admin səlahiyyətləri aktivləşdirildi!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            adminError = "Daxil edilən master parol yalnışdır."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold, contentColor = Color(0xFF1A0508))
                ) {
                    Text("Təsdiq Et", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAdminLoginDialog = false },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LuxuryTextMuted)
                ) {
                    Text("Ləğv Et")
                }
            }
        )
    }

    // Exam Detail Dialog
    selectedExamDetail?.let { exam ->
        Dialog(onDismissRequest = { selectedExamDetail = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .border(1.dp, BurgundyBorder, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BurgundySurfaceElevated)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = exam.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = LuxuryTextHighContrast,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { selectedExamDetail = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Bağla", tint = LuxuryTextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Bal: ${exam.score}/100 - ${exam.verdict}",
                        fontWeight = FontWeight.Bold,
                        color = if (exam.score >= 60) SuccessGreen else ErrorRed,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Cavabınız:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = LuxuryGold
                    )
                    Text(
                        text = exam.userAnswerText,
                        fontSize = 12.sp,
                        color = LuxuryTextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Süni İntellektin Rəyi:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = LuxuryGold
                    )
                    Text(
                        text = exam.feedback,
                        fontSize = 12.sp,
                        color = LuxuryTextHighContrast
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileStatItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = 0.15f))
                .border(1.dp, tint.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxuryTextHighContrast)
        Text(text = title, fontSize = 11.sp, color = LuxuryTextMuted)
    }
}

@Composable
fun RealExamHistoryCard(
    exam: ExamResultEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.8.dp, BurgundyBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = BurgundySurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exam.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = LuxuryTextHighContrast,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(exam.createdAt))
                Text(
                    text = "${exam.topicCategory} • $dateStr",
                    fontSize = 11.sp,
                    color = LuxuryTextMuted
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (exam.score >= 60) SuccessGreen.copy(alpha = 0.18f) else ErrorRed.copy(alpha = 0.18f))
                    .border(0.8.dp, if (exam.score >= 60) SuccessGreen.copy(alpha = 0.5f) else ErrorRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${exam.score} Bal",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (exam.score >= 60) SuccessGreen else ErrorRed
                )
            }
        }
    }
}

@Composable
fun AchievementCard(
    title: String,
    description: String,
    isUnlocked: Boolean,
    progress: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 0.8.dp,
                color = if (isUnlocked) LuxuryGold.copy(alpha = 0.4f) else BurgundyBorder,
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) BurgundySurfaceElevated else BurgundySurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isUnlocked) LuxuryGold else Color(0xFF22080D))
                    .border(1.dp, if (isUnlocked) LuxuryGoldDark else BurgundyBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (isUnlocked) Color(0xFF1A0508) else Color(0xFF6B3A45),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (isUnlocked) LuxuryGold else LuxuryTextHighContrast
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = LuxuryTextMuted
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = progress,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (isUnlocked) LuxuryGold else LuxuryTextMuted
            )
        }
    }
}
