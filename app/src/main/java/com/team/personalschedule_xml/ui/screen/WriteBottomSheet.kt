package com.team.personalschedule_xml.ui.screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.data.model.Schedule
import com.team.personalschedule_xml.data.model.ScheduleRepository
import com.team.personalschedule_xml.databinding.FragmentWriteBottomSheetBinding
import com.team.personalschedule_xml.ui.schedule.CalendarViewModel
import com.team.personalschedule_xml.utils.DayViewContainer
import com.team.personalschedule_xml.utils.MonthHeaderViewContainer
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class WriteBottomSheet : BottomSheetDialogFragment() {

    private var _binding : FragmentWriteBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val calendarViewModel: CalendarViewModel by activityViewModels()

    private lateinit var scheduleRepository: ScheduleRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWriteBottomSheetBinding.inflate(inflater, container, false)
        binding.viewModel = calendarViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        scheduleRepository = ScheduleRepository(requireContext())

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
        }
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

        binding.writeBottomStartCalendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data

                // 날짜 숫자를 설정합니다.
                container.binding.tvDay.text = data.date.dayOfMonth.toString()

                if (calendarViewModel.selectedStartDate.value == data.date) {
                    container.binding.tvDay.setBackgroundResource(R.drawable.current_day_curcle)
                } else {
                    container.binding.tvDay.background = null
                }

                container.view.setOnClickListener {
                    calendarViewModel.selectStartDate(data.date)
                }
            }

            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view)
            }
        }

        binding.writeBottomEndCalendar.apply {
            val currentMonth = YearMonth.now()
            val startMonth = currentMonth.minusMonths(12)
            val endMonth = currentMonth.plusMonths(12)

            setup(startMonth, endMonth, DayOfWeek.SUNDAY)
            scrollToMonth(currentMonth)
        }

        binding.writeBottomEndCalendar.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthHeaderViewContainer> {
            override fun bind(container: MonthHeaderViewContainer, data: CalendarMonth) {
                val formatter = DateTimeFormatter.ofPattern("yy년 M월")
                container.binding.monthHeaderText.text = data.yearMonth.format(formatter)
            }

            override fun create(view: View): MonthHeaderViewContainer {
                return MonthHeaderViewContainer(view)
            }

        }

        binding.writeBottomEndCalendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data

                // 날짜 숫자를 설정합니다.
                container.binding.tvDay.text = data.date.dayOfMonth.toString()

                if (calendarViewModel.selectedEndDate.value == data.date) {
                    container.binding.tvDay.setBackgroundResource(R.drawable.current_day_curcle)
                } else {
                    container.binding.tvDay.background = null
                }

                container.view.setOnClickListener {
                    calendarViewModel.selectEndDate(data.date)
                    Log.d("EndDate", "seleceted EndDate" + calendarViewModel.selectedEndDate.value)
                }
            }

            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view)
            }
        }

        calendarViewModel.selectedStartDate.observe(viewLifecycleOwner) {
            binding.writeBottomStartCalendar.notifyCalendarChanged()
            Log.d("WriteBottom", "Calendar Value" + calendarViewModel.selectedStartDate.value)
        }

        calendarViewModel.selectedEndDate.observe(viewLifecycleOwner) {
            binding.writeBottomEndCalendar.notifyCalendarChanged()
            Log.d("WriteBottom", "Calendar Value" + calendarViewModel.selectedStartDate.value)
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

        binding.bottomWriteLabelLayout.setOnClickListener {
            // "ColorPickerBottomSheet" 태그로 이미 프래그먼트가 추가되어 있는지 확인
            if (childFragmentManager.findFragmentByTag("ColorPickerBottomSheet") == null) {
                val colorPicker = ColorPickerBottomSheet()
                colorPicker.show(childFragmentManager, "ColorPickerBottomSheet")
            }
        }

        binding.bottomWriteAlarmLayout.setOnClickListener {
            if (childFragmentManager.findFragmentByTag("AlarmBottomSheet") == null) {
                val alarmSheet = AlarmBottomSheet()
                alarmSheet.show(childFragmentManager, "AlarmBottomSheet")
            }
        }

        binding.writeBottomMemoCloseImg.setOnClickListener {
            binding.memoText.text?.clear()
        }
        binding.writeBottomCloseIBtn.setOnClickListener {
            dialog?.dismiss()
        }

        binding.writeBottomSaveText.setOnClickListener {
            Log.d("WriteBottomSheet", "Click")
            saveScheduleToDb()
        }

    }

    private fun saveScheduleToDb() {
        Log.d("WriteBottomSheet", "saveScheduleToDb() called")
        val title = binding.writeBottomTitleEdit.text.toString()
        val isAllDay = binding.writeBottomAllDaySwitch.isChecked
        val startDate = calendarViewModel.selectedStartDate.value
        val endDate = calendarViewModel.selectedEndDate.value

        val startTime = if(!isAllDay) calendarViewModel.selectedStartTime.value else null
        val endTime = if(!isAllDay) calendarViewModel.selectedEndTime.value else null

        // ISO-8601 문자열로 변환 (간단하게 LocalDate.toString(), LocalTime.toString() 사용)
        val startDateTime = if (!isAllDay && startDate != null && startTime != null)
            "${startDate}T${startTime}" else startDate?.toString() ?: ""
        val endDateTime = if (!isAllDay && endDate != null && endTime != null)
            "${endDate}T${endTime}" else endDate?.toString() ?: ""

        val labelColor = calendarViewModel.selectedLabel.value?.color ?: 0
        val labelName = calendarViewModel.selectedLabel.value?.colorName ?: ""
        val alarm = calendarViewModel.alarmText.value ?: ""

        val memo = binding.memoText.text.toString()

        val schedule = Schedule(
            title = title,
            isAllDay = isAllDay,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            labelColor = labelColor,
            labelName = labelName,
            alarm = alarm,
            memo = memo
        )

        lifecycleScope.launch {
            Log.d("TEST", "Inside coroutine")
            try {
                scheduleRepository.insertSchedule(schedule)
                Log.d("WriteBottomSheet", "Schedule saved: $schedule")
                dismiss()  // 저장 후 바텀시트 dismiss
            }catch (e :Exception) {
                Log.e("WriteBottomSheet", "Error saving schedule", e)
            }
        }
    }



    override fun onStart() {
        super.onStart()
        dialog?.let { d ->
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
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