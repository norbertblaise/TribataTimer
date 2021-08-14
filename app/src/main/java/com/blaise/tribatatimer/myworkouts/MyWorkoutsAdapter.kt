package com.blaise.tribatatimer.myworkouts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blaise.tribatatimer.R
import com.blaise.tribatatimer.session.Workout
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import org.w3c.dom.Text

class MyWorkoutsAdapter(options: FirestoreRecyclerOptions<Workout>) :
    FirestoreRecyclerAdapter<Workout, MyWorkoutsAdapter.MyWorkoutsHolder>(options) {

    class MyWorkoutsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var workoutNameTextView = itemView.findViewById<TextView>(R.id.rv_workout_name_textView)
        var numberSets = itemView.findViewById<TextView>(R.id.rv_numSets_textView)
        var workoukDurationTextView = itemView.findViewById<TextView>(R.id.rv_workout_duration_textView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyWorkoutsHolder {
        return MyWorkoutsHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.workout_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyWorkoutsHolder, position: Int, model: Workout) {

//        val sessionDuration = getSessionDuration(model)
//        holder.workoutNameTextView.text = model.workoutName
//        holder.numberSets.text = model.numSets
//        holder.workoukDurationTextView.text = sessionDuration.toString()

    }

//    public fun getSessionDuration(workout: Workout): Int{
//        var totalDuration = 0
//       val numberSets = workout.numSets
//        val workDuration = workout.workDuration?.toInt()
//        val restDuration = workout.restDuration?.toInt()
//        val numExercises = workout.numExercises?.toInt()
//        val restBtwnSets = workout.restBtwnSets?.toInt()
//
//        for(i in numberSets){
//            totalDuration += ((workDuration!! + restDuration!!) * (numExercises - 1)) + restBtwnSets
//        }
//
//        return totalDuration
//    }
}