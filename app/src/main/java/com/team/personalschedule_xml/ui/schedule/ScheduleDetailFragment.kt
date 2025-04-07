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
import com.team.personalschedule_xml.data.model.Memo
import com.team.personalschedule_xml.data.model.Schedule
import com.team.personalschedule_xml.data.repository.ScheduleRepository
import com.team.personalschedule_xml.databinding.LayoutScheduleDetailBinding
import com.team.personalschedule_xml.ui.common.viewmodel.CalendarViewModel
import com.team.personalschedule_xml.ui.memo.MemoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleDetailFragment : Fragment() {

    private var _binding: LayoutScheduleDetailBinding? = null
    private val binding get() = _binding!!

    private val calendarViewModel: CalendarViewModel by activityViewModels()
    private lateinit var scheduleRepository: ScheduleRepository
    private var scheduleId: Int = -1
    private var isMemo : Boolean = false
    private val memoViewModel : MemoViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleId = ScheduleDetailFragmentArgs.fromBundle(requireArguments()).scheduleId
        isMemo = ScheduleDetailFragmentArgs.fromBundle(requireArguments()).isMemo
        Log.d("ScheduleDetailFragment", "Schedule ID: $scheduleId")
        Log.d("ScheduleDetailFragment", "Schedule isMemo: $isMemo")
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

        if (isMemo) {
            binding.scheduleDetailMoreLayout.visibility = View.GONE
            memoViewModel.memoList.observe(viewLifecycleOwner) { memos ->
                val memo = memos.find { it.id == scheduleId } ?: return@observe
                Log.d("ScheduleDetailFragment : ", "Memo -> " + memo.memo)
                bindMemoToUI(memo)
            }
        } else {
            binding.scheduleDetailMoreLayout.visibility = View.VISIBLE
            calendarViewModel.scheduleMap.observe(viewLifecycleOwner) { scheduleMap ->
                val schedule = scheduleMap.values.flatten().find { it.id == scheduleId }
                schedule?.let {
                   bindScheduleToUI(schedule)
                }
            }
        }

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

    private fun bindMemoToUI(memo : Memo) {
        binding.scheduleDetailTitle.text = memo.title
        binding.scheduleDetailColorLabel.text = memo.labelName
        binding.scheduleDetailContent.text = memo.memo
    }

    private fun bindScheduleToUI(schedule: Schedule) {
        binding.scheduleDetailTitle.text = schedule.title
        binding.scheduleDetailColorLabel.text = schedule.labelName
        binding.scheduleDetailContent.text = schedule.memo
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
                    lifecycleScope.launch {
                        val schedule = scheduleRepository.getScheduleById(scheduleId)
                        schedule?.let {
                            val bottomSheet = CopyScheduleBottomSheet(it) {

                            }
                            bottomSheet.show(parentFragmentManager, "CopyScheduleBottomSheet")
                        }
                    }
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
