package org.pomaid;

import junit.framework.TestCase;

public class TestPomVersion extends TestCase {

	public void test1() {
		PomVersion v = new PomVersion("1.2.3");
		assertTrue(v.toString().equals("1.2.3"));
	}
	
	public void test2() {
		PomVersion v = new PomVersion("1.2.3");
		PomVersion v2 = new PomVersion("1.2.4");
		assertTrue(v.compareTo(v2) == -1);
	}
	
	public void test3() {
		PomVersion v = new PomVersion("1.2.3");
		PomVersion v2 = new PomVersion("1.3");
		assertTrue(v.compareTo(v2) == -1);
	}
	
	public void test4() {
		PomVersion v = new PomVersion("1.2.3");
		PomVersion v2 = new PomVersion("2.2.3");
		assertTrue(v.compareTo(v2) == -1);
	}
	
	public void test5() {
		PomVersion v = new PomVersion("1");
		PomVersion v2 = new PomVersion("1");
		assertTrue(v.compareTo(v2) == 0);
	}
	
	public void test6() {
		PomVersion v = new PomVersion("1.2.a");
		PomVersion v2 = new PomVersion("1.3.b");
		assertTrue(v.compareTo(v2) == -1);
	}
	
	public void test7() {
		PomVersion v = new PomVersion("a");
		PomVersion v2 = new PomVersion("b");
		assertTrue(v.compareTo(v2) == -1);
	}
	
	public void test8() {
		PomVersion v = new PomVersion("1.2-a");
		PomVersion v2 = new PomVersion("1.2-b");
		assertTrue(v.compareTo(v2) == -1);
	}
}
