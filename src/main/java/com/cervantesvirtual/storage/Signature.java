package com.cervantesvirtual.storage;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Collections;

/**
 * Signature is a list of tags sorted in reverse order
 */
class Signature extends ArrayList<Tag> implements Comparable<Signature> {
   
    public Signature() { super(); }

    public Signature(List<Tag> list) {
	super(list);
    }

    public Signature(String word) {	
	TreeMap<Character, Integer> counter = 
	    new TreeMap<Character, Integer>(Collections.reverseOrder()); 

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
    public int wordlength() {
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
     * @param other another signature
     * @return the L.C.P. between this signature and the other signature
     */
    public Signature lcp(Signature other) {
	Signature res = new Signature();
	int pos = 0;

	while (pos < this.size() && pos < other.size() && 
                this.get(pos).equals(other.get(pos))) {	    
	    res.add(get(pos++));
	}
	return res;
    }


    // Comparabality functions
    public boolean equals (Signature sig) {
	return super.equals(sig);
    }

    @Override
    public int hashCode() {
	return super.hashCode();
    }

    @Override
    public int compareTo (Signature sig) {
	if (equals(sig)) {
	    return 0;
	} else {
	    for(int n = 0; n < size(); ++n) {
		if (sig.size() < n ) { 
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