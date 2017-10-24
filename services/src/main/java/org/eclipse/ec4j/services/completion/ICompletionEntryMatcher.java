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

/**
 * Matcher for completion entry.
 *
 * @author <a href="mailto:angelo.zerr@gmail.com">Angelo Zerr</a>
 */
public interface ICompletionEntryMatcher {

    ICompletionEntryMatcher LCS = new ICompletionEntryMatcher() {

        @Override
        public int[] bestSubsequence(String completion, String token) {
            return LCSS.bestSubsequence(completion, token);
        }

    };

    ICompletionEntryMatcher START_WITH_MATCHER = new ICompletionEntryMatcher() {

        @Override
        public int[] bestSubsequence(String completion, String token) {
            if (!completion.startsWith(token)) {
                return null;
            }
            return new int[] { 0, token.length() - 1 };
        }
    };

    int[] bestSubsequence(String completion, String token);

}
