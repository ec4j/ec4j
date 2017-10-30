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
import org.eclipse.ec4j.core.model.propertytype.PropertyException;
import org.eclipse.ec4j.core.model.propertytype.PropertyType;
import org.eclipse.ec4j.core.model.propertytype.PropertyTypeRegistry;
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

    public ValidationEditorConfigHandler(IReporter reporter, ISeverityProvider provider, PropertyTypeRegistry registry) {
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
    public void endPropertyName(ParseContext context, String name) {
        // Validate property name
        this.type = registry.getType(name);
        if (type == null) {
            Location end = context.getLocation();
            ErrorType errorType = ErrorType.PropertyNameNotExists;
            reporter.addError(MessageFormat.format(PROPERTY_NAME_NOT_EXISTS_MESSAGE, name), propertyNameStart, end,
                    errorType, provider.getSeverity(errorType));
        }
        propertyNameStart = null;
    }

    @Override
    public void endPropertyValue(ParseContext context, String value) {
        // Validate value of the property name
        try {
            if (type != null) {
                type.validate(value);
            }
        } catch (PropertyException e) {
            Location end = context.getLocation();
            ErrorType errorType = ErrorType.PropertyValueType;
            reporter.addError(e.getMessage(), propertyValueStart, end, errorType, provider.getSeverity(errorType));
        }
        type = null;
        propertyValueStart = null;
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
    public void startProperty(ParseContext context) {

    }

    @Override
    public void endProperty(ParseContext context) {

    }

    @Override
    public void startPropertyName(ParseContext context) {

    }

    @Override
    public void startPropertyValue(ParseContext context) {

    }
}
