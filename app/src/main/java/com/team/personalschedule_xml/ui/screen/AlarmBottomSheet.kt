package com.team.personalschedule_xml.ui.screen

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.data.model.CalendarLabel
import com.team.personalschedule_xml.databinding.FragmentAlarmBottomSheetBinding
import com.team.personalschedule_xml.databinding.FragmentColorPickerBinding
import com.team.personalschedule_xml.ui.schedule.CalendarViewModel
import com.team.personalschedule_xml.utils.CalendarLabelAdapter

class AlarmBottomSheet : BottomSheetDialogFragment() {
    private var _binding : FragmentAlarmBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val calendarViewModel : CalendarViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.alarmStartBottomLayout.setOnClickListener {
            binding.alarmStartBottomCheck.isChecked = !binding.alarmStartBottomCheck.isChecked
            updateAlarmMessage()
        }

        binding.alarmTenMinuteBottomLayout.setOnClickListener {
            binding.alarmTenMinuteBottomCheck.isChecked = !binding.alarmTenMinuteBottomCheck.isChecked
            updateAlarmMessage()
        }

        binding.alarmOneHourBottomCheck.setOnClickListener {
            binding.alarmOneHourBottomCheck.isChecked = !binding.alarmOneHourBottomCheck.isChecked
            updateAlarmMessage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 메시지 업데이트 함수
    private fun updateAlarmMessage() {
        val sb = StringBuilder()

        // 순서는 항상 "시작", "10분 전", "1시간 전"
        if (binding.alarmStartBottomCheck.isChecked) {
            sb.append("시작")
        }
        if (binding.alarmTenMinuteBottomCheck.isChecked) {
            if (sb.isNotEmpty()) sb.append(", ")
            sb.append("10분 전")
        }
        if (binding.alarmOneHourBottomCheck.isChecked) {
            if (sb.isNotEmpty()) sb.append(", ")
            sb.append("1시간 전")
        }
        if (sb.isNotEmpty()) {
            sb.append("에 알림이 도착합니다.")
        }else{
            sb.append("알림이 설정되어 있지 않습니다.")
        }
        binding.alarmBottomTitleTextView.text = sb.toString()
    }
}