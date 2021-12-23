package top.iseason.heping

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController
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
}


