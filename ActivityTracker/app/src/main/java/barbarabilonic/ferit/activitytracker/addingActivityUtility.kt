package barbarabilonic.ferit.activitytracker

import java.util.concurrent.TimeUnit

fun getAverageSpeed(time:Long,distance:Double):Double {

    var timeInHours=TimeUnit.MILLISECONDS.toHours(time)
    var distanceInKm=distance/1000
    return distanceInKm/timeInHours
}

fun getCaloriesBurned(type: ActivityType,weight:Int,time:Long,distance:Double):Int{
    var MET:Double
    when(type){
        ActivityType.RUN->{
            MET = if(getAverageSpeed(time,distance)>=9.0){
                11.2
            }else{
                8.8
            }
        }
        ActivityType.CYCLE->{
            MET=6.0
        }
        ActivityType.WALK->{
            MET= if (getAverageSpeed(time, distance)>=4.8){
                3.0
            }else{
                2.0
            }
        }
    }
    var caloriesInHour=MET*weight
    var timeDivider=1/TimeUnit.MILLISECONDS.toHours(time)
    return (caloriesInHour/timeDivider).toInt()
}
