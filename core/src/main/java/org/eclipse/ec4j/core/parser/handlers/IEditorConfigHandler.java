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
import org.eclipse.ec4j.core.parser.ParseException;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public interface IEditorConfigHandler<S, O> {

    void setParser(EditorConfigParser<S, O> parser);

    void startDocument();

    void endDocument();

    S startSection();

    void endSection(S section);

    void startPattern(S section);

    void endPattern(S section, String pattern);

    void startOption();

    void startOptionName();

    O endOptionName(String name);

    void endOption(O option, S section);

    void startOptionValue(O option, String name);

    void endOptionValue(O option, String value, String name);

    void error(ParseException e);

}