package work.onss.hero.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dagger.hilt.android.lifecycle.HiltViewModel
import work.onss.hero.data.repository.score.Score
import work.onss.hero.data.repository.score.ScoreRepository
import javax.inject.Inject


@ExperimentalPagingApi
@HiltViewModel
class ScoreViewModel @Inject constructor(private val scoreRepository: ScoreRepository) : ViewModel() {
    @Composable
    fun item(item: Score) {
        Card {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text("id：${item.id}")
                Text("商户名称：${item.storeShortname}")
                Text("取货地址：${item.storeAddressName}")
                Text("配送地址：${item.addressName}")
            }
        }
    }

    @Composable
    fun list() {
        val refreshState = rememberSwipeRefreshState(isRefreshing = false)
        val items = scoreRepository.getAll().collectAsLazyPagingItems()
        val scrollState = rememberLazyListState()
        SwipeRefresh(state = refreshState, onRefresh = { items.refresh() }) {
            Log.d("Error", items.loadState.toString())
            LazyColumn(verticalArrangement = Arrangement.spacedBy(1.dp), state = scrollState) {
                items(items = items, key = { item -> item.id }) { item ->
                    item?.let { item(item = it) }
                }
            }
        }
    }
}