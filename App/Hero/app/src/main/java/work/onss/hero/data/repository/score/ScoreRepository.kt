package work.onss.hero.data.repository.score

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import work.onss.hero.data.repository.Database
import javax.inject.Inject

@ExperimentalPagingApi
class ScoreRepository @Inject constructor(
    private val scoreApi: ScoreApi,
    private val database: Database
) : RemoteMediator<Int, Score>() {
    fun getAll(): Flow<PagingData<Score>> {
        val pagingSourceFactory = { database.ScoreDao().getAll() }
        return Pager(
            config = PagingConfig(pageSize = 10, prefetchDistance = 9),
            remoteMediator = this,
            pagingSourceFactory = pagingSourceFactory,
        ).flow
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Score>): MediatorResult {
        val scoreDao = database.ScoreDao();
        try {
            when (loadType) {
                LoadType.REFRESH -> {
                    scoreDao.deleteAll();
                    val data = scoreApi.getAll(0, 0, state.config.pageSize);
                    scoreDao.insert(data.content)
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItemOrNull = state.lastItemOrNull()
                    return if (lastItemOrNull == null) {
                        MediatorResult.Success(endOfPaginationReached = true)
                    } else {
                        val data = scoreApi.getAll(lastItemOrNull.id, 0, state.config.pageSize);
                        if (!data.empty) {
                            scoreDao.insert(data.content)
                        }
                        return MediatorResult.Success(endOfPaginationReached = data.empty)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return MediatorResult.Error(e)
        }
    }
}