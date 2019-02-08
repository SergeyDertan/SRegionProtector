package Sergey_Dertan.SRegionProtector.Settings;

import java.util.Map;

public final class MySQLSettings {

    public final String address;
    public final int port;
    public final String user;
    public final String password;

    public MySQLSettings(Map<String, Object> data) {
        this.address = (String) data.get("address");
        this.port = ((Number) data.get("port")).intValue();
        this.user = (String) data.get("user");
        this.password = (String) data.get("password");
    }

    public int getPort() {
        return this.port;
    }

    public String getAddress() {
        return this.address;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUser() {
        return this.user;
    }
}
