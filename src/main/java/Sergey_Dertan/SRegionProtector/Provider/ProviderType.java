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
}
