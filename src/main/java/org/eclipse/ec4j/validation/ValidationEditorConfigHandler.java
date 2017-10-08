/**
 * The MIT License
 * Copyright © 2017 Angelo Zerr and other contributors as
 * indicated by the @author tags.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.eclipse.ec4j.validation;

import java.text.MessageFormat;

import org.eclipse.ec4j.model.optiontypes.OptionException;
import org.eclipse.ec4j.model.optiontypes.OptionType;
import org.eclipse.ec4j.model.optiontypes.OptionTypeRegistry;
import org.eclipse.ec4j.parser.ErrorType;
import org.eclipse.ec4j.parser.Location;
import org.eclipse.ec4j.parser.ParseException;
import org.eclipse.ec4j.parser.handlers.EditorConfigHandlerAdapter;

public class ValidationEditorConfigHandler extends EditorConfigHandlerAdapter<Object, Object> {

	private static final String OPTION_NAME_NOT_EXISTS_MESSAGE = "The option ''{0}'' is not supported by .editorconfig";
	private static final String OPTION_VALUE_TYPE_MESSAGE = "The option ''{0}'' doesn't support the value ''{1}''";

	private final IReporter reporter;
	private final ISeverityProvider provider;
	private final OptionTypeRegistry registry;

	public ValidationEditorConfigHandler(IReporter reporter, ISeverityProvider provider, OptionTypeRegistry registry) {
		this.reporter = reporter;
		this.provider = provider != null ? provider : ISeverityProvider.DEFAULT;
		this.registry = registry != null ? registry : OptionTypeRegistry.DEFAULT;
	}

	@Override
	public void endPattern(Object section, String pattern, int i) {
		// TODO: validate pattern
	}

	@Override
	public Object endOptionName(String name) {
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
	public void endOptionValue(Object option, String value, String name) {
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
		OptionType<?> type = getOption(name);
		if (type != null) {
			type.validate(value);
		}
		return true;
	}

	private boolean isOptionExists(String name) {
		return getOption(name) != null;
	}

	private OptionType<?> getOption(String name) {
		return registry.getType(name);
	}
}
