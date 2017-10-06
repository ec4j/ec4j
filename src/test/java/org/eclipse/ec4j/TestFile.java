package org.eclipse.ec4j;

import java.io.Reader;
import java.io.StringReader;

public class TestFile extends TestResource {

	private final String content;

	public TestFile(String name, String content, TestFolder parent) {
		super(name, parent);
		this.content = content;		
	}

	public Reader getReader() {
		return new StringReader(content);
	}

	public TestFile getResource(String child) {
		// TODO Auto-generated method stub
		return null;
	}
}
