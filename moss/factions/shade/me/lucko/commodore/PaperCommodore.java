/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  org.bukkit.command.Command
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package moss.factions.shade.me.lucko.commodore;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import moss.factions.shade.me.lucko.commodore.AbstractCommodore;
import moss.factions.shade.me.lucko.commodore.Commodore;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

final class PaperCommodore
extends AbstractCommodore
implements Commodore,
Listener {
    private final List<CommodoreCommand> commands = new ArrayList<CommodoreCommand>();

    PaperCommodore(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents((Listener)this, plugin);
    }

    @Override
    public void register(LiteralCommandNode<?> literalCommandNode) {
        Objects.requireNonNull(literalCommandNode, "node");
        this.commands.add(new CommodoreCommand(literalCommandNode, null));
    }

    @Override
    public void register(Command command, LiteralCommandNode<?> literalCommandNode, Predicate<? super Player> predicate) {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(literalCommandNode, "node");
        Objects.requireNonNull(predicate, "permissionTest");
        try {
            PaperCommodore.setRequiredHackyFieldsRecursively(literalCommandNode, DUMMY_SUGGESTION_PROVIDER);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        Collection<String> collection = PaperCommodore.getAliases(command);
        if (!collection.contains(literalCommandNode.getLiteral())) {
            literalCommandNode = PaperCommodore.renameLiteralNode(literalCommandNode, command.getName());
        }
        for (String string : collection) {
            if (literalCommandNode.getLiteral().equals(string)) {
                this.commands.add(new CommodoreCommand(literalCommandNode, predicate));
                continue;
            }
            LiteralCommandNode literalCommandNode2 = ((LiteralArgumentBuilder)LiteralArgumentBuilder.literal((String)string).redirect(literalCommandNode)).build();
            this.commands.add(new CommodoreCommand(literalCommandNode2, predicate));
        }
    }

    @EventHandler
    public void onPlayerSendCommandsEvent(AsyncPlayerSendCommandsEvent<?> asyncPlayerSendCommandsEvent) {
        if (asyncPlayerSendCommandsEvent.isAsynchronous() || !asyncPlayerSendCommandsEvent.hasFiredAsync()) {
            for (CommodoreCommand commodoreCommand : this.commands) {
                commodoreCommand.apply(asyncPlayerSendCommandsEvent.getPlayer(), asyncPlayerSendCommandsEvent.getCommandNode());
            }
        }
    }

    static void ensureSetup() {
    }

    static {
        try {
            Class.forName("com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent");
        } catch (ClassNotFoundException classNotFoundException) {
            throw new UnsupportedOperationException("Not running on modern Paper!", classNotFoundException);
        }
    }

    private static final class CommodoreCommand {
        private final LiteralCommandNode<?> node;
        private final Predicate<? super Player> permissionTest;

        private CommodoreCommand(LiteralCommandNode<?> literalCommandNode, Predicate<? super Player> predicate) {
            this.node = literalCommandNode;
            this.permissionTest = predicate;
        }

        public void apply(Player player, RootCommandNode<?> rootCommandNode) {
            if (this.permissionTest != null && !this.permissionTest.test((Player)player)) {
                return;
            }
            AbstractCommodore.removeChild(rootCommandNode, this.node.getName());
            rootCommandNode.addChild(this.node);
        }
    }
}

