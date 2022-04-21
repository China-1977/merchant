package work.onss.heroman.DataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import work.onss.heroman.model.entity.Score

class ScorePagingSource() : PagingSource<Int, Score>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Score> {
        val data = mutableListOf(Score("一九七七", "马沙窝村", "星美城市广场二期"));
        return LoadResult.Page(
            data = data,
            prevKey = 1,
            nextKey = 2
        )
    }

    override fun getRefreshKey(state: PagingState<Int, Score>): Int? {
        TODO("Not yet implemented")
    }
}