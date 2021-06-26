package barbarabilonic.ferit.activitytracker.persistance

import android.content.Context
import barbarabilonic.ferit.activitytracker.ActivityTracker.Companion.application
import barbarabilonic.ferit.activitytracker.utilities.ActivityType

class SharedPreferencesManager {
    companion object {
        const val PREFS_FILE = "MyPreferences"
        const val PREFS_KEY_TYPE="Type"
    }

    fun saveData(type: ActivityType) {
        var input=when(type){
            ActivityType.RUN->1
            ActivityType.CYCLE->2
            ActivityType.WALK->3
        }
        val sharedPreferences = application.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.putInt(PREFS_KEY_TYPE, input).apply()

    }
    fun retrieveType(): ActivityType {
        val sharedPreferences = application.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)
        val data= sharedPreferences.getInt(PREFS_KEY_TYPE,1)
        val type=when(data){
            1-> ActivityType.RUN
            2-> ActivityType.CYCLE
            else-> ActivityType.WALK
        }
        return type
    }

}