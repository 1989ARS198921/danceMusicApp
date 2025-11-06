// Lesson.kt
package com.example.dancemusicapp.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val timestamp: Long, // Хранение даты как timestamp (Long)
    val durationMinutes: Int
)