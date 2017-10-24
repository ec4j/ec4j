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

/**
 * Content provider API.
 *
 * @param <T>
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public interface ContentProvider<T> {

    ContentProvider<String> STRING_CONTENT_PROVIDER = new ContentProvider<String>() {

        @Override
        public char getChar(String document, int index) throws Exception {
            return document.charAt(index);
        }

        @Override
        public int getLength(String document) {
            return document.length();
        }

    };

    /**
     * Returns the character at the given document offset in this document.
     *
     * @param document
     *            the document
     * @param offset
     *            a document offset
     * @return the character at the offset
     * @exception if
     *                the offset is invalid in this document for instance
     */
    char getChar(T document, int offset) throws Exception;

    int getLength(T document);

}