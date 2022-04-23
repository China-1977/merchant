package work.onss.heroman.data.repository.key

import androidx.room.Dao
import androidx.room.Query

@Dao
interface KeysDao {

    @Query("SELECT * FROM `key` WHERE id =:id")
    suspend fun getAll(id: String): Key
}