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
package fr.opensagres.ec4j.core.parser;

/**
 * A handler that gets notified about parse events from
 * {@link EditorConfigParser#parse(fr.opensagres.ec4j.core.Resources.Resource, EditorConfigHandler)}.
 * <p>
 * Implementations will generally want to keep some internal state and therefore their instances should not be accessed
 * from concurrent threads.
 * <p>
 * The implementations should still allow the use case when the same {@link EditorConfigHandler} instance is used for
 * parsing multiple {@code .editorconfig} files sequentially one after another.
 * <p>
 * All event notifications methods have {@link ParseContext} parameter which can be used to query the current
 * {@link Location} in the parsed file or for getting the parsed file via {@link ParseContext#getResource()}.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public interface EditorConfigHandler {

    /**
     * Start of a document event
     *
     * @param context
     *            the {@link ParseContext}
     */
    void startDocument(ParseContext context);

    /**
     * End of a document event
     *
     * @param context
     *            the {@link ParseContext}
     */
    void endDocument(ParseContext context);

    /**
     * Start of a section
     *
     * @param context
     *            the {@link ParseContext}
     */
    void startSection(ParseContext context);

    /**
     * End of a section
     *
     * @param context
     *            the {@link ParseContext}
     */
    void endSection(ParseContext context);

    /**
     * Start of a glob pattern
     *
     * @param context
     *            the {@link ParseContext}
     */
    void startPattern(ParseContext context);

    /**
     * End of a glob pattern
     *
     * @param context
     *            the {@link ParseContext}
     */
    void endPattern(ParseContext context, String pattern);

    /**
     * Start of a property
     *
     * @param context
     *            the {@link ParseContext}
     */
    void startProperty(ParseContext context);

    /**
     * End of a property
     *
     * @param context
     *            the {@link ParseContext}
     */
    void endProperty(ParseContext context);

    /**
     * Start of a property name
     *
     * @param context
     *            the {@link ParseContext}
     */
    void startPropertyName(ParseContext context);

    /**
     * End of a property name
     *
     * @param context
     *            the {@link ParseContext}
     * @param name the name
     */
    void endPropertyName(ParseContext context, String name);

    /**
     * Start of a property value
     *
     * @param context
     *            the {@link ParseContext}
     */
    void startPropertyValue(ParseContext context);

    /**
     * Start of a property name
     *
     * @param context
     *            the {@link ParseContext}
     * @param value the value
     */
    void endPropertyValue(ParseContext context, String value);

    /**
     * A {@link ParseException} occured
     *
     * @param e the error
     */
    void error(ParseException e);

}