package top.iseason.heping.ui.screen.me

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.iseason.heping.manager.ConfigManager
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.ui.screen.NavBar
import top.iseason.heping.ui.screen.focus.SettingLine
import top.iseason.heping.ui.screen.focus.SettingTitle

@Composable
fun MainSetting() {
    NavBar("高级设置") {
        LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
            item {
                SettingTitle("权限和运行")
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingLine("自动从最近任务列表隐藏", "Main-Setting-AutoHideFromRecent")
            }
        }
        DisposableEffect(Unit) {
            onDispose {
                ModelManager.setHideFromRecent(ConfigManager.getBoolean("Main-Setting-AutoHideFromRecent"))
            }
        }
    }
}