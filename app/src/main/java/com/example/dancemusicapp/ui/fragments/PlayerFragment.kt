// PlayerFragment.kt
package com.example.dancemusicapp.ui.fragments // <-- Новое место

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dancemusicapp.adapters.SongAdapter // <-- Импорт адаптера
import com.example.dancemusicapp.R // <-- Импорт ресурсов
import com.example.dancemusicapp.ui.viewmodels.PlayerViewModel // <-- Импорт ViewModel
import kotlinx.coroutines.launch

class PlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModels()
    private lateinit var adapter: SongAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnPlay: Button
    private lateinit var btnPause: Button
    private lateinit var btnStop: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Замените на имя вашего макета
        return inflater.inflate(R.layout.fragment_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewSongs) // Предполагаемый ID
        btnPlay = view.findViewById(R.id.btnPlay) // Предполагаемый ID
        btnPause = view.findViewById(R.id.btnPause) // Предполагаемый ID
        btnStop = view.findViewById(R.id.btnStop) // Предполагаемый ID

        adapter = SongAdapter { song ->
            // Слушатель нажатия на песню, передаём в ViewModel
            viewModel.playSong(song.path)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false) // Горизонтальный, если нужно
        recyclerView.adapter = adapter

        btnPlay.setOnClickListener { viewModel.play() }
        btnPause.setOnClickListener { viewModel.pause() }
        btnStop.setOnClickListener { viewModel.stop() }

        // Наблюдение за состоянием плеера и списком песен из ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Пример: наблюдение за списком песен
                viewModel.songsState.collect { songs ->
                    // adapter.submitList(songs) // Если ListAdapter
                    adapter.updateList(songs) // Если стандартный Adapter
                }

                // Пример: наблюдение за состоянием воспроизведения (isPlaying, currentSong и т.д.)
                // viewModel.playerState.collect { state ->
                //     btnPlay.isEnabled = !state.isPlaying
                //     btnPause.isEnabled = state.isPlaying
                //     // Обновить UI текущей песни
                // }
            }
        }
    }
}