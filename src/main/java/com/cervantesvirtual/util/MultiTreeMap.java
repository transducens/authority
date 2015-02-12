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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A multimap storing only different values for the same key.
 *
 * @author RCC
 * @version 2001.03.30
 * @param <K> The type of keys used to access the container.
 * @param <V> The type of value stored.
 */
public class MultiTreeMap<K, V> {

    int size; // The number of values stored.
    Map<K, Collection<V>> map;

    /**
     * Default constructor
     */
    public MultiTreeMap() {
        size = 0;
        map = new TreeMap<>();
    }

    /**
     * Copy constructor from map
     *
     * @param copy the source map.
     */
    public MultiTreeMap(Map<K, V> copy) {
        map = new TreeMap<>();
        if (copy != null) {
            for (K key : copy.keySet()) {
                add(key, copy.get(key));
            }
        }
    }

    /**
     * Return the aggregated size of all the collections stored.
     *
     * @return The number of values stored.
     */
    public int size() {
        return size;
    }

    /**
     * Check if the map contains a key.
     *
     * @param key a map key.
     * @return true if the multimap has entries under this key.
     */
    public boolean keyHasValues(K key) {
        Collection<V> values = map.get(key);
        return ((values != null) && (!values.isEmpty()));
    }

    /**
     * Check if the map contains a given value.
     *
     * @param value
     * @return true if the multimap has entries with this value.
     */
    public boolean containsValue(V value) {
        for (K key : map.keySet()) {
            Collection<V> values = map.get(key);
            if (values.contains(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param key
     * @return The collection of values under this key.
     */
    public Collection<V> get(K key) {
        return map.get(key);
    }

    /**
     * Assign a new collection to this key
     *
     * @param key the key
     * @param values the new collection
     * @return The collection of values previously stored under this key.
     */
    public Collection<V> put(K key, Collection<V> values) {
        Collection<V> original = get(key);
        size += values.size() - original.size();
        map.put(key, values);
        return original;
    }

    /**
     * Add a new value to a key.
     *
     * @param key the key
     * @param value its value
     * @return true if the collection changed its size;
     */
    public boolean add(K key, V value) {
        boolean b = getValues(key).add(value);
        if (b) { // a new value has been added
            ++size;
        }
        return b;
    }

    /**
     * Add a collection to the collection stored under this key.
     *
     * @param key a key
     * @param values the collection to be added.
     * @return true if this collection changed as a result of the call
     */
    public boolean addAll(K key, Collection<V> values) {
        int originalSize = map.get(key).size();
        boolean b = map.get(key).addAll(values);
        size += map.get(key).size() - originalSize;
        return b;
    }

    /**
     * Get the set of stored keys.
     *
     * @return the set of keys in this map.
     */
    public java.util.Set<K> keySet() {
        return map.keySet();
    }

    /**
     * Get all values under this key.
     *
     * @param key
     * @return The collection stored under this key.
     */
    private Collection<V> getValues(K key) {
        Collection<V> col = map.get(key);
        if (col == null) {
            col = new TreeSet<>();
            map.put(key, col);
        }
        return col;
    }

    /**
     * Remove the collection under this key.
     *
     * @param key a key
     * @return the collection previously associated to this key
     */
    public Collection<V> remove(K key) {
        Collection<V> original = map.get(key);
        size -= original.size();
        map.remove(key);
        return original;
    }

    /**
     * Remove a value under a key.
     *
     * @param key a key
     * @param value the value t be removed
     * @return true if the value was found under this key
     */
    public boolean remove(K key, V value) {
        Collection<V> values = map.get(key);
        if (values == null) {
            return false;
        } else {
            boolean b = values.remove(value);
            if (b) {
                --size;
            }
            return b;
        }
    }

    /**
     * Remove all entries in the map.
     */
    public void clear() {
        size = 0;
        map.clear();
    }

    /**
     * @return a string representation of the multimap
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("{");
        for (K key : map.keySet()) {
            if (builder.length() > 1) {
                builder.append(", ");
            }
            Collection<V> values = getValues(key);
            builder.append(key).append(": ").append(values);
        }
        builder.append("}");
        return builder.toString();
    }

    /**
     * Returns a list (sorted by ascending key) with all the values.
     *
     * @return All values in the multimap
     */
    public List<V> values() {
        List<V> result = new ArrayList<>();
        for (Collection<V> c : map.values()) {
            result.addAll(c);
        }
        return result;
    }
}
