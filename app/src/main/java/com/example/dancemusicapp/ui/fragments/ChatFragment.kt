// ChatFragment.kt
package com.example.dancemusicapp.ui.fragments

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dancemusicapp.R
import com.example.dancemusicapp.adapters.ChatAdapter
import com.example.dancemusicapp.local.AppDatabase
import com.example.dancemusicapp.repository.LessonRepository
import com.example.dancemusicapp.repository.PlayerRepository
import com.example.dancemusicapp.ui.viewmodels.ChatViewModel
import com.example.dancemusicapp.ui.viewmodels.ChatViewModelFactory
import com.example.dancemusicapp.controllers.LessonController
import kotlinx.coroutines.launch
import java.util.*

class ChatFragment : Fragment(), TextToSpeech.OnInitListener {

    // Используем lazy, чтобы получить зависимости только тогда, когда они нужны
    private val database by lazy { AppDatabase.getDatabase(requireContext()) }
    private val lessonRepository by lazy { LessonRepository(database.lessonDao()) }
    private val playerRepository by lazy { PlayerRepository(requireContext()) /* Передаём Context */ }

    // Создаём LessonController
    private val lessonController by lazy {
        LessonController(requireContext(), lessonRepository)
    }

    // Получаем ViewModel через ViewModelProvider и пользовательскую фабрику
    private val viewModel: ChatViewModel by lazy {
        ViewModelProvider(
            this,
            ChatViewModelFactory.create( // <-- Используем статический метод create из фабрики
                requireActivity().application, // <-- Передаём Application
                lessonRepository,
                playerRepository, // <-- Передаём PlayerRepository (может быть null)
                lessonController // <-- Передаём LessonController
            )
        )[ChatViewModel::class.java]
    }

    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button
    private lateinit var buttonVoiceInput: Button
    private var textToSpeech: TextToSpeech? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewChat)
        editTextMessage = view.findViewById(R.id.editTextMessage)
        buttonSend = view.findViewById(R.id.buttonSend)
        buttonVoiceInput = view.findViewById(R.id.buttonVoiceInput)

        adapter = ChatAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        textToSpeech = TextToSpeech(requireContext(), this)

        buttonSend.setOnClickListener {
            val messageText = editTextMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                viewModel.sendMessage(messageText)
                editTextMessage.text.clear()
            }
        }

        // Наблюдение за состоянием чата из ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.messages) // <-- ListAdapter использует submitList
                    recyclerView.scrollToPosition(adapter.itemCount - 1)

                    // Обработка isLoading, error (если нужно)
                    // if (state.isLoading) { /* Показать индикатор загрузки */ }
                    // if (state.error != null) { /* Показать ошибку */ }
                }
            }
        }

        // Отправляем приветственное сообщение при первом запуске (опционально)
        // viewModel.sendWelcomeMessage()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale("ru", "RU"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("ChatFragment", "This Language is not supported")
            } else {
                Log.i("ChatFragment", "TTS initialized successfully")
                // speakOut("Привет! Я ваш новый голосовой помощник.")
            }
        } else {
            Log.e("ChatFragment", "Initialization Failed!")
        }
    }

    private fun speakOut(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}