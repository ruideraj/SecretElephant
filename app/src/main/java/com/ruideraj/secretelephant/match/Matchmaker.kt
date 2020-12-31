package com.ruideraj.secretelephant.match

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

interface Matchmaker {
    /**
     * Returns an array containing the order/matches for a gift exchange.
     *
     * @param size Number of participants
     * @param disallowSelfMatch Ensure participants aren't matched with themselves,
     * i.e. a given value shouldn't match its index
     */
    suspend fun match(size: Int, disallowSelfMatch: Boolean): IntArray
}

class MatchmakerImpl @Inject constructor() : Matchmaker {

    override suspend fun match(size: Int, disallowSelfMatch: Boolean): IntArray
            = withContext(Dispatchers.Default) {
        // This implementation uses the Durstenfeld version of the Fisher-Yates shuffle.
        // https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array

        // Initialize order
        val matches = IntArray(size) { index -> index }

        for (i in size - 1 downTo 1) {
            val randomPosition = Random.Default.nextInt(i + 1)
            val value = matches[i]
            matches[i] = matches[randomPosition]
            matches[randomPosition] = value
        }

        if (disallowSelfMatch) checkOrder(matches)

        matches
    }

    private fun checkOrder(matches: IntArray) {
        var skipNext = false
        for (i in matches.indices) {
            if (skipNext) {
                skipNext = false
                continue
            }

            if (i == matches[i]) {
                val value = matches[i]
                val nextIndex = if (i == matches.size - 1) 0 else i + 1
                matches[i] = matches[nextIndex]
                matches[nextIndex] = value

                skipNext = true
            }
        }
    }
}