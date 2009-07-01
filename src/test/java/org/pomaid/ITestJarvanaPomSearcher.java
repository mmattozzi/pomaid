package org.pomaid;

import junit.framework.TestCase;

public class ITestJarvanaPomSearcher extends TestCase {

	public void testSearcher() {
		JarvanaPomSearcher ps = new JarvanaPomSearcher();
		ps.search("commons-httpclient");
	}
	
}
