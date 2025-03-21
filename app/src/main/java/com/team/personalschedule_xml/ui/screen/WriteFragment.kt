package com.team.personalschedule_xml.ui.screen

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.data.model.Schedule
import com.team.personalschedule_xml.data.model.ScheduleRepository
import com.team.personalschedule_xml.databinding.FragmentWriteBinding
import com.team.personalschedule_xml.databinding.FragmentWriteBottomSheetBinding
import com.team.personalschedule_xml.ui.schedule.CalendarViewModel
import com.team.personalschedule_xml.utils.AlarmReceiver
import com.team.personalschedule_xml.utils.DayViewContainer
import com.team.personalschedule_xml.utils.MonthHeaderViewContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class WriteFragment : Fragment() {
    private var _binding : FragmentWriteBinding? = null
    private val binding get() = _binding!!

    private val calendarViewModel: CalendarViewModel by activityViewModels()

    private lateinit var scheduleRepository: ScheduleRepository
    private var scheduleId : Int = -1

    private val timeFormatter = DateTimeFormatter.ofPattern("a hh:mm", java.util.Locale.KOREAN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleId = WriteFragmentArgs.fromBundle(requireArguments()).scheduleId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWriteBinding.inflate(inflater, container, false)
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

        if (scheduleId != -1) {
            calendarViewModel.scheduleMap.observe(viewLifecycleOwner) { scheduleMap ->
                val schedule = scheduleMap?.values?.flatten()?.find { it.id == scheduleId }
                schedule?.let {
                    loadScheduleData(it)
                }
            }
            calendarViewModel.selectedStartTime.observe(viewLifecycleOwner) {time ->
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
                    Log.d("WriteFragment", "EndTimePicker after load: ${binding.writeBottomEndTime.hour}:${binding.writeBottomEndTime.minute}")
                }
            }
        }else{
            calendarViewModel.resetForNewSchedule()
            calendarViewModel.selectedStartTime.observe(viewLifecycleOwner) {time ->
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

        // 종료 날짜 텍스트 클릭 시 하단의 CalendarView 토글
        binding.writeBottomEndDateText.setOnClickListener {
            toggleView(binding.writeBottomEndCalendar)
        }

        // 종료 시간 텍스트 클릭 시 하단의 TimePicker 토글
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
            findNavController().popBackStack()
        }

        binding.writeBottomSaveText.setOnClickListener {
            Log.d("WriteBottomSheet", "Click")
            saveScheduleToDb()
        }

    }

    private fun loadScheduleData(schedule: Schedule) {
        binding.writeBottomTitleEdit.setText(schedule.title)
        binding.writeBottomAllDaySwitch.isChecked = schedule.isAllDay

        val startDateTime = schedule.startDateTime
        if (startDateTime.contains("T")) {
            val (date, time) = startDateTime.split("T")
            calendarViewModel.selectStartDate(LocalDate.parse(date))
            calendarViewModel.selectStartTime(LocalTime.parse(time))
        }else{
            calendarViewModel.selectStartDate(LocalDate.parse(startDateTime))
        }

        val endDateTime = schedule.endDateTime
        if(endDateTime.contains("T")) {
            val (date, time) = endDateTime.split("T")
            calendarViewModel.selectEndDate(LocalDate.parse(date))
            calendarViewModel.selectEndTime(LocalTime.parse(time))
            calendarViewModel.enableEndTimeAutoSync()
        }else{
            calendarViewModel.selectEndDate(LocalDate.parse(endDateTime))
        }
        calendarViewModel.setLabel(schedule.labelColor, schedule.labelName)
        calendarViewModel.setAlarm(schedule.alarm)
        binding.memoText.setText(schedule.memo)

        Log.d("WriteFragment", "isEndTimeAutoSync after load: ${calendarViewModel.isEndTimeAutoSync}")
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
                    scheduleAlarms(newId, schedule) //알림 예약
                    findNavController().popBackStack()
                }
            } catch (e: Exception) {
                Log.e("WriteFragment", "Error saving schedule", e)
            }
        }
    }

    private fun scheduleAlarms(scheduleId : Int, schedule: Schedule) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (schedule.startDateTime.isBlank()) {
            Log.e("WriteFragment", "startDateTime is empty")
            return
        }

        val startDateTime = if (schedule.startDateTime.contains("T")) {
            LocalDateTime.parse(schedule.startDateTime)
        } else {
            LocalDate.parse(schedule.startDateTime).atStartOfDay()
        }
        Log.d("WriteFragment", "Scheduling alarms for startDateTime: $startDateTime")

        val alarmConditions = schedule.alarm.split(",").map { it.trim() }
        alarmConditions.forEach{ condition ->
            val cleanedCondition = when {
                condition.contains("시작") -> "시작"
                condition.contains("10분 전") -> "10분 전"
                condition.contains("1시간 전") -> "1시간 전"
                else -> {
                    Log.d("WriteFragment", "Invalid alarm condition: $condition")
                    return@forEach
            }
        }

            val triggerTime = when (cleanedCondition) {
                "시작" -> startDateTime
                "10분 전" -> startDateTime.minusMinutes(10)
                "1시간 전" -> startDateTime.minusHours(1)
                else -> return@forEach
            }

            val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
                putExtra("scheduleId", scheduleId)
                putExtra("title", schedule.title)
                putExtra("condition", condition)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                scheduleId + condition.hashCode(), // 고유한 requestCode 생성
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val triggerMillis = triggerTime.toInstant(ZoneId.systemDefault().rules.getOffset(triggerTime)).toEpochMilli()
            Log.d("WriteFragment", "Alarm condition: $condition, triggerTime: $triggerTime, triggerMillis: $triggerMillis, currentMillis: ${System.currentTimeMillis()}")
            if (triggerMillis > System.currentTimeMillis()) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
                Log.d("WriteFragment", "Alarm set for $condition at $triggerMillis")
            }else {
                Log.d("WriteFragment", "Alarm not set for $condition: triggerMillis ($triggerMillis) is in the past")
            }
        }
    }

    /*override fun onStart() {
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
    }*/
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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