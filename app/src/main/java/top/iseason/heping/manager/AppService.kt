package top.iseason.heping.manager

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import top.iseason.heping.MainActivity
import top.iseason.heping.R
import top.iseason.heping.manager.ModelManager.queryAppUseTime
import top.iseason.heping.utils.Util
import java.util.*


class AppService : Service() {
    private var isRunning: Boolean = false
    private var isOpening: Boolean = false
    private var lastOpenTime: Long = System.currentTimeMillis()
    private var windowManager = FloatWindowManager()
    private var isAppLimit = false
    private var isTiredLimit = false
    private var isNightLimit = false
    private var isFocusLimit = false
    private val screenChecker = Thread {
        while (true) {
            val interactive = ModelManager.isInteractive()
            if (interactive != isOpening) {
                val time = System.currentTimeMillis()
                if (!interactive) {
                    EventManager.putEvent(Pair(lastOpenTime, System.currentTimeMillis()))
                } else {
                    lastOpenTime = time
                    EventManager.tempUnix = time
                }
            }
            isOpening = interactive
            Thread.sleep(1000)
        }
    }

    private val timeLimiter = Thread {
        while (true) {
            Thread.sleep(1000)
            if (isNightLimit || isTiredLimit) {
                isAppLimit = false
                continue
            }
            val packageName = EventManager.currentEvent.packageName
            val int = ConfigManager.getInt("TimeLimit-$packageName")
            if (int != 0) {
                val queryAppUseTime = queryAppUseTime(packageName)
                if (queryAppUseTime.second > int * 60000L) {
                    windowManager.setText(
                        "${queryAppUseTime.first} 今日已使用",
                        Util.longTimeFormatDetail2(queryAppUseTime.second), "达到使用限额"
                    )
                    isAppLimit = true
                    windowManager.showWindow()
                } else {
                    isAppLimit = false
                    if (!(isTiredLimit || isNightLimit || isFocusLimit))
                        windowManager.hideWindow()
                }
            } else {
                isAppLimit = false
                if (!(isTiredLimit || isNightLimit || isFocusLimit))
                    windowManager.hideWindow()
            }
        }
    }
    private val tiredLimiter = Thread {
        while (true) {
            Thread.sleep(1000)
            if (isNightLimit) {
                isTiredLimit = false
                continue
            }
            val int = ConfigManager.getInt("Health-TiredRecord-Tip")
            if (int != 0) {
                val useTime = System.currentTimeMillis() - EventManager.tempUnix
                isTiredLimit = if (useTime > int * 60000L) {
                    windowManager.setText(
                        "已经连续使用",
                        Util.longTimeFormatDetail2(useTime), "放下手机休息一会儿吧~"
                    )
                    windowManager.showWindow()
                    true
                } else {
                    if (!(isAppLimit || isNightLimit || isFocusLimit))
                        windowManager.hideWindow()
                    false
                }
            } else {
                isTiredLimit = false
                if (!(isAppLimit || isNightLimit || isFocusLimit))
                    windowManager.hideWindow()
            }
        }
    }
    private val nightLimiter = Thread {
        while (true) {
            Thread.sleep(1000)
            val isOpen = ConfigManager.getBoolean("Setting-SleepPlain")
            if (!isOpen) {
                isNightLimit = false
                if (!(isAppLimit || isTiredLimit || isFocusLimit))
                    windowManager.hideWindow()
                continue
            }
            val timeSet = ConfigManager.getString("Setting-SleepPlain-TimeSet")
            if (timeSet == null) {
                isNightLimit = false
                if (!(isAppLimit || isTiredLimit || isFocusLimit))
                    windowManager.hideWindow()
                continue
            }
            val split = timeSet.split(',')
            val fistHour = split[0].toInt()
            val fistMinute = split[1].toInt()
            var lastHour = split[2].toInt()
            val lastMinute = split[3].toInt()
            val sleepTimeStart = Calendar.getInstance().apply {
                if (fistHour >= 12 && fistHour > lastHour && lastHour > 0) {
                    set(Calendar.DATE, -1)
                }
                set(Calendar.HOUR_OF_DAY, fistHour)
                set(Calendar.MINUTE, fistMinute)
                set(Calendar.SECOND, 0)
            }
            val sleepTimeEnd = Calendar.getInstance().apply {
                if (lastHour == 0) lastHour = 24
                set(Calendar.HOUR_OF_DAY, lastHour)
                set(Calendar.MINUTE, lastMinute)
                set(Calendar.SECOND, 0)
            }
            val current = Calendar.getInstance()
            if (current.timeInMillis in sleepTimeStart.timeInMillis..sleepTimeEnd.timeInMillis) {
                windowManager.setText(
                    "已经深夜",
                    Util.longTimeFormatDetail2(current.timeInMillis - Util.getDate(0).timeInMillis),
                    "早点放下手机睡觉吧~"
                )
                isNightLimit = true
                windowManager.showWindow()
                continue
            }
            isNightLimit = false
            if (!(isAppLimit || isTiredLimit || isFocusLimit))
                windowManager.hideWindow()
        }
    }
    private val focusLimiter = Thread {
        while (true) {
            Thread.sleep(1000)
            if (isNightLimit || isTiredLimit || isAppLimit) {
                isFocusLimit = false
                continue
            }
            if (!focusTime.isFocusing) {
                if (!(isTiredLimit || isNightLimit || isAppLimit)) {
                    isFocusLimit = false
                    windowManager.hideWindow()
                }
                continue
            }
            val packageName = EventManager.currentEvent.packageName
            if (packageName != "top.iseason.heping" && packageName != null) {
                windowManager.setText(
                    "专注时间还有",
                    Util.longTimeFormatDetail2((focusTime.focusTime - focusTime.currentTime) * 1000L),
                    "放下手机继续坚持吧!"
                )
                isFocusLimit = true
                windowManager.showWindow()
            } else if (!(isTiredLimit || isNightLimit || isAppLimit)) {
                isFocusLimit = false
                windowManager.hideWindow()
            }
        }
    }

