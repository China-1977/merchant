package work.onss.hero.data.repository.site

import retrofit2.http.GET
import retrofit2.http.Query


interface SiteApi {
    @GET("sites")
    suspend fun getAll(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<Site>

}