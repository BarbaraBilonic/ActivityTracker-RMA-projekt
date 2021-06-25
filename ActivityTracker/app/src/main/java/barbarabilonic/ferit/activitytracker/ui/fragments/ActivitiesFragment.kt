package barbarabilonic.ferit.activitytracker.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import kotlin.math.log


class ActivitiesFragment(private var activities:MutableList<ActivityInfo>) : Fragment(){
    private lateinit var binding: ActivitiesFragmentBinding
    private lateinit var onButtonsClickListener: OnActivityButtonsClickListener
    private val viewModel by sharedViewModel<MainViewModel> ( )
    private var bindingInitialized=false


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





        bindingInitialized=true
        return binding.root

        refreshData(activities)
        viewModel.activities.observe(viewLifecycleOwner,{
            setActivities(it)
        })

    }

    fun setActivities(act:MutableList<ActivityInfo>){
        activities=act

        refreshData(act)



    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnActivityButtonsClickListener){
            onButtonsClickListener = context
        }



    }

    fun refreshData(refreshedActivityInfos:MutableList<ActivityInfo>){
        if(bindingInitialized)
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

    override fun onResume() {
        super.onResume()
        refreshData(activities)
    }
}