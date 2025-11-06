// LessonDao.kt
package com.example.dancemusicapp.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons ORDER BY timestamp ASC")
    fun getAllLessons(): Flow<List<Lesson>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: Lesson)

    @Delete
    suspend fun deleteLesson(lesson: Lesson)

    @Query("DELETE FROM lessons WHERE id = :id")
    suspend fun deleteLessonById(id: Long)

    @Query("SELECT * FROM lessons WHERE id = :id")
    suspend fun getLessonById(id: Long): Lesson?
}