package org.pomaid;

public class DependencyAlreadyExistsException extends Exception {

	protected PomDependency dependency;
	
	public DependencyAlreadyExistsException(PomDependency dependency) {
		this.dependency = dependency;
	}

	public PomDependency getDependency() {
		return dependency;
	}
	
}
