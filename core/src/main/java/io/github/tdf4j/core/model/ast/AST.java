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

import io.github.tdf4j.core.model.Token;
import io.github.tdf4j.core.model.ebnf.NonTerminal;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface AST {

    AST addNode(final ASTNode node);

    AST addNode(final String tag);

    AST addLeaf(final ASTLeaf leaf);

    AST addLeaf(final Token token);

    @Nullable
    ASTNode getLastNode();

    @Nullable
    ASTLeaf getLastLeaf();

    ASTRoot getRoot();

    ASTElement onCursor();

    AST moveCursor(final Consumer<ASTCursor> movement);

    AST moveCursor(final ASTCursor.Movement movement);

    static AST create(final String rootTag) {
        return create(ModifiableASTRoot.create().setTag(rootTag));
    }

    static AST create(final NonTerminal root) {
        return create(ModifiableASTRoot.create().setTag(root.getValue()));
    }

    static AST create(final ASTRoot root) {
        return new ASTImpl(root);
    }

}
