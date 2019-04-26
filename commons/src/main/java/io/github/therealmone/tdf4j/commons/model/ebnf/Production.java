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
package io.github.therealmone.tdf4j.commons.model.ebnf;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class Production {

    public abstract NonTerminal identifier();

    public abstract List<Element> elements();

    public static class Builder extends ImmutableProduction.Builder {
        public Builder then(final Element element) {
            return super.addElements(element);
        }

        public Builder is(final Element ... elements) {
            return super.addElements(elements);
        }

        public Builder identifier(final String identifier) {
            return super.identifier(new NonTerminal.Builder().identifier(identifier).build());
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(identifier()).append(" := ");
        if(elements().size() > 0) {
            for (final Element element : elements()) {
                builder.append(element.toString()).append(",");
            }
        }
        builder.replace(builder.length() - 1, builder.length(), "");
        return builder.toString();
    }
}
