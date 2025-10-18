/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package moss.factions.shade.me.lucko.commodore.file;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Arrays;
import java.util.Collection;
import moss.factions.shade.me.lucko.commodore.file.ArgumentTypeParser;
import moss.factions.shade.me.lucko.commodore.file.Lexer;
import moss.factions.shade.me.lucko.commodore.file.Token;

class Parser<S> {
    private final Lexer lexer;
    private final Collection<ArgumentTypeParser> argumentTypeParsers;

    Parser(Lexer lexer, Collection<ArgumentTypeParser> collection) {
        this.lexer = lexer;
        this.argumentTypeParsers = collection;
    }

    LiteralCommandNode<S> parse() {
        CommandNode<S> commandNode = this.parseNode();
        if (!(commandNode instanceof LiteralCommandNode)) {
            throw this.lexer.createException("Root command node is not a literal command node");
        }
        if (this.lexer.peek() != Token.ConstantToken.EOF) {
            throw this.lexer.createException("Expected end of file but got " + this.lexer.peek());
        }
        return (LiteralCommandNode)commandNode;
    }

    private CommandNode<S> parseNode() {
        Token token = (Token)this.lexer.next();
        if (!(token instanceof Token.StringToken)) {
            throw this.lexer.createException("Expected string token for node name but got " + token);
        }
        String string = ((Token.StringToken)token).getString();
        Object object = this.lexer.peek() instanceof Token.StringToken ? RequiredArgumentBuilder.argument((String)string, this.parseArgumentType()) : LiteralArgumentBuilder.literal((String)string);
        if (this.lexer.peek() == Token.ConstantToken.OPEN_BRACKET) {
            this.lexer.next();
            while (this.lexer.peek() != Token.ConstantToken.CLOSE_BRACKET) {
                CommandNode<S> commandNode = this.parseNode();
                object.then(commandNode);
            }
            this.lexer.next();
        } else {
            if (this.lexer.peek() != Token.ConstantToken.SEMICOLON) {
                throw this.lexer.createException("Node definition not ended with semicolon, got " + this.lexer.peek());
            }
            this.lexer.next();
        }
        return object.build();
    }

    private ArgumentType<?> parseArgumentType() {
        Token token = (Token)this.lexer.next();
        if (!(token instanceof Token.StringToken)) {
            throw this.lexer.createException("Expected string token for argument type but got " + token);
        }
        String string = ((Token.StringToken)token).getString();
        Object[] objectArray = string.split(":");
        if (objectArray.length != 2) {
            throw this.lexer.createException("Invalid key for argument type: " + Arrays.toString(objectArray));
        }
        for (ArgumentTypeParser argumentTypeParser : this.argumentTypeParsers) {
            if (!argumentTypeParser.canParse((String)objectArray[0], (String)objectArray[1])) continue;
            return argumentTypeParser.parse((String)objectArray[0], (String)objectArray[1], this.lexer);
        }
        throw this.lexer.createException("Unable to parse argument type: " + string);
    }
}

