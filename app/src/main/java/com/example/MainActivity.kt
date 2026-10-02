package com.example

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.data.firestore.FirestoreRepository
import com.example.data.firestore.model.FirebaseUserModel
import com.example.data.local.AppDatabase
import com.example.data.repository.LegalRepository
import com.example.ui.navigation.ExamSessionManager
import com.example.ui.navigation.LegalTab
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CaseStudyScreen
import com.example.ui.screens.LegalLibraryScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ShowcaseScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TheoryCheckScreen
import com.example.ui.theme.AzHuquqTheme
import com.example.ui.theme.BurgundyBackground
import com.example.ui.theme.BurgundyBackgroundGradientEnd
import com.example.ui.theme.BurgundyBorder
import com.example.ui.theme.EmeraldBackground
import com.example.ui.theme.EmeraldBackgroundGradientEnd
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.EspressoBackground
import com.example.ui.theme.EspressoBackgroundGradientEnd
import com.example.ui.theme.EspressoBorder
import com.example.ui.theme.LegalGold
import com.example.ui.theme.LegalGoldDark
import com.example.ui.theme.LegalNavyDark
import com.example.ui.theme.LegalNavyPrimary
import com.example.ui.theme.LuxuryGold
import com.example.ui.theme.LuxuryGoldDark
import com.example.ui.theme.LuxuryTextHighContrast
import com.example.ui.theme.LuxuryTextMuted
import com.example.ui.theme.MidnightNavyBackground
import com.example.ui.theme.MidnightNavyBackgroundGradientEnd
import com.example.ui.theme.MidnightNavyBorder
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBackgroundGradientEnd
import com.example.ui.theme.ObsidianBorder
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var repository: LegalRepository
    private lateinit var firestoreRepository: FirestoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            if (com.google.firebase.FirebaseApp.getApps(this).isEmpty()) {
                com.google.firebase.FirebaseApp.initializeApp(this)
            }
        } catch (e: Throwable) {
            Log.e("MainActivity", "FirebaseApp init error", e)
        }

        val firestoreDb = try {
            val resId = resources.getIdentifier("firestore_database_id", "string", packageName)
            val dbId = if (resId != 0) getString(resId) else null
            if (!dbId.isNullOrBlank()) {
                FirebaseFirestore.getInstance(dbId)
            } else {
                FirebaseFirestore.getInstance()
            }
        } catch (e: Throwable) {
            Log.e("MainActivity", "Firestore instance error, using fallback null", e)
            null
        }
        firestoreRepository = FirestoreRepository(firestoreDb)

        try {
            database = AppDatabase.getDatabase(this, lifecycleScope)
        } catch (t: Throwable) {
            Log.e("MainActivity", "Failed to get database, falling back", t)
            database = AppDatabase.createFallbackDatabase()
        }
        repository = LegalRepository(database)

        setContent {
            AzHuquqTheme {
                var isSplashActive by remember { mutableStateOf(true) }

                if (isSplashActive) {
                    SplashScreen(
                        onSplashFinished = { isSplashActive = false }
                    )
                } else {
                    AppRootGate(
                        repository = repository,
                        firestoreRepository = firestoreRepository
                    )
                }
            }
        }
    }
}

