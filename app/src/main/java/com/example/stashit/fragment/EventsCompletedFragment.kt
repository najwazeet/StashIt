package com.example.stashit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashit.R
import com.example.stashit.adapter.EventsCompletedAdapter
import com.example.stashit.data.EventCompleted
import com.example.stashit.databinding.FragmentEventsCompletedBinding
import android.content.Intent
import com.example.stashit.NotificationsActivity

class EventsCompletedFragment : Fragment() {

    private var _binding: FragmentEventsCompletedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsCompletedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dummyData = listOf(
            EventCompleted(
                title = "Bali Trip",
                date = "12 Agu 2026",
                terkumpul = 3_500_000,
                target = 5_000_000,
                iconRes = R.drawable.ic_flight,
                iconBgRes = R.drawable.bg_icon_circle_pink
            ),
            EventCompleted(
                title = "Konser Coldplay",
                date = "3 Sep 2026",
                terkumpul = 3_500_000,
                target = 3_500_000,
                iconRes = R.drawable.ic_music_note,
                iconBgRes = R.drawable.bg_icon_circle_lavender
            ),
            EventCompleted(
                title = "Wisuda Sahabat",
                date = "20 Jul 2026",
                terkumpul = 500_000,
                target = 500_000,
                iconRes = R.drawable.ic_apartment,
                iconBgRes = R.drawable.bg_icon_circle_mint
            )
        )

        binding.rvEventsCompleted.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEventsCompleted.adapter = EventsCompletedAdapter(dummyData) { }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnNotification.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationsActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}