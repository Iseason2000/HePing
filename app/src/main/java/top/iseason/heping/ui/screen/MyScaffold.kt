package top.iseason.heping.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import top.iseason.heping.R
import top.iseason.heping.model.AppViewModel
import top.iseason.heping.ui.screen.health.HealthScreen

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MyScaffold(viewModel: AppViewModel) {
    var selectedItem by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState()
    val items = listOf("健康", "专注", "我的")
    LaunchedEffect(selectedItem) {
        pagerState.animateScrollToPage(selectedItem)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = items[pagerState.currentPage],
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                },
                backgroundColor = MaterialTheme.colors.primaryVariant,
                elevation = 0.dp
            )
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = MaterialTheme.colors.background,
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
                                            R.drawable.icon_mine
                                    }
                                ),
                                null
                            )
                        },
                        label = { Text(item) },
                        selected = pagerState.targetPage == index,
                        selectedContentColor = MaterialTheme.colors.primary,
                        unselectedContentColor = MaterialTheme.colors.onBackground,
                        onClick = { selectedItem = index }
                    )
                }

            }
        }

    ) {
        HorizontalPager(
            count = 3,
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top
        )
        { page: Int ->
            when (page) {
                0 -> {
                    HealthScreen(viewModel = viewModel)
                }
                1 -> {

                }
                2 -> {

                }
            }

        }
    }
}