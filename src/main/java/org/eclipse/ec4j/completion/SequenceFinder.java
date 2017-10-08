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
package org.eclipse.ec4j.completion;

import static java.lang.Character.isLetter;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;
import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Copied from
 * https://github.com/eclipse/recommenders/blob/master/plugins/org.eclipse.recommenders.subwords.rcp/src/org/eclipse/recommenders/internal/subwords/rcp/SequenceFinder.java
 *
 */
class SequenceFinder {

	private static final int[] EMPTY_SEQUENCE = new int[0];

	private List<int[]> curSequences = new LinkedList<int[]>(); // Lists.newLinkedList();
	private List<int[]> nextSequences = new LinkedList<int[]>(); // Lists.newLinkedList();

	private int pCompletion, pToken;
	private String completion, token;

	public SequenceFinder(String completion, String token) {
		this.completion = completion;
		this.token = token;
	}

	public List<int[]> findSeqeuences() {

		if (isConstantName(completion)) {
			rewriteCompletion();
		}

		int[] start = EMPTY_SEQUENCE;
		curSequences.add(start);

		for (pToken = 0; pToken < token.length(); pToken++) {
			char t = token.charAt(pToken);

			for (int[] activeSequence : curSequences) {
				boolean mustmatch = false;
				int startIndex = activeSequence.length == 0 ? 0 : activeSequence[activeSequence.length - 1] + 1;

				for (pCompletion = startIndex; pCompletion < completion.length(); pCompletion++) {
					char c = completion.charAt(pCompletion);
					if (!Character.isLetter(c)) {
						if (c == t) {
							addNewSubsequenceForNext(activeSequence);
							continue;
						}
						mustmatch = true;
						continue;
					} else if (Character.isUpperCase(c)) {
						mustmatch = true;
					}

					if (mustmatch && !isSameIgnoreCase(c, t)) {
						jumpToEndOfWord();
					} else if (isSameIgnoreCase(c, t)) {
						addNewSubsequenceForNext(activeSequence);
					}
				}
			}
			curSequences = nextSequences;
			nextSequences = new LinkedList<int[]>(); // Lists.newLinkedList();
		}

		// filter
		for (Iterator<int[]> it = curSequences.iterator(); it.hasNext();) {
			int[] candidate = it.next();
			if (candidate.length < token.length()) {
				it.remove();
				continue;
			}
		}

		return curSequences;
	}

	private void addNewSubsequenceForNext(int[] activeSequence) {
		int[] copy = Arrays.copyOf(activeSequence, activeSequence.length + 1);
		copy[pToken] = pCompletion;
		nextSequences.add(copy);
	}

	private void rewriteCompletion() {
		StringBuilder sb = new StringBuilder();

		boolean toUpperCase = false;
		for (char c : completion.toCharArray()) {
			if (Character.isLetterOrDigit(c)) {
				sb.append(toUpperCase ? Character.toUpperCase(c) : Character.toLowerCase(c));
				toUpperCase = false;
			} else {
				sb.append(c);
				toUpperCase = true;
			}
		}
		completion = sb.toString();
	}

	private void jumpToEndOfWord() {
		for (pCompletion++; pCompletion < completion.length(); pCompletion++) {
			char next = completion.charAt(pCompletion);

			if (!isLetter(next)) {
				// . or _ word boundary found:
				// XXX numbers are also considered as word boundaries then. this
				// may cause some trouble...
				break;
			}

			if (isUpperCase(next)) {
				pCompletion--;
				break;
			}
		}
	}

	private boolean isConstantName(String completion) {
		for (char c : completion.toCharArray()) {
			if (Character.isLetter(c) && Character.isLowerCase(c)) {
				return false;
			}
		}
		return true;
	}

	private boolean isSameIgnoreCase(char c1, char c2) {
		if (c1 == c2) {
			return true;
		}
		c2 = isLowerCase(c2) ? toUpperCase(c2) : toLowerCase(c2);
		return c1 == c2;
	}

}
