/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.LongArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 */
package moss.factions.shade.me.lucko.commodore.file;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import moss.factions.shade.me.lucko.commodore.file.ArgumentTypeParser;
import moss.factions.shade.me.lucko.commodore.file.Token;
import moss.factions.shade.me.lucko.commodore.file.TokenStream;

public class BrigadierArgumentTypeParser
implements ArgumentTypeParser {
    public static final BrigadierArgumentTypeParser INSTANCE = new BrigadierArgumentTypeParser();

    private BrigadierArgumentTypeParser() {
    }

    @Override
    public boolean canParse(String string, String string2) {
        if (!string.equals("brigadier")) {
            return false;
        }
        switch (string2) {
            case "bool": 
            case "string": 
            case "integer": 
            case "long": 
            case "float": 
            case "double": {
                return true;
            }
        }
        return false;
    }

    @Override
    public ArgumentType<?> parse(String string, String string2, TokenStream tokenStream) {
        switch (string2) {
            case "bool": {
                return BoolArgumentType.bool();
            }
            case "string": {
                return BrigadierArgumentTypeParser.parseStringArgumentType(tokenStream);
            }
            case "integer": {
                return BrigadierArgumentTypeParser.parseIntegerArgumentType(tokenStream);
            }
            case "long": {
                return BrigadierArgumentTypeParser.parseLongArgumentType(tokenStream);
            }
            case "float": {
                return BrigadierArgumentTypeParser.parseFloatArgumentType(tokenStream);
            }
            case "double": {
                return BrigadierArgumentTypeParser.parseDoubleArgumentType(tokenStream);
            }
        }
        throw new AssertionError();
    }

    private static StringArgumentType parseStringArgumentType(TokenStream tokenStream) {
        String string;
        Token token = tokenStream.next();
        if (!(token instanceof Token.StringToken)) {
            throw tokenStream.createException("Expected string token for string type but got " + token);
        }
        switch (string = ((Token.StringToken)token).getString()) {
            case "single_word": {
                return StringArgumentType.word();
            }
            case "quotable_phrase": {
                return StringArgumentType.string();
            }
            case "greedy_phrase": {
                return StringArgumentType.greedyString();
            }
        }
        throw tokenStream.createException("Unknown string type: " + string);
    }

    private static IntegerArgumentType parseIntegerArgumentType(TokenStream tokenStream) {
        if (tokenStream.peek() instanceof Token.StringToken) {
            int n = BrigadierArgumentTypeParser.parseInt(tokenStream);
            if (tokenStream.peek() instanceof Token.StringToken) {
                int n2 = BrigadierArgumentTypeParser.parseInt(tokenStream);
                return IntegerArgumentType.integer((int)n, (int)n2);
            }
            return IntegerArgumentType.integer((int)n);
        }
        return IntegerArgumentType.integer();
    }

    private static LongArgumentType parseLongArgumentType(TokenStream tokenStream) {
        if (tokenStream.peek() instanceof Token.StringToken) {
            long l = BrigadierArgumentTypeParser.parseLong(tokenStream);
            if (tokenStream.peek() instanceof Token.StringToken) {
                long l2 = BrigadierArgumentTypeParser.parseLong(tokenStream);
                return LongArgumentType.longArg((long)l, (long)l2);
            }
            return LongArgumentType.longArg((long)l);
        }
        return LongArgumentType.longArg();
    }

    private static FloatArgumentType parseFloatArgumentType(TokenStream tokenStream) {
        if (tokenStream.peek() instanceof Token.StringToken) {
            float f = BrigadierArgumentTypeParser.parseFloat(tokenStream);
            if (tokenStream.peek() instanceof Token.StringToken) {
                float f2 = BrigadierArgumentTypeParser.parseFloat(tokenStream);
                return FloatArgumentType.floatArg((float)f, (float)f2);
            }
            return FloatArgumentType.floatArg((float)f);
        }
        return FloatArgumentType.floatArg();
    }

    private static DoubleArgumentType parseDoubleArgumentType(TokenStream tokenStream) {
        if (tokenStream.peek() instanceof Token.StringToken) {
            double d = BrigadierArgumentTypeParser.parseDouble(tokenStream);
            if (tokenStream.peek() instanceof Token.StringToken) {
                double d2 = BrigadierArgumentTypeParser.parseDouble(tokenStream);
                return DoubleArgumentType.doubleArg((double)d, (double)d2);
            }
            return DoubleArgumentType.doubleArg((double)d);
        }
        return DoubleArgumentType.doubleArg();
    }

    private static int parseInt(TokenStream tokenStream) {
        Token token = tokenStream.next();
        if (!(token instanceof Token.StringToken)) {
            throw tokenStream.createException("Expected string token for integer but got " + token);
        }
        String string = ((Token.StringToken)token).getString();
        if (string.equals("min")) {
            return Integer.MIN_VALUE;
        }
        if (string.equals("max")) {
            return Integer.MAX_VALUE;
        }
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException numberFormatException) {
            throw tokenStream.createException("Expected int but got " + string, numberFormatException);
        }
    }

    private static long parseLong(TokenStream tokenStream) {
        Token token = tokenStream.next();
        if (!(token instanceof Token.StringToken)) {
            throw tokenStream.createException("Expected string token for long but got " + token);
        }
        String string = ((Token.StringToken)token).getString();
        if (string.equals("min")) {
            return Long.MIN_VALUE;
        }
        if (string.equals("max")) {
            return Long.MAX_VALUE;
        }
        try {
            return Long.parseLong(string);
        } catch (NumberFormatException numberFormatException) {
            throw tokenStream.createException("Expected long but got " + string, numberFormatException);
        }
    }

    private static float parseFloat(TokenStream tokenStream) {
        Token token = tokenStream.next();
        if (!(token instanceof Token.StringToken)) {
            throw tokenStream.createException("Expected string token for float but got " + token);
        }
        String string = ((Token.StringToken)token).getString();
        if (string.equals("min")) {
            return Float.MIN_VALUE;
        }
        if (string.equals("max")) {
            return Float.MAX_VALUE;
        }
        try {
            return Float.parseFloat(string);
        } catch (NumberFormatException numberFormatException) {
            throw tokenStream.createException("Expected float but got " + string, numberFormatException);
        }
    }

    private static double parseDouble(TokenStream tokenStream) {
        Token token = tokenStream.next();
        if (!(token instanceof Token.StringToken)) {
            throw tokenStream.createException("Expected string token for double but got " + token);
        }
        String string = ((Token.StringToken)token).getString();
        if (string.equals("min")) {
            return Double.MIN_VALUE;
        }
        if (string.equals("max")) {
            return Double.MAX_VALUE;
        }
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException numberFormatException) {
            throw tokenStream.createException("Expected double but got " + string);
        }
    }
}

