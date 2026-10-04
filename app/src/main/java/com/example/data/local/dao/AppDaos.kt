package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.FlashcardEntity
import com.example.data.local.entity.QuizAttemptEntity
import com.example.data.local.entity.StudyNoteEntity
import com.example.data.local.entity.StudyTaskEntity
import com.example.data.local.entity.TopicMasteryEntity
import com.example.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    fun getUserProfile(id: String = "primary_student"): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    suspend fun getUserProfileOnce(id: String = "primary_student"): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profiles SET xp = xp + :gainedXp WHERE id = :id")
    suspend fun addXp(id: String = "primary_student", gainedXp: Int)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    fun getMessages(convId: String = "default"): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE conversationId = :convId")
    suspend fun clearMessages(convId: String = "default")
}

@Dao
interface StudyTaskDao {
    @Query("SELECT * FROM study_tasks ORDER BY isCompleted ASC, timestamp DESC")
    fun getAllTasks(): Flow<List<StudyTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: StudyTaskEntity)

    @Update
    suspend fun updateTask(task: StudyTaskEntity)

    @Query("DELETE FROM study_tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)
}

@Dao
interface QuizDao {
    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    fun getQuizAttempts(): Flow<List<QuizAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizAttempt(attempt: QuizAttemptEntity)
}

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards ORDER BY masteryLevel ASC, lastReviewedTimestamp DESC")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity)

    @Update
    suspend fun updateFlashcard(flashcard: FlashcardEntity)

    @Query("DELETE FROM flashcards WHERE id = :cardId")
    suspend fun deleteFlashcard(cardId: Long)
}

@Dao
interface StudyNoteDao {
    @Query("SELECT * FROM study_notes ORDER BY isPinned DESC, timestamp DESC")
    fun getAllNotes(): Flow<List<StudyNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: StudyNoteEntity)

    @Query("DELETE FROM study_notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: Long)
}

@Dao
interface TopicMasteryDao {
    @Query("SELECT * FROM topic_mastery ORDER BY lastPracticedTimestamp DESC")
    fun getAllMastery(): Flow<List<TopicMasteryEntity>>

    @Query("SELECT * FROM topic_mastery WHERE status = 'Weak' ORDER BY accuracy ASC")
    fun getWeakTopics(): Flow<List<TopicMasteryEntity>>

    @Query("SELECT * FROM topic_mastery WHERE status = 'Mastered' ORDER BY accuracy DESC")
    fun getStrongTopics(): Flow<List<TopicMasteryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMastery(mastery: TopicMasteryEntity)
}

@Dao
interface DocumentDao {
    @Query("SELECT * FROM study_documents ORDER BY timestamp DESC")
    fun getAllDocuments(): Flow<List<com.example.data.local.entity.DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: com.example.data.local.entity.DocumentEntity)

    @Query("DELETE FROM study_documents WHERE id = :docId")
    suspend fun deleteDocument(docId: Long)
}

@Dao
interface MistakeDao {
    @Query("SELECT * FROM mistake_notebook ORDER BY isResolved ASC, timestamp DESC")
    fun getAllMistakes(): Flow<List<com.example.data.local.entity.MistakeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMistake(mistake: com.example.data.local.entity.MistakeEntity)

    @Query("UPDATE mistake_notebook SET isResolved = :resolved WHERE id = :id")
    suspend fun setMistakeResolved(id: Long, resolved: Boolean)

    @Query("DELETE FROM mistake_notebook WHERE id = :id")
    suspend fun deleteMistake(id: Long)
}

@Dao
interface JarvisMemoryDao {
    @Query("SELECT * FROM jarvis_memory ORDER BY timestamp DESC")
    fun getAllMemory(): Flow<List<com.example.data.local.entity.JarvisMemoryEntity>>

    @Query("SELECT * FROM jarvis_memory WHERE `key` = :key LIMIT 1")
    suspend fun getMemoryByKey(key: String): com.example.data.local.entity.JarvisMemoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMemory(memory: com.example.data.local.entity.JarvisMemoryEntity)

    @Query("DELETE FROM jarvis_memory WHERE `key` = :key")
    suspend fun deleteMemory(key: String)
}
