package barbarabilonic.ferit.activitytracker

fun getIcon(type:ActivityType):Int{
    when(type){
        ActivityType.RUN->return R.drawable.ic_run_icon
        ActivityType.CYCLE->return R.drawable.ic_cycle_icon
        else ->return R.drawable.ic_walk_icon
    }
}