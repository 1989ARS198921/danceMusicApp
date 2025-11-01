// LessonsFragment.kt
package com.example.dancemusicapp.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dancemusicapp.R
import com.example.dancemusicapp.ViewModelFactory // <-- Новый импорт
import com.example.dancemusicapp.adapters.LessonAdapter
import com.example.dancemusicapp.local.AppDatabase // <-- Новый импорт
import com.example.dancemusicapp.local.Lesson // <-- Новый импорт
import com.example.dancemusicapp.repository.LessonRepository // <-- Новый импорт
import com.example.dancemusicapp.ui.viewmodels.LessonsViewModel // <-- Новый импорт
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
    private lateinit var fabAddLesson: FloatingActionButton

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
        fabAddLesson = view.findViewById(R.id.fabAddLesson)

        adapter = LessonAdapter()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnAddLesson.setOnClickListener {
            viewModel.addTestLesson()
        }

        fabAddLesson.setOnClickListener {
            showAddLessonDialog()
        }

        // Наблюдение за UI-состоянием
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.updateList(state.lessons)
                    // Обработка isLoading, error
                    if (state.error != null) {
                        Toast.makeText(requireContext(), "Ошибка: ${state.error}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun showAddLessonDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Записаться на занятие")

        val dialogView = layoutInflater.inflate(R.layout.dialog_add_lesson, null)
        val editTextTitle: EditText = dialogView.findViewById(R.id.editTextLessonTitle)
        val editTextDescription: EditText = dialogView.findViewById(R.id.editTextLessonDescription)
        val editTextDate: EditText = dialogView.findViewById(R.id.editTextLessonDate)
        val editTextTime: EditText = dialogView.findViewById(R.id.editTextLessonTime)
        val editTextDuration: EditText = dialogView.findViewById(R.id.editTextLessonDuration)

        val now = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        editTextDate.setText(dateFormat.format(now.time))
        editTextTime.setText(timeFormat.format(now.time))
        editTextDuration.setText("60")

        builder.setView(dialogView)

        builder.setPositiveButton("Добавить") { _, _ ->
            val title = editTextTitle.text.toString().trim()
            val description = editTextDescription.text.toString().trim()
            val dateStr = editTextDate.text.toString().trim()
            val timeStr = editTextTime.text.toString().trim()
            val durationStr = editTextDuration.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || dateStr.isEmpty() || timeStr.isEmpty() || durationStr.isEmpty()) {
                Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            val fullDateTimeStr = "$dateStr $timeStr"
            val fullDateTimeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            val timestamp: Long
            val durationMinutes: Int
            try {
                val parsedDate = fullDateTimeFormat.parse(fullDateTimeStr)
                timestamp = parsedDate?.time ?: System.currentTimeMillis()
                durationMinutes = durationStr.toIntOrNull() ?: 60
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка в формате даты/времени или длительности", Toast.LENGTH_LONG).show()
                return@setPositiveButton
            }

            val newLesson = Lesson(
                // id = 0, // ID будет сгенерирован Room
                title = title,
                description = description,
                timestamp = timestamp,
                durationMinutes = durationMinutes
            )
            viewModel.addLesson(newLesson) // <-- Теперь вызываем метод ViewModel
            // adapter.addItem(newLesson) // <-- Убираем прямой вызов адаптера
        }

        builder.setNegativeButton("Отмена", null)

        val dialog = builder.create()
        dialog.show()
    }
}