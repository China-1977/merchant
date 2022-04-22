package work.onss.heroman.data.repository.score

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query

@Dao
interface ScoreDao {

    @Query("SELECT * FROM score")
    fun getAll(): PagingSource<Int, Score>
}