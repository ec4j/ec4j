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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.ec4j.model.EditorConfig;
import org.eclipse.ec4j.model.Option;
import org.eclipse.ec4j.model.Section;
import org.eclipse.ec4j.model.optiontypes.OptionTypeRegistry;

public class AbstractEditorConfigManager<T> {

	private final String configFilename;
	private final String version;
	private final OptionTypeRegistry registry;
	private final ResourceProvider<T> provider;

	/**
	 * Creates EditorConfig handler with default configuration filename
	 * (.editorconfig) and version {@link EditorConfig#VERSION}
	 */
	public AbstractEditorConfigManager(ResourceProvider<T> provider) {
		this(OptionTypeRegistry.DEFAULT, provider, EditorConfigConstants.EDITORCONFIG, EditorConfigConstants.VERSION);
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
	public AbstractEditorConfigManager(OptionTypeRegistry registry, ResourceProvider<T> provider, String configFilename,
			String version) {
		this.registry = registry;
		this.configFilename = configFilename;
		this.version = version;
		this.provider = provider;
	}

	public Collection<Option> getOptions(T file, Set<T> explicitRootDirs) throws EditorConfigException {
		Map<String, Option> options = new LinkedHashMap<>();
		try {
			boolean root = false;
			T dir = provider.getParent(file);
			while (dir != null && !root) {
				T configFile = provider.getResource(dir, getConfigFilename());
				if (provider.exists(configFile)) {
					EditorConfig config = EditorConfig.load(configFile, provider, getRegistry(), getVersion());
					root = config.isRoot();
					List<Section> sections = config.getSections();
					for (Section section : sections) {
						if (section.match(provider.getPath(file))) {
							// Section matches the editor file, collect options of the section
							List<Option> o = section.getOptions();
							for (Option option : o) {
								options.put(option.getName(), option);
							}
						}
					}
				}
				root |= explicitRootDirs != null && explicitRootDirs.contains(dir);
				dir = provider.getParent(dir);
			}
		} catch (Exception e) {
			throw new EditorConfigException(null, e);
		}

		return options.values();
	}

	public OptionTypeRegistry getRegistry() {
		return registry;
	}

	public String getVersion() {
		return version;
	}

	public String getConfigFilename() {
		return configFilename;
	}
}
