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

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class VersionTest {

    @Test
    public void ofQ() {
        Version actual = Version.of("1.2.3-q");
        Assert.assertEquals(new Version((byte) 1, (byte) 2, (byte) 3, "q"), actual);
        Assert.assertEquals(1, actual.getMajor());
        Assert.assertEquals(2, actual.getMinor());
        Assert.assertEquals(3, actual.getMicro());
        Assert.assertEquals("q", actual.getQualifier());
    }

    @Test
    public void of() {
        Version actual = Version.of("1.2.3");
        Assert.assertEquals(new Version((byte) 1, (byte) 2, (byte) 3, null), actual);
        Assert.assertEquals(1, actual.getMajor());
        Assert.assertEquals(2, actual.getMinor());
        Assert.assertEquals(3, actual.getMicro());
        Assert.assertNull(actual.getQualifier());
    }

    @Test
    public void compareTo() {
        Version[] versions = new Version[] { Version.of("4.2.3-q"), Version.of("2.2.3-q"), Version.of("2.2.2-q"),
                Version.of("2.2.2-q"), Version.of("2.1.2-q"), Version.of("2.1.1-q"), Version.of("0.0.0-b"),
                Version.of("0.0.0-a"), Version.of("0.0.0") };
        Arrays.sort(versions);

        Assert.assertArrayEquals(new Version[] { Version.of("0.0.0"), Version.of("0.0.0-a"), Version.of("0.0.0-b"),
                Version.of("2.1.1-q"), Version.of("2.1.2-q"), Version.of("2.2.2-q"), Version.of("2.2.2-q"),
                Version.of("2.2.3-q"), Version.of("4.2.3-q") }, versions);
    }

}
