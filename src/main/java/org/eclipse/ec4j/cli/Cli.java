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
package org.eclipse.ec4j.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.ec4j.EditorConfigConstants;
import org.eclipse.ec4j.EditorConfigManager;
import org.eclipse.ec4j.model.Option;
import org.eclipse.ec4j.model.optiontypes.OptionTypeRegistry;

/**
 * A simple command line wrapper over {@link EditorConfigManager} so that it can
 * be tested against <a href=
 * "https://github.com/editorconfig/editorconfig-core-test">editorconfig-core-test</a>
 * <p>
 * The current class is based on <a href=
 * "https://github.com/editorconfig/editorconfig-core-java/blob/8f9cf27964a6be1f385594d85c2f1eb587290561/src/main/java/org/editorconfig/EditorConfigCLI.java">EditorConfigCLI</a>
 * by Dennis Ushakov.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class Cli {
	public static void main(String[] args) throws Exception {
		List<String> paths = new ArrayList<>();
		String editorconfigFileName = EditorConfigConstants.EDITORCONFIG;
		String version = EditorConfigConstants.VERSION;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			switch (arg) {
			case "-b":
				if (i + 1 < args.length) {
					version = args[++i];
					continue;
				} else {
					System.err.println("-b option must be followed by a version");
					System.exit(1);
				}
				break;
			case "-f":
				if (i + 1 < args.length) {
					editorconfigFileName = args[++i];
					continue;
				} else {
					System.err.println("-f option must be followed by a file path");
					System.exit(1);
				}
				break;
			case "--version":
			case "-v":
				break;
			default:
				paths.add(args[i]);
				break;
			}
		}

		if (paths.isEmpty()) {
			System.err.println("At least one file path needs to be specified");
			System.exit(1);
		}

		EditorConfigManager editorConfigManager = new EditorConfigManager(OptionTypeRegistry.DEFAULT,
				editorconfigFileName, version);
		Set<File> roots = Collections.singleton(new File(".").getAbsoluteFile());
		for (String path : paths) {
			if (paths.size() > 1) {
				System.out.println("[" + path + "]");
			}
			Collection<Option> opts = editorConfigManager.getOptions(new File(path).getAbsoluteFile(), roots);
			for (Option opt : opts) {
				System.out.println(opt.getName() + "=" + opt.getValue());
			}
		}
	}

}
