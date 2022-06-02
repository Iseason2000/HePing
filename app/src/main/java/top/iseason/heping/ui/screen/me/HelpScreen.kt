package top.iseason.heping.ui.screen.me

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.iseason.heping.ui.screen.NavBar
import top.iseason.heping.ui.screen.focus.SettingTitle

@Composable
fun HelpScreen() {
    NavBar(title = "使用帮助") {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item { SettingTitle(title = "OSD提醒字幕") }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                TextCard(
                    null,
                    "本应用大部分功能都使用OSD字幕（即悬浮于屏幕内容上方的文字）配合震动或提示音进行提醒，在不影响其他应用程序正常使用的情况下，帮助您实时知悉当前的屏幕使用状况。"
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { SettingTitle(title = "健康模块") }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                TextCard(
                    "屏幕使用统计",
                    "您可以查看一天中不同时段的使用情况和各个应用的使用时长。点击该应用，可以单独查看该应用的使用情况。当您觉得某个应用消耗了您过多的时间时，还可以为该应用设定每日可用的时长。"
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                TextCard(
                    "睡眠",
                    "本应用会根据手机的使用情况推测出您昨晚的睡眠情况。您可以设定一个睡眠计划，当您在睡眠计划的时间段内使用手机时，和屏将提醒您按时进行睡眠，保证您的睡眠质量。推荐的睡眠时段为23:00~次日6点。"
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                TextCard(
                    "疲劳记录",
                    "本应用会记录您已连续使用屏幕的时长，您可以设定一个使用时长，连续使用达到该时长时和屏将提醒您暂时放下手机休息2分钟后再继续使用。推荐设定的疲劳时长为30~60分钟。"
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { SettingTitle(title = "专注模块") }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                TextCard(
                    "快速专注",
                    "选择时长，即可快速开始专注。 \n长按标签可以输入自定义专注时长。"
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                TextCard(
                    "番茄循环",
                    "番茄工作法：工作25分钟，休息5分钟恢复精力，再继续工作，以此循环。 \n选择循环次数，即可开始番茄工作循环。"
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                TextCard(
                    "专注设置",
                    "您可以对专注功能进行个性化设置。 \n如启用专注或休息时间开始、结束时的消息提醒；自定义番茄工作的专注时长和休息时长；启用强力模式等。"
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { SettingTitle(title = "个人模块") }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                TextCard(
                    null,
                    "在“我的”即个人模块中，您可以登录账号、设置APP的界面色彩，以及在高级设置中进行更多个性化设置。"
                )
            }
            item { Spacer(modifier = Modifier.height(64.dp)) }
        }
    }
}

@Composable
fun TextCard(title: String?, text: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
    ) {
        Column(
            modifier = Modifier.padding(
                top = 10.dp,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            )
        ) {
            if (title != null) {
                Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = if (MaterialTheme.colors.isLight) Color(0XFF787A79) else Color(0xFFC5C5C5)
            )
        }

    }
}