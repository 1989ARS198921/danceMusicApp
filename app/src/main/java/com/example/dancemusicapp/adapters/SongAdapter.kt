// SongAdapter.kt
package com.example.dancemusicapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter // <-- Наследуемся от ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dancemusicapp.R
import com.example.dancemusicapp.Song

// Callback для ListAdapter, чтобы он понимал, как сравнивать элементы
class SongDiffCallback : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem == newItem
    }
}

class SongAdapter(
    private val onItemClick: (Song) -> Unit
) : ListAdapter<Song, SongAdapter.SongViewHolder>(SongDiffCallback()) {

    // Метод для обновления списка (для ListAdapter используется submitList)
    fun updateList(newList: List<Song>) {
        submitList(newList) // ListAdapter использует submitList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val artist: TextView = itemView.findViewById(R.id.artist)

        fun bind(song: Song, onItemClick: (Song) -> Unit) {
            title.text = song.title
            artist.text = song.artist
            itemView.setOnClickListener { onItemClick(song) }
        }
    }
}