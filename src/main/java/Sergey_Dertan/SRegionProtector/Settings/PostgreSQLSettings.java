package Sergey_Dertan.SRegionProtector.Settings;

import java.util.Map;

public final class PostgreSQLSettings {

    public final String address;
    public final int port;
    public final String username;
    public final String password;
    public final String database;

    PostgreSQLSettings(Map<String, Object> data) {
        this.address = (String) data.get("address");
        this.port = ((Number) data.get("port")).intValue();
        this.username = (String) data.get("username");
        this.password = (String) data.get("password");
        this.database = (String) data.get("database");
    }
}
