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

/**
 * The tag of a node in the radix tree is a pair with a character and an integer.
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
        if (this == obj) {
            return true;
         } else if (obj == null || this.getClass() != obj.getClass()) {
            return false;
    } else {
	Tag that = (Tag)obj;
	return this.c == that.c && this.n == that.n;
    }
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