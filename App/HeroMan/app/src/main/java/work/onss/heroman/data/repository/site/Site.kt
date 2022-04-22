package work.onss.heroman.data.repository.site

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "site")
data class Site(
    @PrimaryKey
    val id: Int,
    val addressName: String,
    val addressDetail: String
)
