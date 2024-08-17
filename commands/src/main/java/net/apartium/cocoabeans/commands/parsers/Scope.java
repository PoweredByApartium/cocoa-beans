package net.apartium.cocoabeans.commands.parsers;

public enum Scope {
    VARIANT,
    CLASS,
    ALL;

    public boolean isClass() {
        return this == CLASS || this == ALL;
    }

    public boolean isVariant() {
        return this == VARIANT || this == ALL;
    }

}
