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

import java.io.IOException;
import java.io.Reader;

import org.ec4j.core.Resource;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class EditorConfigParser implements ParseContext {

    public static class Builder {
        private int bufferSize = DEFAULT_BUFFER_SIZE;

        public Builder bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public EditorConfigParser build() {
            return new EditorConfigParser(bufferSize);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    private EditorConfigHandler handler;
    private ErrorHandler errorHandler;
    private Reader reader;
    private final char[] buffer;
    private int bufferOffset;
    private int index;
    private int fill;
    private int line;
    private int lineOffset;
    private int last;
    private int current;
    private final StringBuilder captureBuffer;
    private int captureStart;
    private boolean inSection = false;

    private Resource resource;

    /**
     * Use the {@link #builder()} to create new instances.
     *
     * @param bufferSize
     * @param tolerant
     */
    EditorConfigParser(int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("buffersize is zero or negative");
        }
        this.buffer = new char[bufferSize];
        this.captureBuffer = new StringBuilder();
    }

    /**
     * Reads the entire input from the {@code resource} and transforms it into a sequence of parse events which are sent
     * to the given {@link EditorConfigHandler}.
     *
     * @param resource
     *            the {@link Resource} to parse
     * @param handler
     *            the handler to send the parse events to
     * @param errorHandler
     *            an {@link ErrorHandler} to notify on parse errors
     * @throws IOException
     *             on I/O problems when reading out of the given {@link Resource}
     * @throws ParseException
     *             only if {@link #tolerant} is {@code false}; otherwise the exceptions are passed to the
     *             {@link EditorConfigHandler}
     */
    public void parse(Resource resource, EditorConfigHandler handler, ErrorHandler errorHandler) throws IOException {
        this.resource = resource;
        this.handler = handler;
        this.errorHandler = errorHandler;
        bufferOffset = 0;
        index = 0;
        fill = 0;
        line = 1;
        lineOffset = 0;
        current = 0;
        last = -1;
        captureStart = -1;

        try (Reader reader = resource.openReader()) {
            this.reader = reader;
            readLines();
            if (!isEndOfText()) {
                throw error("Unexpected character");
            }
        }
    }

    private void readLines() throws IOException {
        handler.startDocument(this);
        int currentLine = 0;
        do {
            read();
            if (currentLine != line) {
                currentLine = line;
                readLine();
            }
        } while (!isEndOfText());
        if (inSection) {
            handler.endSection(this);
            inSection = false;
        }
        handler.endDocument(this);
    }

    private void readLine() throws IOException {
        try {
            readLineAndThrowExceptionIfError();
        } catch (ParseException e) {
            errorHandler.error(this, e);
        }
    }

    private void readLineAndThrowExceptionIfError() throws IOException {
        skipWhiteSpace();
        if (isNewLine()) {
            // blank line
            handler.blankLine(this);
            return;
        } else if (current == '\ufeff') {
            // BOM character, do nothing
            return;
        }
        switch (current) {
        case '#':
        case ';':
            // comment line
            readComment();
            break;
        case '[':
            // section line
            readSection();
            break;
        default:
            // property line
            readProperty();
        }
    }

    private void readComment() throws IOException {
        handler.startComment(this);
        startCapture();
        do {
            read();
        } while (!isEndOfText() && !isNewLine());
        handler.endComment(this, endCapture());
    }

    private void readSection() throws IOException {
        if (inSection) {
            handler.endSection(this);
            inSection = false;
        }
        handler.startSection(this);
        inSection = true;
        read();
        if (isEndOfText()) {
            throw new SectionNotClosedException(getLocation());
        }
        if (readChar(']')) {
            return;
        }
        // read pattern of the given section
        readPatternAndCloseSection();
    }

    private void readPatternAndCloseSection() throws IOException {
        handler.startPattern(this);
        String patternAndCloseSection = readString(StopReading.Pattern);
        // Search ']' close section at the end of the line
        char c;
        int i = -1;
        for (i = patternAndCloseSection.length() - 1; i >= 0; i--) {
            c = patternAndCloseSection.charAt(i);
            if (c == ']') {
                break;
            } else if (!isWhiteSpace(c)) {
                int oldIndex = index;
                index -= i;
                try {
                    throw new SectionNotClosedException(getLocation());
                } finally {
                    index = oldIndex;
                }
            }
        }
        if (i == -1) {
            throw new SectionNotClosedException(getLocation());
        }
        int oldIndex = index;
        index -= i + 1;
        try {
            String pattern = patternAndCloseSection.substring(0, i);
            handler.endPattern(this, pattern);
            index++;
        } finally {
            index = oldIndex;
        }
    }

    private enum StopReading {
        Pattern, PropertyName, PropertyValue
    }

    private String readString(StopReading stop) throws IOException {
        startCapture();
        while (!isStopReading(stop)) {
            /*
             * if (current == '\\') { pauseCapture(); readEscape(); startCapture(); } else
             */
            if (current < 0x20) {
                throw expected("valid string character");
            } else {
                read();
            }
        }
        return endCapture();
    }

    private boolean isStopReading(StopReading stop) {
        if (isEndOfText() || isNewLine()) {
            return true;
        }
        switch (stop) {
        case Pattern:
            // Read the full line
            if ((current == ';' || current == '#') && isWhiteSpace(last)) {
                // Inline comment
                return true;
            }
            return false;
        case PropertyName:
            return isColonSeparator() || isWhiteSpace();
        case PropertyValue:
            if ((current == ';' || current == '#') && isWhiteSpace(last)) {
                // Inline comment
                return true;
            }
            return false;
        default:
            return isWhiteSpace();
        }
    }

    private void readProperty() throws IOException {
        if (!inSection) {
            handler.startSection(this);
            inSection = true;
        }

        handler.startProperty(this);
        // property name
        skipWhiteSpace();
        handler.startPropertyName(this);
        // Get property property name
        String name = preprocessPropertyName(readString(StopReading.PropertyName));
        handler.endPropertyName(this, name);
        skipWhiteSpace();
        if (!readChar('=') && !readChar(':')) {
            throw new PropertyAssignementMissingException(name, getLocation());
        }
        // property value
        skipWhiteSpace();
        handler.startPropertyValue(this);
        String value = readString(StopReading.PropertyValue);
        if (value.length() < 1) {
            throw new PropertyValueMissingException(name, getLocation());
        }
        handler.endPropertyValue(this, value);
        handler.endProperty(this);
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
                last = -1;
                index++;
                return;
            }
        }
        if (current == '\n') {
            line++;
            lineOffset = bufferOffset + index;
        }
        last = current;
        current = buffer[index++];
    }

    private void startCapture() {
        captureStart = index - 1;
    }

    private String endCapture() {
        return endCapture(index);
    }

    private String endCapture(int index) {
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

    /** {@inheritDoc} */
    @Override
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
        return isWhiteSpace(current);
    }

    private static boolean isWhiteSpace(int c) {
        return c == ' ' || c == '\t';
    }

    private boolean isNewLine() {
        return current == '\n' || current == '\r';
    }

    private boolean isEndOfText() {
        return current == -1;
    }

    private boolean isColonSeparator() {
        return current == '=' || current == ':';
    }

    /**
     * Return the lowercased property name.
     *
     * @param name
     * @return the lowercased property name.
     */
    private static String preprocessPropertyName(String name) {
        if (name == null) {
            return name;
        }
        // According test "lowercase_names" : all property names are lowercased.
        return name.toLowerCase();
    }

    /** {@inheritDoc} */
    @Override
    public Resource getResource() {
        return resource;
    }
}
