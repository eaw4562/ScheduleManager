package com.team.personalschedule_xml.ui.write

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.team.personalschedule_xml.databinding.LayoutAlarmBottomSheetBinding
import com.team.personalschedule_xml.ui.common.viewmodel.CalendarViewModel

class AlarmBottomSheet : BottomSheetDialogFragment() {
    private var _binding : LayoutAlarmBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val calendarViewModel : CalendarViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutAlarmBottomSheetBinding.inflate(inflater, container, false)
        binding.viewModel = calendarViewModel
        binding.lifecycleOwner = viewLifecycleOwner
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

        binding.alarmOneHourBottomLayout.setOnClickListener {
            binding.alarmOneHourBottomCheck.isChecked = !binding.alarmOneHourBottomCheck.isChecked
            updateAlarmMessage()
        }

        // Observer를 통해 체크박스 상태를 UI에 반영
        calendarViewModel.alarmStartEnabled.observe(viewLifecycleOwner) { isChecked ->
            binding.alarmStartBottomCheck.isChecked = isChecked
            updateAlarmMessage()
        }
        calendarViewModel.alarmTenMinuteEnabled.observe(viewLifecycleOwner) { isChecked ->
            binding.alarmTenMinuteBottomCheck.isChecked = isChecked
            updateAlarmMessage()
        }
        calendarViewModel.alarmOneHourEnabled.observe(viewLifecycleOwner) { isChecked ->
            binding.alarmOneHourBottomCheck.isChecked = isChecked
            updateAlarmMessage()
        }

        calendarViewModel.alarmText.observe(viewLifecycleOwner) { text ->
            binding.alarmBottomTitleTextView.text = text
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // dismiss 시 최종 메시지 업데이트
        calendarViewModel.updateAlarmText() // ViewModel 내에서 LiveData를 업데이트하도록 수정할 수 있음.
    }

    // 메시지 업데이트 함수
    private fun updateAlarmMessage() {
        val sb = StringBuilder()

        // 순서는 항상 "시작", "10분 전", "1시간 전"x
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}