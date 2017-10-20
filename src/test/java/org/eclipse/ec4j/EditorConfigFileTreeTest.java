/**
 * The MIT License
 * Copyright © 2017 Angelo Zerr and other contributors as
 * indicated by the @author tags.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.eclipse.ec4j;

import java.io.IOException;
import java.util.Collection;

import org.eclipse.ec4j.model.Option;
import org.junit.Test;

/**
 * Some glob test of
 * https://github.com/editorconfig/editorconfig-core-test/tree/master/filetree
 *
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
