package com.team.personalschedule_xml.ui.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.team.personalschedule_xml.databinding.LayoutScheduleWeekBinding
import com.team.personalschedule_xml.ui.common.viewmodel.CalendarViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale


class ScheduleWeekFragment : Fragment() {

    private var _binding : LayoutScheduleWeekBinding? = null
    private val binding get() = _binding!!

    private val calendarViewModel : CalendarViewModel by activityViewModels()
    private lateinit var weekAdapter : WeekAdapter
    private var currentWeekStart : LocalDate = LocalDate.now().with(WeekFields.of(Locale.KOREAN).dayOfWeek(), 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = LayoutScheduleWeekBinding.inflate(inflater, container, false)
        binding.viewModel = calendarViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupWeekNavigation()
        updateWeekHeader()

    }

    private fun setupRecyclerView() {
        weekAdapter = WeekAdapter(
            calendarViewModel,
            onDayClick = { date, isSelected ->
                if (isSelected) {
                    val action = ScheduleWeekFragmentDirections
                        .actionScheduleWeekFragmentToScheduleBottomSheet(selectedDate = date.toString())
                    findNavController().navigate(action)
                } else {
                  calendarViewModel.selectStartDate(date)
                }
            }
        )
        binding.weekRecyclerView.apply {
            adapter = weekAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        updateWeekDays()

        calendarViewModel.selectedStartDate.observe(viewLifecycleOwner) { date ->
            val weekStart = date.with(WeekFields.of(Locale.KOREAN).dayOfWeek(), 1)
            if (weekStart != currentWeekStart) {
                currentWeekStart = weekStart
                updateWeekDays()
                updateWeekHeader()
            } else {
                weekAdapter.notifyDataSetChanged()
            }
        }

        calendarViewModel.scheduleMap.observe(viewLifecycleOwner) {
            weekAdapter.notifyDataSetChanged()
        }
    }

    private fun setupWeekNavigation() {
        binding.prevWeekButton.setOnClickListener {
            currentWeekStart = currentWeekStart.minusWeeks(1)
            updateWeekDays()
            updateWeekHeader()
        }
    }

    private fun updateWeekDays() {
        val days = (0..6).map { offset ->
            currentWeekStart.plusDays(offset.toLong())
        }
        weekAdapter.submitList(days)
    }

    private fun updateWeekHeader() {
        val formatter = DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREAN)
        binding.weekHeaderText.text = currentWeekStart.format(formatter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}