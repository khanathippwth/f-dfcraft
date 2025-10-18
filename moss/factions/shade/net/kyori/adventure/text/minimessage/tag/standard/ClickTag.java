/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import moss.factions.shade.net.kyori.adventure.text.event.ClickEvent;
import moss.factions.shade.net.kyori.adventure.text.format.Style;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.QuotingOverride;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.StyleClaim;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.Nullable;

final class ClickTag {
    private static final String CLICK = "click";
    static final TagResolver RESOLVER = SerializableResolver.claimingStyle("click", ClickTag::create, StyleClaim.claim("click", Style::clickEvent, (clickEvent, tokenEmitter) -> tokenEmitter.tag(CLICK).argument(ClickEvent.Action.NAMES.key(clickEvent.action())).argument(clickEvent.value(), QuotingOverride.QUOTED)));

    private ClickTag() {
    }

    static Tag create(ArgumentQueue argumentQueue, Context context) {
        String string = argumentQueue.popOr(() -> "A click tag requires an action of one of " + ClickEvent.Action.NAMES.keys()).lowerValue();
        @Nullable ClickEvent.Action action = ClickEvent.Action.NAMES.value(string);
        if (action == null) {
            throw context.newException("Unknown click event action '" + string + "'", argumentQueue);
        }
        String string2 = argumentQueue.popOr("Click event actions require a value").value();
        return Tag.styling(ClickEvent.clickEvent(action, string2));
    }
}

