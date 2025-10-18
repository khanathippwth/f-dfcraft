/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.loader;

import java.io.BufferedWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class AtomicFiles {
    private AtomicFiles() {
    }

    public static @NonNull Callable<BufferedWriter> createAtomicWriterFactory(@NonNull Path path, @NonNull Charset charset) {
        Objects.requireNonNull(path, "path");
        return () -> AtomicFiles.createAtomicBufferedWriter(path, charset);
    }

    public static @NonNull BufferedWriter createAtomicBufferedWriter(@NonNull Path path, @NonNull Charset charset) {
        path = path.toAbsolutePath();
        try {
            while (Files.isSymbolicLink(path)) {
                path = Files.readSymbolicLink(path);
            }
        } catch (IOException | UnsupportedOperationException exception) {
            // empty catch block
        }
        Path path2 = AtomicFiles.getTemporaryPath(path.getParent(), path.getFileName().toString());
        if (Files.exists(path, new LinkOption[0])) {
            Files.copy(path, path2, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
        }
        BufferedWriter bufferedWriter = Files.newBufferedWriter(path2, charset, new OpenOption[0]);
        return new BufferedWriter(new AtomicFileWriter(path2, path, bufferedWriter));
    }

    private static @NonNull Path getTemporaryPath(@NonNull Path path, @NonNull String string) {
        String string2 = System.nanoTime() + (long)ThreadLocalRandom.current().nextInt() + Objects.requireNonNull(string, "key").replaceAll("\\\\|/|:", "-") + ".tmp";
        return path.resolve(string2);
    }

    private static class AtomicFileWriter
    extends FilterWriter {
        private final Path targetPath;
        private final Path writePath;

        protected AtomicFileWriter(Path path, Path path2, Writer writer) {
            super(writer);
            this.writePath = path;
            this.targetPath = path2;
        }

        @Override
        public void close() {
            super.close();
            Files.createDirectories(this.targetPath.getParent(), new FileAttribute[0]);
            Files.move(this.writePath, this.targetPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

