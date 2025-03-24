package com.team.personalschedule_xml.ui.write

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.AlarmManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.data.model.Schedule
import com.team.personalschedule_xml.data.repository.ScheduleRepository
import com.team.personalschedule_xml.databinding.LayoutWriteBinding
import com.team.personalschedule_xml.ui.common.viewmodel.CalendarViewModel
import com.team.personalschedule_xml.ui.common.calendar.DayViewContainer
import com.team.personalschedule_xml.ui.common.calendar.MonthHeaderViewContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class WriteFragment : Fragment() {
    private var _binding: LayoutWriteBinding? = null
    private val binding get() = _binding!!

    private val calendarViewModel: CalendarViewModel by activityViewModels()
    private lateinit var scheduleRepository: ScheduleRepository
    private var scheduleId: Int = -1

    private val timeFormatter = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREAN)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = LayoutWriteBinding.inflate(inflater, container, false)
        binding.viewModel = calendarViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        scheduleRepository = ScheduleRepository(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = WriteFragmentArgs.fromBundle(requireArguments())
        scheduleId = args.scheduleId

        // 조건 처리: 새 스케줄 작성 시(selectedDate가 SafeArgs에 전달되었는지 여부)
        if (scheduleId == -1) {
            if (args.selectedDate != null) {
                val date = LocalDate.parse(args.selectedDate)
                calendarViewModel.selectStartDate(date)
                calendarViewModel.selectEndDate(date)
                Log.d("WriteFragment", "SafeArgs 전달 받은 값: $date")
            } else {
                calendarViewModel.resetForNewSchedule()
                Log.d("WriteFragment", "SafeArgs 전달 값 없음 - resetForNewSchedule() 호출")
            }
        } else {
            // 기존 스케줄 수정인 경우
            lifecycleScope.launch {
                val schedule = scheduleRepository.getScheduleById(scheduleId)
                if (schedule != null) {
                    loadScheduleData(schedule)
                } else {
                    Log.e("WriteFragment", "Schedule not found for ID: $scheduleId")
                }
            }
        }

        // TimePicker 관찰자
        calendarViewModel.selectedStartTime.observe(viewLifecycleOwner) { time ->
            time?.let {
                binding.writeBottomStartTime.hour = it.hour
                binding.writeBottomStartTime.minute = it.minute
                binding.writeBottomStartTimeText.text = it.format(timeFormatter)
            }
        }
        calendarViewModel.selectedEndTime.observe(viewLifecycleOwner) { time ->
            time?.let {
                binding.writeBottomEndTime.hour = it.hour
                binding.writeBottomEndTime.minute = it.minute
                binding.writeBottomEndTimeText.text = it.format(timeFormatter)
            }
        }

        // 시작 캘린더 설정
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

        // 종료 캘린더 설정
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
                container.binding.tvDay.text = data.date.dayOfMonth.toString()
                if (calendarViewModel.selectedEndDate.value == data.date) {
                    container.binding.tvDay.setBackgroundResource(R.drawable.current_day_curcle)
                } else {
                    container.binding.tvDay.background = null
                }
                container.view.setOnClickListener {
                    calendarViewModel.selectEndDate(data.date)
                    Log.d("WriteFragment", "Selected EndDate: ${calendarViewModel.selectedEndDate.value}")
                }
            }
            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view)
            }
        }

        // LiveData 관찰: 캘린더 갱신
        calendarViewModel.selectedStartDate.observe(viewLifecycleOwner) {
            binding.writeBottomStartCalendar.notifyCalendarChanged()
            Log.d("WriteFragment", "Updated selectedStartDate: ${calendarViewModel.selectedStartDate.value}")
        }
        calendarViewModel.selectedEndDate.observe(viewLifecycleOwner) {
            binding.writeBottomEndCalendar.notifyCalendarChanged()
            Log.d("WriteFragment", "Updated selectedEndDate: ${calendarViewModel.selectedEndDate.value}")
        }

        // UI 이벤트 처리
        binding.writeBottomStartDateText.setOnClickListener {
            toggleView(binding.writeBottomStartCalendar)
        }
        binding.writeBottomStartTimeText.setOnClickListener {
            toggleView(binding.writeBottomStartTime)
            toggleView(binding.writeBottomStartTimeConfirmBtn)
        }
        binding.writeBottomStartTime.setOnTimeChangedListener { _, hour, minute ->
            val time = LocalTime.of(hour, minute)
            binding.writeBottomStartTimeText.text = time.format(timeFormatter)
        }
        binding.writeBottomStartTimeConfirmBtn.setOnClickListener {
            val hour = binding.writeBottomStartTime.hour
            val minute = binding.writeBottomStartTime.minute
            calendarViewModel.selectStartTime(LocalTime.of(hour, minute))
            toggleView(binding.writeBottomStartTime)
            toggleView(binding.writeBottomStartTimeConfirmBtn)
        }
        binding.writeBottomEndDateText.setOnClickListener {
            toggleView(binding.writeBottomEndCalendar)
        }
        binding.writeBottomEndTimeText.setOnClickListener {
            toggleView(binding.writeBottomEndTime)
            toggleView(binding.writeBottomEndTimeConfirmBtn)
        }
        binding.writeBottomEndTime.setOnTimeChangedListener { _, hour, minute ->
            val time = LocalTime.of(hour, minute)
            binding.writeBottomEndTimeText.text = time.format(timeFormatter)
        }
        binding.writeBottomEndTimeConfirmBtn.setOnClickListener {
            val hour = binding.writeBottomEndTime.hour
            val minute = binding.writeBottomEndTime.minute
            calendarViewModel.selectEndTime(LocalTime.of(hour, minute))
            toggleView(binding.writeBottomEndTime)
            toggleView(binding.writeBottomEndTimeConfirmBtn)
        }
        binding.bottomWriteLabelLayout.setOnClickListener {
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
            findNavController().popBackStack()
        }
        binding.writeBottomSaveText.setOnClickListener {
            Log.d("WriteFragment", "Save button clicked")
            saveScheduleToDb()
        }
    }

    private fun loadScheduleData(schedule: Schedule) {
        binding.writeBottomTitleEdit.setText(schedule.title)
        binding.writeBottomAllDaySwitch.isChecked = schedule.isAllDay

        schedule.startDateTime?.let { startDateTime ->
            calendarViewModel.selectStartDate(startDateTime.toLocalDate())
            calendarViewModel.selectStartTime(startDateTime.toLocalTime())
        }
        schedule.endDateTime?.let { endDateTime ->
            calendarViewModel.selectEndDate(endDateTime.toLocalDate())
            calendarViewModel.selectEndTime(endDateTime.toLocalTime())
        }
        calendarViewModel.setLabel(schedule.labelColor, schedule.labelName)
        calendarViewModel.setAlarm(schedule.alarm)
        binding.memoText.setText(schedule.memo)
        Log.d("WriteFragment", "Loaded schedule; isEndTimeAutoSync: ${calendarViewModel.isEndTimeAutoSync}")
    }

    private fun saveScheduleToDb() {
        Log.d("WriteFragment", "saveScheduleToDb() called")
        val title = binding.writeBottomTitleEdit.text.toString()
        val isAllDay = binding.writeBottomAllDaySwitch.isChecked
        val startDate = calendarViewModel.selectedStartDate.value
        val endDate = calendarViewModel.selectedEndDate.value

        val startTime = if (!isAllDay) calendarViewModel.selectedStartTime.value else null
        val endTime = if (!isAllDay) calendarViewModel.selectedEndTime.value else null

        val startDateTime = if (!isAllDay && startDate != null && startTime != null) {
            LocalDateTime.of(startDate, startTime)
        } else {
            startDate?.atStartOfDay()
        }
        val endDateTime = if (!isAllDay && endDate != null && endTime != null) {
            LocalDateTime.of(endDate, endTime)
        } else {
            endDate?.atStartOfDay()
        }
        val labelColor = calendarViewModel.selectedLabel.value?.color ?: 0
        val labelName = calendarViewModel.selectedLabel.value?.colorName ?: ""
        val alarm = calendarViewModel.alarmText.value ?: ""
        val memo = binding.memoText.text.toString()

        val schedule = Schedule(
            id = if (scheduleId != -1) scheduleId else 0,
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
            try {
                val newId = if (scheduleId != -1) {
                    scheduleRepository.updateSchedule(schedule)
                    Log.d("WriteFragment", "Schedule updated: $schedule")
                    scheduleId
                } else {
                    val insertedId = scheduleRepository.insertSchedule(schedule).toInt()
                    Log.d("WriteFragment", "Schedule inserted with ID: $insertedId")
                    insertedId
                }
                withContext(Dispatchers.Main) {
                    calendarViewModel.loadSchedules()
                    // Alarm 예약 코드는 필요시 활성화
                    findNavController().popBackStack()
                }
            } catch (e: Exception) {
                Log.e("WriteFragment", "Error saving schedule", e)
            }
        }
    }

    private fun toggleView(view: View) {
        if (view.visibility == View.GONE) {
            view.alpha = 0f
            view.visibility = View.VISIBLE
            view.animate().alpha(1f).setDuration(300).start()
        } else {
            view.animate().alpha(0f).setDuration(300).withEndAction {
                view.visibility = View.GONE
            }.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
