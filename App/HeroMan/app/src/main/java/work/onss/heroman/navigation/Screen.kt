package work.onss.heroman.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector) {
    object Score : Screen("订单", Icons.Filled.Home)
    object Site : Screen("驿站", Icons.Filled.DateRange)
    object Mine : Screen("我的", Icons.Filled.Person)

}
