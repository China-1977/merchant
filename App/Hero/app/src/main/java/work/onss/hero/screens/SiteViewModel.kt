package work.onss.hero.screens

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
import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import dagger.hilt.android.lifecycle.HiltViewModel
import work.onss.hero.data.repository.site.Site
import work.onss.hero.data.repository.site.SiteRepository
import work.onss.hero.screens.common.dataLoading
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class SiteViewModel @Inject constructor(private val siteRepository: SiteRepository) : ViewModel() {

    @Composable
    fun item(item: Site) {
        Card {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(item.addressName)
                Text(item.addressDetail)
            }
        }
    }

    @ExperimentalPagingApi
    @Composable
    fun list() {
        val items = siteRepository.getAll().collectAsLazyPagingItems()
        if (items.loadState.refresh == LoadState.Loading) {
            dataLoading(message = "加载中。。。")
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            items(items = items, key = { item -> item.id }) { item ->
                item?.let { item(item = it) }
            }
        }
        if (items.loadState.append == LoadState.Loading) {
            dataLoading(message = "加载中。。。")
        }
    }
}