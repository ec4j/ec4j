package org.eclipse.ec4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public interface ResourceProvider<T> {

	T getParentFile(T file);

	T getResource(T dir, String configFilename);

	boolean exists(T configFile);

	String getPath(T file);

	Reader getReader(T configFile) throws IOException;

}
