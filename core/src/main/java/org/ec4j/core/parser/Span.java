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

import org.ec4j.core.model.Property;
import org.ec4j.core.model.Section;

/**
 * A pair of start and end {@link Location}s. This class aggregates a few {@link Span} subclasses to be able to store
 * multiple {@link Span}s in one model element, such as {@link NameSpan} and {@link ValueSpan} in a {@link Property}.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class Span {

    /**
     * A {@link Span} builder.
     */
    public static class Builder {
        private Location end;
        private Location start;

        /**
         * @return a new {@link NameSpan}
         */
        public NameSpan buildNameSpan() {
            return new NameSpan(start, end);
        }

        /**
         * @return a new {@link PatternSpan}
         */
        public PatternSpan buildPatternSpan() {
            return new PatternSpan(start, end);
        }

        /**
         * @return a new {@link NameSpan}
         */
        public Span buildSpan() {
            return new Span(start, end);
        }

        /**
         * @return a new {@link ValueSpan}
         */
        public ValueSpan buildValueSpan() {
            return new ValueSpan(start, end);
        }

        /**
         * Sets the end of the {@link Span}.
         *
         * @param end
         *            the end {@link Location}
         * @return this {@link Builder}
         */
        public Builder end(Location end) {
            this.end = end;
            return this;
        }

        /**
         * Sets the end of the {@link Span} unless it was set already.
         *
         * @param end
         *            the end {@link Location}
         * @return this {@link Builder}
         */
        public Builder endIfNeeded(Location end) {
            if (this.end == null) {
                this.end = end;
            }
            return this;
        }

        /**
         * Sets the start of the {@link Span}.
         *
         * @param start
         *            the start {@link Location}
         * @return this {@link Builder}
         */
        public Builder start(Location start) {
            this.start = start;
            return this;
        }
    }

    /**
     * A subclass of {@link Span} to be able to set a span for both name and value of a {@link Property}.
     */
    public static class NameSpan extends Span {

        private NameSpan(Location start, Location end) {
            super(start, end);
        }

    }

    /**
     * A subclass of {@link Span} to be able to set a span for both glob and the whole secttion of a {@link Section}.
     */
    public static class PatternSpan extends Span {

        private PatternSpan(Location start, Location end) {
            super(start, end);
        }

    }

    /**
     * A subclass of {@link Span} to be able to set a span for both name and value of a {@link Property}.
     */
    public static class ValueSpan extends Span {

        private ValueSpan(Location start, Location end) {
            super(start, end);
        }

    }

    /**
     * @return a new {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Parses a {@link Span} from a {@link String} that has the format defined by {@link #toString()}. Should make it
     * easier to write tests.
     *
     * @param spanString
     *            the span string to parse
     * @return a new {@link Span} parsed out of the given {@code spanString}
     */
    public static Span parse(String spanString) {
        StringTokenizer st = new StringTokenizer(spanString, "-");
        if (st.hasMoreTokens()) {
            final Location start = Location.parse(st.nextToken());
            if (st.hasMoreTokens()) {
                final Location end = Location.parse(st.nextToken());
                return new Span(start, end);
            }
        }
        throw new IllegalArgumentException("Cannot parse \"" + spanString + "\" into a " + Span.class.getName());
    }

    private final Location end;
    private final Location start;

    public Span(Location start, Location end) {
        super();
        this.start = start;
        this.end = end;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Span other = (Span) obj;
        if (end == null) {
            if (other.end != null)
                return false;
        } else if (!end.equals(other.end))
            return false;
        if (start == null) {
            if (other.start != null)
                return false;
        } else if (!start.equals(other.start))
            return false;
        return true;
    }

    /**
     * @return the end {@link Location} of this {@link Span}
     */
    public Location getEnd() {
        return end;
    }

    /**
     * @return the start {@link Location} of this {@link Span}
     */
    public Location getStart() {
        return start;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((end == null) ? 0 : end.hashCode());
        result = prime * result + ((start == null) ? 0 : start.hashCode());
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return start + " - " + end;
    }
}
