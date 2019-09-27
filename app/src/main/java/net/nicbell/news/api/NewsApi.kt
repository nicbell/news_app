package net.nicbell.news.api

import net.nicbell.news.api.news.NewsArticle
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    /**
     * Get news articles.
     */
    @GET("content?&countryCode=US&language=en")
    suspend fun getNews(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Paging<NewsArticle>
}