package com.ruideraj.secretelephant.match

import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class MatchmakerImplTest {

    private var matchmaker = MatchmakerImpl()

    @Test
    fun matchmaker_match_noOrderCheck_allIndicesPresent() = runBlocking {
        // Check even numbered size.
        var size = 10
        var matches = matchmaker.match(size, false)
        checkMatches(matches, false)

        // Check odd numbered size.
        size = 5
        matches = matchmaker.match(size, false)
        checkMatches(matches, false)
    }

    @Test
    fun matchmaker_match_orderCheck_allIndicesPresentAndValuesDifferentFromIndices() = runBlocking {
        // Check even numbered size.
        var size = 10
        var matches = matchmaker.match(size, true)
        checkMatches(matches, true)

        // Check odd numbered size.
        size = 5
        matches = matchmaker.match(size, true)
        checkMatches(matches, true)
    }

    private fun checkMatches(matches: IntArray, checkOrder: Boolean) {
        val nums = mutableSetOf<Int>()
        matches.forEach { nums.add(it) }

        for (i in matches.indices) {
            assert(nums.contains(i))

            if (checkOrder) assertThat(matches[i], not(equalTo(i)))
        }
    }
}