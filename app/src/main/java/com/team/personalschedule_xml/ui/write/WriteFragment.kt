package com.team.personalschedule_xml.ui.write

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.team.personalschedule_xml.data.model.Memo
import com.team.personalschedule_xml.data.model.Schedule
import com.team.personalschedule_xml.data.repository.ScheduleRepository
import com.team.personalschedule_xml.databinding.LayoutWriteBinding
import com.team.personalschedule_xml.ui.common.viewmodel.CalendarViewModel
import com.team.personalschedule_xml.ui.memo.MemoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.Instant
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

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)", Locale.KOREAN)
    private val timeFormatter = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREAN)
    val memoViewModel: MemoViewModel by activityViewModels()

    private var isMemo : Boolean = false

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

        binding.writeBottomStartDateText.setOnClickListener { showStartDatePicker() }
        binding.writeBottomStartTimeText.setOnClickListener { showStartTimePicker() }
        binding.writeBottomEndDateText.setOnClickListener { showEndDatePicker() }
        binding.writeBottomEndTimeText.setOnClickListener { showEndTimePicker() }

        binding.writeBottomAllDaySwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.writeBottomStartTimeText.visibility = View.GONE
                binding.writeBottomEndTimeText.visibility = View.GONE
            } else {
                binding.writeBottomStartTimeText.visibility = View.VISIBLE
                binding.writeBottomEndTimeText.visibility = View.VISIBLE
            }
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
            if (isMemo) {
                saveMemoToDb()
            }else {
                saveScheduleToDb()
            }
        }

        binding.writeBottomMemoSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isMemo = true
                binding.writeBottomDateLayout.visibility = View.GONE
            } else{
                isMemo = false
                binding.writeBottomDateLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun showStartDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("시작 날짜 선택")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val localDate = Instant.ofEpochMilli(selection)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            calendarViewModel.selectStartDate(localDate)

            // 만약 '종일' 스위치가 OFF라면 시간도 선택
            if (!binding.writeBottomAllDaySwitch.isChecked) {
                showStartTimePicker()
            }
        }
        datePicker.show(childFragmentManager, "START_DATE_PICKER")
    }

    private fun showStartTimePicker() {
        if (binding.writeBottomAllDaySwitch.isChecked) {
            // 종일인 경우 시간 선택 안 함
            return
        }
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText("시작 시간 선택")
            .setTimeFormat(TimeFormat.CLOCK_12H) // or CLOCK_24H
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val selectedTime = LocalTime.of(timePicker.hour, timePicker.minute)
            calendarViewModel.selectStartTime(selectedTime)
        }
        timePicker.show(childFragmentManager, "START_TIME_PICKER")
    }

    private fun showEndDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("종료 날짜 선택")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val localDate = Instant.ofEpochMilli(selection)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            calendarViewModel.selectEndDate(localDate)

            if (!binding.writeBottomAllDaySwitch.isChecked) {
                showEndTimePicker()
            }
        }
        datePicker.show(childFragmentManager, "END_DATE_PICKER")
    }

    private fun showEndTimePicker() {
        if (binding.writeBottomAllDaySwitch.isChecked) {
            return
        }
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText("종료 시간 선택")
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val selectedTime = LocalTime.of(timePicker.hour, timePicker.minute)
            calendarViewModel.selectEndTime(selectedTime)
        }
        timePicker.show(childFragmentManager, "END_TIME_PICKER")
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

    private fun saveMemoToDb() {
        Log.d("WriteFragment", "saveMemoToDb() called")


        val title = binding.writeBottomTitleEdit.text.toString()
        val labelColor = calendarViewModel.selectedLabel.value?.color ?: 0
        val labelName = calendarViewModel.selectedLabel.value?.colorName ?: ""
        val content = binding.memoText.text.toString()
        if (title.isBlank()) {
            Toast.makeText(requireContext(), "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }
        val memo = Memo(
            title = title,
            memo = content,
            labelName = labelName,
            labelColorResId = labelColor)

        memoViewModel.insertMemo(memo)
        findNavController().popBackStack()
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
