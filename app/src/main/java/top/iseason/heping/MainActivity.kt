package top.iseason.heping

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import top.iseason.heping.manager.AppService
import top.iseason.heping.manager.ConfigManager
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.model.AppViewModel
import top.iseason.heping.ui.screen.MainScreen
import top.iseason.heping.ui.theme.HePingTheme


class MainActivity : ComponentActivity() {
    @SuppressLint("StateFlowValueCalledInComposition")
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ModelManager.setMainActivity(this)
        ConfigManager.setSharedPreferences(getSharedPreferences("heping", Context.MODE_PRIVATE))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, Intent(this, AppService::class.java))
        } else {
            startService(Intent(this, AppService::class.java))
        }

        val viewModel = AppViewModel()
        setContent {
            LaunchedEffect(Unit) {
                ModelManager.setHideFromRecent(ConfigManager.getBoolean("Main-Setting-AutoHideFromRecent"))
                if (ConfigManager.getBoolean("MyTheme-AutoSwitch")) {
                    viewModel._isDarkMod.value = 0
                } else {
                    val int = ConfigManager.getInt("MyTheme-Mode")
                    if (int != 0) {
                        viewModel._isDarkMod.value = int
                    }
                }
            }
            val theme = viewModel.isDarkMod.collectAsState()
            val isDrak = if (theme.value == 0) isSystemInDarkTheme() else when (theme.value) {
                1 -> false
                else -> true
            }
            HePingTheme(
                darkTheme = isDrak
            ) {
                val systemUiController = rememberSystemUiController()
                val color =
                    if (!isDrak) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.background
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = color,
                        darkIcons = !isDrak
                    )
                }
                MainScreen(viewModel)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // 这是前提——你的app至少运行了一个service。这里表示当进程不在前台时，马上开启一个service
//        startService(Intent(this, AppService::class.java))
    }

    override fun onDestroy() {
        unbindService(ModelManager.conn)
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val service = ModelManager.getService()
        if (service?.tomatoCircle?.isCircle == true) {
            return false
        } else if (service?.focusTime?.isFocusing == true) {
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

}


