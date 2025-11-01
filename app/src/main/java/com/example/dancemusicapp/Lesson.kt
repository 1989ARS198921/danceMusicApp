// Lesson.kt
package com.example.dancemusicapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class Lesson(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val timestamp: Long, // <-- Стал Long (timestamp)
    val durationMinutes: Int // <-- Также переименовали
)