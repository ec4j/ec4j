/**
 *  Copyright (c) 2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.ec4j.parser;

/**
 * An immutable object that represents a location in the parsed text.
 */
public class Location {
	/**
	 * The absolute character index, starting at 0.
	 */
	public final int offset;

	/**
	 * The line number, starting at 1.
	 */
	public final int line;

	/**
	 * The column number, starting at 1.
	 */
	public final int column;

	Location(int offset, int line, int column) {
		this.offset = offset;
		this.column = column;
		this.line = line;
	}

	@Override
	public String toString() {
		return line + ":" + column + " (" + offset + ")";
	}

	@Override
	public int hashCode() {
		return offset;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Location other = (Location) obj;
		return offset == other.offset && column == other.column && line == other.line;
	}

	public Location adjust(int increment) {
		return new Location(this.offset + increment, this.line, this.column + increment);
	}
}
