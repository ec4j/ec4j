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
package org.eclipse.ec4j.core.parser.handlers;

import org.eclipse.ec4j.core.parser.EditorConfigParser;
import org.eclipse.ec4j.core.parser.Location;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public abstract class AbstractEditorConfigHandler<S, O> implements IEditorConfigHandler<S, O> {

    private EditorConfigParser<S, O> parser;

    @Override
    public void setParser(EditorConfigParser<S, O> parser) {
        this.parser = parser;
    }

    /**
     * Returns the current parser location.
     *
     * @return the current parser location
     */
    protected Location getLocation() {
        return parser.getLocation();
    }

}
