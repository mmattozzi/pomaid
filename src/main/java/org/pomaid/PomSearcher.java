package org.pomaid;

import java.util.Map;
import java.util.List;

public interface PomSearcher {

	public Map<String,List<PomDependency>> search(String term);
	
}
