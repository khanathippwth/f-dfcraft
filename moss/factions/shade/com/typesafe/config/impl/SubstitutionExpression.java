/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import moss.factions.shade.com.typesafe.config.impl.Path;

final class SubstitutionExpression {
    private final Path path;
    private final boolean optional;

    SubstitutionExpression(Path path, boolean bl) {
        this.path = path;
        this.optional = bl;
    }

    Path path() {
        return this.path;
    }

    boolean optional() {
        return this.optional;
    }

    SubstitutionExpression changePath(Path path) {
        if (path == this.path) {
            return this;
        }
        return new SubstitutionExpression(path, this.optional);
    }

    public String toString() {
        return "${" + (this.optional ? "?" : "") + this.path.render() + "}";
    }

    public boolean equals(Object object) {
        if (object instanceof SubstitutionExpression) {
            SubstitutionExpression substitutionExpression = (SubstitutionExpression)object;
            return substitutionExpression.path.equals(this.path) && substitutionExpression.optional == this.optional;
        }
        return false;
    }

    public int hashCode() {
        int n = 41 * (41 + this.path.hashCode());
        n = 41 * (n + (this.optional ? 1 : 0));
        return n;
    }
}

