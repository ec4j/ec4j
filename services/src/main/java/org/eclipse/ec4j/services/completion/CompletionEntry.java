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
package org.eclipse.ec4j.services.completion;

import org.eclipse.ec4j.core.model.optiontypes.OptionType;

/**
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public class CompletionEntry implements ICompletionEntry {

    // Negative value ensures subsequence matches have a lower relevance than
    // standard JDT or template proposals
    private static final int SUBWORDS_RANGE_START = -9000;
    private static final int minPrefixLengthForTypes = 1;

    private ICompletionEntryMatcher matcher;
    private int relevance;

    private final String name;
    private String prefix;
    private int initialOffset;
    private CompletionContextType contextType;
    private OptionType<?> optionType;

    public CompletionEntry(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public ICompletionEntryMatcher getMatcher() {
        return matcher;
    }

    @Override
    public void setMatcher(ICompletionEntryMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public void setContextType(CompletionContextType contextType) {
        this.contextType = contextType;
    }

    public CompletionContextType getContextType() {
        return contextType;
    }

    @Override
    public void setOptionType(OptionType<?> optionType) {
        this.optionType = optionType;
    }

    public OptionType<?> getOptionType() {
        return optionType;
    }

    @Override
    public void setInitialOffset(int initialOffset) {
        this.initialOffset = initialOffset;
    }

    public int getInitialOffset() {
        return initialOffset;
    }

    @Override
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
                        relevanceBoost = 16 * (RelevanceConstants.R_EXACT_NAME + RelevanceConstants.R_CASE);
                    }
                } else if (name.equalsIgnoreCase(prefix)) {
                    if (minPrefixLengthForTypes < prefix.length()) {
                        relevanceBoost = 16 * RelevanceConstants.R_EXACT_NAME;
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
