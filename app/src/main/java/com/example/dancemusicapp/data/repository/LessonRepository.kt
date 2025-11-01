// LessonRepository.kt
package com.example.dancemusicapp.repository // <-- Новое место

import com.example.dancemusicapp.local.LessonDao // <-- Импорт DAO
import com.example.dancemusicapp.local.Lesson // <-- Импорт Entity
import kotlinx.coroutines.flow.Flow

class LessonRepository(
    private val lessonDao: LessonDao // Зависимость от DAO
) {
    // Получить все занятия как Flow
    fun getAllLessons(): Flow<List<Lesson>> = lessonDao.getAllLessons()

    // Вставить занятие
    suspend fun insertLesson(lesson: Lesson) = lessonDao.insertLesson(lesson)

    // Удалить занятие
    suspend fun deleteLesson(lesson: Lesson) = lessonDao.deleteLesson(lesson)

    // (Опционально) Удалить по ID
    suspend fun deleteLessonById(id: Long) = lessonDao.deleteLessonById(id)

    // (Опционально) Получить занятие по ID
    suspend fun getLessonById(id: Long): Lesson? = lessonDao.getLessonById(id)
}