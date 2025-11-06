// ChatAdapter.kt
package com.example.dancemusicapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter // <-- Используем ListAdapter для эффективности
import androidx.recyclerview.widget.RecyclerView
import com.example.dancemusicapp.ChatMessageItem
import com.example.dancemusicapp.R
import java.text.SimpleDateFormat
import java.util.*

// DiffUtil.Callback для ListAdapter
class ChatMessageDiffCallback : DiffUtil.ItemCallback<ChatMessageItem>() {
    override fun areItemsTheSame(oldItem: ChatMessageItem, newItem: ChatMessageItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChatMessageItem, newItem: ChatMessageItem): Boolean {
        return oldItem == newItem
    }
}

class ChatAdapter : ListAdapter<ChatMessageItem, RecyclerView.ViewHolder>(ChatMessageDiffCallback()) {

    companion object {
        private const val TYPE_TEXT_MESSAGE = 1
        private const val TYPE_INFO_MESSAGE = 2
        private const val TYPE_ERROR_MESSAGE = 3
        private const val TYPE_WELCOME_MESSAGE = 4 // <-- Добавлен тип для приветственного сообщения
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChatMessageItem.TextMessage -> TYPE_TEXT_MESSAGE
            is ChatMessageItem.InfoMessage -> TYPE_INFO_MESSAGE
            is ChatMessageItem.ErrorMessage -> TYPE_ERROR_MESSAGE
            is ChatMessageItem.WelcomeMessageItem -> TYPE_WELCOME_MESSAGE // <-- Добавлено
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_TEXT_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_text_message, parent, false) // Убедись, что layout существует
                TextMessageViewHolder(view)
            }
            TYPE_INFO_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_info_message, parent, false) // Убедись, что layout существует
                InfoMessageViewHolder(view)
            }
            TYPE_ERROR_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_error_message, parent, false) // Убедись, что layout существует
                ErrorMessageViewHolder(view)
            }
            TYPE_WELCOME_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_chat_welcome_message, parent, false) // Убедись, что layout существует
                WelcomeMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TextMessageViewHolder -> holder.bind(getItem(position) as ChatMessageItem.TextMessage)
            is InfoMessageViewHolder -> holder.bind(getItem(position) as ChatMessageItem.InfoMessage)
            is ErrorMessageViewHolder -> holder.bind(getItem(position) as ChatMessageItem.ErrorMessage)
            is WelcomeMessageViewHolder -> holder.bind(getItem(position) as ChatMessageItem.WelcomeMessageItem) // <-- Добавлено
        }
    }

    // ViewHolder для текстовых сообщений
    class TextMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.messageText) // Убедись, что ID правильный
        private val messageSender: TextView = itemView.findViewById(R.id.messageSender) // Убедись, что ID правильный
        private val messageTime: TextView = itemView.findViewById(R.id.messageTime) // Убедись, что ID правильный

        fun bind(message: ChatMessageItem.TextMessage) {
            messageText.text = message.text
            messageSender.text = if (message.isBot) "Бот" else "Вы"
            messageTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
        }
    }

    // ViewHolder для информационных сообщений
    class InfoMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val infoMessageText: TextView = itemView.findViewById(R.id.infoMessageText) // Убедись, что ID правильный
        private val infoMessageTime: TextView = itemView.findViewById(R.id.infoMessageTime) // Убедись, что ID правильный

        fun bind(message: ChatMessageItem.InfoMessage) {
            infoMessageText.text = message.text
            infoMessageTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
        }
    }

    // ViewHolder для сообщений об ошибках
    class ErrorMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val errorMessageText: TextView = itemView.findViewById(R.id.errorMessageText) // Убедись, что ID правильный
        private val errorMessageTime: TextView = itemView.findViewById(R.id.errorMessageTime) // Убедись, что ID правильный

        fun bind(message: ChatMessageItem.ErrorMessage) {
            errorMessageText.text = message.text
            errorMessageTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
        }
    }

    // ViewHolder для приветственных сообщений
    class WelcomeMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val welcomeText: TextView = itemView.findViewById(R.id.welcomeText) // Убедись, что ID правильный
        private val actionsContainer: ViewGroup = itemView.findViewById(R.id.actionsContainer) // Убедись, что ID правильный

        fun bind(message: ChatMessageItem.WelcomeMessageItem) {
            welcomeText.text = message.text

            // Очищаем предыдущие кнопки
            actionsContainer.removeAllViews()

            // Добавляем кнопки действий
            message.actions.forEach { actionText ->
                val button = android.widget.Button(actionsContainer.context).apply {
                    this.text = actionText
                    // Можно добавить стиль, например, style="?attr/borderlessButtonStyle"
                    // Установи слушатель, если нужно
                    // setOnClickListener { /* Обработка нажатия кнопки */ }
                }
                actionsContainer.addView(button)
            }
        }
    }
}