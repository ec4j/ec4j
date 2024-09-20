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
package org.ec4j.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.ec4j.core.Resource.Resources;
import org.ec4j.core.Resource.Resources.StringResourceTree;
import org.ec4j.core.model.EditorConfig;
import org.ec4j.core.model.Glob;
import org.ec4j.core.model.Property;
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

    @Test
    public void parent_directory() throws IOException {
        final String testFile = "root/parent_directory/test.a";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", getClass().getResource("/filetree/.editorconfig"),
                        StandardCharsets.UTF_8)//
                .resource("root/parent_directory/.editorconfig",
                        getClass().getResource("/filetree/parent_directory/.editorconfig"), StandardCharsets.UTF_8)//
                .touch(testFile) //
                .build();

        final ResourceProperties result = ResourcePropertiesService.default_()
                .queryProperties(tree.getResource(testFile));
        Collection<Property> properties = result.getProperties().values();
        Assert.assertEquals(1, properties.size());
        Assert.assertEquals("key = value", properties.iterator().next().toString());

        Assert.assertEquals(
                Arrays.asList(
                        tree.getResource("root/parent_directory/.editorconfig").getPath(),
                        tree.getResource("root/.editorconfig").getPath()),
                result.getEditorConfigFiles());
    }

    /**
     * Windows style path separator in the command line should work on Windows, but should not work on other systems
     *
     * @throws IOException
     */
    @Test
    public void path_separator_backslash_in_cmd_line() throws IOException {

        if (isWindows) {
            final Resource testFile = Resources.ofPath(testProjectDir.resolve("path\\separator"),
                    StandardCharsets.UTF_8);
            Collection<Property> properties = ResourcePropertiesService.default_().queryProperties(testFile)
                    .getProperties().values();
            Assert.assertEquals(1, properties.size());
            Iterator<Property> it = properties.iterator();
            Assert.assertEquals("key = value", it.next().toString());
        } else {
            final Resource testFile = Resources.ofPath(testProjectDir.resolve(Paths.get("", "path\\separator")),
                    StandardCharsets.UTF_8);
            Collection<Property> properties = ResourcePropertiesService.default_().queryProperties(testFile)
                    .getProperties().values();
            Assert.assertEquals(0, properties.size());
        }

    }

    @Test
    public void singleDefault() throws IOException {
        final String testFile = "root/parent_directory/test.a";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", getClass().getResource("/filetree/.editorconfig"),
                        StandardCharsets.UTF_8)//
                .resource("root/parent_directory/.editorconfig",
                        getClass().getResource("/filetree/parent_directory/.editorconfig"), StandardCharsets.UTF_8)//
                .touch(testFile) //
                .build();

        final EditorConfig defaultEditorConfig = EditorConfig.builder() //
                .openSection() //
                .glob(new Glob("test.a")) //
                .openProperty() //
                .name("key2") //
                .value("value2") //
                .closeProperty() //
                .openProperty() //
                .name("key") //
                .value("foo") //
                .closeProperty() //
                .closeSection() //
                .build();

        ResourcePropertiesService rps = ResourcePropertiesService.builder() //
                .defaultEditorConfig(defaultEditorConfig) //
                .build();

        Map<String, Property> properties = rps.queryProperties(tree.getResource(testFile)).getProperties();
        Assert.assertEquals(2, properties.size());
        Assert.assertEquals("value", properties.get("key").getValueAs());
        Assert.assertEquals("value2", properties.get("key2").getValueAs());
    }

    @Test
    public void twoDefaults() throws IOException {
        final String testFile = "root/parent_directory/test.a";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", getClass().getResource("/filetree/.editorconfig"),
                        StandardCharsets.UTF_8)//
                .resource("root/parent_directory/.editorconfig",
                        getClass().getResource("/filetree/parent_directory/.editorconfig"), StandardCharsets.UTF_8)//
                .touch(testFile) //
                .build();

        final EditorConfig defaultEditorConfig1 = EditorConfig.builder() //
                .openSection() //
                .glob(new Glob("test.a")) //
                .openProperty() //
                .name("key2") //
                .value("value2") //
                .closeProperty() //
                .openProperty() //
                .name("key") //
                .value("foo") //
                .closeProperty() //
                .closeSection() //
                .build();

        final EditorConfig defaultEditorConfig2 = EditorConfig.builder() //
                .openSection() //
                .glob(new Glob("test.a")) //
                .openProperty() //
                .name("key3") //
                .value("value3") //
                .closeProperty() //
                .openProperty() //
                .name("key2") //
                .value("foo") //
                .closeProperty() //
                .openProperty() //
                .name("key") //
                .value("foo") //
                .closeProperty() //
                .closeSection() //
                .build();

        ResourcePropertiesService rps = ResourcePropertiesService.builder() //
                .defaultEditorConfigs(defaultEditorConfig1, defaultEditorConfig2) //
                .build();

        Map<String, Property> properties = rps.queryProperties(tree.getResource(testFile)).getProperties();
        Assert.assertEquals(3, properties.size());
        Assert.assertEquals("value", properties.get("key").getValueAs());
        Assert.assertEquals("value2", properties.get("key2").getValueAs());
        Assert.assertEquals("value3", properties.get("key3").getValueAs());
    }

    @Test
    public void windows_separator() throws IOException {
        String content = "; test for path separator\r\n" + //
                "\r\n" + //
                "root=true\r\n" + //
                "\r\n" + //
                "[path/separator]\r\n" + //
                "key=value\r\n" + //
                "\r\n" + //
                "[/top/of/path]\r\n" + //
                "key=value\r\n" + //
                "\r\n" + //
                "[windows\\separator]\r\n" + //
                "key=value\r\n" + //
                "\r\n" + //
                "[windows\\\\separator2]\r\n" + //
                "key=value\r\n" + //
                "";

        final String testFile = "root/windows/separator";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", content)//
                .touch(testFile) //
                .build();

        ResourcePropertiesService.default_().queryProperties(tree.getResource(testFile));
    }

}
