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
import java.util.Collection;

import org.eclipse.ec4j.core.model.Option;
import org.junit.Assert;
import org.junit.Test;

/**
 * Some glob test of
 * https://github.com/editorconfig/editorconfig-core-test/tree/master/parser
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class EditorConfigParserTest {

    private static final String WHITESPACE_DOT_IN = "; test whitespace usage\r\n" +
            "\r\n" +
            "root = true\r\n" +
            "\r\n" +
            "; no whitespace\r\n" +
            "[test1.c]\r\n" +
            "key=value\r\n" +
            "\r\n" +
            "; spaces around equals\r\n" +
            "[test2.c]\r\n" +
            "key = value\r\n" +
            "\r\n" +
            "; lots of space after equals\r\n" +
            "[test3.c]\r\n" +
            "key  =   value\r\n" +
            "\r\n" +
            "; spaces before property name\r\n" +
            "[test4.c]\r\n" +
            "  key=value\r\n" +
            "\r\n" +
            "; spaces after property value\r\n" +
            "[test5.c]\r\n" +
            "key=value  \r\n" +
            "\r\n" +
            "; blank lines between properties\r\n" +
            "[test6.c]\r\n" +
            "\r\n" +
            "key1=value1\r\n" +
            "\r\n" +
            "key2=value2\r\n" +
            "\r\n" +
            "; spaces in section name\r\n" +
            "[ test 7 ]\r\n" +
            "key=value\r\n" +
            "\r\n" +
            "; spaces before section name\r\n" +
            "  [test8.c]\r\n" +
            "key=value\r\n" +
            "\r\n" +
            "; spaces after section name\r\n" +
            "[test9.c]  \r\n" +
            "key=value\r\n" +
            "\r\n" +
            "; spacing before middle property\r\n" +
            "[test10.c]\r\n" +
            "key1=value1\r\n" +
            "  key2=value2\r\n" +
            "key3=value3\r\n" +
            "\r\n" +
            "; colon separator with no spaces\r\n" +
            "[test1.d]\r\n" +
            "key:value\r\n" +
            "\r\n" +
            "; colon separator with space after\r\n" +
            "[test2.d]\r\n" +
            "key: value\r\n" +
            "\r\n" +
            "; colon separator with space before and after\r\n" +
            "[test3.d]\r\n" +
            "key : value\r\n" +
            "\r\n" +
            "; colon separator with spaces befor\r\n" +
            "[test4.d]\r\n" +
            "  key:value\r\n" +
            "\r\n" +
            "; colon seperator with spaces after property value\r\n" +
            "[test5.d]\r\n" +
            "key:value  \r\n" +
            "";

    @Test
    public void spaces_in_section_name() throws IOException, EditorConfigException {

        TestEditorConfigManager manager = new TestEditorConfigManager();

        TestFolder root = new TestFolder("root");
        root.addFile(".editorconfig", WHITESPACE_DOT_IN);
        TestFile file = root.addFile(" test 7 ");

        Collection<Option> options = manager.getOptions(file, null);
        Assert.assertEquals(1, options.size());
        Assert.assertEquals("key = value", options.iterator().next().toString());
    }

    private static final String COMMENTS_DOT_IN = "; test comments\r\n" +
            "\r\n" +
            "root = true\r\n" +
            "\r\n" +
            "[test1.c]\r\n" +
            "key=value ; Comment after property is ignored\r\n" +
            "\r\n" +
            "[test2.c] ; Comment ignored, even with ] character\r\n" +
            "key=value\r\n" +
            "\r\n" +
            "[test3.c]\r\n" +
            "; Comment before properties ignored\r\n" +
            "key=value\r\n" +
            "\r\n" +
            "[test4.c]\r\n" +
            "key1=value1\r\n" +
            "; Comment between properties ignored\r\n" +
            "key2=value2\r\n" +
            "\r\n" +
            "; Semicolon at end of value read as part of value\r\n" +
            "[test5.c]\r\n" +
            "key=value; not comment\r\n" +
            "\r\n" +
            "; Escaped semicolon in value\r\n" +
            "[test6.c]\r\n" +
            "key=value \\; not comment\r\n" +
            "\r\n" +
            "; Escaped semicolon in section name\r\n" +
            "[test\\;.c]\r\n" +
            "key=value\r\n" +
            "\r\n" +
            "[test7.c]\r\n" +
            "key=value # Comment after property is ignored\r\n" +
            "\r\n" +
            "[test8.c] # Comment ignored, even with ] character\r\n" +
            "key=value\r\n" +
            "\r\n" +
            "[test9.c]\r\n" +
            "# Comment before properties ignored\r\n" +
            "key=value\r\n" +
            "\r\n" +
            "[test10.c]\r\n" +
            "key1=value1\r\n" +
            "# Comment between properties ignored\r\n" +
            "key2=value2\r\n" +
            "\r\n" +
            "# Semicolon at end of value read as part of value\r\n" +
            "[test11.c]\r\n" +
            "key=value# not comment\r\n" +
            "\r\n" +
            "# Escaped semicolon in value\r\n" +
            "[test12.c]\r\n" +
            "key=value \\# not comment\r\n" +
            "\r\n" +
            "# Escaped semicolon in section name\r\n" +
            "[test\\#.c]\r\n" +
            "key=value\r\n" +
            "\r\n" +
            "";

    @Test
    public void comments_after_property() throws IOException, EditorConfigException {

        TestEditorConfigManager manager = new TestEditorConfigManager();

        TestFolder root = new TestFolder("root");
        root.addFile(".editorconfig", COMMENTS_DOT_IN);
        TestFile file = root.addFile("test7.c");

        Collection<Option> options = manager.getOptions(file, null);
        Assert.assertEquals(1, options.size());
        Assert.assertEquals("key = value ", options.iterator().next().toString());
    }

}
