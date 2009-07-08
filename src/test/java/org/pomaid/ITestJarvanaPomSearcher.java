package org.pomaid;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class ITestJarvanaPomSearcher extends TestCase {

	public void testSearcher() {
		JarvanaPomSearcher ps = new JarvanaPomSearcher();
		Map<String, List<PomDependency>> results = ps.search("jopt");
		assertTrue(results.containsKey("net.sf.jopt-simple/jopt-simple"));
		PomDependency pomDep = results.get("net.sf.jopt-simple/jopt-simple").get(0);
		assertTrue(pomDep.artifactId.equals("jopt-simple"));
		assertTrue(pomDep.groupId.equals("net.sf.jopt-simple"));
	}
	
}
