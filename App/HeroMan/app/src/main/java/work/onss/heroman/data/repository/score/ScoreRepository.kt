package work.onss.heroman.data.repository.score

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import work.onss.heroman.data.repository.Database
import javax.inject.Inject

@ExperimentalPagingApi
class ScoreRepository @Inject constructor(
    private val scoreApi: ScoreApi,
    private val database: Database
) : RemoteMediator<Int, Score>() {
    fun getAll(): Flow<PagingData<Score>> {
        val pagingSourceFactory = { database.ScoreDao().getAll() }
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = this,
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Score>): MediatorResult {
        val scoreDao = database.ScoreDao();
        return try {
            when (loadType) {
                LoadType.REFRESH -> {
                    scoreDao.deleteAll();
                    val data = scoreApi.getAll(0, 10)
                    scoreDao.insert(data.content)
                }
                LoadType.PREPEND -> {
                    val firstItemOrNull = state.firstItemOrNull();
                    firstItemOrNull?.id
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val lastItemOrNull = state.lastItemOrNull()
                    lastItemOrNull?.id
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }
            MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: Exception) {
            e.printStackTrace()
            return MediatorResult.Error(e)
        }
    }
}