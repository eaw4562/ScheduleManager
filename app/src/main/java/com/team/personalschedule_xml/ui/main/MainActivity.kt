package com.team.personalschedule_xml.ui.main

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            if (isGranted) {
                Log.d("MainActivity", "POST_NOTIFICATIONS permission granted")
                prefs.edit().putInt("notification_denial_count", 0).apply()
            } else {
                Log.d("MainActivity", "POST_NOTIFICATIONS permission denied")
                val denialCount = prefs.getInt("notification_denial_count", 0) + 1
                prefs.edit().putInt("notification_denial_count", denialCount).apply()
                handlePermissionDenied("notification", denialCount)
            }
        }

    /*private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Log.d("MainActivity", "POST_NOTIFICATIONS permission denied")
                showNotificationPermissionDeniedDialog()
            } else {
                Log.d("MainActivity", "POST_NOTIFICATIONS permission granted")
            }
        }*/

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
        checkExactAlarmPermission()
        checkNotificationPermission()
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
            val canScheduleExact = AlarmManagerCompat.canScheduleExactAlarms(alarmManager)
            Log.d("MainActivity", "checkExactAlarmPermission called, canScheduleExactAlarms: $canScheduleExact")
            if (!canScheduleExact) {
                val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val hasRequested = prefs.getBoolean("has_requested_exact_alarm", false)
                Log.d("MainActivity", "hasRequestedExactAlarm: $hasRequested")
                if (!hasRequested) {
                    showPermissionDialog("exact_alarm")
                    prefs.edit().putBoolean("has_requested_exact_alarm", true).apply()
                } else {
                    val denialCount = prefs.getInt("exact_alarm_denial_count", 0)
                    handlePermissionDenied("exact_alarm", denialCount)
                }
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionStatus = checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            val isGranted = permissionStatus == PackageManager.PERMISSION_GRANTED
            Log.d("MainActivity", "checkNotificationPermission called, POST_NOTIFICATIONS granted: $isGranted")
            if (!isGranted) {
                val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val hasRequested = prefs.getBoolean("has_requested_notifications", false)
                Log.d("MainActivity", "hasRequestedNotifications: $hasRequested")
                if (!hasRequested) {
                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                        showPermissionDialog("notification_rationale")
                    } else {
                        requestNotificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                    prefs.edit().putBoolean("has_requested_notifications", true).apply()
                } else {
                    val denialCount = prefs.getInt("notification_denial_count", 0)
                    handlePermissionDenied("notification", denialCount)
                }
            }
        }
    }

    private fun showPermissionDialog(permissionType: String) {
        Log.d("MainActivity", "Showing permission dialog for $permissionType")
        val (title, message, positiveAction) = when (permissionType) {
            "exact_alarm" -> Triple(
                "정확한 알림 권한 요청",
                "일정 알림을 정확한 시간에 받으려면 '정확한 알림' 권한이 필요합니다. 설정 화면에서 권한을 허용하시겠습니까?",
                {
                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    requestExactAlarmPermission.launch(intent)
                    getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        .edit().putInt("exact_alarm_denial_count", 0).apply()
                }
            )
            "notification_rationale" -> Triple(
                "알림 권한 필요",
                "일정 알림을 받으려면 알림 권한이 필요합니다. 권한을 허용해 주세요.",
                { requestNotificationPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS) }
            )
            else -> return
        }

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(if (permissionType == "exact_alarm") "허용" else "확인") { _, _ ->
                positiveAction()
            }
            .setNegativeButton("거부") { dialog, _ ->
                dialog.dismiss()
                val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val key = if (permissionType == "exact_alarm") "exact_alarm_denial_count" else "notification_denial_count"
                val denialCount = prefs.getInt(key, 0) + 1
                prefs.edit().putInt(key, denialCount).apply()
                handlePermissionDenied(
                    if (permissionType == "exact_alarm") "exact_alarm" else "notification",
                    denialCount
                )
            }
            .setCancelable(false)
            .show()
    }

    private fun handlePermissionDenied(permissionType: String, denialCount: Int) {
        val shouldShowRationale = permissionType == "notification" &&
                !shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)
        if (shouldShowRationale || denialCount >= 2) {
            Log.d("MainActivity", "$permissionType permission denied $denialCount times or 'Don't ask again' checked, showing settings dialog")
            val (title, message, intent) = when (permissionType) {
                "exact_alarm" -> Triple(
                    "정확한 알림 권한 필요",
                    "정확한 알림 권한이 거부되었습니다. 설정에서 권한을 허용하지 않으면 알림이 작동하지 않습니다.",
                    Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                )
                "notification" -> Triple(
                    "알림 권한 필요",
                    "알림 권한이 거부되었습니다. 설정에서 권한을 허용하지 않으면 알림을 받을 수 없습니다.",
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    }
                )
                else -> return
            }

            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("설정으로 이동") { _, _ ->
                    startActivity(intent)
                }
                .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
                .setCancelable(false)
                .show()
        }
    }

    /*private fun showWriteBottomSheet() {
        if (supportFragmentManager.findFragmentByTag("WriteBottomSheet") == null){
            val bottomSheet = WriteBottomSheet()
            bottomSheet.show(supportFragmentManager, "WriteBottomSheet")
        }
    }*/
}