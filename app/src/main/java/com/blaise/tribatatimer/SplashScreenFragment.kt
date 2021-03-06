package com.blaise.tribatatimer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.blaise.tribatatimer.databinding.FragmentSplashScreenBinding
import com.blaise.tribatatimer.util.FirebaseUtil
import com.blaise.tribatatimer.util.JavaFirebaseUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashScreenFragment : Fragment() {
    //firebase variables
    private lateinit var mAuth: FirebaseAuth
    private lateinit var loggedUser: FirebaseUser
    val gotoSplashScreenFragment = SplashScreenFragmentDirections.actionSplashScreenFragmentToLoginFragment()
    val gotoMyWorkoutsFragment = SplashScreenFragmentDirections.actionSplashScreenFragmentToMyWorkoutsFragment()

    //only valid between onCreateVIew() and OnDestroyView()
    private var _binding: FragmentSplashScreenBinding? = null

    val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        //set up animation on logo image
        val appLogo: ImageView = binding.splashScreenImage
        val appName : TextView = binding.appNametextView
        val slideDownAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_down)
        val slideUpAnimation = AnimationUtils.loadAnimation(context,R.anim.slide_up)
        val logoSpinAnimation = AnimationUtils.loadAnimation(context, R.anim.logo_spin)
        appLogo.startAnimation(slideDownAnimation)
        appName.startAnimation(slideUpAnimation)

        //add an animation listener to the slidedown animation for the the logo
        slideDownAnimation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                //start the spin animation after the slideDown is complete
                appLogo.startAnimation(logoSpinAnimation)
            }

            override fun onAnimationRepeat(animation: Animation?) {
                TODO("Not yet implemented")
            }
        })

        //detail transition by 5 second
        lifecycleScope.launch {
            delay(5000)
            //check if user is signed in, if not, thentake them to sign in page else take them to workouts page
//            val currentUser = FirebaseUtil.mAuth.currentUser
//            if( currentUser != null) {
//                findNavController().navigate(R.id.action_splashScreenFragment_to_myWorkoutsFragment)
//            }else{
//                findNavController().navigate(R.id.action_splashScreenFragment_to_loginFragment)
//            }

            // Check if user is signed in (non-null) and update UI accordingly.
            findNavController().navigate(gotoMyWorkoutsFragment)

        }
    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}