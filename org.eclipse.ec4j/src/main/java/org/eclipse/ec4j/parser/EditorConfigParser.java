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
package org.eclipse.ec4j.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.eclipse.ec4j.parser.handlers.IEditorConfigHandler;

public class EditorConfigParser<Section, Option> {

	private static final int MIN_BUFFER_SIZE = 10;
	private static final int DEFAULT_BUFFER_SIZE = 1024;

	private final IEditorConfigHandler<Section, Option> handler;
	private Reader reader;
	private char[] buffer;
	private int bufferOffset;
	private int index;
	private int fill;
	private int line;
	private int lineOffset;
	private int current;
	private StringBuilder captureBuffer;
	private int captureStart;

	private boolean tolerant;
	private Section currentSection;

	public EditorConfigParser(IEditorConfigHandler<Section, Option> handler) {
		if (handler == null) {
			throw new NullPointerException("handler is null");
		}
		this.handler = handler;
		handler.setParser(this);
		setTolerant(false);
	}

	public void setTolerant(boolean tolerant) {
		this.tolerant = tolerant;
	}

	public boolean isTolerant() {
		return tolerant;
	}

	/**
	 * Parses the given input string. The input must contain a valid .editorconfig
	 * value, optionally padded with whitespace.
	 *
	 * @param string
	 *            the input string, must be valid .editorconfig
	 * @throws ParseException
	 *             if the input is not valid .editorconfig
	 */
	public void parse(String string) {
		if (string == null) {
			throw new NullPointerException("string is null");
		}
		int bufferSize = Math.max(MIN_BUFFER_SIZE, Math.min(DEFAULT_BUFFER_SIZE, string.length()));
		try {
			parse(new StringReader(string), bufferSize);
		} catch (IOException exception) {
			// StringReader does not throw IOException
			throw new RuntimeException(exception);
		}
	}

