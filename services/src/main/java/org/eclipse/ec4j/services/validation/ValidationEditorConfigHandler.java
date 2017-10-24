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
package org.eclipse.ec4j.services.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.eclipse.ec4j.core.model.Option;
import org.eclipse.ec4j.core.model.Section;
import org.eclipse.ec4j.core.model.optiontypes.OptionException;
import org.eclipse.ec4j.core.model.optiontypes.OptionType;
import org.eclipse.ec4j.core.model.optiontypes.OptionTypeRegistry;
import org.eclipse.ec4j.core.parser.ErrorType;
import org.eclipse.ec4j.core.parser.Location;
import org.eclipse.ec4j.core.parser.ParseException;
import org.eclipse.ec4j.core.parser.handlers.EditorConfigHandlerAdapter;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class ValidationEditorConfigHandler extends EditorConfigHandlerAdapter<Section, Option> {

    private static final String PATTERN_SYNTAX_MESSAGE = "The pattern ''{0}'' is not valid ''{1}''";
    private static final String OPTION_NAME_NOT_EXISTS_MESSAGE = "The option ''{0}'' is not supported by .editorconfig";

    // private static final String OPTION_VALUE_TYPE_MESSAGE = "The option ''{0}''
    // doesn't support the value ''{1}''";

    private final IReporter reporter;
    private final ISeverityProvider provider;
    private final OptionTypeRegistry registry;
    private List<Section> sections;

    public ValidationEditorConfigHandler(IReporter reporter, ISeverityProvider provider, OptionTypeRegistry registry) {
        this.reporter = reporter;
        this.provider = provider != null ? provider : ISeverityProvider.DEFAULT;
        this.registry = registry != null ? registry : OptionTypeRegistry.DEFAULT;
        this.sections = new ArrayList<>();
    }

    @Override
    public void startDocument() {

    }

    @Override
    public void endDocument() {
        for (Section section : getSections()) {
            section.preprocessOptions();
        }
    }

    @Override
    public Section startSection() {
        return new Section(null);
    }

    @Override
    public void endSection(Section section) {
        sections.add(section);
    }

    @Override
    public void endPattern(Section section, String pattern) {
        section.setPattern(pattern);
        PatternSyntaxException e = section.getGlob().getError();
        if (e != null) {
            Location start = getLocation();
            Location end = start.adjust(pattern.length());
            ErrorType errorType = ErrorType.PatternSyntaxType;
            reporter.addError(MessageFormat.format(PATTERN_SYNTAX_MESSAGE, pattern, e.getMessage()), start, end,
                    errorType, provider.getSeverity(errorType));
        }
    }

    @Override
    public Option endOptionName(String name) {
        // Validate option name
        if (!isOptionExists(name)) {
            Location start = getLocation();
            Location end = start.adjust(-name.length());
            ErrorType errorType = ErrorType.OptionNameNotExists;
            reporter.addError(MessageFormat.format(OPTION_NAME_NOT_EXISTS_MESSAGE, name), start, end, errorType,
                    provider.getSeverity(errorType));
        }
        return null;
    }

    @Override
    public void endOptionValue(Option option, String value, String name) {
        // Validate value of the option name
        try {
            validateOptionValue(name, value);
        } catch (OptionException e) {
            Location start = getLocation();
            ErrorType errorType = ErrorType.OptionValueType;
            Location end = start.adjust(-value.length());
            reporter.addError(e.getMessage(), start, end, errorType, provider.getSeverity(errorType));
        }
    }

    @Override
    public void error(ParseException e) {
        reporter.addError(e.getMessage(), e.getLocation(), null, e.getErrorType(), getSeverity(e));
    }

    protected Severity getSeverity(ParseException e) {
        return provider.getSeverity(e.getErrorType());
    }

    private boolean validateOptionValue(String name, String value) throws OptionException {
        OptionType<?> type = getOptionType(name);
        if (type != null) {
            type.validate(value);
        }
        return true;
    }

    private boolean isOptionExists(String name) {
        return getOptionType(name) != null;
    }

    private OptionType<?> getOptionType(String name) {
        return registry.getType(name);
    }

    private List<Section> getSections() {
        return sections;
    }
}
