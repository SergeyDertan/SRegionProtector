package Sergey_Dertan.SRegionProtector.Provider;

public enum ProviderType {

    YAML("YAML"),
    MYSQL("MySQL"),
    SQLite3("SQLite3");

    public final String name;

    ProviderType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static ProviderType fromString(String name) {
        switch (name.toLowerCase()) {
            case "yaml":
            case "yml":
                return ProviderType.YAML;
            case "mysql":
                return ProviderType.MYSQL;
            case "sqlite":
            case "sqlite3":
                return ProviderType.SQLite3;
        }
        return null;
    }
}
