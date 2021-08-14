package com.blaise.tribatatimer.auth

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.blaise.tribatatimer.R
import com.blaise.tribatatimer.databinding.ResetPasswordFragmentBinding
import com.blaise.tribatatimer.util.JavaFirebaseUtil
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordFragment : Fragment() {
    private val TAG = "ResetPasswordFragment"
    private lateinit var mAuth: FirebaseAuth

    private var _binding: ResetPasswordFragmentBinding? = null

    private val binding get() = _binding!!

    private lateinit var emailTextLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var sendEmailButton: Button

    val gotoLogin = ResetPasswordFragmentDirections.actionResetPasswordFragmentToLoginFragment()

    private lateinit var email: String

    companion object {
        fun newInstance() = ResetPasswordFragment()
    }

    private lateinit var viewModel: ResetPasswordViewModel

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ResetPasswordFragmentBinding.inflate(inflater, container, false)
        mAuth = JavaFirebaseUtil.mAuth
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ResetPasswordViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailTextLayout = binding.emailTextInput
        emailEditText = binding.resetEmailEditText
        sendEmailButton = binding.sendEmailButton

        email = emailEditText.text.toString()

        sendEmailButton.setOnClickListener {
            if (TextUtils.isEmpty(email)){
                emailTextLayout.setError("Field Required")
            }else{
                sendEmail()
            }
        }
    }

    private fun sendEmail() {
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                    findNavController().safeNavigate(gotoLogin)
                }
            }
    }


    fun NavController.safeNavigate(direction: NavDirections) {
        Log.d(LoginFragment.TAG, "safeNavigate: called")
        currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}