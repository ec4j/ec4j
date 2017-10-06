package org.eclipse.ec4j;

import java.io.IOException;
import java.io.Reader;

public class TestResourceProvider implements ResourceProvider<TestResource> {

	@Override
	public TestResource getParent(TestResource file) {
		return file.getParent();
	}

	@Override
	public TestResource getResource(TestResource parent, String child) {
		return ((TestFolder) parent).getResource(child);
	}

	@Override
	public boolean exists(TestResource configFile) {
		return configFile.exists();
	}

	@Override
	public String getPath(TestResource file) {
		return file.getPath();
	}

	@Override
	public Reader getContent(TestResource configFile) throws IOException {
		return ((TestFile) configFile).getReader();
	}

}
