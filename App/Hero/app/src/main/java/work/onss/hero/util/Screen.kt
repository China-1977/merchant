package work.onss.hero.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.paging.ExperimentalPagingApi
import work.onss.hero.screens.ScoreViewModel
import work.onss.hero.screens.SiteViewModel


sealed class Screen(val route: String, val icon: ImageVector) {
    object Score : Screen("订单", Icons.Filled.Home)
    object Site : Screen("驿站", Icons.Filled.DateRange)

    @ExperimentalPagingApi
    @Composable
    fun setupNavGraph(
        navController: NavHostController,
        scoreViewModel: ScoreViewModel = hiltViewModel(),
        siteViewModel: SiteViewModel = hiltViewModel()
    ) {
        NavHost(navController = navController, startDestination = route) {
            composable(Score.route) { scoreViewModel.list() }
            composable(Site.route) { siteViewModel.list() }
        }
    }
}
