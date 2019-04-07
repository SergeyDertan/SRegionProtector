package Sergey_Dertan.SRegionProtector.Provider.Database;

import Sergey_Dertan.SRegionProtector.Settings.MySQLSettings;
import com.mysql.cj.jdbc.Driver;
import org.datanucleus.metadata.PersistenceUnitMetaData;

public final class MySQLDataProvider extends DataBaseDataProvider {

    public MySQLDataProvider(MySQLSettings settings) {
        PersistenceUnitMetaData pumd = new PersistenceUnitMetaData("dynamic-unit", "RESOURCE_LOCAL", null);
        pumd.addProperty("javax.jdo.option.ConnectionDriverName", Driver.class.getName());
        pumd.addProperty("javax.jdo.option.ConnectionURL", "jdbc:mysql://" + settings.address + ":" + settings.port + "/" + settings.database + "?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        pumd.addProperty("javax.jdo.option.ConnectionUserName", settings.username);
        pumd.addProperty("javax.jdo.option.ConnectionPassword", settings.password);
        this.init(pumd);
    }
}
