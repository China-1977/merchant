package work.onss.heroman.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.lifecycle.HiltViewModel
import work.onss.heroman.data.repository.score.ScoreRepository
import work.onss.heroman.screens.common.ScoreList
import javax.inject.Inject

@ExperimentalPagingApi
@Composable
fun ScoreScreen(
    scoreViewModel: ScoreViewModel = hiltViewModel()
) {
    val scores = scoreViewModel.scores.collectAsLazyPagingItems()
    ScoreList(items = scores)
}

@ExperimentalPagingApi
@HiltViewModel
class ScoreViewModel @Inject constructor(scoreRepository: ScoreRepository) : ViewModel() {
    val scores = scoreRepository.getAll()
}