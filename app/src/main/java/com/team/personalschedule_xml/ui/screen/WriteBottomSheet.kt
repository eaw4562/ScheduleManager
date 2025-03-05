package com.team.personalschedule_xml.ui.screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.databinding.FragmentWriteBottomSheetBinding
import com.team.personalschedule_xml.ui.schedule.CalendarViewModel
import com.team.personalschedule_xml.utils.DayViewContainer
import com.team.personalschedule_xml.utils.MonthHeaderViewContainer
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class WriteBottomSheet : BottomSheetDialogFragment() {

    private var _binding : FragmentWriteBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val calendarViewModel: CalendarViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWriteBottomSheetBinding.inflate(inflater, container, false)
        binding.viewModel = calendarViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        Log.d("WriteBottomStart", "초기값" + calendarViewModel.selectedDate.value)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.writeBottomStartCalendar.apply {
            val currentMonth = YearMonth.now()
            val startMonth = currentMonth.minusMonths(12)
            val endMonth = currentMonth.plusMonths(12)

            setup(startMonth, endMonth, DayOfWeek.SUNDAY)
            scrollToMonth(currentMonth)

            binding.writeBottomStartCalendar.monthHeaderBinder = object :
                MonthHeaderFooterBinder<MonthHeaderViewContainer> {
                override fun bind(container: MonthHeaderViewContainer, data: CalendarMonth) {
                    val formatter = DateTimeFormatter.ofPattern("yy년 M월")
                    container.binding.monthHeaderText.text = data.yearMonth.format(formatter)
                }

                override fun create(view: View): MonthHeaderViewContainer {
                    return MonthHeaderViewContainer(view)
                }

            }


            dayBinder = object : MonthDayBinder<DayViewContainer> {
                override fun bind(container: DayViewContainer, data: CalendarDay) {
                   if(calendarViewModel.selectedDate.value == data.date){
                       container.binding.tvDay.setBackgroundResource(R.drawable.current_day_curcle)
                   }else{
                       container.binding.tvDay.background = null
                   }
                }

                override fun create(view: View): DayViewContainer {
                    return DayViewContainer(view)
                }

            }
        }

        calendarViewModel.selectedDate.observe(viewLifecycleOwner) {
            binding.writeBottomStartCalendar.notifyCalendarChanged()
            Log.d("WriteBottom", "Calendar Value" + calendarViewModel.selectedDate.value)
        }
        binding.writeBottomStartDateText.setOnClickListener {
            toggleView(binding.writeBottomStartCalendar)
        }

        // 시작 시간 텍스트 클릭 시 하단의 TimePicker 토글
        binding.writeBottomStartTimeText.setOnClickListener {
            toggleView(binding.writeBottomStartTime)
        }

        // 종료 날짜 텍스트 클릭 시 하단의 CalendarView 토글
        binding.writeBottomEndDateText.setOnClickListener {
            toggleView(binding.writeBottomEndCalendar)
        }

        // 종료 시간 텍스트 클릭 시 하단의 TimePicker 토글
        binding.writeBottomEndTimeText.setOnClickListener {
            toggleView(binding.writeBottomEndTime)
        }
    }

    override fun onStart() {
        super.onStart()
    }
}

private fun toggleView(view: View) {
    if (view.visibility == View.GONE) {
        // Fade-in 애니메이션 적용 (선택 사항)
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(300)
            .start()
    } else {
        // Fade-out 애니메이션 적용 (선택 사항)
        view.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                view.visibility = View.GONE
            }
            .start()
    }
}