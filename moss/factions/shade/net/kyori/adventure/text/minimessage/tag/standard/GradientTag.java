/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.Range
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.Stream;
import moss.factions.shade.net.kyori.adventure.text.format.TextColor;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.AbstractColorChangingTag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.ColorTagResolver;
import moss.factions.shade.net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

final class GradientTag
extends AbstractColorChangingTag {
    private static final String GRADIENT = "gradient";
    static final TagResolver RESOLVER = TagResolver.resolver("gradient", GradientTag::create);
    private int index = 0;
    private double multiplier = 1.0;
    private final TextColor[] colors;
    private @Range(from=-1L, to=1L) double phase;

    static Tag create(ArgumentQueue argumentQueue, Context context) {
        List<TextColor> list;
        double d = 0.0;
        if (argumentQueue.hasNext()) {
            list = new ArrayList();
            while (argumentQueue.hasNext()) {
                Object object;
                Tag.Argument argument = argumentQueue.pop();
                if (!argumentQueue.hasNext() && ((OptionalDouble)(object = argument.asDouble())).isPresent()) {
                    d = ((OptionalDouble)object).getAsDouble();
                    if (!(d < -1.0) && !(d > 1.0)) break;
                    throw context.newException(String.format("Gradient phase is out of range (%s). Must be in the range [-1.0, 1.0] (inclusive).", d), argumentQueue);
                }
                object = ColorTagResolver.resolveColor(argument.value(), context);
                list.add((TextColor)object);
            }
            if (list.size() == 1) {
                throw context.newException("Invalid gradient, not enough colors. Gradients must have at least two colors.", argumentQueue);
            }
        } else {
            list = Collections.emptyList();
        }
        return new GradientTag(d, list);
    }

    private GradientTag(double d, List<TextColor> list) {
        this.colors = list.isEmpty() ? new TextColor[]{TextColor.color(0xFFFFFF), TextColor.color(0)} : list.toArray(new TextColor[0]);
        if (d < 0.0) {
            this.phase = 1.0 + d;
            Collections.reverse(Arrays.asList(this.colors));
        } else {
            this.phase = d;
        }
    }

    @Override
    protected void init() {
        this.multiplier = this.size() == 1 ? 0.0 : (double)(this.colors.length - 1) / (double)(this.size() - 1);
        this.phase *= (double)(this.colors.length - 1);
        this.index = 0;
    }

    @Override
    protected void advanceColor() {
        ++this.index;
    }

    @Override
    protected TextColor color() {
        double d = (double)this.index * this.multiplier + this.phase;
        int n = (int)Math.floor(d);
        int n2 = (int)Math.ceil(d) % this.colors.length;
        int n3 = n % this.colors.length;
        return TextColor.lerp((float)d - (float)n, this.colors[n3], this.colors[n2]);
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("phase", this.phase), ExaminableProperty.of("colors", this.colors));
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        GradientTag gradientTag = (GradientTag)object;
        return this.index == gradientTag.index && this.phase == gradientTag.phase && Arrays.equals(this.colors, gradientTag.colors);
    }

    @Override
    public int hashCode() {
        int n = Objects.hash(this.index, this.phase);
        n = 31 * n + Arrays.hashCode(this.colors);
        return n;
    }
}

