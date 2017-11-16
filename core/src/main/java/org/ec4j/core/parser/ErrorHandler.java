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
package org.ec4j.core.parser;

/**
 * A handler that gets notified on {@link ParseException}s by {@link EditorConfigParser}. Note that the basic
 * {@link #THROWING} and {@link #IGNORING} implementations are available in this class.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public interface ErrorHandler {

    /** An {@link ErrorHandler} that does nothing in {@link #error(ParseContext, ParseException)} */
    ErrorHandler IGNORING = new ErrorHandler() {
        @Override
        public void error(ParseContext context, ParseException e) throws ParseException {
        }
    };

    /**
     * An {@link ErrorHandler} that throws every {@link ParseException} it gets via
     * {@link #error(ParseContext, ParseException)}
     */
    ErrorHandler THROWING = new ErrorHandler() {
        @Override
        public void error(ParseContext context, ParseException e) throws ParseException {
            throw e;
        }
    };

    /**
     * An {@link ErrorHandler} that throws only those {@link ParseException}s whose
     * {@link ParseException#isSyntaxError()} returns {@code true}
     */
    ErrorHandler THROW_SYNTAX_ERRORS_IGNORE_OTHERS = new ErrorHandler() {
        @Override
        public void error(ParseContext context, ParseException e) throws ParseException {
            if (e.isSyntaxError()) {
                throw e;
            }
        }
    };

    /**
     * A {@link ParseException} occured
     *
     * @param context
     *            the {@link ParseContext}
     * @param e
     *            the error
     */
    void error(ParseContext context, ParseException e) throws ParseException;

}
