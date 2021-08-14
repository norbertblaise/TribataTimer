//package com.blaise.tribatatimer.myworkouts;
//
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.blaise.tribatatimer.R;
//import com.blaise.tribatatimer.session.Workout;
//import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
//
//import org.jetbrains.annotations.NotNull;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class MyWorkoutsFirestoreAdapter extends FirestoreRecyclerAdapter<Workout, MyWorkoutsFirestoreAdapter.MyWorkoutsHolder> {
//    private static final String TAG = "MyWorkoutsFirestoreAdap";
//    /**
//     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
//     * FirestoreRecyclerOptions} for configuration options.
//     *
//     * @param options
//     */
//    public MyWorkoutsFirestoreAdapter(@NonNull @NotNull FirestoreRecyclerOptions<Workout> options) {
//        super(options);
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull @NotNull MyWorkoutsHolder holder, int position, @NonNull @NotNull Workout model) {
//        Log.d(TAG, "onBindViewHolder: called");
//        int totalWorkoutDuration = getWorkoutDuration(model);
//
//        holder.workoutNameTextView.setText(model.getWorkoutName());
//        holder.numSetsTextView.setText(model.getNumSets());
//        holder.durationTextView.setText(totalWorkoutDuration);
//    }
//
//    @NonNull
//    @NotNull
//    @Override
//    public MyWorkoutsHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_list_item,
//                parent, false);
//        return new MyWorkoutsHolder(v);
//    }
//
//    class MyWorkoutsHolder extends RecyclerView.ViewHolder{
//        TextView workoutNameTextView;
//        TextView durationTextView;
//        TextView numSetsTextView;
//
//
//        public MyWorkoutsHolder(@NonNull @NotNull View itemView) {
//            super(itemView);
//
//            workoutNameTextView= itemView.findViewById(R.id.rv_workout_name_textView);
//            durationTextView = itemView.findViewById(R.id.rv_workout_duration_textView);
//            numSetsTextView = itemView.findViewById(R.id.rv_numSets_textView);
//        }
//    }
//
//    public int getWorkoutDuration(Workout workout){
//        int numberSets = Integer.parseInt(workout.getNumSets());
//        int workDuration = Integer.parseInt(workout.getWorkDuration());
//        int restDuration = Integer.parseInt(workout.getRestDuration());
//        int restBtwnSets = Integer.parseInt(workout.getRestBtwnSets());
//        int numExercises = Integer.parseInt(workout.getNumExercises());
//
//        int totalDuration = ((workDuration +restDuration ) * numExercises) ;
//
//        for (int i = 1; i <= numberSets; i++){
//             totalDuration = ((workDuration +restDuration ) * numExercises) + restBtwnSets;
//        }
//
//        return totalDuration;
//    }
//}
