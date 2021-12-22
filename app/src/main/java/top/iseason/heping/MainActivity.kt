package top.iseason.heping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.SideEffect
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import top.iseason.heping.model.AppViewModel
import top.iseason.heping.model.ModelManager
import top.iseason.heping.ui.screen.MyScaffold
import top.iseason.heping.ui.screen.health.HealthAppDetail
import top.iseason.heping.ui.screen.health.HealthTotalInfo
import top.iseason.heping.ui.theme.HePingTheme


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ModelManager.setMainActivity(this)
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
                val navController = rememberAnimatedNavController()
                ModelManager.setNavHostController(navController)
                ModelManager.setViewModel(viewModel)
                AnimatedNavHost(navController = navController, startDestination = "main") {
                    composable(route = "main", enterTransition = {
                        EnterTransition.None
                    }) {
                        MyScaffold(viewModel)
                    }
                    composable(route = "healthTotal", exitTransition = {
                        slideOutHorizontally(targetOffsetX = { -it })
                    }, enterTransition = {
                        slideInHorizontally()
                    }) {
                        HealthTotalInfo()
                    }
                    composable(route = "healthAppDetail/{packageName}", exitTransition = {
                        slideOutHorizontally(targetOffsetX = { -it })
                    }, enterTransition = {
                        slideInHorizontally()
                    }) {
                        val packageName = it.arguments?.getString("packageName")
                        if (packageName == null) {
                            navController.popBackStack()
                            return@composable
                        }
                        HealthAppDetail(packageName)
                    }
                }

            }
        }
    }
}


