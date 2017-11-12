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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ec4j.core.PropertyTypeRegistry;
import org.eclipse.ec4j.core.Resource;
import org.eclipse.ec4j.core.Resource.Resources;
import org.eclipse.ec4j.core.model.Comments.CommentBlocks;
import org.eclipse.ec4j.core.model.EditorConfig;
import org.eclipse.ec4j.core.model.Section;
import org.eclipse.ec4j.core.model.Version;
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

        List<Section> sections = config.getSections();
        Assert.assertEquals(3, sections.size());

        Iterator<Section> it = sections.iterator();
        Section section = it.next();
        // TODO Assert.assertEquals(Span.parse(""), section.getAdapter(Span.class));
    }

    private EditorConfig parse(Resource file) throws IOException {
        ErrorHandler errorHandler = ErrorHandler.THROWING;
        EditorConfigModelHandler handler = new LocationAwareModelHandler(PropertyTypeRegistry.getDefault(),
                Version.CURRENT, errorHandler);
        EditorConfigParser parser = EditorConfigParser.builder().build();
        parser.parse(file, handler, errorHandler);
        return handler.getEditorConfig();
    }

}
