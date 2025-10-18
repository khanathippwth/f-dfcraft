/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import moss.factions.shade.com.typesafe.config.Config;
import moss.factions.shade.com.typesafe.config.ConfigException;
import moss.factions.shade.com.typesafe.config.ConfigList;
import moss.factions.shade.com.typesafe.config.ConfigObject;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.ConfigValue;
import moss.factions.shade.com.typesafe.config.ConfigValueType;
import moss.factions.shade.com.typesafe.config.impl.AbstractConfigValue;
import moss.factions.shade.com.typesafe.config.impl.ConfigBoolean;
import moss.factions.shade.com.typesafe.config.impl.ConfigDouble;
import moss.factions.shade.com.typesafe.config.impl.ConfigInt;
import moss.factions.shade.com.typesafe.config.impl.ConfigLong;
import moss.factions.shade.com.typesafe.config.impl.ConfigNull;
import moss.factions.shade.com.typesafe.config.impl.ConfigNumber;
import moss.factions.shade.com.typesafe.config.impl.ConfigString;
import moss.factions.shade.com.typesafe.config.impl.ResolveStatus;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigList;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigObject;
import moss.factions.shade.com.typesafe.config.impl.SimpleConfigOrigin;

class SerializedConfigValue
extends AbstractConfigValue
implements Externalizable {
    private static final long serialVersionUID = 1L;
    private ConfigValue value;
    private boolean wasConfig;

    public SerializedConfigValue() {
        super(null);
    }

    SerializedConfigValue(ConfigValue configValue) {
        this();
        this.value = configValue;
        this.wasConfig = false;
    }

    SerializedConfigValue(Config config) {
        this(config.root());
        this.wasConfig = true;
    }

    private Object readResolve() {
        if (this.wasConfig) {
            return ((ConfigObject)this.value).toConfig();
        }
        return this.value;
    }

    private static void writeOriginField(DataOutput dataOutput, SerializedField serializedField, Object object) {
        switch (serializedField) {
            case ORIGIN_DESCRIPTION: {
                dataOutput.writeUTF((String)object);
                break;
            }
            case ORIGIN_LINE_NUMBER: {
                dataOutput.writeInt((Integer)object);
                break;
            }
            case ORIGIN_END_LINE_NUMBER: {
                dataOutput.writeInt((Integer)object);
                break;
            }
            case ORIGIN_TYPE: {
                dataOutput.writeByte((Integer)object);
                break;
            }
            case ORIGIN_URL: {
                dataOutput.writeUTF((String)object);
                break;
            }
            case ORIGIN_RESOURCE: {
                dataOutput.writeUTF((String)object);
                break;
            }
            case ORIGIN_COMMENTS: {
                List list = (List)object;
                int n = list.size();
                dataOutput.writeInt(n);
                for (String string : list) {
                    dataOutput.writeUTF(string);
                }
                break;
            }
            case ORIGIN_NULL_URL: 
            case ORIGIN_NULL_RESOURCE: 
            case ORIGIN_NULL_COMMENTS: {
                break;
            }
            default: {
                throw new IOException("Unhandled field from origin: " + (Object)((Object)serializedField));
            }
        }
    }

    static void writeOrigin(DataOutput dataOutput, SimpleConfigOrigin simpleConfigOrigin, SimpleConfigOrigin simpleConfigOrigin2) {
        Map<Object, Object> map = simpleConfigOrigin != null ? simpleConfigOrigin.toFieldsDelta(simpleConfigOrigin2) : Collections.emptyMap();
        for (Map.Entry entry : map.entrySet()) {
            FieldOut fieldOut = new FieldOut((SerializedField)((Object)entry.getKey()));
            Object v = entry.getValue();
            SerializedConfigValue.writeOriginField(fieldOut.data, fieldOut.code, v);
            SerializedConfigValue.writeField(dataOutput, fieldOut);
        }
        SerializedConfigValue.writeEndMarker(dataOutput);
    }

    static SimpleConfigOrigin readOrigin(DataInput dataInput, SimpleConfigOrigin simpleConfigOrigin) {
        EnumMap<SerializedField, Object> enumMap = new EnumMap<SerializedField, Object>(SerializedField.class);
        while (true) {
            ArrayList<String> arrayList = null;
            SerializedField serializedField = SerializedConfigValue.readCode(dataInput);
            switch (serializedField) {
                case END_MARKER: {
                    return SimpleConfigOrigin.fromBase(simpleConfigOrigin, enumMap);
                }
                case ORIGIN_DESCRIPTION: {
                    dataInput.readInt();
                    arrayList = dataInput.readUTF();
                    break;
                }
                case ORIGIN_LINE_NUMBER: {
                    dataInput.readInt();
                    arrayList = dataInput.readInt();
                    break;
                }
                case ORIGIN_END_LINE_NUMBER: {
                    dataInput.readInt();
                    arrayList = dataInput.readInt();
                    break;
                }
                case ORIGIN_TYPE: {
                    dataInput.readInt();
                    arrayList = dataInput.readUnsignedByte();
                    break;
                }
                case ORIGIN_URL: {
                    dataInput.readInt();
                    arrayList = dataInput.readUTF();
                    break;
                }
                case ORIGIN_RESOURCE: {
                    dataInput.readInt();
                    arrayList = dataInput.readUTF();
                    break;
                }
                case ORIGIN_COMMENTS: {
                    dataInput.readInt();
                    int n = dataInput.readInt();
                    ArrayList<String> arrayList2 = new ArrayList<String>(n);
                    for (int i = 0; i < n; ++i) {
                        arrayList2.add(dataInput.readUTF());
                    }
                    arrayList = arrayList2;
                    break;
                }
                case ORIGIN_NULL_URL: 
                case ORIGIN_NULL_RESOURCE: 
                case ORIGIN_NULL_COMMENTS: {
                    dataInput.readInt();
                    arrayList = "";
                    break;
                }
                case ROOT_VALUE: 
                case ROOT_WAS_CONFIG: 
                case VALUE_DATA: 
                case VALUE_ORIGIN: {
                    throw new IOException("Not expecting this field here: " + (Object)((Object)serializedField));
                }
                case UNKNOWN: {
                    SerializedConfigValue.skipField(dataInput);
                }
            }
            if (arrayList == null) continue;
            enumMap.put(serializedField, (Object)arrayList);
        }
    }

    private static void writeValueData(DataOutput dataOutput, ConfigValue configValue) {
        SerializedValueType serializedValueType = SerializedValueType.forValue(configValue);
        dataOutput.writeByte(serializedValueType.ordinal());
        switch (serializedValueType) {
            case BOOLEAN: {
                dataOutput.writeBoolean(((ConfigBoolean)configValue).unwrapped());
                break;
            }
            case NULL: {
                break;
            }
            case INT: {
                dataOutput.writeInt(((ConfigInt)configValue).unwrapped());
                dataOutput.writeUTF(((ConfigNumber)configValue).transformToString());
                break;
            }
            case LONG: {
                dataOutput.writeLong(((ConfigLong)configValue).unwrapped());
                dataOutput.writeUTF(((ConfigNumber)configValue).transformToString());
                break;
            }
            case DOUBLE: {
                dataOutput.writeDouble(((ConfigDouble)configValue).unwrapped());
                dataOutput.writeUTF(((ConfigNumber)configValue).transformToString());
                break;
            }
            case STRING: {
                dataOutput.writeUTF(((ConfigString)configValue).unwrapped());
                break;
            }
            case LIST: {
                ConfigList configList = (ConfigList)configValue;
                dataOutput.writeInt(configList.size());
                for (ConfigValue configValue2 : configList) {
                    SerializedConfigValue.writeValue(dataOutput, configValue2, (SimpleConfigOrigin)configList.origin());
                }
                break;
            }
            case OBJECT: {
                ConfigObject configObject = (ConfigObject)configValue;
                dataOutput.writeInt(configObject.size());
                for (Map.Entry entry : configObject.entrySet()) {
                    dataOutput.writeUTF((String)entry.getKey());
                    SerializedConfigValue.writeValue(dataOutput, (ConfigValue)entry.getValue(), (SimpleConfigOrigin)configObject.origin());
                }
                break;
            }
        }
    }

    private static AbstractConfigValue readValueData(DataInput dataInput, SimpleConfigOrigin simpleConfigOrigin) {
        int n = dataInput.readUnsignedByte();
        SerializedValueType serializedValueType = SerializedValueType.forInt(n);
        if (serializedValueType == null) {
            throw new IOException("Unknown serialized value type: " + n);
        }
        switch (serializedValueType) {
            case BOOLEAN: {
                return new ConfigBoolean(simpleConfigOrigin, dataInput.readBoolean());
            }
            case NULL: {
                return new ConfigNull(simpleConfigOrigin);
            }
            case INT: {
                int n2 = dataInput.readInt();
                String string = dataInput.readUTF();
                return new ConfigInt(simpleConfigOrigin, n2, string);
            }
            case LONG: {
                long l = dataInput.readLong();
                String string = dataInput.readUTF();
                return new ConfigLong(simpleConfigOrigin, l, string);
            }
            case DOUBLE: {
                double d = dataInput.readDouble();
                String string = dataInput.readUTF();
                return new ConfigDouble(simpleConfigOrigin, d, string);
            }
            case STRING: {
                return new ConfigString.Quoted(simpleConfigOrigin, dataInput.readUTF());
            }
            case LIST: {
                int n3 = dataInput.readInt();
                ArrayList<AbstractConfigValue> arrayList = new ArrayList<AbstractConfigValue>(n3);
                for (int i = 0; i < n3; ++i) {
                    AbstractConfigValue abstractConfigValue = SerializedConfigValue.readValue(dataInput, simpleConfigOrigin);
                    arrayList.add(abstractConfigValue);
                }
                return new SimpleConfigList(simpleConfigOrigin, arrayList);
            }
            case OBJECT: {
                int n4 = dataInput.readInt();
                HashMap<String, AbstractConfigValue> hashMap = new HashMap<String, AbstractConfigValue>(n4);
                for (int i = 0; i < n4; ++i) {
                    String string = dataInput.readUTF();
                    AbstractConfigValue abstractConfigValue = SerializedConfigValue.readValue(dataInput, simpleConfigOrigin);
                    hashMap.put(string, abstractConfigValue);
                }
                return new SimpleConfigObject(simpleConfigOrigin, hashMap);
            }
        }
        throw new IOException("Unhandled serialized value type: " + (Object)((Object)serializedValueType));
    }

    private static void writeValue(DataOutput dataOutput, ConfigValue configValue, SimpleConfigOrigin simpleConfigOrigin) {
        FieldOut fieldOut = new FieldOut(SerializedField.VALUE_ORIGIN);
        SerializedConfigValue.writeOrigin(fieldOut.data, (SimpleConfigOrigin)configValue.origin(), simpleConfigOrigin);
        SerializedConfigValue.writeField(dataOutput, fieldOut);
        FieldOut fieldOut2 = new FieldOut(SerializedField.VALUE_DATA);
        SerializedConfigValue.writeValueData(fieldOut2.data, configValue);
        SerializedConfigValue.writeField(dataOutput, fieldOut2);
        SerializedConfigValue.writeEndMarker(dataOutput);
    }

    private static AbstractConfigValue readValue(DataInput dataInput, SimpleConfigOrigin simpleConfigOrigin) {
        AbstractConfigValue abstractConfigValue = null;
        SimpleConfigOrigin simpleConfigOrigin2 = null;
        while (true) {
            SerializedField serializedField;
            if ((serializedField = SerializedConfigValue.readCode(dataInput)) == SerializedField.END_MARKER) {
                if (abstractConfigValue == null) {
                    throw new IOException("No value data found in serialization of value");
                }
                return abstractConfigValue;
            }
            if (serializedField == SerializedField.VALUE_DATA) {
                if (simpleConfigOrigin2 == null) {
                    throw new IOException("Origin must be stored before value data");
                }
                dataInput.readInt();
                abstractConfigValue = SerializedConfigValue.readValueData(dataInput, simpleConfigOrigin2);
                continue;
            }
            if (serializedField == SerializedField.VALUE_ORIGIN) {
                dataInput.readInt();
                simpleConfigOrigin2 = SerializedConfigValue.readOrigin(dataInput, simpleConfigOrigin);
                continue;
            }
            SerializedConfigValue.skipField(dataInput);
        }
    }

    private static void writeField(DataOutput dataOutput, FieldOut fieldOut) {
        byte[] byArray = fieldOut.bytes.toByteArray();
        dataOutput.writeByte(fieldOut.code.ordinal());
        dataOutput.writeInt(byArray.length);
        dataOutput.write(byArray);
    }

    private static void writeEndMarker(DataOutput dataOutput) {
        dataOutput.writeByte(SerializedField.END_MARKER.ordinal());
    }

    private static SerializedField readCode(DataInput dataInput) {
        int n = dataInput.readUnsignedByte();
        if (n == SerializedField.UNKNOWN.ordinal()) {
            throw new IOException("field code " + n + " is not supposed to be on the wire");
        }
        return SerializedField.forInt(n);
    }

    private static void skipField(DataInput dataInput) {
        int n = dataInput.readInt();
        int n2 = dataInput.skipBytes(n);
        if (n2 < n) {
            byte[] byArray = new byte[n - n2];
            dataInput.readFully(byArray);
        }
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) {
        if (((AbstractConfigValue)this.value).resolveStatus() != ResolveStatus.RESOLVED) {
            throw new NotSerializableException("tried to serialize a value with unresolved substitutions, need to Config#resolve() first, see API docs");
        }
        FieldOut fieldOut = new FieldOut(SerializedField.ROOT_VALUE);
        SerializedConfigValue.writeValue(fieldOut.data, this.value, null);
        SerializedConfigValue.writeField(objectOutput, fieldOut);
        fieldOut = new FieldOut(SerializedField.ROOT_WAS_CONFIG);
        fieldOut.data.writeBoolean(this.wasConfig);
        SerializedConfigValue.writeField(objectOutput, fieldOut);
        SerializedConfigValue.writeEndMarker(objectOutput);
    }

    @Override
    public void readExternal(ObjectInput objectInput) {
        SerializedField serializedField;
        while ((serializedField = SerializedConfigValue.readCode(objectInput)) != SerializedField.END_MARKER) {
            DataInput dataInput = this.fieldIn(objectInput);
            if (serializedField == SerializedField.ROOT_VALUE) {
                this.value = SerializedConfigValue.readValue(dataInput, null);
                continue;
            }
            if (serializedField != SerializedField.ROOT_WAS_CONFIG) continue;
            this.wasConfig = dataInput.readBoolean();
        }
        return;
    }

    private DataInput fieldIn(ObjectInput objectInput) {
        byte[] byArray = new byte[objectInput.readInt()];
        objectInput.readFully(byArray);
        return new DataInputStream(new ByteArrayInputStream(byArray));
    }

    private static ConfigException shouldNotBeUsed() {
        return new ConfigException.BugOrBroken(SerializedConfigValue.class.getName() + " should not exist outside of serialization");
    }

    @Override
    public ConfigValueType valueType() {
        throw SerializedConfigValue.shouldNotBeUsed();
    }

    @Override
    public Object unwrapped() {
        throw SerializedConfigValue.shouldNotBeUsed();
    }

    @Override
    protected SerializedConfigValue newCopy(ConfigOrigin configOrigin) {
        throw SerializedConfigValue.shouldNotBeUsed();
    }

    @Override
    public final String toString() {
        return this.getClass().getSimpleName() + "(value=" + this.value + ",wasConfig=" + this.wasConfig + ")";
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof SerializedConfigValue) {
            return this.canEqual(object) && this.wasConfig == ((SerializedConfigValue)object).wasConfig && this.value.equals(((SerializedConfigValue)object).value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int n = 41 * (41 + this.value.hashCode());
        n = 41 * (n + (this.wasConfig ? 1 : 0));
        return n;
    }

    private static class FieldOut {
        final SerializedField code;
        final ByteArrayOutputStream bytes;
        final DataOutput data;

        FieldOut(SerializedField serializedField) {
            this.code = serializedField;
            this.bytes = new ByteArrayOutputStream();
            this.data = new DataOutputStream(this.bytes);
        }
    }

    private static enum SerializedValueType {
        NULL(ConfigValueType.NULL),
        BOOLEAN(ConfigValueType.BOOLEAN),
        INT(ConfigValueType.NUMBER),
        LONG(ConfigValueType.NUMBER),
        DOUBLE(ConfigValueType.NUMBER),
        STRING(ConfigValueType.STRING),
        LIST(ConfigValueType.LIST),
        OBJECT(ConfigValueType.OBJECT);

        ConfigValueType configType;

        private SerializedValueType(ConfigValueType configValueType) {
            this.configType = configValueType;
        }

        static SerializedValueType forInt(int n) {
            if (n < SerializedValueType.values().length) {
                return SerializedValueType.values()[n];
            }
            return null;
        }

        static SerializedValueType forValue(ConfigValue configValue) {
            ConfigValueType configValueType = configValue.valueType();
            if (configValueType == ConfigValueType.NUMBER) {
                if (configValue instanceof ConfigInt) {
                    return INT;
                }
                if (configValue instanceof ConfigLong) {
                    return LONG;
                }
                if (configValue instanceof ConfigDouble) {
                    return DOUBLE;
                }
            } else {
                for (SerializedValueType serializedValueType : SerializedValueType.values()) {
                    if (serializedValueType.configType != configValueType) continue;
                    return serializedValueType;
                }
            }
            throw new ConfigException.BugOrBroken("don't know how to serialize " + configValue);
        }
    }

    static enum SerializedField {
        UNKNOWN,
        END_MARKER,
        ROOT_VALUE,
        ROOT_WAS_CONFIG,
        VALUE_DATA,
        VALUE_ORIGIN,
        ORIGIN_DESCRIPTION,
        ORIGIN_LINE_NUMBER,
        ORIGIN_END_LINE_NUMBER,
        ORIGIN_TYPE,
        ORIGIN_URL,
        ORIGIN_COMMENTS,
        ORIGIN_NULL_URL,
        ORIGIN_NULL_COMMENTS,
        ORIGIN_RESOURCE,
        ORIGIN_NULL_RESOURCE;


        static SerializedField forInt(int n) {
            if (n < SerializedField.values().length) {
                return SerializedField.values()[n];
            }
            return UNKNOWN;
        }
    }
}

