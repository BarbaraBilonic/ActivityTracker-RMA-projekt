package barbarabilonic.ferit.activitytracker.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import barbarabilonic.ferit.activitytracker.ActivityInfo
import barbarabilonic.ferit.activitytracker.ActivitySetUpInfo
import barbarabilonic.ferit.activitytracker.UserRepository
import barbarabilonic.ferit.activitytracker.db.Authentication
import com.google.firebase.auth.FirebaseUser

class MainViewModel(private val authentication:Authentication,private val userRepository: UserRepository) : ViewModel(){
    private var authUser : MutableLiveData<FirebaseUser> = authentication.getUserLiveData()
    private val _activities = MutableLiveData(userRepository.getActivities())
    val activities: LiveData<MutableList<ActivityInfo>> = _activities
    private var _activitySetUpInfo:MutableLiveData<ActivitySetUpInfo> = MutableLiveData<ActivitySetUpInfo>()
    val activitySetUpInfo:LiveData<ActivitySetUpInfo> = _activitySetUpInfo
    fun getAuthUserLiveData() :MutableLiveData<FirebaseUser>{
        return authUser
    }



    fun signInUser(email:String,password:String){
       authentication.signInUser(email,password)
    }
    fun registerUser(email:String,password:String,username:String){
        authentication.registerUser(email,password)
    }
    fun resetPassword(email:String){
        authentication.resetPassword(email)
    }
    fun signOutUser(){
        authentication.signOutUser()
    }
    fun checkIfUserIsSignedIn():Boolean{
        return authentication.checkIfUserIsSignedIn()
    }
    fun sortAndFilter(filter:Int, sort:Int):MutableList<ActivityInfo>{
        return userRepository.sortAndFilter(filter,sort)

    }
    fun setActivitySetUpInfo(activitySetUpInfo: ActivitySetUpInfo){
        _activitySetUpInfo.postValue(activitySetUpInfo)
    }


}