package com.team.personalschedule_xml.ui.common.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.animation.Transformation
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.data.model.CalendarLabel
import com.team.personalschedule_xml.data.model.Schedule
import com.team.personalschedule_xml.data.repository.ScheduleRepository
import com.team.personalschedule_xml.ui.schedule.ScheduleListItem
import com.team.personalschedule_xml.utils.constants.AlarmConstants
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    // 시작 날짜
    private val _selectedStartDate = MutableLiveData<LocalDate>(LocalDate.now())
    val selectedStartDate: LiveData<LocalDate> = _selectedStartDate

    // 종료 날자
    private val _selectedEndDate = MutableLiveData<LocalDate>(LocalDate.now())
    val selectedEndDate : LiveData<LocalDate> = _selectedEndDate

    //종료 날짜 시작 날짜 동기화 여부
    private var isEndDateAutoSync = true

    private var endDateDeltaDays : Long = 0

    private val _selectedStartTime = MutableLiveData<LocalTime>(LocalTime.now())
    val selectedStartTime : LiveData<LocalTime> = _selectedStartTime

    // 초기 종료 시간은 시작 시간보다 1시간 뒤1
    private val _selectedEndTime = MutableLiveData<LocalTime>(LocalTime.now().plusHours(1))
    val selectedEndTime : LiveData<LocalTime> = _selectedEndTime


    // 종료 시간 동기화 여부 및 시간 차이(시간 단위)
    public var isEndTimeAutoSync = true
    private var endTimeDeltaHours : Long = 1

    private val _selectedLabel = MutableLiveData<CalendarLabel>(
        CalendarLabel(
            color = R.color.emerald_green,  // 초기값
            colorName = "에메랄드 그린",
            isSelected = true
        )
    )
    val selectedLabel: LiveData<CalendarLabel> = _selectedLabel

    // 알림 체크 상태를 저장하는 LiveData (초기값 false)
    val alarmStartEnabled = MutableLiveData<Boolean>(false)
    val alarmTenMinuteEnabled = MutableLiveData<Boolean>(false)
    val alarmOneHourEnabled = MutableLiveData<Boolean>(false)

    // 알림 텍스트 (초기값 "알림 없음")
    private val _alarmText = MutableLiveData<String>(AlarmConstants.NO_ALARM)
    val alarmText: LiveData<String> get() = _alarmText

    private val _scheduleMap = MutableLiveData<Map<LocalDate, List<Schedule>>>(emptyMap())
    val scheduleMap : LiveData<Map<LocalDate, List<Schedule>>> = _scheduleMap

    val scheduleColorsMap = MutableLiveData<Map<LocalDate, List<Int>>>(emptyMap())

    fun loadScheduleColorsMap() {
        viewModelScope.launch {
            val schedules = repository.getAllSchedules()
            val colorsMap = schedules.filter { it.startDateTime != null }
                .groupBy {
                    it.startDateTime!!.toLocalDate()
                }.mapValues { entry ->
                    entry.value.map { it.labelColor }
                }
            scheduleColorsMap.value = colorsMap
        }
    }

    //Todo - DI
    private val repository: ScheduleRepository = ScheduleRepository(application)

    val allSchedules: LiveData<Map<LocalDate, List<Schedule>>> =
        repository.getAllSchedulesLive().map { schedueles ->
            schedueles.groupBy { it.startDateTime?.toLocalDate() ?: LocalDate.now() }
        }

    fun transformScheduleList(scheduleMap: Map<LocalDate, List<Schedule>>): List<ScheduleListItem> {
        val list = mutableListOf<ScheduleListItem>()
        val sortedMap = scheduleMap.toSortedMap()

        sortedMap.forEach { (date, schedules) ->
            list.add(ScheduleListItem.Header(date, date == LocalDate.now()))
            schedules.sortedBy { it.startDateTime }.forEach { schedule ->
                list.add(ScheduleListItem.ScheduleItem(schedule))
            }
        }
        return list
    }

    init {
        loadSchedules()
    }

    fun loadSchedules() {
        viewModelScope.launch {
            val scheduleList = repository.getAllSchedules()
            Log.d("CalendarViewModel", "Loaded schedules: ${scheduleList.size}")

            val map = scheduleList.filter { it.startDateTime != null }
                .groupBy { schedule ->
                    schedule.startDateTime!!.toLocalDate()
                }
            _scheduleMap.value = map
            Log.d("CalendarViewModel", "ScheduleMap updated: $_scheduleMap")
        }
    }

    fun copyScheduleToDate(schedule: Schedule, dates : List<LocalDate>, onComplete : () -> Unit) {
        viewModelScope.launch {
            dates.forEach { date ->
                val newSchedule = schedule.copy(
                    id = 0,
                    startDateTime = replaceDate(schedule.startDateTime, date),
                    endDateTime = replaceDate(schedule.endDateTime, date)
                )
                repository.insertSchedule(newSchedule)
            }
            loadSchedules()
            onComplete()
        }
    }

    private fun replaceDate(originalDateTime: LocalDateTime?, newDate: LocalDate): LocalDateTime? {
        return originalDateTime?.let {
            LocalDateTime.of(newDate, it.toLocalTime())
        }
    }


    /**
    * 시작 날짜를 선택할 때 호출됩니다.
    * - 만약 종료 날짜가 아직 자동 동기화 모드라면, 시작 날짜와 동일하게 업데이트합니다.
    * - 만약 사용자가 종료 날짜를 직접 선택해 자동 동기화 모드가 해제된 상태라면,
    *   기존의 종료 날짜와 시작 날짜의 차이(Δ)를 유지하여 종료 날짜를 업데이트합니다.
    */
    fun selectStartDate(newStart : LocalDate) {
        val oldStart = _selectedStartDate.value ?: newStart
        _selectedStartDate.value = newStart

        if (isEndDateAutoSync) {
            // 종료 날짜가 자동 동기화 모드라면 시작 날짜와 동일하게 설정
            _selectedEndDate.value = newStart
            endDateDeltaDays = 0
        } else {
            // 수동으로 종료 날짜를 선택한 상태라면, 기존 차이(Δ)를 유지하여 업데이트
            _selectedEndDate.value = newStart.plusDays(endDateDeltaDays)
        }
    }

    /**
     * 종료 날짜를 선택할 때 호출됩니다.
     * 이 메서드는 사용자가 종료 날짜를 직접 변경하는 경우에 사용됩니다.
     * 종료 날짜를 수동으로 선택하면 자동 동기화 모드를 해제하고,
     * 현재 시작 날짜와 선택된 종료 날짜 간의 차이(Δ)를 계산하여 저장합니다.
     */
    fun selectEndDate(newEnd : LocalDate) {
        val currentStart = _selectedStartDate.value ?: newEnd
        if (newEnd.isBefore(currentStart)) {
            _selectedStartDate.value = newEnd
            _selectedEndDate.value = newEnd
            isEndDateAutoSync = true
            endDateDeltaDays = 0
        } else {
            _selectedEndDate.value = newEnd
            isEndDateAutoSync = false
            endDateDeltaDays = ChronoUnit.DAYS.between(currentStart, newEnd)
        }
    }

    /**
     * 필요할 경우 종료 날짜의 자동 동기화를 다시 활성화하는 메서드.
     * 이 경우 종료 날짜는 시작 날짜와 동일하게 설정됩니다.
     */
    fun enableEndDateAutoSync() {
        isEndDateAutoSync = true
        _selectedEndDate.value = _selectedStartDate.value
        endDateDeltaDays = 0
    }

    fun selectStartTime(newStartTime: LocalTime) {
        _selectedStartTime.value = newStartTime
        if (isEndTimeAutoSync) {
            // 종료 시간은 시작 시간 + 기존 시간 차이
            _selectedEndTime.value = newStartTime.plusHours(endTimeDeltaHours)
            Log.d("CalendarViewModel", "StartTime: $newStartTime, EndTime synced: ${_selectedEndTime.value}")
        }else {
            Log.d("CalendarViewModel", "StartTime: $newStartTime, Sync skipped, isEndTimeAutoSync: $isEndTimeAutoSync")
        }
    }


    fun selectEndTime(newEndTime : LocalTime) {
        _selectedEndTime.value = newEndTime
        isEndTimeAutoSync = false
        _selectedStartTime.value?.let { startTime ->
            endTimeDeltaHours = ChronoUnit.HOURS.between(startTime, newEndTime)
        }
        Log.d("CalendarViewModel", "EndTime set manually: $newEndTime, Sync disabled")
    }

    fun enableEndTimeAutoSync() {
        isEndTimeAutoSync = true
        _selectedEndTime.value = _selectedStartTime.value?.plusHours(endTimeDeltaHours)
            ?: LocalTime.now().plusHours(1)
        Log.d("CalendarViewModel", "EndTime auto-sync enabled: ${_selectedEndTime.value}")
    }

    fun resetForNewSchedule() {
        _selectedStartDate.value = LocalDate.now()
        _selectedEndDate.value = LocalDate.now()
        isEndDateAutoSync = true
        endDateDeltaDays = 0
        _selectedStartTime.value = LocalTime.now()
        _selectedEndTime.value = LocalTime.now().plusHours(1)
        isEndTimeAutoSync = true
        endTimeDeltaHours = 1
        _selectedLabel.value = CalendarLabel(R.color.emerald_green, "에메랄드 그린", true)
        alarmStartEnabled.value = false
        alarmTenMinuteEnabled.value = false
        alarmOneHourEnabled.value = false
        _alarmText.value = AlarmConstants.NO_ALARM
        Log.d("CalendarViewModel", "Reset for new schedule")
    }

    fun selectLabel(label: CalendarLabel) {
        _selectedLabel.value = label
    }

    fun setLabel(color : Int, colorName : String) {
        _selectedLabel.value = CalendarLabel(color, colorName)
    }

    fun setAlarm(alarm: String) {
        _alarmText.value = alarm
    }

    fun updateAlarmText() {
        val selected = mutableListOf<String>()
        if (alarmStartEnabled.value == true) selected.add(AlarmConstants.AT_START)
        if (alarmTenMinuteEnabled.value == true) selected.add(AlarmConstants.TEN_MINUTES_BEFORE)
        if (alarmOneHourEnabled.value == true) selected.add(AlarmConstants.ONE_HOUR_BEFORE)
        _alarmText.value = if (selected.isEmpty()) {
            AlarmConstants.NO_ALARM
        } else {
            selected.joinToString(separator = ", ") + " 알림이 설정되어 있습니다"
        }
    }

    private val _searchKeyword = MutableLiveData<String>("")

    val filteredSchedules : LiveData<Map<LocalDate, List<Schedule>>> =
        _searchKeyword.switchMap { keyword ->
            repository.searchSchedules(keyword).map { schedules ->
                schedules.groupBy { it.startDateTime?.toLocalDate() ?: LocalDate.now() }
            }
        }

    fun setSearchKeyword(keyword : String) {
        _searchKeyword.value = keyword
    }
}