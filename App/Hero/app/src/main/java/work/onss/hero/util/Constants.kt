package work.onss.hero.util

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.paging.ExperimentalPagingApi
import work.onss.hero.navigation.Screen

@ExperimentalPagingApi
object Constants {

    @Composable
    fun topOne(title: String = "一九七七") {
        return TopAppBar(
            title = {
                Text(
                    text = title, modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center)
                )
            }, modifier = Modifier.fillMaxWidth()
        )
    }

    @Composable
    fun bottomOne(navController: NavHostController, vararg screens: Screen) {
        return BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
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
                            imageVector = screen.icon, contentDescription = screen.route
                        )
                    },
                    label = {
                        Text(text = screen.route)
                    },
                )
            }
        }
    }

    @Composable
    fun dataLoading(message: String) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(50.dp),
        ) {
            Text(message)
        }
    }
}

