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
        TODO("Not yet implemented")
    }
}