@Composable
fun AppRootGate(
    repository: LegalRepository,
    firestoreRepository: FirestoreRepository
) {
    val auth = remember {
        try {
            Firebase.auth
        } catch (e: Throwable) {
            try {
                FirebaseAuth.getInstance()
            } catch (e2: Throwable) {
                null
            }
        }
    }
    var firebaseUser by remember { mutableStateOf(auth?.currentUser) }
    var currentUserProfile by remember { mutableStateOf<FirebaseUserModel?>(null) }
    var isLoadingProfile by remember { mutableStateOf(auth?.currentUser != null) }

    DisposableEffect(auth) {
        if (auth == null) {
            return@DisposableEffect onDispose {}
        }
        val listener = FirebaseAuth.AuthStateListener { fa ->
            firebaseUser = fa.currentUser
            if (fa.currentUser == null) {
                currentUserProfile = null
                isLoadingProfile = false
            }
        }
        try {
            auth.addAuthStateListener(listener)
        } catch (e: Throwable) {
            Log.e("AppRootGate", "Error adding auth state listener", e)
        }
        onDispose {
            try {
                auth.removeAuthStateListener(listener)
            } catch (e: Throwable) {
                Log.e("AppRootGate", "Error removing auth state listener", e)
            }
        }
    }

    LaunchedEffect(firebaseUser?.uid) {
        val uid = firebaseUser?.uid
        if (uid != null) {
            isLoadingProfile = true
            try {
                firestoreRepository.observeUser(uid).collect { profile ->
                    currentUserProfile = profile
                    isLoadingProfile = false
                }
            } catch (e: Throwable) {
                Log.e("AppRootGate", "Error collecting user profile", e)
                isLoadingProfile = false
            }
        } else {
            currentUserProfile = null
            isLoadingProfile = false
        }
    }

    if (isLoadingProfile) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LegalNavyDark),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = LegalGold)
        }
    } else if (firebaseUser == null || currentUserProfile == null) {
        AuthScreen(
            firestoreRepository = firestoreRepository,
            onAuthSuccess = { profile ->
                currentUserProfile = profile
            }
        )
    } else {
        MainAppScreen(
            repository = repository,
            firestoreRepository = firestoreRepository,
            currentUser = currentUserProfile!!,
            onSignOut = {
                currentUserProfile = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    repository: LegalRepository,
    firestoreRepository: FirestoreRepository,
    currentUser: FirebaseUserModel,
    onSignOut: () -> Unit
) {
    var currentTab by remember { mutableStateOf(LegalTab.THEORY_CHECK) }
    val sessionManager = remember { ExamSessionManager() }

    // Seed local legal sources on launch
    LaunchedEffect(Unit) {
        repository.checkAndSeedInitialData()
    }

    // BackHandler: return to Theory Check if secondary tab is open
    BackHandler(enabled = currentTab != LegalTab.THEORY_CHECK) {
        currentTab = LegalTab.THEORY_CHECK
    }

    val currentTopBarBg = when (currentTab) {
        LegalTab.PROFILE -> BurgundyBackground
        LegalTab.THEORY_CHECK -> MidnightNavyBackground
        LegalTab.CASE_STUDY -> EmeraldBackground
        LegalTab.SHOWCASE -> ObsidianBackground
        LegalTab.LEGAL_LIBRARY -> EspressoBackground
    }

    val currentBorderColor = when (currentTab) {
        LegalTab.PROFILE -> BurgundyBorder
        LegalTab.THEORY_CHECK -> MidnightNavyBorder
        LegalTab.CASE_STUDY -> EmeraldBorder
        LegalTab.SHOWCASE -> ObsidianBorder
        LegalTab.LEGAL_LIBRARY -> EspressoBorder
    }

    val currentBottomBarBg = when (currentTab) {
        LegalTab.PROFILE -> BurgundyBackgroundGradientEnd
        LegalTab.THEORY_CHECK -> MidnightNavyBackgroundGradientEnd
        LegalTab.CASE_STUDY -> EmeraldBackgroundGradientEnd
        LegalTab.SHOWCASE -> ObsidianBackgroundGradientEnd
        LegalTab.LEGAL_LIBRARY -> EspressoBackgroundGradientEnd
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = currentTopBarBg,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .border(width = 0.8.dp, color = currentBorderColor.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sleek glowing classic balance emblem
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F141E))
                            .border(
                                width = 1.5.dp,
                                brush = Brush.linearGradient(
                                    listOf(LuxuryGold, Color(0xFFFFF4D0), LuxuryGoldDark)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.avlaw_clean_icon_1790724069727),
                            contentDescription = "Av-Law Emblemi",
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Av-Law",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp,
                                color = LuxuryTextHighContrast
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(LuxuryGold.copy(alpha = 0.18f))
                                    .border(
                                        width = 0.8.dp,
                                        color = LuxuryGold,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (currentUser.role == "admin") "ADMIN" else "AZ QANUNVERİCİLİYİ",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.4.sp,
                                    color = LuxuryGold
                                )
                            }
                        }
                        Text(
                            text = "Xoş gəldiniz, ${currentUser.nickname}",
                            style = MaterialTheme.typography.labelSmall,
                            color = LuxuryTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .border(width = 0.8.dp, color = currentBorderColor.copy(alpha = 0.5f))
                    .testTag("bottom_navigation_bar"),
                containerColor = currentBottomBarBg,
                tonalElevation = 8.dp
            ) {
                LegalTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF140D04),
                            selectedTextColor = LuxuryGold,
                            indicatorColor = LuxuryGold,
                            unselectedIconColor = LuxuryTextMuted,
                            unselectedTextColor = LuxuryTextMuted
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentTab,
                label = "tab_crossfade"
            ) { tab ->
                when (tab) {
                    LegalTab.PROFILE -> ProfileScreen(
                        repository = repository,
                        firestoreRepository = firestoreRepository,
                        currentUser = currentUser,
                        onSignOut = onSignOut
                    )
                    LegalTab.THEORY_CHECK -> TheoryCheckScreen(
                        repository = repository,
                        firestoreRepository = firestoreRepository,
                        currentUser = currentUser,
                        sessionManager = sessionManager,
                        onNavigateToLibrary = { currentTab = LegalTab.LEGAL_LIBRARY }
                    )
                    LegalTab.CASE_STUDY -> CaseStudyScreen(
                        repository = repository,
                        firestoreRepository = firestoreRepository,
                        currentUser = currentUser,
                        sessionManager = sessionManager,
                        onNavigateToLibrary = { currentTab = LegalTab.LEGAL_LIBRARY }
                    )
                    LegalTab.SHOWCASE -> ShowcaseScreen(
                        firestoreRepository = firestoreRepository,
                        currentUser = currentUser
                    )
                    LegalTab.LEGAL_LIBRARY -> LegalLibraryScreen(
                        repository = repository,
                        firestoreRepository = firestoreRepository,
                        currentUser = currentUser
                    )
                }
            }
        }
    }
}
