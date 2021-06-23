package barbarabilonic.ferit.activitytracker.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import barbarabilonic.ferit.activitytracker.ActivitySetUpInfo
import barbarabilonic.ferit.activitytracker.ActivityType
import barbarabilonic.ferit.activitytracker.EditTextInputFilter
import barbarabilonic.ferit.activitytracker.PermissionUtility
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
            requestPermissions()
            viewModel.setActivitySetUpInfo(getActivitySetUp())
        }
        binding.etHours.filters= arrayOf(EditTextInputFilter("0","23"))
        binding.etMinutes.filters= arrayOf(EditTextInputFilter("0","59"))
        binding.etDistance.filters= arrayOf(EditTextInputFilter("0","10"))
        return binding.root
    }

    private fun getActivitySetUp() : ActivitySetUpInfo{
        var time:Long
        var distance:Double
        var type:ActivityType
            if (binding.etHours.text==null || binding.etHours.text.toString()==""){
                time=TimeUnit.HOURS.toMillis(0L)
            }else{
                time=TimeUnit.HOURS.toMillis(binding.etHours.text.toString().trim().toLong())
            }
        if(binding.etMinutes.text==null || binding.etMinutes.text.toString()==""){
            time+=TimeUnit.MINUTES.toMillis(0L)
        }
        else{
            time+=TimeUnit.MINUTES.toMillis(binding.etMinutes.text.toString().trim().toLong())
        }

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