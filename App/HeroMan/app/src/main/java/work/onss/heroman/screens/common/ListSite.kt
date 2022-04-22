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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import work.onss.heroman.data.repository.site.Site

@Composable
fun SiteItem(item: Site) {
    Card {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Text(
                text = item.addressName,
            )
            Text(
                text = item.addressDetail,
            )
        }
    }
}

@Composable
fun SiteList(items: LazyPagingItems<Site>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        items(items = items, key = { item -> item.id }) { item ->
            item?.let { SiteItem(item = it) }
        }
    }
}


