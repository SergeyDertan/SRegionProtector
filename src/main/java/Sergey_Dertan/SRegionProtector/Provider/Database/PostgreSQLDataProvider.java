package Sergey_Dertan.SRegionProtector.Provider.Database;

import Sergey_Dertan.SRegionProtector.Settings.PostgreSQLSettings;
import org.datanucleus.metadata.PersistenceUnitMetaData;
import org.postgresql.Driver;

public final class PostgreSQLDataProvider extends DatabaseDataProvider {

    public PostgreSQLDataProvider(PostgreSQLSettings settings) {
        PersistenceUnitMetaData pumd = new PersistenceUnitMetaData("dynamic-unit", "RESOURCE_LOCAL", null);
        pumd.addProperty("javax.jdo.option.ConnectionDriverName", Driver.class.getName());
        pumd.addProperty("javax.jdo.option.ConnectionURL", "jdbc:postgresql://" + settings.address + ":" + settings.port + "/" + settings.database);
        pumd.addProperty("javax.jdo.option.ConnectionUserName", settings.username);
        pumd.addProperty("javax.jdo.option.ConnectionPassword", settings.password);
        this.init(pumd);
    }

    @Override
    public Type getType() {
        return Type.POSTGRESQL;
    }
}
