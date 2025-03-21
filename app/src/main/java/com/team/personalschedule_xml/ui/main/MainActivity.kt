package com.team.personalschedule_xml.ui.main

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.AlarmManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.team.personalschedule_xml.R
import com.team.personalschedule_xml.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val requestExactAlarmPermission =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            checkExactAlarmPermission()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_main)
        initBinding()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initNavigation()
        createNotificationChannel()

        intent?.let {
            val scheduleId = it.getIntExtra("scheduleId", -1)
            val navigateTo = it.getStringExtra("navigateTo")
            if (scheduleId != -1 && navigateTo == "ScheduleDetail") {
                val navController = findNavController(R.id.nav_host)
                val bundle = Bundle().apply {
                    putInt("scheduleId", scheduleId)
                }
                navController.navigate(R.id.action_global_scheduleDetailFragment, bundle)
            }
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_bar)

        binding.navBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.scheduleFragment -> {
                    navController.navigate(R.id.scheduleFragment)
                    true
                }
                R.id.memoFragment -> {
                    navController.navigate(R.id.memoFragment)
                    true
                }
                R.id.writeBottomSheet -> {
                    navController.navigate(R.id.writeFragment)
                    true
                }
                R.id.notificationFragment -> {
                    navController.navigate(R.id.notificationFragment)
                    true
                }
                R.id.settingsFragment -> {
                    navController.navigate(R.id.settingsFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun initBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initNavigation() {
        NavigationUI.setupWithNavController(binding.navBar, findNavController(R.id.nav_host))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "schedule_channel",
                "Schedule Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!AlarmManagerCompat.canScheduleExactAlarms(alarmManager)) {
                val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val hasRequested = prefs.getBoolean("has_requested_exact_alarm", false)
                if (!hasRequested) {
                    showPermissionRequestDialog()
                    prefs.edit().putBoolean("has_requested_exact_alarm", true).apply()
                }
            }
        }
    }

    private fun showPermissionRequestDialog() {
        AlertDialog.Builder(this)
            .setTitle("정확한 알림 권한 요청")
            .setMessage("일정 알림을 정확한 시간에 받으려면 '정확한 알림' 권한이 필요합니다. 설정 화면에서 권한을 허용하시겠습니까?")
            .setPositiveButton("허용") { _, _ ->
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                requestExactAlarmPermission.launch(intent)
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
                // 권한이 없으면 알림 관련 기능이 제한됨을 사용자에게 알림 (선택 사항)
            }
            .setCancelable(false)
            .show()
    }

    /*private fun showWriteBottomSheet() {
        if (supportFragmentManager.findFragmentByTag("WriteBottomSheet") == null){
            val bottomSheet = WriteBottomSheet()
            bottomSheet.show(supportFragmentManager, "WriteBottomSheet")
        }
    }*/
}