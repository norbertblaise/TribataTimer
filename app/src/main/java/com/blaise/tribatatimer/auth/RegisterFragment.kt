package com.blaise.tribatatimer.auth

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.blaise.tribatatimer.R
import com.blaise.tribatatimer.User
import com.blaise.tribatatimer.databinding.RegisterFragmentBinding
import com.blaise.tribatatimer.util.JavaFirebaseUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import org.w3c.dom.Text

class RegisterFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var loggedUser: FirebaseUser

    //create action to myWorkouts
    val gotoMyWorkouts = RegisterFragmentDirections.actionRegisterFragmentToMyWorkoutsFragment()
    val gotoLoginFragment = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
    val gotoTermsFragment = RegisterFragmentDirections.actionRegisterFragmentToTermsFragment()

    private var _binding: RegisterFragmentBinding? = null

    private val binding get() = _binding!!

    companion object {
        fun newInstance() = RegisterFragment()
        private const val TAG = "RegisterFragment"
    }

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RegisterFragmentBinding.inflate(inflater, container, false)
        mAuth = FirebaseAuth.getInstance()
        if (mAuth.currentUser!= null){
            loggedUser = mAuth.currentUser!!
        }
//        mAuth = JavaFirebaseUtil.mAuth
//        currentUser = mAuth.currentUser!!

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
//instantiate views
        val signUpButton = binding.signupButton
        val termsTextView = binding.tcsTextView
        val loginTextView = binding.loginTextView

        val fNameLayout = binding.fNameTextInput
        val lNameLayout = binding.lNameTextInput
        val uNameLayout = binding.usernameTextInput
        val countryLayout = binding.countryTextInput
        val emailLayout = binding.emailTextInput
        val passwordLayout = binding.passwordTextInput

        val fName = binding.fNameEditText
        val lName = binding.lNameEditText
        val uName = binding.usernameEditText
        val countryEditText = binding.countryEditText
        val emailEditText = binding.emailEditText
        val passwordEditText = binding.passwordEditText



        termsTextView.setOnClickListener { findNavController().safeNavigate(gotoTermsFragment) }
        loginTextView.setOnClickListener { findNavController().safeNavigate(gotoLoginFragment) }
        signUpButton.setOnClickListener {
//get values from text fields
            val firstName = fName.text.toString()
            val lastName  = lName.text.toString()
            val username = uName.text.toString()
            val country = countryEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            //validate fields
            if(TextUtils.isEmpty(firstName)){
                fNameLayout.setError("Required Field")
                Log.d(TAG, "onActivityCreated: ${firstName}")
            }
            else if (TextUtils.isEmpty(lastName)){
                lNameLayout.setError("Required Field")
            }else if (TextUtils.isEmpty(username)){
                uNameLayout.setError("Required Field")
            }else if (TextUtils.isEmpty(country)){
                countryLayout.setError("Required Field")
            }else if (TextUtils.isEmpty(email)){
                emailLayout.setError("Required Field")
            }else if (TextUtils.isEmpty(password)){
                passwordLayout.setError("Required Field")
            }else{
                val user = User(firstName, lastName, username, email, country)
                createUser(user, email, password)
            }

        }

        // TODO: Use the ViewModel
    }

    private fun createUser(user: User, email: String, password: String) {

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //add user to Users collection
                    login(email, password)

                    if (mAuth.currentUser != null) {
                        loggedUser = mAuth.currentUser!!
                        user.userId = loggedUser.uid
                        viewModel.createUser(user)
                        // sign in

                        findNavController().navigate(gotoMyWorkouts)
                    }else{
                        Toast.makeText(context, "nouser", Toast.LENGTH_SHORT).show()
                    }


                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(Companion.TAG, "createUserWithEmail:failure", task.exception)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
    }

    /**
     * handles user login
     */
    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "login: successful")
                } else {
                    Toast.makeText(context, "Invalid Login", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        //check i
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            //navigate to myworkouts
            findNavController().safeNavigate(gotoMyWorkouts)
        }
    }



    fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
    }

}