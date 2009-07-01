package org.pomaid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JarvanaPomSearcher implements PomSearcher {

	private static Log LOG = LogFactory.getLog(JarvanaPomSearcher.class);
	
	protected Pattern matchingPomPattern = Pattern.compile("search\\?search_type=inspect_pom&amp;path_to_pom=(.*?)\\.pom");
	
	public Map<String, List<PomDependency>> search(String term) {
		
		HttpClient httpClient = new HttpClient();
		try {
			String urlEncodedTerm = URLEncoder.encode(term, "utf-8");
			GetMethod getMethod = 
				new GetMethod("http://jarvana.com/jarvana/search?search_type=project&project=" + 
						urlEncodedTerm);
			
			int statusCode = httpClient.executeMethod(getMethod);
			if (statusCode != 200) {
				LOG.fatal(String.format("Received status code %s from url %s", statusCode, getMethod.getURI()));
				LOG.fatal(getMethod.getResponseBodyAsString());
				return null;
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(getMethod.getResponseBodyAsStream()));
			String line = null;
			
			Map<String, List<PomDependency>> pomsFound = new HashMap<String, List<PomDependency>>();
			
			int count = 0;
			
			while((line = reader.readLine()) != null) {
				Matcher matchingPomMatcher = matchingPomPattern.matcher(line);
				if (matchingPomMatcher.find()) {
					PomDependency pom = PomDependency.createFromPath(matchingPomMatcher.group(1));
					LOG.debug(pom);
					count++;
					addPomToMap(pomsFound, pom);
				}
			}
			
			sortPomMap(pomsFound);
			
			LOG.debug(String.format("Found %d poms.", count));
			
			return pomsFound;
		} catch (Exception e) {
			LOG.fatal(e);
			return null;
		} 
	}

	private void sortPomMap(Map<String, List<PomDependency>> pomsFound) {
		for (String id : pomsFound.keySet()) {
			Collections.sort(pomsFound.get(id));
		}
	}

	protected void addPomToMap(Map<String, List<PomDependency>> pomsFound, PomDependency pom) {
		String id = pom.groupId + "/" + pom.artifactId;
		if (pomsFound.containsKey(id)) {
			pomsFound.get(id).add(pom);
		} else {
			List<PomDependency> pomList = new ArrayList<PomDependency>();
			pomList.add(pom);
			pomsFound.put(id, pomList);
		}
	}
}
