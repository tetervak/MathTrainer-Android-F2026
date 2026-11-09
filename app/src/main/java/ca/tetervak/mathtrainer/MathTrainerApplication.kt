package ca.tetervak.mathtrainer

import android.app.Application
import ca.tetervak.mathtrainer.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MathTrainerApplication : Application(){

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MathTrainerApplication)
            modules(appModules)
        }
    }
}