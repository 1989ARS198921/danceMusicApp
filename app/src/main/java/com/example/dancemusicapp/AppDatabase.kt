// app/src/main/java/com/example/dancemusicapp/AppDatabase.kt
package com.example.dancemusicapp

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Lesson::class], version = 2) // Убедимся, что Lesson::class указан
abstract class AppDatabase : RoomDatabase() {
    abstract fun lessonDao(): LessonDao // Убедимся, что DAO указан

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // <-- Добавь эту строку
                    .build() // <-- Теперь .build()
                INSTANCE = instance
                instance
            }
        }
    }
}