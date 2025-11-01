package com.example.dancemusicapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class LessonAdapter(
    private val lessonList: List<Lesson>
) : RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    class LessonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.lessonTitle)
        private val date: TextView = view.findViewById(R.id.lessonDate) // Используем старое имя id из item_lesson.xml
        private val description: TextView = view.findViewById(R.id.lessonDescription)

        fun bind(lesson: Lesson) {
            title.text = lesson.title
            // Преобразуем timestamp в строку формата "dd.MM.yyyy HH:mm"
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            date.text = dateFormat.format(Date(lesson.timestamp)) // <-- Вот тут используем timestamp
            description.text = lesson.description
            // Опционально: можно добавить отображение durationMinutes
            // description.text = "${lesson.description} (Длительность: ${lesson.durationMinutes} мин)"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson, parent, false)
        return LessonViewHolder(view)
    }

    override fun getItemCount(): Int = lessonList.size

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.bind(lessonList[position])
    }
}