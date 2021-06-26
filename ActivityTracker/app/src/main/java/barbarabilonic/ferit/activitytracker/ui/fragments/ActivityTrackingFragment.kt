package barbarabilonic.ferit.activitytracker.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import barbarabilonic.ferit.activitytracker.utilities.ActivityType
import barbarabilonic.ferit.activitytracker.utilities.Constants.PAUSE
import barbarabilonic.ferit.activitytracker.utilities.Constants.START_OR_RESUME
import barbarabilonic.ferit.activitytracker.utilities.Constants.STOP
import barbarabilonic.ferit.activitytracker.R
import barbarabilonic.ferit.activitytracker.persistance.SharedPreferencesManager
import barbarabilonic.ferit.activitytracker.databinding.ActivityTrackingFragmentBinding
import barbarabilonic.ferit.activitytracker.formatDuration
import barbarabilonic.ferit.activitytracker.service.ActivityTrackingService
import barbarabilonic.ferit.activitytracker.service.Line
import barbarabilonic.ferit.activitytracker.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ActivityTrackingFragment: Fragment() {
    private val sharedPreferencesManager= SharedPreferencesManager()
    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var binding:ActivityTrackingFragmentBinding
    private var map: GoogleMap? = null

    private var isTracking=false
    private var pathPoints= mutableListOf<Line>()
    private var distance=0.0
    private var timeInMilliseconds=0L
    private var timeInSeconds=0L
    private lateinit var type: ActivityType



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= ActivityTrackingFragmentBinding.inflate(inflater,container,false)
        binding.btnStartStop.setOnClickListener {
            startStopActivity()
        }
        binding.btnFinnish.setOnClickListener {
            finnishActivity()
        }
        binding.btnCancel.setOnClickListener { showCancelDialog()}

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mv.onCreate(savedInstanceState)
        binding.mv.getMapAsync {
            map=it
            addAllLines()
        }

        startObservers()
    }
    private fun sendCommandToActivityTracingService(action:String)=
        Intent(requireContext(), ActivityTrackingService::class.java).also{
            it.action=action
            requireContext().startService(it)
        }

    private fun finnishActivity(){

        stopActivity()
        binding.btnFinnish.visibility=View.GONE
        binding.btnCancel.visibility=View.GONE
        viewModel.addActivity(type,timeInMilliseconds,distance)

    }
    override fun onResume() {
        super.onResume()
       (binding.mv)?.onResume()
    }

    override fun onStart() {
        super.onStart()
       (binding.mv)?.onStart()
    }

    override fun onStop() {
        super.onStop()
        (binding.mv)?.onStop()
    }

    override fun onPause() {
        super.onPause()
        (binding.mv)?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        (binding.mv)?.onLowMemory()
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mv?.onSaveInstanceState(outState)

    }

    private fun addLatestLine(){
        if(pathPoints.isNotEmpty() && pathPoints.last().size>1){
            val secondLast=pathPoints.last()[pathPoints.last().size-2]
            val last=pathPoints.last().last()
            val options=PolylineOptions()
                .color(R.color.black)
                .width(10f)
                .add(secondLast)
                .add(last)
            map?.addPolyline(options)

        }
    }

    private fun addAllLines(){
        for(line in pathPoints){
            val options=PolylineOptions()
                .color(R.color.black)
                .width(10f)
                .addAll(line)
            map?.addPolyline(options)
        }
    }

    private fun moveCamera(){
        if(pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    15f
                )
            )
        }
    }

    private fun updateTracking(isTracking:Boolean){
        this.isTracking=isTracking
        if(timeInSeconds>0){
            binding.btnCancel.visibility=View.VISIBLE
            binding.btnFinnish.visibility=View.VISIBLE
        }
        if(!isTracking){
            binding.btnFinnish.visibility=View.VISIBLE
            binding.btnCancel.visibility=View.VISIBLE
            binding.btnStartStop.text="Start"

        }else{
            binding.btnStartStop.text="Stop"
            binding.btnCancel.visibility=View.GONE
            binding.btnFinnish.visibility=View.GONE

        }
    }

    private fun showCancelDialog(){
        val dialog=MaterialAlertDialogBuilder(requireContext())
            .setTitle("Cancel activity?")
            .setMessage("Are you sure you want to cancel current activity?")
            .setPositiveButton("Yes"){_,_->
                cancelActivity()



            }
            .setNegativeButton("No"){dialogInterface,_->
                dialogInterface.cancel()
            }
            .create()
        dialog.show()
    }
     private fun cancelActivity(){
         stopActivity()
         binding.btnFinnish.visibility=View.GONE
         binding.btnCancel.visibility=View.GONE
         viewModel.setIsCanceled(true)
     }
    private fun stopActivity(){
        sendCommandToActivityTracingService(STOP)

    }

    private fun startStopActivity(){
        if(isTracking){

            sendCommandToActivityTracingService(PAUSE)
        }else{
            sendCommandToActivityTracingService(START_OR_RESUME)
        }
    }

    private fun startObservers(){
         isTracking=false
         pathPoints= mutableListOf()
         distance=0.0
         timeInMilliseconds=0L
         timeInSeconds=0L
        ActivityTrackingService.isTracking.observe(viewLifecycleOwner,{
            updateTracking(it)
        })

        ActivityTrackingService.pathPoints.observe(viewLifecycleOwner,{
            pathPoints=it
            addLatestLine()
            moveCamera()
        })

        ActivityTrackingService.timeInSeconds.observe(viewLifecycleOwner, {

            timeInSeconds=it
            val formatedTime= formatDuration(timeInMilliseconds)
            binding.tvTimer.text=formatedTime

        })
        ActivityTrackingService.timeInMilliseconds.observe(viewLifecycleOwner,{
            timeInMilliseconds=it
        })
        ActivityTrackingService.distance.observe(viewLifecycleOwner,{
            distance=it
            binding.tvDistance.text=String.format("%.2f km",distance/1000)

        })
       if(viewModel.activitySetUpInfo.value!=null){
           type= viewModel.activitySetUpInfo.value!!
           sharedPreferencesManager.saveData(type)
       }else{
           type=sharedPreferencesManager.retrieveType()
       }
    }



}