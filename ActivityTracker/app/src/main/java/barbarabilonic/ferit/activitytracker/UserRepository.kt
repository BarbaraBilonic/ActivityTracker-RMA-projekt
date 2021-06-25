package barbarabilonic.ferit.activitytracker


import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestoreSettings


class UserRepository {
    private var user =User()
    private var firestoreInstance:FirebaseFirestore= FirebaseFirestore.getInstance()
    private var activities=MutableLiveData<MutableList<ActivityInfo>>(user.getActivities())
    private var mAuth:FirebaseAuth = FirebaseAuth.getInstance()
    private var userId:String=""
    private var isSignedIn:MutableLiveData<Boolean> = MutableLiveData(false)
    private var isLoggedOut:MutableLiveData<Boolean> = MutableLiveData(false)




    init {

        val settings = firestoreSettings {
            isPersistenceEnabled=true
            cacheSizeBytes= FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED
        }
        firestoreInstance= FirebaseFirestore.getInstance()
        firestoreInstance.firestoreSettings=settings
      if(mAuth.currentUser!=null){
          userId= mAuth.currentUser!!.uid
          getUserDataFromDatabase()
          isSignedIn.postValue(true)

      }
    }

    fun getSignedOutLiveData():MutableLiveData<Boolean>{
            return isLoggedOut
    }

    fun getIsSignedInLiveData(): MutableLiveData<Boolean>{
        return isSignedIn
    }


    fun registerUser(email:String,password:String){

        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    userId= mAuth.currentUser?.uid!!
                    isSignedIn.postValue(true )
                    createUser()
                }
                else{
                    Toast.makeText(ActivityTracker.application,"Error, registration failed", Toast.LENGTH_LONG).show()
                }

            }

    }

    fun signInUser(email: String,password: String){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
            if(it.isSuccessful){
                userId= mAuth.currentUser?.uid!!
                isSignedIn.postValue(true)
                getUserDataFromDatabase()
            }
            else{
                Toast.makeText(ActivityTracker.application,"Error, sign in failed", Toast.LENGTH_LONG).show()
            }

        }
    }

    fun signOutUser(){
        mAuth.signOut()
        userId=""
        isSignedIn.postValue(false)
        isLoggedOut.postValue(true)
        user.changeWeight(75)
        user.clearActivityInfo()

    }

    fun resetPassword(email:String){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(
                    ActivityTracker.application,"Reset password link has been sent to you email",
                    Toast.LENGTH_LONG)

            }
            else{
                Toast.makeText(ActivityTracker.application,"Error, please try again", Toast.LENGTH_LONG)
            }
        }
    }

    fun checkIfUserIsSignedIn():Boolean{


        return mAuth.currentUser!=null
    }
    fun getActivitiesLiveData():MutableLiveData<MutableList<ActivityInfo>>{
        return activities
    }


    fun createUser(){

        firestoreInstance.collection("Users").document(userId).set(hashMapOf(
            "weight" to user.getWeight()
        ))




    }
    fun getUserDataFromDatabase(){
        firestoreInstance.collection("Users").document(userId).get().addOnSuccessListener {
            user.changeWeight(it.get("weight").toString().toInt())
        }
        user.clearActivityInfo()
        firestoreInstance.collection("Users").document(userId).collection("Activities").get().addOnSuccessListener {
            for(document in it){
                user.addActivity(document.toObject(ActivityInfo::class.java))

            }
            activities.postValue(user.getActivities())
        }

    }



    fun changeWeight(weight:Int){
        user.changeWeight(weight)
        firestoreInstance.collection("Users").document(userId).update("weight",weight)
    }


    fun getUserWeight():Int{
        return user.getWeight()
    }

    fun getActivities():MutableList<ActivityInfo>{
        return  user.getActivities()
    }

    fun sortAndFilter(filter:Int, sort:Int):MutableList<ActivityInfo>{
        var filteredActivities=user.getActivities()
        var filterItem : ActivityType=ActivityType.RUN
        if(filter!=0) {
            when (filter) {
                1 -> filterItem = ActivityType.RUN
                2 -> filterItem = ActivityType.CYCLE
                3 -> filterItem = ActivityType.WALK
            }
            filteredActivities=user.getActivities().filter { it.activityType==filterItem }.toMutableList()
        }
        when(sort){
            0->filteredActivities.sortByDescending { it.date }
            1->filteredActivities.sortByDescending { it.distance }
            2->filteredActivities.sortByDescending { it.duration }
            3->filteredActivities.sortByDescending { it.caloriesBurned }
            4->filteredActivities.sortByDescending { it.averageSpeed }
        }
        return filteredActivities

    }

    fun addNewActivity(type:ActivityType,time:Long,distance:Double){
        user.addNewActivity(type,time,distance/1000)
        activities.postValue(user.getActivities())
        firestoreInstance.collection("Users").document(userId).collection("Activities").add(user.getActivities().last())
    }


    fun getTotalAverageSpeed(type: ActivityType):Double{
        var filteredActivities=user.getActivities().filter { it.activityType==type }.toMutableList()
        var totalSpeed:Double=0.0
        for(activity in filteredActivities){
            totalSpeed+=activity.averageSpeed
        }
        if(filteredActivities.size==0) return 0.0
        return totalSpeed/(filteredActivities.size.toDouble())
    }

    fun getTotalCalories(type: ActivityType):Int{
        var filteredActivities=user.getActivities().filter { it.activityType==type }.toMutableList()
        var calories:Int=0
        for(activity in filteredActivities){
            calories+=activity.caloriesBurned
        }
        return calories
    }

    fun getTotalDistance(type: ActivityType):Double{
        var filteredActivities=user.getActivities().filter { it.activityType==type }.toMutableList()
        var distance:Double=0.0
        for(activity in filteredActivities){
            distance+=activity.distance
        }
        return distance
    }

    fun getTotalTime(type: ActivityType):Long{
        var filteredActivities=user.getActivities().filter { it.activityType==type }.toMutableList()
        var time:Long=0L
        for(activity in filteredActivities){
            time+=activity.duration
        }
        return time
    }






}






