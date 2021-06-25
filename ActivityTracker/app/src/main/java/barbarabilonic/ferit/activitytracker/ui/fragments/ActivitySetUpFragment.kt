package barbarabilonic.ferit.activitytracker.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import barbarabilonic.ferit.activitytracker.*
import barbarabilonic.ferit.activitytracker.databinding.SetUpActivityFragmentBinding
import barbarabilonic.ferit.activitytracker.ui.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

class ActivitySetUpFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private val viewModel by sharedViewModel<MainViewModel> ( )
    private lateinit var binding: SetUpActivityFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= SetUpActivityFragmentBinding.inflate(inflater,container,false)
        binding.btnStart.setOnClickListener {

            viewModel.setActivitySetUpInfo(getActivitySetUp())
        }
        binding.npHours.minValue=0
        binding.npHours.maxValue=23
        binding.npHours.wrapSelectorWheel=false
        binding.npMinutes.minValue=0
        binding.npMinutes.maxValue=59
        binding.npMinutes.wrapSelectorWheel=false
        binding.etDistance.filters= arrayOf(EditTextInputFilterDouble("0.0","50.0"))
        return binding.root
        requestPermissions()
    }

    private fun getActivitySetUp() : ActivitySetUpInfo{
        var time:Long=0
        var distance:Double
        var type:ActivityType
        time+=TimeUnit.HOURS.toMillis(binding.npHours.value.toLong())
        time+=TimeUnit.MINUTES.toMillis(binding.npMinutes.value.toLong())

        if(binding.etDistance.text==null || binding.etDistance.text.toString()==""){
            distance=0.0
        }else{
            distance=binding.etDistance.text.toString().trim().toDouble()
        }
        if(binding.rgActivityChoice.checkedRadioButtonId<0){
            type=ActivityType.RUN
        }else if(binding.rbRun.isChecked){
            type=ActivityType.RUN
        }else if(binding.rbCycle.isChecked){
            type=ActivityType.CYCLE
        }else{
            type=ActivityType.WALK
        }
        return ActivitySetUpInfo(type,time,distance)

    }

    private fun requestPermissions() {
        if (PermissionUtility.hasLocationPermissions(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "To use the app you need to accept location permission.",
                0,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {

                EasyPermissions.requestPermissions(
                    this,
                    "To use the app you need to accept location permission.",
                    0,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )


        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }
        else{
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }


}