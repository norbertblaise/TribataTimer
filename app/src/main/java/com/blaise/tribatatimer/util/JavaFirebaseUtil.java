package com.blaise.tribatatimer.util;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.blaise.tribatatimer.R;
import com.blaise.tribatatimer.User;
import com.blaise.tribatatimer.session.Workout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

public class JavaFirebaseUtil {
    private static final String TAG = "JavaFirebaseUtil";
    public static final String MY_WORKOUTS = "MyWorkouts";
    public static final String USERS = "Users";
    private static FirebaseUtil javaFirebaseUtil;
    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static FirebaseFirestore firestoreDb;
    public static CollectionReference collectionReference;
    public static DocumentReference mUsersDocumentRef;
    public static CollectionReference workoutsSubCollectionRef;
    public static CollectionReference myWorkoutsRef;
    public static ArrayList<Workout> mWorkouts;

    private static boolean[] userWasAdded = new boolean[1];

    //public static ArrayList<Scholarship> mScholarships;
    public static FirebaseUser currentUser;

    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static FirebaseAuth.AuthStateListener mEmailVerified;
    public static final int RC_SIGN_IN = 123;
    private static Activity caller;

    private JavaFirebaseUtil() {
    }


    public static void openFBReference(String ref, Activity callerActivity) {
        if (javaFirebaseUtil == null) {
            javaFirebaseUtil = new FirebaseUtil();
            firestoreDb = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            if (mAuth.getCurrentUser() != null){
                currentUser = mAuth.getCurrentUser();


            }
           // mScholarships = new ArrayList<Scholarship>();

            caller = callerActivity;
//            mAuthListener = new FirebaseAuth.AuthStateListener() {
////                @Override
////                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
////                    if (firebaseAuth.getCurrentUser() == null) {
////
////                    }
////                    Toast.makeText(callerActivity.getBaseContext(), "Welcome back!", Toast.LENGTH_LONG).show();
////
////
////                }
//            };




        }
        if(mAuth.getCurrentUser() != null){
            mWorkouts = new ArrayList<Workout>();
            workoutsSubCollectionRef = firestoreDb.collection(USERS + "/" + currentUser.getUid()+"/"+MY_WORKOUTS);
            myWorkoutsRef = firestoreDb.collection(USERS).document(currentUser.getUid()).collection(MY_WORKOUTS);

            collectionReference = firestoreDb.collection(ref);
        }

//    mCollectionReference = mFirebaseFirestore.collection("customers");
    }


    public static void signIn() {
        //Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );


        //Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logo_main)
                        .setTheme(R.style.FirebaseUITheme)
                        .build(),
                RC_SIGN_IN);



    }

//    public static void newWorkout(Workout workout){
//
//       if (mAuth.getCurrentUser() != null){
//           currentUser = mAuth.getCurrentUser();
//           workoutsSubCollectionRef = firestoreDb.collection(USERS)
//                   .document(currentUser.getUid()).collection(MY_WORKOUTS);
//
//           firestoreDb.collection("Users")
//                   .document(currentUser.getUid())
//                   .collection(MY_WORKOUTS)
//                   .add(workout)
//                   .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                       @Override
//                       public void onSuccess(DocumentReference documentReference) {
//                           Toast.makeText(caller.getBaseContext(), "Workout Added", Toast.LENGTH_LONG).show();
//                       }
//                   });
//       }
//    }

    public static void newWorkout(Workout workout){

        if (mAuth.getCurrentUser() != null){
            currentUser = mAuth.getCurrentUser();
            workoutsSubCollectionRef = firestoreDb.collection(USERS)
                    .document(currentUser.getUid()).collection(MY_WORKOUTS);

            myWorkoutsRef.add(workout)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(caller.getBaseContext(), "Workout Added", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    /**
     * Method signs in existing users
     */
    public static void signInExistingUsers(String email, String password){
        //todo move sign inmethod here
    }

    /**
     * method adds a new User record to the Users collection
     * @param user
     */
    public static void addNewUser(User user){

        if (mAuth.getCurrentUser() != null){
            currentUser = mAuth.getCurrentUser();
            firestoreDb.collection(USERS)
                    .document(currentUser.getUid())
                    .set(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            Log.d(TAG, "onComplete: called");
                            if (currentUser != null){
                                userWasAdded[0] = true;
                            }else{
                                //show some error
                            }
                        }
                    });
        }
    }

    /**
     * check if user record already exists
     *
     */
    public static boolean isUserOld(){

        final boolean[] result = new boolean[1];
        //query Users collection for document with name matching currentUser.Uid
        if(mAuth.getCurrentUser() !=null){
            currentUser = mAuth.getCurrentUser();

            mUsersDocumentRef = firestoreDb.collection(USERS).document(currentUser.getUid());
            mUsersDocumentRef.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                assert document != null;
                                if (document.exists()) {
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                    result[0] = true;
                                } else {
                                    Log.d(TAG, "No such document");
                                    result[0] = false;
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }

                    });
        }


        return  result[0];
    }

    /**
     * check if user was added successfully
     */

    public static boolean wasUserAdded(){
        return userWasAdded[0];
    }
    public static void attachListener() {
        mAuth.addAuthStateListener(mAuthListener);
    }

    public static void detachListener() {
        mAuth.removeAuthStateListener(mAuthListener);
    }

}
