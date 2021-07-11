package com.blaise.tribatatimer.auth

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.blaise.tribatatimer.R
import com.blaise.tribatatimer.databinding.LoginFragmentBinding
import com.blaise.tribatatimer.util.JavaFirebaseUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginFragment : Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    //navaction for login to register fragment
    val gotoRegisterFragment = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
    val gotoMyWorkoutsFragment = LoginFragmentDirections.actionLoginFragmentToMyWorkoutsFragment()

    private var _binding: LoginFragmentBinding? = null

    private val binding get() = _binding!!
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = LoginFragment()
        private const val TAG = "LoginFragment"
    }

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        mAuth = JavaFirebaseUtil.mAuth
        
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        val scrollView = binding.loginScrollView
        val loginButton = binding.loginButton
        val signUpTextView = binding.signupTextView

        //stopview from scrolling
        scrollView.setOnTouchListener { p0, p1 -> true }
        //set OnClickListeners
        loginButton.setOnClickListener {

            //get text from text fields
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            //validate fields beforecalling login
            if (TextUtils.isEmpty(email)){
                val emailLayout = binding.emailTextInput
                emailLayout.setError("Field Required")
            }else if (TextUtils.isEmpty(password)){
                val passwordLayout = binding.passwordTextInput
                passwordLayout.setError("Field Required")
            } else{
                login(email, password) }
            }


        signUpTextView.setOnClickListener { findNavController().safeNavigate(gotoRegisterFragment) }
        // TODO: Use the ViewModel

    }

    /**
     * handles user login
     */
    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    //gotoMyWorkoutsFragment
                    findNavController().navigate(gotoMyWorkoutsFragment)
                }else{
                    Toast.makeText(context, "Invalid Login", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun NavController.safeNavigate(direction: NavDirections) {
        Log.d(Companion.TAG, "safeNavigate: called")
        currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
    }

}