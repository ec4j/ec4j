/**
 * The MIT License
 * Copyright © 2017 Angelo Zerr and other contributors as
 * indicated by the @author tags.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.eclipse.ec4j.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ec4j.EditorConfigConstants;
import org.eclipse.ec4j.ResourceProvider;
import org.eclipse.ec4j.model.optiontypes.OptionTypeRegistry;
import org.eclipse.ec4j.parser.EditorConfigParser;

public class EditorConfig {

	private final List<Section> sections;
	private final OptionTypeRegistry registry;
	private final String version;

	private Boolean root;
	private String dirPath;

	public EditorConfig() {
		this(OptionTypeRegistry.DEFAULT, EditorConfigConstants.VERSION);
	}

	public EditorConfig(OptionTypeRegistry registry, String version) {
		this.sections = new ArrayList<>();
		this.registry = registry;
		this.version = version;
	}

	public OptionTypeRegistry getRegistry() {
		return registry;
	}

	public static EditorConfig load(Reader reader) throws IOException {
		return load(reader, OptionTypeRegistry.DEFAULT);
	}

	public static EditorConfig load(Reader reader, OptionTypeRegistry registry) throws IOException {
		return load(reader, registry, EditorConfigConstants.VERSION);
	}

	public static EditorConfig load(Reader reader, OptionTypeRegistry registry, String version) throws IOException {
		EditorConfigHandler handler = new EditorConfigHandler(registry, version);
		new EditorConfigParser<Section, Option>(handler).setVersion(version).parse(reader);
		return handler.getEditorConfig();
	}

	public static <T> EditorConfig load(T configFile, ResourceProvider<T> provider, OptionTypeRegistry registry,
			String version) throws IOException {
		try (BufferedReader reader = new BufferedReader(provider.getContent(configFile));) {
			EditorConfig config = load(reader, registry, version);
			T dir = provider.getParent(configFile);
			config.setDirPath(provider.getPath(dir) + "/");
			return config;
		}
	}

	public void addSection(Section section) {
		sections.add(section);
	}

	public List<Section> getSections() {
		return sections;
	}

	public Boolean getRoot() {
		return root;
	}

	public void setRoot(Boolean root) {
		this.root = root;
	}

	public boolean isRoot() {
		return root != null && root;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		if (getRoot() != null) {
			s.append("root = ");
			s.append(isRoot());
			s.append("\n\n");
		}
		int i = 0;
		for (Section section : sections) {
			if (i > 0) {
				s.append("\n\n");
			}
			s.append(section.toString());
			i++;
		}
		return s.toString();
	}

	public String getDirPath() {
		return dirPath;
	}

	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}

	public String getVersion() {
		return version;
	}
}