    class MyBinder(val appService: AppService) : Binder()

    private val binder: Binder = MyBinder(this)
    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate() {
        super.onCreate()
//        startForeground(NOTIFICATION_ID, createForegroundNotification())
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (isRunning) return super.onStartCommand(intent, flags, startId)
        screenChecker.start()
        EventManager.isInit = true
        EventManager.eventGetter.start()
        windowManager.init()
        nightLimiter.start()
        tiredLimiter.start()
        timeLimiter.start()
        focusLimiter.start()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            startForeground(NOTIFICATION_ID, createForegroundNotification())
        }
        isRunning = true
        return super.onStartCommand(intent, flags, startId)
    }

    val focusTime = FocusTimer()
    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
    }

    //创建前台通知
    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createForegroundNotification(): Notification {
        //前台通知的id名，任意
        val channelId = "HePing"
        //前台通知的名称，任意
        val channelName = "和屏"
        //发送通知的等级，此处为高，根据业务情况而定
        val importance = NotificationManager.IMPORTANCE_HIGH
        //判断Android版本，不同的Android版本请求不一样，以下代码为官方写法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.lightColor = Color.BLUE
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        //点击通知时可进入的Activity
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        //最终创建的通知，以下代码为官方写法
        //注释部分是可扩展的参数，根据自己的功能需求添加
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("和屏")
            .setContentText("服务运行中")
            .setSmallIcon(R.drawable.heping_icon) //通知显示的图标
            .setContentIntent(pendingIntent) //点击通知进入Activity
//            .setTicker("通知的提示语")
            .setOngoing(false)
            //.setPriority(NotificationCompat.PRIORITY_MAX)
            //.setCategory(Notification.CATEGORY_TRANSPORT)
            //.setLargeIcon(Icon)
            //.setWhen(System.currentTimeMillis())
            .build()
    }

    class FocusTimer(
        var isFocusing: Boolean = false,
        var focusTime: Int = 600, //设定时间 单位秒
        var currentTime: Int = 0
    ) {
        var timer = Timer()
        fun start(minutes: Int): Boolean {
            if (isFocusing) return false
            currentTime = 0
            focusTime = minutes
            isFocusing = true
            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    currentTime++
                    if (currentTime > focusTime) {
                        timer.cancel()
                        isFocusing = false
                    }
                }
            }, 0L, 1000L)
            return true
        }

        fun stop() {
            timer.cancel()
            isFocusing = false
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 2233
    }

}

