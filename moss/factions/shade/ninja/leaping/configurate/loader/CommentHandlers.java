/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.loader;

import com.google.common.collect.Collections2;
import java.io.BufferedReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import moss.factions.shade.ninja.leaping.configurate.loader.CommentHandler;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public enum CommentHandlers implements CommentHandler
{
    HASH(new AbstractPrefixHandler("#")),
    DOUBLE_SLASH(new AbstractPrefixHandler("//")),
    SLASH_BLOCK(new AbstractDelineatedHandler("/*", "*/", "*")),
    XML_STYLE(new AbstractDelineatedHandler("<!--", "-->", "~"));

    private static final int READAHEAD_LEN = 4096;
    private final CommentHandler delegate;

    private CommentHandlers(CommentHandler commentHandler) {
        this.delegate = commentHandler;
    }

    @Override
    public @NonNull Optional<String> extractHeader(@NonNull BufferedReader bufferedReader) {
        return this.delegate.extractHeader(bufferedReader);
    }

    @Override
    public @NonNull Collection<String> toComment(@NonNull Collection<String> collection) {
        return this.delegate.toComment(collection);
    }

    public static @Nullable String extractComment(@NonNull BufferedReader bufferedReader, @NonNull CommentHandler... commentHandlerArray) {
        bufferedReader.mark(4096);
        for (CommentHandler commentHandler : commentHandlerArray) {
            Optional<String> optional = commentHandler.extractHeader(bufferedReader);
            if (optional.isPresent()) {
                return optional.get();
            }
            bufferedReader.reset();
        }
        return null;
    }

    static boolean beginsWithPrefix(String string, BufferedReader bufferedReader) {
        CharBuffer charBuffer = CharBuffer.allocate(string.length());
        if (bufferedReader.read(charBuffer) != charBuffer.limit()) {
            return false;
        }
        charBuffer.flip();
        return string.contentEquals(charBuffer);
    }

    private static final class AbstractPrefixHandler
    implements CommentHandler {
        private final String commentPrefix;

        AbstractPrefixHandler(String string) {
            this.commentPrefix = string;
        }

        @Override
        public @NonNull Optional<String> extractHeader(@NonNull BufferedReader bufferedReader) {
            if (!CommentHandlers.beginsWithPrefix(this.commentPrefix, bufferedReader)) {
                return Optional.empty();
            }
            boolean bl = true;
            StringBuilder stringBuilder = new StringBuilder();
            String string = bufferedReader.readLine();
            while (string != null) {
                if (bl || string.trim().startsWith(this.commentPrefix)) {
                    string = bl ? string : string.substring(string.indexOf(this.commentPrefix) + 1);
                    bl = false;
                    if (string.startsWith(" ")) {
                        string = string.substring(1);
                    }
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append("\n");
                    }
                } else {
                    if (string.trim().isEmpty()) break;
                    return Optional.empty();
                }
                stringBuilder.append(string);
                string = bufferedReader.readLine();
            }
            return stringBuilder.length() > 0 ? Optional.of(stringBuilder.toString()) : Optional.empty();
        }

        @Override
        public @NonNull Collection<String> toComment(@NonNull Collection<String> collection) {
            return Collections2.transform(collection, string -> {
                if (string.startsWith(" ")) {
                    return this.commentPrefix + string;
                }
                return this.commentPrefix + " " + string;
            });
        }
    }

    private static final class AbstractDelineatedHandler
    implements CommentHandler {
        private final String startSequence;
        private final String endSequence;
        private final String lineIndentSequence;

        private AbstractDelineatedHandler(String string, String string2, String string3) {
            this.startSequence = string;
            this.endSequence = string2;
            this.lineIndentSequence = string3;
        }

        @Override
        public @NonNull Optional<String> extractHeader(@NonNull BufferedReader bufferedReader) {
            if (!CommentHandlers.beginsWithPrefix(this.startSequence, bufferedReader)) {
                return Optional.empty();
            }
            StringBuilder stringBuilder = new StringBuilder();
            String string = bufferedReader.readLine();
            if (string == null) {
                return Optional.empty();
            }
            if (this.handleSingleLine(stringBuilder, string)) {
                string = bufferedReader.readLine();
                while (string != null && this.handleSingleLine(stringBuilder, string)) {
                    string = bufferedReader.readLine();
                }
            }
            if ((string = bufferedReader.readLine()) != null && !string.trim().isEmpty()) {
                return Optional.empty();
            }
            if (stringBuilder.length() > 0) {
                return Optional.of(stringBuilder.toString());
            }
            return Optional.empty();
        }

        private boolean handleSingleLine(StringBuilder stringBuilder, String string) {
            boolean bl = true;
            if (string.trim().endsWith(this.endSequence)) {
                if ((string = string.substring(0, string.lastIndexOf(this.endSequence))).endsWith(" ")) {
                    string = string.substring(0, string.length() - 1);
                }
                bl = false;
                if (string.isEmpty()) {
                    return false;
                }
            }
            if (string.trim().startsWith(this.lineIndentSequence)) {
                string = string.substring(string.indexOf(this.lineIndentSequence) + 1);
            }
            if (string.startsWith(" ")) {
                string = string.substring(1);
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(string.replace("\r", "").replace("\n", "").replace("\r\n", ""));
            return bl;
        }

        @Override
        public @NonNull Collection<String> toComment(@NonNull Collection<String> collection) {
            if (collection.size() == 1) {
                return collection.stream().map(string -> this.startSequence + " " + string + " " + this.endSequence).collect(Collectors.toList());
            }
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(this.startSequence);
            arrayList.addAll(collection.stream().map(string -> " " + this.lineIndentSequence + " " + string).collect(Collectors.toList()));
            arrayList.add(" " + this.endSequence);
            return arrayList;
        }
    }
}

