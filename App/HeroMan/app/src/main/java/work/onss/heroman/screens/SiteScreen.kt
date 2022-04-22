package work.onss.heroman.screens


import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.lifecycle.HiltViewModel
import work.onss.heroman.data.repository.site.SiteRepository
import work.onss.heroman.screens.common.BottomBar
import work.onss.heroman.screens.common.SiteList
import work.onss.heroman.screens.common.TopBar
import work.onss.heroman.util.Constants
import javax.inject.Inject

@ExperimentalPagingApi
@Composable
fun SiteScreen(navController: NavHostController, siteViewModel: SiteViewModel = hiltViewModel()) {
    val sites = siteViewModel.sites.collectAsLazyPagingItems()
    Scaffold(
        topBar = { TopBar() },
        content = {
            SiteList(items = sites)
        },
        bottomBar = {
            BottomBar(screens = Constants.screens, navController = navController)
        }
    )
}

@ExperimentalPagingApi
@HiltViewModel
class SiteViewModel @Inject constructor(scoreRepository: SiteRepository) : ViewModel() {
    val sites = scoreRepository.getAll()
}