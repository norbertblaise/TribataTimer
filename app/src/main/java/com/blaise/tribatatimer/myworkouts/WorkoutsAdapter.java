package com.blaise.tribatatimer.myworkouts;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blaise.tribatatimer.R;
import com.blaise.tribatatimer.session.CurrentSessionFragment;
import com.blaise.tribatatimer.session.Workout;
import com.blaise.tribatatimer.util.Communicator;
import com.blaise.tribatatimer.util.JavaFirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavAction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.WorkoutViewHolder> {
    private static final String TAG = "WorkoutsAdapter";
    ArrayList<Workout> workouts;
    private FirebaseFirestore mFirebaseFirestore;
    private CollectionReference mWorkoutsReference;
    private FirebaseAuth mAuth;
    private Communicator listener;

    public WorkoutsAdapter(Communicator listeningFragment) {
//        JavaFirebaseUtil.openFBReference("MyWorkouts", this);
        mAuth = JavaFirebaseUtil.mAuth;
        mFirebaseFirestore = JavaFirebaseUtil.firestoreDb;
        mWorkoutsReference = JavaFirebaseUtil.myWorkoutsRef;
        workouts = JavaFirebaseUtil.mWorkouts;
        listener = listeningFragment;


       if(mAuth.getCurrentUser() != null){
           mWorkoutsReference.addSnapshotListener((snapshot, e) -> {
               if (e != null) {
                   Log.w(TAG, "WorkoutsAdapter: ", e);
                   return;
               }

               for (DocumentChange dc : snapshot.getDocumentChanges()) {
                   switch (dc.getType()) {
                       case ADDED:
                           //refreshUI
                           Workout workout = dc.getDocument().toObject(Workout.class);
                           workouts.add(workout);
                           notifyItemInserted(workouts.size() - 1);
                           break;
                       case REMOVED:
                           //handle removal
                           break;
                       case MODIFIED:
                           //handle updates
                           break;
                   }

               }
           });
       }

    }

    @NonNull
    @NotNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.workout_list_item, parent, false);

        return new WorkoutViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);
        holder.bind(workout);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " );
        return workouts.size();
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TextView workoutNameTextView;
        TextView durationTextView;
        TextView numSetsTextView;

        public WorkoutViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            workoutNameTextView = itemView.findViewById(R.id.rv_workout_name_textView);
            durationTextView = itemView.findViewById(R.id.rv_workout_duration_textView);
            numSetsTextView = itemView.findViewById(R.id.rv_numSets_textView);
            itemView.setOnClickListener(this);
        }

        public void bind(Workout workout) {
//            int workoutDuration = getWorkoutDuration(workout);
            workoutNameTextView.setText(workout.getWorkoutName());
//            durationTextView.setText(Integer.toString(workoutDuration));
            numSetsTextView.setText(workout.getNumSets());

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            Workout workout = workouts.get(position);
            CurrentSessionFragment currentSessionFragment = new CurrentSessionFragment();
            listener.passData(workout.getWorkDuration(),
                    workout.getRestDuration(),
                    workout.getWarmUp(),
                    workout.getRestBtwnSets(),
                    workout.getNumExercises(),
                    workout.getNumSets());
//
//            Bundle bundle = new Bundle();
//            bundle.putString("workoutName", workout.getWorkoutName());
//            bundle.putString("workDuration", workout.getWorkDuration());
//            bundle.putString("restDuration", workout.getRestDuration());
//            bundle.putString("warmup", workout.getWarmUp());
//            bundle.putString("numExercises", workout.getNumExercises());
//            bundle.putString("numSets", workout.getNumSets());
//            bundle.putString("restBtwnSets", workout.getRestBtwnSets());
//
//            AppCompatActivity activity = (AppCompatActivity) v.getContext();
//            NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
//            @NonNull NavDirections navAction = MyWorkoutsFragmentDirections
//                    .actionMyWorkoutsFragmentToCurrentSessionFragment(workout.getWorkDuration(),
//                            workout.getRestDuration(),
//                            workout.getWarmUp(),
//                            workout.getNumExercises(),
//                            workout.getRestBtwnSets(),
//                            workout.getNumSets());
//
//
////            navController.navigate(R.id.action_myWorkoutsFragment_to_currentSessionFragment, bundle);
//            navController.navigate(navAction);

        }
    }

    public int getWorkoutDuration(Workout workout) {
        int numberSets = Integer.parseInt(workout.getNumSets());
        int workDuration = Integer.parseInt(workout.getWorkDuration());
        int restDuration = Integer.parseInt(workout.getRestDuration());
        int restBtwnSets = Integer.parseInt(workout.getRestBtwnSets());
        int numExercises = Integer.parseInt(workout.getNumExercises());

        int totalDuration = (((workDuration * numExercises) +
                (restDuration*(numExercises-1)) +
                restBtwnSets)*numberSets);

//        for (int i = 1; i <= numberSets; i++) {
//            totalDuration = ((workDuration + restDuration) * numExercises) + restBtwnSets;
//        }
         int durationMinutes = (int)Math.ceil(totalDuration/60);

        return durationMinutes;
    }
}
