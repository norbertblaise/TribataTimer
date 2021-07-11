package com.blaise.tribatatimer.myworkouts

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blaise.tribatatimer.R
import com.blaise.tribatatimer.databinding.MyWorkoutsFragmentBinding
import com.blaise.tribatatimer.session.Workout
import com.blaise.tribatatimer.util.JavaFirebaseUtil
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query

class MyWorkoutsFragment : Fragment() {

    //only valid between on create and on destroy
    private var _binding: MyWorkoutsFragmentBinding? = null
    private val binding get() = _binding!!

    //firebase variables
    private lateinit var mAuth: FirebaseAuth
    private lateinit var loggedUser: FirebaseUser

    private val db = JavaFirebaseUtil.firestoreDb
    private val workoutsRef = JavaFirebaseUtil.workoutsCollectionRef
    private lateinit var myWorkoutsAdapter: MyWorkoutsAdapter

    private lateinit var workoutsRecyclerView: RecyclerView

    private lateinit var gotoLoginFragment: NavDirections

    companion object {
        fun newInstance() = MyWorkoutsFragment()
        private const val TAG = "MyWorkoutsFragment"
    }

    private lateinit var viewModel: MyWorkoutsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gotoLoginFragment = MyWorkoutsFragmentDirections.actionMyWorkoutsFragmentToLoginFragment()

        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        mAuth = FirebaseAuth.getInstance()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MyWorkoutsFragmentBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(MyWorkoutsViewModel::class.java)
        JavaFirebaseUtil.openFBReference("Users", activity)

        //initialize views
        val scrollview = binding.noDataScrollView
        workoutsRecyclerView = binding.workoutRecylerView
        val fab = binding.newWorkoutFab

        setUpRecyclerView()

        //setfab onclick listener
        fab.setOnClickListener {
            //open new workout bottomsheet
            val newWorkoutBottomSheet = context?.let { it1 -> BottomSheetDialog(it1) }
            val view = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)

            //initialize all dialog buttons and inputs
            val workoutNameEditText = view.findViewById<EditText>(R.id.workout_name_editText)
            val warmupDurationEditText = view.findViewById<EditText>(R.id.warm_up_editText)
            val workoutDurationEditText = view.findViewById<EditText>(R.id.workout_duration_editText)
            val restDurationEditText = view.findViewById<EditText>(R.id.rest_duration_editText)
            val numExercisesEditText = view.findViewById<EditText>(R.id.exercises_editText)
            val numSetsEditText = view.findViewById<EditText>(R.id.numSets_editText)
            val restBtwnSetsEditText = view.findViewById<EditText>(R.id.rest_btwn_sets_editText)
            val saveButton = view.findViewById<Button>(R.id.save_button)
            val cancelButton = view.findViewById<Button>(R.id.cancel_button)
            val closeButton = view.findViewById<ImageButton>(R.id.close_button)

            val workoutNameLayout = view.findViewById<TextInputLayout>(R.id.workout_name_textInput)
            val warmUpLayout = view.findViewById<TextInputLayout>(R.id.warmup_textInput)
            val workDurationLayout =
                view.findViewById<TextInputLayout>(R.id.workout_duration_textInput)
            val restLayout = view.findViewById<TextInputLayout>(R.id.rest_duration_textInput)
            val numExercisesLayout = view.findViewById<TextInputLayout>(R.id.exercises_textInput)
            val numSetsLayout = view.findViewById<TextInputLayout>(R.id.numSets_textInput)
            val restBtwnSetsLayout = view.findViewById<TextInputLayout>(R.id.rest_btwn_sets_textInput)

