package com.team.personalschedule_xml.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.databinding.LayoutNotificationBinding

class NotificationFragment : Fragment() {

    private var _binding : LayoutNotificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel : NotificationViewModel by activityViewModels()
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding =  LayoutNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NotificationAdapter(emptyList()) { notification ->
            val action = NotificationFragmentDirections
                .actionNotificationFragmentToScheduleDetailFragment(notification.scheduleId)
            findNavController().navigate(action)
        }

        binding.notiRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.notiRecycler.adapter = adapter

        viewModel.loadNotifications()

        viewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            adapter.updateNotifications(notifications)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}