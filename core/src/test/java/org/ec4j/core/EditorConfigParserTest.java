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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.ec4j.core.Resource.Resources.StringResourceTree;
import org.ec4j.core.model.Property;
import org.ec4j.core.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Some glob test of https://github.com/editorconfig/editorconfig-core-test/tree/master/parser
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 *
 */
public class EditorConfigParserTest {

    private static final String WHITESPACE_DOT_IN = "; test whitespace usage\r\n" + //
            "\r\n" + //
            "root = true\r\n" + //
            "\r\n" + //
            "; no whitespace\r\n" + //
            "[test1.c]\r\n" + //
            "key=value\r\n" + //
            "\r\n" + //
            "; spaces around equals\r\n" + //
            "[test2.c]\r\n" + //
            "key = value\r\n" + //
            "\r\n" + //
            "; lots of space after equals\r\n" + //
            "[test3.c]\r\n" + //
            "key  =   value\r\n" + //
            "\r\n" + //
            "; spaces before property name\r\n" + //
            "[test4.c]\r\n" + //
            "  key=value\r\n" + //
            "\r\n" + //
            "; spaces after property value\r\n" + //
            "[test5.c]\r\n" + //
            "key=value  \r\n" + //
            "\r\n" + //
            "; blank lines between properties\r\n" + //
            "[test6.c]\r\n" + //
            "\r\n" + //
            "key1=value1\r\n" + //
            "\r\n" + //
            "key2=value2\r\n" + //
            "\r\n" + //
            "; spaces in section name\r\n" + //
            "[ test 7 ]\r\n" + //
            "key=value\r\n" + //
            "\r\n" + //
            "; spaces before section name\r\n" + //
            "  [test8.c]\r\n" + //
            "key=value\r\n" + //
            "\r\n" + //
            "; spaces after section name\r\n" + //
            "[test9.c]  \r\n" + //
            "key=value\r\n" + //
            "\r\n" + //
            "; spacing before middle property\r\n" + //
            "[test10.c]\r\n" + //
            "key1=value1\r\n" + //
            "  key2=value2\r\n" + //
            "key3=value3\r\n" + //
            "\r\n" + //
            "; colon separator with no spaces\r\n" + //
            "[test1.d]\r\n" + //
            "key:value\r\n" + //
            "\r\n" + //
            "; colon separator with space after\r\n" + //
            "[test2.d]\r\n" + //
            "key: value\r\n" + //
            "\r\n" + //
            "; colon separator with space before and after\r\n" + //
            "[test3.d]\r\n" + //
            "key : value\r\n" + //
            "\r\n" + //
            "; colon separator with spaces befor\r\n" + //
            "[test4.d]\r\n" + //
            "  key:value\r\n" + //
            "\r\n" + //
            "; colon seperator with spaces after property value\r\n" + //
            "[test5.d]\r\n" + //
            "key:value  \r\n" + //
            "";

    @Test
    public void spaces_in_section_name() throws IOException {

        final String testFile = "root/ test 7 ";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", WHITESPACE_DOT_IN)//
                .touch(testFile) //
                .build();

        Collection<Property> properties = ResourcePropertiesService.default_()
                .queryProperties(tree.getResource(testFile)).getProperties().values();
        Assert.assertEquals(1, properties.size());
        Assert.assertEquals("key = value", properties.iterator().next().toString());
    }

    private static final String COMMENTS_DOT_IN = "; test comments\r\n" + //
            "\r\n" + //
            "root = true\r\n" + //
            "\r\n" + //
            "[test1.c]\r\n" + //
            "key=value ; Comment after property is ignored\r\n" + //
            "\r\n" + //
            "[test2.c] ; Comment ignored, even with ] character\r\n" + //
            "key=value\r\n" + //
            "\r\n" + //
            "[test3.c]\r\n" + //
            "; Comment before properties ignored\r\n" + //
            "key=value\r\n" + //
            "\r\n" + //
            "[test4.c]\r\n" + //
            "key1=value1\r\n" + //
            "; Comment between properties ignored\r\n" + //
            "key2=value2\r\n" + //
            "\r\n" + //
            "; Semicolon at end of value read as part of value\r\n" + //
            "[test5.c]\r\n" + //
            "key=value; not comment\r\n" + //
            "\r\n" + //
            "; Escaped semicolon in value\r\n" + //
            "[test6.c]\r\n" + //
            "key=value \\; not comment\r\n" + //
            "\r\n" + //
            "; Escaped semicolon in section name\r\n" + //
            "[test\\;.c]\r\n" + //
            "key=value\r\n" + //
            "\r\n" + //
            "[test7.c]\r\n" + //
            "key=value # Comment after property is ignored\r\n" + //
            "\r\n" + //
            "[test8.c] # Comment ignored, even with ] character\r\n" + //
            "key=value\r\n" + //
            "\r\n" + //
            "[test9.c]\r\n" + //
            "# Comment before properties ignored\r\n" + //
            "key=value\r\n" + //
            "\r\n" + //
            "[test10.c]\r\n" + //
            "key1=value1\r\n" + //
            "# Comment between properties ignored\r\n" + //
            "key2=value2\r\n" + //
            "\r\n" + //
            "# Semicolon at end of value read as part of value\r\n" + //
            "[test11.c]\r\n" + //
            "key=value# not comment\r\n" + //
            "\r\n" + //
            "# Escaped semicolon in value\r\n" + //
            "[test12.c]\r\n" + //
            "key=value \\# not comment\r\n" + //
            "\r\n" + //
            "# Escaped semicolon in section name\r\n" + //
            "[test\\#.c]\r\n" + //
            "key=value\r\n" + //
            "\r\n" + //
            "";

