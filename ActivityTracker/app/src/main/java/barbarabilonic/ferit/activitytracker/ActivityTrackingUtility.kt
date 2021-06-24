package barbarabilonic.ferit.activitytracker

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun formatDuration(ms: Long, includeMs: Boolean = false): String {
    var milliseconds = ms
    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    milliseconds -= TimeUnit.HOURS.toMillis(hours)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
    milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
    if(!includeMs) {
        return "${if(hours < 10) "0" else ""}$hours:" +
                "${if(minutes < 10) "0" else ""}$minutes:" +
                "${if(seconds < 10) "0" else ""}$seconds"
    }
    milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
    milliseconds /= 10
    return "${if(hours < 10) "0" else ""}$hours:" +
            "${if(minutes < 10) "0" else ""}$minutes:" +
            "${if(seconds < 10) "0" else ""}$seconds:" +
            "${if(milliseconds < 10) "0" else ""}$milliseconds"
}

fun formatDate(ms:Long):String{
    val formatter = SimpleDateFormat("dd/MM/yyyy");
    return formatter.format( Date(ms))
}

fun formatNotificationText(time:Long, distance:Double):String{

    return formatDuration(time*1000L)+"  "+String.format("%.2f",distance/1000)+"km"

}

