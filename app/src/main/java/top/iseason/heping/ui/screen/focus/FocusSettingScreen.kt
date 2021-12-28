package top.iseason.heping.ui.screen.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.iseason.heping.manager.ConfigManager
import top.iseason.heping.ui.screen.NavBar
import top.iseason.heping.ui.screen.controller.ScrollerPicker

@Composable
fun FocusSettingScreen() {
    NavBar("专注设置") {
        LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
            item {
                SettingTitle("通用")
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingLine("进入专注状态自动锁屏", "Focus-Setting-AutoLock")
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingLine("专注开始时提醒", "Focus-Setting-StartTip")
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingLine("专注结束时提醒", "Focus-Setting-EndTip")
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingLine("提醒时发出提示音", "Focus-Setting-TipSound")
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SettingTitle("番茄工作")
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                TomatoTimePick()
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingLine("休息开始时提醒", "Focus-Setting-TomatoTip")
            }

        }
    }
}

@Composable
fun TomatoTimePick() {
    var focusTime by remember { mutableStateOf(25) }
    var releaseTime by remember { mutableStateOf(5) }
    val int = ConfigManager.getInt("Focus-Setting-Tomato-FocusTime")
    if (int != 0) focusTime = int
    val int1 = ConfigManager.getInt("Focus-Setting-Tomato-ReleaseTime")
    if (int1 != 0) releaseTime = int1
    DisposableEffect(Unit) {
        onDispose {
            ConfigManager.setInt("Focus-Setting-Tomato-FocusTime", focusTime)
            ConfigManager.setInt("Focus-Setting-Tomato-ReleaseTime", releaseTime)
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(text = "番茄循环时长", fontSize = 16.sp, fontWeight = FontWeight.Normal)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "专注", fontSize = 12.sp, fontWeight = FontWeight.Normal)
                    Spacer(modifier = Modifier.width(16.dp))
                    ScrollerPicker(90, focusTime, 10, onValueChange = { focusTime = it })
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "分钟", fontSize = 12.sp, fontWeight = FontWeight.Normal)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "休息", fontSize = 12.sp, fontWeight = FontWeight.Normal)
                    Spacer(modifier = Modifier.width(16.dp))
                    ScrollerPicker(30, releaseTime, 10, onValueChange = { releaseTime = it })
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "分钟", fontSize = 12.sp, fontWeight = FontWeight.Normal)
                }
            }
        }

    }
}

@Composable
fun SettingTitle(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(8.dp))
        Spacer(
            modifier = Modifier
                .size(4.dp, 16.dp)
                .clip(MaterialTheme.shapes.small)
                .background(color = MaterialTheme.colors.primary)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Medium)
    }

}

@Composable
fun SettingLine(title: String, key: String, default: Boolean = false) {
    var isOpen by remember { mutableStateOf(default) }
    if (ConfigManager.hasKey(key)) {
        isOpen = ConfigManager.getBoolean(key)
    }
    Surface(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
    ) {
        LaunchedEffect(isOpen) {
            ConfigManager.setBoolean(key, isOpen)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Normal)
            Switch(
                checked = isOpen,
                onCheckedChange = {
                    isOpen = it
                },
                modifier = Modifier.size(46.dp, 24.dp),
                enabled = true,
                colors = SwitchDefaults.colors(
                    uncheckedThumbColor = MaterialTheme.colors.primary,
                    uncheckedTrackColor = MaterialTheme.colors.secondaryVariant,
                    checkedThumbColor = MaterialTheme.colors.secondaryVariant,
                    checkedTrackColor = MaterialTheme.colors.primary,
                    checkedTrackAlpha = 1F,
                    uncheckedTrackAlpha = 1F
                )
            )
        }

    }
}