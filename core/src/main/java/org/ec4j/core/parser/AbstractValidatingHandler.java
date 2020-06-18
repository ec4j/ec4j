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

import java.util.Locale;
import java.util.regex.PatternSyntaxException;

import org.ec4j.core.PropertyTypeRegistry;
import org.ec4j.core.model.Glob;
import org.ec4j.core.model.PropertyType;
import org.ec4j.core.model.PropertyType.PropertyValue;
import org.ec4j.core.parser.ErrorEvent.ErrorType;

/**
 * A base class for {@link EditorConfigHandler}s which require value and glob related errors to be passed to
 * {@link ErrorHandler}.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public abstract class AbstractValidatingHandler implements EditorConfigHandler {
    protected Location globStart;
    protected Location propertyValueStart;
    protected final PropertyTypeRegistry registry;
    protected PropertyType<?> type;

    public AbstractValidatingHandler(PropertyTypeRegistry registry) {
        super();
        this.registry = registry;
    }

    /** {@inheritDoc} */
    @Override
    public void endGlob(ParseContext context, String globSource) {
        final Glob glob = new Glob(globSource);
        final PatternSyntaxException e = glob.getError();
        if (e != null) {
            final String msg = String.format("The glob '%s' is not valid: %s",
                    globSource,
                    e.getMessage());
            context.getErrorHandler().error(context,
                    new ErrorEvent(globStart, context.getLocation(), context.getResource(), msg, ErrorType.INVALID_GLOB));
        }
        glob(context, glob);
        globStart = null;
    }

    /** {@inheritDoc} */
    @Override
    public void endPropertyName(ParseContext context, String name) {
        name = normalizePropertyName(name);
        this.type = registry.getType(name);
        if (this.type != null) {
            /* propertyBuilder.type(type) sets also the (lowercased) name */
            type(context, type);
        } else {
            name(context, name);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void endPropertyValue(ParseContext context, String value) {
        final PropertyValue<?> propValue = type == null ? PropertyValue.valid(value, value) : type.parse(value);
        if (!propValue.isValid()) {
            context.getErrorHandler().error(context,
                    new ErrorEvent(
                            propertyValueStart,
                            context.getLocation(),
                            context.getResource(),
                            propValue.getErrorMessage(),
                            ErrorType.INVALID_PROPERTY_VALUE));
        }
        propertyValue(context, propValue);
        this.type = null;
        this.propertyValueStart = null;
    }

    /**
     * Handle the {@link Glob} created out of the glob string hit recently in the underlying {@code .editorconfig} file.
     * Note that this method gets called after any evetual errors related to the glob were sent to {@link ErrorHandler}.
     *
     * @param context
     *        the current {@link ParseContext}
     * @param glob
     *        the {@link Glob} to handle
     */
    protected abstract void glob(ParseContext context, Glob glob);

    /**
     * Handle the given property {@code name}.
     * <p>
     * Note that this method is called only when the given property {@code name} has no associated {@link PropertyType}.
     * If the given property name has an associated {@link PropertyType}, {@link #type} is invoked instead.
     *
     * @param context
     *        the current {@link ParseContext}
     * @param name
     *        the property name
     */
    protected abstract void name(ParseContext context, String name);

    /**
     * Lower-cases the given property {@code name}.
     *
     * @param name
     *        the property name to normalize
     * @return the normalized property name
     */
    protected String normalizePropertyName(String name) {
        return name == null ? null : name.toLowerCase(Locale.US);
    }

    /**
     * Handle the given {@link PropertyValue}. Note that this method gets called after any evetual errors related to the
     * value were sent to {@link ErrorHandler}.
     *
     * @param context
     *        the current parse context
     * @param propValue
     *        the {@link PropertyValue} to handle
     */
    protected abstract void propertyValue(ParseContext context, PropertyValue<?> propValue);

    /** {@inheritDoc} */
    @Override
    public void startGlob(ParseContext context) {
        this.globStart = context.getLocation();
    }

    /** {@inheritDoc} */
    @Override
    public void startPropertyName(ParseContext context) {
    }

    /** {@inheritDoc} */
    @Override
    public void startPropertyValue(ParseContext context) {
        this.propertyValueStart = context.getLocation();
    }

    /**
     * Handle the given {@link PropertyType} associated with a property {@code name} that has been recently hit in the
     * underlying {@code .editorconfig} file.
     * <p>
     * Note that this method is called only when the given property {@code name} has an associated {@link PropertyType}.
     * If the given property name has no associated {@link PropertyType}, {@link #name(ParseContext, String)} is invoked
     * instead.
     *
     * @param context
     *        the current parse context
     * @param type
     *        the {@link PropertyType} to handle
     */
    protected abstract void type(ParseContext context, PropertyType<?> type);

}
