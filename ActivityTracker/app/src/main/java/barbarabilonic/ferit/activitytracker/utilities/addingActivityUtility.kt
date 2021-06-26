package barbarabilonic.ferit.activitytracker

import barbarabilonic.ferit.activitytracker.utilities.ActivityType

fun getAverageSpeed(time:Long,distance:Double):Double {

    var timeInHours=time.toDouble()/1000.0/60.0/60.0
    var distanceInKm=distance/1000
    return distanceInKm/timeInHours
}

fun getCaloriesBurned(type: ActivityType, weight:Int, time:Long, distance:Double):Int{
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
    var msToHours=time.toDouble()/1000.0/60.0/60.0
    var caloriesInHour=MET*weight
    var timeDivider=(1.0/msToHours)

    return (caloriesInHour/timeDivider).toInt()
}
