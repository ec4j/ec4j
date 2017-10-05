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
