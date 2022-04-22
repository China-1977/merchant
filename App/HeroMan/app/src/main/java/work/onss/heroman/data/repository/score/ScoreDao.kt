package work.onss.heroman.data.repository.score

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScoreDao {

    @Query("SELECT * FROM score")
    fun getAll(): PagingSource<Int, Score>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scores: List<Score>)

}