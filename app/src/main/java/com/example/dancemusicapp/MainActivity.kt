package com.example.dancemusicapp

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // --- Зависимости ---
    private lateinit var database: AppDatabase

    // --- Плеер ---
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var recyclerViewSongs: RecyclerView
    private lateinit var songAdapter: SongAdapter
    private val songList = mutableListOf<Song>()

    // --- Занятия ---
    private lateinit var recyclerViewLessons: RecyclerView
    private lateinit var lessonAdapter: LessonAdapter
    private val lessonList = mutableListOf<Lesson>()
    private lateinit var btnAddLesson: Button

    // --- Чат ---
    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<ChatMessage>()
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button
    private lateinit var buttonVoiceInput: Button
    private var speechRecognizer: android.speech.SpeechRecognizer? = null

    // --- Делегаты ---
    private lateinit var playerController: PlayerController
    private lateinit var lessonController: LessonController
    private lateinit var chatController: ChatController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- Инициализация БД ---
        database = AppDatabase.getDatabase(this)

        // --- Инициализация RecyclerView (после setContentView) ---
        recyclerViewSongs = findViewById(R.id.recyclerViewSongs)
        recyclerViewLessons = findViewById(R.id.recyclerViewLessons)
        recyclerViewChat = findViewById(R.id.recyclerViewChat)

        // --- Создание адаптеров (после RecyclerView) ---
        songAdapter = SongAdapter(songList) { song -> playerController.playMusic(song.path) }
        lessonAdapter = LessonAdapter(lessonList)
        chatAdapter = ChatAdapter(chatList)

        // --- Инициализация делегатов (после адаптеров и RecyclerView) ---
        playerController = PlayerController(this, songList, ::playMusic, ::pauseMusic, ::stopMusic)
        lessonController = LessonController(this, lessonList, database)
        // chatController получает уже инициализированные компоненты
        chatController = ChatController(this, chatList, chatAdapter, recyclerViewChat, lessonController, playerController)

        // --- Инициализация UI ---
        initializeUI()

        // --- Загрузка начальных данных ---
        lessonController.loadLessons()
    }

    private fun initializeUI() {
        // --- Настройка UI для ПЛЕЕРА ---
        recyclerViewSongs.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewSongs.adapter = songAdapter
        playerController.initializeUI(recyclerViewSongs, songAdapter, this)

        // --- Настройка UI для ЗАНЯТИЙ ---
        recyclerViewLessons.layoutManager = LinearLayoutManager(this)
        recyclerViewLessons.adapter = lessonAdapter
        lessonController.setUIComponents(recyclerViewLessons, lessonAdapter)

        // --- Настройка UI для ЧАТА ---
        recyclerViewChat.layoutManager = LinearLayoutManager(this)
        recyclerViewChat.adapter = chatAdapter
        // Передаём View в ChatController для инициализации
        chatController.initializeUI(
            findViewById(R.id.editTextMessage),
            findViewById(R.id.buttonSend),
            findViewById(R.id.buttonVoiceInput)
        )

        // --- Инициализация кнопок ---
        val btnPlay = findViewById<Button>(R.id.btnPlay)
        val btnPause = findViewById<Button>(R.id.btnPause)
        val btnStop = findViewById<Button>(R.id.btnStop)
        btnAddLesson = findViewById(R.id.btnAddLesson)
        buttonVoiceInput = findViewById(R.id.buttonVoiceInput)

        // --- Добавляем слушатели событий ---
        btnPlay.setOnClickListener { playerController.playMusic() }
        btnPause.setOnClickListener { playerController.pauseMusic() }
        btnStop.setOnClickListener { playerController.stopMusic() }

        btnAddLesson.setOnClickListener {
            lessonController.addLessonToDatabase(
                Lesson(
                    title = "Танцы",
                    description = "Индивидуальное занятие по современным танцам",
                    timestamp = System.currentTimeMillis(), // Временная заглушка
                    durationMinutes = 60
                )
            )
        }
    }

    // --- Методы для делегатов ---
    private fun playMusic(path: String = songList[0].path) {
        playerController.playMusic(path)
    }

    private fun pauseMusic() {
        playerController.pauseMusic()
    }

    private fun stopMusic() {
        playerController.stopMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerController.stopMusic()
    }
}

