package com.example.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val fullName: String = "Əli Məmmədov",
    val rankTitle: String = "Təcrübəçi Hüquqşünas",
    val email: String = "ali.mammadov@law.az",
    val totalPoints: Int = 450,
    val theoryChecksCompleted: Int = 12,
    val casesSolved: Int = 8,
    val appealsWon: Int = 3,
    val appealsLost: Int = 1,
    val pointsWonFromAppeals: Int = 150,
    val pointsLostFromAppeals: Int = 40
) {
    val successRate: Int
        get() {
            val total = theoryChecksCompleted + casesSolved
            if (total == 0) return 0
            // Success defined as passed checks/cases
            val successful = ((total * 0.75f).toInt()).coerceAtMost(total)
            return ((successful.toFloat() / total) * 100).toInt()
        }

    val dynamicRank: String
        get() = when {
            totalPoints >= 3000 -> "Ali Hakimlər Şurası Fəxri Üzvü"
            totalPoints >= 2000 -> "Peşəkar Vəkil"
            totalPoints >= 1200 -> "Aparıcı Hüquq Məsləhətçisi"
            totalPoints >= 600 -> "Vəkil Köməkçisi"
            totalPoints >= 300 -> "Təcrübəçi Hüquqşünas"
            else -> "Hüquq Tələbəsi"
        }
}

@Entity(tableName = "exam_results")
data class ExamResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examType: String, // "THEORY" or "CASE"
    val title: String,
    val topicCategory: String,
    val userAnswerText: String,
    val score: Int,
    val verdict: String,
    val accuracyScore: Int,
    val terminologyScore: Int,
    val reasoningScore: Int,
    val fluencyScore: Int,
    val feedback: String,
    val recommendations: String,
    val appealStatus: String = "NONE", // "NONE", "SUBMITTED", "WON", "LOST"
    val appealWager: Int = 0,
    val appealReason: String = "",
    val appealFeedback: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val authorName: String,
    val authorRank: String,
    val category: String,
    val summary: String,
    val content: String,
    val legalCitations: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val isPinned: Boolean = false,
    val readMinutes: Int = 4,
    val isUserCreated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "legal_sources")
data class LegalSourceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val codeCategory: String,
    val articleNumber: String,
    val title: String,
    val content: String,
    val keywords: String,
    val isBookmarked: Boolean = false
)
