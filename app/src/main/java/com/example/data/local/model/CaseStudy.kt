package com.example.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity representing a legal case study.
 * Stores legal cases with title, category, description, and status for AZ law exam & practice.
 */
@Entity(tableName = "case_studies")
data class CaseStudy(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String,
    val description: String,
    val status: String = "ACTIVE", // e.g. "ACTIVE", "RESOLVED", "PENDING", "ARCHIVED"
    val facts: String = "",
    val legalIssue: String = "",
    val solution: String = "",
    val parties: String = "",
    val difficulty: String = "Orta",
    val pointsReward: Int = 50,
    val createdAt: Long = System.currentTimeMillis()
)
