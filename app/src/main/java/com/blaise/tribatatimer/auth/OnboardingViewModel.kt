package com.blaise.tribatatimer.auth

import androidx.lifecycle.ViewModel
import com.blaise.tribatatimer.User
import com.blaise.tribatatimer.util.JavaFirebaseUtil

class OnboardingViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    fun wasUserAdded(): Boolean{
        return JavaFirebaseUtil.wasUserAdded()
    }
    fun addNewUser(user: User){
        JavaFirebaseUtil.addNewUser(user)

    }
}