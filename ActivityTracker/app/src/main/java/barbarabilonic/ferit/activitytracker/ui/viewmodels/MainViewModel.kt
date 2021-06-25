package barbarabilonic.ferit.activitytracker.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import barbarabilonic.ferit.activitytracker.ActivityInfo
import barbarabilonic.ferit.activitytracker.ActivityType
import barbarabilonic.ferit.activitytracker.UserRepository

class MainViewModel(private val userRepository: UserRepository) : ViewModel(){

    private var _isSignedIn = userRepository.getIsSignedInLiveData()
    val isSignedIn : LiveData<Boolean> = _isSignedIn

    private var _activities=userRepository.getActivitiesLiveData()
    val activities : LiveData<MutableList<ActivityInfo>> = _activities

    private var _activitySetUpInfo=MutableLiveData<ActivityType>()
    val activitySetUpInfo:LiveData<ActivityType> = _activitySetUpInfo

    private var _isCanceled=MutableLiveData<Boolean>(false)
    val isCanceled : LiveData<Boolean> =_isCanceled

    private var _isFinnished=MutableLiveData<Boolean>(false)
    val isFinnished: LiveData<Boolean> = _isFinnished

    private var _isLoggedOut = userRepository.getSignedOutLiveData()
    val isLoggedOut : LiveData<Boolean> = _isLoggedOut










    fun signInUser(email:String,password:String){
        userRepository.signInUser(email,password)
    }
    fun registerUser(email:String,password:String){
        userRepository.registerUser(email,password)


    }
    fun resetPassword(email:String){
        userRepository.resetPassword(email)
    }
    fun signOutUser(){
        userRepository.signOutUser()
    }
    fun checkIfUserIsSignedIn():Boolean{
        return userRepository.checkIfUserIsSignedIn()
    }
    fun sortAndFilter(filter:Int, sort:Int):MutableList<ActivityInfo>{
        return userRepository.sortAndFilter(filter,sort)

    }
    fun setActivitySetUpInfo(type:ActivityType){
        _activitySetUpInfo.postValue(type)
    }
    fun setIsCanceled(isCanceled: Boolean){
        _isCanceled.postValue(isCanceled)
    }

    fun addActivity(type:ActivityType,time:Long,distance:Double){
        userRepository.addNewActivity(type,time,distance)
        _isFinnished.postValue(true)

    }


    fun getWeight():Int{
        return userRepository.getUserWeight()
    }

    fun changeWeight(weight:Int){
        userRepository.changeWeight(weight)
    }

    fun getAverageSpeed(type: ActivityType):Double=userRepository.getTotalAverageSpeed(type)
    fun getTotalCalories(type: ActivityType):Int=userRepository.getTotalCalories(type)
    fun getTotalDistance(type: ActivityType):Double=userRepository.getTotalDistance(type)
    fun getTotalTime(type: ActivityType):Long=userRepository.getTotalTime(type)




}

