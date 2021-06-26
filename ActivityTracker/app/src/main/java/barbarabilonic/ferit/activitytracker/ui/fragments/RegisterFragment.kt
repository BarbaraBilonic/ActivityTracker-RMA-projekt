package barbarabilonic.ferit.activitytracker.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import barbarabilonic.ferit.activitytracker.*
import barbarabilonic.ferit.activitytracker.databinding.RegisterFragmentBinding
import barbarabilonic.ferit.activitytracker.interfaces.OnSignedInRegButtonClicked
import barbarabilonic.ferit.activitytracker.ui.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class RegisterFragment : Fragment(){
    private lateinit var binding: RegisterFragmentBinding
    private lateinit var onSignedInButtonClickedListener: OnSignedInRegButtonClicked
    private val viewModel by sharedViewModel<MainViewModel> (  )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= RegisterFragmentBinding.inflate(inflater,container,false)
        binding.btnRegister.setOnClickListener { getRegisterData() }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnSignedInRegButtonClicked){
            onSignedInButtonClickedListener = context
        }
    }

    private fun getRegisterData(){
        var email=binding.etRegEmailInput.text.toString().trim()
        var password=binding.etRegPasswordInput.text.toString().trim()
        var repeatPassword=binding.etRegRepeatPasswordInput.text.toString().trim()



         if(!checkEmail(email)){
            binding.etRegEmailInput.error="Email invalid"
            binding.etRegEmailInput.requestFocus()
        }else if(!checkPassword(password)){
            binding.etRegPasswordInput.error="Password invalid"
            binding.etRegPasswordInput.requestFocus()
        }else if(!checkIfPasswordsMatch(password,repeatPassword)){
            binding.etRegRepeatPasswordInput.error="Password doesn't match"
            binding.etRegRepeatPasswordInput.requestFocus()
            Log.i("moje1","${password},${repeatPassword}")
        }else{
            viewModel.registerUser(email,password)
        }
    }

}