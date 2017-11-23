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
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import org.ec4j.core.PropertyTypeRegistry;
import org.ec4j.core.Resource;
import org.ec4j.core.Resource.Resources;
import org.ec4j.core.model.Comments.CommentBlock;
import org.ec4j.core.model.Comments.CommentBlocks;
import org.ec4j.core.model.EditorConfig;
import org.ec4j.core.model.Section;
import org.ec4j.core.model.Version;
import org.junit.Assert;
import org.junit.Test;

public class LocationAwareModelHandlerTest {

    @Test
    public void locations() throws IOException {
        EditorConfig config = parse(Resources.ofClassPath(getClass().getClassLoader(), "/location-aware/.editorconfig",
                StandardCharsets.UTF_8));

        CommentBlocks commentBlocks = config.getAdapter(CommentBlocks.class);
        Assert.assertNotNull(commentBlocks);
        Assert.assertEquals(12, commentBlocks.getCommentBlocks().size());

        CommentBlock block0 = commentBlocks.getCommentBlocks().get(0);
        Span block0Span = block0.getAdapter(Span.class);
        Assert.assertNotNull("CommentBlock should have a span", block0Span);
        Assert.assertEquals(new Span(new Location(0, 1, 1), new Location(639, 16, 2)), block0Span);

        CommentBlock block1 = commentBlocks.getCommentBlocks().get(1);
        Span block1Span = block1.getAdapter(Span.class);
        Assert.assertNotNull("CommentBlock should have a span", block1Span);
        Assert.assertEquals(new Span(new Location(643, 20, 1), new Location(692, 21, 25)), block1Span);

        List<Section> sections = config.getSections();
        Assert.assertEquals(3, sections.size());

        Iterator<Section> it = sections.iterator();
        Section section = it.next();
        // TODO Assert.assertEquals(Span.parse(""), section.getAdapter(Span.class));
    }

    private EditorConfig parse(Resource file) throws IOException {
        ErrorHandler errorHandler = ErrorHandler.THROWING;
        EditorConfigModelHandler handler = new LocationAwareModelHandler(PropertyTypeRegistry.default_(),
                Version.CURRENT, errorHandler);
        EditorConfigParser parser = EditorConfigParser.default_();
        parser.parse(file, handler, errorHandler);
        return handler.getEditorConfig();
    }

}
