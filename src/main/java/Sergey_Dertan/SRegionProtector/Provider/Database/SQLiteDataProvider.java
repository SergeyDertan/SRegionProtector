package Sergey_Dertan.SRegionProtector.Provider.Database;

import Sergey_Dertan.SRegionProtector.Provider.DataObject.FlagListDataObject;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.RegionDataObject;
import Sergey_Dertan.SRegionProtector.Settings.SQLiteSettings;
import org.datanucleus.metadata.PersistenceUnitMetaData;
import org.sqlite.JDBC;

public final class SQLiteDataProvider extends DataBaseDataProvider {

    public SQLiteDataProvider(SQLiteSettings settings) {
        PersistenceUnitMetaData pumd = new PersistenceUnitMetaData("dynamic-unit", "RESOURCE_LOCAL", null);
        pumd.addClassName(RegionDataObject.class.getName());
        pumd.addClassName(FlagListDataObject.class.getName());
        pumd.setExcludeUnlistedClasses(true);
        pumd.addProperty("javax.jdo.option.ConnectionDriverName", JDBC.class.getName());
        pumd.addProperty("javax.jdo.option.ConnectionURL", "jdbc:sqlite:" + settings.databaseFile);
        pumd.addProperty("javax.jdo.option.ConnectionUserName", "");
        pumd.addProperty("javax.jdo.option.ConnectionPassword", "");
        this.init(pumd);
    }
}
