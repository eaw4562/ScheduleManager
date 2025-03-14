package com.team.personalschedule_xml.ui.schedule

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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

        calendarViewModel.initRepository(requireContext())
        calendarViewModel.loadSchedules()

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

                container.binding.scheduleContainer.removeAllViews()

                // 마진을 dp 단위로 지정하고 픽셀로 변환 (예: 상하 4dp)
                val marginInPx = (4 * container.view.context.resources.displayMetrics.density).toInt()

                // 현재 날짜인 경우: tvDay와 dayLayout 모두에 current_day_curcle 적용
                if (data.date == LocalDate.now()) {
                    container.binding.tvDay.setBackgroundResource(R.drawable.current_day_curcle)
                    container.binding.tvDay.setTextColor(Color.WHITE)
                    // 만약 현재 날짜에 선택 배경도 적용하고 싶다면 (현재 날짜가 선택되었을 때)
                    if (calendarViewModel.selectedStartDate.value == data.date) {
                        container.binding.dayLayout.setBackgroundResource(R.drawable.selected_day_background)
                    } else {
                        container.binding.dayLayout.background = null
                    }
                }
                // 현재 날짜가 아니고 선택된 날짜인 경우: dayLayout에 selected_day_background 적용, tvDay의 배경은 초기화
                else if (calendarViewModel.selectedStartDate.value == data.date) {
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

                if (!holidayName.isNullOrEmpty()) {
                    // 공휴일이 있으면 별도의 TextView로 추가
                    val holidayTextView = TextView(container.view.context).apply {
                        text = holidayName
                        textSize = 12f
                        setTextColor(Color.WHITE)
                        setBackgroundColor(Color.RED) // 필요에 따라 drawable이나 다른 색상 적용
                        setPadding(4, 2, 4, 2)
                        // 한 줄로 표시하고, 텍스트가 길면 말줄임 처리
                        maxLines = 1
                        ellipsize = TextUtils.TruncateAt.END
                    }

                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    lp.setMargins(0,marginInPx,0,marginInPx)
                    holidayTextView.layoutParams = lp

                    container.binding.scheduleContainer.addView(holidayTextView)
                }

                // 2. 해당 날짜에 저장된 스케줄들 처리 (calendarViewModel의 scheduleMap 사용)
                val schedulesForDay = calendarViewModel.scheduleMap.value?.get(data.date)
                if (!schedulesForDay.isNullOrEmpty()) {
                    schedulesForDay.forEach { schedule ->
                        val scheduleTextView = TextView(container.view.context).apply {
                            text = schedule.title
                            textSize = 12f
                            setTextColor(Color.WHITE)
                            // labelColor는 리소스 ID이므로 ContextCompat를 사용해 실제 색상으로 변환
                            setBackgroundColor(
                                androidx.core.content.ContextCompat.getColor(
                                    container.view.context,
                                    schedule.labelColor
                                )
                            )
                            setPadding(4, 2, 4, 2)

                            // 한 줄로 표시하고, 텍스트가 길면 말줄임 처리
                            maxLines = 1
                            ellipsize = TextUtils.TruncateAt.END
                        }
                        val lp = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        lp.setMargins(0, marginInPx, 0, marginInPx)
                        scheduleTextView.layoutParams = lp

                        container.binding.scheduleContainer.addView(scheduleTextView)
                    }
                }

                // 클릭 시 ViewModel의 선택 상태 업데이트 (이후 전체 달력 셀 재바인딩)
                container.view.setOnClickListener {
                    if(calendarViewModel.selectedStartDate.value == data.date){
                        showBottomSheetFragment(data.date)
                    }else{
                        calendarViewModel.selectStartDate(data.date)
                    }
                    Log.d("CurrentDate", data.date.toString())
                }
            }

            override fun create(view: View): DayViewContainer {
                return DayViewContainer(view)
            }
        }




        // ViewModel의 선택된 날짜 변화를 관찰하여 UI 업데이트 (필요 시)
        calendarViewModel.selectedStartDate.observe(viewLifecycleOwner) { selectedDate ->
            // 선택된 날짜가 변경되면 캘린더를 갱신하여 강조 표시 업데이트
            binding.calendarView.notifyCalendarChanged()
        }

        // scheduleMap이 변경되면 달력을 갱신하여 UI 업데이트
        calendarViewModel.scheduleMap.observe(viewLifecycleOwner) {
            Log.d("ScheduleFragment", "ScheduleMap observer triggered: $it")
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
