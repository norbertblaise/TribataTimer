package com.blaise.tribatatimer.myworkouts

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.textservice.TextInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.blaise.tribatatimer.R
import com.blaise.tribatatimer.session.Workout
import com.blaise.tribatatimer.util.JavaFirebaseUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class NewWorkoutBottomSheetDialog : BottomSheetDialogFragment() {
    val gotoMyWorkoutsFragment = NewWorkoutBottomSheetDialogDirections.actionNewWorkoutBottomSheetDialogToMyWorkoutsFragment()

    //    private BottomSheetListener mListener;


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.bottom_sheet_layout, container, false)
        val closeButton = v.findViewById<ImageButton>(R.id.close_button)
        val cancelButton = v.findViewById<Button>(R.id.cancel_button)
        val saveButton = v.findViewById<Button>(R.id.save_button)

        saveButton.setOnClickListener {
            //addd new workout to the node under current
            val workoutName =
                v.findViewById<TextInputEditText>(R.id.workout_name_editText).text.toString()
            val warmUpDuration =
                v.findViewById<TextInputEditText>(R.id.warm_up_editText).text.toString()
            val workDuration =
                v.findViewById<TextInputEditText>(R.id.workout_duration_editText).text.toString()
            val restDuration =
                v.findViewById<TextInputEditText>(R.id.rest_duration_editText).text.toString()
            val numExercises =
                v.findViewById<TextInputEditText>(R.id.exercises_editText).text.toString()
            val numSets = v.findViewById<TextInputEditText>(R.id.numSets_editText).text.toString()
            val restBtwnSetsDuration =
                v.findViewById<TextInputEditText>(R.id.rest_btwn_sets_editText).text.toString()

            val workoutNameLayout = v.findViewById<TextInputLayout>(R.id.workout_name_textInput)
            val warmUpLayout = v.findViewById<TextInputLayout>(R.id.warmup_textInput)
            val workDurationLayout =
                v.findViewById<TextInputLayout>(R.id.workout_duration_textInput)
            val restLayout = v.findViewById<TextInputLayout>(R.id.rest_duration_textInput)
            val numExercisesLayout = v.findViewById<TextInputLayout>(R.id.exercises_textInput)
            val numSetsLayout = v.findViewById<TextInputLayout>(R.id.numSets_textInput)
            val restBtwnSetsLayout = v.findViewById<TextInputLayout>(R.id.rest_btwn_sets_textInput)

            if (TextUtils.isEmpty(workoutName)) {
                workoutNameLayout.setError("required field")
            } else if (TextUtils.isEmpty(warmUpDuration)) {
                warmUpLayout.setError("required field")
            } else if (workDuration.isEmpty()) {
                workDurationLayout.setError("required field")

            } else if (restDuration.isEmpty()) {
                restLayout.setError("required field")
            } else if (numExercises.isEmpty()) {
                numExercisesLayout.setError("required field")
            } else if (numSets.isEmpty()) {
                numSetsLayout.setError("required field")
            } else if (restBtwnSetsDuration.isEmpty()) {
                restBtwnSetsLayout.setError("required field")
            } else {
                val workout = Workout(
                    workoutName,
                    warmUpDuration,
                    workDuration,
                    restDuration,
                    numExercises,
                    numSets,
                    restBtwnSetsDuration
                )
                //add workout to the database
                addWorkout(workout)

            }
        }
        closeButton.setOnClickListener { v.findNavController().safeNavigate(gotoMyWorkoutsFragment) }
        cancelButton.setOnClickListener { v.findNavController().safeNavigate(gotoMyWorkoutsFragment) }
        return v
    } //    public interface BottomSheetListener {

    private fun addWorkout(workout: Workout) {
        JavaFirebaseUtil.newWorkout(workout)
    }

    fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
    }
    //        void onButtonClicked(String text);
    //    }
    //
    //    @Override
    //    public void onAttach(@NonNull @NotNull Context context) {
    //        super.onAttach(context);
    //
    //        try {
    //            mListener = (BottomSheetListener) context;
    //        } catch (ClassCastException e) {
    //            throw new ClassCastException(context.toString()
    //                    + " must implement BottomSheetListener");
    //        }
    //    }
}