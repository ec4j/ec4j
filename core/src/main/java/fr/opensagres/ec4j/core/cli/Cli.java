/**
 * Copyright (c) 2017 Angelo Zerr and other contributors as
 * indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.opensagres.ec4j.core.cli;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.opensagres.ec4j.core.EditorConfigConstants;
import fr.opensagres.ec4j.core.EditorConfigLoader;
import fr.opensagres.ec4j.core.EditorConfigSession;
import fr.opensagres.ec4j.core.ResourcePaths;
import fr.opensagres.ec4j.core.Resources.Resource;
import fr.opensagres.ec4j.core.model.Property;
import fr.opensagres.ec4j.core.model.Version;

/**
 * A simple command line wrapper over {@link EditorConfigManager} so that it can
 * be tested against <a href=
 * "https://github.com/editorconfig/editorconfig-core-test">editorconfig-core-test</a>
 * <p>
 * The current class is based on <a href=
 * "https://github.com/editorconfig/editorconfig-core-java/blob/8f9cf27964a6be1f385594d85c2f1eb587290561/src/main/java/org/editorconfig/EditorConfigCLI.java">EditorConfigCLI</a>
 * by Dennis Ushakov.
 *
 * @author Dennis Ushakov
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class Cli {
    private static final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

    public static void main(String[] args) throws Exception {
        List<String> paths = new ArrayList<>();
        String editorconfigFileName = EditorConfigConstants.EDITORCONFIG;
        Version version = Version.CURRENT;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
            case "-b":
                if (i + 1 < args.length) {
                    version = Version.of(args[++i]);
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
                System.out.println("EditorConfig Java Version " + version);
                System.exit(0);
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

        EditorConfigSession editorConfigSession = EditorConfigSession.builder() //
                .configFileName(editorconfigFileName) //
                .rootDirectory(ResourcePaths.ofPath(Paths.get(".").toAbsolutePath().normalize(), StandardCharsets.UTF_8)) //
                .loader(EditorConfigLoader.of(version)) //
                .build();

        for (String path : paths) {
            if (paths.size() > 1) {
                System.out.println("[" + path + "]");
            }

            /*
             * Citing from https://github.com/editorconfig/editorconfig-core-test/blob/efc9b441f7aa54c17850e75607012cafc3438752/filetree/CMakeLists.txt#L55 :
             * Windows style path separator in the command line should work on Windows, but should not work on other
             * systems
             */
            final Path p;
            if (isWindows) {
                p = Paths.get(path).toAbsolutePath().normalize();
            } else {
                int firstBackSlash = path.indexOf('\\');
                if (firstBackSlash < 0) {
                    /* No backslash - the single arg Path.get() will work properly */
                    p = Paths.get(path).toAbsolutePath().normalize();
                } else {
                    /* Otherwise, we have to use the multiarg Path.get(first, more...) so that the backslashes are not
                     * interpreted as separators. "" segments are ignored by Paths.get() */
                    final String first = path.startsWith("/") ? "/" : "";
                    p = Paths.get(first, path.split("/")).toAbsolutePath().normalize();
                }
            }
            Resource file = fr.opensagres.ec4j.core.Resources.ofPath(p, StandardCharsets.UTF_8);
            Collection<Property> props = editorConfigSession.queryProperties(file);
            for (Property prop : props) {
                System.out.println(prop.getName() + "=" + prop.getSourceValue());
            }
        }
    }

}
