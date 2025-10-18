/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import moss.factions.shade.net.kyori.adventure.key.InvalidKeyException;
import moss.factions.shade.net.kyori.adventure.key.Key;
import moss.factions.shade.net.kyori.adventure.key.Keyed;
import moss.factions.shade.net.kyori.adventure.nbt.api.BinaryTagHolder;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.event.DataComponentValue;
import moss.factions.shade.net.kyori.adventure.text.event.HoverEvent;
import moss.factions.shade.net.kyori.adventure.text.format.Style;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.ParsingException;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.StyleClaim;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class HoverTag {
    private static final String HOVER = "hover";
    static final TagResolver RESOLVER = SerializableResolver.claimingStyle("hover", HoverTag::create, StyleClaim.claim("hover", Style::hoverEvent, HoverTag::emit));

    private HoverTag() {
    }

    static Tag create(ArgumentQueue argumentQueue, Context context) {
        String string = argumentQueue.popOr("Hover event requires an action as its first argument").value();
        HoverEvent.Action<?> action = HoverEvent.Action.NAMES.value(string);
        ActionHandler<?> actionHandler = HoverTag.actionHandler(action);
        if (actionHandler == null) {
            throw context.newException("Don't know how to turn '" + argumentQueue + "' into a hover event", argumentQueue);
        }
        return Tag.styling(HoverEvent.hoverEvent(action, actionHandler.parse(argumentQueue, context)));
    }

    static void emit(HoverEvent<?> hoverEvent, TokenEmitter tokenEmitter) {
        ActionHandler<?> actionHandler = HoverTag.actionHandler(hoverEvent.action());
        tokenEmitter.tag(HOVER).argument(HoverEvent.Action.NAMES.key(hoverEvent.action()));
        actionHandler.emit(hoverEvent.value(), tokenEmitter);
    }

    @Nullable
    static <V> ActionHandler<V> actionHandler(HoverEvent.Action<V> action) {
        ActionHandler<Component> actionHandler = null;
        if (action == HoverEvent.Action.SHOW_TEXT) {
            actionHandler = ShowText.INSTANCE;
        } else if (action == HoverEvent.Action.SHOW_ITEM) {
            actionHandler = ShowItem.INSTANCE;
        } else if (action == HoverEvent.Action.SHOW_ENTITY) {
            actionHandler = ShowEntity.INSTANCE;
        }
        return actionHandler;
    }

    @NotNull
    static String compactAsString(@NotNull Key key) {
        if (key.namespace().equals("minecraft")) {
            return key.value();
        }
        return key.asString();
    }

    static final class ShowEntity
    implements ActionHandler<HoverEvent.ShowEntity> {
        static final ShowEntity INSTANCE = new ShowEntity();

        private ShowEntity() {
        }

        @Override
        public @NotNull HoverEvent.ShowEntity parse(@NotNull ArgumentQueue argumentQueue, @NotNull Context context) {
            try {
                Key key = Key.key(argumentQueue.popOr("Show entity needs a type argument").value());
                UUID uUID = UUID.fromString(argumentQueue.popOr("Show entity needs an entity UUID").value());
                if (argumentQueue.hasNext()) {
                    Component component = context.deserialize(argumentQueue.pop().value());
                    return HoverEvent.ShowEntity.showEntity(key, uUID, component);
                }
                return HoverEvent.ShowEntity.showEntity(key, uUID);
            } catch (IllegalArgumentException | InvalidKeyException runtimeException) {
                throw context.newException("Exception parsing show_entity hover", runtimeException, argumentQueue);
            }
        }

        @Override
        public void emit(HoverEvent.ShowEntity showEntity, TokenEmitter tokenEmitter) {
            tokenEmitter.argument(HoverTag.compactAsString(showEntity.type())).argument(showEntity.id().toString());
            if (showEntity.name() != null) {
                tokenEmitter.argument(showEntity.name());
            }
        }
    }

    static final class ShowItem
    implements ActionHandler<HoverEvent.ShowItem> {
        private static final ShowItem INSTANCE = new ShowItem();

        private ShowItem() {
        }

        @Override
        public @NotNull HoverEvent.ShowItem parse(@NotNull ArgumentQueue argumentQueue, @NotNull Context context) {
            try {
                int n;
                Key key = Key.key(argumentQueue.popOr("Show item hover needs at least an item ID").value());
                int n2 = n = argumentQueue.hasNext() ? argumentQueue.pop().asInt().orElseThrow(() -> context.newException("The count argument was not a valid integer")) : 1;
                if (argumentQueue.hasNext()) {
                    String string = argumentQueue.peek().value();
                    if (string.startsWith("{")) {
                        argumentQueue.pop();
                        return ShowItem.legacyShowItem(key, n, string);
                    }
                    HashMap<Key, BinaryTagHolder> hashMap = new HashMap<Key, BinaryTagHolder>();
                    while (argumentQueue.hasNext()) {
                        Key key2 = Key.key(argumentQueue.pop().value());
                        String string2 = argumentQueue.popOr("a value was expected for key " + key2).value();
                        hashMap.put(key2, BinaryTagHolder.binaryTagHolder(string2));
                    }
                    return HoverEvent.ShowItem.showItem((Keyed)key, n, hashMap);
                }
                return HoverEvent.ShowItem.showItem(key, n);
            } catch (NumberFormatException | InvalidKeyException runtimeException) {
                throw context.newException("Exception parsing show_item hover", runtimeException, argumentQueue);
            }
        }

        private static @NotNull HoverEvent.ShowItem legacyShowItem(Key key, int n, String string) {
            return HoverEvent.ShowItem.showItem(key, n, BinaryTagHolder.binaryTagHolder(string));
        }

        @Override
        public void emit(HoverEvent.ShowItem showItem, TokenEmitter tokenEmitter) {
            tokenEmitter.argument(HoverTag.compactAsString(showItem.item()));
            if (showItem.count() != 1 || ShowItem.hasLegacy(showItem) || !showItem.dataComponents().isEmpty()) {
                tokenEmitter.argument(Integer.toString(showItem.count()));
                if (ShowItem.hasLegacy(showItem)) {
                    ShowItem.emitLegacyHover(showItem, tokenEmitter);
                } else {
                    for (Map.Entry<Key, DataComponentValue.TagSerializable> entry : showItem.dataComponentsAs(DataComponentValue.TagSerializable.class).entrySet()) {
                        tokenEmitter.argument(entry.getKey().asMinimalString());
                        tokenEmitter.argument(entry.getValue().asBinaryTag().string());
                    }
                }
            }
        }

        static boolean hasLegacy(HoverEvent.ShowItem showItem) {
            return showItem.nbt() != null;
        }

        static void emitLegacyHover(HoverEvent.ShowItem showItem, TokenEmitter tokenEmitter) {
            if (showItem.nbt() != null) {
                tokenEmitter.argument(showItem.nbt().string());
            }
        }
    }

    static final class ShowText
    implements ActionHandler<Component> {
        private static final ShowText INSTANCE = new ShowText();

        private ShowText() {
        }

        @Override
        @NotNull
        public Component parse(@NotNull ArgumentQueue argumentQueue, @NotNull Context context) {
            return context.deserialize(argumentQueue.popOr("show_text action requires a message").value());
        }

        @Override
        public void emit(Component component, TokenEmitter tokenEmitter) {
            tokenEmitter.argument(component);
        }
    }

    static interface ActionHandler<V> {
        @NotNull
        public V parse(@NotNull ArgumentQueue var1, @NotNull Context var2) throws ParsingException;

        public void emit(V var1, TokenEmitter var2);
    }
}

