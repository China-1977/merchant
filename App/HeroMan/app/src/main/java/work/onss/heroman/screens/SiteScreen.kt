package work.onss.heroman.screens


import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.lifecycle.HiltViewModel
import work.onss.heroman.data.repository.site.SiteRepository
import work.onss.heroman.screens.common.SiteList
import javax.inject.Inject

@ExperimentalPagingApi
@Composable
fun SiteScreen(siteViewModel: SiteViewModel = hiltViewModel()) {
    val sites = siteViewModel.sites.collectAsLazyPagingItems()
    SiteList(items = sites)
}

@ExperimentalPagingApi
@HiltViewModel
class SiteViewModel @Inject constructor(scoreRepository: SiteRepository) : ViewModel() {
    val sites = scoreRepository.getAll()
}