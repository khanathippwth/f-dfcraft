/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ArgumentQueueImpl<T extends Tag.Argument>
implements ArgumentQueue {
    private final Context context;
    final List<T> args;
    private int ptr = 0;

    ArgumentQueueImpl(Context context, List<T> list) {
        this.context = context;
        this.args = list;
    }

    @NotNull
    public T pop() {
        if (!this.hasNext()) {
            throw this.context.newException("Missing argument for this tag!", this);
        }
        return (T)((Tag.Argument)this.args.get(this.ptr++));
    }

    @NotNull
    public T popOr(@NotNull String string) {
        Objects.requireNonNull(string, "errorMessage");
        if (!this.hasNext()) {
            throw this.context.newException(string, this);
        }
        return (T)((Tag.Argument)this.args.get(this.ptr++));
    }

    @NotNull
    public T popOr(@NotNull Supplier<String> supplier) {
        Objects.requireNonNull(supplier, "errorMessage");
        if (!this.hasNext()) {
            throw this.context.newException(Objects.requireNonNull(supplier.get(), "errorMessage.get()"), this);
        }
        return (T)((Tag.Argument)this.args.get(this.ptr++));
    }

    @Nullable
    public T peek() {
        return (T)(this.hasNext() ? (Tag.Argument)this.args.get(this.ptr) : null);
    }

    @Override
    public boolean hasNext() {
        return this.ptr < this.args.size();
    }

    @Override
    public void reset() {
        this.ptr = 0;
    }

    public String toString() {
        return this.args.toString();
    }
}

