package top.iseason.heping.ui.screen.health

import androidx.compose.runtime.Composable
import top.iseason.heping.model.ModelManager

@Composable
fun HealthAppDetail(packageName: String) {

    val appInfoForAllDays = ModelManager.getViewModel().getAppInfoForAllDays(packageName)

}