package barbarabilonic.ferit.activitytracker


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestoreSettings


class UserRepository {
    private var user =User()
    private var firestoreInstance:FirebaseFirestore= FirebaseFirestore.getInstance()
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userId:String

    init {

        val settings = firestoreSettings {
            isPersistenceEnabled=true
            cacheSizeBytes= FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED
        }
        firestoreInstance= FirebaseFirestore.getInstance()
        firestoreInstance.firestoreSettings=settings
    }

    fun setUserId(userId:String){
        this.userId=userId
    }
    fun createUser(){
            firestoreInstance.collection("Users").document(userId).set(user.getUserInfo())

    }
    fun getUserDataFromDatabase(){
        firestoreInstance.collection("Users").document(userId).get().addOnSuccessListener {
            it.toObject(UserInfo::class.java)?.let { it1 -> user.setUserInfo(it1) }
        }
        firestoreInstance.collection("Users").document(userId).collection("Activities").get().addOnSuccessListener {
            for(document in it){
                user.addActivity(document.toObject(ActivityInfo::class.java))
            }
        }
    }

    fun addActivity(activityInfo:ActivityInfo)
    {
        user.addActivity(activityInfo)
        firestoreInstance.collection("Users").document(userId).collection("Activities").add(activityInfo)
    }

    fun changeWeight(weight:Int){
        user.changeWeight(weight)
        firestoreInstance.collection("Users").document(userId).update("weight",weight)
    }
    fun changeUsername(username:String){
        user.changeUsername(username)
        firestoreInstance.collection("Users").document(userId).update("username",username)
    }

    fun getUserWeight():Int{
        return user.getUserInfo().weight
    }
    fun getUsername():String{
        return user.getUserInfo().username
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
            filteredActivities.filter { it->it.activityType==filterItem }
        }
        when(sort){
            0->filteredActivities.sortBy { it.date }
            1->filteredActivities.sortBy { it.distance }
            2->filteredActivities.sortBy { it.duration }
            3->filteredActivities.sortBy { it.caloriesBurned }
            4->filteredActivities.sortBy { it.averageSpeed }
        }
        return filteredActivities

    }

    fun addNewActivity(type:ActivityType,time:Long,distance:Double){
        user.addNewActivity(type,time,distance)
    }


}