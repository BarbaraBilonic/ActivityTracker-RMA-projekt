package barbarabilonic.ferit.activitytracker


class User {
    private  var userInfo: UserInfo =UserInfo()
    private var activityInfos:MutableList<ActivityInfo> = mutableListOf()

    fun setUserInfo(userInfo:UserInfo){
        this.userInfo= UserInfo()
    }
    fun getUserInfo():UserInfo{
        return userInfo
    }
    fun getActivities():MutableList<ActivityInfo>{
        return activityInfos
    }
    fun addActivity(activityInfo:ActivityInfo){
        activityInfos.add(activityInfo)
    }
    fun changeWeight(weight:Int){
        userInfo.weight=weight
    }
    fun changeUsername(username:String){
        userInfo.username=username
    }
}