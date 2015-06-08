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
package com.cervantesvirtual.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A class for cyclic arrays.
 *
 * @author R.C.C.
 * @version 2011.03.10
 * @param <Type> the type of objects in the CyclicArray
 */
public class CyclicArray<Type extends Comparable<Type>> implements
        Comparable<CyclicArray<Type>> {

    List<Type> source;

    /**
     * The default constructor.
     */
    public CyclicArray() {
        source = null;
    }

    /**
     * Constructor from a list.
     *
     * @param source a list of objects
     */
    public CyclicArray(List<Type> source) {
        this.source = source;
    }

    /**
     * Constructor from an array.
     *
     * @param source an array of objects
     */
    public CyclicArray(Type[] source) {
        this(Arrays.asList(source));
    }

    /**
     * Copy constructor.
     *
     * @param other another CyclicArray
     */
    public CyclicArray(CyclicArray<Type> other) {
        this.source = new ArrayList<>(other.source);
    }

    /**
     * Create a list with the source array rotated by offset positions to the
     * left.
     *
     * @param offset the initial position.
     * @return the original array rotated by offset positions to the left.
     */
    public List<Type> asList(int offset) {
        List<Type> result = new ArrayList<>(source);

        Collections.rotate(result, -offset);

        /**
         * List<Type> result = new ArrayList<>(source.size()); for (int pos = 0;
         * pos < source.size(); ++pos) { result.add(source.get((pos + offset) %
         * source.size())); }
         */
        return result;

    }

    /**
     * @return The size of the array.
     */
    public int size() {
        return source.size();
    }

    /**
     * @return A hash code which is rotation independent.
     */
    @Override
    public int hashCode() {
        int h = 1;
        for (Type item : source) {
            h ^= item.hashCode();
        }
        return h;
    }

    /**
     * Check if the array is any rotation of this.
     *
     * @param other the array to be compared with this.
     * @return true if the input array is a rotation of this array.
     */
    public boolean equals(CyclicArray<Type> other) {
        if (this.size() != other.size()) {
            return false;
        } else {
            for (int offset = 0; offset < size(); ++offset) {
                if (this.source.equals(other.asList(offset))) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * @param o an object
     * @return true if the input array is a rotation of this array.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        } else {
            return equals((CyclicArray<Type>) o);
        }
    }

    /**
     * Cyclic array comparison.
     *
     * @param other another CyclicArray
     * @return 0 if both cyclic arrays are equal, -1 if this source precedes the
     * other source, and 1 otherwise.
     *
     */
    @Override
    public int compareTo(CyclicArray<Type> other) {
        if (this.equals(other)) {
            return 0;
        } else {
            int limit = Math.min(this.size(), other.size());
            for (int pos = 0; pos < limit; ++pos) {
                if (!this.source.get(pos).equals(other.source.get(pos))) {
                    return this.source.get(pos).compareTo(other.source.get(pos));
                }
            }
            return this.size() < other.size() ? -1 : 1;
        }
    }

    /**
     * @param offset the initial position.
     * @return string representation of the source array as a List rotated by
     * offset positions.
     */
    public String toString(int offset) {
        return asList(offset).toString();
    }

    /**
     * @return string representation of the cyclic array (as a List)
     */
    @Override
    public String toString() {
        return toString(0);
    }
}
