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
package org.eclipse.ec4j.parser.handlers;

import org.eclipse.ec4j.parser.EditorConfigParser;
import org.eclipse.ec4j.parser.Location;

public abstract class AbstractEditorConfigHandler<Section, Option> implements IEditorConfigHandler<Section, Option> {

	private EditorConfigParser<Section, Option> parser;

	@Override
	public void setParser(EditorConfigParser<Section, Option> parser) {
		this.parser = parser;
	}

	/**
	 * Returns the current parser location.
	 *
	 * @return the current parser location
	 */
	protected Location getLocation() {
		return parser.getLocation();
	}

}
