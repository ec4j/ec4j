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
package org.eclipse.ec4j.core.parser;

/**
 * An immutable object that represents a location in the parsed text.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class Location {
    /**
     * The absolute character index, starting at 0.
     */
    public final int offset;

    /**
     * The line number, starting at 1.
     */
    public final int line;

    /**
     * The column number, starting at 1.
     */
    public final int column;

    Location(int offset, int line, int column) {
        this.offset = offset;
        this.column = column;
        this.line = line;
    }

    @Override
    public String toString() {
        return line + ":" + column + " (" + offset + ")";
    }

    @Override
    public int hashCode() {
        return offset;
    }

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

    public Location adjust(int increment) {
        return new Location(this.offset + increment, this.line, this.column + increment);
    }
}
