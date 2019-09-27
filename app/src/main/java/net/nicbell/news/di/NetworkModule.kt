package net.nicbell.news.di

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import net.nicbell.news.R
import net.nicbell.news.api.NewsApi
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.*

/**
 * Network module for DI,
 * I wouldn't usually have these types of functions directly in here but I don't want to spend too long..
 */
val networkModule = module {
    /**
     * Create OkHttpClient
     */
    fun okHttpClientWithToken(application: Application): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addNetworkInterceptor { chain ->
                var request = chain.request()

                val url = request.url().newBuilder()
                    .addQueryParameter("key", application.getString(R.string.api_key))
                    .addQueryParameter("publisherId", application.getString(R.string.api_publisher_id))
                    .addQueryParameter("userId", application.getString(R.string.api_user_id))
                    .build()

                request = request.newBuilder()
                    .url(url)
                    .build()

                chain.proceed(request)
            }
            .build()
    }

    /**
     * Create retrofit
     */
    fun retrofit(application: Application, client: OkHttpClient): Retrofit {
        val moshi = Moshi
            .Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .build()

        return Retrofit.Builder()
            .baseUrl(application.getString(R.string.api_base))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    single { okHttpClientWithToken(androidApplication()) }
    single { retrofit(androidApplication(), get()) }
    single { get<Retrofit>().create<NewsApi>() }
}