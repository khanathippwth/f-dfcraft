/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.ninja.leaping.configurate.objectmapping;

import com.google.common.reflect.TypeToken;
import moss.factions.shade.ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class InvalidTypeException
extends ObjectMappingException {
    public static final long serialVersionUID = 6063916638575438044L;

    public InvalidTypeException(TypeToken<?> typeToken) {
        super("Invalid type presented to serializer: " + typeToken);
    }
}

