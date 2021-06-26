package barbarabilonic.ferit.activitytracker.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import barbarabilonic.ferit.activitytracker.utilities.Constants.SHOW_ACTIVITY_TRACKING_FRAGMENT
import barbarabilonic.ferit.activitytracker.interfaces.OnSignedInRegButtonClicked
import barbarabilonic.ferit.activitytracker.interfaces.OnActivityButtonsClickListener
import barbarabilonic.ferit.activitytracker.ui.viewmodels.MainViewModel
import barbarabilonic.ferit.activitytracker.R
import barbarabilonic.ferit.activitytracker.databinding.ActivityMainBinding
import barbarabilonic.ferit.activitytracker.ui.fragments.*

import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), OnSignedInRegButtonClicked,
    OnActivityButtonsClickListener {
    private val viewModel by viewModel<MainViewModel>()
    private lateinit var binding: ActivityMainBinding
    private  val activitiesFragment=ActivitiesFragment( mutableListOf())
    private  val statisticsFragment=StatisticsFragment()
    private  val signInFragment=SignInFragment()
    private  val registerFragment=RegisterFragment()
    private val resetPasswordFragment=ResetPasswordFragment()
    private val activitySetUpFragment=ActivitySetUpFragment()
    private val activityTrackingFragment=ActivityTrackingFragment()
    private  val profileFragment=ProfileFragment()





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
                    R.id.nav_profile->{
                        setCurrentFragment(profileFragment,true)
                    }
                }
                true
            }
        }
        setContentView(binding.root)

        viewModel.isSignedIn.observe(this,{

                if(intent?.action==SHOW_ACTIVITY_TRACKING_FRAGMENT)
                    goToActivityTrackingFragment(intent)

                else
                setCurrentFragment(activitiesFragment, true)


        })



        viewModel.isCanceled.observe(this,{

                setCurrentFragment(activitiesFragment, true)
                intent.action="NO"

        })

        viewModel.activitySetUpInfo.observe(this,{
            setCurrentFragment(activityTrackingFragment,false)
        })

        viewModel.isFinnished.observe(this,{

            setCurrentFragment(activitiesFragment,true)
            intent.action="NO"

    })

        if(viewModel.checkIfUserIsSignedIn()){
            if(intent?.action==SHOW_ACTIVITY_TRACKING_FRAGMENT)
                goToActivityTrackingFragment(intent)
            else
            setCurrentFragment(activitiesFragment,true)
        }else{


            setCurrentFragment(signInFragment,false)
        }
        viewModel.activities.observe(this,{
            activitiesFragment.setActivities(it)
        })

        viewModel.isLoggedOut.observe(this,{

                setCurrentFragment(signInFragment,false)
        })

        viewModel.back.observe(this,{
            setCurrentFragment(activitiesFragment,true)
        })


    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        goToActivityTrackingFragment(intent)
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
            commit()

        }



    }



    override fun onForgotPasswordButtonClicked() {
        setCurrentFragment(resetPasswordFragment,false)
    }

    override fun onSignedInRegButtonClicked() {

        setCurrentFragment(registerFragment,false)
    }



    override fun onSendEmailButtonClicked(email: String) {
        viewModel.resetPassword(email)
        setCurrentFragment(signInFragment,false)
    }

    override fun onStartActivityButtonClicked() {
        setCurrentFragment(activitySetUpFragment,false)
    }

    override fun onSortSpinnerItemClicked(sort: Int, filter: Int) {
        val sortedActivities=viewModel.sortAndFilter(filter,sort)
        activitiesFragment.setActivities(sortedActivities)
    }

    override fun onActivitiesSpinnerItemClicked(filter: Int, sort: Int) {
        val filteredActivities=viewModel.sortAndFilter(filter,sort)
        activitiesFragment.setActivities(filteredActivities)
    }

    private fun goToActivityTrackingFragment(intent: Intent?){
        if(intent?.action==SHOW_ACTIVITY_TRACKING_FRAGMENT){
            setCurrentFragment(activityTrackingFragment,false)
        }
    }

}