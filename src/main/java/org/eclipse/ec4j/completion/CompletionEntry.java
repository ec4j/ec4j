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

import org.eclipse.ec4j.model.optiontypes.OptionType;

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
