package com.ruideraj.secretelephant.match;

import java.util.Random;

public class MatchMaker {

    /**
     * Creates an array which contains the order/matches for a gift exchange.
     * Uses the Durstenfeld version of the Fisher-Yates shuffle.
     *
     * https://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
     * @param size Number of participants
     */
    public static int[] match(int size, boolean checkOrder) {
        int[] matches = new int[size];

        // Initialize order.
        for(int i = 0; i < size; i++) {
            matches[i] = i;
        }

        Random random = new Random();
        for(int i = size - 1; i > 0; i--) {
            int randomPosition = random.nextInt(i + 1);
            int value = matches[i];
            matches[i] = matches[randomPosition];
            matches[randomPosition] = value;
        }

        if(checkOrder) checkOrder(matches);

        return matches;
    }

    /**
     * Checks the order to ensure that no person is matched with themselves,
     * i.e. each value should not match the index.
     * @param matches Array containing the order of the matches.
     */
    private static void checkOrder(int[] matches) {
        for(int i = 0, size = matches.length; i < size; i++) {
            int value = matches[i];
            if(value == i) {
                // If a person is matched with themselves, switch their match with the next person.
                int nextIndex = i == size - 1 ? 0 : i + 1;
                matches[i] = matches[nextIndex];
                matches[nextIndex] = value;

                // Skip the next index since we've swapped with it and
                // we know its value won't match with it.
                i++;
            }
        }
    }
}
