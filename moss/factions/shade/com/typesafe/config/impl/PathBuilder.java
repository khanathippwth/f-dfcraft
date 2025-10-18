/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.Stack;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.impl.Path;

final class PathBuilder {
    private final Stack<String> keys = new Stack();
    private Path result;

    PathBuilder() {
    }

    private void checkCanAppend() {
        if (this.result != null) {
            throw new ConfigException.BugOrBroken("Adding to PathBuilder after getting result");
        }
    }

    void appendKey(String string) {
        this.checkCanAppend();
        this.keys.push(string);
    }

    void appendPath(Path path) {
        this.checkCanAppend();
        String string = path.first();
        Path path2 = path.remainder();
        while (true) {
            this.keys.push(string);
            if (path2 == null) break;
            string = path2.first();
            path2 = path2.remainder();
        }
    }

    Path result() {
        if (this.result == null) {
            Path path = null;
            while (!this.keys.isEmpty()) {
                String string = this.keys.pop();
                path = new Path(string, path);
            }
            this.result = path;
        }
        return this.result;
    }
}

