/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag;

import moss.factions.shade.net.kyori.adventure.internal.Internals;
import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import moss.factions.shade.net.kyori.examination.Examinable;

abstract class AbstractTag
implements Tag,
Examinable {
    AbstractTag() {
    }

    public final String toString() {
        return Internals.toString(this);
    }
}

