package com.blaise.tribatatimer.session


data class Workout(
    val workoutName: String? = null,
    val warmUp: String?= null,
    val workDuration: String? = null,
    val restDuration: String? = null,
    val numExercises: String? = null,
    val numSets: String? = null,
    val restBtwnSets: String? = null
) {
    var userId: String? = null
}
