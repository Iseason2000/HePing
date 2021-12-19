package top.iseason.heping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import top.iseason.heping.model.AppViewModel
import top.iseason.heping.model.ModelManager
import top.iseason.heping.ui.screen.MyScaffold
import top.iseason.heping.ui.theme.HePingTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ModelManager.setMainActivity(this)
        setContent {
            HePingTheme {
                val viewModel = AppViewModel()
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = MaterialTheme.colors.isLight
                val color = MaterialTheme.colors.primaryVariant
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = color,
                        darkIcons = !useDarkIcons
                    )
                }
                MyScaffold(viewModel)
            }
        }
    }
}


