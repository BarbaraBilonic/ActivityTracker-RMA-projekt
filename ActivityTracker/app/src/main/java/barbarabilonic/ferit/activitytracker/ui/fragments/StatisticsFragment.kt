package barbarabilonic.ferit.activitytracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import barbarabilonic.ferit.activitytracker.ActivityType
import barbarabilonic.ferit.activitytracker.databinding.StatisticFragmentBinding
import barbarabilonic.ferit.activitytracker.formatDuration
import barbarabilonic.ferit.activitytracker.ui.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class StatisticsFragment : Fragment() {
    private lateinit var binding:StatisticFragmentBinding
    private val viewModel by sharedViewModel<MainViewModel> (  )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= StatisticFragmentBinding.inflate(inflater,container,false)


        return binding.root
        setUpData()
    }

    override fun onResume() {
        super.onResume()
        setUpData()
    }
    private fun setUpData(){
        binding.tvCaloriesValueR.text=viewModel.getTotalCalories(ActivityType.RUN).toString()
        binding.tvAvgSpeedValueR.text= String.format("%.2f km/h",viewModel.getAverageSpeed(ActivityType.RUN))
        binding.tvTimeValueR.text= formatDuration(viewModel.getTotalTime(ActivityType.RUN))
        binding.tvDistanceValueR.text=String.format("%.2f km",viewModel.getTotalDistance(ActivityType.RUN))

        binding.tvCaloriesValueC.text=viewModel.getTotalCalories(ActivityType.CYCLE).toString()
        binding.tvAvgSpeedValueC.text= String.format("%.2f km/h",viewModel.getAverageSpeed(ActivityType.CYCLE))
        binding.tvTimeValueC.text= formatDuration(viewModel.getTotalTime(ActivityType.CYCLE))
        binding.tvDistanceValueC.text=String.format("%.2f km",viewModel.getTotalDistance(ActivityType.CYCLE))

        binding.tvCaloriesValueW.text=viewModel.getTotalCalories(ActivityType.WALK).toString()
        binding.tvAvgSpeedValueW.text= String.format("%.2f km/h",viewModel.getAverageSpeed(ActivityType.WALK))
        binding.tvTimeValueW.text= formatDuration(viewModel.getTotalTime(ActivityType.WALK))
        binding.tvDistanceValueW.text=String.format("%.2f km",viewModel.getTotalDistance(ActivityType.WALK))
    }
}