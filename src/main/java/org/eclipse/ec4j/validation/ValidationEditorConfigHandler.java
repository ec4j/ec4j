/**
 *  Copyright (c) 2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
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
