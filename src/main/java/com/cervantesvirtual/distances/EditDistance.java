/*
 * Copyright (C) 2014 Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.cervantesvirtual.distances;

/**
 * Provides basic implementations of some popular edit distance methods.
 *
 * @author R.C.C.
 * @version 2011.03.10
 */
public class EditDistance {

    /**
     * @return 3-wise minimum.
     */
    private static int min(int x, int y, int z) {
        return Math.min(x, Math.min(y, z));
    }

    /**
     * @param first the first string.
     * @param second the second string.
     * @return the indel distance between first and second.
     */
    public static int indelDistance(String first, String second) {
        int i, j;
        int[][] A = new int[first.length() + 1][second.length() + 1];

        // Compute first row
        A[0][0] = 0;
        for (j = 1; j <= second.length(); ++j) {
            A[0][j] = A[0][j - 1] + 1;
        }

        // Compute other rows
        for (i = 1; i <= first.length(); ++i) {
            A[i][0] = A[i - 1][0] + 1;
            for (j = 1; j <= second.length(); ++j) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    A[i][j] = A[i - 1][j - 1];
                } else {
                    A[i][j] = Math.min(A[i - 1][j] + 1, A[i][j - 1] + 1);
                }
            }
        }
        return A[first.length()][second.length()];
    }

    /**
     * @param first the first string.
     * @param second the second string.
     * @return the Levenshtein distance between first and second.
     */
    public static int levenshteinDistance(String first, String second) {
        int i, j;
        int[][] A = new int[first.length() + 1][second.length() + 1];

        // Compute first row
        A[0][0] = 0;
        for (j = 1; j <= second.length(); ++j) {
            A[0][j] = A[0][j - 1] + 1;
        }

        // Compute other rows
        for (i = 1; i <= first.length(); ++i) {
            A[i][0] = A[i - 1][0] + 1;
            for (j = 1; j <= second.length(); ++j) {
                if (first.charAt(i - 1) == second.charAt(j - 1)) {
                    A[i][j] = A[i - 1][j - 1];
                } else {
                    A[i][j] = min(A[i - 1][j] + 1, A[i][j - 1] + 1,
                            A[i - 1][j - 1] + 1);
                }
            }
        }
        return A[first.length()][second.length()];
    }

    /**
     * @param first the first string.
     * @param second the second string.
     * @param type the type of distance
     * @return the edit distance between first and second.
     */
    public static int distance(String first, String second, DistanceType type) {
        switch (type) {
            case INDEL:
                return indelDistance(first, second);
            case LEVENSHTEIN:
                return levenshteinDistance(first, second);
            default:
                return 0;
        }
    }
}
