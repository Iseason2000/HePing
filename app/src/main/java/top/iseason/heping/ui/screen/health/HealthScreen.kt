package top.iseason.heping.ui.screen.health

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import top.iseason.heping.model.AppViewModel

@Composable
fun HealthScreen(modifier: Modifier = Modifier, viewModel: AppViewModel) {
    Column(modifier = modifier) {
        UsageWindow(viewModel = viewModel)
    }
}