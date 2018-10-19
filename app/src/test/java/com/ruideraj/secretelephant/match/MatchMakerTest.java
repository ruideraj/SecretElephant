package com.ruideraj.secretelephant.match;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class MatchMakerTest {

    @Test
    public void matchNoOrderCheck() {
        // Check even numbered size.
        int size = 10;
        int[] matches = MatchMaker.match(size, false);
        checkMatches(matches, false);

        // Check odd numbered size.
        size = 5;
        matches = MatchMaker.match(size, false);
        checkMatches(matches, false);
    }

    @Test
    public void matchOrderCheck() {
        // Check even numbered size.
        int size = 10;
        int[] matches = MatchMaker.match(size, true);
        checkMatches(matches, true);

        // Check odd numbered size.
        size = 5;
        matches = MatchMaker.match(size, true);
        checkMatches(matches, true);
    }

    private void checkMatches(int[] matches, boolean checkOrder) {
        Set<Integer> nums = new HashSet<>();
        for(int i : matches) {
            nums.add(i);
        }
        for(int i = 0; i < matches.length; i++) {
            assertTrue(nums.contains(i));
            if(checkOrder) assertTrue(matches[i] != i);
        }
    }
}
