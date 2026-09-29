package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PushPin
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.model.ArticleEntity
import com.example.data.local.model.UserProfile
import com.example.data.repository.LegalRepository
import com.example.ui.theme.LegalGold
import com.example.ui.theme.LegalGoldDark
import com.example.ui.theme.LegalNavyDark
import com.example.ui.theme.LegalNavyPrimary
import kotlinx.coroutines.launch

@Composable
fun ShowcaseScreen(
    repository: LegalRepository,
    userProfile: UserProfile?,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val articles by repository.allArticles.collectAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Hamısı") }
    var showPublishDialog by remember { mutableStateOf(false) }
    var articleToRead by remember { mutableStateOf<ArticleEntity?>(null) }

    val categories = listOf("Hamısı", "Mülki Hüquq", "Cinayət Hüququ", "Əmək Hüququ", "Konstitusiya", "İnzibati")

    val filteredArticles = articles.filter { article ->
        val matchesCategory = selectedCategory == "Hamısı" || article.category.contains(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                article.title.contains(searchQuery, ignoreCase = true) ||
                article.summary.contains(searchQuery, ignoreCase = true) ||
                article.content.contains(searchQuery, ignoreCase = true) ||
                article.authorName.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
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
                        text = "Sərgiləmə Mərkəzi",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Hüquqşünasların elmi məqalələri və tədqiqatları",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showPublishDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LegalGold,
                        contentColor = LegalNavyDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("publish_article_top_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Dərc Et", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Axtar", tint = LegalGold)
                },
                placeholder = { Text("Məqalə, müəllif və ya mövzu axtar...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("articles_search_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LegalGold,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
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

            // Articles Feed
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredArticles, key = { it.id }) { article ->
                    ArticleCard(
                        article = article,
                        onArticleClick = { articleToRead = article },
                        onLikeClick = {
                            scope.launch {
                                repository.toggleArticleLike(article.id, article.isLiked)
                            }
                        },
                        onTogglePin = {
                            scope.launch {
                                repository.toggleArticlePin(article.id, !article.isPinned)
                            }
                        }
                    )
                }
            }
        }
    }

    // Article Reader Dialog
    articleToRead?.let { article ->
        ArticleReaderDialog(
            article = article,
            onDismiss = { articleToRead = null },
            onLikeClick = {
                scope.launch {
                    repository.toggleArticleLike(article.id, article.isLiked)
                }
            }
        )
    }

    // Publish Article Dialog
    if (showPublishDialog) {
        PublishArticleDialog(
            userProfile = userProfile,
            onDismiss = { showPublishDialog = false },
            onPublish = { title, category, summary, content, citations ->
                scope.launch {
                    repository.publishArticle(
                        title = title,
                        category = category,
                        summary = summary,
                        content = content,
                        citations = citations,
                        authorName = userProfile?.fullName ?: "Əli Məmmədov",
                        authorRank = userProfile?.dynamicRank ?: "Təcrübəçi Hüquqşünas"
                    )
                    showPublishDialog = false
                }
            }
        )
    }
}

@Composable
fun ArticleCard(
    article: ArticleEntity,
    onArticleClick: () -> Unit,
    onLikeClick: () -> Unit,
    onTogglePin: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onArticleClick() }
            .testTag("article_card_${article.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (article.isPinned) LegalGold.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        border = if (article.isPinned) androidx.compose.foundation.BorderStroke(1.dp, LegalGold) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Pinned badge / Category / Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (article.isPinned) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(LegalGold)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "📌 Seçilmiş Məqalə",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LegalNavyDark
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(LegalNavyPrimary.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = article.category,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LegalNavyPrimary
                        )
                    }
                }

                Text(
                    text = "${article.readMinutes} dəq oxu",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = article.summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Author row & interactions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(LegalGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👨‍⚖️", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = article.authorName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = article.authorRank,
                            style = MaterialTheme.typography.labelSmall,
                            color = LegalGoldDark,
                            fontSize = 10.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onTogglePin,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (article.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin",
                            tint = if (article.isPinned) LegalGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onLikeClick() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (article.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Bəyən",
                            tint = if (article.isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${article.likesCount}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleReaderDialog(
    article: ArticleEntity,
    onDismiss: () -> Unit,
    onLikeClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("article_reader_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(LegalGold.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = article.category,
                            color = LegalGoldDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Bağla")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Müəllif: ${article.authorName} (${article.authorRank})",
                    style = MaterialTheme.typography.labelMedium,
                    color = LegalNavyPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Legal Citations Box
                if (article.legalCitations.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = LegalGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Əsas normativ istinadlar: ${article.legalCitations}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                Text(
                    text = article.content,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onLikeClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (article.isLiked) Color.Red.copy(alpha = 0.15f) else LegalNavyPrimary.copy(alpha = 0.1f),
                            contentColor = if (article.isLiked) Color.Red else LegalNavyPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(if (article.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${article.likesCount} Bəyənmə")
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = LegalNavyPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Bağla")
                    }
                }
            }
        }
    }
}

@Composable
fun PublishArticleDialog(
    userProfile: UserProfile?,
    onDismiss: () -> Unit,
    onPublish: (title: String, category: String, summary: String, content: String, citations: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Mülki Hüquq") }
    var summary by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var citations by remember { mutableStateOf("") }

    val categories = listOf("Mülki Hüquq", "Cinayət Hüququ", "Əmək Hüququ", "Konstitusiya", "İnzibati")
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("publish_article_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Elmi Məqalə Dərc Et",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Bağla")
                    }
                }

                Text(
                    text = "Məqalə dərc etdikdə profilinizə +50 nüfuz xalı əlavə olunacaq!",
                    style = MaterialTheme.typography.bodySmall,
                    color = LegalGoldDark
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Məqalənin Başlığı") },
                    placeholder = { Text("Məs: Mülki Məcəllədə Restitusiya Prinsipləri") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LegalGold)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Kateqoriya:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LegalGold,
                                selectedLabelColor = LegalNavyDark
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Qısa Xülasə") },
                    placeholder = { Text("Məqalənin qısa mahiyyəti və toxunduğu əsas hüquqi məsələ") },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LegalGold)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = citations,
                    onValueChange = { citations = it },
                    label = { Text("İstinad Edilən Qanunvericilik Maddələri") },
                    placeholder = { Text("Məs: AR Mülki Məcəlləsi m. 182, 337; AR Konstitusiyası m. 60") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LegalGold)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Məqalənin Tam Mətni") },
                    placeholder = { Text("Ətraflı elmi və təcrübi araşdırmanızı bura qeyd edin...") },
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LegalGold)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onPublish(title, selectedCategory, summary, content, citations)
                    },
                    enabled = title.isNotBlank() && content.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LegalGold,
                        contentColor = LegalNavyDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_publish_button")
                ) {
                    Text("Məqaləni İcmaya Təqdim Et (+50 Xal)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
