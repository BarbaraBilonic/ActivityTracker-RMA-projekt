package barbarabilonic.ferit.activitytracker

import android.app.Application
import barbarabilonic.ferit.activitytracker.di.appModules
import barbarabilonic.ferit.activitytracker.di.viewModelModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ActivityTracker : Application() {

    companion object{
        lateinit var application:ActivityTracker
    }
    override fun onCreate() {
        super.onCreate()
        application = this

        startKoin {
            androidContext(this@ActivityTracker)
            modules(appModules, viewModelModules)

        }
    }
}