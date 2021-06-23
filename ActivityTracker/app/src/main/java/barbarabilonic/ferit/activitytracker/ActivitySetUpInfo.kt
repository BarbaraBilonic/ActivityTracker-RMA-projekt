package barbarabilonic.ferit.activitytracker

data class ActivitySetUpInfo (
    val type:ActivityType=ActivityType.RUN,
    val time:Long=0L,
    val distance:Double=0.0
        )