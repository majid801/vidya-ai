package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ChatDao
import com.example.data.local.dao.DocumentDao
import com.example.data.local.dao.FlashcardDao
import com.example.data.local.dao.JarvisMemoryDao
import com.example.data.local.dao.MistakeDao
import com.example.data.local.dao.QuizDao
import com.example.data.local.dao.StudyNoteDao
import com.example.data.local.dao.StudyTaskDao
import com.example.data.local.dao.TopicMasteryDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.FlashcardEntity
import com.example.data.local.entity.JarvisMemoryEntity
import com.example.data.local.entity.MistakeEntity
import com.example.data.local.entity.QuizAttemptEntity
import com.example.data.local.entity.StudyNoteEntity
import com.example.data.local.entity.StudyTaskEntity
import com.example.data.local.entity.TopicMasteryEntity
import com.example.data.local.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        ChatMessageEntity::class,
        StudyTaskEntity::class,
        QuizAttemptEntity::class,
        FlashcardEntity::class,
        StudyNoteEntity::class,
        TopicMasteryEntity::class,
        DocumentEntity::class,
        MistakeEntity::class,
        JarvisMemoryEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun studyTaskDao(): StudyTaskDao
    abstract fun quizDao(): QuizDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun studyNoteDao(): StudyNoteDao
    abstract fun topicMasteryDao(): TopicMasteryDao
    abstract fun documentDao(): DocumentDao
    abstract fun mistakeDao(): MistakeDao
    abstract fun jarvisMemoryDao(): JarvisMemoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vidya_ai_learning.db"
                ).fallbackToDestructiveMigration(dropAllTables = false).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
