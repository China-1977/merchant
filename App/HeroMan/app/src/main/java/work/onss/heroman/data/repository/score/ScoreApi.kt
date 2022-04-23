package work.onss.heroman.data.repository.score

import retrofit2.http.GET
import retrofit2.http.Query


interface ScoreApi {
    @GET("scores")
    suspend fun getAll(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Page<Score>

}