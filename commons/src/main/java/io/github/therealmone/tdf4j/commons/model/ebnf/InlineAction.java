package io.github.therealmone.tdf4j.commons.model.ebnf;

import org.immutables.value.Value;

@Value.Immutable
public abstract class InlineAction {

    @Value.Default
    public String code() {
        return "";
    }

    public static class Builder extends ImmutableInlineAction.Builder {
    }

    @Override
    public String toString() {
        return code().trim().equalsIgnoreCase("")
                ? ""
                : "<<\n" + code() + "\n>>";
    }
}
