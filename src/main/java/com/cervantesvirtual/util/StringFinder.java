/**
 * Copyright (C) 2014 Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package com.cervantesvirtual.util;

import com.cervantesvirtual.distances.DistanceType;
import com.cervantesvirtual.distances.EditDistance;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Stores strings and quickly finds those similar to a given one.
 * Toy implementation
 */
public class StringFinder {

    Map<String, Signature> signatures;

    /**
     * The signature of a string is the number of occurrences for each character
     * in the string after normalization (the string as a bag of characters).
     */
    private class Signature {

        Map<Character, Integer> sig;

        /**
         * Create signature of a string.
         */
        private Signature(String string) {
            String normal = Normalizer.normalize(string);
            sig = new HashMap<>();
            for (Character c : normal.toCharArray()) {
                if (sig.containsKey(c)) {
                    sig.put(c, sig.get(c) + 1);
                } else {
                    sig.put(c, 1);
                }
            }
        }

        /**
         * @return The number of c in the string.
         */
        public int count(Character c) {
            return sig.containsKey(c) ? sig.get(c) : 0;
        }

        /**
         * @return The characters contained in the string.
         */
        public Set<Character> charset() {
            return sig.keySet();
        }
    }

    /**
     * Default constructor
     *
     */
    public StringFinder() {
        signatures = new HashMap<>();
    }

    /**
     * @return the signature of a string.
     */
    private Signature signature(String string) {
        return new Signature(string);
    }

    /**
     * Store a string with its signature.
     */
    public void add(String string) {
        if (!signatures.containsKey(string)) {
            signatures.put(string, signature(string));
        }
    }

    /**
     * Store a set of strings
     *
     * @param stringSet A set of strings
     */
    public void add(java.util.Set<String> stringSet) {
        for (String s : stringSet) {
            add(s);
        }
    }

    /**
     * Select those strings whose distance to the target is smaller than a
     * value.
     *
     * @param target the target string.
     * @param dist upper bound for the distance.
     * @return all strings stored whose indel distance to the target string is
     * smaller than dist (and, hence, their Levenshtein distance, which is never
     * greater than indel)
     */
    public Set<String> select(String target, int dist) {
        Set<String> set = new HashSet<>();
        Signature targetSignature = signature(target);
        DistanceType type = DistanceType.INDEL; // The milder one
        for (String stored : signatures.keySet()) {
            int delta = Math.abs(target.length() - stored.length());
            if (delta < dist
                    && lowerBound(targetSignature, signatures.get(stored), type) < dist) {
                set.add(stored);
            }
        }
        return set;
    }

    /**
     * Select those strings whose distance to the target is smaller than a
     * value.
     *
     * @param target the target string.
     * @param dist upper bound for the distance.
     * @param type a distance type (indel, Levenshtein).
     * @return all strings stored whose distance to the target string is smaller
     * than dist.
     */
    public Set<String> find(String target, int dist, DistanceType type) {
        Set<String> set = new HashSet<>();
        Signature targetSignature = signature(target);

        for (String stored : signatures.keySet()) {
            int delta = Math.abs(target.length() - stored.length());
            if (delta < dist
                    && lowerBound(targetSignature, signatures.get(stored), type) < dist) {
                if (type == DistanceType.INDEL
                        && EditDistance.indelDistance(target, stored) < dist) {
                    set.add(stored);
                } else if (type == DistanceType.LEVENSHTEIN
                        && EditDistance.levenshteinDistance(target, stored) < dist) {
                    set.add(stored);
                }
            }
        }
        return set;
    }

    /**
     * Compute a lower bound for the edit distance (independent of character
     * normalization and order).
     *
     * @return A lower bound for the Levenshtein distance.
     */
    private static int lowerBound(Signature first, Signature second,
            DistanceType type) {
        int deltaMax = 0;
        int positive = 0;
        int negative = 0;
        Set<Character> charset = new HashSet<>(first.charset());
        charset.addAll(second.charset());

        for (Character c : charset) {
            int delta = first.count(c) - second.count(c);
            if (delta > 0) {
                positive += delta;
            } else {
                negative -= delta;
            }
            deltaMax = Math.max(deltaMax, Math.abs(delta));
        }
        switch (type) {
            case INDEL:
                return Math.max(deltaMax, positive + negative);
            case LEVENSHTEIN:
                return Math.max(deltaMax, Math.max(positive, negative));
            default:
                return 0;
        }
    }
}
