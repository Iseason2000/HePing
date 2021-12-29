package top.iseason.heping.ui.screen.me

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.iseason.heping.R
import top.iseason.heping.manager.ModelManager
import top.iseason.heping.ui.screen.NavBar
import top.iseason.heping.ui.screen.focus.SettingTitle

@Composable
fun FeedBack() {
    NavBar("反馈问题") {
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            item { SettingTitle(title = "关于软件") }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                MessageBar("反馈问题/Bug") {
                    ModelManager
                        .getMainActivity()
                        .startActivity(Intent(Intent.ACTION_VIEW).apply {
                            data =
                                Uri.parse("https://github.com/Iseason2000/HePing/issues")
                        })
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                MessageBar("开源地址(GPL-3.0 License)") {
                    ModelManager
                        .getMainActivity()
                        .startActivity(Intent(Intent.ACTION_VIEW).apply {
                            data =
                                Uri.parse("https://github.com/Iseason2000/HePing")
                        })
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                SettingTitle(title = "关于作者")
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                SelectionContainer {
                    TextCard(
                        "制作人员",
                        "界面设计: @Wayne (huguangweichn@foxmail.com) \n开发: \n@Iseason (Iseason2000@outlook.com)"
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MyInfo(R.drawable.github_fill, Color.Black, Modifier.weight(1F, true)) {
                        ModelManager
                            .getMainActivity()
                            .startActivity(Intent(Intent.ACTION_VIEW).apply {
                                data =
                                    Uri.parse("https://github.com/Iseason2000")
                            })
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    MyInfo(R.drawable.bilibili_line, Color(0XFF02b5da), Modifier.weight(1F, true)) {
                        ModelManager
                            .getMainActivity()
                            .startActivity(Intent(Intent.ACTION_VIEW).apply {
                                data =
                                    Uri.parse("https://space.bilibili.com/8689588")
                            })
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MyInfo(R.drawable.blog, Color(0XFFFF8800), Modifier.weight(1F, true)) {
                        ModelManager
                            .getMainActivity()
                            .startActivity(Intent(Intent.ACTION_VIEW).apply {
                                data =
                                    Uri.parse("https://www.iseason.top/")
                            })
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Surface(
                        modifier = Modifier
                            .defaultMinSize(156.dp, 100.dp)
                            .height(100.dp)
                            .clip(MaterialTheme.shapes.large)
                            .weight(1F, true)
                    ) {
                        SelectionContainer {
                            Column(
                                modifier = Modifier
                                    .padding(all = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.mail),
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(50.dp)
                                )
                                Text(
                                    text = "Iseason2000@qq.com",
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                SettingTitle(title = "请作者喝咖啡")
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Image(
                    painter = painterResource(id = R.drawable.wechatpay),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Image(painter = painterResource(id = R.drawable.aipay), contentDescription = null)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun MyInfo(icon: Int, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(68.dp)
            )
        }
    }
}