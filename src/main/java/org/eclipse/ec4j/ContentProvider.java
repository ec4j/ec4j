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
package org.eclipse.ec4j;

/**
 * Content provider API.
 *
 * @param <T>
 */
public interface ContentProvider<T> {

	public static final ContentProvider<String> STRING_CONTENT_PROVIDER = new ContentProvider<String>() {

		@Override
		public char getChar(String document, int index) throws Exception {
			return document.charAt(index);
		}

		@Override
		public int getLength(String document) {
			return document.length();
		}

	};

	/**
	 * Returns the character at the given document offset in this document.
	 *
	 * @param document
	 *            the document
	 * @param offset
	 *            a document offset
	 * @return the character at the offset
	 * @exception if
	 *                the offset is invalid in this document for instance
	 */
	char getChar(T document, int offset) throws Exception;

	int getLength(T document);

}