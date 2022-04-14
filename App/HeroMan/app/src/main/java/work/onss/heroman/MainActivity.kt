package work.onss.heroman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import work.onss.heroman.ui.screens.MainFrame
import work.onss.heroman.ui.theme.HeroManTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeroManTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainFrame()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    MainFrame()
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    HeroManTheme {
        MainFrame()
    }
}