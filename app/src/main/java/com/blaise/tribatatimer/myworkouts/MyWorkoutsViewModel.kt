package com.blaise.tribatatimer.myworkouts

import androidx.lifecycle.ViewModel
import com.blaise.tribatatimer.session.Workout
import com.blaise.tribatatimer.util.JavaFirebaseUtil

class MyWorkoutsViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    //passworkout to JavafirebaseUtil
    fun newWorkout( workout: Workout){
        JavaFirebaseUtil.newWorkout(workout)
    }
fun isNewOld(): Boolean{
    return JavaFirebaseUtil.isUserOld()
}


}