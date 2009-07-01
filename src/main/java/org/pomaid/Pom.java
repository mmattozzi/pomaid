package org.pomaid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Pom {

	protected File file;	
	protected Document pomDoc;
	
	public Pom(File file) throws ParserConfigurationException, SAXException, IOException {
		this.file = file;
	
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		pomDoc = documentBuilder.parse(this.file);
	}
	
	public void addDependency(PomDependency pomDependency) {
		NodeList nl = pomDoc.getElementsByTagName("dependencies");
		if (nl.getLength() > 0) {
			Element dependencies = (Element) nl.item(0);
			
			Element newDependency = pomDoc.createElement("dependency");
			Element groupId = pomDoc.createElement("groupId");
			groupId.setTextContent(pomDependency.groupId);
			Element artifactId = pomDoc.createElement("artifactId");
			artifactId.setTextContent(pomDependency.artifactId);
			Element version = pomDoc.createElement("version");
			version.setTextContent(pomDependency.version);
			newDependency.appendChild(groupId);
			newDependency.appendChild(artifactId);
			newDependency.appendChild(version);
			dependencies.appendChild(newDependency);
		}
	}
	
	public void write() throws TransformerException, FileNotFoundException {
		write(new FileOutputStream(file));
	}
	
	public void print() throws TransformerException {
		write(System.out);
	}
	
	private void write(OutputStream out) throws TransformerException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		tFactory.setAttribute("indent-number", 2);  
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		
		DOMSource source = new DOMSource(pomDoc);
		StreamResult result = new StreamResult(out);
		transformer.transform(source, result); 
	}
	
}
