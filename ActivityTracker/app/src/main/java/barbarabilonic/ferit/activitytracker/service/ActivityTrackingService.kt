package barbarabilonic.ferit.activitytracker.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getService
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import barbarabilonic.ferit.activitytracker.ActivityInfo
import barbarabilonic.ferit.activitytracker.Constants.NOTIFICATION_CHANNEL_ID
import barbarabilonic.ferit.activitytracker.Constants.NOTIFICATION_CHANNEL_NAME
import barbarabilonic.ferit.activitytracker.Constants.NOTIFICATION_ID
import barbarabilonic.ferit.activitytracker.Constants.PAUSE
import barbarabilonic.ferit.activitytracker.Constants.SHOW_ACTIVITY_TRACKING_FRAGMENT
import barbarabilonic.ferit.activitytracker.Constants.START_OR_RESUME
import barbarabilonic.ferit.activitytracker.Constants.STOP
import barbarabilonic.ferit.activitytracker.PermissionUtility
import barbarabilonic.ferit.activitytracker.R
import barbarabilonic.ferit.activitytracker.formatNotificationText
import barbarabilonic.ferit.activitytracker.ui.activities.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


typealias Line=MutableList<com.google.android.gms.maps.model.LatLng>
typealias Lines=MutableList<Line>
class ActivityTrackingService : LifecycleService(){

    var isFirst=true
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var isTimerEnabled=false
    private var durationTimeBetweenStartAndStop=0L
    private var totalTime=0L
    private var startTime=0L
    private var lastSecondTimeStamp=0L

    private lateinit var notificationBuilder:NotificationCompat.Builder
    private lateinit var currentNotificationBuilder:NotificationCompat.Builder
    private var serviceKilled=false



    companion object{
        val distance=MutableLiveData<Double>()
         val timeInSeconds = MutableLiveData<Long>()
        val isTracking=MutableLiveData<Boolean>()
        val pathPoints=MutableLiveData<Lines>()
        val timeInMilliseconds=MutableLiveData<Long>()

    }

    private fun postInitValues(){

    isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeInSeconds.postValue(0L)
        timeInMilliseconds.postValue(0L)
        distance.postValue(0.0)
    }



    override fun onCreate() {
        super.onCreate()
        postInitValues()
        fusedLocationProviderClient= FusedLocationProviderClient(this)
        isTracking.observe(this, {
            updateLocationTracking(it)
            updateNotification(it)
        })
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                START_OR_RESUME->{
                    if(isFirst){
                        startForegroundService()
                        isFirst=false
                    }else{
                        startTimer()
                    }
                }
                PAUSE->{
                    pauseService()
                }
                STOP->{
                    stopService()
                }

                else -> {

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTimer(){
        addNewLine()
        isTracking.postValue(true)
        startTime=System.currentTimeMillis()
        isTimerEnabled=true
        CoroutineScope(Dispatchers.Main).launch {
            while(isTracking.value!!){
                durationTimeBetweenStartAndStop=System.currentTimeMillis()-startTime
                timeInMilliseconds.postValue((totalTime+durationTimeBetweenStartAndStop).toLong())
                if(timeInMilliseconds.value!! >=lastSecondTimeStamp+1000L){
                    timeInSeconds.postValue(timeInSeconds.value!! +1)
                    lastSecondTimeStamp+=1000L
                }
                delay(50L)
            }
            totalTime+=durationTimeBetweenStartAndStop
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking:Boolean){
        if(isTracking){
            if(PermissionUtility.hasLocationPermissions(this)){
                val request=LocationRequest().apply {
                    interval=5000L
                    fastestInterval=2000L
                    priority=PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(request,locationCallback,Looper.getMainLooper())
            }
        }
        else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback=object: LocationCallback(){
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if(isTracking.value!!){
                result?.locations?.let{
                    for(location in it){
                        addPathPoint(location)

                    }
                }
            }
        }

    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled=false
    }

    private fun addPathPoint(location:Location?){
        location?.let {
            val position=com.google.android.gms.maps.model.LatLng(location.latitude,location.longitude)
            if(pathPoints.value!=null && pathPoints.value?.last()?.size!! >0){
                val result=FloatArray(1)
                Location.distanceBetween(
                    pathPoints.value!!.last().last().latitude,
                    pathPoints.value!!.last().last().longitude,
                    location.latitude,
                    location.longitude,
                    result

                )
                val currDist= distance.value?:0.0
                distance.postValue(currDist+result[0])
            }

                pathPoints.value?.apply {
                    last().add(position)
                    pathPoints.postValue(this)
                }
            }
        }

    private fun addNewLine()= pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    }?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun startForegroundService(){
        startTimer()
        isTracking.postValue(true)
        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

         notificationBuilder=NotificationCompat.Builder(
            this,
            NOTIFICATION_CHANNEL_ID,
        )
            .setAutoCancel(false)
            .setOngoing(true)
        .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Activity Tracker")
            .setContentText("00:00:00   0.0km")
            .setContentIntent(getMainActivityPendingIntent())
        currentNotificationBuilder=notificationBuilder
        startForeground(NOTIFICATION_ID,notificationBuilder.build())

        timeInSeconds.observe(this, {
            if (!serviceKilled){
            val notification = currentNotificationBuilder
                .setContentText(
                    formatNotificationText(it, distance.value ?: 0.0)
                )
            notificationManager.notify(NOTIFICATION_ID, notification.build())
        }
        })

    }

    private fun getMainActivityPendingIntent()=PendingIntent.getActivity(
        this,
        0,
        Intent(this,MainActivity::class.java).also {
            it.action= SHOW_ACTIVITY_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )



    private fun updateNotification(isTracking: Boolean){
        val stateText=if(isTracking) "Pause" else "Resume"
        val pendingIntent=if(isTracking){
            val pauseIntent=Intent(this,ActivityTrackingService::class.java).apply {
                action=PAUSE
            }
            PendingIntent.getService(
                this,
                1,
                pauseIntent,
                FLAG_UPDATE_CURRENT
            )
        }else{
            val resumeIntent=Intent(this,ActivityTrackingService::class.java).apply {
                action= START_OR_RESUME
            }
            getService(
                this,
                2,
                resumeIntent,
                FLAG_UPDATE_CURRENT
            )
        }
        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible=true
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())

        }
        if(!serviceKilled) {
            currentNotificationBuilder = notificationBuilder
                .addAction(R.drawable.ic_pause, stateText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager:NotificationManager){
        val channel=NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

    private fun stopService(){
        serviceKilled=true
        isFirst=true
        pauseService()
        stopForeground(true)
        postInitValues()

         durationTimeBetweenStartAndStop=0L
         totalTime=0L
         startTime=0L
         lastSecondTimeStamp=0L

        stopSelf()
    }


}