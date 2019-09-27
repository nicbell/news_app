package net.nicbell.news

import android.app.Application
import net.nicbell.news.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NewsApp  : Application() {

    override fun onCreate() {
        super.onCreate()

        //AndroidThreeTen.init(this)

        // start Koin!
        startKoin {
            androidContext(this@NewsApp)
            modules(listOf(networkModule))
        }
    }
}