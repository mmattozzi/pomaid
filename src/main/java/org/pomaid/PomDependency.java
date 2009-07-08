package org.pomaid;

import java.util.HashMap;

public class PomDependency implements Comparable<PomDependency> {

	protected static HashMap<String, PomDependency> pomDependencySingletons = 
		new HashMap<String, PomDependency>();
	
	protected String groupId;
	protected String artifactId;
	protected PomVersion version;
	
	public static PomDependency newInstance(String groupId, String artifactId, String version) {
		String hashString = String.format("%s;%s;%s", groupId, artifactId, version);
		if (pomDependencySingletons.containsKey(hashString)) {
			return pomDependencySingletons.get(hashString);
		} else {
			PomDependency pomDependency = new PomDependency();
			pomDependency.groupId = groupId;
			pomDependency.artifactId = artifactId;
			pomDependency.version = new PomVersion(version);
			pomDependencySingletons.put(hashString, pomDependency);
			return pomDependency;
		}
	}
	
	private PomDependency() { }
	
	public static PomDependency createFromPath(String path) throws IllegalArgumentException {
		String parts[] = path.split("/");
		
		if (parts.length < 4) {
			throw new IllegalArgumentException("Pom path is invalid, not long enough");
		}
		
		String version = parts[parts.length - 2];
		String artifactId = parts[parts.length - 3];
		StringBuffer groupId = new StringBuffer();
		
		for (int i = 0; i <= (parts.length - 4); i++ ) {
			if (! parts[i].isEmpty()) {
				groupId.append(parts[i] + ".");
			}
		}
		
		return newInstance(groupId.substring(0, groupId.length() - 1), artifactId, version);
	}
	
	@Override
	public String toString() {
		return String.format("GroupId: %s, ArtifactId: %s, Version: %s", groupId, artifactId, version);
	}

	public int compareTo(PomDependency o) {
		return this.version.compareTo(o.version);
	}
	
	public boolean equalsArtifact(PomDependency d) {
		return (this.artifactId.equals(d.artifactId) && this.groupId.equals(d.groupId));
	}
	
	public String toXml() {
		return String.format("<dependency>\n" +
							 "   <groupId>%s</groupId>\n" +
							 "   <artifactId>%s</artifactId>\n" +
							 "   <version>%s</version>\n" +
							 "</dependency>", groupId, artifactId, version);
	}
}
