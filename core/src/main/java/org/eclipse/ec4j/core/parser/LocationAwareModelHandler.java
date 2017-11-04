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

import org.eclipse.ec4j.core.PropertyTypeRegistry;
import org.eclipse.ec4j.core.model.Comments.CommentBlock;
import org.eclipse.ec4j.core.model.Comments.CommentBlocks;
import org.eclipse.ec4j.core.model.Version;

/**
 * An {@link EditorConfigHandler} that stores the start and end location information to the individual model elements
 * using {@link Span} adapters and its subclasses.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class LocationAwareModelHandler extends EditorConfigModelHandler {

    private CommentBlock.Builder commentBlockBuilder;
    private CommentBlocks.Builder commentBlocksBuilder;
    private Span.Builder commentSpan;
    private Span.Builder patternSpan;

    private Span.Builder propertyNameSpan;
    private Span.Builder propertyValueSpan;
    private Span.Builder sectionSpan;

    public LocationAwareModelHandler(PropertyTypeRegistry registry, Version version) {
        super(registry, version);
    }

    @Override
    public void blankLine(ParseContext context) {
        super.blankLine(context);
        closeCommentBlockIfNeeded();
    }

    private void closeCommentBlockIfNeeded() {
        if (commentBlockBuilder != null) {
            commentBlockBuilder.closeCommentBlock();
            commentBlockBuilder = null;
        }
    }

    @Override
    public void endComment(ParseContext context, String comment) {
        if (commentBlocksBuilder == null) {
            commentBlocksBuilder = CommentBlocks.builder();
        }
        if (commentBlockBuilder == null) {
            commentBlockBuilder = commentBlocksBuilder.openCommentBlock();
        }
        commentBlockBuilder.openCommentLine().adapter(commentSpan.end(context.getLocation()).buildSpan()).text(comment)
                .closeComment();
        commentSpan = null;
        super.endComment(context, comment);
    }

    /** {@inheritDoc} */
    @Override
    public void endDocument(ParseContext context) {
        if (commentBlocksBuilder != null) {
            closeCommentBlockIfNeeded();
            editorConfigBuilder.adapter(commentBlocksBuilder.build());
            commentBlocksBuilder = null;
        }
        super.endDocument(context);
    }

    /** {@inheritDoc} */
    @Override
    public void endPattern(ParseContext context, String pattern) {
        sectionBuilder.adapter(patternSpan.end(context.getLocation()).buildPatternSpan());
        patternSpan = null;
        super.endPattern(context, pattern);
    }

    /** {@inheritDoc} */
    @Override
    public void endPropertyName(ParseContext context, String name) {
        propertyBuilder.adapter(propertyNameSpan.end(context.getLocation()).buildNameSpan());
        propertyNameSpan = null;
        super.endPropertyName(context, name);
    }

    /** {@inheritDoc} */
    @Override
    public void endPropertyValue(ParseContext context, String value) {
        propertyBuilder.adapter(propertyValueSpan.end(context.getLocation()).buildValueSpan());
        propertyValueSpan = null;
        super.endPropertyValue(context, value);
    }

    /** {@inheritDoc} */
    @Override
    public void endSection(ParseContext context) {
        sectionBuilder.adapter(sectionSpan.end(context.getLocation()).buildSpan());
        sectionSpan = null;
        super.endSection(context);
    }

    /** {@inheritDoc} */
    @Override
    public void startComment(ParseContext context) {
        super.startComment(context);
        commentSpan = Span.builder().start(context.getLocation());
    }

    /** {@inheritDoc} */
    @Override
    public void startPattern(ParseContext context) {
        super.startPattern(context);
        patternSpan = Span.builder().start(context.getLocation());
    }

    /** {@inheritDoc} */
    @Override
    public void startProperty(ParseContext context) {
        closeCommentBlockIfNeeded();
        super.startProperty(context);
    }

    /** {@inheritDoc} */
    @Override
    public void startPropertyName(ParseContext context) {
        super.startPropertyName(context);
        propertyNameSpan = Span.builder().start(context.getLocation());
    }

    /** {@inheritDoc} */
    @Override
    public void startPropertyValue(ParseContext context) {
        super.startPropertyValue(context);
        propertyValueSpan = Span.builder().start(context.getLocation());
    }

    /** {@inheritDoc} */
    @Override
    public void startSection(ParseContext context) {
        closeCommentBlockIfNeeded();
        super.startSection(context);
        sectionSpan = Span.builder().start(context.getLocation());
    }

}
