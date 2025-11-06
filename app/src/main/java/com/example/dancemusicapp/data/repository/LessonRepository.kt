// LessonRepository.kt
package com.example.dancemusicapp.repository

import com.example.dancemusicapp.local.Lesson
import com.example.dancemusicapp.local.LessonDao
import kotlinx.coroutines.flow.Flow

class LessonRepository(
    private val lessonDao: LessonDao
) {
    fun getAllLessons(): Flow<List<Lesson>> = lessonDao.getAllLessons()
    suspend fun insertLesson(lesson: Lesson) = lessonDao.insertLesson(lesson)
    suspend fun deleteLesson(lesson: Lesson) = lessonDao.deleteLesson(lesson)
    suspend fun deleteLessonById(id: Long) = lessonDao.deleteLessonById(id)
    suspend fun getLessonById(id: Long): Lesson? = lessonDao.getLessonById(id)
}