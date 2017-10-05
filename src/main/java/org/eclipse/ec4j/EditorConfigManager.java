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
package org.eclipse.ec4j;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.ec4j.model.EditorConfig;
import org.eclipse.ec4j.model.Option;
import org.eclipse.ec4j.model.Section;
import org.eclipse.ec4j.model.optiontypes.OptionTypeRegistry;

public class EditorConfigManager {

	public static String VERSION = "0.12.0-final";

	private final String configFilename;
	private final String version;
	private final OptionTypeRegistry registry;

	/**
	 * Creates EditorConfig handler with default configuration filename
	 * (.editorconfig) and version {@link EditorConfig#VERSION}
	 */
	public EditorConfigManager() {
		this(OptionTypeRegistry.DEFAULT, EditorConfigConstants.EDITORCONFIG, VERSION);
	}

	/**
	 * Creates EditorConfig handler with specified configuration filename and
	 * version. Used mostly for debugging/testing.
	 * 
	 * @param option
	 *            type registry
	 * @param configFilename
	 *            configuration file name to be searched for instead of
	 *            .editorconfig
	 * @param version
	 *            required version
	 */
	public EditorConfigManager(OptionTypeRegistry registry, String configFilename, String version) {
		this.registry = registry;
		this.configFilename = configFilename;
		this.version = version;
	}

	public Collection<Option> getOptions(File file, Set<File> explicitRootDirs) throws EditorConfigException {
		Map<String, Option> oldOptions = Collections.emptyMap();
		Map<String, Option> options = new LinkedHashMap<>();

		try {
			boolean root = false;
			File dir = file.getParentFile();
			while (dir != null && !root) {
				File configFile = new File(dir, configFilename);
				if (configFile.exists()) {
					EditorConfig config = EditorConfig.load(configFile, getRegistry());
					root = config.isRoot();
					List<Section> sections = config.getSections();
					for (Section section : sections) {
						if (section.match(file)) {
							// Section matches the editor file, collect options of the section
							List<Option> o = section.getOptions();
							for (Option option : o) {
								options.put(option.getName(), option);
							}
						}
					}
				}
				root |= explicitRootDirs != null && explicitRootDirs.contains(dir);
				dir = dir.getParentFile();
			}
		} catch (IOException e) {
			throw new EditorConfigException(null, e);
		}

		return options.values();
	}

	public OptionTypeRegistry getRegistry() {
		return registry;
	}
}
