package work.onss.hero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.navigation.compose.rememberNavController
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.AndroidEntryPoint
import work.onss.hero.util.Screen
import work.onss.hero.ui.theme.HeroTheme
import work.onss.hero.util.Constants

@ExperimentalPagingApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeroTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = { Constants.topOne() },
                    content = { Screen.Score.setupNavGraph(navController) },
                    bottomBar = { Constants.bottomOne(navController, Screen.Score, Screen.Site) }
                )
            }
        }
    }
}
