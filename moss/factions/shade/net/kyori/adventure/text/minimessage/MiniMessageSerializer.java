/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.TextComponent;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.ClaimConsumer;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.Emitable;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.QuotingOverride;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.TokenEmitter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class MiniMessageSerializer {
    private MiniMessageSerializer() {
    }

    @NotNull
    static String serialize(@NotNull Component component, @NotNull SerializableResolver serializableResolver, boolean bl) {
        StringBuilder stringBuilder = new StringBuilder();
        Collector collector = new Collector(serializableResolver, bl, stringBuilder);
        collector.mark();
        MiniMessageSerializer.visit(component, collector, serializableResolver, true);
        if (bl) {
            collector.popAll();
        } else {
            collector.completeTag();
        }
        return stringBuilder.toString();
    }

    private static void visit(@NotNull Component component, Collector collector, SerializableResolver serializableResolver, boolean bl) {
        serializableResolver.handle(component, collector);
        collector.flushClaims(component);
        Iterator<Component> iterator = component.children().iterator();
        while (iterator.hasNext()) {
            collector.mark();
            MiniMessageSerializer.visit(iterator.next(), collector, serializableResolver, bl && !iterator.hasNext());
        }
        if (!bl) {
            collector.popToMark();
        }
    }

    static final class Collector
    implements TokenEmitter,
    ClaimConsumer {
        private static final String MARK = "__<'\"\\MARK__";
        private static final char[] TEXT_ESCAPES = new char[]{'\\', '<'};
        private static final char[] TAG_TOKENS = new char[]{'>', ':'};
        private static final char[] SINGLE_QUOTED_ESCAPES = new char[]{'\\', '\''};
        private static final char[] DOUBLE_QUOTED_ESCAPES = new char[]{'\\', '\"'};
        private final SerializableResolver resolver;
        private final boolean strict;
        private final StringBuilder consumer;
        private String[] activeTags = new String[4];
        private int tagLevel = 0;
        private TagState tagState = TagState.TEXT;
        @Nullable
        Emitable componentClaim;
        final Set<String> claimedStyleElements = new HashSet<String>();

        Collector(SerializableResolver serializableResolver, boolean bl, StringBuilder stringBuilder) {
            this.resolver = serializableResolver;
            this.strict = bl;
            this.consumer = stringBuilder;
        }

        private void pushActiveTag(String string) {
            if (this.tagLevel >= this.activeTags.length) {
                this.activeTags = Arrays.copyOf(this.activeTags, this.activeTags.length * 2);
            }
            this.activeTags[this.tagLevel++] = string;
        }

        private String popTag(boolean bl) {
            if (this.tagLevel-- <= 0) {
                throw new IllegalStateException("Unbalanced tags, tried to pop below depth");
            }
            String string = this.activeTags[this.tagLevel];
            if (!bl && string == MARK) {
                throw new IllegalStateException("Tried to pop past mark, tag stack: " + Arrays.toString(this.activeTags) + " @ " + this.tagLevel);
            }
            return string;
        }

        void mark() {
            this.pushActiveTag(MARK);
        }

        void popToMark() {
            String string;
            if (this.tagLevel == 0) {
                return;
            }
            while ((string = this.popTag(true)) != MARK) {
                this.emitClose(string);
            }
        }

        void popAll() {
            while (this.tagLevel > 0) {
                String string;
                if ((string = this.activeTags[--this.tagLevel]) == MARK) continue;
                this.emitClose(string);
            }
        }

        void completeTag() {
            if (this.tagState.isTag) {
                this.consumer.append('>');
                this.tagState = TagState.TEXT;
            }
        }

        @Override
        @NotNull
        public Collector tag(@NotNull String string) {
            this.completeTag();
            this.consumer.append('<');
            this.escapeTagContent(string, QuotingOverride.UNQUOTED);
            this.tagState = TagState.MID;
            this.pushActiveTag(string);
            return this;
        }

        @Override
        @NotNull
        public TokenEmitter selfClosingTag(@NotNull String string) {
            this.completeTag();
            this.consumer.append('<');
            this.escapeTagContent(string, QuotingOverride.UNQUOTED);
            this.tagState = TagState.MID_SELF_CLOSING;
            return this;
        }

        @Override
        @NotNull
        public TokenEmitter argument(@NotNull String string) {
            if (!this.tagState.isTag) {
                throw new IllegalStateException("Not within a tag!");
            }
            this.consumer.append(':');
            this.escapeTagContent(string, null);
            return this;
        }

        @Override
        @NotNull
        public TokenEmitter argument(@NotNull String string, @NotNull QuotingOverride quotingOverride) {
            if (!this.tagState.isTag) {
                throw new IllegalStateException("Not within a tag!");
            }
            this.consumer.append(':');
            this.escapeTagContent(string, Objects.requireNonNull(quotingOverride, "quotingPreference"));
            return this;
        }

        @Override
        @NotNull
        public TokenEmitter argument(@NotNull Component component) {
            String string = MiniMessageSerializer.serialize(component, this.resolver, this.strict);
            return this.argument(string, QuotingOverride.QUOTED);
        }

        @Override
        @NotNull
        public Collector text(@NotNull String string) {
            this.completeTag();
            Collector.appendEscaping(this.consumer, string, TEXT_ESCAPES, true);
            return this;
        }

        private void escapeTagContent(String string, @Nullable QuotingOverride quotingOverride) {
            boolean bl = quotingOverride == QuotingOverride.QUOTED;
            boolean bl2 = false;
            boolean bl3 = false;
            for (int i = 0; i < string.length(); ++i) {
                char c = string.charAt(i);
                if (c == '>' || c == ':' || c == ' ') {
                    bl = true;
                    if (!bl2 || !bl3) continue;
                    break;
                }
                if (c == '\'') {
                    bl2 = true;
                    break;
                }
                if (c != '\"') continue;
                bl3 = true;
                if (bl && bl2) break;
            }
            if (bl2) {
                this.consumer.append('\"');
                Collector.appendEscaping(this.consumer, string, DOUBLE_QUOTED_ESCAPES, true);
                this.consumer.append('\"');
            } else if (bl3 || bl) {
                this.consumer.append('\'');
                Collector.appendEscaping(this.consumer, string, SINGLE_QUOTED_ESCAPES, true);
                this.consumer.append('\'');
            } else {
                Collector.appendEscaping(this.consumer, string, TAG_TOKENS, false);
            }
        }

        static void appendEscaping(StringBuilder stringBuilder, String string, char[] cArray, boolean bl) {
            int n = 0;
            boolean bl2 = false;
            for (int i = 0; i < string.length(); ++i) {
                char c = string.charAt(i);
                boolean bl3 = false;
                for (char c2 : cArray) {
                    if (c != c2) continue;
                    if (!bl) {
                        throw new IllegalArgumentException("Invalid escapable character '" + c + "' found at index " + i + " in string '" + string + "'");
                    }
                    bl3 = true;
                    break;
                }
                if (bl3) {
                    if (bl2) {
                        stringBuilder.append(string, n, i);
                    }
                    n = i + 1;
                    stringBuilder.append('\\').append(c);
                    continue;
                }
                bl2 = true;
            }
            if (n < string.length() && bl2) {
                stringBuilder.append(string, n, string.length());
            }
        }

        @Override
        @NotNull
        public Collector pop() {
            this.emitClose(this.popTag(false));
            return this;
        }

        private void emitClose(@NotNull String string) {
            if (this.tagState.isTag) {
                if (this.tagState == TagState.MID) {
                    this.consumer.append('/');
                }
                this.consumer.append('>');
                this.tagState = TagState.TEXT;
            } else {
                this.consumer.append('<').append('/');
                this.escapeTagContent(string, QuotingOverride.UNQUOTED);
                this.consumer.append('>');
            }
        }

        @Override
        public void style(@NotNull String string, @NotNull Emitable emitable) {
            if (this.claimedStyleElements.add(Objects.requireNonNull(string, "claimKey"))) {
                emitable.emit(this);
            }
        }

        @Override
        public boolean component(@NotNull Emitable emitable) {
            if (this.componentClaim != null) {
                return false;
            }
            this.componentClaim = Objects.requireNonNull(emitable, "componentClaim");
            return true;
        }

        @Override
        public boolean componentClaimed() {
            return this.componentClaim != null;
        }

        @Override
        public boolean styleClaimed(@NotNull String string) {
            return this.claimedStyleElements.contains(string);
        }

        void flushClaims(Component component) {
            if (this.componentClaim != null) {
                this.componentClaim.emit(this);
                this.componentClaim = null;
            } else if (component instanceof TextComponent) {
                this.text(((TextComponent)component).content());
            } else {
                throw new IllegalStateException("Unclaimed component " + component);
            }
            this.claimedStyleElements.clear();
        }

        static enum TagState {
            TEXT(false),
            MID(true),
            MID_SELF_CLOSING(true);

            final boolean isTag;

            private TagState(boolean bl) {
                this.isTag = bl;
            }
        }
    }
}

