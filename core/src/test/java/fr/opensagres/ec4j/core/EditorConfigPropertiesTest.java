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
package fr.opensagres.ec4j.core;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import fr.opensagres.ec4j.core.Resources.StringResourceTree;
import fr.opensagres.ec4j.core.model.Property;

/**
 * Some glob test of
 * https://github.com/editorconfig/editorconfig-core-test/tree/master/properties
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class EditorConfigPropertiesTest {

    @Test
    public void indent_size_default() throws IOException, EditorConfigException {
        String content = "root = true\r\n" +
                "\r\n" +
                "[test.c]\r\n" +
                "indent_style = tab\r\n" +
                "\r\n" +
                "[test2.c]\r\n" +
                "indent_style = space\r\n" +
                "\r\n" +
                "[test3.c]\r\n" +
                "indent_style = tab\r\n" +
                "tab_width = 2\r\n" +
                "";

        final String testFile = "root/test.c";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", content)//
                .touch(testFile) //
                .build();

        Collection<Property> properties = EditorConfigSession.default_().queryProperties(tree.getResource(testFile));

        Assert.assertEquals(2, properties.size());
        Iterator<Property> iter = properties.iterator();
        Assert.assertEquals("indent_style = tab", iter.next().toString());
        Assert.assertEquals("indent_size = tab", iter.next().toString());
    }

}

