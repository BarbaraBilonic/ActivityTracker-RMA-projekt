package barbarabilonic.ferit.activitytracker.dataModel

import barbarabilonic.ferit.activitytracker.utilities.ActivityType
import barbarabilonic.ferit.activitytracker.getAverageSpeed
import barbarabilonic.ferit.activitytracker.getCaloriesBurned


class User {
    private  var weight:Int=75
    private var activityInfos:MutableList<ActivityInfo> = mutableListOf()


    fun clearActivityInfo(){
        activityInfos.clear()
    }
    fun getActivities():MutableList<ActivityInfo>{
        return activityInfos
    }
    fun addActivity(activityInfo: ActivityInfo){
        activityInfos.add(activityInfo)
    }
    fun changeWeight(weight:Int){
        this.weight=weight
    }
    fun getWeight():Int=weight

    fun addNewActivity(type: ActivityType, time:Long, distance:Double){
        var date=System.currentTimeMillis()
        var avgSpeed= getAverageSpeed(time,distance)
        var calories= getCaloriesBurned(type,weight,time,distance)
        activityInfos.add(ActivityInfo(type,date,time,distance,calories,avgSpeed))
    }
}