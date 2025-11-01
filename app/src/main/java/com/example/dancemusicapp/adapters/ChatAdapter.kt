// ChatAdapter.kt
package com.example.dancemusicapp.adapters // <-- Новое место

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dancemusicapp.ChatMessage // <-- Импорт модели
import com.example.dancemusicapp.R // <-- Импорт ресурсов

// Использование стандартного RecyclerView.Adapter (можно заменить на ListAdapter)
class ChatAdapter(
    private val chatList: List<ChatMessage> = emptyList() // Можно передать извне или обновлять через метод
    // Если нужно реагировать на действия пользователя (например, долгое нажатие), передайте слушатель
    // private val onMessageLongClick: (ChatMessage) -> Unit = {}
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    // Если список обновляется, нужно уведомить адаптер
    fun updateList(newList: List<ChatMessage>) {
        // Если используется ListAdapter, вызывается submitList
        // this.submitList(newList)
        // Если используется стандартный Adapter, можно просто обновить список и уведомить
        // this.chatList = newList // НЕЛЬЗЯ, так как val и immutable
        // Лучше создать новый экземпляр адаптера или передавать изменяемый список (не рекомендуется)
        // Или использовать ListAdapter.
    }

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageText: TextView = view.findViewById(R.id.messageText)
        private val messageSender: TextView = view.findViewById(R.id.messageSender)

        fun bind(message: ChatMessage) {
            messageText.text = message.text
            messageSender.text = if (message.isBot) "Бот" else "Вы"
            // Пример: itemView.setOnLongClickListener { onMessageLongClick(message); true }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun getItemCount(): Int = chatList.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatList[position])
    }
}