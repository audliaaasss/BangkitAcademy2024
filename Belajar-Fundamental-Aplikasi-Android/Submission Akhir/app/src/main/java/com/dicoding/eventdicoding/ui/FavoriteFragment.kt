package com.dicoding.eventdicoding.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.eventdicoding.data.database.FavoriteEventDatabase
import com.dicoding.eventdicoding.data.repository.FavoriteEventRepository
import com.dicoding.eventdicoding.data.response.ListEventsItem
import com.dicoding.eventdicoding.databinding.FragmentFavoriteBinding
import com.dicoding.eventdicoding.ui.detail.DetailEventActivity

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavoriteEventViewModel
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoading(true)

        val database = FavoriteEventDatabase.getDatabase(requireContext())
        val favoriteEventDao = database.favoriteEventDao()
        val repository = FavoriteEventRepository(favoriteEventDao)
        val factory = FavoriteEventViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(FavoriteEventViewModel::class.java)

        setupRecyclerView()
        observeFavoriteEvents()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvEvents.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireActivity(), layoutManager.orientation)
        binding.rvEvents.addItemDecoration(itemDecoration)

        eventAdapter = EventAdapter { selectedEvent ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java).apply {
                putExtra("EVENT_ID", selectedEvent.id)
            }
            startActivity(intent)
        }
        binding.rvEvents.adapter = eventAdapter
    }

    private fun observeFavoriteEvents() {
        viewModel.favoriteEvents.observe(viewLifecycleOwner) { events ->
            showLoading(false)
            val items = arrayListOf<ListEventsItem>()
            events.map {
                Log.d("FavoriteFragment", "Image URL: ${it.mediaCover}")
                val item = ListEventsItem(
                    id = it.id, // Ensure this conversion is valid
                    name = it.name,
                    imageLogo = it.mediaCover
                )
                items.add(item)
            }
            eventAdapter.submitList(items)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}