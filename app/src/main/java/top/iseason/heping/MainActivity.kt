package top.iseason.heping

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.SideEffect
import androidx.core.content.ContextCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import top.iseason.heping.manager.AppService
import top.iseason.heping.manager.ConfigManager
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.model.AppViewModel
import top.iseason.heping.ui.screen.MainScreen
import top.iseason.heping.ui.theme.HePingTheme

class MainActivity : ComponentActivity() {

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

        setContent {
            val viewModel = AppViewModel()
            HePingTheme {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = MaterialTheme.colors.isLight
                val color = MaterialTheme.colors.primaryVariant
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = color,
                        darkIcons = !useDarkIcons
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

}


