package top.iseason.heping.ui.screen.controller

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.iseason.heping.manager.ConfigManager


@Composable
fun AppLimiter(packName: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
    ) {
        var limitTime by remember { mutableStateOf(0) }
        if (ConfigManager.hasKey("TimeLimit-$packName")) {
            limitTime = ConfigManager.getInt("TimeLimit-$packName")
        }
//            else{
//                ConfigManager.setInt("TimeLimit-$packName",0)
//            }
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Text(
                text = "应用限额设置",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "单日使用此应用达到指定时长（分钟）时将提醒您",
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colors.onError
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                CountButton("关闭", limitTime, 0) {
                    limitTime = 0
                    ConfigManager.setInt("TimeLimit-$packName", 0)
                }
                CountButton("30", limitTime, 30) {
                    limitTime = 30
                    ConfigManager.setInt("TimeLimit-$packName", 30)
                }
                CountButton("60", limitTime, 60) {
                    limitTime = 60
                    ConfigManager.setInt("TimeLimit-$packName", 60)
                }
                CountButton("90", limitTime, 90) {
                    limitTime = 90
                    ConfigManager.setInt("TimeLimit-$packName", 90)
                }
            }
        }

    }
}

@Composable
fun CountButton(text: String, limitTime: Int, num: Int, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = if (limitTime == num) MaterialTheme.colors.primary
            else Color(0xFFF3F6F5)
        ),
        modifier = Modifier.size(68.dp, 48.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = if (limitTime == num) Color.White
            else MaterialTheme.colors.primary
        )
    }
}