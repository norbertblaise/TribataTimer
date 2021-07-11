package com.blaise.tribatatimer.auth

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.blaise.tribatatimer.R
import com.blaise.tribatatimer.User
import com.blaise.tribatatimer.databinding.OnboardingFragmentBinding
import com.blaise.tribatatimer.util.JavaFirebaseUtil

class OnboardingFragment : Fragment() {
    //only valid between oncreate and ondestroy
    private var _binding : OnboardingFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = OnboardingFragment()
    }

    private lateinit var viewModel: OnboardingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = OnboardingFragmentBinding.inflate(inflater, container, false)

        //initialize views and get values
        val username = binding.usernameEditText.text.toString().trim()
        val usernameTextInputLayout = binding.usernameTextInput
        val country = binding.countryEditText.text.toString().trim()
        val countryTextInputLayout = binding.countryTextInput
        val termsTextView = binding.tcsTextView
        val continueButton = binding.continueButton

        //set onclick listeners
        termsTextView.setOnClickListener { findNavController().navigate(R.id.action_onboardingFragment_to_termsFragment)}

        continueButton.setOnClickListener {
            //check if field are not null, then pass it to the
            if (username.isNotEmpty() && country.isNotEmpty()){
                val newUser = createUser(username, country)
                //send the created user to the firestore collection
                addNewUser(newUser)
                val userWasAdded = wasUserAdded()

                if (userWasAdded){
                    findNavController().navigate(R.id.action_onboardingFragment_to_myWorkoutsFragment)
                }

            }else{
                if (username.isEmpty()){
                    usernameTextInputLayout.error = "Username Required"
                }else if(country.isEmpty()) {
                    countryTextInputLayout.error = " Country Required"
                }
            }
        }
        return binding.root
    }

    private fun createUser(userName :String, country: String): User {
        val email = JavaFirebaseUtil.currentUser.email.toString()
        val displayName = JavaFirebaseUtil.currentUser.displayName.toString()

        //split the displayName
        val fName = displayName.split(" ").getOrNull(1).toString()
        val lName = displayName.split(" ").getOrNull(1).toString()

        val user = User(fName, lName, userName, email, country)
        user.userId = JavaFirebaseUtil.currentUser.uid
        return user
    }

    private fun addNewUser(user: User){
        viewModel.addNewUser(user)
    }

    private fun wasUserAdded(): Boolean{
       return viewModel.wasUserAdded()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OnboardingViewModel::class.java)
        // TODO: Use the ViewModel
    }

}