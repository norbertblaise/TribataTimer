package com.blaise.tribatatimer.util

interface Communicator {
    fun passData(
        warmupDuration: String,
        workDuration: String,
        restDuration: String,
        restBetweenSets: String,
        numExercises: String,
        numSets: String
    )
}