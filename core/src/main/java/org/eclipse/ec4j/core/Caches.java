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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ec4j.core.Resources.Resource;
import org.eclipse.ec4j.core.model.EditorConfig;

/**
 * An aggregate class of the {@link Cache} interface and some of its basic implementations.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class Caches {

    /**
     * A simple cache interface for {@link EditorConfig}s loaded out of {@link Resource}s.
     * <p>
     * It is up to the implementors to choose a particular caching strategy. Note that there is few {@link Cache}
     * implementations accessible through the factory methods of {@link Caches} class.
     * <p>
     * The implementations should state clearly if their instances can be accessed from concurrent threads.
     */
    public interface Cache {
        EditorConfig get(Resource editorConfigFile, EditorConfigLoader loader) throws IOException;
    }

    /**
     * A simple {@link HashMap} based {@link Cache} whose entries are kept forver unless {@link #clear()} is called.
     * <p>
     * Instances of this class cannot be acceseed from concurrent threads safely.
     */
    public static class PermanentCache implements Cache {
        private final Map<Resource, EditorConfig> entries = new HashMap<>();

        /**
         * Removes all entries from this {@link Cache}.
         */
        public void clear() {
            entries.clear();
        }

        @Override
        public EditorConfig get(Resource editorConfigFile, EditorConfigLoader loader) throws IOException {
            EditorConfig result = entries.get(editorConfigFile);
            if (result == null) {
                result = loader.load(editorConfigFile);
                entries.put(editorConfigFile, result);
            }
            return result;
        }

    }

    /** {@link #NO_CACHE} keeps no state, we can thus have a singleton */
    private static final Cache NO_CACHE = new Cache() {
        @Override
        public EditorConfig get(Resource editorConfigFile, EditorConfigLoader loader) throws IOException {
            return loader.load(editorConfigFile);
        }
    };

    /**
     * Returns a new dummy {@link Cache} instance that does not cache at all. Instead, it calls
     * {@link EditorConfigLoader#load(Resource)} on each invocation of {@link Cache#get(Resource, EditorConfigLoader)}.
     * Because it keeps no state, the instance can be safely accessed from concurrent threads.
     *
     * @return a new dummy {@link Cache} instance
     */
    public static Cache none() {
        return NO_CACHE;
    }

    /**
     * @return a new {@link PermanentCache}
     */
    public static Cache permanent() {
        return new PermanentCache();
    }

    private Caches() {
    }

}
