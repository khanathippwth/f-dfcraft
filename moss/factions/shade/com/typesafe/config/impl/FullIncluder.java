/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import moss.factions.shade.com.typesafe.config.ConfigIncluder;
import moss.factions.shade.com.typesafe.config.ConfigIncluderClasspath;
import moss.factions.shade.com.typesafe.config.ConfigIncluderFile;
import moss.factions.shade.com.typesafe.config.ConfigIncluderURL;

interface FullIncluder
extends ConfigIncluder,
ConfigIncluderFile,
ConfigIncluderURL,
ConfigIncluderClasspath {
}

