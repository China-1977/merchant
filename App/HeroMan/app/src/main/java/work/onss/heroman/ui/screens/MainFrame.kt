package work.onss.heroman.ui.screens


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController


sealed class Screen(val route: String, val icon: ImageVector) {
    object Score : Screen("订单", Icons.Filled.Home)
    object Site : Screen("驿站", Icons.Filled.DateRange)
    object Mine : Screen("我的", Icons.Filled.Person)
}

@Composable
fun MainFrame() {
    val navController = rememberNavController()
    val screens = listOf(
        Screen.Score,
        Screen.Site,
        Screen.Mine
    )
    Scaffold(
        topBar = {
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
        },
        bottomBar = {
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
        },
        content = {
            NavHost(navController = navController, startDestination = Screen.Score.route) {
                composable(Screen.Score.route) { ScoreScreen() }
                composable(Screen.Site.route) { SiteScreen() }
                composable(Screen.Mine.route) { MineScreen() }
            }
        }
    )
}

@Preview
@Composable
fun MainFramePreview() {
    MainFrame()
}

