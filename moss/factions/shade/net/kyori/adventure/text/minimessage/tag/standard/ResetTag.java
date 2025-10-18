/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.ParserDirective;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

final class ResetTag {
    private static final String RESET = "reset";
    static final TagResolver RESOLVER = TagResolver.resolver("reset", ParserDirective.RESET);

    private ResetTag() {
    }
}