// --- Класс-делегат для работы с плеером ---
class PlayerController(
    private val activity: MainActivity,
    private val songList: MutableList<Song>,
    private val onPlay: (String) -> Unit,
    private val onPause: () -> Unit,
    private val onStop: () -> Unit
) {
    private var mediaPlayer: MediaPlayer? = null

    fun initializeUI(recyclerView: RecyclerView, adapter: SongAdapter, context: MainActivity) {
        // Логика инициализации UI плеера, если нужно
        // Например, добавление песен в songList
        songList.add(
            Song(
                1,
                "Test Song",
                "Test Artist",
                Uri.parse("android.resource://${context.packageName}/${R.raw.sample}").toString()
            )
        )
        adapter.notifyDataSetChanged()
    }

    fun playMusic(path: String = songList[0].path) {
        if (mediaPlayer == null) {
            val uri = Uri.parse(path)
            mediaPlayer = MediaPlayer.create(activity, uri)
            mediaPlayer?.start()
        }
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun hasSongs(): Boolean {
        return songList.isNotEmpty()
    }
}

// --- Класс-делегат для работы с чатом ---
class ChatController(
    private val activity: MainActivity,
    private val chatList: MutableList<ChatMessage>,
    private val chatAdapter: ChatAdapter,
    private val recyclerViewChat: RecyclerView, // <-- Принимаем RecyclerView
    private val lessonController: LessonController,
    private val playerController: PlayerController
) {
    private var speechRecognizer: android.speech.SpeechRecognizer? = null

    // Принимаем EditText и Buttons, но RecyclerView уже передан в конструкторе
    fun initializeUI(editTextMessage: EditText, buttonSend: Button, buttonVoiceInput: Button) {
        // Инициализация UI чата
        speechRecognizer = android.speech.SpeechRecognizer.createSpeechRecognizer(activity)
        val speechRecognizerIntent = android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Скажите команду")
        }

        val listener = object : android.speech.RecognitionListener {
            override fun onReadyForSpeech(params: android.os.Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                var errorMessage = "Ошибка распознавания: $error"
                when (error) {
                    android.speech.SpeechRecognizer.ERROR_AUDIO -> errorMessage = "Проблема с аудио"
                    android.speech.SpeechRecognizer.ERROR_CLIENT -> errorMessage = "Ошибка клиента"
                    android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> errorMessage = "Нет разрешения на микрофон"
                    android.speech.SpeechRecognizer.ERROR_NETWORK -> errorMessage = "Сетевая ошибка"
                    android.speech.SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> errorMessage = "Таймаут сети"
                    android.speech.SpeechRecognizer.ERROR_NO_MATCH -> errorMessage = "Не удалось распознать речь"
                    android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> errorMessage = "Сервис занят"
                    android.speech.SpeechRecognizer.ERROR_SERVER -> errorMessage = "Ошибка сервера"
                    android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> errorMessage = "Таймаут речи"
                }
                sendMessageToChat(errorMessage, isBot = true)
            }
            override fun onResults(results: android.os.Bundle?) {
                val matches = results?.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spokenText = matches[0]
                    sendMessageToChat(spokenText, isBot = false)
                    processUserMessage(spokenText)
                }
            }
            override fun onPartialResults(partialResults: android.os.Bundle?) {}
            override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
        }

        speechRecognizer?.setRecognitionListener(listener)

        buttonSend.setOnClickListener {
            val messageText = editTextMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessageToChat(messageText, isBot = false)
                editTextMessage.text.clear()
                processUserMessage(messageText)
            }
        }

        buttonVoiceInput.setOnClickListener {
            if (activity.checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                speechRecognizer?.startListening(speechRecognizerIntent)
            } else {
                activity.requestPermissions(arrayOf(android.Manifest.permission.RECORD_AUDIO), 1001)
            }
        }
    }

    private fun sendMessageToChat(message: String, isBot: Boolean) {
        chatList.add(ChatMessage(message, isBot))
        chatAdapter.notifyItemInserted(chatList.size - 1)
        recyclerViewChat.scrollToPosition(chatList.size - 1) // <-- Используем recyclerViewChat из конструктора
    }

    private fun processUserMessage(message: String) {
        val lowerMessage = message.lowercase()
        var botResponse = "Простите, я не понимаю. Попробуйте: 'Запиши меня на танцы в субботу в 15:00' или 'Включи музыку для танцев'."
        // ... (остальной код обработки сообщений) ...
        if (lowerMessage.contains("танцы") && lowerMessage.contains("запиши")) {
            val datePattern = Regex("""(\d{1,2})[./](\d{1,2})(?:[./](\d{2,4}))?""")
            val timePattern = Regex("""(\d{1,2})[:.](\d{2})""")

            val dateMatch = datePattern.find(message)
            val timeMatch = timePattern.find(message)

            var timestamp: Long? = null
            var durationMinutes = 60

            if (dateMatch != null && timeMatch != null) {
                val (day, month, yearStr) = dateMatch.destructured
                val (hour, minute) = timeMatch.destructured

                val year = yearStr.toIntOrNull() ?: 2025
                val monthInt = month.toIntOrNull()?.minus(1) ?: 0
                val dayInt = day.toIntOrNull() ?: 1
                val hourInt = hour.toIntOrNull() ?: 0
                val minuteInt = minute.toIntOrNull() ?: 0

                if (year in 2024..2030 && monthInt in 0..11 && dayInt in 1..31 && hourInt in 0..23 && minuteInt in 0..59) {
                    val calendar = Calendar.getInstance()
                    calendar.set(year, monthInt, dayInt, hourInt, minuteInt, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    timestamp = calendar.timeInMillis

                    if (lowerMessage.contains("полтора часа")) durationMinutes = 90
                    else if (lowerMessage.contains("два часа")) durationMinutes = 120
                    else if (lowerMessage.contains("три часа")) durationMinutes = 180
                    else if (lowerMessage.contains("час")) durationMinutes = 60
                }
            }

            if (timestamp != null) {
                val newLesson = Lesson(
                    title = "Танцы",
                    description = "Индивидуальное занятие по современным танцам",
                    timestamp = timestamp,
                    durationMinutes = durationMinutes
                )
                lessonController.addLessonToDatabase(newLesson)
                botResponse = "Вы записаны на занятие по танцам на $durationMinutes минут в ${SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(java.util.Date(timestamp))}!"
            } else {
                botResponse = "Уточните дату и время. Пожалуйста, выберите в интерфейсе."
                lessonController.showDateTimePickerForLesson("Танцы", "Индивидуальное занятие по современным танцам")
            }
        } else if (lowerMessage.contains("музыку") || lowerMessage.contains("включи")) {
            if (playerController.hasSongs()) {
                playerController.playMusic()
                botResponse = "Включаю музыку для танцев!"
            } else {
                botResponse = "Нет доступных песен."
            }
        }

        sendMessageToChat(botResponse, isBot = true)
    }
}