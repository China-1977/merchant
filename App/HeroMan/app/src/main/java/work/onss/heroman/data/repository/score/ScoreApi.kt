package work.onss.heroman.data.repository.score

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface ScoreApi {
    @Headers("Authorization: Client-ID ")
    @GET("/photos")
    suspend fun getAllScores(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): List<Score>

}