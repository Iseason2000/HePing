package top.iseason.heping.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import top.iseason.heping.model.AppViewModel
import top.iseason.heping.ui.screen.health.HealthScreen

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MyScaffold(viewModel: AppViewModel) {
    var selectedItem by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState()
    val items = listOf("健康", "专注", "我的")
    LaunchedEffect(key1 = selectedItem, block = {
        pagerState.animateScrollToPage(selectedItem)
    })
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = items[pagerState.currentPage],
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
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
                            when (index) {
                                0 -> if (pagerState.currentPage == index) Icon(
                                    Icons.Filled.Favorite,
                                    contentDescription = null
                                ) else Icon(Icons.Filled.FavoriteBorder, contentDescription = null)
                                1 -> if (pagerState.currentPage == index) Icon(
                                    Icons.Filled.CenterFocusStrong,
                                    contentDescription = null
                                ) else Icon(
                                    Icons.Filled.CenterFocusWeak,
                                    contentDescription = null
                                )
                                else -> if (pagerState.currentPage == index) Icon(
                                    Icons.Filled.Person,
                                    contentDescription = null
                                ) else Icon(Icons.Outlined.Person, contentDescription = null)
                            }
                        },
                        label = { Text(item) },
                        selected = pagerState.currentPage == index,
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