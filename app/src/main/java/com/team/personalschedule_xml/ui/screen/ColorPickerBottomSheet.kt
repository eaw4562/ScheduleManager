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
import com.team.personalschedule_xml.databinding.FragmentColorPickerBinding
import com.team.personalschedule_xml.ui.schedule.CalendarViewModel
import com.team.personalschedule_xml.utils.CalendarLabelAdapter

class ColorPickerBottomSheet : BottomSheetDialogFragment() {
    private var _binding : FragmentColorPickerBinding? = null
    private val binding get() = _binding!!

    private val calendarViewModel : CalendarViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentColorPickerBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val labels = mutableListOf(
            CalendarLabel(color = R.color.emerald_green, colorName = "에메랄드 그린"),
            CalendarLabel(color = R.color.modern_color, colorName = "모던 사이언"),
            CalendarLabel(color = R.color.deepskyblue, colorName = "딥 스카이블루"),
            CalendarLabel(color = R.color.pastel_brown, colorName = "파스텔 브라운"),
            CalendarLabel(color = R.color.midnight_blue, colorName = "미드나잇 블루"),
            CalendarLabel(color = R.color.apple_red, colorName = "애플 레드"),
            CalendarLabel(color = R.color.french_rose, colorName = "프렌치 로즈"),
            CalendarLabel(color = R.color.crayola_pink, colorName = "코랄 핑크"),
            CalendarLabel(color = R.color.bright_orange, colorName = "브라이트 오렌지"),
            CalendarLabel(color = R.color.soft_violet, colorName = "소프트 바이올렛"),
        )

        calendarViewModel.selectedLabel.value?.let { selected ->
            labels.forEach { it.isSelected = it.color == selected.color }
        }

        val adapter = CalendarLabelAdapter(labels) { selectedLabel ->
            calendarViewModel.selectLabel(selectedLabel)
            dismiss()
        }
        binding.colorRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.colorRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}