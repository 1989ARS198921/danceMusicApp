// AppDatabase.kt
package com.example.dancemusicapp.local // <-- Новое место (ранее мог быть в корне)

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Миграция (пример: с версии 1 на 2)
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Пример: добавить новый столбец
        // database.execSQL("ALTER TABLE lessons ADD COLUMN new_column TEXT DEFAULT '' NOT NULL")
    }
}

@Database(
    entities = [Lesson::class], // Указываем сущности
    version = 2,                // Увеличиваем версию
    exportSchema = false        // Удобно для разработки, но в продакшене лучше true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lessonDao(): LessonDao // Метод для получения DAO

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
                    .addMigrations(MIGRATION_1_2) // Добавляем миграцию
                    // .fallbackToDestructiveMigration() // <-- Можно убрать, если добавили миграцию
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}