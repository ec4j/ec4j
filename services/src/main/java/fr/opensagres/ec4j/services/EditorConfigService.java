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
package fr.opensagres.ec4j.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.opensagres.ec4j.core.EditorConfigConstants;
import fr.opensagres.ec4j.core.Resources;
import fr.opensagres.ec4j.core.Resources.RandomReader;
import fr.opensagres.ec4j.core.model.propertytype.PropertyType;
import fr.opensagres.ec4j.core.model.propertytype.PropertyTypeRegistry;
import fr.opensagres.ec4j.core.parser.EditorConfigParser;
import fr.opensagres.ec4j.services.completion.CompletionContextType;
import fr.opensagres.ec4j.services.completion.CompletionEntry;
import fr.opensagres.ec4j.services.completion.ICompletionEntry;
import fr.opensagres.ec4j.services.completion.ICompletionEntryMatcher;
import fr.opensagres.ec4j.services.validation.IReporter;
import fr.opensagres.ec4j.services.validation.ISeverityProvider;
import fr.opensagres.ec4j.services.validation.ValidationEditorConfigHandler;

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

    public interface CompletionEntryFactory {
        ICompletionEntry newEntry(String name);
    }

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
     * <li>check property name is an EditorConfig properties
     * {@link https://github.com/editorconfig/editorconfig/wiki/EditorConfig-Properties}
     * </li>
     * <li>check property value according the property name.</li>
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
            PropertyTypeRegistry registry) {
        ValidationEditorConfigHandler handler = new ValidationEditorConfigHandler(reporter, provider, registry);
        // Set parser as tolerant to collect the full errors of each line of the
        // editorconfig.
        EditorConfigParser parser = EditorConfigParser.builder().tolerant().build();
        try {
            parser.parse(Resources.ofString(EditorConfigConstants.EDITORCONFIG, content), handler);
        } catch (IOException e) {
            /* should not happen with Resources.ofString(content) */
            throw new RuntimeException(e);
        }
    }

    // ------------- Completion service

    public static final CompletionEntryFactory COMPLETION_ENTRY_FACTORY = new CompletionEntryFactory() {
        @Override
        public CompletionEntry newEntry(String name) {
            return new CompletionEntry(name);
        }
    };

    public static List<ICompletionEntry> getCompletionEntries(int offset, RandomReader reader,
            ICompletionEntryMatcher matcher) throws Exception {
        return getCompletionEntries(offset, reader, matcher, COMPLETION_ENTRY_FACTORY);
    }

    public static List<ICompletionEntry> getCompletionEntries(int offset, RandomReader reader,
            ICompletionEntryMatcher matcher, final CompletionEntryFactory factory)
            throws Exception {
        return getCompletionEntries(offset, reader, matcher, factory, null);
    }

    public static List<ICompletionEntry> getCompletionEntries(int offset, RandomReader reader,
            ICompletionEntryMatcher matcher, final CompletionEntryFactory factory,
            PropertyTypeRegistry registry) throws Exception {
        if (registry == null) {
            registry = PropertyTypeRegistry.getDefault();
        }
        TokenContext context = getTokenContext(offset, reader, false);
        switch (context.type) {
        case PROPERTY_NAME: {
            ICompletionEntry entry = null;
            List<ICompletionEntry> entries = new ArrayList<>();
            for (PropertyType<?> type : registry.getTypes()) {
                entry = factory.newEntry(type.getName());
                entry.setMatcher(matcher);
                entry.setPropertyType(type);
                entry.setContextType(context.type);
                entry.setInitialOffset(offset);
                if (entry.updatePrefix(context.prefix)) {
                    entries.add(entry);
                }
            }
            return entries;
        }
        case PROPERTY_VALUE: {
            PropertyType<?> propertyType = registry.getType(context.name);
            if (propertyType != null) {
                String[] values = propertyType.getPossibleValues();
                if (values != null) {
                    ICompletionEntry entry = null;
                    List<ICompletionEntry> entries = new ArrayList<>();
                    for (String value : values) {
                        entry = factory.newEntry(value);
                        entry.setMatcher(matcher);
                        entry.setPropertyType(propertyType);
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

    public static <T> String getHover(int offset, RandomReader reader) throws Exception {
        return getHover(offset, reader, null);
    }

    public static <T> String getHover(int offset, RandomReader reader, PropertyTypeRegistry registry)
            throws Exception {
        if (registry == null) {
            registry = PropertyTypeRegistry.getDefault();
        }
        TokenContext context = getTokenContext(offset, reader, true);
        switch (context.type) {
        case PROPERTY_NAME: {
            PropertyType<?> type = registry.getType(context.prefix);
            return type != null ? type.getDescription() : null;
        }
        case PROPERTY_VALUE: {
            PropertyType<?> type = registry.getType(context.name);
            return type != null ? type.getDescription() : null;
        }
        default:
            return null;
        }
    }

    private static class TokenContext {
        public final String prefix;
        public final String name; // property name, only available when context type is an property value
        public final CompletionContextType type;

        private TokenContext(String prefix, String name, CompletionContextType type) {
            this.prefix = prefix;
            this.name = name;
            this.type = type;
        }
    }

    private static TokenContext getTokenContext(int offset, RandomReader reader, boolean collectWord) throws Exception {

        final long length = reader.getLength();
        char c;
        CompletionContextType type = CompletionContextType.PROPERTY_NAME;
        StringBuilder prefix = new StringBuilder();
        StringBuilder name = null;
        long i = offset - 1;

        // Collect prefix
        while (i >= 0) {
            c = reader.read(i);
            if (Character.isJavaIdentifierPart(c)) {
                prefix.insert(0, c);
                i--;
            } else {
                break;
            }
        }
        if (collectWord) {
            int j = offset;
            while (j <= length) {
                c = reader.read(i);
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
            c = reader.read(i--);
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
                type = CompletionContextType.PROPERTY_VALUE;
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
