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
package org.ec4j.core.services;

import org.ec4j.core.services.completion.TokenContextType;

public class TokenContext {
    final String prefix;
    private final String name; // property name, only available when context type is an property value
    private final TokenContextType type;

    TokenContext(String prefix, String name, TokenContextType type) {
        this.prefix = prefix;
        this.name = name;
        this.type = type;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getName() {
        return name;
    }

    public TokenContextType getType() {
        return type;
    }
}