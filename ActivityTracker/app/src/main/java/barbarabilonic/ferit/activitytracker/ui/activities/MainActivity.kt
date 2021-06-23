package barbarabilonic.ferit.activitytracker.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import barbarabilonic.ferit.activitytracker.ActivityInfo
import barbarabilonic.ferit.activitytracker.OnSignedInRegButtonClicked
import barbarabilonic.ferit.activitytracker.OnActivityButtonsClickListener
import barbarabilonic.ferit.activitytracker.ui.viewmodels.MainViewModel
import barbarabilonic.ferit.activitytracker.R
import barbarabilonic.ferit.activitytracker.databinding.ActivityMainBinding
import barbarabilonic.ferit.activitytracker.ui.fragments.*

import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), OnSignedInRegButtonClicked, OnActivityButtonsClickListener{
    private val viewModel by viewModel<MainViewModel>()
    private lateinit var binding: ActivityMainBinding
    private  val activitiesFragment=ActivitiesFragment( mutableListOf<ActivityInfo>())
    private  val statisticsFragment=StatisticsFragment()
    private  val signInFragment=SignInFragment()
    private  val registerFragment=RegisterFragment()
    private val resetPasswordFragment=ResetPasswordFragment()
    private val activitySetUpFragment=ActivitySetUpFragment()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityMainBinding.inflate(layoutInflater).also {
            it.bottomNavController.setOnNavigationItemSelectedListener {it2->
                when(it2.itemId){
                    R.id.nav_activities -> {
                        setCurrentFragment(activitiesFragment,true)

                    }
                    R.id.nav_statistics -> {
                        setCurrentFragment(statisticsFragment,true)
                    }
                }
                true
            }
        }
        setContentView(binding.root)



        viewModel.getAuthUserLiveData().observe(this,{checkUserLoggedInStatus()})

    }


    private fun checkUserLoggedInStatus(){
        if(viewModel.checkIfUserIsSignedIn()) {
            setCurrentFragment(activitiesFragment,true)
        }else{
            setCurrentFragment(signInFragment,false)
        }
    }

    private fun setCurrentFragment(fragment: Fragment, isPartOfBottomNav:Boolean){
        if(isPartOfBottomNav){
            binding.bottomNavController.visibility=View.VISIBLE
        }
        else{
            binding.bottomNavController.visibility=View.GONE
        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl,fragment)
            commit();

        }


    }

    override fun onSignedInButtonClicked(email: String, password: String) {
        viewModel.signInUser(email,password)
    }

    override fun onForgotPasswordButtonClicked() {
        setCurrentFragment(resetPasswordFragment,false)
    }

    override fun onSignedInRegButtonClicked() {
        setCurrentFragment(registerFragment,false)
    }

    override fun onRegisterButtonClicked(email: String, password: String, username: String) {
        viewModel.registerUser(email,password,username)
    }

    override fun onSendEmailButtonClicked(email: String) {
        viewModel.resetPassword(email)
    }

    override fun onStartActivityButtonClicked() {
        setCurrentFragment(activitySetUpFragment,false)
    }

    override fun onSortSpinnerItemClicked(sort: Int, filter: Int) {
       var sortedActivities=viewModel.sortAndFilter(filter,sort)
        activitiesFragment.refreshData(sortedActivities)
    }

    override fun onActivitiesSpinnerItemClicked(filter: Int, sort: Int) {
        var filteredActivities=viewModel.sortAndFilter(filter,sort)
        activitiesFragment.refreshData(filteredActivities)
    }


}