/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.translation;

import java.util.Locale;
import java.util.function.Supplier;
import moss.factions.shade.net.kyori.adventure.internal.properties.AdventureProperties;
import moss.factions.shade.net.kyori.adventure.translation.Translator;
import org.jetbrains.annotations.Nullable;

final class TranslationLocales {
    private static final Supplier<Locale> GLOBAL;

    private TranslationLocales() {
    }

    static Locale global() {
        return GLOBAL.get();
    }

    static {
        @Nullable String string = AdventureProperties.DEFAULT_TRANSLATION_LOCALE.value();
        if (string == null || string.isEmpty()) {
            GLOBAL = () -> Locale.US;
        } else if (string.equals("system")) {
            GLOBAL = Locale::getDefault;
        } else {
            Locale locale = Translator.parseLocale(string);
            GLOBAL = () -> locale;
        }
    }
}

