// PlayerFragment.kt
package com.example.dancemusicapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider // <-- Добавь этот import
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dancemusicapp.adapters.SongAdapter
import com.example.dancemusicapp.R
import com.example.dancemusicapp.ui.viewmodels.PlayerViewModel
import com.example.dancemusicapp.ui.viewmodels.PlayerViewModelFactory // <-- Добавь этот import
import kotlinx.coroutines.launch

class PlayerFragment : Fragment() {

    // Убираем lazy initialization viewModel здесь, инициализируем в onViewCreated
    private lateinit var viewModel: PlayerViewModel
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
        return inflater.inflate(R.layout.fragment_player, container, false) // Убедись, что layout существует
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

        // --- ИНИЦИАЛИЗАЦИЯ PlayerViewModel ЧЕРЕЗ FACTORY ---
        viewModel = ViewModelProvider(
            this,
            PlayerViewModelFactory.create(
                requireActivity().application // <-- Передаём Application
                // null // <-- Передаём PlayerRepository (если нужно и создано), или PlayerRepository()
            )
        )[PlayerViewModel::class.java]
        // --- КОНЕЦ ИНИЦИАЛИЗАЦИИ ---

        btnPlay.setOnClickListener { viewModel.play() }
        btnPause.setOnClickListener { viewModel.pause() }
        btnStop.setOnClickListener { viewModel.stop() }

        // Наблюдение за состоянием плеера и списком песен из ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Пример: наблюдение за списком песен
                viewModel.songsState.collect { songs ->
                    adapter.updateList(songs) // <-- Теперь вызов updateList работает
                }

                // Пример: наблюдение за состоянием воспроизведения (isPlaying, currentSong и т.д.)
                // viewModel.uiState.collect { state ->
                //     btnPlay.isEnabled = !state.isPlaying
                //     btnPause.isEnabled = state.isPlaying
                //     // Обновить UI текущей песни
                // }
            }
        }
    }
}