/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CommentHandler {
    public @NonNull Optional<String> extractHeader(@NonNull BufferedReader var1) throws IOException;

    public @NonNull Collection<String> toComment(@NonNull Collection<String> var1);
}

