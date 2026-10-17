package edu.kit.kastel.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Utility class for performing weighted random selection.
 * @author uqibg
 */
public final class WeightedRandom {

    private WeightedRandom() {
    }

    /**
     * Represents a closed interval [start, end] for the weighted random selection.
     */
    public static final class Interval {
        private final int start;
        private final int end;

        /**
         * Constructor for Interval.
         * @param start the starting value of the interval
         * @param end the ending value of the interval
         */
        public Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        /**
         * Gets the ending value of the interval.
         * @return the ending value
         */
        public int end() {
            return end;
        }

        /**
         * Checks if the given value is contained within the interval [start, end].
         * @param value the value to check
         * @return true if the value is within the interval, false otherwise
         */
        public boolean contains(int value) {
            return value >= start && value <= end;
        }

        /**
         * Checks if the interval is empty (i.e., start > end).
         * @return true if the interval is empty, false otherwise
         */
        public boolean isEmpty() {
            return start > end;
        }
    }

    /**
     * Builds a list of intervals based on the given weights. Each weight corresponds to an interval of integers.
     * @param weights the list of weights, where each weight corresponds to the size of the interval for that index
     * @return a list of intervals corresponding to the weights, where each interval is defined as [currentStart, currentStart + weight - 1]
     */
    public static List<Interval> buildIntervals(List<Integer> weights) {
        Objects.requireNonNull(weights, "weights must not be null");

        List<Interval> intervals = new ArrayList<>(weights.size());
        int currentStart = 1;

        for (Integer w : weights) {
            int ww = (w == null) ? 0 : w;
            if (ww < 0) {
                ww = 0;
            }

            int currentEnd = currentStart + ww - 1;
            intervals.add(new Interval(currentStart, currentEnd));

            currentStart = currentEnd + 1;
        }

        return intervals;
    }

    /**
     * Picks an index from the list of weights based on a weighted random selection.
     * @param weights the list of weights, where each weight corresponds to the probability of picking that index
     * @param rng the random number generator to use for picking an index
     * @return the index picked based on the weights, or -1 if the weights list is empty or all weights are non-positive
     */
    public static int pickIndex(List<Integer> weights, Random rng) {
        Objects.requireNonNull(weights, "weights must not be null");
        Objects.requireNonNull(rng, "rng must not be null");

        if (weights.isEmpty()) {
            return -1;
        }

        List<Interval> intervals = buildIntervals(weights);
        return pickIndexFromIntervals(intervals, rng);
    }

    /**
     * Picks an index from the list of intervals based on a weighted random selection.
     * @param intervals the list of intervals corresponding to the weights.
     * @param rng the random number generator to use for picking an index
     * @return the index corresponding to the interval that contains the randomly picked number.
     */
    public static int pickIndexFromIntervals(List<Interval> intervals, Random rng) {
        Objects.requireNonNull(intervals, "intervals must not be null");
        Objects.requireNonNull(rng, "rng must not be null");

        if (intervals.isEmpty()) {
            return -1;
        }

        int total = 0;
        for (Interval interval : intervals) {
            if (!interval.isEmpty()) {
                total = interval.end();
            }
        }

        if (total <= 0) {
            return -1;
        }

        int r = rng.nextInt(1, total + 1);

        for (int i = 0; i < intervals.size(); i++) {
            Interval interval = intervals.get(i);
            if (!interval.isEmpty() && interval.contains(r)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Picks an index from the list of weights based on an inverse weighted random selection.
     * @param weights the list of weights, where each weight corresponds to the probability of picking that index.
     *                Higher weights will have lower chances of being picked.
     * @param rng the random number generator to use for picking an index
     * @return the index picked based on the inverse of the weights, or -1 if the weights list is empty or all weights are non-positive
     */
    public static int pickIndexInverse(List<Integer> weights, Random rng) {
        Objects.requireNonNull(weights, "weights must not be null");
        Objects.requireNonNull(rng, "rng must not be null");

        if (weights.isEmpty()) {
            return -1;
        }

        int max = 0;
        List<Integer> clamped = new ArrayList<>(weights.size());
        boolean anyPositive = false;

        for (Integer w : weights) {
            int ww = (w == null) ? 0 : w;
            if (ww < 0) {
                ww = 0;
            }
            clamped.add(ww);
            if (ww > max) {
                max = ww;
            }
            if (ww > 0) {
                anyPositive = true;
            }
        }

        if (!anyPositive) {
            return -1;
        }

        List<Integer> transformed = new ArrayList<>(clamped.size());
        boolean anyTransformedPositive = false;
        for (int ww : clamped) {
            int transformedWeight = max - ww;
            transformed.add(transformedWeight);
            if (transformedWeight > 0) {
                anyTransformedPositive = true;
            }
        }
        if (!anyTransformedPositive) {
            return rng.nextInt(weights.size());
        }

        return pickIndex(transformed, rng);
    }
}