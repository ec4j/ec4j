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
package fr.opensagres.ec4j.core;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import fr.opensagres.ec4j.core.Resources.Resource;
import fr.opensagres.ec4j.core.Resources.StringResourceTree;
import fr.opensagres.ec4j.core.model.EditorConfig;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class EditorConfigTest {

    @Test
    public void load() throws IOException, EditorConfigException {
        String s = "# EditorConfig is awesome: http://EditorConfig.org\n";
        s += "\n";
        s += "# top-most EditorConfig file\n";
        s += "root = true\n";
        s += "\n";
        s += "# Unix-style newlines with a newline ending every file\n";
        s += "[*]\n";
        s += "end_of_line = lf\n";
        s += "insert_final_newline = true\n\n";
        s += "# Matches multiple files with brace expansion notation\n";
        s += "# Set default charset\n";
        s += "[*.{js,py}]\n";
        s += "charset = utf-8";

        Resource r = StringResourceTree.builder().resource(".editorconfig", s).build().getResource(".editorconfig");

        EditorConfig config = EditorConfigLoader.getDefault().load(r);
        Assert.assertEquals("root = true\n" + "\n" + "[*]\n" + "end_of_line = lf\n" + "insert_final_newline = true\n" + "\n"
                + "[*.{js,py}]\n" + "charset = utf-8", config.toString());
    }
}

