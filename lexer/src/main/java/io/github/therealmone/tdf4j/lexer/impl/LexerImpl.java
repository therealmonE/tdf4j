package io.github.therealmone.tdf4j.lexer.impl;

import io.github.therealmone.tdf4j.commons.Stream;
import io.github.therealmone.tdf4j.commons.model.ebnf.Terminal;
import io.github.therealmone.tdf4j.commons.Token;
import io.github.therealmone.tdf4j.lexer.SymbolListener;
import io.github.therealmone.tdf4j.lexer.UnexpectedSymbolException;
import io.github.therealmone.tdf4j.lexer.config.AbstractLexerModule;
import io.github.therealmone.tdf4j.lexer.Lexer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class LexerImpl implements Lexer {
    private final List<Terminal> terminals;
    private final SymbolListener listener;

    public LexerImpl(final AbstractLexerModule config, final SymbolListener listener) {
        this.terminals = config.getTerminals();
        this.listener = listener;
    }

    @Override
    @Nonnull
    public List<Token> analyze(final String input) {
        final List<Token> tokens = new ArrayList<>();
        final Stream<Token> stream = stream(input);
        stream.forEach(tokens::add);
        return tokens;
    }

    @Nonnull
    @Override
    public Stream<Token> stream(final String input) {
        listener.reset();
        final StringBuilder in = new StringBuilder(input);
        return new Stream.Builder<Token>().generator(() -> nextToken(in)).build();
    }

    @Nullable
    private Token nextToken(final StringBuilder in) {
        final StringBuilder buffer = new StringBuilder();
        while(in.length() != 0) {
            trim(in);
            buffer.append(in.charAt(buffer.length()));
            if(!anyTerminalHitEnd(buffer) || buffer.length() == in.length()) {
                removeLast(buffer, in);
                final Terminal terminal = tryToSpecifyTerminal(buffer);
                if(terminal != null) {
                    return tokenFrom(terminal, buffer, in);
                } else {
                    throw exception(buffer, in);
                }
            }
        }
        return null;
    }

    private Token tokenFrom(final Terminal terminal, final StringBuilder buffer, final StringBuilder in) {
        final Token token = new Token.Builder()
                .tag(terminal.tag().value())
                .value(buffer.toString())
                .build();
        buffer.chars().forEach(ch -> listener.listen((char) ch));
        in.replace(0, buffer.length(), "");
        buffer.replace(0, buffer.length(), "");
        return token;
    }

    private UnexpectedSymbolException exception(final StringBuilder buffer, final StringBuilder in) {
        if(buffer.length() != 0) {
            buffer.chars().forEach(ch -> listener.listen((char) ch));
        } else {
            listener.listen(in.charAt(0));
        }
        return new UnexpectedSymbolException(in.charAt(buffer.length() == 0 ? buffer.length() : buffer.length() - 1), listener.line(), listener.column());
    }

    private void removeLast(final StringBuilder buffer, final StringBuilder in) {
        if(buffer.length() != in.length()) {
            buffer.deleteCharAt(buffer.length() - 1);
        } else if(tryToSpecifyTerminal(buffer) == null) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
    }

    private void trim(final StringBuilder builder) {
        trimLeading(builder);
        trimFollowing(builder);
    }

    private void trimLeading(final StringBuilder builder) {
        while(builder.length() > 0 && spaceOrNewLine(builder.charAt(builder.length() - 1))) {
            builder.replace(builder.length() - 1, builder.length(), "");
        }
    }

    private void trimFollowing(final StringBuilder builder) {
        while(builder.length() > 0 && spaceOrNewLine(builder.charAt(0))) {
            //tracking following spaces
            listener.listen(builder.charAt(0));
            builder.replace(0, 1, "");
        }
    }

    private boolean spaceOrNewLine(final char ch) {
        return ch == ' ' || ch == '\n';
    }

    private boolean anyTerminalHitEnd(final StringBuilder buffer) {
        for(final Terminal terminal : terminals) {
            final Matcher matcher = terminal.pattern().matcher(buffer);
            if(matcher.matches() || matcher.hitEnd()) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private Terminal tryToSpecifyTerminal(final StringBuilder buffer) {
        final List<Terminal> terminals = this.terminals.stream()
                .filter(terminal -> terminal.pattern().matcher(buffer).matches())
                .sorted(Comparator.comparingLong(Terminal::priority))
                .collect(Collectors.toList());
        return terminals.size() > 0
                ? terminals.get(terminals.size() - 1)
                : null;
    }
}
