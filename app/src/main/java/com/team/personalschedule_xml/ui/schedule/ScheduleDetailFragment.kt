package com.team.personalschedule_xml.ui.schedule

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.team.personalschedule_xml.data.model.Schedule
import com.team.personalschedule_xml.databinding.FragmentScheduleDetailBinding

class ScheduleDetailFragment : Fragment() {

    private var _binding : FragmentScheduleDetailBinding? = null
    private val binding get() = _binding!!

    private var schedule: Schedule? = null
    private val calendarViewModel : CalendarViewModel by activityViewModels()

    private var scheduleId : Int = 0

    /*companion object {
        private const val ARG_SCHEDULE_ID = "arg_schedule_id"
        fun newInstance(scheduleId: Int): ScheduleDetailFragment {
            val fragment = ScheduleDetailFragment()
            val args = Bundle()
            args.putInt(ARG_SCHEDULE_ID, scheduleId)
            fragment.arguments = args
            return fragment
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleId = ScheduleDetailFragmentArgs.fromBundle(requireArguments()).scheduleId
        Log.d("ScheduleDetailSheet", "Schedule ID: $scheduleId")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentScheduleDetailBinding.inflate(inflater, container, false)
       binding.lifecycleOwner = viewLifecycleOwner

       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendarViewModel.scheduleMap.observe(viewLifecycleOwner) { scheduleMap ->
            schedule = scheduleMap?.values?.flatten()?.find { it.id == scheduleId }
            binding.schedule = schedule
        }
        val schedule = calendarViewModel.scheduleMap.value?.values?.flatten()?.find { it.id == scheduleId }
    }
}