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

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.eclipse.ec4j.core.ResourcePaths.ResourcePath;
import org.eclipse.ec4j.core.Resources.Resource;
import org.junit.Assert;
import org.junit.Test;

public class ResourcesTest {

    @Test
    public void ofClassPath() {
        Resource r = Resources.ofClassPath(getClass().getClassLoader(), "/location-aware/.editorconfig", StandardCharsets.UTF_8);
        Assert.assertNotNull(r);
        Assert.assertEquals("/location-aware/.editorconfig", r.getPath());
        Assert.assertTrue(r.exists());

        ResourcePath parent = r.getParent();
        Assert.assertNotNull(parent);
        Assert.assertEquals("/location-aware", parent.getPath());
        Assert.assertTrue(parent.hasParent());

        Resource r2 = parent.resolve(".editorconfig");
        Assert.assertNotNull(r2);
        Assert.assertEquals("/location-aware/.editorconfig", r2.getPath());
        Assert.assertTrue(r2.exists());
        Assert.assertEquals(r, r2);

        ResourcePath root = parent.getParent();
        Assert.assertNotNull(root);
        Assert.assertEquals("/", root.getPath());
        Assert.assertFalse(root.hasParent());

    }

    @Test
    public void ofPath() {
        Resource r = Resources.ofPath(Paths.get("src/test/resources/location-aware/.editorconfig"), StandardCharsets.UTF_8);
        Assert.assertNotNull(r);
        Assert.assertEquals("src/test/resources/location-aware/.editorconfig", r.getPath());
        Assert.assertTrue(r.exists());

        ResourcePath parent = r.getParent();
        Assert.assertNotNull(parent);
        Assert.assertEquals("src/test/resources/location-aware", parent.getPath());
        Assert.assertTrue(parent.hasParent());

        Resource r2 = parent.resolve(".editorconfig");
        Assert.assertNotNull(r2);
        Assert.assertEquals("src/test/resources/location-aware/.editorconfig", r2.getPath());
        Assert.assertTrue(r2.exists());
        Assert.assertEquals(r, r2);

        ResourcePath root = parent.getParent();
        Assert.assertNotNull(root);
        Assert.assertEquals("src/test/resources", root.getPath());
        Assert.assertTrue(root.hasParent());

    }
}
