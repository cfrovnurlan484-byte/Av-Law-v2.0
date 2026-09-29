package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.LegalDatabaseSeeder
import com.example.data.local.model.ArticleEntity
import com.example.data.local.model.ExamResultEntity
import com.example.data.local.model.LegalSourceEntity
import com.example.data.local.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class LegalRepository(private val database: AppDatabase) {

    private val userDao = database.userDao()
    private val examResultDao = database.examResultDao()
    private val articleDao = database.articleDao()
    private val legalSourceDao = database.legalSourceDao()

    val userProfile: Flow<UserProfile?> = userDao.getUser()
    val allExams: Flow<List<ExamResultEntity>> = examResultDao.getAllExams()
    val allArticles: Flow<List<ArticleEntity>> = articleDao.getAllArticles()
    val userArticles: Flow<List<ArticleEntity>> = articleDao.getUserArticles()
    val allLegalSources: Flow<List<LegalSourceEntity>> = legalSourceDao.getAllSources()

    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val currentUser = userDao.getUser().firstOrNull()
        if (currentUser == null) {
            userDao.insertOrUpdate(LegalDatabaseSeeder.getInitialProfile())
            LegalDatabaseSeeder.getInitialExams().forEach {
                examResultDao.insert(it)
            }
            LegalDatabaseSeeder.getInitialArticles().forEach {
                articleDao.insert(it)
            }
            legalSourceDao.insertAll(LegalDatabaseSeeder.getInitialLegalSources())
        }
    }

    suspend fun saveExamResult(result: ExamResultEntity): Long = withContext(Dispatchers.IO) {
        val id = examResultDao.insert(result)
        // Update user stats
        if (result.examType == "THEORY") {
            userDao.incrementTheoryChecks()
        } else {
            userDao.incrementCasesSolved()
        }
        // Points awarded: Score * 0.5 (e.g. 80 score = 40 points)
        val awardedPoints = (result.score * 0.5f).toInt()
        userDao.addPoints(awardedPoints)
        id
    }

    suspend fun submitAppeal(
        examId: Long,
        wager: Int,
        justification: String,
        isSuccess: Boolean,
        appealFeedback: String
    ) = withContext(Dispatchers.IO) {
        val existing = examResultDao.getExamById(examId) ?: return@withContext

        val status = if (isSuccess) "WON" else "LOST"
        val updatedExam = existing.copy(
            appealStatus = status,
            appealWager = wager,
            appealReason = justification,
            appealFeedback = appealFeedback,
            score = if (isSuccess) (existing.score + 10).coerceAtMost(100) else existing.score
        )
        examResultDao.update(updatedExam)

        if (isSuccess) {
            // Wager refunded + 50% bonus
            val wonPoints = (wager * 1.5f).toInt()
            userDao.recordAppealWon(wonPoints)
        } else {
            // Wagered points forfeited
            userDao.recordAppealLost(wager)
        }
    }

    suspend fun publishArticle(
        title: String,
        category: String,
        summary: String,
        content: String,
        citations: String,
        authorName: String,
        authorRank: String
    ): Long = withContext(Dispatchers.IO) {
        val article = ArticleEntity(
            title = title,
            authorName = authorName,
            authorRank = authorRank,
            category = category,
            summary = summary,
            content = content,
            legalCitations = citations,
            likesCount = 0,
            isLiked = false,
            isPinned = false,
            readMinutes = ((content.split(" ").size / 150) + 1).coerceAtLeast(2),
            isUserCreated = true
        )
        val id = articleDao.insert(article)
        // Publishing an article awards 50 points
        userDao.addPoints(50)
        id
    }

    suspend fun toggleArticleLike(id: Long, currentlyLiked: Boolean) = withContext(Dispatchers.IO) {
        val newLiked = !currentlyLiked
        val delta = if (newLiked) 1 else -1
        articleDao.toggleLike(id, newLiked, delta)
    }

    suspend fun toggleArticlePin(id: Long, isPinned: Boolean) = withContext(Dispatchers.IO) {
        articleDao.updatePinStatus(id, isPinned)
    }

    suspend fun toggleSourceBookmark(id: Long, currentlyBookmarked: Boolean) = withContext(Dispatchers.IO) {
        legalSourceDao.toggleBookmark(id, !currentlyBookmarked)
    }

    fun searchSources(query: String): Flow<List<LegalSourceEntity>> {
        return legalSourceDao.searchSources(query)
    }

    fun getSourcesByCategory(category: String): Flow<List<LegalSourceEntity>> {
        return legalSourceDao.getSourcesByCategory(category)
    }

    suspend fun updateUserProfile(name: String, email: String) = withContext(Dispatchers.IO) {
        val current = userDao.getUser().firstOrNull() ?: return@withContext
        userDao.insertOrUpdate(current.copy(fullName = name, email = email))
    }
}
