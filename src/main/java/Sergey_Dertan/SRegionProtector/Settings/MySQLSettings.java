package Sergey_Dertan.SRegionProtector.Settings;

import java.util.Map;

public final class MySQLSettings {

    public final String address;
    public final int port;
    public final String user;
    public final String password;
    public final String database;

    MySQLSettings(Map<String, Object> data) {
        this.address = (String) data.get("address");
        this.port = ((Number) data.get("port")).intValue();
        this.user = (String) data.get("user");
        this.password = (String) data.get("password");
        this.database = (String) data.get("database");
    }
}
