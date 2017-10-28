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
import java.util.regex.PatternSyntaxException;

import org.eclipse.ec4j.core.model.Glob;
import org.eclipse.ec4j.core.model.optiontypes.OptionException;
import org.eclipse.ec4j.core.model.optiontypes.OptionType;
import org.eclipse.ec4j.core.model.optiontypes.OptionTypeRegistry;
import org.eclipse.ec4j.core.parser.EditorConfigHandler;
import org.eclipse.ec4j.core.parser.ErrorType;
import org.eclipse.ec4j.core.parser.Location;
import org.eclipse.ec4j.core.parser.ParseContext;
import org.eclipse.ec4j.core.parser.ParseException;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class ValidationEditorConfigHandler implements EditorConfigHandler {

    private static final String PATTERN_SYNTAX_MESSAGE = "The pattern ''{0}'' is not valid ''{1}''";
    private static final String OPTION_NAME_NOT_EXISTS_MESSAGE = "The option ''{0}'' is not supported by .editorconfig";

    // private static final String OPTION_VALUE_TYPE_MESSAGE = "The option ''{0}''
    // doesn't support the value ''{1}''";

    private final IReporter reporter;
    private final ISeverityProvider provider;
    private final OptionTypeRegistry registry;
    private Location patternStart;
    private Location optionNameStart;
    private Location optionValueStart;
    private OptionType<?> type;

    public ValidationEditorConfigHandler(IReporter reporter, ISeverityProvider provider, OptionTypeRegistry registry) {
        this.reporter = reporter;
        this.provider = provider != null ? provider : ISeverityProvider.DEFAULT;
        this.registry = registry != null ? registry : OptionTypeRegistry.getDefault();
    }

    @Override
    public void startDocument(ParseContext context) {

    }

    @Override
    public void endDocument(ParseContext context) {
    }

    @Override
    public void startSection(ParseContext context) {
    }

    @Override
    public void endSection(ParseContext context) {
    }

    @Override
    public void endPattern(ParseContext context, String pattern) {
        Glob g = new Glob(context.getResource().getParent().getPath(), pattern);
        PatternSyntaxException e = g.getError();
        if (e != null) {
            Location end = context.getLocation();
            ErrorType errorType = ErrorType.PatternSyntaxType;
            reporter.addError(MessageFormat.format(PATTERN_SYNTAX_MESSAGE, pattern, e.getMessage()), patternStart, end,
                    errorType, provider.getSeverity(errorType));
        }
        patternStart = null;
    }

    @Override
    public void endOptionName(ParseContext context, String name) {
        // Validate option name
        this.type = registry.getType(name);
        if (type == null) {
            Location end = context.getLocation();
            ErrorType errorType = ErrorType.OptionNameNotExists;
            reporter.addError(MessageFormat.format(OPTION_NAME_NOT_EXISTS_MESSAGE, name), optionNameStart, end,
                    errorType, provider.getSeverity(errorType));
        }
        optionNameStart = null;
    }

    @Override
    public void endOptionValue(ParseContext context, String value) {
        // Validate value of the option name
        try {
            if (type != null) {
                type.validate(value);
            }
        } catch (OptionException e) {
            Location end = context.getLocation();
            ErrorType errorType = ErrorType.OptionValueType;
            reporter.addError(e.getMessage(), optionValueStart, end, errorType, provider.getSeverity(errorType));
        }
        type = null;
        optionValueStart = null;
    }

    @Override
    public void error(ParseException e) {
        reporter.addError(e.getMessage(), e.getLocation(), null, e.getErrorType(), getSeverity(e));
    }

    protected Severity getSeverity(ParseException e) {
        return provider.getSeverity(e.getErrorType());
    }

    @Override
    public void startPattern(ParseContext context) {

    }

    @Override
    public void startOption(ParseContext context) {

    }

    @Override
    public void endOption(ParseContext context) {

    }

    @Override
    public void startOptionName(ParseContext context) {

    }

    @Override
    public void startOptionValue(ParseContext context) {

    }
}
