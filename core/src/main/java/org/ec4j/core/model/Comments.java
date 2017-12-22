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
package org.ec4j.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.ec4j.core.parser.Location;
import org.ec4j.core.parser.Span;

/**
 * Aggregates {@link CommentBlocks}, {@link CommentBlock} and {@link CommentLine}.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class Comments {

    /**
     * A collection of contiguous {@link CommentLine}s.
     */
    public static class CommentBlock extends Adaptable {

        /**
         * A {@link CommentBlock} builder.
         */
        public static class Builder extends Adaptable.Builder<Builder> {
            private List<CommentLine> commentLines = new ArrayList<>();
            private final CommentBlocks.Builder parentBuilder;
            private Location start;

            public Builder(CommentBlocks.Builder parentBuilder) {
                super();
                this.parentBuilder = parentBuilder;
            }

            /**
             * @return a new {@link CommentBlock}
             */
            public CommentBlock build() {
                final int linesCount = this.commentLines.size();
                if (start != null && linesCount > 0) {
                    Span lastSpan = this.commentLines.get(linesCount - 1).getAdapter(Span.class);
                    if (lastSpan != null) {
                        adapter(new Span(start, lastSpan.getEnd()));
                    }
                }

                List<CommentLine> useComments = Collections.unmodifiableList(this.commentLines);
                this.commentLines = null;
                return new CommentBlock(sealAdapters(), useComments);
            }

            /**
             * Calls {@link #build()}, passes the result to {@link #parentBuilder} and returns {@link #parentBuilder}.
             *
             * @return {@link #parentBuilder}
             */
            public CommentBlocks.Builder closeCommentBlock() {
                return parentBuilder.commentBlock(build());
            }

            /**
             * Adds a single {@link CommentLine}.
             *
             * @param commentLine
             *            the {@link CommentLine} to add
             * @return this {@link Builder}
             */
            public Builder commentLine(CommentLine commentLine) {
                if (this.commentLines.size() == 0) {
                    /* this was the first comment line of this block */
                    Span span = commentLine.getAdapter(Span.class);
                    if (span != null) {
                        start = span.getStart();
                    }
                }
                this.commentLines.add(commentLine);
                return this;
            }

            /**
             * Adds multiple {@link CommentLine}s.
             *
             * @param commentLines
             *            the {@link CommentLine}s to add
             * @return this {@link Builder}
             */
            public Builder commentLines(Collection<CommentLine> commentLines) {
                if (this.commentLines.size() == 0 && commentLines.size() > 0) {
                    /* this was the first comment line of this block */
                    Span span = commentLines.iterator().next().getAdapter(Span.class);
                    if (span != null) {
                        start = span.getStart();
                    }
                }
                this.commentLines.addAll(commentLines);
                return this;
            }

            /**
             * Adds multiple {@link CommentLine}s.
             *
             * @param commentLines
             *            the {@link CommentLine}s to add
             * @return this {@link Builder}
             */
            public Builder commentLines(CommentLine... commentLines) {
                if (this.commentLines.size() == 0 && commentLines.length > 0) {
                    /* this was the first comment line of this block */
                    Span span = commentLines[0].getAdapter(Span.class);
                    if (span != null) {
                        start = span.getStart();
                    }
                }
                for (CommentLine commentLine : commentLines) {
                    this.commentLines.add(commentLine);
                }
                return this;
            }

            /**
             * @return a new {@link CommentLine.Builder}
             */
            public CommentLine.Builder openCommentLine() {
                return new CommentLine.Builder(this);
            }
        }

        private final List<CommentLine> commentLines;

        CommentBlock(List<Object> adapters, List<CommentLine> commentLines) {
            super(adapters);
            this.commentLines = commentLines;
        }

        /**
         * @return an unmodifiable {@link List} of {@link CommentLine}s
         */
        public List<CommentLine> getCommentLines() {
            return commentLines;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (CommentLine commentLine : commentLines) {
                sb.append(commentLine).append('\n');
            }
            return sb.toString();
        }

    }

    /**
     * A collection of {@link CommentBlock}s.
     */
    public static class CommentBlocks extends Adaptable {

        /**
         * A {@link CommentBlocks} builder.
         */
        public static class Builder extends Adaptable.Builder<Builder> {
            private List<CommentBlock> commentBlocks = new ArrayList<>();

            /**
             * @return new {@link CommentBlocks}
             */
            public CommentBlocks build() {
                return new CommentBlocks(sealAdapters(), commentBlocks);
            }

            /**
             * Adds a single {@link CommentBlock}.
             *
             * @param commentBlock
             *            the {@link CommentBlock} to add
             * @return this {@link Builder}
             */
            public Builder commentBlock(CommentBlock commentBlock) {
                this.commentBlocks.add(commentBlock);
                return this;
            }

            /**
             * Adds multiple {@link CommentBlock}s.
             *
             * @param commentBlocks
             *            the {@link CommentBlock}s to add
             * @return this {@link Builder}
             */
            public Builder commentBlocks(Collection<CommentBlock> commentBlocks) {
                this.commentBlocks.addAll(commentBlocks);
                return this;
            }

            /**
             * Adds multiple {@link CommentBlock}s.
             *
             * @param commentBlocks
             *            the {@link CommentBlock}s to add
             * @return this {@link Builder}
             */
            public Builder commentBlocks(CommentBlock... commentBlocks) {
                for (CommentBlock commentBlock : commentBlocks) {
                    this.commentBlocks.add(commentBlock);
                }
                return this;
            }

            /**
             * @return new {@link CommentBlock.Builder}
             */
            public CommentBlock.Builder openCommentBlock() {
                return new CommentBlock.Builder(this);
            }
        }

        /**
         * @return a new {@link Builder}
         */
        public static Builder builder() {
            return new Builder();
        }

        private final List<CommentBlock> commentBlocks;

        public CommentBlocks(List<Object> adapters, List<CommentBlock> commentBlocks) {
            super(adapters);
            this.commentBlocks = commentBlocks;
        }

        /**
         * @return an unmodifiable {@link List} of {@link CommentBlock}s
         */
        public List<CommentBlock> getCommentBlocks() {
            return commentBlocks;
        }
    }

    /**
     * A single comment line.
     */
    public static class CommentLine extends Adaptable {

        /**
         * A {@link CommentLine} builder.
         */
        public static class Builder extends Adaptable.Builder<Builder> {
            private final CommentBlock.Builder parentBuilder;
            private String text;

            Builder(CommentBlock.Builder parentBuilder) {
                super();
                this.parentBuilder = parentBuilder;
            }

            /**
             * @return a new {@link CommentLine}
             */
            public CommentLine build() {
                return new CommentLine(sealAdapters(), text);
            }

            /**
             * Calls {@link #build()}, passes the result to {@link #parentBuilder} and returns the
             * {@link #parentBuilder}
             *
             * @return {@link #parentBuilder}
             */
            public CommentBlock.Builder closeComment() {
                return parentBuilder.commentLine(build());
            }

            /**
             * @param text
             *            the comment text
             * @return this {@link Builder}
             */
            public Builder text(String text) {
                this.text = text;
                return this;
            }
        }

        private final String text;

        CommentLine(List<Object> adapters, String text) {
            super(adapters);
            this.text = text;
        }

        /**
         * @return the text of the comment
         */
        public String getText() {
            return text;
        }

        /** {@inheritDoc} */
        @Override
        public String toString() {
            return text;
        }

    }

}
