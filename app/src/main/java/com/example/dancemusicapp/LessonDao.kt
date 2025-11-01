// LessonDao.kt
package com.example.dancemusicapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons ORDER BY timestamp ASC") // <-- Сортировка по timestamp
    fun getAllLessons(): Flow<List<Lesson>>

    @Insert
    suspend fun insertLesson(lesson: Lesson)

    @Query("DELETE FROM lessons WHERE id = :id")
    suspend fun deleteLesson(id: Long)
}