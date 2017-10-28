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
package org.eclipse.ec4j.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.ec4j.core.Resources.Resource;
import org.eclipse.ec4j.core.Resources.StringResourceTree;
import org.eclipse.ec4j.core.model.Option;
import org.junit.Assert;
import org.junit.Test;

/**
 * Some glob test of https://github.com/editorconfig/editorconfig-core-test/tree/master/filetree
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 *
 */
public class EditorConfigFileTreeTest {
    private static final Path basedir = Paths.get(System.getProperty("basedir", ".")).toAbsolutePath().normalize();
    private static final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
    private static final Path testProjectDir = basedir.resolve("target/test-classes/path_separator");

    /**
     * Demonstrates way to construct a {@link Path} containing a segment with backslash on non-Windows operating
     * systems.
     */
    @Test
    public void backslashNameOnUnix() {
        if (!isWindows) {
            Assert.assertEquals("/root/folder\\folder", Paths.get("/root", "folder\\folder").toString());
            Assert.assertEquals("/root\\folder", Paths.get("/", "root\\folder").toString());

            /* funny enough, "" is ignored */
            Path p = Paths.get("", "root\\folder");
            Assert.assertEquals("root\\folder", p.toString());

            /* make also sure that normalization does not break the backslash in the last segment */
            Path dir = Paths.get(".").toAbsolutePath().normalize();
            Path pNormal = p.toAbsolutePath().normalize();
            String suffix = pNormal.toString().substring(dir.toString().length() + 1);
            Assert.assertEquals("root\\folder", suffix);
        }
    }

    /**
     * Windows style path separator in the command line should work on Windows, but should not work on other systems
     *
     * @throws IOException
     * @throws EditorConfigException
     */
    @Test
    public void path_separator_backslash_in_cmd_line() throws IOException, EditorConfigException {

        if (isWindows) {
            final Resource testFile = Resources.ofPath(testProjectDir.resolve("path\\separator"));
            Collection<Option> options = EditorConfigSession.default_().queryOptions(testFile);
            Assert.assertEquals(1, options.size());
            Iterator<Option> it = options.iterator();
            Assert.assertEquals("key = value", it.next().toString());
        } else {
            final Resource testFile = Resources.ofPath(testProjectDir.resolve(Paths.get("", "path\\separator")));
            Collection<Option> options = EditorConfigSession.default_().queryOptions(testFile);
            Assert.assertEquals(0, options.size());
        }

    }

    @Test
    public void parent_directory() throws IOException, EditorConfigException {
        final String testFile = "root/parent_directory/test.a";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", getClass().getResource("/filetree/.editorconfig"), StandardCharsets.UTF_8)//
                .resource("root/parent_directory/.editorconfig", getClass().getResource("/filetree/parent_directory/.editorconfig"), StandardCharsets.UTF_8)//
                .touch(testFile) //
                .build();

        Collection<Option> options = EditorConfigSession.default_().queryOptions(tree.getResource(testFile));
        Assert.assertEquals(1, options.size());
        Assert.assertEquals("key = value", options.iterator().next().toString());
    }

    @Test
    public void windows_separator() throws IOException, EditorConfigException {
        String content = "; test for path separator\r\n" + "\r\n" + "root=true\r\n" + "\r\n" + "[path/separator]\r\n"
                + "key=value\r\n" + "\r\n" + "[/top/of/path]\r\n" + "key=value\r\n" + "\r\n"
                + "[windows\\separator]\r\n" + "key=value\r\n" + "\r\n" + "[windows\\\\separator2]\r\n"
                + "key=value\r\n" + "";

        final String testFile = "root/windows/separator";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", content)//
                .touch(testFile) //
                .build();

        EditorConfigSession.default_().queryOptions(tree.getResource(testFile));
        // Assert.assertTrue(options.isEmpty());
    }

}
