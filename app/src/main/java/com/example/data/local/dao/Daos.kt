package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.model.ArticleEntity
import com.example.data.local.model.ExamResultEntity
import com.example.data.local.model.LegalSourceEntity
import com.example.data.local.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUser(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserProfile)

    @Query("UPDATE user_profile SET totalPoints = totalPoints + :points WHERE id = 1")
    suspend fun addPoints(points: Int)

    @Query("""
        UPDATE user_profile 
        SET appealsWon = appealsWon + 1,
            pointsWonFromAppeals = pointsWonFromAppeals + :pointsWon,
            totalPoints = totalPoints + :pointsWon
        WHERE id = 1
    """)
    suspend fun recordAppealWon(pointsWon: Int)

    @Query("""
        UPDATE user_profile 
        SET appealsLost = appealsLost + 1,
            pointsLostFromAppeals = pointsLostFromAppeals + :pointsLost,
            totalPoints = CASE WHEN (totalPoints - :pointsLost) < 0 THEN 0 ELSE totalPoints - :pointsLost END
        WHERE id = 1
    """)
    suspend fun recordAppealLost(pointsLost: Int)

    @Query("UPDATE user_profile SET theoryChecksCompleted = theoryChecksCompleted + 1 WHERE id = 1")
    suspend fun incrementTheoryChecks()

    @Query("UPDATE user_profile SET casesSolved = casesSolved + 1 WHERE id = 1")
    suspend fun incrementCasesSolved()
}

@Dao
interface ExamResultDao {
    @Query("SELECT * FROM exam_results ORDER BY createdAt DESC")
    fun getAllExams(): Flow<List<ExamResultEntity>>

    @Query("SELECT * FROM exam_results WHERE id = :id LIMIT 1")
    suspend fun getExamById(id: Long): ExamResultEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: ExamResultEntity): Long

    @Update
    suspend fun update(result: ExamResultEntity)
}

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles ORDER BY isPinned DESC, createdAt DESC")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE isUserCreated = 1 ORDER BY createdAt DESC")
    fun getUserArticles(): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: ArticleEntity): Long

    @Query("UPDATE articles SET likesCount = likesCount + :delta, isLiked = :isLiked WHERE id = :id")
    suspend fun toggleLike(id: Long, isLiked: Boolean, delta: Int)

    @Query("UPDATE articles SET isPinned = :isPinned WHERE id = :id")
    suspend fun updatePinStatus(id: Long, isPinned: Boolean)
}

@Dao
interface LegalSourceDao {
    @Query("SELECT * FROM legal_sources ORDER BY codeCategory ASC, id ASC")
    fun getAllSources(): Flow<List<LegalSourceEntity>>

    @Query("""
        SELECT * FROM legal_sources 
        WHERE title LIKE '%' || :query || '%' 
           OR content LIKE '%' || :query || '%' 
           OR articleNumber LIKE '%' || :query || '%' 
           OR keywords LIKE '%' || :query || '%'
        ORDER BY id ASC
    """)
    fun searchSources(query: String): Flow<List<LegalSourceEntity>>

    @Query("SELECT * FROM legal_sources WHERE codeCategory = :category ORDER BY id ASC")
    fun getSourcesByCategory(category: String): Flow<List<LegalSourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sources: List<LegalSourceEntity>)

    @Query("UPDATE legal_sources SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun toggleBookmark(id: Long, isBookmarked: Boolean)
}
