/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node;

import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.parser.node.ElementNode;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tree.Node;
import org.jetbrains.annotations.NotNull;

public final class RootNode
extends ElementNode
implements Node.Root {
    private final String beforePreprocessing;

    public RootNode(@NotNull String string, @NotNull String string2) {
        super(null, null, string);
        this.beforePreprocessing = string2;
    }

    @Override
    @NotNull
    public String input() {
        return this.beforePreprocessing;
    }
}

