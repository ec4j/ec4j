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

import org.eclipse.ec4j.core.Resources.StringResourceTree;
import org.eclipse.ec4j.core.model.Option;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Some glob test of
 * https://github.com/editorconfig/editorconfig-core-test/tree/master/glob
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class EditorConfigGlobTest {
    private static final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

    @Test
    public void star_after_slash() throws IOException, EditorConfigException {
        String content = "; test *\r\n" +
                "\r\n" +
                "root=true\r\n" +
                "\r\n" +
                "[a*e.c]\r\n" +
                "key=value\r\n" +
                "\r\n" +
                "[Bar/*]\r\n" +
                "keyb=valueb\r\n" +
                "";


        final String testFile = "root/Bar/foo.txt";
        StringResourceTree tree = StringResourceTree.builder() //
            .resource("root/.editorconfig", content)//
            .touch(testFile) //
            .build();

        Collection<Option> options = EditorConfigSession.default_().queryOptions(tree.getResource(testFile));
        Assert.assertEquals(1, options.size());
        Assert.assertEquals("keyb = valueb", options.iterator().next().toString());
    }

    @Test
    public void utf_8_char() throws IOException, EditorConfigException {
        String content = "; test EditorConfig files with UTF-8 characters larger than 127\r\n" +
                "\r\n" +
                "root = true\r\n" +
                "\r\n" +
                "[中文.txt]\r\n" +
                "key = value\r\n" +
                "";

        final String testFile = "root/中文.txt";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", content)//
                .touch(testFile) //
                .build();

        Collection<Option> options = EditorConfigSession.default_().queryOptions(tree.getResource(testFile));
        Assert.assertEquals(1, options.size());
        Assert.assertEquals("key = value", options.iterator().next().toString());
    }

    private static final String BRACKETS_DOT_IN = "; test [ and ]\r\n" +
            "\r\n" +
            "root=true\r\n" +
            "\r\n" +
            "; Character choice\r\n" +
            "[[ab].a]\r\n" +
            "choice=true\r\n" +
            "\r\n" +
            "; Negative character choice\r\n" +
            "[[!ab].b]\r\n" +
            "choice=false\r\n" +
            "\r\n" +
            "; Character range\r\n" +
            "[[d-g].c]\r\n" +
            "range=true\r\n" +
            "\r\n" +
            "; Negative character range\r\n" +
            "[[!d-g].d]\r\n" +
            "range=false\r\n" +
            "\r\n" +
            "; Range and choice\r\n" +
            "[[abd-g].e]\r\n" +
            "range_and_choice=true\r\n" +
            "\r\n" +
            "; Choice with dash\r\n" +
            "[[-ab].f]\r\n" +
            "choice_with_dash=true\r\n" +
            "\r\n" +
            "; Close bracket inside\r\n" +
            "[[\\]ab].g]\r\n" +
            "close_inside=true\r\n" +
            "\r\n" +
            "; Close bracket outside\r\n" +
            "[[ab]].g]\r\n" +
            "close_outside=true\r\n" +
            "\r\n" +
            "; Negative close bracket inside\r\n" +
            "[[!\\]ab].g]\r\n" +
            "close_inside=false\r\n" +
            "\r\n" +
            "; Negative¬close bracket outside\r\n" +
            "[[!ab]].g]\r\n" +
            "close_outside=false\r\n" +
            "\r\n" +
            "; Slash inside brackets\r\n" +
            "[ab[e/]cd.i]\r\n" +
            "slash_inside=true\r\n" +
            "\r\n" +
            "; Slash after an half-open bracket\r\n" +
            "[ab[/c]\r\n" +
            "slash_half_open=true\r\n" +
            "";

    @Ignore
    @Test()
    public void brackets_close_inside() throws IOException, EditorConfigException {

        final String testFile = "root/].g";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", BRACKETS_DOT_IN)//
                .touch(testFile) //
                .build();

        Collection<Option> options = EditorConfigSession.default_().queryOptions(tree.getResource(testFile));
        Assert.assertEquals(1, options.size());
        Assert.assertEquals("close_inside = true", options.iterator().next().toString());
    }

    @Test
    public void brackets_close_outside() throws IOException, EditorConfigException {

        final String testFile = "root/b].g";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", BRACKETS_DOT_IN)//
                .touch(testFile) //
                .build();

        Collection<Option> options = EditorConfigSession.default_().queryOptions(tree.getResource(testFile));
        Assert.assertEquals(1, options.size());
        Assert.assertEquals("close_outside = true", options.iterator().next().toString());
    }

    private static final String BRACES_DOT_IN = "; test { and }\r\n" +
            "\r\n" +
            "root=true\r\n" +
            "\r\n" +
            "; word choice\r\n" +
            "[*.{py,js,html}]\r\n" +
            "choice=true\r\n" +
            "\r\n" +
            "; single choice\r\n" +
            "[{single}.b]\r\n" +
            "choice=single\r\n" +
            "\r\n" +
            "; empty choice\r\n" +
            "[{}.c]\r\n" +
            "empty=all\r\n" +
            "\r\n" +
            "; choice with empty word\r\n" +
            "[a{b,c,}.d]\r\n" +
            "empty=word\r\n" +
            "\r\n" +
            "; choice with empty words\r\n" +
            "[a{,b,,c,}.e]\r\n" +
            "empty=words\r\n" +
            "\r\n" +
            "; no closing brace\r\n" +
            "[{.f]\r\n" +
            "closing=false\r\n" +
            "\r\n" +
            "; nested braces\r\n" +
            "[{word,{also},this}.g]\r\n" +
            "nested=true\r\n" +
            "\r\n" +
            "; closing inside beginning\r\n" +
            "[{},b}.h]\r\n" +
            "closing=inside\r\n" +
            "\r\n" +
            "; opening inside beginning\r\n" +
            "[{{,b,c{d}.i]\r\n" +
            "unmatched=true\r\n" +
            "\r\n" +
            "; escaped comma\r\n" +
            "[{a\\,b,cd}.txt]\r\n" +
            "comma=yes\r\n" +
            "\r\n" +
            "; escaped closing brace\r\n" +
            "[{e,\\},f}.txt]\r\n" +
            "closing=yes\r\n" +
            "\r\n" +
            "; escaped backslash\r\n" +
            "[{g,\\\\,i}.txt]\r\n" +
            "backslash=yes\r\n" +
            "\r\n" +
            "; patterns nested in braces\r\n" +
            "[{some,a{*c,b}[ef]}.j]\r\n" +
            "patterns=nested\r\n" +
            "\r\n" +
            "; numeric braces\r\n" +
            "[{3..120}]\r\n" +
            "number=true\r\n" +
            "\r\n" +
            "; alphabetical\r\n" +
            "[{aardvark..antelope}]\r\n" +
            "words=a\r\n" +
            "";

    @Test
    public void braces_word_choice1() throws IOException, EditorConfigException {

        final String testFile = "root/test.py";
        StringResourceTree tree = StringResourceTree.builder() //
            .resource("root/.editorconfig", BRACES_DOT_IN)//
            .touch(testFile) //
            .build();

        Collection<Option> options = EditorConfigSession.default_().queryOptions(tree.getResource(testFile));
        Assert.assertEquals(1, options.size());
        Assert.assertEquals("choice = true", options.iterator().next().toString());
    }

    private static final String STAR_STAR_DOT_IN = "; test **\r\n" +
            "\r\n" +
            "root=true\r\n" +
            "\r\n" +
            "[a**z.c]\r\n" +
            "key1=value1\r\n" +
            "\r\n" +
            "[b/**z.c]\r\n" +
            "key2=value2\r\n" +
            "\r\n" +
            "[c**/z.c]\r\n" +
            "key3=value3\r\n" +
            "\r\n" +
            "[d/**/z.c]\r\n" +
            "key4=value4\r\n" +
            "";

    @Test
    public void star_star_over_separator7() throws IOException, EditorConfigException {

        final String testFile = "root/b/z.c";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", STAR_STAR_DOT_IN)//
                .touch(testFile) //
                .build();


        Collection<Option> options = EditorConfigSession.default_().queryOptions(tree.getResource(testFile));
        Assert.assertEquals(1, options.size());
        Assert.assertEquals("key2 = value2", options.iterator().next().toString());
    }

    @Test
    public void braces_escaped_backslash2() throws EditorConfigException {
        if (!isWindows) {
            final String testFile = "root/\\.txt";
            StringResourceTree tree = StringResourceTree.builder() //
                .resource("root/.editorconfig", BRACES_DOT_IN)//
                .touch(testFile) //
                .build();

            Collection<Option> options = EditorConfigSession.default_().queryOptions(tree.getResource(testFile));
            Assert.assertEquals(1, options.size());
            Assert.assertEquals("backslash = yes", options.iterator().next().toString());
        }

    }

}

