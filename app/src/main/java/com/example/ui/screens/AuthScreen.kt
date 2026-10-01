package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.data.firestore.FirestoreRepository
import com.example.data.firestore.model.FirebaseUserModel
import com.example.ui.theme.LegalGold
import com.example.ui.theme.LegalGoldDark
import com.example.ui.theme.LegalNavyDark
import com.example.ui.theme.LegalNavyPrimary
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun AuthScreen(
    firestoreRepository: FirestoreRepository,
    onAuthSuccess: (FirebaseUserModel) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = Firebase.auth

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSignUpMode by remember { mutableStateOf(false) }

    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var nicknameInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var nicknameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // First-time Google user nickname dialog
    var showNicknameSetup by remember { mutableStateOf(false) }
    var tempUid by remember { mutableStateOf("") }
    var tempEmail by remember { mutableStateOf("") }
    var dialogNicknameInput by remember { mutableStateOf("") }
    var dialogPasswordInput by remember { mutableStateOf("") }
    var isDialogPasswordVisible by remember { mutableStateOf(false) }
    var dialogNicknameError by remember { mutableStateOf<String?>(null) }
    var dialogPasswordError by remember { mutableStateOf<String?>(null) }
    var isRegistering by remember { mutableStateOf(false) }

    val digitsCount = passwordInput.count { it.isDigit() }
    val lettersCount = passwordInput.count { it.isLetter() }
    val isPasswordPolicyMet = digitsCount >= 8 && lettersCount >= 1

    val dialogDigitsCount = dialogPasswordInput.count { it.isDigit() }
    val dialogLettersCount = dialogPasswordInput.count { it.isLetter() }
    val isDialogPasswordPolicyMet = dialogDigitsCount >= 8 && dialogLettersCount >= 1

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Glowing Av-Law emblem
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(22.dp))
                .background(LegalNavyDark)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        listOf(LegalGold, Color(0xFFFFF4D0), LegalGoldDark)
                    ),
                    shape = RoundedCornerShape(22.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avlaw_clean_icon_1790724069727),
                    contentDescription = "Av-Law Emblemi",
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(18.dp))
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Av-Law",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Azərbaycan Hüquq Portalı və İmtahan Sistemi",
                fontSize = 13.sp,
                color = LegalGold,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF132032)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Mode Switcher Tabs: Sign In / Sign Up
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0C1624))
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (!isSignUpMode) LegalGold else Color.Transparent)
                                .clickable {
                                    isSignUpMode = false
                                    errorMessage = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Daxil Ol",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (!isSignUpMode) LegalNavyDark else Color(0xFFA0B4C8)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSignUpMode) LegalGold else Color.Transparent)
                                .clickable {
                                    isSignUpMode = true
                                    errorMessage = null
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Qeydiyyat",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isSignUpMode) LegalNavyDark else Color(0xFFA0B4C8)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (errorMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    if (isSignUpMode) {
                        // Unique Nickname Input
                        OutlinedTextField(
                            value = nicknameInput,
                            onValueChange = {
                                nicknameInput = it
                                nicknameError = null
                            },
                            label = { Text("Unikal Ləqəb (Nickname)") },
                            placeholder = { Text("məs: Vəkil_Samir") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = LegalGold) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_nickname_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LegalGold,
                                unfocusedBorderColor = Color(0xFF334A6A),
                                focusedLabelColor = LegalGold,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            isError = nicknameError != null
                        )

                        if (nicknameError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = nicknameError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 11.sp,
                                modifier = Modifier.align(Alignment.Start)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Email Input
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("E-poçt Ünvanı") },
                        placeholder = { Text("huquqsunas@azlaw.az") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = LegalGold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(if (isSignUpMode) "register_email_input" else "login_email_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LegalGold,
                            unfocusedBorderColor = Color(0xFF334A6A),
                            focusedLabelColor = LegalGold,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Input
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            passwordError = null
                        },
                        label = { Text("Şifrə") },
                        placeholder = { Text(if (isSignUpMode) "Ən azı 8 rəqəm və 1 hərf" else "Şifrənizi daxil edin") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = LegalGold) },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color(0xFFA0B4C8)
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(if (isSignUpMode) "register_password_input" else "login_password_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LegalGold,
                            unfocusedBorderColor = Color(0xFF334A6A),
                            focusedLabelColor = LegalGold,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        isError = passwordError != null
                    )

                    if (isSignUpMode) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Live Strict Password Policy Indicators
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0C1827))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Məcburi Şifrə Qaydaları:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFCAD8E8)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (digitsCount >= 8) Color(0xFF4CAF50) else Color(0xFF8899A6),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Ən azı 8 rəqəm ($digitsCount/8)",
                                    fontSize = 11.sp,
                                    color = if (digitsCount >= 8) Color(0xFF4CAF50) else Color(0xFF8899A6)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (lettersCount >= 1) Color(0xFF4CAF50) else Color(0xFF8899A6),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Ən azı 1 hərf ($lettersCount/1)",
                                    fontSize = 11.sp,
                                    color = if (lettersCount >= 1) Color(0xFF4CAF50) else Color(0xFF8899A6)
                                )
                            }
                        }
                    }

                    if (passwordError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = passwordError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Primary Submit Button (Email/Password)
                    Button(
                        onClick = {
                            if (isLoading) return@Button
                            val cleanEmail = emailInput.trim()
                            val cleanPass = passwordInput.trim()

                            if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
                                errorMessage = "Düzgün e-poçt ünvanı daxil edin."
                                return@Button
                            }

                            if (isSignUpMode) {
                                val cleanNick = nicknameInput.trim()
                                if (cleanNick.length < 3) {
                                    nicknameError = "Ləqəb ən azı 3 simvoldan ibarət olmalıdır."
                                    return@Button
                                }
                                if (!isPasswordPolicyMet) {
                                    passwordError = "Şifrə tələblərə cavab vermir: ən azı 8 rəqəm və 1 hərf olmalıdır."
                                    return@Button
                                }

                                isLoading = true
                                errorMessage = null
                                scope.launch {
                                    val isAvailable = firestoreRepository.checkNicknameAvailable(cleanNick)
                                    if (!isAvailable) {
                                        isLoading = false
                                        nicknameError = "Bu ləqəb artıq başqa istifadəçi tərəfindən götürülüb. Fərqli bir ləqəb seçin."
                                        return@launch
                                    }

                                    try {
                                        val authResult = auth.createUserWithEmailAndPassword(cleanEmail, cleanPass).await()
                                        val user = authResult.user
                                        if (user != null) {
                                            val newUser = FirebaseUserModel(
                                                id = user.uid,
                                                nickname = cleanNick,
                                                email = cleanEmail,
                                                role = "user",
                                                points = 0L,
                                                streak = 0L,
                                                achievements = emptyList(),
                                                timeoutUntilMillis = 0L
                                            )
                                            val saved = firestoreRepository.registerUserProfile(newUser)
                                            isLoading = false
                                            if (saved) {
                                                Toast.makeText(context, "Qeydiyyat uğurla tamamlandı!", Toast.LENGTH_SHORT).show()
                                                onAuthSuccess(newUser)
                                            } else {
                                                errorMessage = "Profil bazaya yazılarkən xəta baş verdi."
                                            }
                                        }
                                    } catch (e: Exception) {
                                        isLoading = false
                                        errorMessage = "Qeydiyyat xətası: ${e.localizedMessage ?: "Naməlum xəta"}"
                                    }
                                }
                            } else {
                                // Sign In Mode
                                if (cleanPass.isBlank()) {
                                    passwordError = "Şifrəni daxil edin."
                                    return@Button
                                }
                                isLoading = true
                                errorMessage = null
                                scope.launch {
                                    try {
                                        val authResult = auth.signInWithEmailAndPassword(cleanEmail, cleanPass).await()
                                        val user = authResult.user
                                        if (user != null) {
                                            firestoreRepository.observeUser(user.uid).collect { profile ->
                                                isLoading = false
                                                if (profile != null && profile.nickname.isNotBlank()) {
                                                    onAuthSuccess(profile)
                                                } else {
                                                    tempUid = user.uid
                                                    tempEmail = user.email ?: cleanEmail
                                                    showNicknameSetup = true
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        isLoading = false
                                        errorMessage = "Daxil olma xətası: ${e.localizedMessage ?: "E-poçt və ya şifrə yalnışdır."}"
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag(if (isSignUpMode) "email_sign_up_button" else "email_sign_in_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LegalGold,
                            contentColor = LegalNavyDark
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = LegalNavyDark,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (isSignUpMode) "Qeydiyyatdan Keç" else "Daxil Ol",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Or Separator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(0.8.dp)
                                .background(Color(0xFF2A3D54))
                        )
                        Text(
                            text = "VƏ YA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7E94AA),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(0.8.dp)
                                .background(Color(0xFF2A3D54))
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Google Sign-In Button
                    OutlinedButton(
                        onClick = {
                            if (isLoading) return@OutlinedButton
                            isLoading = true
                            errorMessage = null
                            scope.launch {
                                performGoogleSignIn(
                                    context = context,
                                    auth = auth,
                                    onSuccess = { user ->
                                        scope.launch {
                                            firestoreRepository.observeUser(user.uid).collect { existingProfile ->
                                                isLoading = false
                                                if (existingProfile != null && existingProfile.nickname.isNotBlank()) {
                                                    onAuthSuccess(existingProfile)
                                                } else {
                                                    tempUid = user.uid
                                                    tempEmail = user.email ?: ""
                                                    showNicknameSetup = true
                                                }
                                            }
                                        }
                                    },
                                    onError = { err ->
                                        isLoading = false
                                        errorMessage = err
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("google_sign_in_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = LegalGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Google ilə Daxil Ol",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Profile Registration Dialog: Unique Nickname & Strict Password Policy
        if (showNicknameSetup) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF14243B)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(22.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(LegalGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = LegalGold,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Profil Qeydiyyatı",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "İctimaiyyət və imtahan nəticələri üçün unikal ləqəb və təhlükəsizlik şifrəsi təyin edin.",
                            fontSize = 12.sp,
                            color = Color(0xFFA0B4C8),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Nickname Field
                        OutlinedTextField(
                            value = dialogNicknameInput,
                            onValueChange = {
                                dialogNicknameInput = it
                                dialogNicknameError = null
                            },
                            label = { Text("Unikal Ləqəb (Nickname)") },
                            placeholder = { Text("məs: Vəkil_Samir") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("nickname_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LegalGold,
                                unfocusedBorderColor = Color(0xFF334A6A),
                                focusedLabelColor = LegalGold,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            isError = dialogNicknameError != null
                        )

                        if (dialogNicknameError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dialogNicknameError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 11.sp,
                                modifier = Modifier.align(Alignment.Start)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Password / PIN Field enforcing strict policy
                        OutlinedTextField(
                            value = dialogPasswordInput,
                            onValueChange = {
                                dialogPasswordInput = it
                                dialogPasswordError = null
                            },
                            label = { Text("Təhlükəsizlik Şifrəsi (PİN)") },
                            placeholder = { Text("Ən azı 8 rəqəm və 1 hərf") },
                            singleLine = true,
                            visualTransformation = if (isDialogPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                IconButton(onClick = { isDialogPasswordVisible = !isDialogPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isDialogPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = Color(0xFFA0B4C8)
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LegalGold,
                                unfocusedBorderColor = Color(0xFF334A6A),
                                focusedLabelColor = LegalGold,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            isError = dialogPasswordError != null
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Password Policy Indicators
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0C1827))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "Məcburi Şifrə Qaydaları:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFCAD8E8)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (dialogDigitsCount >= 8) Color(0xFF4CAF50) else Color(0xFF8899A6),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Ən azı 8 rəqəm ($dialogDigitsCount/8 daxil edilib)",
                                    fontSize = 11.sp,
                                    color = if (dialogDigitsCount >= 8) Color(0xFF4CAF50) else Color(0xFF8899A6)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (dialogLettersCount >= 1) Color(0xFF4CAF50) else Color(0xFF8899A6),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Ən azı 1 hərf ($dialogLettersCount/1 daxil edilib)",
                                    fontSize = 11.sp,
                                    color = if (dialogLettersCount >= 1) Color(0xFF4CAF50) else Color(0xFF8899A6)
                                )
                            }
                        }

                        if (dialogPasswordError != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dialogPasswordError ?: "",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 11.sp,
                                modifier = Modifier.align(Alignment.Start)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Submit Registration
                        Button(
                            onClick = {
                                val cleanNick = dialogNicknameInput.trim()
                                if (cleanNick.length < 3) {
                                    dialogNicknameError = "Ləqəb ən azı 3 simvoldan ibarət olmalıdır."
                                    return@Button
                                }
                                if (!isDialogPasswordPolicyMet) {
                                    dialogPasswordError = "Şifrə tələblərə cavab vermir: ən azı 8 rəqəm və 1 hərf olmalıdır."
                                    return@Button
                                }

                                isRegistering = true
                                scope.launch {
                                    val isAvailable = firestoreRepository.checkNicknameAvailable(cleanNick)
                                    if (!isAvailable) {
                                        isRegistering = false
                                        dialogNicknameError = "Bu ləqəb artıq başqa bir istifadəçi tərəfindən götürülüb. Fərqli bir ləqəb seçin."
                                        return@launch
                                    }

                                    // Register with 0 points and real data
                                    val newUser = FirebaseUserModel(
                                        id = tempUid,
                                        nickname = cleanNick,
                                        email = tempEmail,
                                        role = "user",
                                        points = 0L,
                                        streak = 0L,
                                        achievements = emptyList(),
                                        timeoutUntilMillis = 0L
                                    )

                                    val success = firestoreRepository.registerUserProfile(newUser)
                                    isRegistering = false
                                    if (success) {
                                        showNicknameSetup = false
                                        Toast.makeText(context, "Profil uğurla yaradıldı!", Toast.LENGTH_SHORT).show()
                                        onAuthSuccess(newUser)
                                    } else {
                                        dialogNicknameError = "Qeydiyyat zamanı xəta baş verdi. Yenidən cəhd edin."
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("complete_registration_button"),
                            enabled = !isRegistering && dialogNicknameInput.isNotBlank() && isDialogPasswordPolicyMet,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LegalGold,
                                contentColor = LegalNavyDark
                            )
                        ) {
                            if (isRegistering) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = LegalNavyDark,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Qeydiyyatı Tamamla", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

private suspend fun performGoogleSignIn(
    context: Context,
    auth: FirebaseAuth,
    onSuccess: (com.google.firebase.auth.FirebaseUser) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val credentialManager = CredentialManager.create(context)
        val webClientId = context.getString(R.string.default_web_client_id)

        val googleIdOption = GetSignInWithGoogleOption.Builder(webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(
            request = request,
            context = context as Activity
        )

        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken

            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(firebaseCredential).await()
            val user = authResult.user
            if (user != null) {
                onSuccess(user)
            } else {
                onError("İstifadəçi məlumatları əldə edilə bilmədi.")
            }
        } else {
            onError("Gözlənilməz giriş növü aşkarlandı.")
        }
    } catch (e: GetCredentialCancellationException) {
        Log.w("AuthScreen", "Sign-in cancelled by user")
        onError("Giriş prosesi istifadəçi tərəfindən dayandırıldı.")
    } catch (e: Exception) {
        Log.e("AuthScreen", "Google Sign-in failed", e)
        onError("Daxil olma zamanı xəta baş verdi: ${e.localizedMessage ?: "Naməlum xəta"}")
    }
}
