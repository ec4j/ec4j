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
package org.eclipse.ec4j.core;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.ec4j.core.model.Option;
import org.junit.Test;

/**
 * Some glob test of
 * https://github.com/editorconfig/editorconfig-core-test/tree/master/filetree
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class EditorConfigFileTreeTest {

    @Test
    public void windows_separator() throws IOException, EditorConfigException {
        String content = "; test for path separator\r\n" +
                "\r\n" +
                "root=true\r\n" +
                "\r\n" +
                "[path/separator]\r\n" +
                "key=value\r\n" +
                "\r\n" +
                "[/top/of/path]\r\n" +
                "key=value\r\n" +
                "\r\n" +
                "[windows\\separator]\r\n" +
                "key=value\r\n" +
                "\r\n" +
                "[windows\\\\separator2]\r\n" +
                "key=value\r\n" +
                "";

        TestEditorConfigManager manager = new TestEditorConfigManager();

        TestFolder root = new TestFolder("root");
        root.addFile(".editorconfig", content);
        TestFolder windows = root.addFolder("windows");
        TestFile file = windows.addFile("separator");

        Collection<Option> options = manager.getOptions(file, null);
        //Assert.assertTrue(options.isEmpty());
    }

}
