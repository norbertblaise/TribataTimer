package com.blaise.tribatatimer.session

data class Workout(
    val workoutName: String,
    val warmUp: String,
    val workDuration: String,
    val restDuration: String,
    val numExercises: String,
    val numSets: String,
    val restBtwnSets: String
) {
    var workoutId: String? = null
}
