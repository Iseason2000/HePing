package top.iseason.heping.ui.screen.health

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.iseason.heping.ui.screen.NavBar
import top.iseason.heping.ui.screen.controller.TimePicker

@Composable
fun HealthSleep() {
    NavBar("睡眠") {

    LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
            item { TimePicker() }

        }
    }
}