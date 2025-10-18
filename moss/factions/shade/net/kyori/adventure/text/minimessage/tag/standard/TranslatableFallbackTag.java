/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.TranslatableComponent;
import moss.factions.shade.net.kyori.adventure.text.TranslationArgument;
import moss.factions.shade.net.kyori.adventure.text.format.StyleBuilderApplicable;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.Emitable;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.jetbrains.annotations.Nullable;

final class TranslatableFallbackTag {
    private static final String TR_OR = "tr_or";
    private static final String TRANSLATE_OR = "translate_or";
    private static final String LANG_OR = "lang_or";
    static final TagResolver RESOLVER = SerializableResolver.claimingComponent(StandardTags.names("lang_or", "translate_or", "tr_or"), TranslatableFallbackTag::create, TranslatableFallbackTag::claim);

    private TranslatableFallbackTag() {
    }

    static Tag create(ArgumentQueue argumentQueue, Context context) {
        List list;
        String string = argumentQueue.popOr("A translation key is required").value();
        String string2 = argumentQueue.popOr("A fallback messages is required").value();
        if (argumentQueue.hasNext()) {
            list = new ArrayList();
            while (argumentQueue.hasNext()) {
                list.add(context.deserialize(argumentQueue.pop().value()));
            }
        } else {
            list = Collections.emptyList();
        }
        return Tag.inserting(Component.translatable(string, string2, list, new StyleBuilderApplicable[0]));
    }

    @Nullable
    static Emitable claim(Component component) {
        if (!(component instanceof TranslatableComponent) || ((TranslatableComponent)component).fallback() == null) {
            return null;
        }
        TranslatableComponent translatableComponent = (TranslatableComponent)component;
        return tokenEmitter -> {
            tokenEmitter.tag(LANG_OR);
            tokenEmitter.argument(translatableComponent.key());
            tokenEmitter.argument(translatableComponent.fallback());
            for (TranslationArgument translationArgument : translatableComponent.arguments()) {
                tokenEmitter.argument(translationArgument.asComponent());
            }
        };
    }
}

