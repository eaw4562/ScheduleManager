package com.team.personalschedule_xml.ui.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.databinding.LayoutScheduleListBinding
import com.team.personalschedule_xml.ui.common.viewmodel.CalendarViewModel

class ScheduleListFragment : Fragment() {
    private var _binding : LayoutScheduleListBinding? = null
    private val binding get() = _binding!!
    private val viewModel : CalendarViewModel by activityViewModels()
    private lateinit var adapter : ScheduleListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = LayoutScheduleListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ScheduleListAdapter()

        binding.scheduleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.scheduleRecyclerView.adapter = adapter

        viewModel.allSchedules.observe(viewLifecycleOwner) { scheduleMap ->
            val groupedList = viewModel.transformScheduleList(scheduleMap)
            adapter.submitList(groupedList)
        }

    }
}