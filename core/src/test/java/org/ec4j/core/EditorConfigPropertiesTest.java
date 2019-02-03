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
import java.util.Collection;
import java.util.Iterator;

import org.ec4j.core.Resource.Resources.StringResourceTree;
import org.ec4j.core.model.Property;
import org.ec4j.core.model.PropertyType;
import org.ec4j.core.model.Version;
import org.junit.Assert;
import org.junit.Test;

/**
 * Some glob test of https://github.com/editorconfig/editorconfig-core-test/tree/master/properties
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class EditorConfigPropertiesTest {

    @Test
    public void indent_size_default() throws IOException {
        String content = "root = true\n" + //
                "\n" + //
                "[test.c]\n" + //
                "indent_style = tab\n" + //
                "\n" + //
                "[test2.c]\n" + //
                "indent_style = space\n" + //
                "\n" + //
                "[test3.c]\n" + //
                "indent_style = tab\n" + //
                "tab_width = 2\n" + //
                "";

        final String testFile = "root/test.c";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", content)//
                .touch(testFile) //
                .build();

        Collection<Property> properties = ResourcePropertiesService.default_()
                .queryProperties(tree.getResource(testFile)).getProperties().values();

        Assert.assertEquals(2, properties.size());
        Iterator<Property> iter = properties.iterator();
        Assert.assertEquals("indent_style = tab", iter.next().toString());
        Assert.assertEquals("indent_size = tab", iter.next().toString());
    }

    @Test
    public void max_line_length() throws IOException {
        String content = "root = true\n" + //
                "\n" + //
                "[test.a]\n" + //
                "max_line_length = 2147483647\n" + //
                "\n" + //
                "[test.b]\n" + //
                "max_line_length = 128\n" + //
                "\n" + //
                "[test.c]\n" + //
                "max_line_length = off\n" + //
                "";

        final String testA = "root/test.a";
        final String testB = "root/test.b";
        final String testC = "root/test.c";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", content)//
                .touch(testA) //
                .touch(testB) //
                .touch(testC) //
                .build();

        final PropertyTypeRegistry registry = PropertyTypeRegistry.builder() //
                .defaults() //
                .type(PropertyType.max_line_length) //
                .build();
        final ResourcePropertiesService rps = ResourcePropertiesService.builder() //
                .loader(EditorConfigLoader.of(Version.CURRENT, registry)) //
                .build();

        {
            ResourceProperties properties = rps.queryProperties(tree.getResource(testC));
            Assert.assertNull(properties.getValue(PropertyType.max_line_length, null, true));
            Assert.assertEquals(1, properties.getProperties().size());
        }
        {
            ResourceProperties properties = rps.queryProperties(tree.getResource(testA));
            Assert.assertEquals(1, properties.getProperties().size());
            Assert.assertEquals(Integer.valueOf(Integer.MAX_VALUE),
                    properties.getValue(PropertyType.max_line_length, null, true));
        }
        {
            ResourceProperties properties = rps.queryProperties(tree.getResource(testB));
            Assert.assertEquals(1, properties.getProperties().size());
            Assert.assertEquals(Integer.valueOf(128), properties.getValue(PropertyType.max_line_length, null, true));
        }
    }
}
