/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.Server
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.platform.bukkit;

import java.util.Collection;
import moss.factions.shade.net.kyori.adventure.platform.bukkit.CraftBukkitFacet;
import moss.factions.shade.net.kyori.adventure.platform.bukkit.MinecraftReflection;
import moss.factions.shade.net.kyori.adventure.platform.bukkit.SpigotFacet;
import moss.factions.shade.net.kyori.adventure.platform.facet.Facet;
import moss.factions.shade.net.kyori.adventure.platform.facet.FacetComponentFlattener;
import moss.factions.shade.net.kyori.adventure.text.flattener.ComponentFlattener;
import moss.factions.shade.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import moss.factions.shade.net.kyori.adventure.text.serializer.gson.legacyimpl.NBTLegacyHoverEventSerializer;
import moss.factions.shade.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;

public final class BukkitComponentSerializer {
    private static final boolean IS_1_16 = MinecraftReflection.findEnum(Material.class, "NETHERITE_PICKAXE") != null;
    private static final Collection<FacetComponentFlattener.Translator<Server>> TRANSLATORS = Facet.of(SpigotFacet.Translator::new, CraftBukkitFacet.Translator::new);
    private static final LegacyComponentSerializer LEGACY_SERIALIZER;
    private static final GsonComponentSerializer GSON_SERIALIZER;
    static final ComponentFlattener FLATTENER;

    private BukkitComponentSerializer() {
    }

    @NotNull
    public static LegacyComponentSerializer legacy() {
        return LEGACY_SERIALIZER;
    }

    @NotNull
    public static GsonComponentSerializer gson() {
        return GSON_SERIALIZER;
    }

    static {
        FLATTENER = FacetComponentFlattener.get(Bukkit.getServer(), TRANSLATORS);
        if (IS_1_16) {
            LEGACY_SERIALIZER = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().flattener(FLATTENER).build();
            GSON_SERIALIZER = GsonComponentSerializer.builder().legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get()).build();
        } else {
            LEGACY_SERIALIZER = LegacyComponentSerializer.builder().character('\u00a7').flattener(FLATTENER).build();
            GSON_SERIALIZER = GsonComponentSerializer.builder().legacyHoverEventSerializer(NBTLegacyHoverEventSerializer.get()).emitLegacyHoverEvent().downsampleColors().build();
        }
    }
}

