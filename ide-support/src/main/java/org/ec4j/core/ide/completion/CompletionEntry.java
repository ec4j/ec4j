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
package org.ec4j.core.ide.completion;

import org.ec4j.core.ide.TokenContext.TokenContextType;
import org.ec4j.core.model.PropertyType;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class CompletionEntry {

    // Negative value ensures subsequence matches have a lower relevance than
    // standard JDT or template proposals
    private static final int SUBWORDS_RANGE_START = -9000;
    private static final int R_EXACT_NAME = 4;
    private static final int R_CASE = 10;
    private static final int minPrefixLengthForTypes = 1;

    private final CompletionEntryMatcher matcher;
    private int relevance;

    private final String name;
    private String prefix;
    private final int initialOffset;
    private final TokenContextType contextType;
    private final PropertyType<?> propertyType;
    public CompletionEntry(String name, CompletionEntryMatcher matcher, PropertyType<?> propertyType,
            TokenContextType contextType, int initialOffset) {
        this.name = name;
        this.matcher = matcher;
        this.propertyType = propertyType;
        this.contextType = contextType;
        this.initialOffset = initialOffset;
    }

    public String getName() {
        return name;
    }

    public CompletionEntryMatcher getMatcher() {
        return matcher;
    }

    public TokenContextType getContextType() {
        return contextType;
    }

    public PropertyType<?> getPropertyType() {
        return propertyType;
    }

    public int getInitialOffset() {
        return initialOffset;
    }

    public boolean updatePrefix(String prefix) {
        this.prefix = prefix;
        Integer relevanceBoost = null;
        int[] bestSequence = null;
        if (isEmpty(prefix)) {
            relevanceBoost = 0;
        } else {
            String name = getName();
            bestSequence = matcher.bestSubsequence(name, prefix);
            if ((bestSequence != null && bestSequence.length > 0)) {
                relevanceBoost = 0;
                if (name.equals(prefix)) {
                    if (minPrefixLengthForTypes < prefix.length()) {
                        relevanceBoost = 16 * (CompletionEntry.R_EXACT_NAME + CompletionEntry.R_CASE);
                    }
                } else if (name.equalsIgnoreCase(prefix)) {
                    if (minPrefixLengthForTypes < prefix.length()) {
                        relevanceBoost = 16 * CompletionEntry.R_EXACT_NAME;
                    }
                } else if (startsWithIgnoreCase(prefix, name)) {
                    // Don't adjust score
                } else {
                    int score = LCSS.scoreSubsequence(bestSequence);
                    relevanceBoost = SUBWORDS_RANGE_START + score;
                }
            }
        }
        if (relevanceBoost != null) {
            relevance = relevanceBoost;
            return true;
        }
        return false;
    }

    private boolean startsWithIgnoreCase(String prefix, String name) {
        return prefix.toUpperCase().startsWith(name.toUpperCase());
    }

    public int getRelevance() {
        return relevance;
    }

    private static boolean isEmpty(String prefix) {
        return prefix == null || prefix.length() == 0;
    }

    public String getPrefix() {
        return prefix;
    }

}
