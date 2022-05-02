package work.onss.hero.data.repository.site

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import work.onss.hero.data.repository.Database
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
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItemOrNull = state.lastItemOrNull()
                    lastItemOrNull?.id
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }
            val data = siteApi.getAll(currentPage, 10)

            MediatorResult.Success(endOfPaginationReached = data.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}