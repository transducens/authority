package com.cervantesvirtual.util;

import com.cervantesvirtual.distances.DistanceType;
import junit.framework.TestCase;
import com.cervantesvirtual.util.StringFinder;

public class StringFinderTest extends TestCase {

	public void testStringFincer() {
		String[] words = { "hola", "ola", "halo", "hilo", "filo", "mola" };
		StringFinder finder = new StringFinder();
		for (String word : words) {
			finder.add(word);
		}
		java.util.Set<String> output;
		output = finder.find("mora", 2, DistanceType.INDEL);
		assertEquals(0, output.size());
		output = finder.find("mora", 3, DistanceType.INDEL);
		assertEquals(1, output.size());
		output = finder.find("mora", 2, DistanceType.LEVENSHTEIN);
		assertEquals(1, output.size());
		output = finder.find("mora", 3, DistanceType.LEVENSHTEIN);
		assertEquals(3, output.size());
	}
}
