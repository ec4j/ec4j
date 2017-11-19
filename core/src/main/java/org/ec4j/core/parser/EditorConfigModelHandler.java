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
import org.ec4j.core.model.EditorConfig;
import org.ec4j.core.model.Glob;
import org.ec4j.core.model.Property;
import org.ec4j.core.model.PropertyType;
import org.ec4j.core.model.PropertyType.PropertyValue;
import org.ec4j.core.model.Section;
import org.ec4j.core.model.Version;

/**
 * A {@link EditorConfigHandler} implementation that assemles {@link EditorConfig} instances out of the parse
 * notifications.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class EditorConfigModelHandler implements EditorConfigHandler {

    protected EditorConfig.Builder editorConfigBuilder;
    protected Section.Builder sectionBuilder;
    protected Property.Builder propertyBuilder;
    protected final PropertyTypeRegistry registry;
    protected final Version version;
    protected PropertyType<?> type;
    protected Location patternStart;

    public EditorConfigModelHandler(PropertyTypeRegistry registry, Version version) {
        this.registry = registry;
        this.version = version;
    }

    /** {@inheritDoc} */
    @Override
    public void startDocument(ParseContext context) {
        editorConfigBuilder = EditorConfig.builder().version(version);
    }

    /** {@inheritDoc} */
    @Override
    public void endDocument(ParseContext context) {
    }

    /** {@inheritDoc} */
    @Override
    public void startSection(ParseContext context) {
        sectionBuilder = editorConfigBuilder.openSection();
    }

    /** {@inheritDoc} */
    @Override
    public void endSection(ParseContext context) {
        sectionBuilder.applyDefaults().closeSection();
        sectionBuilder = null;
    }

    /** {@inheritDoc} */
    @Override
    public void startProperty(ParseContext context) {
        propertyBuilder = sectionBuilder.openProperty();
    }

    /** {@inheritDoc} */
    @Override
    public void endProperty(ParseContext context) {
        propertyBuilder.closeProperty();
        propertyBuilder = null;
    }

    /**
     * @return the {@link EditorConfig} instance parsed out of the event stream
     */
    public EditorConfig getEditorConfig() {
        EditorConfig result = editorConfigBuilder.build();
        editorConfigBuilder = null;
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public void startPattern(ParseContext context) {
        this.patternStart = context.getLocation();
    }

    /** {@inheritDoc} */
    @Override
    public void endPattern(ParseContext context, String pattern) {
        final Glob glob = new Glob(context.getResource().getParent().getPath(), pattern);
        final PatternSyntaxException e = glob.getError();
        if (e != null) {
            final String msg = String.format("The pattern '%s' is not valid: %s", pattern, e.getMessage());
            context.getErrorHandler().error(context, new ParseException(msg, false, context.getLocation()));
        }
        sectionBuilder.glob(glob);
        patternStart = null;
    }

    /** {@inheritDoc} */
    @Override
    public void startPropertyName(ParseContext context) {
    }

    /** {@inheritDoc} */
    @Override
    public void endPropertyName(ParseContext context, String name) {
        name = normalizePropertyName(name);
        this.type = registry.getType(name);
        if (this.type != null) {
            /* propertyBuilder.type(type) sets also the (lowercased) name */
            propertyBuilder.type(type);
        } else {
            propertyBuilder.name(name);
        }
    }

    /**
     * Lower-cases the given property {@code name}.
     *
     * @param name
     *            the property name to normalize
     * @return the normalized property name
     */
    protected String normalizePropertyName(String name) {
        return name == null ? null : name.toLowerCase(Locale.US);
    }

    /** {@inheritDoc} */
    @Override
    public void startPropertyValue(ParseContext context) {
    }

    /** {@inheritDoc} */
    @Override
    public void endPropertyValue(ParseContext context, String value) {
        final PropertyValue<?> propValue = type == null ? PropertyValue.valid(value, value) : type.parse(value);
        if (!propValue.isValid()) {
            context.getErrorHandler().error(context,
                    new InvalidPropertyValueException(propValue.getErrorMessage(), context.getLocation()));
        }
        propertyBuilder.value(propValue);
        this.type = null;
    }

    /** {@inheritDoc} */
    @Override
    public void startComment(ParseContext context) {
    }

    /** {@inheritDoc} */
    @Override
    public void endComment(ParseContext context, String comment) {
    }

    /** {@inheritDoc} */
    @Override
    public void blankLine(ParseContext context) {
    }

}