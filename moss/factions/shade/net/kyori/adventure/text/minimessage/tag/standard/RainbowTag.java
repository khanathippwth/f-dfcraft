/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import java.util.Objects;
import java.util.stream.Stream;
import moss.factions.shade.net.kyori.adventure.text.format.TextColor;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.AbstractColorChangingTag;
import moss.factions.shade.net.kyori.adventure.util.HSVLike;
import moss.factions.shade.net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class RainbowTag
extends AbstractColorChangingTag {
    private static final String REVERSE = "!";
    private static final String RAINBOW = "rainbow";
    static final TagResolver RESOLVER = TagResolver.resolver("rainbow", RainbowTag::create);
    private final boolean reversed;
    private final double dividedPhase;
    private int colorIndex = 0;

    static Tag create(ArgumentQueue argumentQueue, Context context) {
        boolean bl = false;
        int n = 0;
        if (argumentQueue.hasNext()) {
            String string = argumentQueue.pop().value();
            if (string.startsWith(REVERSE)) {
                bl = true;
                string = string.substring(REVERSE.length());
            }
            if (string.length() > 0) {
                try {
                    n = Integer.parseInt(string);
                } catch (NumberFormatException numberFormatException) {
                    throw context.newException("Expected phase, got " + string);
                }
            }
        }
        return new RainbowTag(bl, n);
    }

    private RainbowTag(boolean bl, int n) {
        this.reversed = bl;
        this.dividedPhase = (double)n / 10.0;
    }

    @Override
    protected void init() {
        if (this.reversed) {
            this.colorIndex = this.size() - 1;
        }
    }

    @Override
    protected void advanceColor() {
        this.colorIndex = this.reversed ? (this.colorIndex == 0 ? this.size() - 1 : --this.colorIndex) : ++this.colorIndex;
    }

    @Override
    protected TextColor color() {
        float f = this.colorIndex;
        float f2 = (float)(((double)(f / (float)this.size()) + this.dividedPhase) % 1.0);
        return TextColor.color(HSVLike.hsvLike(f2, 1.0f, 1.0f));
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("phase", this.dividedPhase));
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        RainbowTag rainbowTag = (RainbowTag)object;
        return this.colorIndex == rainbowTag.colorIndex && this.dividedPhase == rainbowTag.dividedPhase;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.colorIndex, this.dividedPhase);
    }
}

