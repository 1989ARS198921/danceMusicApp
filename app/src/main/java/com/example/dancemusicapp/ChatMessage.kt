package com.example.dancemusicapp

data class ChatMessage(
    val text: String,
    val isBot: Boolean // true если сообщение от бота, false если от пользователя
)