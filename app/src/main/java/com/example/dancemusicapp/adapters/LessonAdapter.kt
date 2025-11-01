// LessonAdapter.kt
package com.example.dancemusicapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dancemusicapp.R
import com.example.dancemusicapp.local.Lesson // Убедись, что импорт правильный

class LessonAdapter(
    private var lessonList: MutableList<Lesson> = mutableListOf() // Используем MutableList
) : RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    // Метод для обновления всего списка
    fun updateList(newList: List<Lesson>) {
        lessonList.clear()
        lessonList.addAll(newList)
        notifyDataSetChanged()
    }

    // === Добавь этот метод ===
    /**
     * Добавляет одно занятие в конец списка.
     *
     * @param lesson Новое занятие для добавления.
     */
    fun addItem(lesson: Lesson) {
        val position = lessonList.size
        lessonList.add(lesson)
        notifyItemInserted(position)
    }
    // =========================

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson, parent, false)
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.bind(lessonList[position])
    }

    override fun getItemCount(): Int = lessonList.size

    class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.lessonTitle)
        private val date: TextView = itemView.findViewById(R.id.lessonDate)
        private val description: TextView = itemView.findViewById(R.id.lessonDescription)

        fun bind(lesson: Lesson) {
            title.text = lesson.title
            // Форматирование даты
            date.text = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date(lesson.timestamp))
            description.text = lesson.description
        }
    }
}