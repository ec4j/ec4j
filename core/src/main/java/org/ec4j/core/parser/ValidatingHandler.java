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

import org.ec4j.core.PropertyTypeRegistry;
import org.ec4j.core.model.Glob;
import org.ec4j.core.model.PropertyType;
import org.ec4j.core.model.PropertyType.PropertyValue;

/**
 * An {@link EditorConfigHandler} that inherits from {@link AbstractValidatingHandler} and adds nothing new to it except
 * that {@link ValidatingHandler} is not abstract.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class ValidatingHandler extends AbstractValidatingHandler {

    public ValidatingHandler(PropertyTypeRegistry registry) {
        super(registry);
    }

    /** {@inheritDoc} */
    @Override
    public void blankLine(ParseContext context) {
    }

    /** {@inheritDoc} */
    @Override
    public void endComment(ParseContext context, String comment) {
    }

    /** {@inheritDoc} */
    @Override
    public void endDocument(ParseContext context) {
    }

    /** {@inheritDoc} */
    @Override
    public void endProperty(ParseContext context) {
    }

    /** {@inheritDoc} */
    @Override
    public void endSection(ParseContext context) {
    }

    /** {@inheritDoc} */
    @Override
    protected void glob(ParseContext context, Glob glob) {
    }

    /** {@inheritDoc} */
    @Override
    protected void name(ParseContext context, String name) {
    }

    /** {@inheritDoc} */
    @Override
    protected void propertyValue(ParseContext context, PropertyValue<?> propValue) {
    }

    /** {@inheritDoc} */
    @Override
    public void startComment(ParseContext context) {
    }

    /** {@inheritDoc} */
    @Override
    public void startDocument(ParseContext context) {
    }

    /** {@inheritDoc} */
    @Override
    public void startProperty(ParseContext context) {
    }

    /** {@inheritDoc} */
    @Override
    public void startSection(ParseContext context) {
    }

    /** {@inheritDoc} */
    @Override
    protected void type(ParseContext context, PropertyType<?> type) {
    }

}
