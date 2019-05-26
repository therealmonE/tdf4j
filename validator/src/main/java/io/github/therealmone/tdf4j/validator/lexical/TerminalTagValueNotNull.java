package io.github.therealmone.tdf4j.validator.lexical;

import io.github.therealmone.tdf4j.model.ebnf.Terminal;
import io.github.therealmone.tdf4j.module.lexer.AbstractLexerModule;
import io.github.therealmone.tdf4j.validator.ValidatorException;
import io.github.therealmone.tdf4j.validator.ValidatorRule;

import static io.github.therealmone.tdf4j.validator.lexical.LexerValidatorException.LEXER_TERMINAL_TAG_VALUE_NULL;

public class TerminalTagValueNotNull implements ValidatorRule<AbstractLexerModule> {

    @Override
    public void visit(final AbstractLexerModule module) throws ValidatorException {
        for(final Terminal terminal : module.getTerminals()) {
            if(terminal.tag().value() == null) {
                throw LEXER_TERMINAL_TAG_VALUE_NULL;
            }
        }
    }

}
