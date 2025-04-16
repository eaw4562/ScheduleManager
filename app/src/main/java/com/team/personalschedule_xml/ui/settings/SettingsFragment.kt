package com.team.personalschedule_xml.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.databinding.LayoutSettingsBinding
import com.team.personalschedule_xml.ui.schedule.ScheduleBottomSheetFragmentDirections
import com.team.personalschedule_xml.utils.PreferencesUtil
import com.team.personalschedule_xml.utils.interfaces.OnScheduleModeChangedListener

class SettingsFragment : Fragment() {

    private lateinit var modeListener : OnScheduleModeChangedListener

    private var _binding : LayoutSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = LayoutSettingsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnScheduleModeChangedListener) {
            modeListener = context
        } else {
            throw RuntimeException("$context must implement OnScheduleModeChangedListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selected = PreferencesUtil.getStartDestination(requireContext())
        when (selected) {
            "month" -> binding.radioMonth.isChecked = true
            "week" -> binding.radioWeek.isChecked = true
            "list" -> binding.radioList.isChecked = true
        }

        binding.layoutMonth.setOnClickListener {
            binding.radioMonth.isChecked = true
            PreferencesUtil.setStartDestination(requireContext(), "month")
            modeListener.onScheduleModeChange("month")
        }

        binding.layoutWeek.setOnClickListener {
            binding.radioWeek.isChecked = true
            PreferencesUtil.setStartDestination(requireContext(), "week")
            modeListener.onScheduleModeChange("week")
        }

        binding.layoutList.setOnClickListener {
            binding.radioList.isChecked = true
            PreferencesUtil.setStartDestination(requireContext(), "list")
            modeListener.onScheduleModeChange("list")
        }

        binding.settingSearchLayout.setOnClickListener {
            val action = SettingsFragmentDirections
                .actionSettingsFragmentToScheduleSearchFragment()
            findNavController().navigate(action)
        }
    }
}