package work.onss.heroman.screens.common


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import coil.annotation.ExperimentalCoilApi
import work.onss.heroman.data.repository.score.Score

@Composable
fun ScoreItem(item: Score) {
    Card {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Text(
                text = "商户名称：${item.storeShortname}",
            )
            Text(
                text = "取货地址：${item.storeAddressName}",
            )
            Text(
                text = "配送地址：${item.addressName}",
            )
        }
    }
}

@Composable
fun ScoreList(items: LazyPagingItems<Score>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {

        if (items.loadState.refresh == LoadState.Loading) {
            item {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Text(
                        text = "加载中。。。",
                    )
                }
            }
        }

        items(items = items, key = { item -> item.id }) { item ->
            item?.let { ScoreItem(item = it) }
        }

        if (items.loadState.append == LoadState.Loading) {
            item {
                Card {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(50.dp),
                    ) {
                        Text(
                            text = "加载中。。。",
                        )
                    }
                }
            }
        }

    }
}


