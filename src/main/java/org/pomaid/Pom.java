package org.pomaid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.w3c.dom.Node;
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
	
	public Set<PomDependency> getDependencies() {
		Set<PomDependency> dependencies = new HashSet<PomDependency>();
		NodeList nl = pomDoc.getElementsByTagName("dependencies");
		if (nl.getLength() == 1) {
			Element dependenciesNode = (Element) nl.item(0);
			NodeList depNodes = dependenciesNode.getElementsByTagName("dependency");
			for (int i = 0; i < depNodes.getLength(); i++) {
				dependencies.add(parsePomDependencyNode(depNodes.item(i)));
			}
		}
		
		return dependencies;
	}
	
	private PomDependency parsePomDependencyNode(Node dependency) {
		String artifactId = null;
		String groupId = null;
		String version = null;
		
		NodeList childNodes = dependency.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) childNodes.item(i);
				if (e.getTagName().equals("groupId")) {
					groupId = e.getTextContent();
				} else if (e.getTagName().equals("artifactId")) {
					artifactId = e.getTextContent();
				} else if (e.getTagName().equals("version")) {
					version = e.getTextContent();
				}
			}
		}
		
		PomDependency pomDependency = PomDependency.newInstance(groupId, artifactId, version);
		return pomDependency;
	}

	public void addDependency(PomDependency pomDependency) throws DependencyAlreadyExistsException, DuplicateDependencyException {
		
		Set<PomDependency> existingDependencies = getDependencies();
		if (existingDependencies.contains(pomDependency)) {
			throw new DuplicateDependencyException();
		}
		
		for (PomDependency dep : existingDependencies) {
			if (dep.equalsArtifact(pomDependency)) {
				throw new DependencyAlreadyExistsException();
			}
		}
		
		NodeList nl = pomDoc.getElementsByTagName("dependencies");
		if (nl.getLength() > 0) {
			Element dependencies = (Element) nl.item(0);
			
			addNewDependencyElement(pomDependency, dependencies);
		}
	}

	public void addOrReplaceDependency(PomDependency pomDependency) {
		NodeList nl = pomDoc.getElementsByTagName("dependencies");
		if (nl.getLength() > 0) {
			Element dependencies = (Element) nl.item(0);
			
			List<Node> removalList = new ArrayList<Node>();
			
			NodeList childNodes = dependencies.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				if (childNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) childNodes.item(i);
					PomDependency p = parsePomDependencyNode(e);
					if (p.equalsArtifact(pomDependency)) {
						removalList.add(e);
					}
				}
			}
			
			for (Node n : removalList) {
				dependencies.removeChild(n);
			}
			
			addNewDependencyElement(pomDependency, dependencies);
		}
	}
	
	private void addNewDependencyElement(PomDependency pomDependency,
			Element dependencies) {
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
