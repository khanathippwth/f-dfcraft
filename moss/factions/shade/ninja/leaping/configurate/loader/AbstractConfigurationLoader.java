/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.loader;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.UnaryOperator;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationNode;
import moss.factions.shade.ninja.leaping.configurate.ConfigurationOptions;
import moss.factions.shade.ninja.leaping.configurate.loader.AtomicFiles;
import moss.factions.shade.ninja.leaping.configurate.loader.CommentHandler;
import moss.factions.shade.ninja.leaping.configurate.loader.CommentHandlers;
import moss.factions.shade.ninja.leaping.configurate.loader.ConfigurationLoader;
import moss.factions.shade.ninja.leaping.configurate.loader.HeaderMode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractConfigurationLoader<NodeType extends ConfigurationNode>
implements ConfigurationLoader<NodeType> {
    public static final String CONFIGURATE_LINE_SEPARATOR = "\n";
    protected static final Splitter LINE_SPLITTER = Splitter.on("\n");
    protected static final String SYSTEM_LINE_SEPARATOR = System.lineSeparator();
    protected final @Nullable Callable<BufferedReader> source;
    protected final @Nullable Callable<BufferedWriter> sink;
    private final @NonNull CommentHandler[] commentHandlers;
    private final @NonNull HeaderMode headerMode;
    private final @NonNull ConfigurationOptions defaultOptions;

    protected AbstractConfigurationLoader(@NonNull Builder<?> builder, @NonNull CommentHandler[] commentHandlerArray) {
        this.source = builder.getSource();
        this.sink = builder.getSink();
        this.headerMode = builder.getHeaderMode();
        this.commentHandlers = commentHandlerArray;
        this.defaultOptions = builder.getDefaultOptions();
    }

    public @NonNull CommentHandler getDefaultCommentHandler() {
        return this.commentHandlers[0];
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public @NonNull NodeType load(@NonNull ConfigurationOptions configurationOptions) {
        if (this.source == null) {
            throw new IOException("No source present to read from!");
        }
        try (BufferedReader bufferedReader = this.source.call();){
            String string2;
            if ((this.headerMode == HeaderMode.PRESERVE || this.headerMode == HeaderMode.NONE) && (string2 = CommentHandlers.extractComment(bufferedReader, this.commentHandlers)) != null && string2.length() > 0) {
                configurationOptions = configurationOptions.withHeader(string2);
            }
            string2 = this.createEmptyNode(configurationOptions);
            this.loadInternal(string2, bufferedReader);
            String string = string2;
            return (NodeType)string;
        } catch (FileNotFoundException | NoSuchFileException iOException) {
            return this.createEmptyNode(configurationOptions);
        } catch (Exception exception) {
            if (!(exception instanceof IOException)) throw new IOException(exception);
            throw (IOException)exception;
        }
    }

    protected abstract void loadInternal(NodeType var1, BufferedReader var2);

    @Override
    public void save(@NonNull ConfigurationNode configurationNode) {
        if (this.sink == null) {
            throw new IOException("No sink present to write to!");
        }
        try (Writer writer = this.sink.call();){
            String string;
            this.writeHeaderInternal(writer);
            if (this.headerMode != HeaderMode.NONE && (string = configurationNode.getOptions().getHeader()) != null && !string.isEmpty()) {
                for (String string2 : this.getDefaultCommentHandler().toComment(ImmutableList.copyOf(LINE_SPLITTER.split(string)))) {
                    writer.write(string2);
                    writer.write(SYSTEM_LINE_SEPARATOR);
                }
                writer.write(SYSTEM_LINE_SEPARATOR);
            }
            this.saveInternal(configurationNode, writer);
        } catch (Exception exception) {
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw new IOException(exception);
        }
    }

    protected void writeHeaderInternal(Writer writer) {
    }

    protected abstract void saveInternal(ConfigurationNode var1, Writer var2);

    @Override
    public @NonNull ConfigurationOptions getDefaultOptions() {
        return this.defaultOptions;
    }

    @Override
    public final boolean canLoad() {
        return this.source != null;
    }

    @Override
    public final boolean canSave() {
        return this.sink != null;
    }

    protected static abstract class Builder<T extends Builder<T>> {
        protected @NonNull HeaderMode headerMode = HeaderMode.PRESERVE;
        protected @Nullable Callable<BufferedReader> source;
        protected @Nullable Callable<BufferedWriter> sink;
        protected @NonNull ConfigurationOptions defaultOptions = ConfigurationOptions.defaults();

        protected Builder() {
        }

        private @NonNull T self() {
            return (T)this;
        }

        public @NonNull T setFile(@NonNull File file) {
            return this.setPath(Objects.requireNonNull(file, "file").toPath());
        }

        public @NonNull T setPath(@NonNull Path path) {
            Path path2 = Objects.requireNonNull(path, "path").toAbsolutePath();
            this.source = () -> Files.newBufferedReader(path2, StandardCharsets.UTF_8);
            this.sink = AtomicFiles.createAtomicWriterFactory(path2, StandardCharsets.UTF_8);
            return this.self();
        }

        public @NonNull T setURL(@NonNull URL uRL) {
            Objects.requireNonNull(uRL, "url");
            this.source = () -> new BufferedReader(new InputStreamReader(uRL.openConnection().getInputStream(), StandardCharsets.UTF_8));
            return this.self();
        }

        public @NonNull T setSource(@Nullable Callable<BufferedReader> callable) {
            this.source = callable;
            return this.self();
        }

        public @NonNull T setSink(@Nullable Callable<BufferedWriter> callable) {
            this.sink = callable;
            return this.self();
        }

        public @Nullable Callable<BufferedReader> getSource() {
            return this.source;
        }

        public @Nullable Callable<BufferedWriter> getSink() {
            return this.sink;
        }

        public @NonNull T setHeaderMode(@NonNull HeaderMode headerMode) {
            this.headerMode = Objects.requireNonNull(headerMode, "mode");
            return this.self();
        }

        public @NonNull HeaderMode getHeaderMode() {
            return this.headerMode;
        }

        @Deprecated
        public @NonNull T setPreservesHeader(boolean bl) {
            this.headerMode = bl ? HeaderMode.PRESERVE : HeaderMode.PRESET;
            return this.self();
        }

        @Deprecated
        public boolean preservesHeader() {
            return this.headerMode == HeaderMode.PRESERVE;
        }

        public @NonNull T setDefaultOptions(@NonNull ConfigurationOptions configurationOptions) {
            this.defaultOptions = Objects.requireNonNull(configurationOptions, "defaultOptions");
            return this.self();
        }

        public @NonNull T setDefaultOptions(@NonNull UnaryOperator<ConfigurationOptions> unaryOperator) {
            this.defaultOptions = Objects.requireNonNull((ConfigurationOptions)unaryOperator.apply(this.defaultOptions), "defaultOptions (updated)");
            return this.self();
        }

        public @NonNull ConfigurationOptions getDefaultOptions() {
            return this.defaultOptions;
        }

        public abstract @NonNull AbstractConfigurationLoader<?> build();
    }
}

