package work.onss.heroman.screens

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.lifecycle.HiltViewModel
import work.onss.heroman.data.repository.score.ScoreRepository
import work.onss.heroman.screens.common.BottomBar
import work.onss.heroman.screens.common.ScoreList
import work.onss.heroman.screens.common.TopBar
import work.onss.heroman.util.Constants
import javax.inject.Inject

@ExperimentalPagingApi
@Composable
fun ScoreScreen(
    navController: NavHostController,
    scoreViewModel: ScoreViewModel = hiltViewModel()
) {
    val scores = scoreViewModel.scores.collectAsLazyPagingItems()
    Scaffold(
        topBar = { TopBar() },
        content = {
            ScoreList(items = scores)
        },
        bottomBar = {
            BottomBar(screens = Constants.screens, navController = navController)
        }
    )
}

@ExperimentalPagingApi
@HiltViewModel
class ScoreViewModel @Inject constructor(scoreRepository: ScoreRepository) : ViewModel() {
    val scores = scoreRepository.getAll()
}