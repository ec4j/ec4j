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
import java.util.Collections;

import org.ec4j.core.Resource.Resources;
import org.ec4j.core.model.Property;
import org.ec4j.core.model.PropertyType;
import org.ec4j.core.model.Section;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class EditorConfigUnsetTest {
    private static final Path basedir = Paths.get(System.getProperty("basedir", ".")).toAbsolutePath().normalize();
    private static final Path unsetDir = basedir.resolve("target/test-classes/unset/dir");
    private static final ResourcePropertiesService withoutUnset;
    private static final ResourcePropertiesService withUnset;

    static {
        withoutUnset = ResourcePropertiesService.default_();
        withUnset = ResourcePropertiesService.builder().keepUnset(true).build();
    }

    private static void assertProperties(String key) throws IOException {
        final Resource resource = Resources.ofPath(unsetDir.resolve(key + ".txt"), StandardCharsets.UTF_8);
        ResourceProperties props = withoutUnset.queryProperties(resource);
        Assert.assertEquals(Collections.emptyMap(), props.getProperties());

        ResourceProperties props2 = withUnset.queryProperties(resource);
        Assert.assertEquals(Collections.singletonMap(key, Property.builder().name(key).value("unset").build()),
                props2.getProperties());
    }

    @Test
    public void unset_charset() throws IOException {
        assertProperties(PropertyType.charset.getName());
    }

    @Test
    public void unset_end_of_line() throws IOException {
        assertProperties(PropertyType.end_of_line.getName());
    }

    @Test
    public void unset_indent_size() throws IOException {
        final String key = PropertyType.indent_size.getName();
        final Resource resource = Resources.ofPath(unsetDir.resolve(key + ".txt"), StandardCharsets.UTF_8);
        ResourceProperties props = withoutUnset.queryProperties(resource);
        Assert.assertEquals(Collections.emptyMap(), props.getProperties());

        ResourceProperties props2 = withUnset.queryProperties(resource);
        Assert.assertEquals(Section.builder() //
                .property(Property.builder().name(key).value("unset")) //
                .property(Property.builder().name(PropertyType.tab_width.getName()).value("unset")) //
                .build().getProperties(), //
                props2.getProperties());

    }

    @Test
    public void unset_indent_style() throws IOException {
        assertProperties(PropertyType.indent_style.getName());
    }

    @Test
    public void unset_insert_final_newline() throws IOException {
        assertProperties(PropertyType.insert_final_newline.getName());
    }

    @Test
    public void unset_max_line_length() throws IOException {
        assertProperties(PropertyType.max_line_length.getName());
    }

    @Test
    public void unset_tab_width() throws IOException {
        assertProperties(PropertyType.tab_width.getName());
    }

    @Test
    public void unset_trim_trailing_whitespace() throws IOException {
        assertProperties(PropertyType.trim_trailing_whitespace.getName());
    }
}
