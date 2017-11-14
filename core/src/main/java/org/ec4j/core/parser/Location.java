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

import java.util.StringTokenizer;

/**
 * An immutable object that represents a location in the parsed text.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class Location {

    /**
     * Parses a {@link Location} from a {@link String} that has the format defined by {@link #toString()}. Should make
     * it easier to write tests.
     *
     * @param locationString
     *            the location string to parse
     * @return a new {@link Location} parsed out of the given {@code locationString}
     */
    public static Location parse(String locationString) {
        StringTokenizer st = new StringTokenizer(locationString, ": ()");
        if (st.hasMoreTokens()) {
            final int line = Integer.parseInt(st.nextToken());
            if (st.hasMoreTokens()) {
                final int column = Integer.parseInt(st.nextToken());
                if (st.hasMoreTokens()) {
                    final int offset = Integer.parseInt(st.nextToken());
                    return new Location(offset, line, column);
                }
            }
        }
        throw new IllegalArgumentException(
                "Cannot parse \"" + locationString + "\" into a " + Location.class.getName());
    }

    /**
     * The column number, starting at 1.
     */
    public final int column;

    /**
     * The line number, starting at 1.
     */
    public final int line;

    /**
     * The absolute character index, starting at 0.
     */
    public final int offset;

    Location(int offset, int line, int column) {
        this.offset = offset;
        this.column = column;
        this.line = line;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Location other = (Location) obj;
        return offset == other.offset && column == other.column && line == other.line;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return offset;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return line + ":" + column + " (" + offset + ")";
    }

}
