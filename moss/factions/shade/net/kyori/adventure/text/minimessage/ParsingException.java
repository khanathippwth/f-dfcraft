/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public abstract class ParsingException
extends RuntimeException {
    private static final long serialVersionUID = 4502774670340827070L;
    public static final int LOCATION_UNKNOWN = -1;

    protected ParsingException(@Nullable String string) {
        super(string);
    }

    protected ParsingException(@Nullable String string, @Nullable Throwable throwable) {
        super(string, throwable);
    }

    protected ParsingException(String string, Throwable throwable, boolean bl, boolean bl2) {
        super(string, throwable, bl, bl2);
    }

    @NotNull
    public abstract String originalText();

    @Nullable
    public abstract String detailMessage();

    public abstract int startIndex();

    public abstract int endIndex();
}

