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
package org.eclipse.ec4j.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.eclipse.ec4j.core.ContentProvider;
import org.eclipse.ec4j.core.model.Option;
import org.eclipse.ec4j.core.model.Section;
import org.eclipse.ec4j.core.model.optiontypes.OptionType;
import org.eclipse.ec4j.core.model.optiontypes.OptionTypeRegistry;
import org.eclipse.ec4j.core.parser.EditorConfigParser;
import org.eclipse.ec4j.services.completion.CompletionContextType;
import org.eclipse.ec4j.services.completion.CompletionEntry;
import org.eclipse.ec4j.services.completion.ICompletionEntry;
import org.eclipse.ec4j.services.completion.ICompletionEntryMatcher;
import org.eclipse.ec4j.services.validation.IReporter;
import org.eclipse.ec4j.services.validation.ISeverityProvider;
import org.eclipse.ec4j.services.validation.ValidationEditorConfigHandler;

/**
 * EditorConfig service helpful for IDE:
 *
 * <ul>
 * <li>validation</li>
 * <li>completion</li>
 * </ul>
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class EditorConfigService {

    // ------------- Validation service

    public static void validate(String content, IReporter reporter) {
        validate(content, reporter, null, null);
    }

    /**
     * Validate the given content of an .editorconfig and report errors in the given
     * reporter. This validator is able to validate:
     *
     * <ul>
     * <li>Syntax error like section which are not closed.</li>
     * <li>Semantic error like :
     * <ul>
     * <li>check option name is an EditorConfig properties
     * {@link https://github.com/editorconfig/editorconfig/wiki/EditorConfig-Properties}
     * </li>
     * <li>check option value according the option name.</li>
     * </ul>
     * </li>
     * </ul>
     *
     * @param content
     *            of the .editorconfig to validate
     * @param reporter
     *            used to report errors.
     */
    public static void validate(String content, IReporter reporter, ISeverityProvider provider,
            OptionTypeRegistry registry) {
        ValidationEditorConfigHandler handler = new ValidationEditorConfigHandler(reporter, provider, registry);
        // Set parser as tolerant to collect the full errors of each line of the
        // editorconfig.
        new EditorConfigParser<Section, Option>(handler).setTolerant(true).parse(content);
    }

    // ------------- Completion service

    public static final Function<String, ICompletionEntry> COMPLETION_ENTRY_FACTORY = new Function<String, ICompletionEntry>() {
        @Override
        public CompletionEntry apply(String name) {
            return new CompletionEntry(name);
        }
    };

    public static List<ICompletionEntry> getCompletionEntries(int offset, String document,
            ICompletionEntryMatcher matcher) throws Exception {
        return getCompletionEntries(offset, document, matcher, COMPLETION_ENTRY_FACTORY,
                ContentProvider.STRING_CONTENT_PROVIDER);
    }

    public static <T, C extends ICompletionEntry> List<C> getCompletionEntries(int offset, T document,
            ICompletionEntryMatcher matcher, final Function<String, C> factory, ContentProvider<T> provider)
            throws Exception {
        return getCompletionEntries(offset, document, matcher, factory, provider, null);
    }

    public static <T, C extends ICompletionEntry> List<C> getCompletionEntries(int offset, T document,
            ICompletionEntryMatcher matcher, final Function<String, C> factory, ContentProvider<T> provider,
            OptionTypeRegistry registry) throws Exception {
        if (registry == null) {
            registry = OptionTypeRegistry.DEFAULT;
        }
        TokenContext context = getTokenContext(offset, document, false, provider);
        switch (context.type) {
        case OPTION_NAME: {
            C entry = null;
            List<C> entries = new ArrayList<>();
            for (OptionType<?> type : registry.getTypes()) {
                entry = factory.apply(type.getName());
                entry.setMatcher(matcher);
                entry.setOptionType(type);
                entry.setContextType(context.type);
                entry.setInitialOffset(offset);
                if (entry.updatePrefix(context.prefix)) {
                    entries.add(entry);
                }
            }
            return entries;
        }
        case OPTION_VALUE: {
            OptionType<?> optionType = registry.getType(context.name);
            if (optionType != null) {
                String[] values = optionType.getPossibleValues();
                if (values != null) {
                    C entry = null;
                    List<C> entries = new ArrayList<>();
                    for (String value : values) {
                        entry = factory.apply(value);
                        entry.setMatcher(matcher);
                        entry.setOptionType(optionType);
                        entry.setContextType(context.type);
                        entry.setInitialOffset(offset);
                        if (entry.updatePrefix(context.prefix)) {
                            entries.add(entry);
                        }
                    }
                    return entries;
                }
            }
        }
            break;
        default:
            break;
        }
        return Collections.emptyList();
    }

    // ------------- Hover service

    public static <T> String getHover(int offset, T document, ContentProvider<T> provider) throws Exception {
        return getHover(offset, document, provider, null);
    }

    public static <T> String getHover(int offset, T document, ContentProvider<T> provider, OptionTypeRegistry registry)
            throws Exception {
        if (registry == null) {
            registry = OptionTypeRegistry.DEFAULT;
        }
        TokenContext context = getTokenContext(offset, document, true, provider);
        switch (context.type) {
        case OPTION_NAME: {
            OptionType<?> type = registry.getType(context.prefix);
            return type != null ? type.getDescription() : null;
        }
        case OPTION_VALUE: {
            OptionType<?> type = registry.getType(context.name);
            return type != null ? type.getDescription() : null;
        }
        default:
            return null;
        }
    }

    private static class TokenContext {
        public final String prefix;
        public final String name; // option name, only available when context type is an option value
        public final CompletionContextType type;

        private TokenContext(String prefix, String name, CompletionContextType type) {
            this.prefix = prefix;
            this.name = name;
            this.type = type;
        }
    }

    private static <T> TokenContext getTokenContext(int offset, T document, boolean collectWord,
            ContentProvider<T> provider) throws Exception {
        char c;
        CompletionContextType type = CompletionContextType.OPTION_NAME;
        StringBuilder prefix = new StringBuilder();
        StringBuilder name = null;
        int i = offset - 1;
        // Collect prefix
        while (i >= 0) {
            c = provider.getChar(document, i);
            if (Character.isJavaIdentifierPart(c)) {
                prefix.insert(0, c);
                i--;
            } else {
                break;
            }
        }
        if (collectWord) {
            int j = offset;
            int length = provider.getLength(document);
            while (j <= length) {
                c = provider.getChar(document, j);
                if (Character.isJavaIdentifierPart(c)) {
                    prefix.append(c);
                    j++;
                } else {
                    break;
                }
            }
        }

        // Collect context type
        boolean stop = false;
        while (i >= 0 && !stop) {
            c = provider.getChar(document, i--);
            switch (c) {
            case '[':
                type = CompletionContextType.SECTION;
                stop = true;
                break;
            case '#':
                type = CompletionContextType.COMMENTS;
                stop = true;
                break;
            case ' ':
            case '\t':
                continue;
            case '\r':
            case '\n':
                stop = true;
                break;
            case '=':
                name = new StringBuilder();
                type = CompletionContextType.OPTION_VALUE;
                break;
            default:
                if (name != null && Character.isJavaIdentifierPart(c)) {
                    name.insert(0, c);
                }
            }
        }
        return new TokenContext(prefix.toString(), name != null ? name.toString() : null, type);
    }

    public static String getEndOfLine(String lineDelimiter) {
        if ("\n".equals(lineDelimiter)) {
            return "lf";
        } else if ("\r".equals(lineDelimiter)) {
            return "cr";
        } else if ("\r\n".equals(lineDelimiter)) {
            return "crlf";
        }
        return null;
    }
}