	/**
	 * Reads the entire input from the given reader and parses it as .editorconfig.
	 * The input must contain a valid .editorconfig value, optionally padded with
	 * whitespace.
	 * <p>
	 * Characters are read in chunks into a default-sized input buffer. Hence,
	 * wrapping a reader in an additional <code>BufferedReader</code> likely won't
	 * improve reading performance.
	 * </p>
	 *
	 * @param reader
	 *            the reader to read the input from
	 * @throws IOException
	 *             if an I/O error occurs in the reader
	 * @throws ParseException
	 *             if the input is not valid JSON
	 */
	public void parse(Reader reader) throws IOException {
		parse(reader, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Reads the entire input from the given reader and parses it as JSON. The input
	 * must contain a valid JSON value, optionally padded with whitespace.
	 * <p>
	 * Characters are read in chunks into an input buffer of the given size. Hence,
	 * wrapping a reader in an additional <code>BufferedReader</code> likely won't
	 * improve reading performance.
	 * </p>
	 *
	 * @param reader
	 *            the reader to read the input from
	 * @param buffersize
	 *            the size of the input buffer in chars
	 * @throws IOException
	 *             if an I/O error occurs in the reader
	 * @throws ParseException
	 *             if the input is not valid JSON
	 */
	public void parse(Reader reader, int buffersize) throws IOException {
		if (reader == null) {
			throw new NullPointerException("reader is null");
		}
		if (buffersize <= 0) {
			throw new IllegalArgumentException("buffersize is zero or negative");
		}
		this.reader = reader;
		buffer = new char[buffersize];
		bufferOffset = 0;
		index = 0;
		fill = 0;
		line = 1;
		lineOffset = 0;
		current = 0;
		captureStart = -1;
		readLines();
		if (!isEndOfText()) {
			throw error("Unexpected character");
		}
	}

	private void readLines() throws IOException {
		int currentLine = 0;
		do {
			read();
			if (currentLine != line) {
				currentLine = line;
				readLine();
			}
		} while (!isEndOfText());
	}

	private void readLine() throws IOException {
		try {
			readLineAndThrowExceptionIfError();
		} catch (ParseException e) {
			handler.error(e);
			if (!isTolerant()) {
				throw e;
			}
		}
	}

	private void readLineAndThrowExceptionIfError() throws IOException {
		skipWhiteSpace();
		if (isNewLine()) {
			// blank line, do nothing
			return;
		}
		switch (current) {
		case '#':
		case ';':
			readComment();
			break;
		case '[':
			currentSection = readSection();
			break;
		default:
			readOption();
		}
	}

	private void readComment() throws IOException {
		do {
			read();
		} while (!isEndOfText() && !isNewLine());
	}

	private Section readSection() throws IOException {
		Section section = handler.startSection();
		read();
		if (isEndOfText()) {
			throw new SectionNotClosedException(getLocation());
		}
		skipWhiteSpace();
		if (readChar(']')) {
			handler.endSection(section);
			return section;
		}
		// read pattern of the given section
		readPattern(section);
		skipWhiteSpace();
		if (!readChar(']')) {
			throw new SectionNotClosedException(getLocation());
		}
		handler.endSection(section);
		return section;
	}

	private void readPattern(Section section) throws IOException {
		// read pattern
		boolean multiPattern = readChar('{');
		if (multiPattern) {
			readMultiPatterns(section);
		} else {
			readSinglePattern(section);
		}
	}

	private void readMultiPatterns(Section section) throws IOException {
		handler.startMultiPatternSection(section);
		int i = 0;
		do {
			handler.startPattern(section, i);
			String pattern = readString(StopReading.MultiPattern);
			handler.endPattern(section, pattern, i);
			i++;
		} while (readChar(','));
		if (!readChar('}')) {
			throw new MultiPatternNotClosedException(getLocation());
		}
		handler.endMultiPatternSection(section);
	}

	private void readSinglePattern(Section section) throws IOException {
		handler.startPattern(section, 0);
		String pattern = readString(StopReading.SimplePattern);
		handler.endPattern(section, pattern, 0);
	}

	private enum StopReading {
		SimplePattern, MultiPattern, OptionName, OptionValue
	}

	private String readString(StopReading stop) throws IOException {
		// read();
		startCapture();
		// if (captureStart > 0) captureStart--;
		while (!isStopReading(stop)) {
			if (current == '\\') {
				pauseCapture();
				readEscape();
				startCapture();
			} else if (current < 0x20) {
				throw expected("valid string character");
			} else {
				read();
			}
		}
		String string = endCapture();
		// read();
		return string;
	}

	private boolean isStopReading(StopReading stop) {
		if (isEndOfText() || isNewLine()) {
			return true;
		}
		switch (stop) {
		case SimplePattern:
			return current == ']';
		case MultiPattern:
			return current == ',' || current == '}';
		case OptionName:
			return current == '=' || isWhiteSpace();
		default:
			return isWhiteSpace();
		}
	}

	private void readOption() throws IOException {
		handler.startOption();
		// option name
		skipWhiteSpace();
		handler.startOptionName();
		String name = readString(StopReading.OptionName);
		Option option = handler.endOptionName(name);
		skipWhiteSpace();
		if (!readChar('=')) {
			throw new OptionAssignementMissingException(name, getLocation());
		}
		// option value
		skipWhiteSpace();
		handler.startOptionValue(option, name);
		String value = readString(StopReading.OptionValue);
		if (value.length() < 1) {
			throw new OptionValueMissingException(name, getLocation());
		}
		handler.endOptionValue(option, value, name);
		handler.endOption(option, currentSection);
	}

	private void readEscape() throws IOException {
		read();
		switch (current) {
		case '"':
		case '/':
		case '\\':
			captureBuffer.append((char) current);
			break;
		case 'b':
			captureBuffer.append('\b');
			break;
		case 'f':
			captureBuffer.append('\f');
			break;
		case 'n':
			captureBuffer.append('\n');
			break;
		case 'r':
			captureBuffer.append('\r');
			break;
		case 't':
			captureBuffer.append('\t');
			break;
		case 'u':
			char[] hexChars = new char[4];
			for (int i = 0; i < 4; i++) {
				read();
				if (!isHexDigit()) {
					throw expected("hexadecimal digit");
				}
				hexChars[i] = (char) current;
			}
			captureBuffer.append((char) Integer.parseInt(new String(hexChars), 16));
			break;
		default:
			throw expected("valid escape sequence");
		}
		read();
	}

	private boolean readChar(char ch) throws IOException {
		if (current != ch) {
			return false;
		}
		read();
		return true;
	}

	private void skipWhiteSpace() throws IOException {
		while (isWhiteSpace()) {
			read();
		}
	}

	private void read() throws IOException {
		if (index == fill) {
			if (captureStart != -1) {
				captureBuffer.append(buffer, captureStart, fill - captureStart);
				captureStart = 0;
			}
			bufferOffset += fill;
			fill = reader.read(buffer, 0, buffer.length);
			index = 0;
			if (fill == -1) {
				current = -1;
				index++;
				return;
			}
		}
		if (current == '\n') {
			line++;
			lineOffset = bufferOffset + index;
		}
		current = buffer[index++];
	}

	private void startCapture() {
		if (captureBuffer == null) {
			captureBuffer = new StringBuilder();
		}
		captureStart = index - 1;
	}

	private void pauseCapture() {
		int end = isEndOfText() ? index : index - 1;
		captureBuffer.append(buffer, captureStart, end - captureStart);
		captureStart = -1;
	}

	private String endCapture() {
		int start = captureStart;
		int end = index - 1;
		captureStart = -1;
		if (captureBuffer.length() > 0) {
			captureBuffer.append(buffer, start, end - start);
			String captured = captureBuffer.toString();
			captureBuffer.setLength(0);
			return captured;
		}
		return new String(buffer, start, end - start);
	}

	public Location getLocation() {
		int offset = bufferOffset + index - 1;
		int column = offset - lineOffset + 1;
		return new Location(offset, line, column);
	}

	private ParseException expected(String expected) {
		if (isEndOfText()) {
			return error("Unexpected end of input");
		}
		return error("Expected " + expected);
	}

	private ParseException error(String message) {
		return new ParseException(message, getLocation());
	}

	private boolean isWhiteSpace() {
		return current == ' ' || current == '\t';
	}

	private boolean isNewLine() {
		return current == '\n' || current == '\r';
	}

	private boolean isHexDigit() {
		return current >= '0' && current <= '9' || current >= 'a' && current <= 'f' || current >= 'A' && current <= 'F';
	}

	private boolean isEndOfText() {
		return current == -1;
	}
}
