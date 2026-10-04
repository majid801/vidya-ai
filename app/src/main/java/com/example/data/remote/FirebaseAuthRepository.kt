package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.data.local.entity.UserProfileEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseAuthRepository(private val context: Context) {

    private val isFirebaseAvailable: Boolean
        get() {
            return try {
                FirebaseApp.getApps(context).isNotEmpty()
            } catch (e: Exception) {
                false
            }
        }

    private val auth: FirebaseAuth?
        get() {
            return try {
                if (isFirebaseAvailable) FirebaseAuth.getInstance() else null
            } catch (e: Exception) {
                null
            }
        }

    private val firestore: FirebaseFirestore?
        get() {
            return try {
                if (isFirebaseAvailable) FirebaseFirestore.getInstance() else null
            } catch (e: Exception) {
                null
            }
        }

    val currentUser: FirebaseUser?
        get() = auth?.currentUser

    suspend fun signInWithEmail(email: String, pass: String): Result<String> = withContext(Dispatchers.IO) {
        val authInstance = auth
        if (authInstance == null) {
            return@withContext Result.success("Signed in as Offline Student")
        }
        try {
            val authResult = authInstance.signInWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: "user_default"
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, pass: String): Result<String> = withContext(Dispatchers.IO) {
        val authInstance = auth
        if (authInstance == null) {
            return@withContext Result.success("Created Offline Student Profile")
        }
        try {
            val authResult = authInstance.createUserWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: "user_default"
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInAnonymously(): Result<String> = withContext(Dispatchers.IO) {
        val authInstance = auth
        if (authInstance == null) {
            return@withContext Result.success("offline_guest_id")
        }
        try {
            val authResult = authInstance.signInAnonymously().await()
            val uid = authResult.user?.uid ?: "guest_${System.currentTimeMillis()}"
            Result.success(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Sign out error", e)
        }
    }

    /**
     * Persist user data to Firestore
     */
    suspend fun syncProfileToFirestore(profile: UserProfileEntity): Result<Unit> = withContext(Dispatchers.IO) {
        val db = firestore
        val user = currentUser
        if (db == null || user == null) {
            // Local persistence handles offline state
            return@withContext Result.success(Unit)
        }
        try {
            val data = hashMapOf(
                "name" to profile.name,
                "grade" to profile.grade,
                "stream" to profile.stream,
                "board" to profile.board,
                "targetExam" to profile.targetExam,
                "xp" to profile.xp,
                "level" to profile.level,
                "streakDays" to profile.streakDays,
                "lastActive" to profile.lastActiveTimestamp,
                "email" to (user.email ?: profile.email)
            )
            db.collection("students")
                .document(user.uid)
                .set(data, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("Firestore", "Sync failed, continuing locally", e)
            Result.success(Unit)
        }
    }

    /**
     * Persist quiz results to Firestore
     */
    suspend fun syncQuizAttemptToFirestore(
        title: String,
        subject: String,
        score: Int,
        total: Int
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val db = firestore
        val user = currentUser
        if (db == null || user == null) return@withContext Result.success(Unit)
        try {
            val record = hashMapOf(
                "title" to title,
                "subject" to subject,
                "score" to score,
                "total" to total,
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("students")
                .document(user.uid)
                .collection("quizzes")
                .add(record)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("Firestore", "Quiz sync failed", e)
            Result.success(Unit)
        }
    }
}
