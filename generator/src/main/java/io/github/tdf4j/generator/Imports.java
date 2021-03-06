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
package io.github.tdf4j.generator;

public enum Imports {
    PARSER("io.github.tdf4j.parser.*"),
    LEXER("io.github.tdf4j.lexer.*"),
    MODULE("io.github.tdf4j.core.module.*"),
    MODEL("io.github.tdf4j.core.model.*"),
    MODEL_AST("io.github.tdf4j.core.model.ast.*"),
    UTILS("io.github.tdf4j.core.utils.*"),
    JAVA_UTIL("java.util.*"),
    JAVA_UTIL_FUNCTION("java.util.function.*"),
    EBNF("io.github.tdf4j.core.model.ebnf.*"),
    EBNFBuilder("static io.github.tdf4j.core.model.ebnf.EBNFBuilder.*"),
    CURSOR_MOVEMENT("static io.github.tdf4j.core.model.ast.ASTCursor.Movement.*");

    private final String value;

    Imports(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
