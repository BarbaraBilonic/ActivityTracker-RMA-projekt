package barbarabilonic.ferit.activitytracker.ui.fragments

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import barbarabilonic.ferit.activitytracker.ActivityTracker.Companion.application
import barbarabilonic.ferit.activitytracker.databinding.ProfileFragmentBinding
import barbarabilonic.ferit.activitytracker.ui.viewmodels.MainViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ProfileFragment : Fragment() {

    private  lateinit var binding: ProfileFragmentBinding
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= ProfileFragmentBinding.inflate(inflater,container,false)
        binding.btnEditWeight.setOnClickListener{
            binding.etWeightValue.setText("")
            binding.etWeightValue.inputType=InputType.TYPE_CLASS_NUMBER
            binding.etWeightValue.isFocusable
            it.visibility=View.GONE
            binding.btnDone.visibility=View.VISIBLE
            binding.etWeightValue.isEnabled=true

        }
        binding.btnDone.setOnClickListener {
            if(binding.etWeightValue.text==null){
                Toast.makeText(application, "Weight field can not be empty",Toast.LENGTH_LONG).show()
            }else {
                viewModel.changeWeight(binding.etWeightValue.text.toString().toInt())
                binding.btnDone.visibility=View.GONE
                binding.btnEditWeight.visibility=View.VISIBLE
                binding.etWeightValue.inputType=InputType.TYPE_NULL
                binding.etWeightValue.clearFocus()
                binding.etWeightValue.isEnabled=false
            }
        }
        binding.btnSignOut.setOnClickListener {

            viewModel.signOutUser()
        }

           binding.etWeightValue.setText(viewModel.getWeight().toString(),TextView.BufferType.NORMAL)
        binding.etWeightValue.isEnabled=false
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.etWeightValue.setText(viewModel.getWeight().toString(),TextView.BufferType.NORMAL)

    }
}