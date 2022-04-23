package work.onss.heroman.screens

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.paging.ExperimentalPagingApi
import work.onss.heroman.navigation.SetupNavGraph
import work.onss.heroman.screens.common.BottomBar
import work.onss.heroman.screens.common.TopBar
import work.onss.heroman.util.Constants

@ExperimentalPagingApi
@Composable
fun MainScreen(navController: NavHostController) {
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



