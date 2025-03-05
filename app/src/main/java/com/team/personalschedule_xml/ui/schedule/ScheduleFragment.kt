package com.team.personalschedule_xml.ui.schedule

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.DaySize
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.databinding.FragmentScheduleBinding
import com.team.personalschedule_xml.repository.HolidayRepository
import com.team.personalschedule_xml.utils.DayViewContainer
import com.team.personalschedule_xml.utils.MonthHeaderViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val calendarViewModel: CalendarViewModel by activityViewModels()
    private val scheduleViewModel: ScheduleViewModel by viewModels {
        ScheduleViewModelFactory(HolidayRepository())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 캘린더의 시작 날짜와 종료 날짜를 설정합니다.
        val currentMonth = YearMonth.now()
        val currentYear = LocalDate.now().year
        val startMonth = YearMonth.of(currentYear - 1, 1)  // 2024년 1월
        val endMonth = YearMonth.of(currentYear + 1, 12)   // 2026년 12월
        scheduleViewModel.fetchHolidays(currentYear)

        // 공휴일 데이터가 변경될 때 캘린더를 갱신하도록 observe 추가
        scheduleViewModel.holidayMap.observe(viewLifecycleOwner) { holidayMap ->
            binding.calendarView.notifyCalendarChanged()
        }

        binding.calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthHeaderViewContainer> {
            override fun bind(container: MonthHeaderViewContainer, data: CalendarMonth) {
                val formatter = DateTimeFormatter.ofPattern("yy년 M월")
                container.binding.monthHeaderText.text = data.yearMonth.format(formatter)
            }

            override fun create(view: View): MonthHeaderViewContainer {
                return MonthHeaderViewContainer(view)
            }

        }

        binding.calendarView.apply {
            val currentMonth = YearMonth.now()
            val firstMonth = currentMonth.minusMonths(240)
            val lastMonth = currentMonth.plusMonths(240)
            val firstDayOfWeek = firstDayOfWeekFromLocale()

            setup(firstMonth, lastMonth, firstDayOfWeek)
            scrollToMonth(currentMonth)
        }

        binding.calendarView.doOnPreDraw {
            val firstVisibleMonth = binding.calendarView.findFirstVisibleDay()
            val lastVisibleMonth = binding.calendarView.findLastVisibleDay()
            Log.d("DisplayedMonth", "First visible month: ${firstVisibleMonth?.date}, Last visible month: ${lastVisibleMonth?.date}")
        }


        /*       binding.calendarView.setup(
                   startMonth,  // 시작 월 (YearMonth)
                   endMonth,   // 종료 월 (YearMonth)
                   DayOfWeek.SUNDAY              // 첫 요일
               )

               binding.calendarView.scrollToDate(LocalDate.now())*/

        // DayBinder를 통해 각 날짜 셀의 뷰를 생성하고 바인딩합니다.
        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun bind(
                container: DayViewContainer,
                data: com.kizitonwose.calendar.core.CalendarDay,
            ) {
                container.day = data
                container.binding.tvDay.text = data.date.dayOfMonth.toString()

                // 현재 날짜인 경우: tvDay와 dayLayout 모두에 current_day_curcle 적용
                if (data.date == LocalDate.now()) {
                    container.binding.tvDay.setBackgroundResource(R.drawable.current_day_curcle)
                    container.binding.tvDay.setTextColor(Color.WHITE)
                    // 만약 현재 날짜에 선택 배경도 적용하고 싶다면 (현재 날짜가 선택되었을 때)
                    if (calendarViewModel.selectedDate.value == data.date) {
                        container.binding.dayLayout.setBackgroundResource(R.drawable.selected_day_background)
                    } else {
                        container.binding.dayLayout.background = null
                    }
                }
                // 현재 날짜가 아니고 선택된 날짜인 경우: dayLayout에 selected_day_background 적용, tvDay의 배경은 초기화
                else if (calendarViewModel.selectedDate.value == data.date) {
                    container.binding.tvDay.background = null
                    container.binding.dayLayout.setBackgroundResource(R.drawable.selected_day_background)
                    container.binding.tvDay.setTextColor(Color.BLACK)
                }
                // 그 외의 경우: 모든 배경 초기화
                else {
                    container.binding.tvDay.background = null
                    container.binding.dayLayout.background = null
                    container.binding.tvDay.setTextColor(Color.BLACK)
                }

                // 공휴일 라벨 처리 (날짜 키 비교)
        /*        val lookupDate = if (data.position == com.kizitonwose.calendar.core.DayPosition.MonthDate) {
                    data.date.plusMonths(1)
                } else {
                    data.date
                }*/
                val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                val key = data.date.format(formatter)
                val holidayName = scheduleViewModel.holidayMap.value?.entries?.find {
                    it.key.format(formatter) == key
                }?.value

                //Log.d("ScheduleFragment", "Binding lookup date: $key, holiday: $holidayName")

                if (holidayName != null) {
                    container.binding.tvLabel.visibility = View.VISIBLE
                    container.binding.tvLabel.text = holidayName
                } else {
                    container.binding.tvLabel.visibility = View.GONE
                }

                // 클릭 시 ViewModel의 선택 상태 업데이트 (이후 전체 달력 셀 재바인딩)
                container.view.setOnClickListener {
                    if(calendarViewModel.selectedDate.value == data.date){
                        showBottomSheetFragment(data.date)
                    }else{
                        calendarViewModel.selectDate(data.date)
                    }
                    Log.d("CurrentDate", data.date.toString())
                }
            }

            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view)
            }
        }




        // ViewModel의 선택된 날짜 변화를 관찰하여 UI 업데이트 (필요 시)
        calendarViewModel.selectedDate.observe(viewLifecycleOwner) { selectedDate ->
            // 선택된 날짜가 변경되면 캘린더를 갱신하여 강조 표시 업데이트
            binding.calendarView.notifyCalendarChanged()
        }
    }

    private fun showBottomSheetFragment(data: LocalDate) {
        val bottomSheet = ScheduleBottomSheetFragment.newInstance(data)
        bottomSheet.show(childFragmentManager, "ScheduleBottomSheetFragment")
        Log.d("showBottomSheetFragment", "BottomSheet tag: ScheduleBottomSheetFragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
