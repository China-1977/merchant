package work.onss.hero.screens.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.paging.ExperimentalPagingApi
import work.onss.hero.navigation.Screen
import work.onss.hero.navigation.Screen.Score.setupNavGraph


@ExperimentalPagingApi
@Composable
fun main(navController: NavHostController) {
    val screens = listOf(Screen.Score, Screen.Site)
    Scaffold(
        topBar = { topBar() },
        content = {
            setupNavGraph(navController = navController)
        },
        bottomBar = {
            bottomBar(screens = screens, navController = navController)
        }
    )
}


@Composable
fun topBar(
) {
    TopAppBar(
        title = {
            Text(
                text = "一九七七",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun bottomBar(screens: List<Screen>, navController: NavHostController) {
    BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        screens.forEachIndexed { _, screen ->
            BottomNavigationItem(
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },

                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.route
                    )
                },
                label = {
                    Text(text = screen.route)
                },
            )
        }
    }
}

