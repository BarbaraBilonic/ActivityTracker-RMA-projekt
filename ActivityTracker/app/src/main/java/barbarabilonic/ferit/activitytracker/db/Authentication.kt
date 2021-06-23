package barbarabilonic.ferit.activitytracker.db

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import barbarabilonic.ferit.activitytracker.ActivityTracker.Companion.application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Authentication {
    private var mAuth:FirebaseAuth = FirebaseAuth.getInstance()
    private var user:MutableLiveData<FirebaseUser> = MutableLiveData<FirebaseUser>()


    init{
        user.postValue(mAuth.currentUser)
    }



    fun registerUser(email:String,password:String){

        mAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                    if(it.isSuccessful){
                        user.postValue(mAuth.currentUser)
                    }
                else{
                        Toast.makeText(application,"Error, registration failed",Toast.LENGTH_LONG).show()
                    }

            }

    }

    fun signInUser(email: String,password: String){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
            if(it.isSuccessful){
                user.postValue(mAuth.currentUser)
            }
            else{
                Toast.makeText(application,"Error, sign in failed",Toast.LENGTH_LONG).show()
            }

        }
    }

    fun signOutUser(){
        mAuth.signOut()
        user.postValue(mAuth.currentUser)
    }

    fun resetPassword(email:String){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener{
            if(it.isSuccessful){
                Toast.makeText(application,"Reset password link has been sent to you email",Toast.LENGTH_LONG)

            }
            else{
                Toast.makeText(application,"Error, please try again",Toast.LENGTH_LONG)
            }
        }
    }
    fun getUserLiveData():MutableLiveData<FirebaseUser>{
        return user;
    }
    fun checkIfUserIsSignedIn():Boolean{
        return mAuth.currentUser!=null
    }


}