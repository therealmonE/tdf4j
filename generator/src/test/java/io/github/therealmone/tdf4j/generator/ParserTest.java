package io.github.therealmone.tdf4j.generator;

import io.github.therealmone.tdf4j.generator.impl.LexerGenerator;
import io.github.therealmone.tdf4j.generator.impl.ParserGenerator;
import io.github.therealmone.tdf4j.lexer.Lexer;
import io.github.therealmone.tdf4j.module.lexer.AbstractLexerModule;
import io.github.therealmone.tdf4j.module.parser.AbstractParserModule;
import io.github.therealmone.tdf4j.parser.Parser;
import io.github.therealmone.tdf4j.parser.UnexpectedTokenException;
import io.github.therealmone.tdf4j.model.ast.AST;
import org.junit.BeforeClass;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ParserTest {
    static Lexer lexer;

    @BeforeClass
    public static void globalSetup() {
        lexer = new LexerGenerator(new LexerOptions.Builder()
                .setModule(new AbstractLexerModule() {
                    @Override
                    public void configure() {
                        for (final TestTerminal testLexeme : TestTerminal.values()) {
                            tokenize(testLexeme.getTerminal().getTag().getValue())
                                    .pattern(testLexeme.getTerminal().getPattern().pattern())
                                    .priority(testLexeme.getTerminal().priority());
                        }
                        tokenize("ws").pattern("\\s|\\n|\\r").priority(Integer.MAX_VALUE).hidden(true);
                    }
                })
                .build()
        ).generate();
    }

    static Parser generate(final AbstractParserModule module) {
        final long current = System.currentTimeMillis();
        final Parser parser = new ParserGenerator(new ParserOptions.Builder()
                .setPackage("io.github.therealmone.tdf4j.generator")
                .setClassName("ParserImpl_" + UUID.randomUUID().toString().replaceAll("-", ""))
                .setModule(module)
                .build()
        ).generate();
        System.out.println(parser.meta().getSourceCode());
        System.out.println(module.getGrammar().toString());
        System.out.println(module.getGrammar().getFirstSet().toString());
        System.out.println(module.getGrammar().getFollowSet().toString());
        System.out.println("Compilation time: " + (System.currentTimeMillis() - current));
        return parser;
    }

    static <T extends Parser> T generate(final AbstractParserModule module, final Class<T> interfaceToImplement) {
        final long current = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        final T parser = (T) new ParserGenerator(new ParserOptions.Builder()
                .setPackage("io.github.therealmone.tdf4j.generator")
                .setClassName("ParserImpl_" + UUID.randomUUID().toString().replaceAll("-", ""))
                .setModule(module)
                .setInterface(interfaceToImplement)
                .build()
        ).generate();
        System.out.println(parser.meta().getSourceCode());
        System.out.println(module.getGrammar().toString());
        System.out.println(module.getGrammar().getFirstSet().toString());
        System.out.println(module.getGrammar().getFollowSet().toString());
        System.out.println("Compilation time: " + (System.currentTimeMillis() - current));
        return parser;
    }

    static AST parse(final Parser parser, final String input) {
        final long current = System.currentTimeMillis();
        final AST ast = parser.parse(lexer.stream(input));
        System.out.println("Parsing time: " + (System.currentTimeMillis() - current));
        System.out.println(ast);
        return ast;
    }

    static String unexpectedToken(final TestTerminal testTerminal) {
        return unexpectedToken(testTerminal, 1, 0);
    }

    static String unexpectedToken(final TestTerminal testTerminal, final String... expected) {
        return unexpectedToken(testTerminal, 1, 0, expected);
    }

    static String unexpectedToken(final TestTerminal testTerminal, final long row, final long columt) {
        return String.format("Unexpected token: Token{tag=%1$s, value=%1$s, row=%2$d, column=%3$d}",
                testTerminal.getTerminal().getTag().getValue(),
                row, columt);
    }

    static String unexpectedToken(final TestTerminal testTerminal, final long row, final long columt, final String... expected) {
        return String.format("Unexpected token: Token{tag=%1$s, value=%1$s, row=%2$d, column=%3$d}. Expected: " + Arrays.asList(expected),
                testTerminal.getTerminal().getTag().getValue(),
                row, columt);
    }

    static String unexpectedEOF() {
        return "Unexpected end of file";
    }

    static String unexpectedEOF(final String... expected) {
        return "Unexpected end of file. Expected: " + Arrays.asList(expected);
    }

    static void assertParserFails(final Parser parser, final String input, final String message) {
        assertThrows(() -> parse(parser, input), UnexpectedTokenException.class, message);
    }

    static void assertThrows(final Callback callback, final Class<? extends Throwable> ex, final String message) {
        try {
            callback.call();
            fail("Expected exception: " + ex.getName() + ": " + message);
        } catch (Exception e) {
            assertEquals(ex.getName(), e.getClass().getName());
            assertEquals(message, e.getMessage());
        }
    }

    @FunctionalInterface
    interface Callback {
        void call();
    }
}
