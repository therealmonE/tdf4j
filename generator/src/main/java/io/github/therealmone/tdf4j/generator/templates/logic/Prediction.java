/*
 * Copyright 2019 Roman Fatnev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.therealmone.tdf4j.generator.templates.logic;

import io.github.therealmone.tdf4j.model.ebnf.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Prediction {

    @Nonnull
    protected List<String> getStartElements(@Nullable final Element element) {
        if(element == null) {
            return Collections.emptyList();
        }

        switch (element.kind()) {
            case REPEAT:
                return element.asRepeat().elements().length == 0
                        ? Collections.emptyList()
                        : getStartElements(firstNotInlineElement(element.asRepeat().elements()));

            case REPETITION:
                return element.asRepetition().element() == null
                        ? Collections.emptyList()
                        : getStartElements(firstNotInlineElement(element.asRepetition().element()));

            case OR:
                return new ArrayList<>() {{
                    addAll(getStartElements(firstNotInlineElement(element.asOr().first())));
                    addAll(getStartElements(firstNotInlineElement(element.asOr().second())));
                }};

            case GROUP:
                return element.asGroup().elements().length == 0
                        ? Collections.emptyList()
                        : getStartElements(firstNotInlineElement(element.asGroup().elements()));

            case OPTIONAL:
                return element.asOptional().elements().length == 0
                        ? Collections.emptyList()
                        : getStartElements(firstNotInlineElement(element.asOptional().elements()));

            case TERMINAL_TAG:
                return new ArrayList<>() {{add(element.asTerminalTag().value());}};

            case NON_TERMINAL:
                return new ArrayList<>() {{add(element.asNonTerminal().identifier());}};

            default: return Collections.emptyList();
    }
    }

    @Nullable
    protected Element firstNotInlineElement(final Element... elements) {
        for(final Element element : elements) {
            if(element == null || element.isInlineAction()) {
                continue;
            }
            return element;
        }
        return null;
    }
}
