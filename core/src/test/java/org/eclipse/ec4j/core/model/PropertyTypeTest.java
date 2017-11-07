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
package org.eclipse.ec4j.core.model;

import org.eclipse.ec4j.core.model.PropertyType.EndOfLineValue;
import org.eclipse.ec4j.core.model.PropertyType.IndentStyleValue;
import org.eclipse.ec4j.core.model.PropertyType.ParsedValue;
import org.junit.Assert;
import org.junit.Test;

public class PropertyTypeTest {

    private static void assertBoolean(PropertyType<Boolean> type) {
        Assert.assertEquals(ParsedValue.valid(Boolean.TRUE), type.parse("true"));
        Assert.assertEquals(ParsedValue.valid(Boolean.TRUE), type.parse("True"));
        Assert.assertEquals(ParsedValue.valid(Boolean.TRUE), type.parse("TRUE"));
        Assert.assertEquals(ParsedValue.valid(Boolean.TRUE), type.parse("tRuE"));
        Assert.assertEquals(ParsedValue.valid(Boolean.FALSE), type.parse("false"));
        Assert.assertFalse(type.parse("").isValid());
        Assert.assertFalse(type.parse("foo").isValid());
        Assert.assertFalse(type.parse(null).isValid());

    }

    @Test
    public void charset() {
        Assert.assertEquals(ParsedValue.valid("utf-8"), PropertyType.charset.parse("utf-8"));
        Assert.assertEquals(ParsedValue.valid("UTF-8"), PropertyType.charset.parse("UTF-8"));
        Assert.assertTrue(PropertyType.charset.parse("").isValid());
        Assert.assertTrue(PropertyType.charset.parse("foo").isValid());
        Assert.assertTrue(PropertyType.charset.parse(null).isValid());
    }

    @Test
    public void eol() {
        Assert.assertEquals(ParsedValue.valid(EndOfLineValue.cr), PropertyType.end_of_line.parse("cr"));
        Assert.assertEquals(ParsedValue.valid(EndOfLineValue.cr), PropertyType.end_of_line.parse("CR"));
        Assert.assertEquals(ParsedValue.valid(EndOfLineValue.cr), PropertyType.end_of_line.parse("Cr"));
        Assert.assertEquals(ParsedValue.valid(EndOfLineValue.crlf), PropertyType.end_of_line.parse("CrLf"));
        Assert.assertEquals(ParsedValue.valid(EndOfLineValue.lf), PropertyType.end_of_line.parse("lf"));
        Assert.assertFalse(PropertyType.end_of_line.parse("").isValid());
        Assert.assertFalse(PropertyType.end_of_line.parse("foo").isValid());
        Assert.assertFalse(PropertyType.end_of_line.parse(null).isValid());
    }

    @Test
    public void indentSize() {
        Assert.assertEquals(ParsedValue.valid(1), PropertyType.indent_size.parse("1"));
        // TODO: Assert.assertEquals(ParsedValue.valid(?), PropertyType.indent_size.parse("tab"));
        Assert.assertFalse(PropertyType.indent_size.parse("").isValid());
        Assert.assertFalse(PropertyType.indent_size.parse("0").isValid());
        Assert.assertFalse(PropertyType.indent_size.parse("-1").isValid());
        Assert.assertFalse(PropertyType.indent_size.parse("tab").isValid());
        Assert.assertFalse(PropertyType.indent_size.parse(null).isValid());
    }

    @Test
    public void indentStyle() {
        Assert.assertEquals(ParsedValue.valid(IndentStyleValue.space), PropertyType.indent_style.parse("space"));
        Assert.assertEquals(ParsedValue.valid(IndentStyleValue.space), PropertyType.indent_style.parse("SPACE"));
        Assert.assertEquals(ParsedValue.valid(IndentStyleValue.space), PropertyType.indent_style.parse("SpAcE"));
        Assert.assertEquals(ParsedValue.valid(IndentStyleValue.tab), PropertyType.indent_style.parse("tab"));
        Assert.assertFalse(PropertyType.indent_style.parse("").isValid());
        Assert.assertFalse(PropertyType.indent_style.parse("foo").isValid());
        Assert.assertFalse(PropertyType.indent_style.parse(null).isValid());
    }

    @Test
    public void insertFinalNl() {
        assertBoolean(PropertyType.insert_final_newline);
    }

    @Test
    public void root() {
        assertBoolean(PropertyType.root);
    }

    @Test
    public void tabWidth() {
        Assert.assertEquals(ParsedValue.valid(1), PropertyType.tab_width.parse("1"));
        Assert.assertFalse(PropertyType.tab_width.parse("").isValid());
        Assert.assertFalse(PropertyType.tab_width.parse("0").isValid());
        Assert.assertFalse(PropertyType.tab_width.parse("-1").isValid());
        Assert.assertFalse(PropertyType.tab_width.parse("tab").isValid());
        Assert.assertFalse(PropertyType.tab_width.parse(null).isValid());
    }

    @Test
    public void trimTrailingWs() {
        assertBoolean(PropertyType.trim_trailing_whitespace);
    }

}
