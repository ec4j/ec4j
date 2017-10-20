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