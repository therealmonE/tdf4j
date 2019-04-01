package io.github.therealmone.tdf4j.generator;

import io.github.therealmone.tdf4j.parser.Parser;
import io.github.therealmone.tdf4j.parser.config.AbstractParserModule;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrParsingTest extends ParserTest {

    /**
     * prod1 :+ A | B | C
     */
    @Test
    public void nested() {
        final Parser parser = generate(new AbstractParserModule() {
            @Override
            public void configure() {
                prod("prod1")
                        .then(or(
                                t("A"),
                                or(t("B"), t("C"))
                        ));
            }
        });
        assertNotNull(parse(parser, "A"));
        assertNotNull(parse(parser, "B"));
        assertNotNull(parse(parser, "C"));
        assertParserFails(parser, "", unexpectedEOF());
    }

    /**
     * prod1 := [A | B], C
     */
    @Test
    public void with_optional() {
        final Parser parser = generate(new AbstractParserModule() {
            @Override
            public void configure() {
                prod("prod1")
                        .then(optional(or(t("A"), t("B"))))
                        .then(t("C"));
            }
        });
        assertNotNull(parse(parser, "AC"));
        assertNotNull(parse(parser, "BC"));
        assertNotNull(parse(parser, "C"));
        assertParserFails(parser, "ABC", unexpectedToken(TestTerminal.B));
        assertParserFails(parser, "AB", unexpectedToken(TestTerminal.B));
        assertParserFails(parser, "BAC", unexpectedToken(TestTerminal.A));
        assertParserFails(parser, "BA", unexpectedToken(TestTerminal.A));
        assertParserFails(parser, "", unexpectedEOF());
    }

    /**
     * prod1 := {A | B}, C
     */
    @Test
    public void with_repeat() {
        final Parser parser = generate(new AbstractParserModule() {
            @Override
            public void configure() {
                prod("prod1")
                        .then(repeat(or(t("A"), t("B"))))
                        .then(t("C"));
            }
        });
        assertNotNull(parse(parser, "ABC"));
        assertNotNull(parse(parser, "BC"));
        assertNotNull(parse(parser, "AC"));
        assertNotNull(parse(parser, "AAAC"));
        assertNotNull(parse(parser, "BBBC"));
        assertNotNull(parse(parser, "AABBC"));
        assertNotNull(parse(parser, "C"));
        assertParserFails(parser, "", unexpectedEOF());
    }

    /**
     * prod1 := (A, B) | C
     */
    @Test
    public void with_group() {
        final Parser parser = generate(new AbstractParserModule() {
            @Override
            public void configure() {
                prod("prod1")
                        .then(or(
                                group(t("A"), t("B")),
                                t("C")
                        ));
            }
        });
        assertNotNull(parse(parser, "AB"));
        assertNotNull(parse(parser, "C"));
        assertParserFails(parser, "AC", unexpectedToken(TestTerminal.A));
        assertParserFails(parser, "B", unexpectedToken(TestTerminal.B));
        assertParserFails(parser, "", unexpectedEOF());
    }
}