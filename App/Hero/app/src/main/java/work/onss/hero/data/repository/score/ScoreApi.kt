package work.onss.hero.data.repository.score

import retrofit2.http.GET
import retrofit2.http.Query
import work.onss.hero.data.di.Page


interface ScoreApi {
    @GET("scores")
    suspend fun getAll(
        @Query("id") id: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Page<Score>

}