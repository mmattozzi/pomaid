package org.pomaid;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class TestPomUpdates extends TestCase {

	public void testPomUpdate() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		Pom pom = new Pom(new File(ClassLoader.getSystemResource("pom.xml").getFile()));
		PomDependency pd = new PomDependency("commons-io", "commons-io", "1.4");
		pom.addDependency(pd);
		pom.print();
	}
	
}
