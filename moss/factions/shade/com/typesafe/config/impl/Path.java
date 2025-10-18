/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.Iterator;
import java.util.List;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.impl.ConfigImplUtil;
import moss.factions.shade.com.typesafe.config.impl.PathBuilder;
import moss.factions.shade.com.typesafe.config.impl.PathParser;

final class Path {
    private final String first;
    private final Path remainder;

    Path(String string, Path path) {
        this.first = string;
        this.remainder = path;
    }

    Path(String ... stringArray) {
        if (stringArray.length == 0) {
            throw new ConfigException.BugOrBroken("empty path");
        }
        this.first = stringArray[0];
        if (stringArray.length > 1) {
            PathBuilder pathBuilder = new PathBuilder();
            for (int i = 1; i < stringArray.length; ++i) {
                pathBuilder.appendKey(stringArray[i]);
            }
            this.remainder = pathBuilder.result();
        } else {
            this.remainder = null;
        }
    }

    Path(List<Path> list) {
        this(list.iterator());
    }

    Path(Iterator<Path> iterator) {
        if (!iterator.hasNext()) {
            throw new ConfigException.BugOrBroken("empty path");
        }
        Path path = iterator.next();
        this.first = path.first;
        PathBuilder pathBuilder = new PathBuilder();
        if (path.remainder != null) {
            pathBuilder.appendPath(path.remainder);
        }
        while (iterator.hasNext()) {
            pathBuilder.appendPath(iterator.next());
        }
        this.remainder = pathBuilder.result();
    }

    String first() {
        return this.first;
    }

    Path remainder() {
        return this.remainder;
    }

    Path parent() {
        if (this.remainder == null) {
            return null;
        }
        PathBuilder pathBuilder = new PathBuilder();
        Path path = this;
        while (path.remainder != null) {
            pathBuilder.appendKey(path.first);
            path = path.remainder;
        }
        return pathBuilder.result();
    }

    String last() {
        Path path = this;
        while (path.remainder != null) {
            path = path.remainder;
        }
        return path.first;
    }

    Path prepend(Path path) {
        PathBuilder pathBuilder = new PathBuilder();
        pathBuilder.appendPath(path);
        pathBuilder.appendPath(this);
        return pathBuilder.result();
    }

    int length() {
        int n = 1;
        Path path = this.remainder;
        while (path != null) {
            ++n;
            path = path.remainder;
        }
        return n;
    }

    Path subPath(int n) {
        Path path = this;
        for (int i = n; path != null && i > 0; --i) {
            path = path.remainder;
        }
        return path;
    }

    Path subPath(int n, int n2) {
        if (n2 < n) {
            throw new ConfigException.BugOrBroken("bad call to subPath");
        }
        Path path = this.subPath(n);
        PathBuilder pathBuilder = new PathBuilder();
        for (int i = n2 - n; i > 0; --i) {
            pathBuilder.appendKey(path.first());
            if ((path = path.remainder()) != null) continue;
            throw new ConfigException.BugOrBroken("subPath lastIndex out of range " + n2);
        }
        return pathBuilder.result();
    }

    boolean startsWith(Path path) {
        Path path2 = this;
        Path path3 = path;
        if (path3.length() <= path2.length()) {
            while (path3 != null) {
                if (!path3.first().equals(path2.first())) {
                    return false;
                }
                path2 = path2.remainder();
                path3 = path3.remainder();
            }
            return true;
        }
        return false;
    }

    public boolean equals(Object object) {
        if (object instanceof Path) {
            Path path = (Path)object;
            return this.first.equals(path.first) && ConfigImplUtil.equalsHandlingNull(this.remainder, path.remainder);
        }
        return false;
    }

    public int hashCode() {
        return 41 * (41 + this.first.hashCode()) + (this.remainder == null ? 0 : this.remainder.hashCode());
    }

    static boolean hasFunkyChars(String string) {
        int n = string.length();
        if (n == 0) {
            return false;
        }
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '-' || c == '_') continue;
            return true;
        }
        return false;
    }

    private void appendToStringBuilder(StringBuilder stringBuilder) {
        if (Path.hasFunkyChars(this.first) || this.first.isEmpty()) {
            stringBuilder.append(ConfigImplUtil.renderJsonString(this.first));
        } else {
            stringBuilder.append(this.first);
        }
        if (this.remainder != null) {
            stringBuilder.append(".");
            this.remainder.appendToStringBuilder(stringBuilder);
        }
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Path(");
        this.appendToStringBuilder(stringBuilder);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    String render() {
        StringBuilder stringBuilder = new StringBuilder();
        this.appendToStringBuilder(stringBuilder);
        return stringBuilder.toString();
    }

    static Path newKey(String string) {
        return new Path(string, null);
    }

    static Path newPath(String string) {
        return PathParser.parsePath(string);
    }
}

