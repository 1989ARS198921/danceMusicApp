// ConversationState.kt
package com.example.dancemusicapp

/**
 * Перечисление для отслеживания состояния контекста беседы.
 */
enum class ConversationState {
    IDLE, // Нет активного контекста
    WAITING_FOR_LESSON_DETAILS // Ожидание уточнения деталей занятия (дата, время, длительность)
}