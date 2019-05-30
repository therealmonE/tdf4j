package io.github.therealmone.tdf4j.tdfparser;

import io.github.therealmone.tdf4j.commons.Environment;
import org.junit.Test;

import static org.junit.Assert.*;

public class EnvTest extends TdfParserTest {

    @Test
    public void test() {
        tdfParser.parse(tdfLexer.stream(load("EnvTest.tdf")));
        final Environment environment = tdfParser.getParserModule().build().getEnvironment();

        assertEquals(2, environment.getPackages().length);
        assertEquals("io.github.therealmone.tdf4j.model.Token", environment.getPackages()[0]);
        assertEquals("io.github.therealmone.tdf4j.module.lexer.AbstractLexerModule", environment.getPackages()[1]);
        assertEquals(0, environment.getDependencies().length);
        assertEquals("\r\n" +
                "        public String test() {\r\n" +
                "            return \"\";\r\n" +
                "        }\r\n" +
                "    ", environment.getCode());
    }

}
