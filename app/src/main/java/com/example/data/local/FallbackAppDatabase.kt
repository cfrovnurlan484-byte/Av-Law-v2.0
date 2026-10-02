package com.example.data.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.sqlite.SQLiteConnection
import com.example.data.local.dao.ArticleDao
import com.example.data.local.dao.CaseStudyDao
import com.example.data.local.dao.ExamResultDao
import com.example.data.local.dao.LegalSourceDao
import com.example.data.local.dao.UserDao
import com.example.data.local.model.ArticleEntity
import com.example.data.local.model.CaseStudy
import com.example.data.local.model.ExamResultEntity
import com.example.data.local.model.LegalSourceEntity
import com.example.data.local.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.util.concurrent.atomic.AtomicLong

class FallbackAppDatabase : AppDatabase() {

    private val userState = MutableStateFlow<UserProfile?>(LegalDatabaseSeeder.getInitialProfile())
    private val examsState = MutableStateFlow<List<ExamResultEntity>>(LegalDatabaseSeeder.getInitialExams())
    private val articlesState = MutableStateFlow<List<ArticleEntity>>(LegalDatabaseSeeder.getInitialArticles())
    private val legalSourcesState = MutableStateFlow<List<LegalSourceEntity>>(LegalDatabaseSeeder.getInitialLegalSources())
    private val caseStudiesState = MutableStateFlow<List<CaseStudy>>(LegalDatabaseSeeder.getInitialCaseStudies())

    private val examIdGen = AtomicLong(100)
    private val articleIdGen = AtomicLong(100)
    private val caseStudyIdGen = AtomicLong(100)

    private val _userDao = object : UserDao {
        override fun getUser(): Flow<UserProfile?> = userState

        override suspend fun insertOrUpdate(user: UserProfile) {
            userState.value = user
        }

        override suspend fun addPoints(points: Int) {
            userState.value = userState.value?.let { it.copy(totalPoints = it.totalPoints + points) }
        }

        override suspend fun recordAppealWon(pointsWon: Int) {
            userState.value = userState.value?.let {
                it.copy(
                    appealsWon = it.appealsWon + 1,
                    pointsWonFromAppeals = it.pointsWonFromAppeals + pointsWon,
                    totalPoints = it.totalPoints + pointsWon
                )
            }
        }

        override suspend fun recordAppealLost(pointsLost: Int) {
            userState.value = userState.value?.let {
                val newPoints = (it.totalPoints - pointsLost).coerceAtLeast(0)
                it.copy(
                    appealsLost = it.appealsLost + 1,
                    pointsLostFromAppeals = it.pointsLostFromAppeals + pointsLost,
                    totalPoints = newPoints
                )
            }
        }

        override suspend fun incrementTheoryChecks() {
            userState.value = userState.value?.let { it.copy(theoryChecksCompleted = it.theoryChecksCompleted + 1) }
        }

        override suspend fun incrementCasesSolved() {
            userState.value = userState.value?.let { it.copy(casesSolved = it.casesSolved + 1) }
        }
    }

    private val _examResultDao = object : ExamResultDao {
        override fun getAllExams(): Flow<List<ExamResultEntity>> = examsState

        override suspend fun getExamById(id: Long): ExamResultEntity? {
            return examsState.value.find { it.id == id }
        }

        override suspend fun insert(result: ExamResultEntity): Long {
            val assignedId = if (result.id <= 0L) examIdGen.incrementAndGet() else result.id
            val entity = result.copy(id = assignedId)
            val current = examsState.value.filter { it.id != assignedId }.toMutableList()
            current.add(0, entity)
            examsState.value = current
            return assignedId
        }

        override suspend fun update(result: ExamResultEntity) {
            val current = examsState.value.toMutableList()
            val index = current.indexOfFirst { it.id == result.id }
            if (index != -1) {
                current[index] = result
                examsState.value = current
            }
        }
    }

    private val _articleDao = object : ArticleDao {
        override fun getAllArticles(): Flow<List<ArticleEntity>> = articlesState.map { list ->
            list.sortedWith(compareByDescending<ArticleEntity> { it.isPinned }.thenByDescending { it.createdAt })
        }

        override fun getUserArticles(): Flow<List<ArticleEntity>> = articlesState.map { list ->
            list.filter { it.isUserCreated }.sortedByDescending { it.createdAt }
        }

        override suspend fun insert(article: ArticleEntity): Long {
            val assignedId = if (article.id <= 0L) articleIdGen.incrementAndGet() else article.id
            val entity = article.copy(id = assignedId)
            val current = articlesState.value.filter { it.id != assignedId }.toMutableList()
            current.add(0, entity)
            articlesState.value = current
            return assignedId
        }

        override suspend fun toggleLike(id: Long, isLiked: Boolean, delta: Int) {
            val current = articlesState.value.toMutableList()
            val index = current.indexOfFirst { it.id == id }
            if (index != -1) {
                val item = current[index]
                current[index] = item.copy(isLiked = isLiked, likesCount = (item.likesCount + delta).coerceAtLeast(0))
                articlesState.value = current
            }
        }

        override suspend fun updatePinStatus(id: Long, isPinned: Boolean) {
            val current = articlesState.value.toMutableList()
            val index = current.indexOfFirst { it.id == id }
            if (index != -1) {
                val item = current[index]
                current[index] = item.copy(isPinned = isPinned)
                articlesState.value = current
            }
        }
    }

