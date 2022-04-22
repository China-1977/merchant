package work.onss.heroman.data.repository.site

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface SiteApi {
    @Headers("Authorization: Client-ID ")
    @GET("/photos")
    suspend fun getAll(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<Site>

}