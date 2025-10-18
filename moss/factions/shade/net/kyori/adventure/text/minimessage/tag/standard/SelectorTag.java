/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.SelectorComponent;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.Emitable;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.Nullable;

final class SelectorTag {
    private static final String SEL = "sel";
    private static final String SELECTOR = "selector";
    static final TagResolver RESOLVER = SerializableResolver.claimingComponent(StandardTags.names("sel", "selector"), SelectorTag::create, SelectorTag::claim);

    private SelectorTag() {
    }

    static Tag create(ArgumentQueue argumentQueue, Context context) {
        String string = argumentQueue.popOr("A selection key is required").value();
        Component component = null;
        if (argumentQueue.hasNext()) {
            component = context.deserialize(argumentQueue.pop().value());
        }
        return Tag.inserting(Component.selector(string, component));
    }

    @Nullable
    static Emitable claim(Component component) {
        if (!(component instanceof SelectorComponent)) {
            return null;
        }
        SelectorComponent selectorComponent = (SelectorComponent)component;
        return tokenEmitter -> {
            tokenEmitter.tag(SEL);
            tokenEmitter.argument(selectorComponent.pattern());
            if (selectorComponent.separator() != null) {
                tokenEmitter.argument(selectorComponent.separator());
            }
        };
    }
}

