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
package com.cervantesvirtual.storage;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Collections;

/**
 * Signature is a list of tags (sorted in reverse order
 */
class Signature extends ArrayList<Tag> implements Comparable<Signature> {

    public Signature() {
        super();
    }

    public Signature(List<Tag> list) {
        super(list);
    }

    public Signature(String word) {
        TreeMap<Character, Integer> counter
                = new TreeMap<>(Collections.reverseOrder());

        for (Character c : word.toCharArray()) {
            if (counter.containsKey(c)) {
                int times = counter.get(c);
                counter.put(c, times + 1);
            } else {
                counter.put(c, 1);
            }
        }

        for (Character c : counter.keySet()) {
            Tag tag = new Tag(c, counter.get(c));
            add(tag);
        }
    }

    /**
     * @return the length of the word generating the signature
     */
    public int wordLength() {
        int len = 0;
        for (Tag tag : this) {
            len += tag.n;
        }
        return len;
    }

    /**
     * @param pos a position
     * @return the suffix starting at this position
     */
    public Signature suffix(int pos) {
        return new Signature(subList(pos, size()));
    }

    /**
     * @param pos a position
     * @return the prefix ending at this position (exclusive)
     */
    public Signature prefix(int pos) {
        return new Signature(subList(0, pos));
    }

    /**
     * @param other another signature
     * @return True if this signature is a prefix of the other
     */
    public boolean isPrefix(Signature other) {
        int len = size();
        if (len > other.size()) {
            return false;
        } else {
            return this.equals(other.prefix(len));
        }
    }

    /**
     * Compute the longest common prefix (lpc)
     *
     * @param other another signature
     * @return the L.C.P. between this signature and the other signature
     */
    public Signature lcp(Signature other) {
        Signature res = new Signature();
        int pos = 0;

        while (pos < this.size() && pos < other.size()
                && this.get(pos).equals(other.get(pos))) {
            res.add(get(pos++));
        }
        return res;
    }

    // Comparability functions
    public boolean equals(Signature sig) {
        return super.equals(sig);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;

        } else if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        } else {
            Signature other = (Signature) obj;
            return this.equals(other);
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public int compareTo(Signature sig) {
        if (equals(sig)) {
            return 0;
        } else {
            for (int n = 0; n < size(); ++n) {
                if (sig.size() < n) {
                    return -1;
                } else {
                    Tag tag = sig.get(n);
                    int r = get(n).compareTo(tag);
                    if (r < 0) {
                        return -1;
                    } else if (r > 0) {
                        return 1;
                    }
                }
            }
            return 1;
        }
    }
}
