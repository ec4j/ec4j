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
import org.eclipse.ec4j.core.model.PropertyType.PropertyValue;
import org.junit.Assert;
import org.junit.Test;

public class PropertyTypeTest {

    private static void assertBoolean(PropertyType<Boolean> type) {
        Assert.assertEquals(PropertyValue.valid("true", Boolean.TRUE), type.parse("true"));
        Assert.assertEquals(PropertyValue.valid("True", Boolean.TRUE), type.parse("True"));
        Assert.assertEquals(PropertyValue.valid("TRUE", Boolean.TRUE), type.parse("TRUE"));
        Assert.assertEquals(PropertyValue.valid("tRuE", Boolean.TRUE), type.parse("tRuE"));
        Assert.assertEquals(PropertyValue.valid("false", Boolean.FALSE), type.parse("false"));
        Assert.assertFalse(type.parse("").isValid());
        Assert.assertFalse(type.parse("foo").isValid());
        Assert.assertFalse(type.parse(null).isValid());
    }

    @Test
    public void charset() {
        Assert.assertEquals(PropertyValue.valid("utf-8", "utf-8"), PropertyType.charset.parse("utf-8"));
        Assert.assertEquals(PropertyValue.valid("UTF-8", "UTF-8"), PropertyType.charset.parse("UTF-8"));
        Assert.assertTrue(PropertyType.charset.parse("").isValid());
        Assert.assertTrue(PropertyType.charset.parse("foo").isValid());
        Assert.assertTrue(PropertyType.charset.parse(null).isValid());
    }

    @Test
    public void eol() {
        Assert.assertEquals(PropertyValue.valid("cr", EndOfLineValue.cr), PropertyType.end_of_line.parse("cr"));
        Assert.assertEquals(PropertyValue.valid("CR", EndOfLineValue.cr), PropertyType.end_of_line.parse("CR"));
        Assert.assertEquals(PropertyValue.valid("Cr", EndOfLineValue.cr), PropertyType.end_of_line.parse("Cr"));
        Assert.assertEquals(PropertyValue.valid("CrLf", EndOfLineValue.crlf), PropertyType.end_of_line.parse("CrLf"));
        Assert.assertEquals(PropertyValue.valid("lf", EndOfLineValue.lf), PropertyType.end_of_line.parse("lf"));
        Assert.assertFalse(PropertyType.end_of_line.parse("").isValid());
        Assert.assertFalse(PropertyType.end_of_line.parse("foo").isValid());
        Assert.assertFalse(PropertyType.end_of_line.parse(null).isValid());
    }

    @Test
    public void indentSize() {
        Assert.assertEquals(PropertyValue.valid("1", 1), PropertyType.indent_size.parse("1"));
        Assert.assertEquals(PropertyValue.valid("tab", null), PropertyType.indent_size.parse("tab"));
        Assert.assertFalse(PropertyType.indent_size.parse("").isValid());
        Assert.assertFalse(PropertyType.indent_size.parse("0").isValid());
        Assert.assertFalse(PropertyType.indent_size.parse("-1").isValid());
        Assert.assertTrue(PropertyType.indent_size.parse("tab").isValid());
        Assert.assertFalse(PropertyType.indent_size.parse(null).isValid());
    }

    @Test
    public void indentStyle() {
        Assert.assertEquals(PropertyValue.valid("space", IndentStyleValue.space),
                PropertyType.indent_style.parse("space"));
        Assert.assertEquals(PropertyValue.valid("SPACE", IndentStyleValue.space),
                PropertyType.indent_style.parse("SPACE"));
        Assert.assertEquals(PropertyValue.valid("SpAcE", IndentStyleValue.space),
                PropertyType.indent_style.parse("SpAcE"));
        Assert.assertEquals(PropertyValue.valid("tab", IndentStyleValue.tab), PropertyType.indent_style.parse("tab"));
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
        Assert.assertEquals(PropertyValue.valid("1", 1), PropertyType.tab_width.parse("1"));
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
