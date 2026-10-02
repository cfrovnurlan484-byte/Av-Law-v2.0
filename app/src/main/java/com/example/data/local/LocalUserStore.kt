package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.firestore.model.FirebaseUserModel
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class LocalUserStore(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("azhuquq_auth_store", Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "LocalUserStore"
        private const val KEY_CURRENT_USER_ID = "current_logged_in_user_id"
        private const val KEY_USERS_LIST = "registered_local_users"
    }

    data class LocalUserRecord(
        val id: String,
        val nickname: String,
        val email: String,
        val password: String,
        val role: String = "user",
        val points: Long = 0L,
        val streak: Long = 0L,
        val createdAt: Long = System.currentTimeMillis()
    ) {
        fun toFirebaseUserModel(): FirebaseUserModel {
            return FirebaseUserModel(
                id = id,
                nickname = nickname,
                email = email,
                role = role,
                points = points,
                streak = streak,
                achievements = emptyList(),
                timeoutUntilMillis = 0L
            )
        }

        fun toJson(): JSONObject {
            return JSONObject().apply {
                put("id", id)
                put("nickname", nickname)
                put("email", email)
                put("password", password)
                put("role", role)
                put("points", points)
                put("streak", streak)
                put("createdAt", createdAt)
            }
        }

        companion object {
            fun fromJson(json: JSONObject): LocalUserRecord {
                return LocalUserRecord(
                    id = json.optString("id", UUID.randomUUID().toString()),
                    nickname = json.optString("nickname", "Hüquqşünas"),
                    email = json.optString("email", ""),
                    password = json.optString("password", ""),
                    role = json.optString("role", "user"),
                    points = json.optLong("points", 0L),
                    streak = json.optLong("streak", 0L),
                    createdAt = json.optLong("createdAt", System.currentTimeMillis())
                )
            }
        }
    }

    @Synchronized
    fun getAllUsers(): List<LocalUserRecord> {
        val raw = prefs.getString(KEY_USERS_LIST, null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(raw)
            val list = mutableListOf<LocalUserRecord>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(LocalUserRecord.fromJson(obj))
            }
            list
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing local users json", e)
            emptyList()
        }
    }

    @Synchronized
    private fun saveAllUsers(users: List<LocalUserRecord>) {
        val jsonArray = JSONArray()
        users.forEach { jsonArray.put(it.toJson()) }
        prefs.edit().putString(KEY_USERS_LIST, jsonArray.toString()).apply()
    }

    fun isNicknameAvailable(nickname: String): Boolean {
        val clean = nickname.trim().lowercase()
        if (clean.isBlank()) return false
        val existing = getAllUsers()
        return existing.none { it.nickname.trim().lowercase() == clean }
    }

    @Synchronized
    fun registerUser(nickname: String, email: String, password: String): FirebaseUserModel {
        val cleanNick = nickname.trim()
        val cleanEmail = email.trim()
        val users = getAllUsers().toMutableList()
        val newId = "local_usr_" + UUID.randomUUID().toString().take(12)
        val newRecord = LocalUserRecord(
            id = newId,
            nickname = cleanNick,
            email = cleanEmail,
            password = password.trim(),
            role = "user",
            points = 0L,
            streak = 0L,
            createdAt = System.currentTimeMillis()
        )
        users.add(newRecord)
        saveAllUsers(users)
        setCurrentUserId(newId)
        return newRecord.toFirebaseUserModel()
    }

    @Synchronized
    fun loginUser(emailOrNickname: String, password: String): FirebaseUserModel? {
        val cleanQuery = emailOrNickname.trim().lowercase()
        val cleanPass = password.trim()
        val users = getAllUsers()
        val match = users.find {
            (it.email.trim().lowercase() == cleanQuery || it.nickname.trim().lowercase() == cleanQuery) &&
            it.password == cleanPass
        }
        return if (match != null) {
            setCurrentUserId(match.id)
            match.toFirebaseUserModel()
        } else {
            null
        }
    }

    fun getCurrentUser(): FirebaseUserModel? {
        val currentId = prefs.getString(KEY_CURRENT_USER_ID, null) ?: return null
        val users = getAllUsers()
        val user = users.find { it.id == currentId }
        return user?.toFirebaseUserModel()
    }

    fun saveCurrentUser(user: FirebaseUserModel) {
        setCurrentUserId(user.id)
        val users = getAllUsers().toMutableList()
        val existingIndex = users.indexOfFirst { it.id == user.id }
        if (existingIndex != -1) {
            val old = users[existingIndex]
            users[existingIndex] = old.copy(
                nickname = user.nickname.ifBlank { old.nickname },
                email = user.email.ifBlank { old.email },
                points = user.points,
                streak = user.streak,
                role = user.role
            )
        } else {
            users.add(
                LocalUserRecord(
                    id = user.id,
                    nickname = user.nickname.ifBlank { "Hüquqşünas" },
                    email = user.email,
                    password = "",
                    role = user.role,
                    points = user.points,
                    streak = user.streak
                )
            )
        }
        saveAllUsers(users)
    }

    fun setCurrentUserId(userId: String) {
        prefs.edit().putString(KEY_CURRENT_USER_ID, userId).apply()
    }

    fun clearCurrentUser() {
        prefs.edit().remove(KEY_CURRENT_USER_ID).apply()
    }

    @Synchronized
    fun getOrCreateQuickUser(): FirebaseUserModel {
        val current = getCurrentUser()
        if (current != null) return current
        val users = getAllUsers()
        if (users.isNotEmpty()) {
            val first = users.first()
            setCurrentUserId(first.id)
            return first.toFirebaseUserModel()
        }
        return registerUser("Vəkil_Samir", "samir@azlaw.az", "12345678a")
    }
}
