package com.team.personalschedule_xml.ui.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.data.model.Schedule
import com.team.personalschedule_xml.data.repository.ScheduleRepository
import com.team.personalschedule_xml.databinding.LayoutBottomSheetCopyScheduleBinding
import com.team.personalschedule_xml.ui.common.calendar.DayViewContainer
import com.team.personalschedule_xml.ui.common.calendar.MonthHeaderViewContainer
import com.team.personalschedule_xml.ui.common.viewmodel.CalendarViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CopyScheduleBottomSheet(
    private val schedule: Schedule,
    private val onCopyComplete : () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding : LayoutBottomSheetCopyScheduleBinding? = null
    private val binding get() = _binding!!
    private val selectedDates = mutableListOf<LocalDate>()
    private val calendarViewModel : CalendarViewModel by activityViewModels()
    private lateinit var scheduleRepository: ScheduleRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = LayoutBottomSheetCopyScheduleBinding.inflate(inflater, container, false)
        scheduleRepository = ScheduleRepository(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.copyDatesButton.isEnabled = false

        binding.titleTextView.text = schedule.title
        setupCalendar()

        binding.copyDatesButton.setOnClickListener {
            copyScheduleToSelectedDates()
        }

    }

    private fun setupCalendar() {
        val currentMonth = YearMonth.now()
        binding.copyCalendarView.setup(
            currentMonth.minusMonths(12),
            currentMonth.plusMonths(12),
            DayOfWeek.SUNDAY
        )
        binding.copyCalendarView.scrollToMonth(currentMonth)

        binding.copyCalendarView.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthHeaderViewContainer> {
            override fun bind(container: MonthHeaderViewContainer, data: CalendarMonth) {
                val formatter = DateTimeFormatter.ofPattern("yyyy년 M월")
                container.binding.monthHeaderText.text = data.yearMonth.format(formatter)
            }

            override fun create(view: View): MonthHeaderViewContainer {
                return MonthHeaderViewContainer(view)
            }

        }

        binding.copyCalendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.binding.tvDay.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    container.view.setOnClickListener {
                        if (selectedDates.contains(data.date)) {
                            selectedDates.remove(data.date)
                            container.binding.tvDay.background = null
                        } else {
                            selectedDates.add(data.date)
                            container.binding.dayLayout.setBackgroundResource(R.drawable.selected_day_background)
                        }
                        binding.copyDatesButton.isEnabled = selectedDates.isNotEmpty()
                    }

                    if (selectedDates.contains(data.date)) {
                        container.binding.dayLayout.setBackgroundResource(R.drawable.selected_day_background)
                    } else {
                        container.binding.dayLayout.background = null
                    }
                } else {
                    container.binding.tvDay.alpha = 0.3f
                    container.view.setOnClickListener(null)
                }
            }

            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view)
            }
        }
     }

    private fun copyScheduleToSelectedDates() {
        calendarViewModel.copyScheduleToDate(schedule, selectedDates.toList()) {
            onCopyComplete.invoke()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}