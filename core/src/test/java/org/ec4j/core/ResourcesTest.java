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

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.ec4j.core.Resource.Resources;
import org.ec4j.core.Resource.Resources.StringResourceTree;
import org.junit.Assert;
import org.junit.Test;

public class ResourcesTest {

    @Test
    public void ofClassPath() {
        Resource r = Resources.ofClassPath(getClass().getClassLoader(), "/location-aware/.editorconfig",
                StandardCharsets.UTF_8);
        Assert.assertNotNull(r);
        Assert.assertEquals("/location-aware/.editorconfig", r.getPath().toString());
        Assert.assertTrue(r.exists());

        ResourcePath parent = r.getParent();
        Assert.assertNotNull(parent);
        Assert.assertEquals("/location-aware", parent.getPath().toString());
        Assert.assertTrue(parent.hasParent());

        Resource r2 = parent.resolve(".editorconfig");
        Assert.assertNotNull(r2);
        Assert.assertEquals("/location-aware/.editorconfig", r2.getPath().toString());
        Assert.assertTrue(r2.exists());
        Assert.assertEquals(r, r2);

        ResourcePath root = parent.getParent();
        Assert.assertNotNull(root);
        Assert.assertEquals("/", root.getPath().toString());
        Assert.assertFalse(root.hasParent());

    }

    @Test
    public void ofString() {
        final String testFile = "/Bar/foo.txt";
        StringResourceTree tree = StringResourceTree.builder() //
                .resource("/.editorconfig", "")//
                .touch(testFile) //
                .build();

        Resource r = tree.getResource(testFile);

        Assert.assertTrue(r.exists());
        ResourcePath bar = r.getParent();
        Assert.assertEquals("/Bar", bar.getPath().toString());

        Resource barEditorconfig = bar.resolve(".editorconfig");
        Assert.assertFalse(barEditorconfig.exists());

        ResourcePath root = bar.getParent();
        Assert.assertNotNull(root);
        Resource rootEditorconfig = root.resolve(".editorconfig");
        Assert.assertNotNull(rootEditorconfig);
        Assert.assertTrue(rootEditorconfig.exists());
    }

    @Test
    public void ofPath() {
        Resource r = Resources.ofPath(Paths.get("src/test/resources/location-aware/.editorconfig"),
                StandardCharsets.UTF_8);
        Assert.assertNotNull(r);
        Assert.assertEquals("src/test/resources/location-aware/.editorconfig", r.getPath().toString());
        Assert.assertTrue(r.exists());

        ResourcePath parent = r.getParent();
        Assert.assertNotNull(parent);
        Assert.assertEquals("src/test/resources/location-aware", parent.getPath().toString());
        Assert.assertTrue(parent.hasParent());

        Resource r2 = parent.resolve(".editorconfig");
        Assert.assertNotNull(r2);
        Assert.assertEquals("src/test/resources/location-aware/.editorconfig", r2.getPath().toString());
        Assert.assertTrue(r2.exists());
        Assert.assertEquals(r, r2);

        ResourcePath root = parent.getParent();
        Assert.assertNotNull(root);
        Assert.assertEquals("src/test/resources", root.getPath().toString());
        Assert.assertTrue(root.hasParent());

        ResourcePath resources = root.getParent();
        Assert.assertEquals("src/test", resources.getPath().toString());
        ResourcePath src = resources.getParent();
        Assert.assertEquals("src", src.getPath().toString());
        Assert.assertFalse(src.hasParent());
        ResourcePath srcParent = src.getParent();
        Assert.assertNull(srcParent);
    }
}
