/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigObject;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigBoolean;
import moss.factions.shade.com.typesafe.config.impl.ConfigDouble;
import moss.factions.shade.com.typesafe.config.impl.ConfigLong;
import moss.factions.shade.com.typesafe.config.impl.ConfigNull;
import moss.factions.shade.com.typesafe.config.impl.ConfigString;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigList;

final class DefaultTransformer {
    DefaultTransformer() {
    }

    static AbstractConfigValue transform(AbstractConfigValue abstractConfigValue, ConfigValueType configValueType) {
        if (abstractConfigValue.valueType() == ConfigValueType.STRING) {
            String string = (String)abstractConfigValue.unwrapped();
            switch (configValueType) {
                case NUMBER: {
                    try {
                        Long l = Long.parseLong(string);
                        return new ConfigLong(abstractConfigValue.origin(), l, string);
                    } catch (NumberFormatException numberFormatException) {
                        try {
                            Double d = Double.parseDouble(string);
                            return new ConfigDouble(abstractConfigValue.origin(), d, string);
                        } catch (NumberFormatException numberFormatException2) {
                            break;
                        }
                    }
                }
                case NULL: {
                    if (!string.equals("null")) break;
                    return new ConfigNull(abstractConfigValue.origin());
                }
                case BOOLEAN: {
                    if (string.equals("true") || string.equals("yes") || string.equals("on")) {
                        return new ConfigBoolean(abstractConfigValue.origin(), true);
                    }
                    if (!string.equals("false") && !string.equals("no") && !string.equals("off")) break;
                    return new ConfigBoolean(abstractConfigValue.origin(), false);
                }
                case LIST: {
                    break;
                }
                case OBJECT: {
                    break;
                }
            }
        } else if (configValueType == ConfigValueType.STRING) {
            switch (abstractConfigValue.valueType()) {
                case NUMBER: 
                case BOOLEAN: {
                    return new ConfigString.Quoted(abstractConfigValue.origin(), abstractConfigValue.transformToString());
                }
                case NULL: {
                    break;
                }
                case OBJECT: {
                    break;
                }
                case LIST: {
                    break;
                }
            }
        } else if (configValueType == ConfigValueType.LIST && abstractConfigValue.valueType() == ConfigValueType.OBJECT) {
            AbstractConfigObject abstractConfigObject = (AbstractConfigObject)abstractConfigValue;
            HashMap<Integer, AbstractConfigValue> hashMap = new HashMap<Integer, AbstractConfigValue>();
            for (Object object : abstractConfigObject.keySet()) {
                try {
                    int n = Integer.parseInt((String)object, 10);
                    if (n < 0) continue;
                    hashMap.put(n, abstractConfigObject.get(object));
                } catch (NumberFormatException numberFormatException) {}
            }
            if (!hashMap.isEmpty()) {
                Object object;
                ArrayList arrayList = new ArrayList(hashMap.entrySet());
                Collections.sort(arrayList, new Comparator<Map.Entry<Integer, AbstractConfigValue>>(){

                    @Override
                    public int compare(Map.Entry<Integer, AbstractConfigValue> entry, Map.Entry<Integer, AbstractConfigValue> entry2) {
                        return Integer.compare(entry.getKey(), entry2.getKey());
                    }
                });
                object = new ArrayList();
                Iterator iterator = arrayList.iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry)iterator.next();
                    ((ArrayList)object).add(entry.getValue());
                }
                return new SimpleConfigList(abstractConfigValue.origin(), (List<AbstractConfigValue>)object);
            }
        }
        return abstractConfigValue;
    }
}

