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

import org.junit.Assert;
import org.junit.Test;

public class GlobTest {

    @Test
    public void braces_alpha_range1() {
        Glob glob = new Glob("/dir1", "{aardvark..antelope}");
        Assert.assertTrue(glob.match("/dir/{aardvark..antelope}"));
    }

}
