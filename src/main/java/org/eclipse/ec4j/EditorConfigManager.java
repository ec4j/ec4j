package org.eclipse.ec4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.eclipse.ec4j.model.optiontypes.OptionTypeRegistry;

public class EditorConfigManager extends AbstractEditorConfigManager<File> {

	public static final ResourceProvider<File> FILE_RESOURCE_PROVIDER = new ResourceProvider<File>() {

		@Override
		public File getParentFile(File file) {
			return file.getParentFile();
		}

		@Override
		public File getResource(File parent, String child) {
			return new File(parent, child);
		}

		@Override
		public boolean exists(File file) {
			return file.exists();
		}

		@Override
		public String getPath(File file) {
			return file.toString().replaceAll("[\\\\]", "/");
		}

		@Override
		public Reader getReader(File configFile) throws IOException {
			return new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8);
		}
	};

	public EditorConfigManager() {
		super(FILE_RESOURCE_PROVIDER);
	}

	public EditorConfigManager(OptionTypeRegistry registry, String configFilename, String version) {
		super(registry, FILE_RESOURCE_PROVIDER, configFilename, version);
	}
}
