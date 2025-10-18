/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node;

import java.util.Objects;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.Token;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.ElementNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ValueNode
extends ElementNode {
    private final String value;

    ValueNode(@Nullable ElementNode elementNode, @Nullable Token token, @NotNull String string, @NotNull String string2) {
        super(elementNode, token, string);
        this.value = string2;
    }

    abstract String valueName();

    @NotNull
    public String value() {
        return this.value;
    }

    @Override
    @NotNull
    public Token token() {
        return Objects.requireNonNull(super.token(), "token is not set");
    }

    @Override
    @NotNull
    public StringBuilder buildToString(@NotNull StringBuilder stringBuilder, int n) {
        char[] cArray = this.ident(n);
        stringBuilder.append(cArray).append(this.valueName()).append("('").append(this.value).append("')\n");
        return stringBuilder;
    }
}

