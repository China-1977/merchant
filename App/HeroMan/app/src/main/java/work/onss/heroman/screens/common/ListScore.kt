package work.onss.heroman.screens.common


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import dagger.hilt.android.lifecycle.HiltViewModel
import work.onss.heroman.data.repository.score.Score
import work.onss.heroman.data.repository.score.ScoreRepository
import javax.inject.Inject

@Composable
fun ScoreItem(item: Score) {
    Card {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text("商户名称：${item.storeShortname}")
            Text("取货地址：${item.storeAddressName}")
            Text("配送地址：${item.addressName}")
        }
    }
}

@ExperimentalPagingApi
@Composable
fun ScoreList(scoreViewModel: ScoreViewModel = hiltViewModel()) {
    val items = scoreViewModel.scores.collectAsLazyPagingItems()
    if (items.loadState.refresh == LoadState.Loading) {
        DataLoading(message = "加载中。。。")
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(1.dp)) {
        items(items = items, key = { item -> item.id }) { item ->
            item?.let { ScoreItem(item = it) }
        }
    }
    if (items.loadState.append == LoadState.Loading) {
        DataLoading(message = "加载中。。。")
    }
}

@ExperimentalPagingApi
@HiltViewModel
class ScoreViewModel @Inject constructor(scoreRepository: ScoreRepository) : ViewModel() {
    val scores = scoreRepository.getAll()
}


