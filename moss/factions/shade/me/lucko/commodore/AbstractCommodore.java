/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  org.bukkit.command.Command
 *  org.bukkit.command.PluginCommand
 */
package moss.factions.shade.me.lucko.commodore;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import moss.factions.shade.me.lucko.commodore.Commodore;
import org.bukkit.command.PluginCommand;

abstract class AbstractCommodore
implements Commodore {
    protected static final Field CUSTOM_SUGGESTIONS_FIELD;
    protected static final Field COMMAND_EXECUTE_FUNCTION_FIELD;
    protected static final Field CHILDREN_FIELD;
    protected static final Field LITERALS_FIELD;
    protected static final Field ARGUMENTS_FIELD;
    protected static final Field[] CHILDREN_FIELDS;
    protected static final Command<?> DUMMY_COMMAND;
    protected static final SuggestionProvider<?> DUMMY_SUGGESTION_PROVIDER;

    AbstractCommodore() {
    }

    protected static void removeChild(RootCommandNode rootCommandNode, String string) {
        try {
            for (Field field : CHILDREN_FIELDS) {
                Map map = (Map)field.get(rootCommandNode);
                map.remove(string);
            }
        } catch (ReflectiveOperationException reflectiveOperationException) {
            throw new RuntimeException(reflectiveOperationException);
        }
    }

    protected static void setRequiredHackyFieldsRecursively(CommandNode<?> commandNode, SuggestionProvider<?> suggestionProvider) {
        try {
            COMMAND_EXECUTE_FUNCTION_FIELD.set(commandNode, DUMMY_COMMAND);
        } catch (IllegalAccessException illegalAccessException) {
            illegalAccessException.printStackTrace();
        }
        if (suggestionProvider != null && commandNode instanceof ArgumentCommandNode) {
            ArgumentCommandNode argumentCommandNode = (ArgumentCommandNode)commandNode;
            try {
                CUSTOM_SUGGESTIONS_FIELD.set(argumentCommandNode, suggestionProvider);
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
        for (CommandNode commandNode2 : commandNode.getChildren()) {
            AbstractCommodore.setRequiredHackyFieldsRecursively(commandNode2, suggestionProvider);
        }
    }

    protected static <S> LiteralCommandNode<S> renameLiteralNode(LiteralCommandNode<S> literalCommandNode, String string) {
        LiteralCommandNode literalCommandNode2 = new LiteralCommandNode(string, literalCommandNode.getCommand(), literalCommandNode.getRequirement(), literalCommandNode.getRedirect(), literalCommandNode.getRedirectModifier(), literalCommandNode.isFork());
        for (CommandNode commandNode : literalCommandNode.getChildren()) {
            literalCommandNode2.addChild(commandNode);
        }
        return literalCommandNode2;
    }

    protected static Collection<String> getAliases(org.bukkit.command.Command command) {
        Objects.requireNonNull(command, "command");
        Stream<String> stream = Stream.concat(Stream.of(command.getLabel()), command.getAliases().stream());
        if (command instanceof PluginCommand) {
            String string = ((PluginCommand)command).getPlugin().getName().toLowerCase().trim();
            stream = stream.flatMap(string2 -> Stream.of(string2, string + ":" + string2));
        }
        return stream.distinct().collect(Collectors.toList());
    }

    static {
        try {
            CUSTOM_SUGGESTIONS_FIELD = ArgumentCommandNode.class.getDeclaredField("customSuggestions");
            CUSTOM_SUGGESTIONS_FIELD.setAccessible(true);
            COMMAND_EXECUTE_FUNCTION_FIELD = CommandNode.class.getDeclaredField("command");
            COMMAND_EXECUTE_FUNCTION_FIELD.setAccessible(true);
            CHILDREN_FIELD = CommandNode.class.getDeclaredField("children");
            LITERALS_FIELD = CommandNode.class.getDeclaredField("literals");
            ARGUMENTS_FIELD = CommandNode.class.getDeclaredField("arguments");
            for (Field field : CHILDREN_FIELDS = new Field[]{CHILDREN_FIELD, LITERALS_FIELD, ARGUMENTS_FIELD}) {
                field.setAccessible(true);
            }
            DUMMY_COMMAND = commandContext -> {
                throw new UnsupportedOperationException();
            };
            DUMMY_SUGGESTION_PROVIDER = (commandContext, suggestionsBuilder) -> {
                throw new UnsupportedOperationException();
            };
        } catch (ReflectiveOperationException reflectiveOperationException) {
            throw new ExceptionInInitializerError(reflectiveOperationException);
        }
    }
}

