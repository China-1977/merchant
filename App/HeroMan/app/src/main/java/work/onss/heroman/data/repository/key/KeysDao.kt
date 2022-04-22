package work.onss.heroman.data.repository.key

import androidx.room.Dao
import androidx.room.Query

@Dao
interface KeysDao {

    @Query("SELECT * FROM `Key` WHERE id =:id")
    suspend fun getAll(id: String): Key
}