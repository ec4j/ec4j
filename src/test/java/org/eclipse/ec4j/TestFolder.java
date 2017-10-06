package org.eclipse.ec4j;

import java.util.HashMap;
import java.util.Map;

public class TestFolder extends TestResource {

	private final Map<String, TestResource> children;

	public TestFolder(String name) {
		this(name, null);
	}

	public TestFolder(String name, TestFolder parent) {
		super(name, parent);
		children = new HashMap<>();
	}

	public TestFile addFile(String name, String content) {
		TestFile file = new TestFile(name, content, this);
		children.put(file.getName(), file);
		return file;
	}

	public TestFile addFile(String name) {
		return addFile(name, null);
	}

	public TestResource getResource(String child) {
		return children.get(child);
	}

}
