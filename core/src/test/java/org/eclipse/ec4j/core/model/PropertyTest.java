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
import org.junit.Assert;
import org.junit.Test;

public class PropertyTest {

    @Test
    public void invalid() {
        Property prop = new Property.Builder(null).type(PropertyType.end_of_line).value("foo").build();
        Assert.assertFalse(prop.isValid());
        try {
            prop.getValueAs();
            Assert.fail(RuntimeException.class.getName() + " expected");
        } catch (RuntimeException expected) {
        }
    }

    @Test
    public void valid() {
        Property prop = new Property.Builder(null).type(PropertyType.end_of_line).value("cr").build();
        Assert.assertTrue(prop.isValid());
        Assert.assertEquals(EndOfLineValue.cr, prop.getValueAs());
    }
}
