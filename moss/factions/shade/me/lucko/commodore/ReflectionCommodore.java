/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerCommandSendEvent
 *  org.bukkit.event.server.ServerLoadEvent
 *  org.bukkit.plugin.Plugin
 */
package moss.factions.shade.me.lucko.commodore;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import moss.factions.shade.me.lucko.commodore.AbstractCommodore;
import moss.factions.shade.me.lucko.commodore.Commodore;
import moss.factions.shade.me.lucko.commodore.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.Plugin;

final class ReflectionCommodore
extends AbstractCommodore
implements Commodore {
    private static final Field CONSOLE_FIELD;
    private static final Method GET_COMMAND_DISPATCHER_METHOD;
    private static final Method GET_BRIGADIER_DISPATCHER_METHOD;
    private static final Constructor<?> COMMAND_WRAPPER_CONSTRUCTOR;
    private final Plugin plugin;
    private final List<LiteralCommandNode<?>> registeredNodes = new ArrayList();

    ReflectionCommodore(Plugin plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents((Listener)new ServerReloadListener(this), this.plugin);
    }

    private CommandDispatcher<?> getDispatcher() {
        try {
            Object object = CONSOLE_FIELD.get(Bukkit.getServer());
            Object object2 = GET_COMMAND_DISPATCHER_METHOD.invoke(object, new Object[0]);
            return (CommandDispatcher)GET_BRIGADIER_DISPATCHER_METHOD.invoke(object2, new Object[0]);
        } catch (ReflectiveOperationException reflectiveOperationException) {
            throw new RuntimeException(reflectiveOperationException);
        }
    }

    @Override
    public void register(LiteralCommandNode<?> literalCommandNode) {
        Objects.requireNonNull(literalCommandNode, "node");
        CommandDispatcher<?> commandDispatcher = this.getDispatcher();
        RootCommandNode rootCommandNode = commandDispatcher.getRoot();
        ReflectionCommodore.removeChild(rootCommandNode, literalCommandNode.getName());
        rootCommandNode.addChild(literalCommandNode);
        this.registeredNodes.add(literalCommandNode);
    }

    @Override
    public void register(Command command, LiteralCommandNode<?> literalCommandNode, Predicate<? super Player> predicate) {
        SuggestionProvider suggestionProvider;
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(literalCommandNode, "node");
        Objects.requireNonNull(predicate, "permissionTest");
        try {
            suggestionProvider = (SuggestionProvider)COMMAND_WRAPPER_CONSTRUCTOR.newInstance(this.plugin.getServer(), command);
            ReflectionCommodore.setRequiredHackyFieldsRecursively(literalCommandNode, suggestionProvider);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        suggestionProvider = ReflectionCommodore.getAliases(command);
        if (!suggestionProvider.contains(literalCommandNode.getLiteral())) {
            literalCommandNode = ReflectionCommodore.renameLiteralNode(literalCommandNode, command.getName());
        }
        for (String string : suggestionProvider) {
            if (literalCommandNode.getLiteral().equals(string)) {
                this.register(literalCommandNode);
                continue;
            }
            this.register(((LiteralArgumentBuilder)LiteralArgumentBuilder.literal((String)string).redirect(literalCommandNode)).build());
        }
        this.plugin.getServer().getPluginManager().registerEvents((Listener)new CommandDataSendListener(command, predicate), this.plugin);
    }

    static void ensureSetup() {
    }

    static {
        try {
            Class<?> clazz;
            Class<?> clazz2;
            if (ReflectionUtil.minecraftVersion() >= 19) {
                throw new UnsupportedOperationException("ReflectionCommodore is not supported on MC 1.19 or above. Switch to Paper :)");
            }
            if (ReflectionUtil.minecraftVersion() > 16) {
                clazz2 = ReflectionUtil.mcClass("server.MinecraftServer");
                clazz = ReflectionUtil.mcClass("commands.CommandDispatcher");
            } else {
                clazz2 = ReflectionUtil.nmsClass("MinecraftServer");
                clazz = ReflectionUtil.nmsClass("CommandDispatcher");
            }
            Class<?> clazz3 = ReflectionUtil.obcClass("CraftServer");
            CONSOLE_FIELD = clazz3.getDeclaredField("console");
            CONSOLE_FIELD.setAccessible(true);
            GET_COMMAND_DISPATCHER_METHOD = Arrays.stream(clazz2.getDeclaredMethods()).filter(method -> method.getParameterCount() == 0).filter(method -> clazz.isAssignableFrom(method.getReturnType())).findFirst().orElseThrow(NoSuchMethodException::new);
            GET_COMMAND_DISPATCHER_METHOD.setAccessible(true);
            GET_BRIGADIER_DISPATCHER_METHOD = Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.getParameterCount() == 0).filter(method -> CommandDispatcher.class.isAssignableFrom(method.getReturnType())).findFirst().orElseThrow(NoSuchMethodException::new);
            GET_BRIGADIER_DISPATCHER_METHOD.setAccessible(true);
            Class<?> clazz4 = ReflectionUtil.obcClass("command.BukkitCommandWrapper");
            COMMAND_WRAPPER_CONSTRUCTOR = clazz4.getConstructor(clazz3, Command.class);
        } catch (ReflectiveOperationException reflectiveOperationException) {
            throw new ExceptionInInitializerError(reflectiveOperationException);
        }
    }

    private static final class ServerReloadListener
    implements Listener {
        private final ReflectionCommodore commodore;

        private ServerReloadListener(ReflectionCommodore reflectionCommodore) {
            this.commodore = reflectionCommodore;
        }

        @EventHandler
        public void onLoad(ServerLoadEvent serverLoadEvent) {
            CommandDispatcher commandDispatcher = this.commodore.getDispatcher();
            RootCommandNode rootCommandNode = commandDispatcher.getRoot();
            for (LiteralCommandNode literalCommandNode : this.commodore.registeredNodes) {
                AbstractCommodore.removeChild(rootCommandNode, literalCommandNode.getName());
                rootCommandNode.addChild((CommandNode)literalCommandNode);
            }
        }
    }

    private static final class CommandDataSendListener
    implements Listener {
        private final Set<String> aliases;
        private final Set<String> minecraftPrefixedAliases;
        private final Predicate<? super Player> permissionTest;

        CommandDataSendListener(Command command, Predicate<? super Player> predicate) {
            this.aliases = new HashSet<String>(AbstractCommodore.getAliases(command));
            this.minecraftPrefixedAliases = this.aliases.stream().map(string -> "minecraft:" + string).collect(Collectors.toSet());
            this.permissionTest = predicate;
        }

        @EventHandler
        public void onCommandSend(PlayerCommandSendEvent playerCommandSendEvent) {
            playerCommandSendEvent.getCommands().removeAll(this.minecraftPrefixedAliases);
            if (!this.permissionTest.test((Player)playerCommandSendEvent.getPlayer())) {
                playerCommandSendEvent.getCommands().removeAll(this.aliases);
            }
        }
    }
}

