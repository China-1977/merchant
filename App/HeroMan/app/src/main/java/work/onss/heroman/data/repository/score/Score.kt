package work.onss.heroman.data.repository.score

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "score")
data class Score(
    @PrimaryKey
    val id: Int,
    val storeShortname: String,
    val storeAddressName: String,
    val addressName: String
)
