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
import java.util.List;

import org.ec4j.core.PropertyTypeRegistry;
import org.ec4j.core.Resource;
import org.ec4j.core.Resource.Resources;
import org.ec4j.core.model.Comments.CommentBlock;
import org.ec4j.core.model.Comments.CommentBlocks;
import org.ec4j.core.model.EditorConfig;
import org.ec4j.core.model.Property;
import org.ec4j.core.model.Section;
import org.ec4j.core.model.Version;
import org.ec4j.core.parser.Span.GlobSpan;
import org.ec4j.core.parser.Span.NameSpan;
import org.ec4j.core.parser.Span.ValueSpan;
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

        /* Check that sections and properties have their parent model objects set */
        for (Section section : sections) {
            EditorConfig parent = section.getAdapter(EditorConfig.class);
            Assert.assertNotNull(parent);
            Assert.assertEquals(config, parent);
            for (Property property : section.getProperties().values()) {
                Section propParent = property.getAdapter(Section.class);
                Assert.assertNotNull(propParent);
                Assert.assertEquals(section, propParent);
            }
        }

        int i = 0;
        {
            final Section section = sections.get(i++);
            Span sectionSpan = section.getAdapter(Span.class);
            Assert.assertNotNull(sectionSpan);
            Assert.assertEquals(Span.parse("29:1 (803) - 32:15 (875)"), sectionSpan);

            Span globSpan = section.getAdapter(GlobSpan.class);
            Assert.assertNotNull(globSpan);
            Assert.assertEquals(GlobSpan.parse("29:2 (804) - 29:5 (807)"), globSpan);

            Property prop = section.getProperties().values().iterator().next();

            Span propSpan = prop.getAdapter(Span.class);
            Assert.assertNotNull(propSpan);
            Assert.assertEquals(Span.parse("32:1 (861) - 32:15 (875)"), propSpan);

            Span nameSpan = prop.getAdapter(NameSpan.class);
            Assert.assertNotNull(nameSpan);
            Assert.assertEquals(NameSpan.parse("32:1 (861) - 32:8 (868)"), nameSpan);

            Span valSpan = prop.getAdapter(ValueSpan.class);
            Assert.assertNotNull(valSpan);
            Assert.assertEquals(ValueSpan.parse("32:9 (869) - 32:15 (875)"), valSpan);
        }
        {
            final Section section = sections.get(i++);
            Span sectionSpan = section.getAdapter(Span.class);
            Assert.assertNotNull(sectionSpan);
            Assert.assertEquals(Span.parse("38:1 (979) - 47:15 (1169)"), sectionSpan);

            Span globSpan = section.getAdapter(GlobSpan.class);
            Assert.assertNotNull(globSpan);
            Assert.assertEquals(GlobSpan.parse("38:2 (980) - 38:5 (983)"), globSpan);
        }
        {
            final Section section = sections.get(i++);
            Span sectionSpan = section.getAdapter(Span.class);
            Assert.assertNotNull(sectionSpan);
            Assert.assertEquals(Span.parse("52:1 (1222) - 53:12 (1239)"), sectionSpan);

            Span globSpan = section.getAdapter(GlobSpan.class);
            Assert.assertNotNull(globSpan);
            Assert.assertEquals(GlobSpan.parse("52:2 (1223) - 52:5 (1226)"), globSpan);
        }
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
