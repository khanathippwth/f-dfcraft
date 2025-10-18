/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 */
package moss.factions.shade.me.lucko.commodore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;

final class ReflectionUtil {
    private static final String SERVER_VERSION = ReflectionUtil.getServerVersion();

    private static String getServerVersion() {
        Class<?> clazz = Bukkit.getServer().getClass();
        if (!clazz.getSimpleName().equals("CraftServer")) {
            return ".";
        }
        if (clazz.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
            return ".";
        }
        String string = clazz.getName().substring("org.bukkit.craftbukkit".length());
        return string.substring(0, string.length() - "CraftServer".length());
    }

    public static String mc(String string) {
        return "net.minecraft." + string;
    }

    public static String nms(String string) {
        return "net.minecraft.server" + SERVER_VERSION + string;
    }

    public static Class<?> mcClass(String string) {
        return Class.forName(ReflectionUtil.mc(string));
    }

    public static Class<?> nmsClass(String string) {
        return Class.forName(ReflectionUtil.nms(string));
    }

    public static String obc(String string) {
        return "org.bukkit.craftbukkit" + SERVER_VERSION + string;
    }

    public static Class<?> obcClass(String string) {
        return Class.forName(ReflectionUtil.obc(string));
    }

    public static int minecraftVersion() {
        try {
            Matcher matcher = Pattern.compile("\\(MC: (\\d)\\.(\\d+)\\.?(\\d+?)?( .*)?\\)").matcher(Bukkit.getVersion());
            if (matcher.find()) {
                return Integer.parseInt(matcher.toMatchResult().group(2), 10);
            }
            throw new IllegalArgumentException(String.format("No match found in '%s'", Bukkit.getVersion()));
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new RuntimeException("Failed to determine Minecraft version", illegalArgumentException);
        }
    }

    private ReflectionUtil() {
    }
}

