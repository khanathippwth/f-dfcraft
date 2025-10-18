/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$NonExtendable
 */
package moss.factions.shade.net.kyori.adventure.text.minimessage.tag;

import moss.factions.shade.net.kyori.adventure.text.minimessage.tag.Tag;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ParserDirective
extends Tag {
    public static final Tag RESET = new ParserDirective(){

        public String toString() {
            return "ParserDirective.RESET";
        }
    };
}

