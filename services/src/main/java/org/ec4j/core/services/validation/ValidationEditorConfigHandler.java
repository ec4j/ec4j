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
package org.ec4j.core.services.validation;

import java.text.MessageFormat;
import java.util.regex.PatternSyntaxException;

import org.ec4j.core.PropertyTypeRegistry;
import org.ec4j.core.model.Glob;
import org.ec4j.core.model.PropertyType;
import org.ec4j.core.model.PropertyType.PropertyValue;
import org.ec4j.core.parser.EditorConfigHandler;
import org.ec4j.core.parser.ErrorEvent;
import org.ec4j.core.parser.ErrorEvent.ErrorType;
import org.ec4j.core.parser.ErrorHandler;
import org.ec4j.core.parser.Location;
import org.ec4j.core.parser.ParseContext;
import org.ec4j.core.parser.ParseException;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class ValidationEditorConfigHandler implements EditorConfigHandler, ErrorHandler {

    private static final String PROPERTY_NAME_NOT_EXISTS_MESSAGE = "The property ''{0}'' is not supported by .editorconfig";

    // private static final String PROPERTY_VALUE_TYPE_MESSAGE = "The property ''{0}''
    // doesn't support the value ''{1}''";

    private final IReporter reporter;
    private final ISeverityProvider provider;
    private final PropertyTypeRegistry registry;
    private Location patternStart;
    private Location propertyNameStart;
    private Location propertyValueStart;
    private PropertyType<?> type;

    public ValidationEditorConfigHandler(IReporter reporter, ISeverityProvider provider,
            PropertyTypeRegistry registry) {
        this.reporter = reporter;
        this.provider = provider != null ? provider : ISeverityProvider.DEFAULT;
        this.registry = registry != null ? registry : PropertyTypeRegistry.getDefault();
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
        final Glob glob = new Glob(context.getResource().getParent().getPath(), pattern);
        final PatternSyntaxException e = glob.getError();
        if (e != null) {
            final String msg = String.format("The pattern '%s' is not valid: %s", pattern, e.getMessage());
            this.error(context, new ErrorEvent(patternStart, context.getLocation(), msg, ErrorType.INVALID_GLOB));
        }
        patternStart = null;
    }

    @Override
    public void startPropertyName(ParseContext context) {
        this.propertyNameStart = context.getLocation();
    }

    @Override
    public void endPropertyName(ParseContext context, String name) {
        // Validate property name
        this.type = registry.getType(name);
        if (type == null) {
            Location end = context.getLocation();
            reporter.addError(MessageFormat.format(PROPERTY_NAME_NOT_EXISTS_MESSAGE, name), propertyNameStart, end,
                    Severity.warning);
        }
        propertyNameStart = null;
    }

    @Override
    public void startPropertyValue(ParseContext context) {
        this.propertyValueStart = context.getLocation();
    }

    @Override
    public void endPropertyValue(ParseContext context, String value) {
        // Validate value of the property name
        if (type != null) {
            PropertyValue<?> parsedValue = type.parse(value);
            if (!parsedValue.isValid()) {
                Location end = context.getLocation();
                reporter.addError(parsedValue.getErrorMessage(), propertyValueStart, end, Severity.warning);
            }
        }
        type = null;
        propertyValueStart = null;
    }

    @Override
    public void error(ParseContext context, ErrorEvent e) throws ParseException {
        reporter.addError(e.getMessage(), e.getStart(), e.getEnd(), getSeverity(e));
    }

    protected Severity getSeverity(ErrorEvent e) {
        return provider.getSeverity(e);
    }

    @Override
    public void startPattern(ParseContext context) {

    }

    @Override
    public void startProperty(ParseContext context) {

    }

    @Override
    public void endProperty(ParseContext context) {

    }

    @Override
    public void startComment(ParseContext context) {
    }

    @Override
    public void endComment(ParseContext context, String comment) {
    }

    @Override
    public void blankLine(ParseContext context) {
    }

}
