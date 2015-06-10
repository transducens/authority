package com.cervantesvirtual.storage;

/**
 * A node tag in the SigTree is a pair with a character and an integer.
 */
class Tag implements Comparable<Tag> {
    char c;
    int n;
    
    public Tag(char c, int n) {
	this.c = c;
	this.n = n;
    }

    @Override
    public boolean equals(Object obj) {
	Tag that = (Tag)obj;
	return this.c == that.c && this.n == that.n;
    }

    @Override
    public int hashCode() {
	return 17 * c ^ n;
    }
    
    @Override
    public int compareTo (Tag that) {
	if (this.c != that.c) {
	    return (this.c < that.c) ? -1 : 1;
	} else if (this.n == that.n) {
	    return 0;
	} else {
	    return (this.n < that.n) ? -1 : 1;
	} 
    }

    @Override
    public String toString() {
	return "(" + c + "," + n + ")";
    }
}