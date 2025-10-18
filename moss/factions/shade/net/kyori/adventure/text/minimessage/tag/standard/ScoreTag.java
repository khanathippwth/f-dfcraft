/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag.standard;

import moss.factions.shade.net.kyori.adventure.text.Component;
import moss.factions.shade.net.kyori.adventure.text.ScoreComponent;
import moss.factions.shade.net.kyori.adventure.text.minimessage.Context;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.Emitable;
import moss.factions.shade.net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.Nullable;

final class ScoreTag {
    public static final String SCORE = "score";
    static final TagResolver RESOLVER = SerializableResolver.claimingComponent("score", ScoreTag::create, ScoreTag::emit);

    private ScoreTag() {
    }

    static Tag create(ArgumentQueue argumentQueue, Context context) {
        String string = argumentQueue.popOr("A scoreboard member name is required").value();
        String string2 = argumentQueue.popOr("An objective name is required").value();
        return Tag.inserting(Component.score(string, string2));
    }

    @Nullable
    static Emitable emit(Component component) {
        if (!(component instanceof ScoreComponent)) {
            return null;
        }
        ScoreComponent scoreComponent = (ScoreComponent)component;
        return tokenEmitter -> tokenEmitter.tag(SCORE).argument(scoreComponent.name()).argument(scoreComponent.objective());
    }
}