    @Test
    public void comments_after_property() throws IOException {

        final String testFile = "root/test7.c";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", COMMENTS_DOT_IN)//
                .touch(testFile) //
                .build();

        Collection<Property> properties = ResourcePropertiesService.default_()
                .queryProperties(tree.getResource(testFile)).getProperties().values();
        Assert.assertEquals(1, properties.size());
        Assert.assertEquals("key = value", properties.iterator().next().toString());
    }

    private static final String BASIC_DOT_IN = "[*.a]\n" + //
            "name1=value1\n" + //
            "\n" + //
            "; repeat section\n" + //
            "[*.a]\n" + //
            "name2=value2\n" + //
            "\n" + //
            "[*.b]\n" + //
            "name1 = a\n" + //
            "name2 = a\n" + //
            "\n" + //
            "[b.b]\n" + //
            "name2 = b\n" + //
            "\n" + //
            "[*.b]\n" + //
            "name1 = c\n";

    @Test
    public void repeat_sections() throws IOException {

        final String testFile = "root/a.a";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", BASIC_DOT_IN)//
                .touch(testFile) //
                .build();

        Collection<Property> properties = ResourcePropertiesService.default_()
                .queryProperties(tree.getResource(testFile)).getProperties().values();
        Assert.assertEquals(2, properties.size());
        Iterator<Property> it = properties.iterator();
        Assert.assertEquals("name1 = value1", it.next().toString());
        Assert.assertEquals("name2 = value2", it.next().toString());
    }

    @Test
    public void max_section_name_ok() throws IOException {
        final String testFile = "root/test3";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", getClass().getResource("/parser/.editorconfig"), StandardCharsets.UTF_8)//
                .touch(testFile) //
                .build();

        Collection<Property> properties = ResourcePropertiesService.default_()
                .queryProperties(tree.getResource(testFile)).getProperties().values();
        Assert.assertEquals(1, properties.size());
        Iterator<Property> it = properties.iterator();
        Assert.assertEquals("key3 = value3", it.next().toString());
    }

    @Test
    public void max_section_name_long() throws IOException {
        final String testFile = "root/test4";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", getClass().getResource("/parser/.editorconfig"), StandardCharsets.UTF_8)//
                .touch(testFile) //
                .build();

        Collection<Property> properties = ResourcePropertiesService.default_()
                .queryProperties(tree.getResource(testFile)).getProperties().values();
        Assert.assertEquals(1, properties.size());
        Iterator<Property> it = properties.iterator();
        Assert.assertEquals("key4 = value4", it.next().toString());
    }

    @Test
    public void unfinishedGlob() throws IOException {
        final String testFile = "root/test4";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", "[foo")//
                .touch(testFile) //
                .build();

        try {
            ResourcePropertiesService.default_()
                    .queryProperties(tree.getResource(testFile)).getProperties().values();
            Assert.fail("ParseException expected");
        } catch (ParseException e) {
            Assert.assertEquals(e.getMessage(), "string:root/.editorconfig:1:3: Glob pattern not closed. Expected ']'");
        }
    }

    @Test
    public void emptyEditorConfigFile() throws IOException {
        final String testFile = "root/test4";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", "")//
                .touch(testFile) //
                .build();

        Collection<Property> properties = ResourcePropertiesService.default_()
                .queryProperties(tree.getResource(testFile)).getProperties().values();
        Assert.assertEquals(0, properties.size());

    }

    @Test
    public void emptyValues() throws IOException {
        final String testFile = "root/test.txt";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig",
                        "[*.txt]\r\n" + //
                                "key1=\r\n" +
                                "key2=  \r\n")//
                .touch(testFile) //
                .build();

        Map<String, Property> props = ResourcePropertiesService.default_()
                .queryProperties(tree.getResource(testFile)).getProperties();
        Assert.assertEquals(2, props.size());
        Assert.assertEquals("", props.get("key1").getValueAs());
        Assert.assertEquals("", props.get("key2").getValueAs());

    }

    @Test
    public void valueWithWhitespace() throws IOException {
        final String testFile = "root/test.txt";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig",
                        "[*.txt]\r\n" + //
                                "key1= key with whitespace \r\n")//
                .touch(testFile) //
                .build();

        Map<String, Property> props = ResourcePropertiesService.default_()
                .queryProperties(tree.getResource(testFile)).getProperties();
        Assert.assertEquals(1, props.size());
        Assert.assertEquals("key with whitespace", props.get("key1").getValueAs());

    }

}
