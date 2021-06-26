package barbarabilonic.ferit.activitytracker.dataModel

import barbarabilonic.ferit.activitytracker.utilities.ActivityType

data class ActivityInfo (
    val activityType: ActivityType = ActivityType.RUN,
    val date : Long=System.currentTimeMillis(),
    val duration:Long = 1000L,
    val distance: Double = 0.0,
    val caloriesBurned:Int=0,
    val averageSpeed:Double=0.0


        )