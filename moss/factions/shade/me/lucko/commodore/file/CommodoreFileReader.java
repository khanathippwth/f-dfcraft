/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.tree.LiteralCommandNode
 */
package moss.factions.shade.me.lucko.commodore.file;

import com.mojang.brigadier.tree.LiteralCommandNode;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import moss.factions.shade.me.lucko.commodore.file.ArgumentTypeParser;
import moss.factions.shade.me.lucko.commodore.file.BrigadierArgumentTypeParser;
import moss.factions.shade.me.lucko.commodore.file.Lexer;
import moss.factions.shade.me.lucko.commodore.file.ParseException;
import moss.factions.shade.me.lucko.commodore.file.Parser;

public class CommodoreFileReader {
    public static final CommodoreFileReader INSTANCE = CommodoreFileReader.builder().withArgumentTypeParser(BrigadierArgumentTypeParser.INSTANCE).build();
    private final List<ArgumentTypeParser> argumentTypeParsers;

    public static Builder builder() {
        return new Builder();
    }

    CommodoreFileReader(List<ArgumentTypeParser> list) {
        this.argumentTypeParsers = Collections.unmodifiableList(list);
    }

    public <S> LiteralCommandNode<S> parse(Reader reader) {
        try {
            return new Parser(new Lexer(reader), this.argumentTypeParsers).parse();
        } catch (ParseException parseException) {
            if (parseException.getCause() instanceof IOException) {
                throw (IOException)parseException.getCause();
            }
            throw new IOException(parseException);
        }
    }

    public <S> LiteralCommandNode<S> parse(InputStream inputStream) {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream);){
            LiteralCommandNode<S> literalCommandNode = this.parse(inputStreamReader);
            return literalCommandNode;
        }
    }

    public <S> LiteralCommandNode<S> parse(Path path) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(path, StandardCharsets.UTF_8);){
            LiteralCommandNode<S> literalCommandNode = this.parse(bufferedReader);
            return literalCommandNode;
        }
    }

    public <S> LiteralCommandNode<S> parse(File file) {
        return this.parse(file.toPath());
    }

    public static final class Builder {
        private final List<ArgumentTypeParser> argumentTypeParsers = new ArrayList<ArgumentTypeParser>();

        Builder() {
        }

        public Builder withArgumentTypeParser(ArgumentTypeParser argumentTypeParser) {
            Objects.requireNonNull(argumentTypeParser, "argumentTypeParser");
            this.argumentTypeParsers.add(argumentTypeParser);
            return this;
        }

        public CommodoreFileReader build() {
            return new CommodoreFileReader(new ArrayList<ArgumentTypeParser>(this.argumentTypeParsers));
        }
    }
}

