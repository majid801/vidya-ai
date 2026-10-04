package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.FlashcardEntity
import com.example.data.local.entity.QuizAttemptEntity
import com.example.data.local.entity.StudyNoteEntity
import com.example.data.local.entity.StudyTaskEntity
import com.example.data.local.entity.TopicMasteryEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.model.ChapterInfo
import com.example.data.model.QuizQuestion
import com.example.data.model.SubjectInfo
import com.example.data.model.TeachingMode
import com.example.data.remote.FirebaseAuthRepository
import com.example.data.remote.GeminiRepository
import com.example.domain.CurriculumData
import com.example.util.AudioHelper
import com.example.util.AudioRecorderHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
    ONBOARDING,
    HOME,
    TUTOR_CHAT,
    CURRICULUM,
    PRACTICE_QUIZ,
    STUDY_PLANNER,
    VOICE_LIVE,
    NOTES_FLASHCARDS,
    MEDIA_LAB,
    PROGRESS,
    PARENT_VIEW,
    TEACHER_VIEW,
    PROFILE,
    DOCUMENTS,
    MISTAKE_NOTEBOOK,
    ERROR_RECOVERY,
    TEXTBOOKS,
    PREVIOUS_PAPERS,
    ADMIN_DASHBOARD,
    LEARNING_PATH_INTELLIGENCE
}

class EduMentorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val geminiRepo = GeminiRepository()
    val authRepo = FirebaseAuthRepository(application)
    val audioHelper = AudioHelper(application)
    val audioRecorder = AudioRecorderHelper(application)

    // Current navigation state
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Navigation backstack for proper BackHandler support
    private val screenStack = mutableListOf(AppScreen.HOME)

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            screenStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.size - 1)
            _currentScreen.value = screenStack.last()
            return true
        }
        return false
    }

    // Database Flows
    val userProfile: StateFlow<UserProfileEntity?> = db.userDao().getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val chatMessages: StateFlow<List<ChatMessageEntity>> = db.chatDao().getMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyTasks: StateFlow<List<StudyTaskEntity>> = db.studyTaskDao().getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizAttempts: StateFlow<List<QuizAttemptEntity>> = db.quizDao().getQuizAttempts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flashcards: StateFlow<List<FlashcardEntity>> = db.flashcardDao().getAllFlashcards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyNotes: StateFlow<List<StudyNoteEntity>> = db.studyNoteDao().getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weakTopics: StateFlow<List<TopicMasteryEntity>> = db.topicMasteryDao().getWeakTopics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val strongTopics: StateFlow<List<TopicMasteryEntity>> = db.topicMasteryDao().getStrongTopics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val documents: StateFlow<List<com.example.data.local.entity.DocumentEntity>> = db.documentDao().getAllDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mistakes: StateFlow<List<com.example.data.local.entity.MistakeEntity>> = db.mistakeDao().getAllMistakes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val jarvisMemory: StateFlow<List<com.example.data.local.entity.JarvisMemoryEntity>> = db.jarvisMemoryDao().getAllMemory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Global Floating JARVIS & Global Search States
    val isJarvisSheetOpen = MutableStateFlow(false)
    val isSearchDialogOpen = MutableStateFlow(false)
    val lastSystemError = MutableStateFlow<com.example.data.ai.AppError?>(null)

    // Resilient Fallback AI Provider
    val fallbackProvider = com.example.data.ai.FallbackProvider()

    // Active Selection State
    val selectedSubject = MutableStateFlow<SubjectInfo?>(null)
    val selectedChapter = MutableStateFlow<ChapterInfo?>(null)
    val selectedTeachingMode = MutableStateFlow(TeachingMode.NORMAL)
    val isThinkingModeActive = MutableStateFlow(false)
    val isSearchGroundingActive = MutableStateFlow(false)

    // Chat input state & loading
    val chatInputText = MutableStateFlow("")
    val isAiThinking = MutableStateFlow(false)
    val pendingImageBitmap = MutableStateFlow<Bitmap?>(null)

    // Voice & Audio State
    val isRecordingVoice = MutableStateFlow(false)
    val liveVoiceStatus = MutableStateFlow("Ready to talk with Gemini Live")
    val isTtsPlaying = MutableStateFlow(false)

    // Quiz Player State
    val currentQuizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val currentQuizIndex = MutableStateFlow(0)
    val selectedAnswers = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val isQuizSubmitted = MutableStateFlow(false)
    val isGeneratingQuiz = MutableStateFlow(false)

    // Media Lab State (Images & Veo Video)
    val mediaPrompt = MutableStateFlow("")
    val mediaResultImageBase64 = MutableStateFlow<String?>(null)
    val mediaStatusMessage = MutableStateFlow("")
    val isGeneratingMedia = MutableStateFlow(false)
    val selectedImageResolution = MutableStateFlow("1K") // 1K, 2K, 4K
    val selectedVideoAspect = MutableStateFlow("16:9")   // 16:9, 9:16

    init {
        initializeDefaultData()
    }

    private fun initializeDefaultData() {
        viewModelScope.launch {
            val existing = db.userDao().getUserProfileOnce()
            if (existing == null) {
                val defaultProfile = UserProfileEntity(
                    id = "primary_student",
                    name = "Aarav Sharma",
                    grade = "Class 10",
                    stream = "General",
                    board = "CBSE",
                    targetExam = "Class 10 Board Exams",
                    dailyGoalMinutes = 120,
                    xp = 250,
                    level = 2,
                    streakDays = 5
                )
                db.userDao().insertOrUpdateProfile(defaultProfile)

                // Seed initial subjects
                val defaultSubjects = CurriculumData.getSubjectsFor("Class 10")
                if (defaultSubjects.isNotEmpty()) {
                    selectedSubject.value = defaultSubjects.first()
                    selectedChapter.value = defaultSubjects.first().chapters.firstOrNull()
                }

                // Seed initial study tasks
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                db.studyTaskDao().insertTask(
                    StudyTaskEntity(
                        title = "Practice Quadratic Formula Derivation",
                        subject = "Mathematics",
                        chapter = "Quadratic Equations",
                        allocatedMinutes = 40,
                        dateString = today,
                        priority = "High"
                    )
                )
                db.studyTaskDao().insertTask(
                    StudyTaskEntity(
                        title = "Review Ohm's Law and Series Resistors",
                        subject = "Physics",
                        chapter = "Electricity",
                        allocatedMinutes = 35,
                        dateString = today,
                        priority = "Medium"
                    )
                )

                // Seed initial flashcards
                db.flashcardDao().insertFlashcard(
                    FlashcardEntity(
                        subject = "Mathematics",
                        chapter = "Quadratic Equations",
                        frontText = "What is the Discriminant (D) formula and what do its roots signify?",
                        backText = "D = b² - 4ac.\nIf D > 0: Two distinct real roots\nIf D = 0: Two equal real roots\nIf D < 0: No real roots (complex roots).",
                        masteryLevel = 1
                    )
                )
                db.flashcardDao().insertFlashcard(
                    FlashcardEntity(
                        subject = "Physics",
                        chapter = "Electricity",
                        frontText = "State Joule's Law of Heating and formula.",
                        backText = "H = I²Rt.\nHeat generated is directly proportional to the square of current, resistance of conductor, and time of flow.",
                        masteryLevel = 2
                    )
                )

                // Seed initial topic mastery (Weak and Strong for real recommendations)
                db.topicMasteryDao().upsertMastery(
                    TopicMasteryEntity(
                        topicKey = "Math_Quadratic Equations",
                        subject = "Mathematics",
                        chapter = "Quadratic Equations",
                        topicName = "Nature of Roots & Discriminant",
                        status = "Weak",
                        accuracy = 45,
                        questionsAttempted = 12
                    )
                )
                db.topicMasteryDao().upsertMastery(
                    TopicMasteryEntity(
                        topicKey = "Physics_Light",
                        subject = "Physics",
                        chapter = "Light - Reflection & Refraction",
                        topicName = "Spherical Mirror Formula",
                        status = "Mastered",
                        accuracy = 92,
                        questionsAttempted = 25
                    )
                )
            } else {
                val subjects = CurriculumData.getSubjectsFor(existing.grade, existing.stream)
                if (subjects.isNotEmpty()) {
                    selectedSubject.value = subjects.first()
                    selectedChapter.value = subjects.first().chapters.firstOrNull()
                }
            }
        }
    }

    fun saveOnboardingProfile(
        name: String,
        grade: String,
        stream: String,
        board: String,
        targetExam: String,
        state: String = "Telangana",
        medium: String = "English"
    ) {
        viewModelScope.launch {
            val updated = UserProfileEntity(
                name = name.ifBlank { "Student" },
                grade = grade,
                stream = stream,
                board = board,
                targetExam = targetExam,
                country = "India",
                state = state,
                medium = medium,
                xp = 100,
                level = 1,
                streakDays = 1
            )
            db.userDao().insertOrUpdateProfile(updated)
            authRepo.syncProfileToFirestore(updated)

            val subjects = CurriculumData.getSubjectsFor(grade, stream)
            if (subjects.isNotEmpty()) {
                selectedSubject.value = subjects.first()
                selectedChapter.value = subjects.first().chapters.firstOrNull()
            }

            // Speak dynamic welcome
            audioHelper.speak("Hello ${updated.name}. Welcome to Vidya AI. I have configured your learning workspace for $grade $board. Let's begin!")

            navigateTo(AppScreen.HOME)
        }
    }

    /**
     * Send AI Chat Doubt / Query
     */
    fun sendChatMessage() {
        val text = chatInputText.value.trim()
        val image = pendingImageBitmap.value
        if (text.isEmpty() && image == null) return

        chatInputText.value = ""
        pendingImageBitmap.value = null

        val profile = userProfile.value
        val grade = profile?.grade ?: "Class 10"
        val subject = selectedSubject.value?.name ?: "General Studies"
        val chapter = selectedChapter.value?.title ?: "Academic Doubt"
        val mode = selectedTeachingMode.value
        val useThinking = isThinkingModeActive.value
        val useSearch = isSearchGroundingActive.value

        viewModelScope.launch {
            // Save user message to database
            db.chatDao().insertMessage(
                ChatMessageEntity(
                    sender = "user",
                    text = text.ifBlank { "Please analyze this uploaded educational image." },
                    subject = subject,
                    mode = mode.displayName,
                    hasThinking = useThinking
                )
            )

            isAiThinking.value = true

            if (useSearch) {
                // Search Grounding path using gemini-3.5-flash with googleSearch tool
                val searchResult = geminiRepo.searchGroundedQuery(text, grade, subject)
                isAiThinking.value = false
                searchResult.onSuccess { (answer, sources) ->
                    val fullResponse = if (sources.isNotEmpty()) {
                        "$answer\n\n**Verified Sources:**\n" + sources.joinToString("\n") { "• $it" }
                    } else {
                        answer
                    }
                    db.chatDao().insertMessage(
                        ChatMessageEntity(
                            sender = "ai",
                            text = fullResponse,
                            subject = subject,
                            mode = "Search Grounded"
                        )
                    )
                    db.userDao().addXp(gainedXp = 15)
                }.onFailure { err ->
                    db.chatDao().insertMessage(
                        ChatMessageEntity(
                            sender = "ai",
                            text = "I encountered an issue verifying external search data: ${err.localizedMessage}. Let's solve it together conceptually!",
                            subject = subject,
                            mode = mode.displayName
                        )
                    )
                }
            } else {
                // Standard or Thinking AI Chat path
                val history = chatMessages.value.takeLast(6).map { it.sender to it.text }
                val result = geminiRepo.sendChatMessage(
                    history = history,
                    userMessage = text,
                    grade = grade,
                    subject = subject,
                    chapter = chapter,
                    teachingMode = mode,
                    isComplexTask = useThinking,
                    useThinkingMode = useThinking,
                    imageBitmap = image
                )

                isAiThinking.value = false
                result.onSuccess { reply ->
                    db.chatDao().insertMessage(
                        ChatMessageEntity(
                            sender = "ai",
                            text = reply,
                            subject = subject,
                            mode = mode.displayName,
                            hasThinking = useThinking
                        )
                    )
                    db.userDao().addXp(gainedXp = 20)
                }.onFailure { err ->
                    db.chatDao().insertMessage(
                        ChatMessageEntity(
                            sender = "ai",
                            text = "Your AI tutor is temporarily offline or experiencing a connection issue (${err.localizedMessage}). Please try again in a moment.",
                            subject = subject,
                            mode = mode.displayName
                        )
                    )
                }
            }
        }
    }

    /**
     * Text to Speech (TTS) using gemini-3.8-flash-tts
     */
    fun speakChatMessage(text: String) {
        viewModelScope.launch {
            isTtsPlaying.value = true
            val result = geminiRepo.generateSpeech(text.take(300))
            result.onSuccess { audioBytes ->
                if (audioBytes != null && audioBytes.isNotEmpty()) {
                    audioHelper.playAudioBytes(audioBytes) {
                        isTtsPlaying.value = false
                    }
                } else {
                    // Fallback to native Android TTS
                    audioHelper.speakText(text.take(300)) {
                        isTtsPlaying.value = false
                    }
                }
            }.onFailure {
                audioHelper.speakText(text.take(300)) {
                    isTtsPlaying.value = false
                }
            }
        }
    }

    fun stopSpeaking() {
        audioHelper.stopAudio()
        isTtsPlaying.value = false
    }

    /**
     * Start/Stop Voice recording for gemini-3.5-transcribe
     */
    fun toggleVoiceRecording() {
        if (isRecordingVoice.value) {
            val audioBase64 = audioRecorder.stopRecording()
            isRecordingVoice.value = false
            if (!audioBase64.isNullOrEmpty()) {
                viewModelScope.launch {
                    liveVoiceStatus.value = "Transcribing with gemini-3.5-transcribe..."
                    val result = geminiRepo.transcribeAudio(audioBase64)
                    result.onSuccess { transcript ->
                        chatInputText.value = transcript
                        liveVoiceStatus.value = "Transcribed: \"$transcript\""
                    }.onFailure { err ->
                        liveVoiceStatus.value = "Transcription error: ${err.localizedMessage}"
                    }
                }
            }
        } else {
            val started = audioRecorder.startRecording()
            if (started) {
                isRecordingVoice.value = true
                liveVoiceStatus.value = "Listening to your academic question..."
            }
        }
    }

    /**
     * Live Voice Interaction with gemini-3.8-live
     */
    fun sendLiveVoiceTurn(transcript: String) {
        val grade = userProfile.value?.grade ?: "Class 10"
        val subject = selectedSubject.value?.name ?: "General"
        viewModelScope.launch {
            liveVoiceStatus.value = "AI Tutor is answering..."
            val result = geminiRepo.sendLiveVoiceTurn(transcript, grade, subject)
            result.onSuccess { reply ->
                liveVoiceStatus.value = reply
                speakChatMessage(reply)
            }.onFailure { err ->
                liveVoiceStatus.value = "Voice live error: ${err.localizedMessage}"
            }
        }
    }

    /**
     * Generate AI Quiz
     */
    fun startChapterQuiz(subject: SubjectInfo, chapter: ChapterInfo, difficulty: String = "Medium") {
        selectedSubject.value = subject
        selectedChapter.value = chapter
        isGeneratingQuiz.value = true
        isQuizSubmitted.value = false
        selectedAnswers.value = emptyMap()
        currentQuizIndex.value = 0
        navigateTo(AppScreen.PRACTICE_QUIZ)

        val grade = userProfile.value?.grade ?: "Class 10"
        viewModelScope.launch {
            val result = geminiRepo.generateQuiz(grade, subject.name, chapter.title, difficulty)
            isGeneratingQuiz.value = false
            result.onSuccess { rawResponse ->
                val parsed = parseGeneratedQuiz(rawResponse, subject.name, chapter.title)
                currentQuizQuestions.value = parsed
            }.onFailure {
                // Fallback to smart pre-configured curriculum questions
                currentQuizQuestions.value = getFallbackQuestions(subject.name, chapter.title)
            }
        }
    }

    private fun parseGeneratedQuiz(text: String, subject: String, chapter: String): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()
        try {
            val blocks = text.split(Regex("(?=Q\\d+:)"))
            var qId = 1
            for (block in blocks) {
                if (block.contains("Q") && block.contains("A)")) {
                    val lines = block.lines().map { it.trim() }.filter { it.isNotEmpty() }
                    val qText = lines.firstOrNull { it.startsWith("Q") }?.replace(Regex("^Q\\d+:\\s*"), "") ?: "Question $qId"
                    val optA = lines.firstOrNull { it.startsWith("A)") }?.substringAfter("A)")?.trim() ?: "Option A"
                    val optB = lines.firstOrNull { it.startsWith("B)") }?.substringAfter("B)")?.trim() ?: "Option B"
                    val optC = lines.firstOrNull { it.startsWith("C)") }?.substringAfter("C)")?.trim() ?: "Option C"
                    val optD = lines.firstOrNull { it.startsWith("D)") }?.substringAfter("D)")?.trim() ?: "Option D"

                    val correctChar = lines.firstOrNull { it.startsWith("Correct:") }?.substringAfter("Correct:")?.trim()?.uppercase() ?: "A"
                    val correctIdx = when (correctChar.take(1)) {
                        "A" -> 0
                        "B" -> 1
                        "C" -> 2
                        "D" -> 3
                        else -> 0
                    }
                    val explanation = lines.firstOrNull { it.startsWith("Explanation:") }?.substringAfter("Explanation:")?.trim() ?: "Standard curriculum concept."
                    val topic = lines.firstOrNull { it.startsWith("TopicTag:") }?.substringAfter("TopicTag:")?.trim() ?: chapter

                    questions.add(
                        QuizQuestion(
                            id = qId++,
                            questionText = qText,
                            options = listOf(optA, optB, optC, optD),
                            correctIndex = correctIdx,
                            explanation = explanation,
                            topicTag = topic
                        )
                    )
                }
            }
        } catch (_: Exception) {}

        return if (questions.size >= 3) questions else getFallbackQuestions(subject, chapter)
    }

    private fun getFallbackQuestions(subject: String, chapter: String): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = 1,
                questionText = "For a quadratic equation ax² + bx + c = 0, what condition ensures two equal real roots?",
                options = listOf("b² - 4ac > 0", "b² - 4ac = 0", "b² - 4ac < 0", "b = 0"),
                correctIndex = 1,
                explanation = "When the discriminant D = b² - 4ac equals zero, both roots coincide at -b / (2a).",
                topicTag = "Discriminant & Nature of Roots"
            ),
            QuizQuestion(
                id = 2,
                questionText = "According to Ohm's Law, what remains constant provided temperature is maintained?",
                options = listOf("Current / Resistance", "Voltage * Current", "Ratio of Voltage to Current (V / I)", "Total heat energy"),
                correctIndex = 2,
                explanation = "V / I = R (Resistance), which is constant for a given metallic conductor at constant temperature.",
                topicTag = "Ohm's Law"
            ),
            QuizQuestion(
                id = 3,
                questionText = "Which plant tissue is primarily responsible for the transport of water and dissolved minerals from roots?",
                options = listOf("Phloem", "Xylem", "Parenchyma", "Collenchyma"),
                correctIndex = 1,
                explanation = "Xylem vessels and tracheids transport water unidirectionally from roots to leaves via transpiration pull.",
                topicTag = "Plant Transport System"
            )
        )
    }

    fun selectQuizAnswer(questionIndex: Int, optionIndex: Int) {
        if (isQuizSubmitted.value) return
        val current = selectedAnswers.value.toMutableMap()
        current[questionIndex] = optionIndex
        selectedAnswers.value = current
    }

    fun submitQuiz() {
        if (isQuizSubmitted.value) return
        isQuizSubmitted.value = true

        val questions = currentQuizQuestions.value
        val answers = selectedAnswers.value
        var correctCount = 0
        val weakDetected = mutableListOf<String>()

        questions.forEachIndexed { index, q ->
            val userAns = answers[index]
            if (userAns == q.correctIndex) {
                correctCount++
            } else {
                weakDetected.add(q.topicTag)
            }
        }

        val total = questions.size
        val percentage = if (total > 0) (correctCount * 100) / total else 0
        val subjectName = selectedSubject.value?.name ?: "Mathematics"
        val chapterName = selectedChapter.value?.title ?: "Core Chapter"

        viewModelScope.launch {
            // Save attempt to Room
            db.quizDao().insertQuizAttempt(
                QuizAttemptEntity(
                    title = "$subjectName: $chapterName Quiz",
                    subject = subjectName,
                    chapter = chapterName,
                    totalQuestions = total,
                    correctAnswers = correctCount,
                    scorePercentage = percentage,
                    weakTopicsSummary = weakDetected.joinToString(", ")
                )
            )

            // Update user XP & Level
            val earnedXp = correctCount * 25 + 20
            db.userDao().addXp(gainedXp = earnedXp)

            // Track weak/strong topics in DB
            for (weak in weakDetected) {
                db.topicMasteryDao().upsertMastery(
                    TopicMasteryEntity(
                        topicKey = "${subjectName}_$weak",
                        subject = subjectName,
                        chapter = chapterName,
                        topicName = weak,
                        status = "Weak",
                        accuracy = 30
                    )
                )
            }
            if (percentage >= 80) {
                db.topicMasteryDao().upsertMastery(
                    TopicMasteryEntity(
                        topicKey = "${subjectName}_$chapterName",
                        subject = subjectName,
                        chapter = chapterName,
                        topicName = chapterName,
                        status = "Mastered",
                        accuracy = percentage
                    )
                )
            }

            // Sync with Firestore
            authRepo.syncQuizAttemptToFirestore(
                title = "$subjectName: $chapterName",
                subject = subjectName,
                score = correctCount,
                total = total
            )
        }
    }

    /**
     * Study Planner Tasks
     */
    fun toggleTaskCompletion(task: StudyTaskEntity) {
        viewModelScope.launch {
            val updated = task.copy(isCompleted = !task.isCompleted)
            db.studyTaskDao().updateTask(updated)
            if (updated.isCompleted) {
                db.userDao().addXp(gainedXp = 30)
            }
        }
    }

    fun addNewTask(title: String, subject: String, chapter: String, minutes: Int, priority: String) {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            db.studyTaskDao().insertTask(
                StudyTaskEntity(
                    title = title,
                    subject = subject,
                    chapter = chapter,
                    allocatedMinutes = minutes,
                    dateString = today,
                    priority = priority
                )
            )
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            db.studyTaskDao().deleteTask(taskId)
        }
    }

    /**
     * Media Lab: Create / Edit Image (gemini-3.1-flash-image-preview)
     */
    fun createOrEditConceptImage(prompt: String, sourceBitmap: Bitmap? = null) {
        isGeneratingMedia.value = true
        mediaStatusMessage.value = "Generating educational diagram with gemini-3.1-flash-image-preview..."
        viewModelScope.launch {
            val result = geminiRepo.createOrEditImage(prompt, sourceBitmap)
            isGeneratingMedia.value = false
            result.onSuccess { data ->
                mediaResultImageBase64.value = data
                mediaStatusMessage.value = "Diagram created successfully!"
            }.onFailure { err ->
                mediaStatusMessage.value = "Image creation error: ${err.localizedMessage}"
            }
        }
    }

    /**
     * Media Lab: High Quality Image Generation with 1K, 2K, 4K (gemini-3-pro-image-preview)
     */
    fun generateHighQualityConceptArt(prompt: String) {
        isGeneratingMedia.value = true
        val resolution = selectedImageResolution.value
        mediaStatusMessage.value = "Rendering high-res ($resolution) visual with gemini-3-pro-image-preview..."
        viewModelScope.launch {
            val result = geminiRepo.generateHighQualityImage(
                prompt = prompt,
                imageSize = resolution,
                aspectRatio = "1:1"
            )
            isGeneratingMedia.value = false
            result.onSuccess { data ->
                mediaResultImageBase64.value = data
                mediaStatusMessage.value = "High-Quality ($resolution) visual rendered!"
            }.onFailure { err ->
                mediaStatusMessage.value = "Render error: ${err.localizedMessage}"
            }
        }
    }

    /**
     * Media Lab: Animate Image or Text to Video with Veo (veo-3.1-fast-generate-preview)
     */
    fun generateVeoConceptVideo(prompt: String, photo: Bitmap? = null) {
        isGeneratingMedia.value = true
        val aspect = selectedVideoAspect.value
        mediaStatusMessage.value = "Animating video ($aspect) using veo-3.1-fast-generate-preview..."
        viewModelScope.launch {
            val result = geminiRepo.generateVideo(
                prompt = prompt,
                sourceBitmap = photo,
                aspectRatio = aspect
            )
            isGeneratingMedia.value = false
            result.onSuccess { msg ->
                mediaStatusMessage.value = msg
            }.onFailure { err ->
                mediaStatusMessage.value = "Veo animation error: ${err.localizedMessage}"
            }
        }
    }

    /**
     * Study Notes
     */
    fun createStudyNote(title: String, subject: String, chapter: String, content: String) {
        viewModelScope.launch {
            db.studyNoteDao().insertNote(
                StudyNoteEntity(
                    title = title,
                    subject = subject,
                    chapter = chapter,
                    content = content
                )
            )
            db.userDao().addXp(gainedXp = 15)
        }
    }

    fun deleteStudyNote(noteId: Long) {
        viewModelScope.launch {
            db.studyNoteDao().deleteNote(noteId)
        }
    }

    /**
     * Flashcard mastery
     */
    fun updateFlashcardMastery(card: FlashcardEntity, newLevel: Int) {
        viewModelScope.launch {
            db.flashcardDao().updateFlashcard(card.copy(masteryLevel = newLevel))
        }
    }

    fun addCustomFlashcard(subject: String, chapter: String, front: String, back: String) {
        viewModelScope.launch {
            db.flashcardDao().insertFlashcard(
                FlashcardEntity(
                    subject = subject,
                    chapter = chapter,
                    frontText = front,
                    backText = back,
                    masteryLevel = 0
                )
            )
        }
    }

    fun deleteFlashcard(cardId: Long) {
        viewModelScope.launch {
            db.flashcardDao().deleteFlashcard(cardId)
        }
    }

    /**
     * Document Intelligence methods
     */
    fun insertDocument(doc: com.example.data.local.entity.DocumentEntity) {
        viewModelScope.launch {
            db.documentDao().insertDocument(doc)
            db.userDao().addXp(gainedXp = 20)
        }
    }

    fun deleteDocument(docId: Long) {
        viewModelScope.launch {
            db.documentDao().deleteDocument(docId)
        }
    }

    /**
     * Mistake Notebook methods
     */
    fun insertMistake(mistake: com.example.data.local.entity.MistakeEntity) {
        viewModelScope.launch {
            db.mistakeDao().insertMistake(mistake)
        }
    }

    fun setMistakeResolved(id: Long, resolved: Boolean) {
        viewModelScope.launch {
            db.mistakeDao().setMistakeResolved(id, resolved)
            if (resolved) {
                db.userDao().addXp(gainedXp = 30)
            }
        }
    }

    fun deleteMistake(id: Long) {
        viewModelScope.launch {
            db.mistakeDao().deleteMistake(id)
        }
    }

    fun clearSystemError() {
        lastSystemError.value = null
    }

    override fun onCleared() {
        super.onCleared()
        audioHelper.release()
    }
}
