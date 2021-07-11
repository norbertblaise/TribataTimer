package com.blaise.tribatatimer.auth

import androidx.lifecycle.ViewModel
import com.blaise.tribatatimer.util.FirebaseUtil
import com.blaise.tribatatimer.util.JavaFirebaseUtil
import com.firebase.ui.auth.data.model.User

class RegisterViewModel : ViewModel() {

    fun createUser(user: com.blaise.tribatatimer.User){
        //handle firebase user creation

        JavaFirebaseUtil.addNewUser(user)
    }
}