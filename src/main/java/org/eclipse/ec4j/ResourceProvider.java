package org.eclipse.ec4j;

import java.io.IOException;
import java.io.Reader;

public interface ResourceProvider<T> {

	T getParent(T file);

	T getResource(T dir, String configFilename);

	boolean exists(T file);

	String getPath(T file);

	Reader getContent(T file) throws IOException;

}
