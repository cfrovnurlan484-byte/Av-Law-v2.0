package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.model.CaseStudy
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseStudyDao {
    @Query("SELECT * FROM case_studies ORDER BY createdAt DESC")
    fun getAllCaseStudies(): Flow<List<CaseStudy>>

    @Query("SELECT * FROM case_studies WHERE id = :id LIMIT 1")
    fun getCaseStudyById(id: Long): Flow<CaseStudy?>

    @Query("SELECT * FROM case_studies WHERE category = :category ORDER BY createdAt DESC")
    fun getCaseStudiesByCategory(category: String): Flow<List<CaseStudy>>

    @Query("SELECT * FROM case_studies WHERE status = :status ORDER BY createdAt DESC")
    fun getCaseStudiesByStatus(status: String): Flow<List<CaseStudy>>

    @Query("""
        SELECT * FROM case_studies 
        WHERE title LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%' 
           OR category LIKE '%' || :query || '%'
           OR facts LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchCaseStudies(query: String): Flow<List<CaseStudy>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(caseStudy: CaseStudy): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(caseStudies: List<CaseStudy>)

    @Update
    suspend fun update(caseStudy: CaseStudy)

    @Delete
    suspend fun delete(caseStudy: CaseStudy)

    @Query("DELETE FROM case_studies WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE case_studies SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)
}
