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
            //0..23
            val fistHour = split[0].toInt()
            val fistMinute = split[1].toInt()
            val lastHour = split[2].toInt()
            val lastMinute = split[3].toInt()
            val current = Calendar.getInstance()
            val nowHour = current.get(Calendar.HOUR_OF_DAY)
            val nowMinute = current.get(Calendar.MINUTE)
            var isSleepTime = false
            if (lastHour >= fistHour) {
                //在同一天
                if (nowHour in fistHour..lastHour) {
                    val sleepTimeStart = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, fistHour)
                        set(Calendar.MINUTE, fistMinute)
                        set(Calendar.SECOND, 0)
                    }
                    val sleepTimeEnd = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, lastHour)
                        set(Calendar.MINUTE, lastMinute)
                        set(Calendar.SECOND, 0)
                    }
                    if (current.timeInMillis in sleepTimeStart.timeInMillis..sleepTimeEnd.timeInMillis)
                        isSleepTime = true
                }
            } else {
                //不在同一天,但现在位于教前的一天的睡眠时间之后
                if (nowHour > fistHour || (nowHour == fistHour && nowMinute >= fistMinute)) {
                    isSleepTime = true
                } else if (nowHour in 0 until lastHour || (nowHour == lastHour && nowMinute < lastMinute)) {
                    //现在位于后一天的睡觉时间内
                    isSleepTime = true
                }
            }
            if (isSleepTime) {
                windowManager.setText(
                    "已经深夜",
                    Util.longTimeFormatDetail3(current.timeInMillis - Util.getDate(0).timeInMillis),
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

    val tomatoCircle = TomatoCircleTimer()
    val focusTime = FocusTimer()
    val relaxTime = RelaxTimer()
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

    inner class FocusTimer(
        var isFocusing: Boolean = false,
        var focusTime: Int = 600, //设定时间 单位秒
        var currentTime: Int = 0
    ) {
        private var timer = Timer()
        fun start(minutes: Int): Boolean {
            if (isFocusing || relaxTime.isRelaxing) return false
            currentTime = 0
            focusTime = minutes
            isFocusing = true
            timer = Timer()
            if (ConfigManager.getBoolean("Focus-Setting-StartTip"))
                ModelManager.tip()
            if (ConfigManager.getBoolean("Focus-Setting-AutoLock"))
                ModelManager.lockScreen()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    currentTime++
                    if (currentTime > focusTime) {
                        stop()
                    }
                }
            }, 0L, 1000L)
            return true
        }

        fun stop() {
            timer.cancel()
            isFocusing = false
            if (ConfigManager.getBoolean("Focus-Setting-EndTip"))
                ModelManager.tip()
        }
    }

    inner class RelaxTimer(
        var isRelaxing: Boolean = false,
        var relaxTime: Int = 600, //设定时间 单位秒
        var currentTime: Int = 0
    ) {
        var timer = Timer()
        fun start(minutes: Int): Boolean {
            if (isRelaxing) return false
            currentTime = 0
            relaxTime = minutes
            isRelaxing = true
            timer = Timer()
            if (ConfigManager.getBoolean("Focus-Setting-TomatoTip"))
                ModelManager.tip()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    currentTime++
                    if (currentTime > relaxTime) {
                        timer.cancel()
                        isRelaxing = false
                    }
                }
            }, 0L, 1000L)
            return true
        }

        fun stop() {
            timer.cancel()
            isRelaxing = false
        }
    }

    inner class TomatoCircleTimer(
        var isCircle: Boolean = false,
        var maxTimes: Int = 5,
        var count: Int = 0
    ) {
        var isWorking: Boolean = false
        var isRelaxing: Boolean = false
        private var timer = Timer()
        fun start(times: Int, workTime: Int, relaxTime: Int): Boolean {
            if (isCircle) return false
            if (this@AppService.focusTime.isFocusing || this@AppService.relaxTime.isRelaxing) return false
            maxTimes = times
            count = 0
            isCircle = true
            timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    if (count >= maxTimes) {
                        stop()
//                        println("循环结束")
                        return
                    }
                    println(isWorking)
                    println(isRelaxing)
                    if (!isWorking && !isRelaxing) {
                        if (!this@AppService.focusTime.start(workTime)) {
                            stop()
                            return
                        }
//                        println("开始第${count}次工作")
                        isWorking = true
                        return
                    }
                    if (isWorking && !this@AppService.focusTime.isFocusing) {
                        //工作状态但是工作计时结束了
                        isWorking = false
                        if (!this@AppService.relaxTime.start(relaxTime)) {
                            stop()
                            return
                        }
//                        println("开始第${count}次休息")
                        isRelaxing = true
                        return
                    }
                    if (isRelaxing && !this@AppService.relaxTime.isRelaxing) {
                        //休息状态但是休息时间结束了
                        count++
                        isRelaxing = false
                    }

                }
            }, 0L, 1000L)
            return true
        }

        fun stop() {
            timer.cancel()
            focusTime.stop()
            relaxTime.stop()
            isWorking = false
            isRelaxing = false
            isCircle = false
        }
    }

    fun getWindowManager() = windowManager

    companion object {
        private const val NOTIFICATION_ID = 2233
    }

}

