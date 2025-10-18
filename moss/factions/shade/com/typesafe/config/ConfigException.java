/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.com.typesafe.config;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import moss.factions.shade.com.typesafe.config.ConfigOrigin;
import moss.factions.shade.com.typesafe.config.impl.ConfigImplUtil;

public abstract class ConfigException
extends RuntimeException
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final transient ConfigOrigin origin;

    protected ConfigException(ConfigOrigin configOrigin, String string, Throwable throwable) {
        super(configOrigin.description() + ": " + string, throwable);
        this.origin = configOrigin;
    }

    protected ConfigException(ConfigOrigin configOrigin, String string) {
        this(configOrigin.description() + ": " + string, null);
    }

    protected ConfigException(String string, Throwable throwable) {
        super(string, throwable);
        this.origin = null;
    }

    protected ConfigException(String string) {
        this(string, null);
    }

    public ConfigOrigin origin() {
        return this.origin;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) {
        objectOutputStream.defaultWriteObject();
        ConfigImplUtil.writeOrigin(objectOutputStream, this.origin);
    }

    private static <T> void setOriginField(T t, Class<T> clazz, ConfigOrigin configOrigin) {
        Field field;
        try {
            field = clazz.getDeclaredField("origin");
        } catch (NoSuchFieldException noSuchFieldException) {
            throw new IOException(clazz.getSimpleName() + " has no origin field?", noSuchFieldException);
        } catch (SecurityException securityException) {
            throw new IOException("unable to fill out origin field in " + clazz.getSimpleName(), securityException);
        }
        field.setAccessible(true);
        try {
            field.set(t, configOrigin);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new IOException("unable to set origin field", illegalArgumentException);
        } catch (IllegalAccessException illegalAccessException) {
            throw new IOException("unable to set origin field", illegalAccessException);
        }
    }

    private void readObject(ObjectInputStream objectInputStream) {
        objectInputStream.defaultReadObject();
        ConfigOrigin configOrigin = ConfigImplUtil.readOrigin(objectInputStream);
        ConfigException.setOriginField(this, ConfigException.class, configOrigin);
    }

    public static class Generic
    extends ConfigException {
        private static final long serialVersionUID = 1L;

        public Generic(String string, Throwable throwable) {
            super(string, throwable);
        }

        public Generic(String string) {
            this(string, null);
        }
    }

    public static class BadBean
    extends BugOrBroken {
        private static final long serialVersionUID = 1L;

        public BadBean(String string, Throwable throwable) {
            super(string, throwable);
        }

        public BadBean(String string) {
            this(string, null);
        }
    }

    public static class ValidationFailed
    extends ConfigException {
        private static final long serialVersionUID = 1L;
        private final Iterable<ValidationProblem> problems;

        public ValidationFailed(Iterable<ValidationProblem> iterable) {
            super(ValidationFailed.makeMessage(iterable), null);
            this.problems = iterable;
        }

        public Iterable<ValidationProblem> problems() {
            return this.problems;
        }

        private static String makeMessage(Iterable<ValidationProblem> iterable) {
            StringBuilder stringBuilder = new StringBuilder();
            for (ValidationProblem validationProblem : iterable) {
                stringBuilder.append(validationProblem.origin().description());
                stringBuilder.append(": ");
                stringBuilder.append(validationProblem.path());
                stringBuilder.append(": ");
                stringBuilder.append(validationProblem.problem());
                stringBuilder.append(", ");
            }
            if (stringBuilder.length() == 0) {
                throw new BugOrBroken("ValidationFailed must have a non-empty list of problems");
            }
            stringBuilder.setLength(stringBuilder.length() - 2);
            return stringBuilder.toString();
        }
    }

    public static class ValidationProblem
    implements Serializable {
        private final String path;
        private final transient ConfigOrigin origin;
        private final String problem;

        public ValidationProblem(String string, ConfigOrigin configOrigin, String string2) {
            this.path = string;
            this.origin = configOrigin;
            this.problem = string2;
        }

        public String path() {
            return this.path;
        }

        public ConfigOrigin origin() {
            return this.origin;
        }

        public String problem() {
            return this.problem;
        }

        private void writeObject(ObjectOutputStream objectOutputStream) {
            objectOutputStream.defaultWriteObject();
            ConfigImplUtil.writeOrigin(objectOutputStream, this.origin);
        }

        private void readObject(ObjectInputStream objectInputStream) {
            objectInputStream.defaultReadObject();
            ConfigOrigin configOrigin = ConfigImplUtil.readOrigin(objectInputStream);
            ConfigException.setOriginField(this, ValidationProblem.class, configOrigin);
        }

        public String toString() {
            return "ValidationProblem(" + this.path + "," + this.origin + "," + this.problem + ")";
        }
    }

    public static class NotResolved
    extends BugOrBroken {
        private static final long serialVersionUID = 1L;

        public NotResolved(String string, Throwable throwable) {
            super(string, throwable);
        }

        public NotResolved(String string) {
            this(string, null);
        }
    }

    public static class UnresolvedSubstitution
    extends Parse {
        private static final long serialVersionUID = 1L;
        private final String detail;

        public UnresolvedSubstitution(ConfigOrigin configOrigin, String string, Throwable throwable) {
            super(configOrigin, "Could not resolve substitution to a value: " + string, throwable);
            this.detail = string;
        }

        public UnresolvedSubstitution(ConfigOrigin configOrigin, String string) {
            this(configOrigin, string, null);
        }

        private UnresolvedSubstitution(UnresolvedSubstitution unresolvedSubstitution, ConfigOrigin configOrigin, String string) {
            super(configOrigin, string, unresolvedSubstitution);
            this.detail = unresolvedSubstitution.detail;
        }

        public UnresolvedSubstitution addExtraDetail(String string) {
            return new UnresolvedSubstitution(this, this.origin(), String.format(string, this.detail));
        }
    }

    public static class Parse
    extends ConfigException {
        private static final long serialVersionUID = 1L;

        public Parse(ConfigOrigin configOrigin, String string, Throwable throwable) {
            super(configOrigin, string, throwable);
        }

        public Parse(ConfigOrigin configOrigin, String string) {
            this(configOrigin, string, null);
        }
    }

    public static class IO
    extends ConfigException {
        private static final long serialVersionUID = 1L;

        public IO(ConfigOrigin configOrigin, String string, Throwable throwable) {
            super(configOrigin, string, throwable);
        }

        public IO(ConfigOrigin configOrigin, String string) {
            this(configOrigin, string, null);
        }
    }

    public static class BugOrBroken
    extends ConfigException {
        private static final long serialVersionUID = 1L;

        public BugOrBroken(String string, Throwable throwable) {
            super(string, throwable);
        }

        public BugOrBroken(String string) {
            this(string, null);
        }
    }

    public static class BadPath
    extends ConfigException {
        private static final long serialVersionUID = 1L;

        public BadPath(ConfigOrigin configOrigin, String string, String string2, Throwable throwable) {
            super(configOrigin, string != null ? "Invalid path '" + string + "': " + string2 : string2, throwable);
        }

        public BadPath(ConfigOrigin configOrigin, String string, String string2) {
            this(configOrigin, string, string2, null);
        }

        public BadPath(String string, String string2, Throwable throwable) {
            super(string != null ? "Invalid path '" + string + "': " + string2 : string2, throwable);
        }

        public BadPath(String string, String string2) {
            this(string, string2, null);
        }

        public BadPath(ConfigOrigin configOrigin, String string) {
            this(configOrigin, null, string);
        }
    }

    public static class BadValue
    extends ConfigException {
        private static final long serialVersionUID = 1L;

        public BadValue(ConfigOrigin configOrigin, String string, String string2, Throwable throwable) {
            super(configOrigin, "Invalid value at '" + string + "': " + string2, throwable);
        }

        public BadValue(ConfigOrigin configOrigin, String string, String string2) {
            this(configOrigin, string, string2, null);
        }

        public BadValue(String string, String string2, Throwable throwable) {
            super("Invalid value at '" + string + "': " + string2, throwable);
        }

        public BadValue(String string, String string2) {
            this(string, string2, null);
        }
    }

    public static class Null
    extends Missing {
        private static final long serialVersionUID = 1L;

        private static String makeMessage(String string, String string2) {
            if (string2 != null) {
                return "Configuration key '" + string + "' is set to null but expected " + string2;
            }
            return "Configuration key '" + string + "' is null";
        }

        public Null(ConfigOrigin configOrigin, String string, String string2, Throwable throwable) {
            super(configOrigin, Null.makeMessage(string, string2), throwable);
        }

        public Null(ConfigOrigin configOrigin, String string, String string2) {
            this(configOrigin, string, string2, null);
        }
    }

    public static class Missing
    extends ConfigException {
        private static final long serialVersionUID = 1L;

        public Missing(String string, Throwable throwable) {
            super("No configuration setting found for key '" + string + "'", throwable);
        }

        public Missing(ConfigOrigin configOrigin, String string) {
            this(configOrigin, "No configuration setting found for key '" + string + "'", null);
        }

        public Missing(String string) {
            this(string, null);
        }

        protected Missing(ConfigOrigin configOrigin, String string, Throwable throwable) {
            super(configOrigin, string, throwable);
        }
    }

    public static class WrongType
    extends ConfigException {
        private static final long serialVersionUID = 1L;

        public WrongType(ConfigOrigin configOrigin, String string, String string2, String string3, Throwable throwable) {
            super(configOrigin, string + " has type " + string3 + " rather than " + string2, throwable);
        }

        public WrongType(ConfigOrigin configOrigin, String string, String string2, String string3) {
            this(configOrigin, string, string2, string3, null);
        }

        public WrongType(ConfigOrigin configOrigin, String string, Throwable throwable) {
            super(configOrigin, string, throwable);
        }

        public WrongType(ConfigOrigin configOrigin, String string) {
            super(configOrigin, string, null);
        }
    }
}

