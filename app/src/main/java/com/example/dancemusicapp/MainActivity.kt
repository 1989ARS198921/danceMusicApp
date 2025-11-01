// MainActivity.kt
package com.example.dancemusicapp // <-- Пакет остаётся прежним

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit // Импорт для FragmentManager.commit
import androidx.fragment.app.replace
import com.example.dancemusicapp.local.AppDatabase // <-- Импорт AppDatabase
import com.example.dancemusicapp.repository.LessonRepository // <-- Импорт Repository
import com.example.dancemusicapp.ui.fragments.LessonsFragment // <-- Импорт фрагмента

// Если используется Dependency Injection (например, Hilt), база и репозитории
// будут внедряться автоматически. Пока что инициализируем вручную.
class MainActivity : AppCompatActivity() {

    // Зависимости (в идеале - через DI)
    private lateinit var database: AppDatabase
    private lateinit var lessonRepository: LessonRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Используем новый layout с FragmentContainerView
        setContentView(R.layout.activity_main_with_fragments)

        // Инициализация базы данных и репозитория
        database = AppDatabase.getDatabase(this)
        lessonRepository = LessonRepository(database.lessonDao()) // Создаём Repository

        // Пример: добавляем LessonsFragment в контейнер при запуске
        // Лучше использовать Navigation Component для сложной навигации
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true) // Позволяет оптимизировать операции
                // Заменяем содержимое контейнера (FrameLayout с id fragment_container)
                // на новый экземпляр LessonsFragment
                replace<LessonsFragment>(R.id.fragment_container)
            }
        }
    }

    // override fun onDestroy() {
    //     // Не нужно закрывать базу данных вручную при использовании Singleton
    //     // INSTANCE в AppDatabase закроется сама при уничтожении процесса
    //     super.onDestroy()
    // }
}