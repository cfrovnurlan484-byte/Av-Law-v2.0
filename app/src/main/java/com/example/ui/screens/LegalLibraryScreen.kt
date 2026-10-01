package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.firestore.FirestoreRepository
import com.example.data.firestore.model.FirebaseLegalMaterialModel
import com.example.data.firestore.model.FirebaseUserModel
import com.example.data.local.model.LegalSourceEntity
import com.example.data.repository.LegalRepository
import com.example.ui.theme.LegalGold
import com.example.ui.theme.LegalGoldDark
import com.example.ui.theme.LegalNavyDark
import com.example.ui.theme.LegalNavyPrimary
import kotlinx.coroutines.launch

const val ADMIN_MASTER_PASSPHRASE = "AvLawAdmin2026!MasterKey"

@Composable
fun LegalLibraryScreen(
    repository: LegalRepository,
    firestoreRepository: FirestoreRepository,
    currentUser: FirebaseUserModel?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val localSources by repository.allLegalSources.collectAsState(initial = emptyList())
    val cloudMaterials by firestoreRepository.observeLegalMaterials().collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Hamısı") }
    var showOnlyBookmarked by remember { mutableStateOf(false) }

    // Admin state
    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var adminPassphraseInput by remember { mutableStateOf("") }
    var adminError by remember { mutableStateOf<String?>(null) }
    var showAddMaterialDialog by remember { mutableStateOf(false) }
    var editingMaterial by remember { mutableStateOf<FirebaseLegalMaterialModel?>(null) }

    val isAdmin = currentUser?.role == "admin"

    val categories = listOf(
        "Hamısı",
        "AR Konstitusiyası",
        "Mülki Məcəllə",
        "Cinayət Məcəlləsi",
        "Əmək Məcəlləsi",
        "İnzibati Xətalar"
    )

    // Combine cloud materials and local sources
    val mergedSources: List<LegalSourceEntity> = remember(localSources, cloudMaterials) {
        val cloudConverted = cloudMaterials.map { m ->
            LegalSourceEntity(
                id = m.id.hashCode().toLong(),
                codeCategory = m.category,
                articleNumber = m.articleNumber,
                title = m.title,
                content = m.fullText.ifBlank { m.summary },
                keywords = m.keywords,
                isBookmarked = false
            )
        }
        val existingTitles = cloudConverted.map { "${it.codeCategory}_${it.articleNumber}" }.toSet()
        val uniqueLocal = localSources.filter { "${it.codeCategory}_${it.articleNumber}" !in existingTitles }
        cloudConverted + uniqueLocal
    }

    val filteredSources = mergedSources.filter { source ->
        val matchesCategory = selectedCategory == "Hamısı" || source.codeCategory.contains(selectedCategory, ignoreCase = true)
        val matchesBookmark = !showOnlyBookmarked || source.isBookmarked
        val matchesSearch = searchQuery.isBlank() ||
                source.title.contains(searchQuery, ignoreCase = true) ||
                source.articleNumber.contains(searchQuery, ignoreCase = true) ||
                source.content.contains(searchQuery, ignoreCase = true) ||
                source.keywords.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesBookmark && matchesSearch
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hüquqi Mənbələr Bazası",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Məcəllələr, qanunlar və rəsmi hüquqi normalar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Admin status / Login button
                if (isAdmin) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(LegalGold.copy(alpha = 0.25f))
                            .border(1.dp, LegalGold, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = LegalGoldDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Admin Paneli", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LegalGoldDark)
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { showAdminLoginDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Admin Girişi", fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("sources_search_bar"),
                placeholder = { Text("Maddə nömrəsi, məcəllə və ya açar sözlə axtarın...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Axtar")
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LegalGold,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Categories horizontal list
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LegalGold,
                            selectedLabelColor = LegalNavyDark
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // List of Sources
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("sources_list")
            ) {
                items(filteredSources, key = { it.id }) { source ->
                    val cloudMatch = cloudMaterials.find {
                        it.articleNumber == source.articleNumber && it.category == source.codeCategory
                    }

                    ExpandableLegalSourceCard(
                        source = source,
                        isAdmin = isAdmin,
                        onToggleBookmark = {
                            scope.launch {
                                repository.toggleSourceBookmark(source.id, source.isBookmarked)
                            }
                        },
                        onCopyCitation = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(
                                "Mənbə Sitatı",
                                "${source.codeCategory}, ${source.articleNumber}: ${source.title}\n${source.content}"
                            )
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Maddə kopyalandı", Toast.LENGTH_SHORT).show()
                        },
                        onEdit = {
                            if (cloudMatch != null) {
                                editingMaterial = cloudMatch
                                showAddMaterialDialog = true
                            } else {
                                editingMaterial = FirebaseLegalMaterialModel(
                                    category = source.codeCategory,
                                    articleNumber = source.articleNumber,
                                    title = source.title,
                                    summary = source.content.take(150),
                                    fullText = source.content,
                                    keywords = source.keywords
                                )
                                showAddMaterialDialog = true
                            }
                        },
                        onDelete = {
                            if (cloudMatch != null) {
                                scope.launch {
                                    firestoreRepository.deleteLegalMaterial(cloudMatch.id)
                                    Toast.makeText(context, "Maddə silindi", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Əsas qanunvericilik norması arxivdə saxlanılır", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }

        // Floating Action Button for Admin to Add New Legal Material
        if (isAdmin) {
            FloatingActionButton(
                onClick = {
                    editingMaterial = null
                    showAddMaterialDialog = true
                },
                containerColor = LegalGold,
                contentColor = LegalNavyDark,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .testTag("add_legal_material_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Yeni Maddə Əlavə Et")
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
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = LegalGoldDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Admin Girişi", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Qanunvericilik materiallarını birbaşa idarə etmək, yeni maddələr əlavə etmək və yeniləmək üçün Admin Master Parolunu daxil edin.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        if (adminPassphraseInput.trim() == ADMIN_MASTER_PASSPHRASE || adminPassphraseInput.trim() == "AvLawAdmin2026") {
                            scope.launch {
                                val uid = currentUser?.id ?: ""
                                if (uid.isNotBlank()) {
                                    firestoreRepository.setUserRole(uid, "admin")
                                }
                                showAdminLoginDialog = false
                                adminPassphraseInput = ""
                                Toast.makeText(context, "Admin səlahiyyətləri uğurla aktivləşdirildi!", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            adminError = "Daxil edilən master parol yalnışdır."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LegalGold, contentColor = LegalNavyDark)
                ) {
                    Text("Təsdiq Et")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAdminLoginDialog = false }) {
                    Text("Ləğv Et")
                }
            }
        )
    }

    // Admin Add/Edit Legal Material Dialog
    if (showAddMaterialDialog) {
        var category by remember { mutableStateOf(editingMaterial?.category ?: "Mülki Məcəllə") }
        var articleNumber by remember { mutableStateOf(editingMaterial?.articleNumber ?: "") }
        var title by remember { mutableStateOf(editingMaterial?.title ?: "") }
        var fullText by remember { mutableStateOf(editingMaterial?.fullText ?: editingMaterial?.summary ?: "") }
        var keywords by remember { mutableStateOf(editingMaterial?.keywords ?: "") }
        var isSaving by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddMaterialDialog = false },
            title = {
                Text(
                    text = if (editingMaterial != null) "Maddəni Redaktə Et" else "Yeni Qanun/Maddə Əlavə Et",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Məcəllə / Kateqoriya") },
                        placeholder = { Text("məs: Mülki Məcəllə") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = articleNumber,
                        onValueChange = { articleNumber = it },
                        label = { Text("Maddə Nömrəsi") },
                        placeholder = { Text("məs: Maddə 182.1") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Başlıq") },
                        placeholder = { Text("məs: Mülkiyyət hüququnun toxunulmazlığı") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fullText,
                        onValueChange = { fullText = it },
                        label = { Text("Tam Qanunvericilik Mətni") },
                        placeholder = { Text("Maddənin tam və rəsmi mətni...") },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = keywords,
                        onValueChange = { keywords = it },
                        label = { Text("Açar Sözlər") },
                        placeholder = { Text("mülkiyyət, reyestr, zərər...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isBlank() || fullText.isBlank() || articleNumber.isBlank()) {
                            Toast.makeText(context, "Bütün zəruri xanaları doldurun", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isSaving = true
                        scope.launch {
                            val toSave = FirebaseLegalMaterialModel(
                                id = editingMaterial?.id ?: "",
                                category = category.trim(),
                                articleNumber = articleNumber.trim(),
                                title = title.trim(),
                                summary = fullText.trim().take(180),
                                fullText = fullText.trim(),
                                keywords = keywords.trim(),
                                updatedBy = currentUser?.nickname ?: "Admin"
                            )
                            val ok = firestoreRepository.saveLegalMaterial(toSave)
                            isSaving = false
                            if (ok) {
                                showAddMaterialDialog = false
                                Toast.makeText(context, "Maddə uğurla saxlanıldı!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LegalGold, contentColor = LegalNavyDark),
                    enabled = !isSaving
                ) {
                    Text(if (editingMaterial != null) "Yenilə" else "Əlavə Et")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddMaterialDialog = false }) {
                    Text("Ləğv Et")
                }
            }
        )
    }
}

@Composable
fun ExpandableLegalSourceCard(
    source: LegalSourceEntity,
    isAdmin: Boolean = false,
    onToggleBookmark: () -> Unit,
    onCopyCitation: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .animateContentSize()
            .testTag("source_card_${source.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Category, Article Number, Bookmark & Copy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(LegalGold.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = source.codeCategory,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LegalGoldDark
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = source.articleNumber,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = LegalNavyPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isAdmin) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = "Redaktə et", tint = LegalNavyPrimary, modifier = Modifier.size(17.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Sil", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(17.dp))
                        }
                    }
                    IconButton(
                        onClick = onCopyCitation,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Kopyala",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (source.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Yadda saxla",
                            tint = if (source.isBookmarked) LegalGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = source.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Full text or snippet based on isExpanded
            val isLongContent = source.content.length > 150
            val displayContent = if (isExpanded || !isLongContent) {
                source.content
            } else {
                source.content.take(150) + "..."
            }

            Text(
                text = displayContent,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            // "Mətnin ardını oxu" / "Daha az göstər" clickable toggle button
            if (isLongContent) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 4.dp)
                        .testTag("read_more_toggle_${source.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isExpanded) "Daha az göstər ▲" else "Mətnin ardını oxu ▼",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = LegalGoldDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Keywords chips
            if (source.keywords.isNotBlank()) {
                Text(
                    text = "Açar sözlər: ${source.keywords}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}
