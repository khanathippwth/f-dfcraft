/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.KeybindComponent;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.Emitable;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.Nullable;

final class KeybindTag {
    public static final String KEYBIND = "key";
    static final TagResolver RESOLVER = SerializableResolver.claimingComponent("key", KeybindTag::create, KeybindTag::emit);

    private KeybindTag() {
    }

    static Tag create(ArgumentQueue argumentQueue, Context context) {
        return Tag.inserting(Component.keybind(argumentQueue.popOr("A keybind id is required").value()));
    }

    @Nullable
    static Emitable emit(Component component) {
        if (!(component instanceof KeybindComponent)) {
            return null;
        }
        String string = ((KeybindComponent)component).keybind();
        return tokenEmitter -> tokenEmitter.tag(KEYBIND).argument(string);
    }
}

