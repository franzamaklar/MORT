package com.masters.mort

import android.app.Application
import com.masters.mort.di.repositoryModule
import com.masters.mort.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MORTApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(
                if(BuildConfig.DEBUG)
                    Level.ERROR
                else
                    Level.NONE
            )
            androidContext(this@MORTApplication)
            modules(
                repositoryModule,
                viewModelModule
            )
        }
    }
}