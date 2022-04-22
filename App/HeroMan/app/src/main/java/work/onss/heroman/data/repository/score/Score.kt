package work.onss.heroman.data.repository.score

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score")
data class Score(
    @PrimaryKey
    val id: Int,
    val shortname: String,
    val storeAddressName: String,
    val addressName: String
)
