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
package fr.opensagres.ec4j.core.parser;

import fr.opensagres.ec4j.core.model.EditorConfig;
import fr.opensagres.ec4j.core.model.Property;
import fr.opensagres.ec4j.core.model.Section;
import fr.opensagres.ec4j.core.model.Version;
import fr.opensagres.ec4j.core.model.propertytype.PropertyTypeRegistry;

/**
 * A {@link EditorConfigHandler} implementation that assemles {@link EditorConfig} instances out of the parse
 * notifications.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class EditorConfigModelHandler implements EditorConfigHandler {

    private EditorConfig.Builder editorConfigBuilder;
    private Section.Builder sectionBuilder;
    private Property.Builder propertyBuilder;
    private final PropertyTypeRegistry registry;
    private final Version version;

    public EditorConfigModelHandler(PropertyTypeRegistry registry, Version version) {
        this.registry = registry;
        this.version = version;
    }

    /** {@inheritDoc} */
    @Override
    public void startDocument(ParseContext context) {
        editorConfigBuilder = EditorConfig.builder(registry).version(version);
        editorConfigBuilder.resourcePath(context.getResource().getParent());
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
        sectionBuilder.closeSection();
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

    /** {@inheritDoc} */
    @Override
    public void error(ParseException e) {
        e.printStackTrace();
    }

    /**
     * @return the {@link EditorConfig} instance parsed out of the event stream
     */
    public EditorConfig getEditorConfig() {
        return editorConfigBuilder.build();
    }

    /** {@inheritDoc} */
    @Override
    public void startPattern(ParseContext context) {
    }

    /** {@inheritDoc} */
    @Override
    public void endPattern(ParseContext context, String pattern) {
        sectionBuilder.pattern(pattern);
    }

    /** {@inheritDoc} */
    @Override
    public void startPropertyName(ParseContext context) {
    }

    /** {@inheritDoc} */
    @Override
    public void endPropertyName(ParseContext context, String name) {
        propertyBuilder.name(name);
    }

    /** {@inheritDoc} */
    @Override
    public void startPropertyValue(ParseContext context) {
    }

    /** {@inheritDoc} */
    @Override
    public void endPropertyValue(ParseContext context, String value) {
        propertyBuilder.value(value);
    }

}