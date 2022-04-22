package work.onss.heroman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.navigation.compose.rememberNavController
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.AndroidEntryPoint
import work.onss.heroman.navigation.SetupNavGraph
import work.onss.heroman.screens.common.BottomBar
import work.onss.heroman.screens.common.TopBar
import work.onss.heroman.ui.theme.HeroManTheme
import work.onss.heroman.util.Constants

@ExperimentalPagingApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeroManTheme {
                val navController = rememberNavController()
                Scaffold(
                    topBar = { TopBar() },
                    content = {
                        SetupNavGraph(navController = navController)
                    },
                    bottomBar = {
                        BottomBar(screens = Constants.screens, navController = navController)
                    }
                )

            }
        }
    }
}
