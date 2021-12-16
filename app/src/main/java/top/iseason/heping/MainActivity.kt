package top.iseason.heping

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import top.iseason.heping.model.ModelManager
import top.iseason.heping.ui.screen.MyScaffold
import top.iseason.heping.ui.theme.HePingTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ModelManager.setMainActivity(this)
        setContent {
            HePingTheme {
                MyScaffold()
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HePingTheme {
        MyScaffold()
    }
}


