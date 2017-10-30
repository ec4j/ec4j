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
package fr.opensagres.ec4j.services.completion;

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

    SequenceFinder(String completion, String token) {
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
