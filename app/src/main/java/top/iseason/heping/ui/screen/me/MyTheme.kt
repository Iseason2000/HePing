package top.iseason.heping.ui.screen.me

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.iseason.heping.manager.ConfigManager
import top.iseason.heping.model.AppViewModel
import top.iseason.heping.ui.screen.NavBar
import top.iseason.heping.ui.screen.focus.SettingLine
import top.iseason.heping.ui.theme.GreenPrimary
import top.iseason.heping.ui.theme.GreenSecondary

@Composable
fun MyTheme(viewModel: AppViewModel) {
    NavBar("主题风格") {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {

            var selected by remember { mutableStateOf(1) }
            selected = if (ConfigManager.getBoolean("MyTheme-AutoSwitch")) {
                if (isSystemInDarkTheme()) 2 else 1
            } else
                ConfigManager.getInt("MyTheme-Mode")
            LaunchedEffect(selected) {
                viewModel._isDarkMod.value = selected
            }
            DisposableEffect(Unit) {
                onDispose {
                    if (ConfigManager.getBoolean("MyTheme-AutoSwitch"))
                        viewModel._isDarkMod.value = 0
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ThemeCard(
                    "浅色",
                    GreenSecondary,
                    Color.White,
                    selected == 1,
                    modifier = Modifier
                        .clickable {
                            selected = 1
                            ConfigManager.setBoolean("MyTheme-AutoSwitch", false)
                            ConfigManager.setInt("MyTheme-Mode", 1)
                        }
                        .weight(1F, true)
                )
                Spacer(modifier = Modifier.width(16.dp))
                ThemeCard(
                    "深色",
                    GreenPrimary,
                    Color(0XFF12231E),
                    selected == 2,
                    modifier = Modifier
                        .clickable {
                            selected = 2
                            ConfigManager.setBoolean("MyTheme-AutoSwitch", false)
                            ConfigManager.setInt("MyTheme-Mode", 2)
                        }
                        .weight(1F, true))
            }
            Spacer(modifier = Modifier.height(16.dp))
            SettingLine(title = "深浅主题跟随系统设置", key = "MyTheme-AutoSwitch")
        }
    }
}

@Composable
fun ThemeCard(
    text: String,
    mainColor: Color,
    subColor: Color,
    isCheck: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        color = subColor,
        modifier = modifier
            .size(156.dp)
            .clip(MaterialTheme.shapes.large)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = text, fontSize = 32.sp, fontWeight = FontWeight.Medium, color = mainColor)
        }
        if (isCheck)
            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.padding(18.dp)) {
                Icon(
                    Icons.Rounded.CheckCircleOutline,
                    contentDescription = null,
                    tint = GreenSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
    }
}