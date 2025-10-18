/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  net.luckperms.api.context.ContextCalculator
 *  net.luckperms.api.context.ContextConsumer
 *  net.luckperms.api.context.ContextSet
 *  net.luckperms.api.context.ImmutableContextSet
 *  net.luckperms.api.context.ImmutableContextSet$Builder
 *  org.bukkit.entity.Player
 */
package com.massivecraft.factions.integration.permcontext;

import com.massivecraft.factions.integration.permcontext.Context;
import com.massivecraft.factions.integration.permcontext.ContextManager;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;

public class LuckpermsContextCalculator
implements ContextCalculator<Player> {
    public void calculate(Player player, ContextConsumer contextConsumer) {
        for (Context context : ContextManager.getContexts()) {
            for (String string : context.getValues(player)) {
                contextConsumer.accept(context.getNamespacedName(), string);
            }
        }
    }

    public ContextSet estimatePotentialContexts() {
        ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
        for (Context context : ContextManager.getContexts()) {
            for (String string : context.getPossibleValues()) {
                builder.add(context.getNamespacedName(), string);
            }
        }
        return builder.build();
    }
}

