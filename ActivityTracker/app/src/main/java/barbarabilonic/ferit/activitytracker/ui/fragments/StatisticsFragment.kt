package barbarabilonic.ferit.activitytracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import barbarabilonic.ferit.activitytracker.databinding.StatisticFragmentBinding

class StatisticsFragment : Fragment() {
    private lateinit var binding:StatisticFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= StatisticFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }
}