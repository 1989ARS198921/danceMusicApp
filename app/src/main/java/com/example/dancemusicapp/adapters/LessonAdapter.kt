// LessonAdapter.kt
package com.example.dancemusicapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter // <-- Наследуемся от ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dancemusicapp.R
import com.example.dancemusicapp.local.Lesson
import java.text.SimpleDateFormat
import java.util.*

// Callback для ListAdapter, чтобы он понимал, как сравнивать элементы
class LessonDiffCallback : DiffUtil.ItemCallback<Lesson>() {
    override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
        return oldItem == newItem
    }
}

class LessonAdapter : ListAdapter<Lesson, LessonAdapter.LessonViewHolder>(LessonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson, parent, false)
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.lessonTitle)
        private val date: TextView = itemView.findViewById(R.id.lessonDate)
        private val description: TextView = itemView.findViewById(R.id.lessonDescription)

        fun bind(lesson: Lesson) {
            title.text = lesson.title
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            date.text = dateFormat.format(Date(lesson.timestamp))
            description.text = lesson.description
        }
    }
}