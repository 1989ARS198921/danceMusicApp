// LessonsFragment.kt
package com.example.dancemusicapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dancemusicapp.local.Lesson// Убедись, что импорт правильный
import com.example.dancemusicapp.R
import com.example.dancemusicapp.ViewModelFactory // <-- Новый импорт
import com.example.dancemusicapp.adapters.LessonAdapter // <-- Новый импорт
import com.example.dancemusicapp.local.AppDatabase // <-- Новый импорт
import com.example.dancemusicapp.repository.LessonRepository // <-- Новый импорт
import com.example.dancemusicapp.ui.viewmodels.LessonsViewModel // <-- Новый импорт
import kotlinx.coroutines.launch

class LessonsFragment : Fragment() {

    // Используем lazy, чтобы получить зависимости только тогда, когда они нужны
    private val database by lazy { AppDatabase.getDatabase(requireContext()) }
    private val lessonRepository by lazy { LessonRepository(database.lessonDao()) }

    // Получаем ViewModel через пользовательскую фабрику
    private val viewModel: LessonsViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelFactory.forLessonsViewModel(lessonRepository)
        )[LessonsViewModel::class.java]
    }

    private lateinit var adapter: LessonAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddLesson: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_lessons, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewLessons)
        btnAddLesson = view.findViewById(R.id.btnAddLesson)

        adapter = LessonAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnAddLesson.setOnClickListener {
            // Пример добавления тестовой записи
            viewModel.addTestLesson()
        }

        // Наблюдение за UI-состоянием
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.updateList(state.lessons)
                    // Обработка isLoading, error
                }
            }
        }
    }
}