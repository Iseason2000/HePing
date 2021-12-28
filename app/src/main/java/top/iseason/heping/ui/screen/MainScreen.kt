package top.iseason.heping.ui.screen

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.collect
import top.iseason.heping.R
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.model.AppViewModel
import top.iseason.heping.ui.screen.focus.FocusScreen
import top.iseason.heping.ui.screen.focus.FocusSettingScreen
import top.iseason.heping.ui.screen.focus.FocusTomato
import top.iseason.heping.ui.screen.focus.Focusing
import top.iseason.heping.ui.screen.health.*
import top.iseason.heping.ui.screen.me.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(viewModel: AppViewModel) {
    val navController = rememberAnimatedNavController()
    ModelManager.setNavHostController(navController)
    ModelManager.setViewModel(viewModel)
    AnimatedNavHost(navController = navController, startDestination = "main") {
        composable(route = "main", exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it })
        }, enterTransition = {
            slideInHorizontally(initialOffsetX = { -it })
        }) {
            MyScaffold(viewModel)
        }
        composable(route = "healthTotal", exitTransition = {
            val offset = if (targetState.destination.route == "main") 1 else -1
            slideOutHorizontally(targetOffsetX = { it * offset })
        }, enterTransition = {
            val offset = if (initialState.destination.route == "main") 1 else -1
            slideInHorizontally(initialOffsetX = { it * offset })
        }) {
            HealthTotalInfo()
        }
        navPage("healthAppDetail/{packageName}") {
            val packageName = it.arguments?.getString("packageName")
            if (packageName == null) {
                navController.popBackStack()
                return@navPage
            }
            HealthAppDetail(packageName)
        }
        navPage("healthSleep") {
            HealthSleep()
        }
        navPage("healthTired") {
            HealthTiredRecord()
        }
        navPage("focusSetting") {
            FocusSettingScreen()
        }
        navPage("focusing") {
            Focusing()
        }
        navPage("focusTomato") {
            FocusTomato()
        }
        navPage("myThemeSetting") {
            MyTheme(viewModel)
        }
        navPage("mainSetting") {
            MainSetting()
        }
        navPage("help") {
            HelpScreen()
        }
        navPage("about") {
            About()
        }
        navPage("feedBack") {
            FeedBack()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.navPage(
    rout: String,
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(route = rout, exitTransition = {
        slideOutHorizontally(targetOffsetX = { it })
    }, enterTransition = {
        slideInHorizontally(initialOffsetX = { it })
    }, content = content)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MyScaffold(viewModel: AppViewModel) {
    var selectedItem by remember { mutableStateOf(viewModel.initPage) }
    val pagerState = rememberPagerState()
    val items = listOf("健康", "专注", "我的")
    LaunchedEffect(Unit) {
        val service = ModelManager.getService()
        if (service?.tomatoCircle?.isCircle == true) {
            ModelManager.getNavController().navigate("focusTomato")
        } else if (service?.focusTime?.isFocusing == true) {
            ModelManager.getNavController().navigate("focusing")
        }
    }
    LaunchedEffect(selectedItem) {
        pagerState.animateScrollToPage(selectedItem)
    }
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect {
            viewModel.initPage = pagerState.currentPage
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = items[pagerState.currentPage],
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 10.dp),
                        color = Color.White
                    )
                },
                backgroundColor = if (MaterialTheme.colors.isLight) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.background,
                elevation = 0.dp
            )
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = MaterialTheme.colors.background
            ) {
                items.forEachIndexed { index, item ->
                    BottomNavigationItem(
                        icon = {
                            Image(
                                painterResource(
                                    when (index) {
                                        0 -> if (pagerState.targetPage == index)
                                            R.drawable.icon_health_select else
                                            R.drawable.icon_health
                                        1 -> if (pagerState.targetPage == index)
                                            R.drawable.icon_focus_select else
                                            R.drawable.icon_focus
                                        else -> if (pagerState.targetPage == index)
                                            R.drawable.icon_mine_select else
                                            R.drawable.icon_me
                                    }
                                ),
                                null
                            )
                        },
                        label = {
                            Text(
                                item,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                        },
                        selected = pagerState.targetPage == index,
                        selectedContentColor = MaterialTheme.colors.primary,
                        unselectedContentColor = MaterialTheme.colors.onError,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }

    ) {
        HorizontalPager(
            count = 3,
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        )
        { page: Int ->
            when (page) {
                0 -> {
                    HealthScreen(viewModel)
                }
                1 -> {
                    FocusScreen(viewModel)
                }
                2 -> {
                    MyScreen(viewModel)
                }
            }

        }
    }
}

@Composable
fun NavBar(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(if (MaterialTheme.colors.isLight) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.background)
                    .padding(start = 22.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        ModelManager
                            .getNavController()
                            .popBackStack()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.ArrowBackIos, null,
                    tint = Color.White
                )
                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        backgroundColor = if (MaterialTheme.colors.isLight) Color(0xFFF3F6F5) else MaterialTheme.colors.background,
        modifier = modifier, content = content
    )
}