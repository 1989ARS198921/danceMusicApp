// LessonAdapter.kt
package com.example.dancemusicapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dancemusicapp.local.Lesson
import com.example.dancemusicapp.R // Убедись, что импорт ресурсов правильный

class LessonAdapter(
    private var lessonList: MutableList<Lesson> = mutableListOf() // Используем MutableList
    // ... другие параметры, например, слушатель
) : RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    // Метод для обновления списка
    fun updateList(newList: List<Lesson>) {
        lessonList.clear()
        lessonList.addAll(newList)
        notifyDataSetChanged() // Неэффективно для больших списков, лучше использовать ListAdapter
    }

    // ... (остальной код: onCreateViewHolder, onBindViewHolder, getItemCount, LessonViewHolder)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson, parent, false) // Убедись, что item_lesson.xml существует
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.bind(lessonList[position]) // ... или передай слушатель
    }

    override fun getItemCount(): Int = lessonList.size

    class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.lessonTitle) // Убедись, что ID правильный
        private val date: TextView = itemView.findViewById(R.id.lessonDate)
        private val description: TextView = itemView.findViewById(R.id.lessonDescription)

        fun bind(lesson: Lesson) {
            title.text = lesson.title
            // ... форматирование даты и установка в date.text
            date.text = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(lesson.timestamp))
            description.text = lesson.description
        }
    }
}