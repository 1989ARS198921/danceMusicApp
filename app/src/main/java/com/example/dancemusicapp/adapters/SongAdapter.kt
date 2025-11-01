// SongAdapter.kt
package com.example.dancemusicapp.adapters // Убедись, что пакет правильный

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dancemusicapp.R
import com.example.dancemusicapp.Song // Убедись, что импорт правильный

class SongAdapter(
    private var songList: MutableList<Song> = mutableListOf(), // Используем MutableList для возможности изменения
    private val onItemClick: (Song) -> Unit
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    inner class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.title)
        private val artist: TextView = view.findViewById(R.id.artist)

        fun bind(song: Song) {
            title.text = song.title
            artist.text = song.artist
            itemView.setOnClickListener { onItemClick(song) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun getItemCount(): Int = songList.size

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(songList[position])
    }

    // === Добавляем метод updateList ===
    /**
     * Обновляет список песен в адаптере.
     *
     * @param newList Новый список песен для отображения.
     */
    fun updateList(newList: List<Song>) {
        songList.clear()
        songList.addAll(newList)
        notifyDataSetChanged() // Уведомляем адаптер об изменениях.
        // Примечание: notifyDataSetChanged() неэффективен для больших списков.
        // Для лучшей производительности рассмотрите использование ListAdapter с DiffUtil.
    }
    // ================================
}