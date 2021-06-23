package barbarabilonic.ferit.activitytracker.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import barbarabilonic.ferit.activitytracker.ActivityTracker.Companion.application
import barbarabilonic.ferit.activitytracker.OnSignedInRegButtonClicked
import barbarabilonic.ferit.activitytracker.checkEmail
import barbarabilonic.ferit.activitytracker.checkPassword
import barbarabilonic.ferit.activitytracker.databinding.SignInFragmentBinding

class SignInFragment : Fragment() {
    private lateinit var binding:SignInFragmentBinding
    private lateinit var onSignedInButtonClickedListener: OnSignedInRegButtonClicked

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= SignInFragmentBinding.inflate(inflater,container,false)
        binding.btnSignIn.setOnClickListener { getSignInData() }
        binding.tvForgotPassword.setOnClickListener { onSignedInButtonClickedListener.onForgotPasswordButtonClicked() }
        binding.tvRegisterFromSignIn.setOnClickListener { onSignedInButtonClickedListener.onSignedInRegButtonClicked() }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnSignedInRegButtonClicked){
            onSignedInButtonClickedListener = context
        }
    }

    private fun getSignInData(){
        var email=binding.etEmailInput.text.toString().trim()
        var password=binding.etPasswordInput.text.toString().trim()

        if(email==null || password==null){
            Toast.makeText(application,"All fields must be filled out",Toast.LENGTH_LONG).show()

        }else if(!checkEmail(email)){
            binding.etEmailInput.error="Email invalid"
            binding.etEmailInput.requestFocus()
        }else if(!checkPassword(password)){
            binding.etPasswordInput.error="Password invalid"
            binding.etPasswordInput.requestFocus()
        }else{
            onSignedInButtonClickedListener.onSignedInButtonClicked(email,password)
        }
    }

}