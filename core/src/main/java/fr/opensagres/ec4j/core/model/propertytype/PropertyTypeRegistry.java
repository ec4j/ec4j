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
package fr.opensagres.ec4j.core.model.propertytype;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.opensagres.ec4j.core.model.propertytype.PropertyType.Charset;
import fr.opensagres.ec4j.core.model.propertytype.PropertyType.EndOfLine;
import fr.opensagres.ec4j.core.model.propertytype.PropertyType.IndentSize;
import fr.opensagres.ec4j.core.model.propertytype.PropertyType.IndentStyle;
import fr.opensagres.ec4j.core.model.propertytype.PropertyType.InsertFinalNewline;
import fr.opensagres.ec4j.core.model.propertytype.PropertyType.Root;
import fr.opensagres.ec4j.core.model.propertytype.PropertyType.TabWidth;
import fr.opensagres.ec4j.core.model.propertytype.PropertyType.TrimTrailingWhitespace;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class PropertyTypeRegistry {

    private static final PropertyTypeRegistry DEFAULT;

    static {
        DEFAULT = new PropertyTypeRegistry();
        DEFAULT.register(new IndentStyle());
        DEFAULT.register(new IndentSize());
        DEFAULT.register(new TabWidth());
        DEFAULT.register(new EndOfLine());
        DEFAULT.register(new Charset());
        DEFAULT.register(new TrimTrailingWhitespace());
        DEFAULT.register(new InsertFinalNewline());
        DEFAULT.register(new Root());
    }

    public static final PropertyTypeRegistry getDefault() {
        return DEFAULT;
    }

    private Map<String, PropertyType<?>> types;

    public PropertyTypeRegistry() {
        this.types = new HashMap<>();
    }

    public void register(PropertyType<?> type) {
        types.put(type.getName().toUpperCase(), type);
    }

    public PropertyType<?> getType(String name) {
        return types.get(name.toUpperCase());
    }

    public Collection<PropertyType<?>> getTypes() {
        return types.values();
    }

}
