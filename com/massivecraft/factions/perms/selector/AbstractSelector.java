/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.perms.selector;

import com.massivecraft.factions.perms.PermSelector;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.minimessage.MiniMessage;

public abstract class AbstractSelector
implements PermSelector {
    private final PermSelector.Descriptor descriptor;

    protected AbstractSelector(PermSelector.Descriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public PermSelector.Descriptor descriptor() {
        return this.descriptor;
    }

    public String toString() {
        return this.serialize();
    }

    public int hashCode() {
        return Objects.hashCode(this.serialize());
    }

    public boolean equals(Object object) {
        return object instanceof PermSelector && ((PermSelector)object).serialize().equals(this.serialize());
    }

    public static class BasicDescriptor
    implements PermSelector.Descriptor {
        private final Function<String, PermSelector> function;
        private final String name;
        private boolean acceptsEmpty;
        private Supplier<String> instructions;
        private final Supplier<String> displayName;

        public BasicDescriptor(String string, Supplier<String> supplier, Function<String, PermSelector> function) {
            this.name = string;
            this.function = function;
            this.displayName = supplier;
        }

        @Override
        public PermSelector create(String string) {
            return this.function.apply(string);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Component getDisplayName() {
            return MiniMessage.miniMessage().deserialize(this.displayName.get());
        }

        public BasicDescriptor acceptEmpty() {
            this.acceptsEmpty = true;
            return this;
        }

        @Override
        public boolean acceptsEmpty() {
            return this.acceptsEmpty;
        }

        public BasicDescriptor withInstructions(Supplier<String> supplier) {
            this.instructions = supplier;
            return this;
        }

        @Override
        public String getInstructions() {
            return this.instructions == null ? null : this.instructions.get();
        }
    }
}

