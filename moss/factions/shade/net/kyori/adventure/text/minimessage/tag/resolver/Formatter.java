/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver;

import java.text.ChoiceFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.TagPattern;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

public final class Formatter {
    private Formatter() {
    }

    @NotNull
    public static TagResolver number(@TagPattern @NotNull String string, @NotNull Number number) {
        return TagResolver.resolver(string, (argumentQueue, context) -> {
            NumberFormat numberFormat;
            if (argumentQueue.hasNext()) {
                String string = argumentQueue.pop().value();
                if (argumentQueue.hasNext()) {
                    String string2 = argumentQueue.pop().value();
                    numberFormat = new DecimalFormat(string2, new DecimalFormatSymbols(Locale.forLanguageTag(string)));
                } else {
                    numberFormat = string.contains(".") ? new DecimalFormat(string, DecimalFormatSymbols.getInstance()) : DecimalFormat.getInstance(Locale.forLanguageTag(string));
                }
            } else {
                numberFormat = DecimalFormat.getInstance();
            }
            return Tag.inserting(context.deserialize(numberFormat.format(number)));
        });
    }

    @NotNull
    public static TagResolver date(@TagPattern @NotNull String string, @NotNull TemporalAccessor temporalAccessor) {
        return TagResolver.resolver(string, (argumentQueue, context) -> {
            String string = argumentQueue.popOr("Format expected.").value();
            return Tag.inserting(context.deserialize(DateTimeFormatter.ofPattern(string).format(temporalAccessor)));
        });
    }

    @NotNull
    public static TagResolver choice(@TagPattern @NotNull String string, Number number) {
        return TagResolver.resolver(string, (argumentQueue, context) -> {
            String string = argumentQueue.popOr("Format expected.").value();
            ChoiceFormat choiceFormat = new ChoiceFormat(string);
            return Tag.inserting(context.deserialize(choiceFormat.format(number)));
        });
    }

    public static TagResolver booleanChoice(@TagPattern @NotNull String string, boolean bl) {
        return TagResolver.resolver(string, (argumentQueue, context) -> {
            String string = argumentQueue.popOr("True format expected.").value();
            String string2 = argumentQueue.popOr("False format expected.").value();
            return Tag.inserting(context.deserialize(bl ? string : string2));
        });
    }
}

