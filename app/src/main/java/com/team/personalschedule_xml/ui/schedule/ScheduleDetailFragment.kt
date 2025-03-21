package com.team.personalschedule_xml.ui.schedule

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.data.model.Schedule
import com.team.personalschedule_xml.data.repository.ScheduleRepository
import com.team.personalschedule_xml.databinding.LayoutScheduleDetailBinding
import com.team.personalschedule_xml.ui.common.viewmodel.CalendarViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleDetailFragment : Fragment() {

    private var _binding: LayoutScheduleDetailBinding? = null
    private val binding get() = _binding!!

    private val calendarViewModel: CalendarViewModel by activityViewModels()
    private lateinit var scheduleRepository: ScheduleRepository
    private var scheduleId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleId = ScheduleDetailFragmentArgs.fromBundle(requireArguments()).scheduleId
        Log.d("ScheduleDetailFragment", "Schedule ID: $scheduleId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutScheduleDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        scheduleRepository = ScheduleRepository(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (scheduleId != -1) {
            loadScheduleDetails()
        } else {
            Toast.makeText(requireContext(), "유효하지 않은 일정입니다.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        binding.scheduleDetailMoreIBtn.setOnClickListener { view ->
            showPopupMenu(view)
        }

        binding.scheduleDetailBackIBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun loadScheduleDetails() {
        lifecycleScope.launch {
            val schedule = scheduleRepository.getScheduleById(scheduleId)
            if (schedule != null) {
                binding.schedule = schedule
                Log.d("ScheduleDetailFragment", "Loaded schedule: ${schedule.title}")
            } else {
                Log.e("ScheduleDetailFragment", "Schedule not found for ID: $scheduleId")
                Toast.makeText(requireContext(), "일정을 찾을 수 없습니다.", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.detail_more_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    val action = ScheduleDetailFragmentDirections
                        .actionScheduleDetailFragmentToWriteFragment(scheduleId)
                    findNavController().navigate(action)
                    true
                }

                R.id.action_share -> {
                    // 공유 기능 구현
                    true
                }

                R.id.action_delete -> {
                    lifecycleScope.launch {
                        scheduleRepository.deleteSchedule(scheduleId)
                        calendarViewModel.loadSchedules()
                        withContext(Dispatchers.Main) {
                            findNavController().popBackStack()
                        }
                    }
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
