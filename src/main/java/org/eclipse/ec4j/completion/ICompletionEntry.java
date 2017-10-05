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
package org.eclipse.ec4j.completion;

import org.eclipse.ec4j.model.optiontypes.OptionType;

public interface ICompletionEntry {

	String getName();
	
	void setContextType(CompletionContextType type);

	void setOptionType(OptionType<?> optionType);
	
	void setMatcher(ICompletionEntryMatcher matcher);
	
	void setInitialOffset(int offset);

	boolean updatePrefix(String prefix);
}
