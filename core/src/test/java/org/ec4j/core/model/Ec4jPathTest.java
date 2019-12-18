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
package org.ec4j.core.model;

import org.ec4j.core.model.Ec4jPath.Ec4jPaths;
import org.junit.Assert;
import org.junit.Test;

public class Ec4jPathTest {

    @Test
    public void absoluteL1() {
        final Ec4jPath p = Ec4jPaths.of("/dir1");
        Assert.assertTrue(p.isAbsolute());
        Assert.assertEquals("/", p.getParentPath().toString());
        Assert.assertEquals(Ec4jPaths.root(), p.getParentPath());
        Assert.assertEquals("dir1", Ec4jPaths.root().relativize(p).toString());

        Ec4jPath child = p.resolve("dir2");
        Assert.assertEquals("/dir1/dir2", child.toString());

    }

    @Test
    public void absoluteL2() {
        final Ec4jPath p = Ec4jPaths.of("/dir1/dir2");
        Assert.assertTrue(p.isAbsolute());
        Ec4jPath parent = p.getParentPath();
        Assert.assertEquals("/dir1", parent.toString());
        Assert.assertEquals(Ec4jPaths.of("/dir1"), parent);
        Assert.assertEquals("dir2", parent.relativize(p).toString());

        Ec4jPath child = p.resolve("dir3");
        Assert.assertEquals("/dir1/dir2/dir3", child.toString());

        try {
            p.resolve("/abs");
            Assert.fail(IllegalArgumentException.class.getSimpleName() + " expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void ofString() {
        try {
            Ec4jPaths.of("path/");
            Assert.fail(IllegalArgumentException.class.getSimpleName() + " expected");
        } catch (IllegalArgumentException expected) {
        }
        try {
            Ec4jPaths.of("//");
            Assert.fail(IllegalArgumentException.class.getSimpleName() + " expected");
        } catch (IllegalArgumentException expected) {
        }
        try {
            Ec4jPaths.of("");
            Assert.fail(IllegalArgumentException.class.getSimpleName() + " expected");
        } catch (IllegalArgumentException expected) {
        }
        try {
            Ec4jPaths.of((String) null);
            Assert.fail(IllegalArgumentException.class.getSimpleName() + " expected");
        } catch (IllegalArgumentException expected) {
        }

    }

    @Test
    public void relativeL1() {
        final Ec4jPath p = Ec4jPaths.of("dir1");
        Assert.assertFalse(p.isAbsolute());
        Assert.assertNull(p.getParentPath());

        try {
            Ec4jPaths.root().relativize(p);
            Assert.fail(IllegalArgumentException.class.getSimpleName() + " expected");
        } catch (IllegalArgumentException expected) {
        }

        Ec4jPath child = p.resolve("dir2");
        Assert.assertEquals("dir1/dir2", child.toString());

    }

    @Test
    public void relativeL2() {
        final Ec4jPath p = Ec4jPaths.of("dir1/dir2");
        Assert.assertFalse(p.isAbsolute());
        Ec4jPath parent = p.getParentPath();
        Assert.assertEquals("dir1", parent.toString());
        Assert.assertEquals(Ec4jPaths.of("dir1"), parent);
        Assert.assertEquals("dir2", parent.relativize(p).toString());

        try {
            Ec4jPaths.root().relativize(p);
            Assert.fail(IllegalArgumentException.class.getSimpleName() + " expected");
        } catch (IllegalArgumentException expected) {
        }

        Ec4jPath child = p.resolve("dir3");
        Assert.assertEquals("dir1/dir2/dir3", child.toString());

    }

    @Test
    public void root() {
        final Ec4jPath p = Ec4jPaths.root();
        Assert.assertTrue(p.isAbsolute());
        Assert.assertTrue(p.getParentPath() == null);
        Ec4jPath child = p.resolve("dir1");
        Assert.assertEquals("/dir1", child.toString());
    }

}
