package com.cervantesvirtual.storage;

import java.util.TreeMap;
import java.util.ArrayList;

/**
 * A radix tree which associates keys (signatures) to multiple values (strings)
 */
class RadixTree {
    TreeMap<Signature, RadixTree> children; 
    ArrayList<String> words;        // Better a Set

    /**
     * Default constructor
     */
    public RadixTree () {
	children = new TreeMap<Signature, RadixTree>();
	words = new ArrayList<String>();
    }

    /**
     * Copy constructor
     */
     public RadixTree (RadixTree other) {
	 this.children = new TreeMap<Signature, RadixTree>(other.children);
	 this.words = new ArrayList<String>(other.words);
    }
    
    /**
     * Find a node associated with a key 
     * @param sig a signature
     * @return the node storing that signature.
     */
    private RadixTree find (Signature key) {
	if (key.isEmpty()) {
	    return this;
	} else {
	    for (Signature label: children.keySet()) {
		if (label.isPrefix(key)) {
		    RadixTree child = children.get(label);
		    int len = label.size();
		    Signature suffix = key.suffix(len);
		    return child.find(suffix);
		}
	    }
	} 
	return null;
    }

    /**
     * Split child into two, father and son. The father keeps a prefix
     * of the signature.  The son acquires all the content, the
     * descendents and a suffix of the signature.
     * @param key the key leading to the child to be split.
     * @param prefix the part of the signature which remains in the
     * father node.
     * @return the new father node.
     */
    private RadixTree split (Signature key, Signature prefix) {
	Signature suffix =  key.suffix(prefix.size());
	RadixTree son = children.get(key);
	RadixTree father = new RadixTree();
 
	children.remove(key);
	children.put(prefix, father);
	father.children.put(suffix, son);

	return father;
    }

    /**
     * Add word under its signature  
     * @param key a signature 
     * @param value a word with the signature key.
     * @return the node where the word has been stored
     */
    private RadixTree add (Signature key, String word) {
	if (key.isEmpty()) {
	    words.add(word);
	    return this;
	} else {
	    for (Signature label: children.keySet()) {
		if (label.isPrefix(key)) {
		    int len = label.size();
		    Signature suffix = key.suffix(len);
		    RadixTree child = children.get(label);
		    return child.add(suffix, word);
		} else {		   
		    Signature prefix = key.lcp(label);
		    int len = prefix.size(); 
		    if (len > 0) {
			RadixTree father = split(label, prefix);
			return father.add(key.suffix(len), word);
		    }
		}
	    } 
	}
	// No continuation found
	RadixTree child = new RadixTree();
	child.words.add(word);
	children.put(key, child);
	return child;
    }

    /**
     * Add a word to the radix tree
     */     
    public void add (String word) {
	Signature sig = new Signature(word);
	add(sig, word);
    }

    /**
     * Look for a word in the radix tree
     */
    public boolean search (String word) {
	Signature sig = new Signature(word);
	RadixTree node = find(sig);
	
	if (node != null) {
	    for (String s : node.words) {
		if (s.equals(word)) {
		    return true;
		}
	    }
	}
	return false;
    }
    

    /**
     * Print the tree structure
     */
    private String xray () {
	StringBuilder buffer = new StringBuilder();
	String content = words.toString().replaceAll("\\[","{").replaceAll("\\]","}");
	buffer.append("[").append(content).append("_").append(children.size());
	for (Signature key : children.keySet()) {
	    String sig = key.toString().replaceAll("\\[","<").replaceAll("\\]",">");	    
	    RadixTree child = children.get(key);
	    String s = child.xray();
	    buffer.append(sig).append(s);
	}
	buffer.append("]");
	return buffer.toString();
    }


    /**
     * A lower bound for the edit distance based on signature
     * @param deltap sum of positive differences
     * @param deltam sum of negative differences (absolute value)
     * @param w substitution weight
     * @return lower bound for the edit distance
     */ 
    private double lambda (int deltap, int deltam, double w) {
	return Math.abs(deltap - deltam) + w * Math.min(deltap, deltam);
    }

    /**
     * Approximate search
     * @param key a signature suffix
     * @param deltap sum of positive differences for the signature prefix
     * @param deltam sum of negative differences (absolute value) for the signature prefix
     * @param dmax maximum allowed distance
     */
    public ArrayList<String> afind(Signature s, int deltap, int deltam, double dmax) {
	ArrayList<String> res = new ArrayList<String>();
	double w = 2.0; // TO BE REDEFINED LATER
	double low = lambda(deltap, deltam, w);
	/*
	System.out.println("Visited " + words + " looking for " + s);
	System.out.println(deltap + "+" + deltam + "+" + s.wordlength() + 
			   "=" + lambda(deltap + s.wordlength(), deltam, w));
	*/
	if (lambda(deltap + s.wordlength(), deltam, w) <= dmax) {
	    res.addAll(words);
	}
	for (Signature key : children.keySet()) {  // it can be accelerated since sorted
	    int incp = 0;
	    int incm = 0;
	    int pos = 0;
	    for (Tag tag : key) {
		while (s.get(pos).c > tag.c) {
		    incp += s.get(pos).n;
		    ++pos;
		}
	
		if (s.get(pos).c == tag.c) {
		    int delta = s.get(pos).n - tag.n;
		    if (delta > 0) {
			incp += delta;
		    } else {
			incm -= delta;
		    }
		    ++pos;
		}
		//System.out.println(tag+" incp="+incp+" incm="+incm);
	    }
	    Signature suffix = s.suffix(pos);
	    RadixTree child = children.get(key);
	    ArrayList<String> add = child.afind(suffix, deltap + incp, 
                    deltam + incm, dmax);
	    res.addAll(add);
	}
	return res;
    }

    public ArrayList<String> afind (String word, double dmax) {
	Signature sig =  new Signature(word);
	return afind(sig, 0, 0, dmax);
    }

   
    public static void main (String[] args) {
	Signature empty = new Signature();
	RadixTree tree = new RadixTree(); 
	String[] words = {"log", "logo", "goal", "mars", "rose", "rosa", "soar"};
	String[] wlook = {"rosa", "goalo"};
	for (String word : words) {
	    tree.add(word);
	}

	//System.out.println(tree.xray());

	for (String word: wlook) {
	    ArrayList<String> res = tree.afind(word, 1);   
	    System.out.println(word + "->" + res);
	}
    }
}