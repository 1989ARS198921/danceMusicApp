// ChatMessageItem.kt
package com.example.dancemusicapp // Убедись, что пакет правильный

import java.util.UUID // <-- Добавь этот import для UUID

// Sealed class для разных типов сообщений в чате
sealed class ChatMessageItem(open val id: Long = UUID.randomUUID().mostSignificantBits) {
    data class TextMessage(
        override val id: Long = UUID.randomUUID().mostSignificantBits,
        val text: String,
        val isBot: Boolean,
        val timestamp: Long = System.currentTimeMillis()
    ) : ChatMessageItem(id)

    data class WelcomeMessageItem(
        override val id: Long = UUID.randomUUID().mostSignificantBits,
        val text: String,
        val actions: List<String> // Список текстов для кнопок
    ) : ChatMessageItem(id)

    data class InfoMessage(
        override val id: Long = UUID.randomUUID().mostSignificantBits,
        val text: String,
        val timestamp: Long = System.currentTimeMillis()
    ) : ChatMessageItem(id)

    data class ErrorMessage(
        override val id: Long = UUID.randomUUID().mostSignificantBits,
        val text: String,
        val timestamp: Long = System.currentTimeMillis()
    ) : ChatMessageItem(id)

    // Можно добавить другие типы: BotActionMessage, UserActionMessage и т.д.
}