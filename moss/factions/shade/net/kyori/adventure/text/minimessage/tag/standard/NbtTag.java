/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import moss.factions.shade.net.kyori.adventure.key.Key;
import moss.factions.shade.net.kyori.adventure.text.BlockNBTComponent;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.EntityNBTComponent;
import moss.factions.shade.net.kyori.adventure.text.NBTComponent;
import moss.factions.shade.net.kyori.adventure.text.NBTComponentBuilder;
import moss.factions.shade.net.kyori.adventure.text.StorageNBTComponent;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.Emitable;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.Nullable;

final class NbtTag {
    private static final String NBT = "nbt";
    private static final String DATA = "data";
    private static final String BLOCK = "block";
    private static final String ENTITY = "entity";
    private static final String STORAGE = "storage";
    private static final String INTERPRET = "interpret";
    static final TagResolver RESOLVER = SerializableResolver.claimingComponent(StandardTags.names("nbt", "data"), NbtTag::resolve, NbtTag::emit);

    private NbtTag() {
    }

    static Tag resolve(ArgumentQueue argumentQueue, Context context) {
        NBTComponentBuilder<BlockNBTComponent, BlockNBTComponent.Builder> nBTComponentBuilder;
        String string;
        String string2 = argumentQueue.popOr("a type of block, entity, or storage is required").lowerValue();
        if (BLOCK.equals(string2)) {
            string = argumentQueue.popOr("A position is required").value();
            try {
                nBTComponentBuilder = Component.blockNBT().pos(BlockNBTComponent.Pos.fromString(string));
            } catch (IllegalArgumentException illegalArgumentException) {
                throw context.newException(illegalArgumentException.getMessage(), argumentQueue);
            }
        } else if (ENTITY.equals(string2)) {
            nBTComponentBuilder = Component.entityNBT().selector(argumentQueue.popOr("A selector is required").value());
        } else if (STORAGE.equals(string2)) {
            nBTComponentBuilder = Component.storageNBT().storage(Key.key(argumentQueue.popOr("A storage key is required").value()));
        } else {
            throw context.newException("Unknown nbt tag type '" + string2 + "'", argumentQueue);
        }
        nBTComponentBuilder.nbtPath(argumentQueue.popOr("An NBT path is required").value());
        if (argumentQueue.hasNext()) {
            string = argumentQueue.pop().value();
            if (INTERPRET.equalsIgnoreCase(string)) {
                nBTComponentBuilder.interpret(true);
            } else {
                nBTComponentBuilder.separator(context.deserialize(string));
                if (argumentQueue.hasNext() && argumentQueue.pop().value().equalsIgnoreCase(INTERPRET)) {
                    nBTComponentBuilder.interpret(true);
                }
            }
        }
        return Tag.inserting((Component)nBTComponentBuilder.build());
    }

    @Nullable
    static Emitable emit(Component component) {
        String string;
        String string2;
        if (component instanceof BlockNBTComponent) {
            string2 = BLOCK;
            string = ((BlockNBTComponent)component).pos().asString();
        } else if (component instanceof EntityNBTComponent) {
            string2 = ENTITY;
            string = ((EntityNBTComponent)component).selector();
        } else if (component instanceof StorageNBTComponent) {
            string2 = STORAGE;
            string = ((StorageNBTComponent)component).storage().asString();
        } else {
            return null;
        }
        return tokenEmitter -> {
            NBTComponent nBTComponent = (NBTComponent)component;
            tokenEmitter.tag(NBT).argument(string2).argument(string).argument(nBTComponent.nbtPath());
            if (nBTComponent.separator() != null) {
                tokenEmitter.argument(nBTComponent.separator());
            }
            if (nBTComponent.interpret()) {
                tokenEmitter.argument(INTERPRET);
            }
        };
    }
}