            //add a new workout object and add to firestore subcollection
            saveButton.setOnClickListener {
                val workoutName = workoutNameEditText.text.toString()
                val warmupDuration = warmupDurationEditText.text.toString()
                val workoutDuration = workoutDurationEditText.text.toString()
                val restDuration = restDurationEditText.text.toString()
                val numExercises = numExercisesEditText.text.toString()
                val numSets = numSetsEditText.text.toString()
                val restBtwnSets = restBtwnSetsEditText.text.toString()

                if (TextUtils.isEmpty(workoutName)) {
                    workoutNameLayout.setError("required field")
                } else if (TextUtils.isEmpty(warmupDuration)) {
                    warmUpLayout.setError("required field")
                } else if (workoutDuration.isEmpty()) {
                    workDurationLayout.setError("required field")

                } else if (restDuration.isEmpty()) {
                    restLayout.setError("required field")
                } else if (numExercises.isEmpty()) {
                    numExercisesLayout.setError("required field")
                } else if (numSets.isEmpty()) {
                    numSetsLayout.setError("required field")
                } else if (restBtwnSets.isEmpty()) {
                    restBtwnSetsLayout.setError("required field")
                } else {
                    //create new workout
                    val workout = Workout(
                        workoutName,
                        warmupDuration,
                        workoutDuration,
                        restDuration,
                        numExercises,
                        numSets,
                        restBtwnSets
                    )

                    newWorkout(workout)
                    newWorkoutBottomSheet?.dismiss()
                }
            }

            //close dialog when cancel button is clicked
            cancelButton.setOnClickListener { newWorkoutBottomSheet?.dismiss() }

            closeButton.setOnClickListener {
                Log.d(TAG, "onCreateView: closebutton clicked")
                newWorkoutBottomSheet?.dismiss()
            }

            //prevent it from closing with tap on screen
            newWorkoutBottomSheet?.setCancelable(false)

            //setting the contentview
            newWorkoutBottomSheet?.setContentView(view)

            //display the dialog
            newWorkoutBottomSheet?.show()


        }

        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainToolbar = binding.topAppBar
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(mainToolbar)
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.myWorkoutsFragment))


        view.findViewById<Toolbar>(R.id.topAppBar)
            .setupWithNavController(navController, appBarConfiguration)
        if (!isNewOld()) {
            //open country and username dialog

        }
    }

//    private fun gotoOnboarding() {
//        findNavController().navigate(R.id.action_myWorkoutsFragment_to_onboardingFragment)
//    }

    private fun isNewOld(): Boolean {
        return viewModel.isNewOld()
    }

    private fun setUpRecyclerView() {
        if (workoutsRef != null) {
            val query: Query = workoutsRef.orderBy("WorkoutName", Query.Direction.ASCENDING)
            val options: FirestoreRecyclerOptions<Workout> =
                FirestoreRecyclerOptions.Builder<Workout>()
                    .setQuery(query, Workout::class.java)
                    .build()

            myWorkoutsAdapter = MyWorkoutsAdapter(options)

            workoutsRecyclerView.setHasFixedSize(true)
            workoutsRecyclerView.layoutManager = LinearLayoutManager(context)
            workoutsRecyclerView.adapter = myWorkoutsAdapter
        } else {
            Log.d(Companion.TAG, "setUpRecyclerView: workoutsRef is null")


        }


    }

    private fun newWorkout(workout: Workout) {
        //add a document to the subcollection
        viewModel.newWorkout(workout)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.top_app_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                mAuth.signOut()
                findNavController().safeNavigate(gotoLoginFragment)
//                Toast.makeText(context, "logout tapped", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.account -> {
                Log.d(TAG, "onOptionsItemSelected: account clicked")
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        mAuth = FirebaseAuth.getInstance()
        val loggedUser = mAuth.currentUser
        if (loggedUser == null) {
            findNavController().safeNavigate(gotoLoginFragment)
        }
        if (workoutsRef != null) {
            myWorkoutsAdapter.startListening()
        }

    }

    override fun onStop() {
        super.onStop()
        if (workoutsRef != null) {
            myWorkoutsAdapter.stopListening()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        //check if user exists in users Collections
        if (mAuth.currentUser == null) {
            findNavController().safeNavigate(gotoLoginFragment)
        } else {
            //go to loginFragment
//                if(findNavController().currentDestination?.id == R.id.action_myWorkoutsFragment_to_loginFragment) {
//                    findNavController().navigate(R.id.action_myWorkoutsFragment_to_loginFragment)
//                }else{
//                    Log.d(TAG, "onResume: action invalid")
//                }
        }


        if (!isNewOld()) {
            //open country and username dialog
//            gotoOnboarding()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyWorkoutsViewModel::class.java)
        // TODO: Use the ViewModel
    }

    fun NavController.safeNavigate(direction: NavDirections) {
        Log.d(TAG, "safeNavigate: called")
        currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
    }

}