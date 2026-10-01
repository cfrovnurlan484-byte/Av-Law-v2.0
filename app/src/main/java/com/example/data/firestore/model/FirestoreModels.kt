package com.example.data.firestore.model

import com.google.firebase.Timestamp

data class FirebaseUserModel(
    val id: String = "",
    val nickname: String = "",
    val email: String = "",
    val role: String = "user",
    val points: Long = 0L,
    val streak: Long = 0L,
    val achievements: List<String> = emptyList(),
    val timeoutUntilMillis: Long = 0L,
    val createdAt: Timestamp? = null
)

data class FirebasePostModel(
    val id: String = "",
    val authorId: String = "",
    val authorNickname: String = "",
    val title: String = "",
    val content: String = "",
    val category: String = "Hüquqi Müzakirə",
    val score: Long = 0L,
    val likesCount: Long = 0L,
    val likedUserIds: List<String> = emptyList(),
    val commentsCount: Long = 0L,
    val createdAt: Timestamp? = null
)

data class FirebaseCommentModel(
    val id: String = "",
    val postId: String = "",
    val authorId: String = "",
    val authorNickname: String = "",
    val text: String = "",
    val createdAt: Timestamp? = null
)

data class FirebaseLegalMaterialModel(
    val id: String = "",
    val category: String = "Mülki Məcəllə",
    val articleNumber: String = "",
    val title: String = "",
    val summary: String = "",
    val fullText: String = "",
    val keywords: String = "",
    val updatedBy: String = "",
    val updatedAt: Timestamp? = null
)
