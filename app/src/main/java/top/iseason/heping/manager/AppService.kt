package top.iseason.heping.manager

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import top.iseason.heping.MainActivity
import top.iseason.heping.R


class AppService : Service() {
    private var isRunning: Boolean = false
    private var isOpening: Boolean = true
    private var lastOpenTime: Long = System.currentTimeMillis()
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

    override fun onBind(intent: Intent?): IBinder? {
        return null
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
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            startForeground(NOTIFICATION_ID, createForegroundNotification())
        }
        isRunning = true
        return super.onStartCommand(intent, flags, startId)
    }

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

    companion object {
        private const val NOTIFICATION_ID = 2233
    }

}
