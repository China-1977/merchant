package work.onss.heroman.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.paging.ExperimentalPagingApi
import work.onss.heroman.screens.MineScreen
import work.onss.heroman.screens.ScoreScreen
import work.onss.heroman.screens.SiteScreen

@ExperimentalPagingApi
@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Score.route) {
        composable(Screen.Score.route) { ScoreScreen(navController) }
        composable(Screen.Site.route) { SiteScreen(navController) }
        composable(Screen.Mine.route) { MineScreen(navController) }
    }
}