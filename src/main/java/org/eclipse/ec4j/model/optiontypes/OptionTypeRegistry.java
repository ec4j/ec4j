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
package org.eclipse.ec4j.model.optiontypes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ec4j.model.optiontypes.OptionType.Charset;
import org.eclipse.ec4j.model.optiontypes.OptionType.EndOfLine;
import org.eclipse.ec4j.model.optiontypes.OptionType.IndentSize;
import org.eclipse.ec4j.model.optiontypes.OptionType.IndentStyle;
import org.eclipse.ec4j.model.optiontypes.OptionType.InsertFinalNewline;
import org.eclipse.ec4j.model.optiontypes.OptionType.Root;
import org.eclipse.ec4j.model.optiontypes.OptionType.TabWidth;
import org.eclipse.ec4j.model.optiontypes.OptionType.TrimTrailingWhitespace;

public class OptionTypeRegistry {

	public static final OptionTypeRegistry DEFAULT;

	static {
		DEFAULT = new OptionTypeRegistry();
		DEFAULT.register(new IndentStyle());
		DEFAULT.register(new IndentSize());
		DEFAULT.register(new TabWidth());
		DEFAULT.register(new EndOfLine());
		DEFAULT.register(new Charset());
		DEFAULT.register(new TrimTrailingWhitespace());
		DEFAULT.register(new InsertFinalNewline());
		DEFAULT.register(new Root());
	}

	private Map<String, OptionType<?>> types;

	public OptionTypeRegistry() {
		this.types = new HashMap<>();
	}

	public void register(OptionType<?> type) {
		types.put(type.getName().toUpperCase(), type);
	}

	public OptionType<?> getType(String name) {
		return types.get(name.toUpperCase());
	}

	public Collection<OptionType<?>> getTypes() {
		return types.values();
	}

}
