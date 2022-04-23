package work.onss.heroman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.AndroidEntryPoint
import work.onss.heroman.screens.MainScreen
import work.onss.heroman.ui.theme.HeroManTheme

@ExperimentalPagingApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HeroManTheme {
                val navController = rememberNavController()
                MainScreen(navController = navController)
            }
        }
    }
}
