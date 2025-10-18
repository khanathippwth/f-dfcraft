/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

public final class ConfigRenderOptions {
    private final boolean originComments;
    private final boolean comments;
    private final boolean formatted;
    private final boolean json;

    private ConfigRenderOptions(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        this.originComments = bl;
        this.comments = bl2;
        this.formatted = bl3;
        this.json = bl4;
    }

    public static ConfigRenderOptions defaults() {
        return new ConfigRenderOptions(true, true, true, true);
    }

    public static ConfigRenderOptions concise() {
        return new ConfigRenderOptions(false, false, false, true);
    }

    public ConfigRenderOptions setComments(boolean bl) {
        if (bl == this.comments) {
            return this;
        }
        return new ConfigRenderOptions(this.originComments, bl, this.formatted, this.json);
    }

    public boolean getComments() {
        return this.comments;
    }

    public ConfigRenderOptions setOriginComments(boolean bl) {
        if (bl == this.originComments) {
            return this;
        }
        return new ConfigRenderOptions(bl, this.comments, this.formatted, this.json);
    }

    public boolean getOriginComments() {
        return this.originComments;
    }

    public ConfigRenderOptions setFormatted(boolean bl) {
        if (bl == this.formatted) {
            return this;
        }
        return new ConfigRenderOptions(this.originComments, this.comments, bl, this.json);
    }

    public boolean getFormatted() {
        return this.formatted;
    }

    public ConfigRenderOptions setJson(boolean bl) {
        if (bl == this.json) {
            return this;
        }
        return new ConfigRenderOptions(this.originComments, this.comments, this.formatted, bl);
    }

    public boolean getJson() {
        return this.json;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("ConfigRenderOptions(");
        if (this.originComments) {
            stringBuilder.append("originComments,");
        }
        if (this.comments) {
            stringBuilder.append("comments,");
        }
        if (this.formatted) {
            stringBuilder.append("formatted,");
        }
        if (this.json) {
            stringBuilder.append("json,");
        }
        if (stringBuilder.charAt(stringBuilder.length() - 1) == ',') {
            stringBuilder.setLength(stringBuilder.length() - 1);
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}

