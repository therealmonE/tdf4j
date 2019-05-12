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
package io.github.therealmone.tdf4j.parser.model.ast;

import io.github.therealmone.tdf4j.commons.Token;
import org.immutables.value.Value;

@Value.Immutable
@Value.Modifiable
public abstract class ASTLeaf implements ASTElement{

    @Override
    public Kind kind() {
        return Kind.LEAF;
    }

    public abstract ASTElement parent();

    public abstract Token token();

    @Override
    public String toString() {
        return token().toString();
    }
}