    private val _legalSourceDao = object : LegalSourceDao {
        override fun getAllSources(): Flow<List<LegalSourceEntity>> = legalSourcesState

        override fun searchSources(query: String): Flow<List<LegalSourceEntity>> = legalSourcesState.map { list ->
            val q = query.lowercase().trim()
            if (q.isBlank()) list else list.filter {
                it.title.lowercase().contains(q) ||
                it.content.lowercase().contains(q) ||
                it.articleNumber.lowercase().contains(q) ||
                it.keywords.lowercase().contains(q)
            }
        }

        override fun getSourcesByCategory(category: String): Flow<List<LegalSourceEntity>> = legalSourcesState.map { list ->
            list.filter { it.codeCategory.equals(category, ignoreCase = true) }
        }

        override suspend fun insertAll(sources: List<LegalSourceEntity>) {
            val map = legalSourcesState.value.associateBy { it.id }.toMutableMap()
            sources.forEach { map[it.id] = it }
            legalSourcesState.value = map.values.toList()
        }

        override suspend fun toggleBookmark(id: Long, isBookmarked: Boolean) {
            val current = legalSourcesState.value.toMutableList()
            val index = current.indexOfFirst { it.id == id }
            if (index != -1) {
                val item = current[index]
                current[index] = item.copy(isBookmarked = isBookmarked)
                legalSourcesState.value = current
            }
        }
    }

    private val _caseStudyDao = object : CaseStudyDao {
        override fun getAllCaseStudies(): Flow<List<CaseStudy>> = caseStudiesState.map { list ->
            list.sortedByDescending { it.createdAt }
        }

        override fun getCaseStudyById(id: Long): Flow<CaseStudy?> = caseStudiesState.map { list ->
            list.find { it.id == id }
        }

        override fun getCaseStudiesByCategory(category: String): Flow<List<CaseStudy>> = caseStudiesState.map { list ->
            list.filter { it.category.equals(category, ignoreCase = true) }
        }

        override fun getCaseStudiesByStatus(status: String): Flow<List<CaseStudy>> = caseStudiesState.map { list ->
            list.filter { it.status.equals(status, ignoreCase = true) }
        }

        override fun searchCaseStudies(query: String): Flow<List<CaseStudy>> = caseStudiesState.map { list ->
            val q = query.lowercase().trim()
            if (q.isBlank()) list else list.filter {
                it.title.lowercase().contains(q) ||
                it.description.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.facts.lowercase().contains(q)
            }
        }

        override suspend fun insert(caseStudy: CaseStudy): Long {
            val assignedId = if (caseStudy.id <= 0L) caseStudyIdGen.incrementAndGet() else caseStudy.id
            val entity = caseStudy.copy(id = assignedId)
            val current = caseStudiesState.value.filter { it.id != assignedId }.toMutableList()
            current.add(0, entity)
            caseStudiesState.value = current
            return assignedId
        }

        override suspend fun insertAll(caseStudies: List<CaseStudy>) {
            val current = caseStudiesState.value.toMutableList()
            caseStudies.forEach { cs ->
                val assignedId = if (cs.id <= 0L) caseStudyIdGen.incrementAndGet() else cs.id
                current.removeAll { it.id == assignedId }
                current.add(cs.copy(id = assignedId))
            }
            caseStudiesState.value = current
        }

        override suspend fun update(caseStudy: CaseStudy) {
            val current = caseStudiesState.value.toMutableList()
            val index = current.indexOfFirst { it.id == caseStudy.id }
            if (index != -1) {
                current[index] = caseStudy
                caseStudiesState.value = current
            }
        }

        override suspend fun delete(caseStudy: CaseStudy) {
            caseStudiesState.value = caseStudiesState.value.filter { it.id != caseStudy.id }
        }

        override suspend fun deleteById(id: Long) {
            caseStudiesState.value = caseStudiesState.value.filter { it.id != id }
        }

        override suspend fun updateStatus(id: Long, status: String) {
            val current = caseStudiesState.value.toMutableList()
            val index = current.indexOfFirst { it.id == id }
            if (index != -1) {
                current[index] = current[index].copy(status = status)
                caseStudiesState.value = current
            }
        }
    }

    override fun userDao(): UserDao = _userDao
    override fun examResultDao(): ExamResultDao = _examResultDao
    override fun articleDao(): ArticleDao = _articleDao
    override fun legalSourceDao(): LegalSourceDao = _legalSourceDao
    override fun caseStudyDao(): CaseStudyDao = _caseStudyDao

    override fun clearAllTables() {
        userState.value = null
        examsState.value = emptyList()
        articlesState.value = emptyList()
        legalSourcesState.value = emptyList()
        caseStudiesState.value = emptyList()
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        return InvalidationTracker(this, emptyMap(), emptyMap(), "user_profile", "exam_results", "articles", "legal_sources", "case_studies")
    }

    override fun createOpenDelegate(): RoomOpenDelegate {
        return object : RoomOpenDelegate(2, "fallback_hash", "fallback_hash") {
            override fun createAllTables(connection: SQLiteConnection) {}
            override fun dropAllTables(connection: SQLiteConnection) {}
            override fun onCreate(connection: SQLiteConnection) {}
            override fun onOpen(connection: SQLiteConnection) {}
            override fun onPreMigrate(connection: SQLiteConnection) {}
            override fun onPostMigrate(connection: SQLiteConnection) {}
            override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
                return RoomOpenDelegate.ValidationResult(true, null)
            }
        }
    }
}
