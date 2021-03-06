package top.iseason.heping.ui.screen.me

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.iseason.heping.R
import top.iseason.heping.manager.ConfigManager
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.model.AppViewModel
import top.iseason.heping.ui.theme.GreenSurface

@Composable
fun MyScreen(viewModel: AppViewModel) {
    val mainColor = MaterialTheme.colors.primaryVariant
    val subColor = GreenSurface
    if (MaterialTheme.colors.isLight)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = if (MaterialTheme.colors.isLight) GreenSurface else Color.Black)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(148.dp)
            ) {
                drawRect(color = mainColor)
            }
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
            ) {
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(mainColor, subColor),
                        start = Offset(size.width / 2, 0F),
                        end = Offset(size.width / 2, size.height)
                    )
                )
            }
        }

    LazyColumn(
        modifier = Modifier
            .padding(all = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            LoginAndIcon()
        }
        item {
            Spacer(modifier = Modifier.height(32.dp))
            DayBar()
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                SettingCard(
                    image = R.drawable.palette_black,
                    title = "????????????",
                    subTitle = "???????????????",
                    modifier = Modifier.weight(1F, true)
                ) {
                    ModelManager.getNavController().navigate("myThemeSetting")
                }
                Spacer(modifier = Modifier.width(16.dp))
                SettingCard(
                    image = R.drawable.help_outline_black,
                    title = "????????????",
                    subTitle = "??????????????????",
                    modifier = Modifier.weight(1F, true)
                ) {
                    ModelManager.getNavController().navigate("help")
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                SettingCard(
                    image = R.drawable.settings_black,
                    title = "????????????",
                    subTitle = "??????????????????",
                    modifier = Modifier.weight(1F, true)
                ) {
                    ModelManager.getNavController().navigate("mainSetting")
                }
                Spacer(modifier = Modifier.width(16.dp))
                SettingCard(
                    image = R.drawable.about, title = "????????????", subTitle = "???????????????",
                    modifier = Modifier.weight(1F, true)
                ) {
                    ModelManager.getNavController().navigate("about")
                }
            }
        }
    }
}

@Composable
fun LoginAndIcon() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colors.background)
        ) {
            Icon(
                Icons.Rounded.Person,
                contentDescription = "Icon",
                modifier = Modifier.fillMaxSize(),
                tint = Color(0XFFD9D9D9)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                ModelManager.showToast("???????????????")

            }) {
            Text(text = "?????????", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "???????????????????????????",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(5.dp))
                Icon(
                    Icons.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(10.dp, 20.dp),
                    tint = Color.White
                )
            }

        }

    }
}

@Composable
fun DayBar() {
    val setupTime = ConfigManager.getLong("App-SetupTime")
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.health_and_safety_black),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "????????????????????????????????? ", fontSize = 16.sp, fontWeight = FontWeight.Normal)
            Text(
                text = (((System.currentTimeMillis() - setupTime) / 86400000)).toInt().toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colors.secondary
            )
            Text(text = " ???", fontSize = 16.sp, fontWeight = FontWeight.Normal)
        }
    }
}

@Composable
fun SettingCard(
    image: Int,
    title: String,
    subTitle: String,
    modifier: Modifier = Modifier,
    onclick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onclick)
    ) {
        Column(modifier = Modifier.padding(all = 16.dp)) {
            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(
                text = subTitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colors.onError
            )
        }

    }
}