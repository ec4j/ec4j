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

import java.util.List;

/**
 * Copied from
 * https://github.com/eclipse/recommenders/blob/master/plugins/org.eclipse.recommenders.subwords.rcp/src/org/eclipse/recommenders/internal/subwords/rcp/LCSS.java
 */
public final class LCSS {

    private LCSS() {
        // Not meant to be instantiated
    }

    private static final int[] EMPTY_SEQUENCE = new int[0];

    /**
     * Returns the best, i.e, the longest continuous sequence - or the empty
     * sequence if no subsequence could be found.
     */
    public static int[] bestSubsequence(String completion, String token) {
        int bestScore = -1;
        int[] bestSequence = EMPTY_SEQUENCE;
        for (int[] s1 : findSequences(completion, token)) {
            int score = scoreSubsequence(s1);
            if (score > bestScore) {
                bestScore = score;
                bestSequence = s1;
            }
        }
        return bestSequence;
    }

    public static int scoreSubsequence(int[] s1) {
        int score = 0;
        for (int i = 0; i < s1.length - 1; i++) {
            if (s1[i] + 1 == s1[i + 1]) {
                score++;
            }
        }
        return score;
    }

    public static List<int[]> findSequences(String completion, String token) {
        return new SequenceFinder(completion, token).findSeqeuences();
    }

    public static boolean containsSubsequence(String completion, String token) {
        return !findSequences(completion, token).isEmpty();
    }
}