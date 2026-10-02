package com.example.data.firestore

import android.util.Log
import com.example.data.firestore.model.FirebaseCommentModel
import com.example.data.firestore.model.FirebaseLegalMaterialModel
import com.example.data.firestore.model.FirebasePostModel
import com.example.data.firestore.model.FirebaseUserModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class FirestoreRepository(
    private val db: FirebaseFirestore?
) {
    companion object {
        private const val TAG = "FirestoreRepo"
    }

    fun observeUser(userId: String): Flow<FirebaseUserModel?> {
        val database = db ?: return flowOf(null)
        return database.collection("users").document(userId)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(FirebaseUserModel::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                } else {
                    null
                }
            }
            .catch { e ->
                Log.w(TAG, "Error observing user $userId", e)
                emit(null)
            }
    }

    suspend fun checkNicknameAvailable(nickname: String): Boolean = withContext(Dispatchers.IO) {
        val clean = nickname.trim().lowercase()
        if (clean.isBlank()) return@withContext false
        val database = db ?: return@withContext true
        try {
            withTimeoutOrNull(2000L) {
                val doc = database.collection("nicknames").document(clean).get().await()
                !doc.exists()
            } ?: true
        } catch (e: Exception) {
            Log.e(TAG, "Error checking nickname", e)
            true
        }
    }

    suspend fun registerUserProfile(user: FirebaseUserModel): Boolean = withContext(Dispatchers.IO) {
        val database = db ?: return@withContext true
        val cleanNickname = user.nickname.trim().lowercase()
        try {
            withTimeoutOrNull(2500L) {
                val batch = database.batch()
                val userRef = database.collection("users").document(user.id)
                val nickRef = database.collection("nicknames").document(cleanNickname)

                val userData = mapOf(
                    "id" to user.id,
                    "nickname" to user.nickname.trim(),
                    "email" to user.email,
                    "role" to user.role,
                    "points" to 0L,
                    "streak" to 0L,
                    "achievements" to emptyList<String>(),
                    "timeoutUntilMillis" to 0L,
                    "createdAt" to FieldValue.serverTimestamp()
                )

                val nickData = mapOf(
                    "uid" to user.id,
                    "nickname" to user.nickname.trim(),
                    "createdAt" to FieldValue.serverTimestamp()
                )

                batch.set(userRef, userData)
                batch.set(nickRef, nickData)
                batch.commit().await()
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error registering user profile", e)
            false
        }
    }

    suspend fun setUserTimeout(userId: String, timeoutMillis: Long) = withContext(Dispatchers.IO) {
        val database = db ?: return@withContext
        try {
            database.collection("users").document(userId).update(
                "timeoutUntilMillis", timeoutMillis
            ).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error setting user timeout", e)
        }
    }

    suspend fun setUserRole(userId: String, role: String): Boolean = withContext(Dispatchers.IO) {
        val database = db ?: return@withContext false
        try {
            database.collection("users").document(userId).update("role", role).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error setting user role", e)
            false
        }
    }

    suspend fun updateUserPoints(userId: String, pointsDelta: Long, isExamWin: Boolean) = withContext(Dispatchers.IO) {
        val database = db ?: return@withContext
        try {
            val userRef = database.collection("users").document(userId)
            database.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentPoints = snapshot.getLong("points") ?: 0L
                val currentStreak = snapshot.getLong("streak") ?: 0L
                val newPoints = (currentPoints + pointsDelta).coerceAtLeast(0L)
                val newStreak = if (isExamWin) currentStreak + 1L else currentStreak
                transaction.update(userRef, "points", newPoints)
                transaction.update(userRef, "streak", newStreak)
            }.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user points", e)
        }
    }

    fun observeCommunityPosts(): Flow<List<FirebasePostModel>> {
        val database = db ?: return flowOf(emptyList())
        return database.collection("community_posts")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(FirebasePostModel::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
            }
            .catch { e ->
                Log.w(TAG, "Error observing community posts", e)
                emit(emptyList())
            }
    }

    suspend fun createCommunityPost(post: FirebasePostModel): Boolean = withContext(Dispatchers.IO) {
        val database = db ?: return@withContext true
        try {
            val docRef = if (post.id.isNotBlank()) {
                database.collection("community_posts").document(post.id)
            } else {
                database.collection("community_posts").document()
            }
            val data = mapOf(
                "id" to docRef.id,
                "authorId" to post.authorId,
                "authorNickname" to post.authorNickname,
                "title" to post.title,
                "content" to post.content,
                "category" to post.category,
                "score" to post.score,
                "likesCount" to 0L,
                "likedUserIds" to emptyList<String>(),
                "commentsCount" to 0L,
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating community post", e)
            false
        }
    }

    suspend fun togglePostLike(postId: String, userId: String) = withContext(Dispatchers.IO) {
        val database = db ?: return@withContext
        try {
            val postRef = database.collection("community_posts").document(postId)
            database.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                @Suppress("UNCHECKED_CAST")
                val likedList = (snapshot.get("likedUserIds") as? List<String>)?.toMutableList() ?: mutableListOf()
                val currentLikes = snapshot.getLong("likesCount") ?: 0L
                if (likedList.contains(userId)) {
                    likedList.remove(userId)
                    transaction.update(postRef, "likedUserIds", likedList)
                    transaction.update(postRef, "likesCount", (currentLikes - 1L).coerceAtLeast(0L))
                } else {
                    likedList.add(userId)
                    transaction.update(postRef, "likedUserIds", likedList)
                    transaction.update(postRef, "likesCount", currentLikes + 1L)
                }
            }.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling post like", e)
        }
    }

    fun observeComments(postId: String): Flow<List<FirebaseCommentModel>> {
        val database = db ?: return flowOf(emptyList())
        return database.collection("community_posts").document(postId)
            .collection("comments")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(FirebaseCommentModel::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
                    .sortedBy { it.createdAt?.toDate()?.time ?: 0L }
            }
            .catch { e ->
                Log.w(TAG, "Error observing comments for post $postId", e)
                emit(emptyList())
            }
    }

    suspend fun addComment(postId: String, authorId: String, authorNickname: String, text: String): Boolean = withContext(Dispatchers.IO) {
        val database = db ?: return@withContext true
        try {
            val postRef = database.collection("community_posts").document(postId)
            val commentRef = postRef.collection("comments").document()
            val commentData = mapOf(
                "id" to commentRef.id,
                "postId" to postId,
                "authorId" to authorId,
                "authorNickname" to authorNickname,
                "text" to text.trim(),
                "createdAt" to FieldValue.serverTimestamp()
            )
            val batch = database.batch()
            batch.set(commentRef, commentData)
            batch.update(postRef, "commentsCount", FieldValue.increment(1))
            batch.commit().await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding comment", e)
            false
        }
    }

    fun observeLegalMaterials(): Flow<List<FirebaseLegalMaterialModel>> {
        val database = db ?: return flowOf(emptyList())
        return database.collection("legal_materials")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(FirebaseLegalMaterialModel::class.java, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE)
            }
            .catch { e ->
                Log.w(TAG, "Error observing legal materials", e)
                emit(emptyList())
            }
    }

    suspend fun saveLegalMaterial(material: FirebaseLegalMaterialModel): Boolean = withContext(Dispatchers.IO) {
        val database = db ?: return@withContext true
        try {
            val docRef = if (material.id.isNotBlank()) {
                database.collection("legal_materials").document(material.id)
            } else {
                database.collection("legal_materials").document()
            }
            val data = mapOf(
                "id" to docRef.id,
                "category" to material.category,
                "articleNumber" to material.articleNumber,
                "title" to material.title,
                "summary" to material.summary,
                "fullText" to material.fullText,
                "keywords" to material.keywords,
                "updatedBy" to material.updatedBy,
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving legal material", e)
            false
        }
    }

    suspend fun deleteLegalMaterial(materialId: String): Boolean = withContext(Dispatchers.IO) {
        val database = db ?: return@withContext true
        try {
            database.collection("legal_materials").document(materialId).delete().await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting legal material", e)
            false
        }
    }

    suspend fun seedInitialMaterialsIfEmpty(defaults: List<FirebaseLegalMaterialModel>) = withContext(Dispatchers.IO) {
        val database = db ?: return@withContext
        try {
            val current = database.collection("legal_materials").limit(1).get().await()
            if (current.isEmpty) {
                val batch = database.batch()
                for (item in defaults) {
                    val ref = database.collection("legal_materials").document()
                    val data = mapOf(
                        "id" to ref.id,
                        "category" to item.category,
                        "articleNumber" to item.articleNumber,
                        "title" to item.title,
                        "summary" to item.summary,
                        "fullText" to item.fullText,
                        "keywords" to item.keywords,
                        "updatedBy" to "Sistem",
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                    batch.set(ref, data)
                }
                batch.commit().await()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error seeding default legal materials", e)
        }
    }
}
