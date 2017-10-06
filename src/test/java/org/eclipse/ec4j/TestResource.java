package org.eclipse.ec4j;

public class TestResource {

	private final String name;
	private final TestResource parent;

	public TestResource(String name, TestFolder parent) {
		this.name = name;
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		StringBuilder path = new StringBuilder(name);
		TestResource p = parent;
		while (p != null) {
			path.insert(0, p.getName() + "/");
			p = p.getParent();
		}
		return path.toString();
	}

	public boolean exists() {
		return true;
	}

	public TestResource getParent() {
		return parent;
	}
}
