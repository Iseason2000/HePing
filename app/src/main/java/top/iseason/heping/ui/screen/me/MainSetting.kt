package top.iseason.heping.ui.screen.me

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import top.iseason.heping.manager.ConfigManager
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.ui.screen.NavBar
import top.iseason.heping.ui.screen.focus.SettingLine
import top.iseason.heping.ui.screen.focus.SettingTitle

@OptIn(ExperimentalGraphicsApi::class)
@Composable
fun MainSetting() {
    NavBar("高级设置") {
        var isShowColorPicker by remember { mutableStateOf(false) }
        var colorl by remember { mutableStateOf(HsvColor.DEFAULT) }
        //todo:完善字幕设置
        LazyColumn(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
            item {
                SettingTitle("权限和运行")
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingLine("自动从最近任务列表隐藏", "Main-Setting-AutoHideFromRecent")
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large)
                        .clickable {
                            isShowColorPicker = true
                        }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                    ) {
                        Text(text = "字符样式", fontSize = 16.sp, fontWeight = FontWeight.Normal)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.ArrowForwardIos,
                                contentDescription = null,
                                tint = MaterialTheme.colors.primary
                            )
                        }
                    }

                }
            }
        }
        if (isShowColorPicker) {
            AlertDialog(onDismissRequest = { isShowColorPicker = false }, buttons = {},
                text = {
                    ClassicColorPicker(
                        onColorChanged = { color: HsvColor ->
                            colorl = color
                        }
                    )
                }, modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
        DisposableEffect(Unit) {
            onDispose {
                ModelManager.setHideFromRecent(ConfigManager.getBoolean("Main-Setting-AutoHideFromRecent"))
            }
        }
    }
}