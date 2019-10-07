/*
 * Copyright (c) 2019 tdf4j
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
package io.github.tdf4j.core.model.ast;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Value.Modifiable
public abstract class ASTRoot implements ASTElement {

    @Override
    public ASTKind kind() {
        return ASTKind.ROOT;
    }

    public abstract List<ASTElement> getChildren();

    public abstract String getTag();

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getTag());
        for(final ASTElement child : getChildren()) {
            builder.append("\n|\n")
                    .append("|--")
                    .append(child.toString());
        }
        return builder.toString();
    }
}