package barbarabilonic.ferit.activitytracker

fun checkEmail(email:String):Boolean{
    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]"

     return !email.matches(emailPattern.toRegex())
}

fun checkPassword(password:String):Boolean{

     return password.length >= 6
}

fun checkIfPasswordsMatch(password:String,repeatPassword:String):Boolean{
    return password==repeatPassword
}