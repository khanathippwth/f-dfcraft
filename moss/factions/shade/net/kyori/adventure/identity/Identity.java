/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.identity;

import java.util.Locale;
import java.util.UUID;
import java.util.stream.Stream;
import moss.factions.shade.net.kyori.adventure.identity.Identified;
import moss.factions.shade.net.kyori.adventure.identity.IdentityImpl;
import moss.factions.shade.net.kyori.adventure.identity.NilIdentity;
import moss.factions.shade.net.kyori.adventure.key.Key;
import moss.factions.shade.net.kyori.adventure.pointer.Pointer;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.examination.Examinable;
import moss.factions.shade.net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;

public interface Identity
extends Examinable,
Identified {
    public static final Pointer<String> NAME = Pointer.pointer(String.class, Key.key("adventure", "name"));
    public static final Pointer<UUID> UUID = Pointer.pointer(UUID.class, Key.key("adventure", "uuid"));
    public static final Pointer<Component> DISPLAY_NAME = Pointer.pointer(Component.class, Key.key("adventure", "display_name"));
    public static final Pointer<Locale> LOCALE = Pointer.pointer(Locale.class, Key.key("adventure", "locale"));

    @NotNull
    public static Identity nil() {
        return NilIdentity.INSTANCE;
    }

    @NotNull
    public static Identity identity(@NotNull UUID uuid) {
        if (uuid.equals(NilIdentity.NIL_UUID)) {
            return NilIdentity.INSTANCE;
        }
        return new IdentityImpl(uuid);
    }

    @NotNull
    public UUID uuid();

    @Override
    @NotNull
    default public Identity identity() {
        return this;
    }

    @Override
    @NotNull
    default public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("uuid", this.uuid()));
    }
}

