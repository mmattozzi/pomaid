package org.pomaid;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class TestPomUpdates extends TestCase {

	public void testPomUpdate() throws ParserConfigurationException, SAXException, IOException, TransformerException, DependencyAlreadyExistsException, DuplicateDependencyException {
		Pom pom = new Pom(new File(ClassLoader.getSystemResource("pom.xml").getFile()));
		PomDependency pd = PomDependency.newInstance("commons-io", "commons-io", "1.4");
		pom.addDependency(pd);
		pom.print();
	}

	public void testPomUpdateReplace() throws ParserConfigurationException, SAXException, IOException, TransformerException, DependencyAlreadyExistsException, DuplicateDependencyException {
		Pom pom = new Pom(new File(ClassLoader.getSystemResource("pom.xml").getFile()));
		PomDependency pd = PomDependency.newInstance("log4j", "log4j", "1.2.15");
		pom.addOrReplaceDependency(pd);
		pom.print();
	}
	
	public void testPomSingletons() {
		PomDependency p1 = PomDependency.newInstance("commons-io", "commons-io", "1.4");
		PomDependency p2 = PomDependency.newInstance("commons-io", "commons-io", "1.4");
		assertTrue(p1.equals(p2));
	}
	
	public void testEqualsArtifact() {
		PomDependency p1 = PomDependency.newInstance("commons-io", "commons-io", "1.4");
		PomDependency p2 = PomDependency.newInstance("commons-io", "commons-io", "1.5");
		assertTrue(p1.equalsArtifact(p2));
	}
	
	public void testPomParse() throws Exception {
		Pom pom = new Pom(new File(ClassLoader.getSystemResource("pom.xml").getFile()));
		System.out.println(pom.getDependencies());
	}
}
