/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.Stream;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.format.NamedTextColor;
import moss.factions.shade.net.kyori.adventure.text.format.TextColor;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Inserting;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import moss.factions.shade.net.kyori.examination.Examinable;
import moss.factions.shade.net.kyori.examination.ExaminableProperty;
import org.jetbrains.annotations.NotNull;

public final class TransitionTag
implements Inserting,
Examinable {
    public static final String TRANSITION = "transition";
    private final TextColor[] colors;
    private final float phase;
    private final boolean negativePhase;
    static final TagResolver RESOLVER = TagResolver.resolver("transition", TransitionTag::create);

    static Tag create(ArgumentQueue argumentQueue, Context context) {
        List<TextColor> list;
        float f = 0.0f;
        if (argumentQueue.hasNext()) {
            list = new ArrayList();
            while (argumentQueue.hasNext()) {
                Object object;
                Tag.Argument argument = argumentQueue.pop();
                if (!argumentQueue.hasNext() && ((OptionalDouble)(object = argument.asDouble())).isPresent()) {
                    f = (float)((OptionalDouble)object).getAsDouble();
                    if (!(f < -1.0f) && !(f > 1.0f)) break;
                    throw context.newException(String.format("Gradient phase is out of range (%s). Must be in the range [-1.0f, 1.0f] (inclusive).", Float.valueOf(f)), argumentQueue);
                }
                object = argument.value();
                TextColor textColor = ((String)object).charAt(0) == '#' ? TextColor.fromHexString((String)object) : (TextColor)NamedTextColor.NAMES.value(argument.lowerValue());
                if (textColor == null) {
                    throw context.newException(String.format("Unable to parse a color from '%s'. Please use named colors or hex (#RRGGBB) colors.", object), argumentQueue);
                }
                list.add(textColor);
            }
            if (list.size() < 2) {
                throw context.newException("Invalid transition, not enough colors. Transitions must have at least two colors.", argumentQueue);
            }
        } else {
            list = Collections.emptyList();
        }
        return new TransitionTag(f, list);
    }

    private TransitionTag(float f, List<TextColor> list) {
        if (f < 0.0f) {
            this.negativePhase = true;
            this.phase = 1.0f + f;
            Collections.reverse(list);
        } else {
            this.negativePhase = false;
            this.phase = f;
        }
        this.colors = list.isEmpty() ? new TextColor[]{TextColor.color(0xFFFFFF), TextColor.color(0)} : list.toArray(new TextColor[0]);
    }

    @Override
    @NotNull
    public Component value() {
        return Component.text("", this.color());
    }

    private TextColor color() {
        float f = 1.0f / (float)(this.colors.length - 1);
        for (int i = 1; i < this.colors.length; ++i) {
            float f2 = (float)i * f;
            if (!(f2 >= this.phase)) continue;
            float f3 = 1.0f + (this.phase - f2) * (float)(this.colors.length - 1);
            if (this.negativePhase) {
                return TextColor.lerp(1.0f - f3, this.colors[i], this.colors[i - 1]);
            }
            return TextColor.lerp(f3, this.colors[i - 1], this.colors[i]);
        }
        return this.colors[0];
    }

    @Override
    @NotNull
    public Stream<? extends ExaminableProperty> examinableProperties() {
        return Stream.of(ExaminableProperty.of("phase", this.phase), ExaminableProperty.of("colors", this.colors));
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        TransitionTag transitionTag = (TransitionTag)object;
        return this.phase == transitionTag.phase && Arrays.equals(this.colors, transitionTag.colors);
    }

    public int hashCode() {
        int n = Objects.hash(Float.valueOf(this.phase));
        n = 31 * n + Arrays.hashCode(this.colors);
        return n;
    }
}

