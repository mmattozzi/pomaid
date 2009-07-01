package org.pomaid;

public class PomDependency implements Comparable<PomDependency> {

	protected String groupId;
	protected String artifactId;
	protected String version;
	
	public PomDependency(String groupId, String artifactId, String version) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
	}
	
	private PomDependency() { }
	
	public static PomDependency createFromPath(String path) throws IllegalArgumentException {
		String parts[] = path.split("/");
		
		if (parts.length < 4) {
			throw new IllegalArgumentException("Pom path is invalid, not long enough");
		}
		
		PomDependency pom = new PomDependency();
		pom.version = parts[parts.length - 2];
		pom.artifactId = parts[parts.length - 3];
		pom.groupId = parts[parts.length - 4];
		
		return pom;
	}
	
	@Override
	public String toString() {
		return String.format("GroupId: %s, ArtifactId: %s, Version: %s", groupId, artifactId, version);
	}

	public int compareTo(PomDependency o) {
		return this.version.compareToIgnoreCase(o.version);
	}
	
	public String toXml() {
		return String.format("<dependency>\n" +
							 "   <groupId>%s</groupId>\n" +
							 "   <artifactId>%s</artifactId>\n" +
							 "   <version>%s</version>\n" +
							 "</dependency>", groupId, artifactId, version);
	}
}
