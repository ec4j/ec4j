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
package org.eclipse.ec4j.core.model.optiontypes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ec4j.core.model.optiontypes.OptionType.Charset;
import org.eclipse.ec4j.core.model.optiontypes.OptionType.EndOfLine;
import org.eclipse.ec4j.core.model.optiontypes.OptionType.IndentSize;
import org.eclipse.ec4j.core.model.optiontypes.OptionType.IndentStyle;
import org.eclipse.ec4j.core.model.optiontypes.OptionType.InsertFinalNewline;
import org.eclipse.ec4j.core.model.optiontypes.OptionType.Root;
import org.eclipse.ec4j.core.model.optiontypes.OptionType.TabWidth;
import org.eclipse.ec4j.core.model.optiontypes.OptionType.TrimTrailingWhitespace;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class OptionTypeRegistry {

    public static final OptionTypeRegistry DEFAULT;

    static {
        DEFAULT = new OptionTypeRegistry();
        DEFAULT.register(new IndentStyle());
        DEFAULT.register(new IndentSize());
        DEFAULT.register(new TabWidth());
        DEFAULT.register(new EndOfLine());
        DEFAULT.register(new Charset());
        DEFAULT.register(new TrimTrailingWhitespace());
        DEFAULT.register(new InsertFinalNewline());
        DEFAULT.register(new Root());
    }

    private Map<String, OptionType<?>> types;

    public OptionTypeRegistry() {
        this.types = new HashMap<>();
    }

    public void register(OptionType<?> type) {
        types.put(type.getName().toUpperCase(), type);
    }

    public OptionType<?> getType(String name) {
        return types.get(name.toUpperCase());
    }

    public Collection<OptionType<?>> getTypes() {
        return types.values();
    }

}
