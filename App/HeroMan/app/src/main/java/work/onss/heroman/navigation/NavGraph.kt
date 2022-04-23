package work.onss.heroman.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.paging.ExperimentalPagingApi
import work.onss.heroman.screens.common.Mine
import work.onss.heroman.screens.common.ScoreList
import work.onss.heroman.screens.common.SiteList

@ExperimentalPagingApi
@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Score.route) {
        composable(Screen.Score.route) { ScoreList() }
        composable(Screen.Site.route) { SiteList() }
        composable(Screen.Mine.route) { Mine() }
    }
}