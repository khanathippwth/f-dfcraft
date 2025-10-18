/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver;

import java.util.Arrays;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.ParsingException;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.ClaimConsumer;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class SequentialTagResolver
implements TagResolver,
SerializableResolver {
    final TagResolver[] resolvers;

    SequentialTagResolver(@NotNull @NotNull TagResolver @NotNull [] tagResolverArray) {
        this.resolvers = tagResolverArray;
    }

    @Override
    @Nullable
    public Tag resolve(@NotNull String string, @NotNull ArgumentQueue argumentQueue, @NotNull Context context) {
        ParsingException parsingException = null;
        for (TagResolver tagResolver : this.resolvers) {
            try {
                @Nullable Tag tag = tagResolver.resolve(string, argumentQueue, context);
                if (tag == null) continue;
                return tag;
            } catch (ParsingException parsingException2) {
                argumentQueue.reset();
                if (parsingException == null) {
                    parsingException = parsingException2;
                    continue;
                }
                parsingException.addSuppressed(parsingException2);
            } catch (Exception exception) {
                argumentQueue.reset();
                ParsingException parsingException3 = context.newException("Exception thrown while parsing <" + string + ">", exception, argumentQueue);
                if (parsingException == null) {
                    parsingException = parsingException3;
                    continue;
                }
                parsingException.addSuppressed(parsingException3);
            }
        }
        if (parsingException != null) {
            throw parsingException;
        }
        return null;
    }

    @Override
    public boolean has(@NotNull String string) {
        for (TagResolver tagResolver : this.resolvers) {
            if (!tagResolver.has(string)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void handle(@NotNull Component component, @NotNull ClaimConsumer claimConsumer) {
        for (TagResolver tagResolver : this.resolvers) {
            if (!(tagResolver instanceof SerializableResolver)) continue;
            ((SerializableResolver)((Object)tagResolver)).handle(component, claimConsumer);
        }
    }

    public boolean equals(@Nullable Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof SequentialTagResolver)) {
            return false;
        }
        SequentialTagResolver sequentialTagResolver = (SequentialTagResolver)object;
        return Arrays.equals(this.resolvers, sequentialTagResolver.resolvers);
    }

    public int hashCode() {
        return Arrays.hashCode(this.resolvers);
    }
}

