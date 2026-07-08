package com.example.stashit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashit.R
import com.example.stashit.adapter.EventsCompletedAdapter
import com.example.stashit.data.EventCompleted
import com.example.stashit.databinding.FragmentEventsCompletedBinding
import com.example.stashit.repository.FirestoreRepository
import kotlinx.coroutines.launch
import android.content.Intent
import com.example.stashit.NotificationUiActivity

class EventsCompletedFragment : Fragment() {

    private var _binding: FragmentEventsCompletedBinding? = null
    private val binding get() = _binding!!
    private val repository = FirestoreRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsCompletedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvEventsCompleted.layoutManager = LinearLayoutManager(requireContext())

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnNotification.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationUiActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadCompletedEvents()
    }

    private fun loadCompletedEvents() {
        lifecycleScope.launch {
            try {
                val acaraWithTotals = repository.getAcaraWithTotals()
                val completedList = acaraWithTotals
                    .filter { it.isCompleted }
                    .map {
                        EventCompleted(
                            title = it.acara.nama_acara,
                            date = it.acara.tanggal,
                            terkumpul = it.totalTerkumpul,
                            target = it.totalTarget,
                            iconRes = R.drawable.ic_flight,
                            iconBgRes = R.drawable.bg_icon_circle_pink
                        )
                    }

                binding.rvEventsCompleted.adapter = EventsCompletedAdapter(completedList) { }

            } catch (e: Exception) {
                // Gagal fetch, biarkan list kosong dulu
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}