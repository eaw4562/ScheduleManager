package com.team.personalschedule_xml.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.data.model.CalendarLabel
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class CalendarViewModel : ViewModel() {
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
    private var isEndTimeAutoSync = true
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
    private val _alarmText = MutableLiveData<String>("알림 없음")
    val alarmText: LiveData<String> get() = _alarmText


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

    fun selectStartTime(newStartTime : LocalTime) {
        _selectedStartTime.value = newStartTime
        if (isEndTimeAutoSync) {
            _selectedStartTime.value = newStartTime.plusHours(endTimeDeltaHours)
        }
    }

    fun selectEndTime(newEndTime : LocalTime) {
        _selectedEndTime.value = newEndTime
        isEndTimeAutoSync = false
        _selectedStartTime.value?.let { startTime ->
            endTimeDeltaHours = ChronoUnit.HOURS.between(startTime, newEndTime)
        }
    }

    fun enableEndTimeAutoSync() {
        isEndDateAutoSync = true
        _selectedEndTime.value = _selectedStartTime.value?.plusHours(endTimeDeltaHours)
            ?: LocalTime.now().plusHours(1)
    }

    fun selectLabel(label: CalendarLabel) {
        _selectedLabel.value = label
    }

    fun updateAlarmText() {
        val selected = mutableListOf<String>()
        if (alarmStartEnabled.value == true) selected.add("시작")
        if (alarmTenMinuteEnabled.value == true) selected.add("10분 전")
        if (alarmOneHourEnabled.value == true) selected.add("1시간 전")
        _alarmText.value = if (selected.isEmpty()) {
            "알림 없음"
        } else {
            selected.joinToString(separator = ", ") + " 알림이 설정되어 있습니다"
        }
    }
}