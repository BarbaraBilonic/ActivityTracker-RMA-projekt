package barbarabilonic.ferit.activitytracker.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import barbarabilonic.ferit.activitytracker.Constants.NOTIFICATION_CHANNEL_ID
import barbarabilonic.ferit.activitytracker.Constants.NOTIFICATION_CHANNEL_NAME
import barbarabilonic.ferit.activitytracker.Constants.NOTIFICATION_ID
import barbarabilonic.ferit.activitytracker.Constants.PAUSE
import barbarabilonic.ferit.activitytracker.Constants.SHOW_ACTIVITY_TRACKING_FRAGMENT
import barbarabilonic.ferit.activitytracker.Constants.START_OR_RESUME
import barbarabilonic.ferit.activitytracker.Constants.STOP
import barbarabilonic.ferit.activitytracker.PermissionUtility
import barbarabilonic.ferit.activitytracker.R
import barbarabilonic.ferit.activitytracker.ui.activities.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult


typealias Line=MutableList<com.google.android.gms.maps.model.LatLng>
typealias Lines=MutableList<Line>
class ActivityTrackingService : LifecycleService(){

    var isFirst=true
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    companion object{
        val isTracking=MutableLiveData<Boolean>()
        val pathPoints=MutableLiveData<Lines>()
    }

    private fun postInitValues(){
    isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        postInitValues()
        fusedLocationProviderClient= FusedLocationProviderClient(this)
        isTracking.observe(this, {
            updateLocationTracking(it)
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
                        startForegroundService()
                    }
                }
                PAUSE->{
                    pauseService()
                }
                STOP->{

                }

                else -> {

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
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
                        Log.i("NEW LOCATION","${location.latitude}, ${location.longitude}" )
                    }
                }
            }
        }

    }

    private fun pauseService(){
        isTracking.postValue(false)
    }

    private fun addPathPoint(location:Location?){
        location?.let {
            val position=com.google.android.gms.maps.model.LatLng(location.latitude,location.longitude)
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
        addNewLine()
        isTracking.postValue(true)
        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder=NotificationCompat.Builder(
            this,
            NOTIFICATION_CHANNEL_ID,
        )
            .setAutoCancel(false)
            .setOngoing(true)
        .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Activity Tracker")
            .setContentText("00:00:00   0.0km")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID,notificationBuilder.build())

    }

    private fun getMainActivityPendingIntent()=PendingIntent.getActivity(
        this,
        0,
        Intent(this,MainActivity::class.java).also {
            it.action= SHOW_ACTIVITY_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT
    )


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager:NotificationManager){
        val channel=NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }


}