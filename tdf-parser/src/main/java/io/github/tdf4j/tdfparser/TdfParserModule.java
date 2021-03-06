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
package io.github.tdf4j.tdfparser;

import io.github.tdf4j.core.module.ParserAbstractModule;

public class TdfParserModule extends ParserAbstractModule {

    @Override
    protected void configure() {

        environment()
                .packages(
                        "io.github.tdf4j.core.module.LexerAbstractModule",
                        "io.github.tdf4j.core.module.ParserAbstractModule",
                        "io.github.tdf4j.tdfparser.constructor.*",
                        "io.github.tdf4j.tdfparser.processor.*"
                )
                .code(
                        "" +
                        "private final Processor<String> stringProcessor = new StringProcessor();\n" +
                        "private final Stack<LetterConstructor> letters = new Stack<>();\n" +
                        "private final Stack<EnvironmentConstructor> environments = new Stack<>();\n" +
                        "private final Stack<ProductionConstructor> productions = new Stack<>();\n" +
                        "private LexerAbstractModule lexerModule;\n" +
                        "private ParserAbstractModule parserModule;\n" +
                        "\n" +
                        "@Override\n" +
                        "public LexerAbstractModule getLexerModule() {\n" +
                        "   return this.lexerModule;\n" +
                        "}\n" +
                        "\n" +
                        "@Override\n" +
                        "public ParserAbstractModule getParserModule() {\n" +
                        "   return this.parserModule;\n" +
                        "}\n"
                );


        prod("tdf_lang")
                .is(
                        inline(
                                "this.lexerModule = new LexerAbstractModule() {\n" +
                                        "   @Override\n" +
                                        "   public void configure() {}\n" +
                                        "};\n" +
                                        "\n" +
                                        "this.parserModule = new ParserAbstractModule() {\n" +
                                        "   @Override\n" +
                                        "   public void configure() {}\n" +
                                        "};\n"
                        ),
                        nt("lexis"),
                        optional(nt("environment")),
                        nt("syntax"),
                        t("EOF")
                );

        prod("lexis")
                .is(
                        t("KEY_LEXIS"),
                        repeat(
                                nt("terminal_description")
                        )
                );

        prod("terminal_description")
                .is(
                        t("TERMINAL_TAG", "letters.push(new LetterConstructor(lexerModule.tokenize(token.getValue())));"),
                        t("STRING", "letters.peek().setPattern(stringProcessor.process(token.getValue()));"),
                        optional(nt("terminal_parameters")),
                        inline(
                                "letters.pop().construct();\n"
                        )
                );

        prod("terminal_parameters")
                .is(
                        t("LEFT_SQUARE_BRACKET"),
                        nt("terminal_parameters_values"),
                        t("RIGHT_SQUARE_BRACKET")
                );

        prod("terminal_parameters_values")
                .is(
                        or(
                                nt("terminal_parameter_priority"),
                                nt("terminal_parameter_hidden"),
                                nt("terminal_parameter_pattern_flag")
                        ),
                        optional(t("COMMA"), nt("terminal_parameters_values"))
                );

        prod("terminal_parameter_priority")
                .is(
                        t("TERMINAL_PARAMETER_PRIORITY"),
                        t("COLON"),
                        t("INTEGER", "letters.peek().setPriority(token.getValue());")
                );

        prod("terminal_parameter_hidden")
                .is(
                        t("TERMINAL_PARAMETER_HIDDEN"),
                        t("COLON"),
                        t("BOOLEAN", "letters.peek().setHidden(token.getValue());")
                );

        prod("terminal_parameter_pattern_flag")
                .is(
                        t("TERMINAL_PARAMETER_PATTERN_FLAG"),
                        t("COLON"),
                        nt("pattern_flags")
                );

        prod("pattern_flags")
                .is(
                        or(
                                t("TERMINAL_PARAMETER_PATTERN_FLAG_VALUE_UNIX_LINES"),
                                t("TERMINAL_PARAMETER_PATTERN_FLAG_VALUE_CASE_INSENSITIVE"),
                                t("TERMINAL_PARAMETER_PATTERN_FLAG_VALUE_COMMENTS"),
                                t("TERMINAL_PARAMETER_PATTERN_FLAG_VALUE_MULTILINE"),
                                t("TERMINAL_PARAMETER_PATTERN_FLAG_VALUE_LITERAL"),
                                t("TERMINAL_PARAMETER_PATTERN_FLAG_VALUE_DOTALL"),
                                t("TERMINAL_PARAMETER_PATTERN_FLAG_VALUE_UNICODE_CASE"),
                                t("TERMINAL_PARAMETER_PATTERN_FLAG_VALUE_CANON_EQ"),
                                t("TERMINAL_PARAMETER_PATTERN_FLAG_VALUE_UNICODE_CHARACTER_CLASS")
                        ),
                        inline(
                                "letters.peek().addFlag(ast.getLastLeaf().getToken().getValue());\n"
                        ),
                        optional(t("OP_SUM"), nt("pattern_flags"))
                );

        prod("environment")
                .is(
                        t("KEY_ENV"),
                        inline(
                                "environments.push(new EnvironmentConstructor(parserModule.environment()));\n"
                        ),
                        repeat(nt("env_import")),
                        optional(nt("env_code")),
                        inline(
                                "environments.pop().construct();\n"
                        )
                );

        prod("env_import")
                .is(
                        t("KEY_IMPORT"),
                        t("STRING", "environments.peek().addPackage(stringProcessor.process(token.getValue()));")
                );

        prod("env_code")
                .is(
                        t("KEY_CODE"),
                        t("STRING", "environments.peek().setCode(stringProcessor.process(token.getValue()));")
                );

        prod("syntax")
                .is(
                        t("KEY_SYNTAX"),
                        repeat(nt("production_description"))
                );

        prod("production_description")
                .is(
                        t("NON_TERMINAL", "productions.push(new ProductionConstructor(parserModule.prod(token.getValue())));"),
                        t("OP_ASSIGN"),
                        nt("ebnf_elements_set", "productions.peek().setElements(node);"),
                        t("DELIMITER"),
                        inline(
                                "productions.pop().construct();\n"
                        )
                );

        prod("ebnf_elements_set")
                .is(
                        nt("ebnf_element"),
                        optional(t("COMMA"), nt("ebnf_elements_set"))
                );

        prod("ebnf_element")
                .is(
                        or(
                                nt("ebnf_optional"),
                                nt("ebnf_or"),
                                nt("ebnf_repeat"),
                                nt("ebnf_repetition"),
                                nt("ebnf_group"),
                                nt("ebnf_terminal"),
                                nt("ebnf_non_terminal"),
                                nt("ebnf_inline_action")
                        )
                );

        prod("ebnf_terminal")
                .is(
                        t("TERMINAL_TAG"),
                        optional(t("LAMBDA"), t("STRING"))
                );

        prod("ebnf_non_terminal")
                .is(
                        t("NON_TERMINAL"),
                        optional(t("LAMBDA"), t("STRING"))
                );

        prod("ebnf_optional")
                .is(
                        t("LEFT_SQUARE_BRACKET"),
                        nt("ebnf_elements_set"),
                        t("RIGHT_SQUARE_BRACKET")
                );

        prod("ebnf_or")
                .is(
                        repetition(
                                group(
                                    t("LOP_OR"),
                                    nt("ebnf_element")
                                ),
                                2
                        ),
                        repeat(
                                t("LOP_OR"),
                                nt("ebnf_element")
                        )
                );

        prod("ebnf_repeat")
                .is(
                        t("LEFT_FIGURE_BRACKET"),
                        nt("ebnf_elements_set"),
                        t("RIGHT_FIGURE_BRACKET")
                );

        prod("ebnf_repetition")
                .is(
                        t("INTEGER"),
                        t("OP_MULTIPLY"),
                        nt("ebnf_element")
                );

        prod("ebnf_group")
                .is(
                        t("LEFT_BRACKET"),
                        nt("ebnf_elements_set"),
                        t("RIGHT_BRACKET")
                );

        prod("ebnf_inline_action")
                .is(
                        t("LEFT_INLINE_ACTION_BRACKET"),
                        t("STRING"),
                        t("RIGHT_INLINE_ACTION_BRACKET")
                );
    }
}
