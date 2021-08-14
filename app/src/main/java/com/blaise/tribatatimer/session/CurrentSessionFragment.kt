package com.blaise.tribatatimer.session

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.blaise.tribatatimer.R
import com.blaise.tribatatimer.databinding.CurrentSessionFragmentBinding
import com.blaise.tribatatimer.util.PrefUtil
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CurrentSessionFragment : Fragment() {
    private var _binding: CurrentSessionFragmentBinding? = null
    private val binding get() = _binding!!

    private val args: CurrentSessionFragmentArgs by navArgs()
    private val TAG = "CurrentSessionFragment"

    enum class TimerState {
        Stopped, Paused, Running
    }

    private var isWarmedUp: Boolean = false
    private var timerLeftInMillis: Long = 0L

    enum class TimerType {
        Work, Rest, Warmup, RestBetween
    }

//    //timerlength values
//    lateinit var workDuration: String
//    lateinit var restDuration: String
//    lateinit var warmupDuration: String
//    lateinit var numExercises: String
//    lateinit var numSets: String
//    lateinit var restBetweenSets: String

    //timerlength values
    var workDuration: String = "0"
    var restDuration: String = "0"
    var warmupDuration: String = "0"
    var numExercises: String = "0"
    var numSets: String = "0"
    var restBetweenSets: String = "0"

    private var currentExercise: Int = 1
    private var currentSet: Int = 0

    //timer variables
    private lateinit var timer: CountDownTimer
    private lateinit var warmupTimer: CountDownTimer
    private lateinit var workTimer: CountDownTimer
    private lateinit var restTimer: CountDownTimer
    private lateinit var restBetweenTimer: CountDownTimer

    private var timerLengthSeconds = 0L
    private var timerState = TimerState.Stopped
    private var timerType = TimerType.Warmup
    private var secondsRemaining = 0L

    private var warmupSecondsRemaining = 0L
    private var workSecondsRemaining = 0L
    private var restSecondsRemaining = 0L
    private var restBetweenSecondsRemaining = 0L


    //ui elements
    private lateinit var workRestLabel: TextView
    private lateinit var workRestValue: TextView
    private lateinit var setsLeft: TextView
    private lateinit var playPauseFab: FloatingActionButton
    private lateinit var timerProgressBar: ProgressBar

    private lateinit var resetFab: ExtendedFloatingActionButton
    private lateinit var resumeFab: ExtendedFloatingActionButton
    private lateinit var progressBarAnimation: ValueAnimator

    var numSetsLeft: Int = 0
    var numberOfExercises: Int = 0


    companion object {
        fun newInstance() = CurrentSessionFragment()
    }

    private lateinit var viewModel: CurrentSessionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CurrentSessionFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workDuration = args.workDuration
        restDuration = args.restDuration
        warmupDuration = args.warmUpDuration
        restBetweenSets = args.restBetweenDuration
        numSets = args.numSets
        numExercises = args.numExercises

        workRestLabel = binding.workRestLabel
        workRestValue = binding.workRestValue
        setsLeft = binding.numSetsTextView
        playPauseFab = binding.pausePlayFab
        resetFab = binding.resetFab
        resumeFab = binding.resumeFab
        timerProgressBar = binding.timerProgressBar

        progressBarAnimation = ValueAnimator.ofInt(0, timerProgressBar.max)

        currentSet = numSets.toInt()
        numSetsLeft = numSets.toInt()
        numberOfExercises = numExercises.toInt()
        setsLeft.text = numSets

        //warmupTimer
        warmupSecondsRemaining = warmupDuration.toLong()

        warmupTimer = object : CountDownTimer(warmupSecondsRemaining * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                warmupSecondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
//                progressBarAnimation.duration = warmupDuration.toLong()
//                progressBarAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener {
////fixme: fix the  progressBarAnimation
//                    timerProgressBar.progress = progressBarAnimation.animatedValue as Int
//                })
            }

            override fun onFinish() {
                timerState = TimerState.Stopped
                startWorkTimer()
            }

        }
        //workTimer
        workSecondsRemaining = workDuration.toLong()
        workTimer = object : CountDownTimer(workSecondsRemaining * 1000, 1000) {


            override fun onTick(millisUntilFinished: Long) {
                timerState = TimerState.Running
                playPauseFab.setImageResource(R.drawable.ic_pause)
                workSecondsRemaining = millisUntilFinished / 1000

//                progressBarAnimation.duration = workDuration.toLong()
//                progressBarAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener {
//                    //fixme: fix the  progressBarAnimation
//                    timerProgressBar.progress = progressBarAnimation.animatedValue as Int
//                })
                updateCountdownUI()
            }

            override fun onFinish() {
                currentExercise++
                timerState = TimerState.Stopped
                //if the current exercise is the last, start the rest restBetweenSets otherwise
                if (currentExercise == numExercises.toInt()) {
                    startRestBetweenTimer()
                } else {

                    startRestTimer()
                }
            }
        }
        //restTimer
        restSecondsRemaining = restDuration.toLong()
        restTimer = object : CountDownTimer(restSecondsRemaining * 1000, 1000) {


            override fun onTick(millisUntilFinished: Long) {
                restSecondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
//                progressBarAnimation.duration = restDuration.toLong()
//                progressBarAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener {
////fixme: fix the  progressBarAnimation
//                    timerProgressBar.progress = progressBarAnimation.animatedValue as Int
//                })
            }

            override fun onFinish() {
                //if the current exercise is the last, start the rest restBetweenSets otherwise startWorkTimer
                timerState = TimerState.Stopped
                startWorkTimer()
            }
        }

        //restBetweenTimer
        restBetweenSecondsRemaining = restBetweenSets.toLong()

        restBetweenTimer = object : CountDownTimer(restBetweenSecondsRemaining * 1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                restBetweenSecondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
//                progressBarAnimation.duration = restBetweenSets.toLong()
//                progressBarAnimation.addUpdateListener(ValueAnimator.AnimatorUpdateListener {
//
//                    //fixme: fix the  progressBarAnimation
//                    timerProgressBar.progress = progressBarAnimation.animatedValue as Int
//                })
            }

            override fun onFinish() {
                currentExercise = 1
                currentSet--
                setsLeft.text = currentSet.toString()
                if (currentSet > 0) {
                    startWorkTimer()
                } else {
                    //stop timer and reset all values
                    onTimerFinished()
                }
            }
        }

        playPauseFab.setOnClickListener {
            //if timer is stopped, start new workout otherwise resume workout
            if (timerState == TimerState.Running) {
                pauseTimer()
            } else {
                startTimer()
            }

//            if (timerState == TimerState.Stopped) {
//                //todo change playPause fab icon to pause icon
//                //iff user isnt warmed up, start the whole workout routine
//                if (!isWarmedUp) {
//                    //run warmup timer once, before running the rest of th timers
//                    startWarmupTimer()
////                    do {
////                        startWorkTimer()
////                        //startRestBetweenTimer()
////                        numSetsLeft--
////                    } while (numSetsLeft > 0)
//                } else {
//                    startWorkTimer()
//                }
////                startTimer()
//
//            }else if(timerState == TimerState.Running){
//                //pause timer and show other button_done
//                timer.cancel()
//                updateButtons()
//            }
        }
        resetFab.setOnClickListener {
            //reset all progress
            resetTimer()
        }
        resumeFab.setOnClickListener {
            //call startTimer for the correct timer
            if(timerState == TimerState.Paused ){
                resumeTimer()
            }
        }
    }

    private fun startTimer() {
        playPauseFab.setImageResource(R.drawable.ic_pause)
        if (isWarmedUp ==false) {
            startWarmupTimer()
        }
        //loop through the rest of the timers
        startWorkTimer()
    }

    private fun pauseTimer() {
        //pause the timer
        timerState = TimerState.Paused
//        timer.cancel()
        when (timerType) {
            CurrentSessionFragment.TimerType.Warmup -> {
                warmupTimer.cancel()

            }
            CurrentSessionFragment.TimerType.Work -> {
                workTimer.cancel()
            }
            CurrentSessionFragment.TimerType.Rest -> {
                restTimer.cancel()
            }
            CurrentSessionFragment.TimerType.RestBetween -> {
                restBetweenTimer.cancel()
            }
        }
        //hide the playPauseFab and show the resetFab and resumeFab
        playPauseFab.visibility = View.GONE
        resumeFab.visibility = View.VISIBLE
        resetFab.visibility = View.VISIBLE

    }

    private fun resumeTimer() {
        playPauseFab.visibility = View.VISIBLE
        resumeFab.visibility = View.GONE
        resetFab.visibility = View.GONE
//        timer.start()
        when (timerType) {
            CurrentSessionFragment.TimerType.Warmup -> {
                warmupTimer.start()
            }
            CurrentSessionFragment.TimerType.Work -> {
                workTimer.start()
            }
            CurrentSessionFragment.TimerType.Rest -> {
                restTimer.start()
            }
            CurrentSessionFragment.TimerType.RestBetween -> {
                restBetweenTimer.start()
            }
        }
        timerState = TimerState.Running
    }

    private fun resetTimer() {
        onTimerFinished()
        playPauseFab.setImageResource(R.drawable.ic_play)
        playPauseFab.visibility = View.VISIBLE
        resumeFab.visibility = View.GONE
        resetFab.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CurrentSessionViewModel::class.java)
    }

    @SuppressLint("ResourceAsColor")
    private fun startWarmupTimer() {
        Log.d(TAG, "startWarmupTimer: called")
        timerState = TimerState.Running
        isWarmedUp = true
        workRestLabel.text = "WarmUp"
        workRestLabel.setTextColor(resources.getColor(R.color.orange_200))
        timerProgressBar.progressDrawable =
            resources.getDrawable(R.drawable.warmup_progress_drawable)
        warmupTimer.start()
    }

    private fun startWorkTimer() {
        Log.d(TAG, "startWorkTimer: called")
        timerState == TimerState.Running
        timerType = TimerType.Work
        workRestLabel.text = "Work"
        workRestLabel.setTextColor(resources.getColor(R.color.orange_500))
        timerProgressBar.progressDrawable = resources.getDrawable(R.drawable.work_progress_drawable)
        workTimer.start()
    }

    private fun startRestTimer() {
        Log.d(TAG, "startRestTimer: called")
        timerType = TimerType.Rest
        timerState == TimerState.Running
        workRestLabel.text = "Rest"
        workRestLabel.setTextColor(resources.getColor(R.color.lime_500))
        timerProgressBar.progressDrawable = resources.getDrawable(R.drawable.rest_progress_drawable)

        restTimer.start()
    }

    private fun startRestBetweenTimer() {
        Log.d(TAG, "startRestBetweenTimer: called")
        timerType = TimerType.RestBetween
        timerState == TimerState.Running
        workRestLabel.text = "Relax"
        workRestLabel.setTextColor(resources.getColor(R.color.lime_700))
        timerProgressBar.progressDrawable =
            resources.getDrawable(R.drawable.rest_btwn_progress_drawable)
        restBetweenTimer.start()
    }

    private fun updateTimerDuration() {
        timerLengthSeconds = when (timerType) {
            CurrentSessionFragment.TimerType.Warmup -> {
                warmupDuration.toLong()
            }
            CurrentSessionFragment.TimerType.Work -> {
                workDuration.toLong()
            }
            CurrentSessionFragment.TimerType.Rest -> {
                restDuration.toLong()
            }
            CurrentSessionFragment.TimerType.RestBetween -> {
                restBetweenSets.toLong()
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        initTimer()
        //TOdo remove notification
    }

//    private fun initTimer() {
//        timerState = PrefUtil.getTimerState(this.requireContext())
//
//        if (timerState == TimerState.Stopped) {
//            setNewTimerLength()
//        } else {
//            setPreviousTimerLength()
//
//            secondsRemaining =
//                if (timerState == TimerState.Running || timerState == TimerState.Paused) {
//                    PrefUtil.getSecondsRemaining(this.requireContext())
//                } else {
//                    timerLengthSeconds
//                }
//
//            //todo change secondsRemaining to the backgroundtimer
//
//            //resumewhere timer left off
//            if (timerState == TimerState.Running) {
//                startTimer()
//            }
////            updateButtons()
//            updateCountdownUI()
//        }
//    }

    private fun onTimerFinished() {
        timerState = TimerState.Stopped
        timerType = TimerType.Warmup
        isWarmedUp = false
//        timer.cancel()
        warmupTimer.cancel()
        workTimer.cancel()
        restTimer.cancel()
        restBetweenTimer.cancel()

        //reset times remainning
        warmupSecondsRemaining = warmupDuration.toLong()
        workSecondsRemaining = workDuration.toLong()
        restSecondsRemaining = restDuration.toLong()
        restBetweenSecondsRemaining = restBetweenSets.toLong()

        currentSet = numSets.toInt()
        numSetsLeft = numSets.toInt()
        setsLeft.text = numSetsLeft.toString()
        numberOfExercises = numExercises.toInt()
//        setNewTimerLength()
        timerProgressBar.progress = 0
//        PrefUtil.setSecondsRemaining(timerLengthSeconds, this.requireContext())
        secondsRemaining = workDuration.toLong()
        workRestLabel.setTextColor(resources.getColor(R.color.orange_500))
        workRestLabel.text = "Work"
        workRestValue.text = "00"
        playPauseFab.setImageResource(R.drawable.ic_play)

    }

    private fun setNewTimerLength() {
        val lengthInMinutes = PrefUtil.getTimerLength(this.requireContext())
        timerLengthSeconds = (lengthInMinutes * 60L)
        timerProgressBar.max = timerLengthSeconds.toInt()
    }

    private fun setPreviousTimerLength() {
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this.requireContext())
        timerProgressBar.max = timerLengthSeconds.toInt()
    }

    private fun updateCountdownUI() {
//        workRestValue.text = "$secondsRemaining"
        workRestValue.text =  when (timerType) {
            CurrentSessionFragment.TimerType.Warmup -> {
                "$warmupSecondsRemaining"
            }
            CurrentSessionFragment.TimerType.Work -> {
                "$workSecondsRemaining"
            }
            CurrentSessionFragment.TimerType.Rest -> {
                "$restSecondsRemaining"
            }
            CurrentSessionFragment.TimerType.RestBetween -> {
                "$restBetweenSecondsRemaining"
            }
        }
        timerProgressBar.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    override fun onPause() {
        super.onPause()
        if (timerState == TimerState.Running) {
            timer.cancel()
            //TODO start BG timer
        } else if (timerState == TimerState.Paused) {
            //TOdo: show notification
        }
//        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, this.requireContext())
//        PrefUtil.setSecondsRemaining(secondsRemaining, this.requireContext())
//        PrefUtil.setTimerState(timerState, this.requireContext())
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}