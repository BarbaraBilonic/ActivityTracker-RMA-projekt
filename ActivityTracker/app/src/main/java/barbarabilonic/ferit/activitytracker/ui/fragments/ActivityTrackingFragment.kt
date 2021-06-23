package barbarabilonic.ferit.activitytracker.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import barbarabilonic.ferit.activitytracker.Constants.PAUSE
import barbarabilonic.ferit.activitytracker.Constants.START_OR_RESUME
import barbarabilonic.ferit.activitytracker.R
import barbarabilonic.ferit.activitytracker.databinding.ActivityTrackingFragmentBinding
import barbarabilonic.ferit.activitytracker.service.ActivityTrackingService
import barbarabilonic.ferit.activitytracker.service.Line
import barbarabilonic.ferit.activitytracker.ui.viewmodels.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ActivityTrackingFragment: Fragment() {

    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var binding:ActivityTrackingFragmentBinding
    private var map: GoogleMap? = null

    private var isTracking=false
    private var pathPoints= mutableListOf<Line>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= ActivityTrackingFragmentBinding.inflate(inflater,container,false)
        binding.btnStartStop.setOnClickListener {
            startStopRun()
        }

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
                    20f
                )
            )
        }
    }

    private fun updateTracking(isTracking:Boolean){
        this.isTracking=isTracking
        if(!isTracking){
            binding.btnStartStop.text="Start"
            binding.btnFinnish.visibility=View.VISIBLE
        }else{
            binding.btnStartStop.text="Stop"
            binding.btnFinnish.visibility=View.GONE
        }
    }

    private fun startStopRun(){
        if(isTracking){
            sendCommandToActivityTracingService(PAUSE)
        }else{
            sendCommandToActivityTracingService(START_OR_RESUME)
        }
    }

    private fun startObservers(){
        ActivityTrackingService.isTracking.observe(viewLifecycleOwner,{
            updateTracking(it)
        })

        ActivityTrackingService.pathPoints.observe(viewLifecycleOwner,{
            pathPoints=it
            addLatestLine()
            moveCamera()
        })
    }
}