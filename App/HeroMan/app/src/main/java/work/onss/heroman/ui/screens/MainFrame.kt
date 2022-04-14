package work.onss.heroman.ui.screens


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import work.onss.heroman.model.entity.NavigationItem


@Composable
fun MainFrame() {

    val navigationItems = listOf(
        NavigationItem(title = "订单", icon = Icons.Filled.Home),
        NavigationItem(title = "驿站", icon = Icons.Filled.DateRange),
        NavigationItem(title = "我的", icon = Icons.Filled.Person),
    )

    var currentNavigationIndex by remember {
        mutableStateOf(0)
    }

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
                navigationItems.forEachIndexed { index, navigationItem ->
                    BottomNavigationItem(
                        selected = currentNavigationIndex == index,
                        onClick = {
                            currentNavigationIndex = index
                        },
                        icon = {
                            Icon(
                                imageVector = navigationItem.icon,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(text = navigationItem.title)
                        },
                    )
                }
            }
        }
    ) {
        when (currentNavigationIndex) {
            0 -> ScoreScreen()
            1 -> SiteScreen()
            2 -> MineScreen()
        }
    }
}

@Preview
@Composable
fun MainFramePreview() {
    MainFrame()
}

