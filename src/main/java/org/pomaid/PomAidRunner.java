package org.pomaid;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

public class PomAidRunner {
	
	private static Log LOG = LogFactory.getLog(PomAidRunner.class);
	
	protected String file = null;
	protected String search = null;
	
	public PomAidRunner(String []args) {
		OptionParser optParser = new OptionParser("f:s:");
		OptionSet optSet = optParser.parse(args);
		if (optSet.hasArgument("s")) {
			search = (String) optSet.valueOf("s");
			LOG.debug(String.format("Search = %s", search));
		}
		if (optSet.hasArgument("f")) {
			file = (String) optSet.valueOf("f");
			LOG.debug(String.format("POM = %s", file));
		} else {
			String currentDir = System.getProperty("user.dir");
			file = currentDir + "/pom.xml";
		}
	}
	
	public void run() throws IOException, ParserConfigurationException, SAXException, TransformerException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		if (search == null) {
			System.out.print("Search: ");
			System.out.flush();
			search = reader.readLine();
		}
		
		PomSearcher ps = new JarvanaPomSearcher();
		Map<String, List<PomDependency>> possibleDeps = ps.search(search);
		
		Map<Integer, String> searchChoices = new HashMap<Integer, String>();
		
		System.out.println("Select matching dependency: ");
		int i = 1;
		for (String key: possibleDeps.keySet()) {
			System.out.println(String.format("[%d] %s", i, key));
			searchChoices.put(i++, key);
		}
		System.out.println("[x] Exit");
		
		String choiceStr = reader.readLine();
		if (choiceStr.equals("x")) System.exit(0);
		
		int choice = Integer.parseInt(choiceStr);
		if (searchChoices.containsKey(choice)) {
			System.out.println("Select available version: ");
			List<PomDependency> deps = possibleDeps.get(searchChoices.get(choice));
			
			for (int j = 1; j <= deps.size(); j++) {
				System.out.println(String.format("[%d] %s", j, deps.get(j-1).version));
			}
			System.out.println("[x] Exit");
			
			choiceStr = reader.readLine();
			if (choiceStr.equals("x")) System.exit(0);
			
			int choice2 = Integer.parseInt(choiceStr);
			System.out.println("Dependency: ");
			PomDependency dependency = deps.get(choice2-1);
			System.out.println(dependency.toXml());
			System.out.print("Add to pom? [y/n] ");
			System.out.flush();
			
			choiceStr = reader.readLine();
			if (choiceStr.equals("y")) {
				Pom pom = new Pom(new File(file));
				try {
					pom.addDependency(deps.get(choice2-1));
				} catch (DependencyAlreadyExistsException e) {
					System.out.print(String.format("Dependency already exists (version  %s), replace with version %s? [y/n] ", e.getDependency().version, dependency.version));
					System.out.flush();
					choiceStr = reader.readLine();
					if (choiceStr.equals("y")) {
						pom.addOrReplaceDependency(dependency);
					} else {
						System.exit(1);
					}
				} catch (DuplicateDependencyException e) {
					System.out.println("Dependency already in pom.");
					System.exit(1);
				}
				pom.write();
			}
			
		} else {
			System.out.println("Invalid choice.");
			System.exit(1);
		}
	}
	
	public static void main(String[] args) throws Exception {
		PomAidRunner pomAid = new PomAidRunner(args);
		pomAid.run();
	}

}
