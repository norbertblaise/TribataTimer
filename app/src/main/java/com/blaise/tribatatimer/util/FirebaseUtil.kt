package com.blaise.tribatatimer.util

import android.util.Log
import android.widget.Toast
import com.blaise.tribatatimer.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

public class FirebaseUtil {
    val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    /**
     * firebaseui authenitcation
     */

    public fun openFbReference(ref :String){
        mAuthStateListener = FirebaseAuth.AuthStateListener(){


        }
    }

    /**
     * attach and detach authStateListners
     */

    fun attachListener(){
        mAuth.addAuthStateListener(mAuthStateListener)    }
    /**
     * custom auth experience
     */

    public fun createFirebaseUser(user: User, password: String) {

        val email = user.Email

        //if no user if logged in, create new user
        if (mAuth.currentUser == null) {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = mAuth.uid.toString()
                        //add userId to the user Object
                        user.userId = userId
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(Companion.TAG, "createUserWithEmail:success")
                        //add other fields to firestore
                        mFirestore.collection(USERS).document(userId).set(user)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful){
                                    //navigate to Myworkouts fragment

                                }
                            }

                    } else {
                        // If sign in fails, display a message to the user.
//                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                        Toast.makeText(, "Authentication failed.",
//                            Toast.LENGTH_SHORT).show()
//                       / updateUI(null)
                    }
                }
        }
    }


    fun authResult(): Boolean {
        if (mAuth.currentUser != null){
            return true

        }
        else{
            return false
        }
    }



    fun attachAuthListener() {


    }

    fun detachAuthListener() {
    }

    companion object {
        private const val TAG = "FirebaseUtil"
        private const val USERS = "Users"
        val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
        lateinit var  mAuthStateListener: FirebaseAuth.AuthStateListener

    }


}