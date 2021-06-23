package barbarabilonic.ferit.activitytracker.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import barbarabilonic.ferit.activitytracker.OnSignedInRegButtonClicked
import barbarabilonic.ferit.activitytracker.checkEmail
import barbarabilonic.ferit.activitytracker.databinding.ResetPasswordFragmentBinding

class ResetPasswordFragment: Fragment(){
    private lateinit var binding: ResetPasswordFragmentBinding
    private lateinit var onSignedInButtonClickedListener: OnSignedInRegButtonClicked

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= ResetPasswordFragmentBinding.inflate(inflater,container,false)
       binding.btnSendEmail.setOnClickListener { getEmail() }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnSignedInRegButtonClicked){
            onSignedInButtonClickedListener = context
        }
    }

    fun getEmail(){
        var email=binding.etResetEmailInput.text.toString().trim()
        if(email==null){
            binding.etResetEmailInput.error="Email can't be empty"
            binding.etResetEmailInput.requestFocus()

        }
        else if(!checkEmail(email)){
            binding.etResetEmailInput.error="Email invalid"
            binding.etResetEmailInput.requestFocus()
        }
        else{
            onSignedInButtonClickedListener.onSendEmailButtonClicked(email)
        }
    }
}