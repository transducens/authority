package com.cervantesvirtual.dates;

import com.cervantesvirtual.dates.DateParser;
import com.cervantesvirtual.dates.DateType;
import com.cervantesvirtual.dates.Period;
import junit.framework.TestCase;

public class DateParserTest extends TestCase {

	public void testDateParser() {
		Period p, ref;
		p = DateParser.parse("fl. 1756 - ca. 1800");
		ref = new Period(1746, 15, 1810, 20, DateType.YEAR);
		System.out.println(p);
		assertEquals(p.equals(ref), true);
		p = DateParser.parse("175? - 15??");
		ref = new Period(175, 50, 1550, 50, DateType.YEAR);
		System.out.println(p);
		assertEquals(p.equals(ref), true);
		p = DateParser.parse("n. 1725");
		ref = new Period(1725, 0, 1785, 30, DateType.YEAR);
		System.out.println(p);
		assertEquals(p.equals(ref), true);
		p = DateParser.parse("- 85 a.C");
		ref = new Period(-145, 30, -85, 0, DateType.YEAR);
		System.out.println(p);
		assertEquals(p.equals(ref), true);
	}
}