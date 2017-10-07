/**
 *  Copyright (c) 2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
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

	private Boolean root;

	private final List<Section> sections;

	private OptionTypeRegistry registry;

	private String dirPath;

	public EditorConfig() {
		this(OptionTypeRegistry.DEFAULT);
	}

	public EditorConfig(OptionTypeRegistry registry) {
		this.sections = new ArrayList<>();
		this.registry = registry;
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
		EditorConfigHandler handler = new EditorConfigHandler(registry);
		new EditorConfigParser<Section, Option>(handler).setVersion(version).parse(reader);
		return handler.getEditorConfig();
	}

	public static <T> EditorConfig load(T configFile, ResourceProvider<T> provider, OptionTypeRegistry registry,
			String version) throws IOException {
		try (BufferedReader reader = new BufferedReader(provider.getContent(configFile));) {
			EditorConfig config = load(reader, registry, version);
			T dir = provider.getParent(configFile);
			config.dirPath = provider.getPath(dir) + "/";
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
}
