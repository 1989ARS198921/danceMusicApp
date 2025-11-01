package com.example.dancemusicapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// --- Класс-делегат для работы с занятиями ---
class LessonController(
    private val context: Context, // Изменим на Context, если не нужен полный доступ к MainActivity
    private val lessonList: MutableList<Lesson>,
    private val database: AppDatabase
    // Убраны: private val lessonAdapter: LessonAdapter, private val recyclerViewLessons: RecyclerView
) {
    // Добавим переменные для хранения UI компонентов
    private var lessonAdapter: LessonAdapter? = null
    private var recyclerViewLessons: RecyclerView? = null

    // Новый метод для установки UI компонентов из MainActivity
    fun setUIComponents(recyclerView: RecyclerView, adapter: LessonAdapter) {
        this.recyclerViewLessons = recyclerView
        this.lessonAdapter = adapter
    }

    fun loadLessons() {
        CoroutineScope(Dispatchers.IO).launch {
            val lessons = database.lessonDao().getAllLessons().first()
            lessonList.clear()
            lessonList.addAll(lessons)
            if (context is AppCompatActivity) {
                context.runOnUiThread {
                    // Обновляем UI через адаптер, если он инициализирован
                    lessonAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    fun addLessonToDatabase(lesson: Lesson) {
        CoroutineScope(Dispatchers.IO).launch {
            database.lessonDao().insertLesson(lesson)
            loadLessons() // Обновляем список после добавления
        }
    }

    fun showDateTimePickerForLesson(title: String, description: String) {
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)

                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)

                        showDurationPicker { duration ->
                            val timestamp = calendar.timeInMillis
                            val newLesson = Lesson(
                                title = title,
                                description = description,
                                timestamp = timestamp,
                                durationMinutes = duration
                            )
                            addLessonToDatabase(newLesson)
                            // Отправить сообщение в чат, если доступен ChatController
                            // chatController?.sendMessageToChat("Вы записаны на занятие...", true) // Предполагаем наличие ссылки
                        }
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun showDurationPicker(onDurationSelected: (Int) -> Unit) {
        val durations = arrayOf("1 час (60 мин)", "1.5 часа (90 мин)", "2 часа (120 мин)", "3 часа (180 мин)")
        val durationValues = intArrayOf(60, 90, 120, 180)

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Выберите длительность занятия")

        builder.setItems(durations) { _, which ->
            onDurationSelected(durationValues[which])
        }

        builder.show()
    }
}