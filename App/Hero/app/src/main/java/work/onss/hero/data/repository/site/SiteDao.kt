package work.onss.hero.data.repository.site

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query

@Dao
interface SiteDao {

    @Query("SELECT * FROM site")
    fun getAll(): PagingSource<Int, Site>

}