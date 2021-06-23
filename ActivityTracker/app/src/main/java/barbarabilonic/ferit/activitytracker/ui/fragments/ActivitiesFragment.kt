package barbarabilonic.ferit.activitytracker.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import barbarabilonic.ferit.activitytracker.ActivityInfo
import barbarabilonic.ferit.activitytracker.ActivityTracker.Companion.application
import barbarabilonic.ferit.activitytracker.OnActivityButtonsClickListener
import barbarabilonic.ferit.activitytracker.R
import barbarabilonic.ferit.activitytracker.adapters.ActivityAdapter
import barbarabilonic.ferit.activitytracker.databinding.ActivitiesFragmentBinding
import barbarabilonic.ferit.activitytracker.ui.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class ActivitiesFragment(private val activities:MutableList<ActivityInfo>) : Fragment(){
    private lateinit var binding: ActivitiesFragmentBinding
    private lateinit var onButtonsClickListener: OnActivityButtonsClickListener
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= ActivitiesFragmentBinding.inflate(inflater,container,false)
        setUpRecyclerView()
        var activityChoices= arrayOf<String>("All","Run","Cycle","Walk")
        val activitiesArrayAdapter:ArrayAdapter<String> = ArrayAdapter(application, R.layout.spinner_item,activityChoices)
        binding.spinnerActivityChoice.adapter=activitiesArrayAdapter
        var sortChoices= arrayOf<String>("Date","Distance","Duration","Calories","Average speed")
        val sortArrayAdapter:ArrayAdapter<String> = ArrayAdapter(application,R.layout.spinner_item,sortChoices)
        binding.spinnerSortChoice.adapter=sortArrayAdapter
        binding.spinnerActivityChoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onButtonsClickListener.onActivitiesSpinnerItemClicked(position,binding.spinnerSortChoice.selectedItemPosition)
            }

        }
        binding.spinnerSortChoice.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                onButtonsClickListener.onSortSpinnerItemClicked(position,binding.spinnerActivityChoice.selectedItemPosition)
            }

        }
        binding.btnStartActivity.setOnClickListener {
            onButtonsClickListener.onStartActivityButtonClicked()
        }






        return binding.root
        viewModel.activities.observe(viewLifecycleOwner,{refreshData(viewModel.activities.value?: mutableListOf<ActivityInfo>())})
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnActivityButtonsClickListener){
            onButtonsClickListener = context
        }

    }

    fun refreshData(refreshedActivityInfos:MutableList<ActivityInfo>){
        (binding.rvActivities.adapter as ActivityAdapter).refreshData(refreshedActivityInfos)
    }

    fun setUpRecyclerView(){
        binding.rvActivities.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvActivities.adapter= ActivityAdapter(
            activities
        )

    }
}