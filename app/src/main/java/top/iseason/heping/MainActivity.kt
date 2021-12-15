package top.iseason.heping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.iseason.heping.ui.theme.HePingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HePingTheme {
                Scaffold()
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HePingTheme {
        Scaffold()
    }
}

@Composable
fun Scaffold() {
    var selectedItem by remember { mutableStateOf(0) }
    var state by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val items = listOf("健康", "专注", "我的")
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AnimatedVisibility(
                        visible = state,
                        enter = fadeIn(initialAlpha = 0.5f),
                        exit = fadeOut(targetAlpha = 0.5f)
                    ) {
                        Text(
                            text = items[selectedItem],
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = 0.dp
            )
        },
        bottomBar = {
            BottomNavigation(backgroundColor = MaterialTheme.colors.background) {
                items.forEachIndexed { index, item ->
                    BottomNavigationItem(
                        icon = {
                            when (index) {
                                0 -> if (selectedItem == index) Icon(
                                    Icons.Filled.Favorite,
                                    contentDescription = null
                                ) else Icon(Icons.Filled.FavoriteBorder, contentDescription = null)
                                1 -> if (selectedItem == index) Icon(
                                    Icons.Filled.CenterFocusStrong,
                                    contentDescription = null
                                ) else Icon(
                                    Icons.Filled.CenterFocusWeak,
                                    contentDescription = null
                                )
                                else -> if (selectedItem == index) Icon(
                                    Icons.Filled.Person,
                                    contentDescription = null
                                ) else Icon(Icons.Outlined.Person, contentDescription = null)
                            }
                        },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            state = false
                            scope.launch {
                                delay(300L)
                                state = true
                            }
                        },

                        )
                }

            }
        }

    ) {

    }
}
