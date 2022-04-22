package work.onss.heroman.data.repository.site

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import work.onss.heroman.data.repository.Database
import javax.inject.Inject

@ExperimentalPagingApi
class SiteRepository @Inject constructor(
    private val siteApi: SiteApi,
    private val database: Database
) : RemoteMediator<Int, Site>() {
    fun getAll(): Flow<PagingData<Site>> {
        val pagingSourceFactory = { database.SiteDao().getAll() }
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = this,
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Site>): MediatorResult {
        TODO("Not yet implemented")
    }
}