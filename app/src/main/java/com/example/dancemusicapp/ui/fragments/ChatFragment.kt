// ChatFragment.kt
package com.example.dancemusicapp.ui.fragments // <-- Новое место

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dancemusicapp.R
import com.example.dancemusicapp.adapters.ChatAdapter
import com.example.dancemusicapp.ui.viewmodels.ChatViewModel
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    // Получаем ViewModel через viewModels(), предполагая использование Factory или DI для передачи зависимостей
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var adapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button
    // private lateinit var buttonVoiceInput: Button // <-- При необходимости

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Замените на имя вашего макета
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация View
        recyclerView = view.findViewById(R.id.recyclerViewChat) // Предполагаемый ID
        editTextMessage = view.findViewById(R.id.editTextMessage) // Предполагаемый ID
        buttonSend = view.findViewById(R.id.buttonSend) // Предполагаемый ID
        // buttonVoiceInput = view.findViewById(R.id.buttonVoiceInput) // <-- При необходимости

        // Настройка RecyclerView
        adapter = ChatAdapter() // Адаптер может принимать слушатель действий
        recyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            // Для чата часто удобно скроллить вниз
            stackFromEnd = true
        }
        recyclerView.adapter = adapter

        // Обработка нажатия кнопки отправки
        buttonSend.setOnClickListener {
            val messageText = editTextMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                viewModel.sendMessage(messageText) // Передаём сообщение в ViewModel
                editTextMessage.text.clear()
            }
        }

        // Наблюдение за состоянием чата из ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Обновляем список в адаптере
                    // Если используется ListAdapter, вызывается submitList
                    // adapter.submitList(state.messages)
                    // Если используется стандартный Adapter, нужно обновить список и уведомить
                    // Это зависит от реализации ChatAdapter
                    // Пример (если ChatAdapter принимает список через конструктор и не обновляется динамически):
                    // adapter = ChatAdapter(state.messages) // Создание нового адаптера (неэффективно)
                    // recyclerView.adapter = adapter

                    // Более эффективно использовать ListAdapter:
                    adapter.updateList(state.messages) // Вызовите метод в адаптере для обновления
                    // Или, если ListAdapter:
                    // adapter.submitList(state.messages)

                    // Прокрутка вниз при новом сообщении (простой способ)
                    if (state.messages.size > adapter.itemCount) {
                        recyclerView.scrollToPosition(state.messages.size - 1)
                    }
                }
            }
        }

        // (Опционально) Наблюдение за другими состояниями из ViewModel (например, ошибки, загрузка)
        // viewModel.errorState.observe(viewLifecycleOwner) { error ->
        //     // Показать ошибку пользователю
        // }
    }
}