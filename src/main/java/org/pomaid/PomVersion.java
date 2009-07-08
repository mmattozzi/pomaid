package org.pomaid;

/**
 * Class to compare common version numbers in some intelligible way. 
 * Knows how to make sense of . and -'s. 
 * @author mmattozzi
 *
 */
public class PomVersion implements Comparable<PomVersion> {

	IntOrString major;
	String divider = ".";
	PomVersion sub;
	
	public PomVersion(String version) {
		int index = version.indexOf('.');
		int dashIndex = version.indexOf('-');
		if ( dashIndex >= 0 && (dashIndex < index || index == -1) ) {
			index = dashIndex;
			this.divider = "-";
		}
		if (index == -1 && dashIndex == -1) {
			initPomVersion(version, null);
			this.divider = null;
		} else {
			initPomVersion(version.substring(0, index), version.substring(index+1));
		}
	}
	
	private void initPomVersion(String major, String rest) {
		if (rest != null) {
			this.major = new IntOrString(major);
			this.sub = new PomVersion(rest);
		} else {
			this.major = new IntOrString(major);
			this.sub = null;
		}
	}
	
	@Override
	public String toString() {
		if (sub != null) {
			return String.format("%s%s%s", major, divider, sub);
		} else {
			return String.format("%s", major);
		}
	}

	public int compareTo(PomVersion o) {
		if (! this.major.equals(o.major)) {
			return this.major.compareTo(o.major);
		} else {
			if (this.sub == null && o.sub != null) return -1;
			else if (this.sub != null && o.sub == null) return 1;
			else if (this.sub == null && this.sub == null) return 0;
			else return this.sub.compareTo(o.sub);
		}
	}
	
	public class IntOrString implements Comparable<IntOrString> {
		
		Integer i;
		String s;
		
		public IntOrString(String version) {
			try {
				i = new Integer(version);
			} catch (NumberFormatException e) {
				s = version;
			}
		}
		
		@Override
		public String toString() {
			return (i != null ? i.toString() : s);
		}

		public int compareTo(IntOrString o) {
			if (this.i != null && o.i != null) {
				return i.compareTo(o.i);
			} else if (this.i != null && o.i == null) {
				return -1;
			} else if (this.i == null && o.i != null) {
				return 1;
			} else if (this.i == null && o.i == null) {
				return this.s.compareTo(o.s);
			}
			return 0;
		}
		
		@Override
		public boolean equals(Object o) {
			if (o instanceof IntOrString) {
				IntOrString i2 = (IntOrString) o;
				return (this.compareTo(i2) == 0);
			} else {
				return false;
			}
		}
	}
}
