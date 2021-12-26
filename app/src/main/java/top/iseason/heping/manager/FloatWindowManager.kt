package top.iseason.heping.manager

import android.annotation.SuppressLint
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import top.iseason.heping.R


@SuppressLint("StaticFieldLeak")
class FloatWindowManager {
    private var isShow = false
    private var isInit = false
    private val windowParams = WindowManager.LayoutParams(

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        },
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        PixelFormat.TRANSLUCENT
    )

    private var rootView: View? = null
    private var title: TextView? = null
    private var time: TextView? = null
    private var tip: TextView? = null

    @SuppressLint("InflateParams")
    fun init() {
        if (isInit) return
        rootView = ModelManager.getLayoutInflater()?.inflate(R.layout.floatwindow, null)
        title = rootView?.findViewById(R.id.title)
        time = rootView?.findViewById(R.id.time)
        tip = rootView?.findViewById(R.id.tip)
        isInit = true
    }

    fun showWindow() {
        if (!isInit) return
        if (rootView == null) return
        // 新建悬浮窗控件
        if (!isShow) {
            Handler(Looper.getMainLooper()).post {
                ModelManager.getWindowManager().addView(rootView, windowParams)
            }
            isShow = true
        }
    }

    fun setText(title: String?, time: String?, tip: String?) {
        if (!isInit) return
        if (title == null) return
        Handler(Looper.getMainLooper()).post {
            this.title?.text = title
            this.time?.text = time
            this.tip?.text = tip
        }
    }

    fun hideWindow() {
        // 新建悬浮窗控件
        if (!isInit) return
        if (rootView == null) return
        if (isShow) {
            Handler(Looper.getMainLooper()).post {
                ModelManager.getWindowManager().removeView(rootView)
            }
            isShow = false
        }
    }
}