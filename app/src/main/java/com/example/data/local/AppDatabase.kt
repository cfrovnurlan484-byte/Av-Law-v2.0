package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ArticleDao
import com.example.data.local.dao.ExamResultDao
import com.example.data.local.dao.LegalSourceDao
import com.example.data.local.dao.UserDao
import com.example.data.local.model.ArticleEntity
import com.example.data.local.model.ExamResultEntity
import com.example.data.local.model.LegalSourceEntity
import com.example.data.local.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserProfile::class,
        ExamResultEntity::class,
        ArticleEntity::class,
        LegalSourceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun examResultDao(): ExamResultDao
    abstract fun articleDao(): ArticleDao
    abstract fun legalSourceDao(): LegalSourceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "azhuquq_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }

            suspend fun populateDatabase(database: AppDatabase) {
                database.userDao().insertOrUpdate(LegalDatabaseSeeder.getInitialProfile())
                LegalDatabaseSeeder.getInitialExams().forEach {
                    database.examResultDao().insert(it)
                }
                LegalDatabaseSeeder.getInitialArticles().forEach {
                    database.articleDao().insert(it)
                }
                database.legalSourceDao().insertAll(LegalDatabaseSeeder.getInitialLegalSources())
            }
        }
    }
}
