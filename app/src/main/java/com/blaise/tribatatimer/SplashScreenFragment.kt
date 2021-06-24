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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashScreenFragment : Fragment() {

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
            findNavController().navigate(R.id.action_splashScreenFragment_to_myWorkoutsFragment)
        }
    }

    companion object {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}