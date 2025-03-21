package com.team.personalschedule_xml.ui.schedule

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.data.model.Schedule
import com.team.personalschedule_xml.databinding.FragmentScheduleBottomSheetFragmentBinding
import com.team.personalschedule_xml.utils.ScheduleItemAdapter
import java.time.LocalDate

class ScheduleBottomSheetFragment : Fragment() {

    private var _binding: FragmentScheduleBottomSheetFragmentBinding? = null
    private val binding get() = _binding!!

    private val calendarViewModel : CalendarViewModel by activityViewModels()

    private var selectedDate : LocalDate? = null

    companion object {
        private const val ARG_DATE = "arg_date"
        fun newInstance(data : LocalDate) : ScheduleBottomSheetFragment {
            val fragment = ScheduleBottomSheetFragment()
            val args = Bundle()
            args.putString(ARG_DATE, data.toString())
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = ScheduleBottomSheetFragmentArgs.fromBundle(requireArguments())
        selectedDate = LocalDate.parse(args.selectedDate)
      /*  arguments?.getString(ARG_DATE)?.let {
            selectedDate = LocalDate.parse(it)
        }*/
        Log.d("ScheduleBottomSheet", "Selected date : $selectedDate")
    }

   /* override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext())
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                val behavior = BottomSheetBehavior.from(it)
                behavior.skipCollapsed = true
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
            return dialog
    }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("BottomSheet", "show")
        _binding = FragmentScheduleBottomSheetFragmentBinding.inflate(inflater, container, false)

        binding.viewModel = calendarViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        calendarViewModel.initRepository(requireContext())
        calendarViewModel.loadSchedules()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet = view.findViewById<View>(R.id.bottom_sheet_content)
        val behavior = BottomSheetBehavior.from(bottomSheet).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            skipCollapsed = true // COLLAPSED 상태 허용
        }

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    findNavController().popBackStack()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d("BottomSheet", "Slide offset: $slideOffset")
                binding.scheduleBottomHeaderLayout.alpha = slideOffset
            }
        })

        calendarViewModel.scheduleMap.observe(viewLifecycleOwner) { scheduleMap ->
            // 선택된 날짜에 해당하는 스케줄 리스트를 새로 가져와서 어댑터를 갱신
            val schedulesForDay = selectedDate?.let { date ->
                scheduleMap[date] ?: emptyList()
            } ?: emptyList()

            if (schedulesForDay.isNotEmpty()) {
                binding.scheduleBottomRecycler.visibility = View.VISIBLE
                binding.scheduleBottomNoScheduleTextView.visibility = View.GONE

                val adapter = ScheduleItemAdapter(schedulesForDay) {selectedSchedule ->
                    showScheduleDetailFragment(selectedSchedule)
                }
                binding.scheduleBottomRecycler.layoutManager = LinearLayoutManager(requireContext())
                binding.scheduleBottomRecycler.adapter = adapter
            } else {
                binding.scheduleBottomRecycler.visibility = View.GONE
                binding.scheduleBottomNoScheduleTextView.visibility = View.VISIBLE
                binding.scheduleBottomNoScheduleTextView.text = "해당 날짜의 스케줄이 없습니다."
            }
            Log.d("ScheduleBottomSheet", "Updated schedules for date $selectedDate: $schedulesForDay")
        }

        binding.scheduleBottomAddIbtn.setOnClickListener {
            findNavController().navigate(R.id.action_scheduleBottomSheetFragment_to_writeFragment)
        }

    }

    private fun showScheduleDetailFragment(schedule: Schedule) {
        val action = ScheduleBottomSheetFragmentDirections
            .actionScheduleBottomSheetFragmentToScheduleDetailFragment(schedule.id)
        findNavController().navigate(action)
        Log.d("ScheduleBottomSheet", "Navigating with schedule id: ${schedule.id}")
    }



    override fun onStart() {
        super.onStart()
        /*dialog?.let { d ->
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }*/
    }

}
