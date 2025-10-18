/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package com.massivecraft.factions.util;

import com.massivecraft.factions.util.Mini;
import com.massivecraft.factions.util.TL;
import java.util.ArrayList;
import java.util.List;
import moss.factions.shade.net.kyori.adventure.text.Component;

public class AsciiCompass {
    public static Point getCompassPointForDirection(double d) {
        double d2 = (d - 180.0) % 360.0;
        if (d2 < 0.0) {
            d2 += 360.0;
        }
        if (0.0 <= d2 && d2 < 22.5) {
            return Point.N;
        }
        if (22.5 <= d2 && d2 < 67.5) {
            return Point.NE;
        }
        if (67.5 <= d2 && d2 < 112.5) {
            return Point.E;
        }
        if (112.5 <= d2 && d2 < 157.5) {
            return Point.SE;
        }
        if (157.5 <= d2 && d2 < 202.5) {
            return Point.S;
        }
        if (202.5 <= d2 && d2 < 247.5) {
            return Point.SW;
        }
        if (247.5 <= d2 && d2 < 292.5) {
            return Point.W;
        }
        if (292.5 <= d2 && d2 < 337.5) {
            return Point.NW;
        }
        if (337.5 <= d2 && d2 < 360.0) {
            return Point.N;
        }
        return null;
    }

    public static List<Component> getAsciiCompass(Point point, String string, String string2) {
        ArrayList<Component> arrayList = new ArrayList<Component>();
        Object object = "";
        object = (String)object + Point.NW.toString(Point.NW == point, string, string2);
        object = (String)object + Point.N.toString(Point.N == point, string, string2);
        object = (String)object + Point.NE.toString(Point.NE == point, string, string2);
        arrayList.add(Mini.parse(((String)object).replace("\\", "\\\\")));
        object = "";
        object = (String)object + Point.W.toString(Point.W == point, string, string2);
        object = (String)object + string2 + "+";
        object = (String)object + Point.E.toString(Point.E == point, string, string2);
        arrayList.add(Mini.parse(((String)object).replace("\\", "\\\\")));
        object = "";
        object = (String)object + Point.SW.toString(Point.SW == point, string, string2);
        object = (String)object + Point.S.toString(Point.S == point, string, string2);
        object = (String)object + Point.SE.toString(Point.SE == point, string, string2);
        arrayList.add(Mini.parse(((String)object).replace("\\", "\\\\")));
        return arrayList;
    }

    public static List<Component> getAsciiCompass(double d, String string, String string2) {
        return AsciiCompass.getAsciiCompass(AsciiCompass.getCompassPointForDirection(d), string, string2);
    }

    public static enum Point {
        N('N'),
        NE('/'),
        E('E'),
        SE('\\'),
        S('S'),
        SW('/'),
        W('W'),
        NW('\\');

        public final char asciiChar;

        private Point(char c) {
            this.asciiChar = c;
        }

        public String toString() {
            return String.valueOf(this.asciiChar);
        }

        public String getTranslation() {
            if (this == N) {
                return TL.COMPASS_SHORT_NORTH.toString();
            }
            if (this == E) {
                return TL.COMPASS_SHORT_EAST.toString();
            }
            if (this == S) {
                return TL.COMPASS_SHORT_SOUTH.toString();
            }
            if (this == W) {
                return TL.COMPASS_SHORT_WEST.toString();
            }
            return this.toString();
        }

        public String toString(boolean bl, String string, String string2) {
            return (bl ? string : string2) + this.getTranslation();
        }
    }
}

