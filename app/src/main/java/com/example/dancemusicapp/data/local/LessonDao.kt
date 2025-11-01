// LessonDao.kt
package com.example.dancemusicapp.local // <-- Новое место (ранее мог быть в корне)

import androidx.room.*
import com.example.dancemusicapp.local.Lesson // <-- Импорт Entity
import kotlinx.coroutines.flow.Flow // Для реактивных запросов

@Dao
interface LessonDao {
    // Получить все занятия, возвращая Flow для реактивности
    @Query("SELECT * FROM lessons ORDER BY timestamp ASC")
    fun getAllLessons(): Flow<List<Lesson>>

    // Вставить новое занятие
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Обновить, если уже существует
    suspend fun insertLesson(lesson: Lesson)

    // Удалить занятие по ID
    @Delete
    suspend fun deleteLesson(lesson: Lesson) // Можно передать объект, Room возьмёт ID

    // (Опционально) Удалить по ID напрямую
    @Query("DELETE FROM lessons WHERE id = :id")
    suspend fun deleteLessonById(id: Long)

    // (Опционально) Найти занятие по ID
    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun getLessonById(id: Long): Lesson?
}