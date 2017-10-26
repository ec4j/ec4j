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
public interface ICompletionEntry {

    String getName();

    void setContextType(CompletionContextType type);

    void setOptionType(OptionType<?> optionType);

    void setMatcher(ICompletionEntryMatcher matcher);

    void setInitialOffset(int offset);

    boolean updatePrefix(String prefix);
}
