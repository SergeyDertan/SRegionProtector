package Sergey_Dertan.SRegionProtector.Provider;

import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.Converter;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.FlagListDataObject;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.RegionDataObject;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Settings.MySQLSettings;
import cn.nukkit.plugin.LibraryLoader;
import cn.nukkit.utils.Logger;
import cn.nukkit.utils.TextFormat;
import com.mysql.cj.jdbc.Driver;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.metadata.PersistenceUnitMetaData;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class MySQLDataProvider extends DataBaseDataProvider {

    private final MySQLSettings settings;
    private PersistenceManager pm;

    public MySQLDataProvider(Logger logger, MySQLSettings settings) throws Exception {
        super(logger);
        this.settings = settings;

        this.logger.info(TextFormat.GREEN + Messenger.getInstance().getMessage("loading.init.db-libraries"));
        this.loadLibraries();

        this.init();
    }

    private void loadLibraries() {
        LibraryLoader.load("mysql:mysql-connector-java:8.0.15");
        LibraryLoader.load("org.datanucleus:datanucleus-core:5.2.0-release");
        LibraryLoader.load("org.datanucleus:javax.jdo:3.2.0-m11");
        LibraryLoader.load("org.datanucleus:datanucleus-api-jdo:5.2.0-release");
        LibraryLoader.load("org.datanucleus:datanucleus-rdbms:5.2.0-release");
    }

    @Override
    public FlagListDataObject loadFlags(String region) {
        Query<FlagListDataObject> query = this.pm.newQuery(FlagListDataObject.class, "region = '" + region + "'");
        return query.executeResultList(FlagListDataObject.class).iterator().next();
    }

    @Override
    public List<RegionDataObject> loadRegionList() {
        Query<RegionDataObject> query = this.pm.newQuery(RegionDataObject.class);
        Collection<RegionDataObject> result = query.executeResultList(RegionDataObject.class);
        return new ArrayList<>(result);
    }

    @Override
    public RegionDataObject loadRegion(String name) {
        //TypedQuery<RegionDataObject> result=this.emf.createNamedQuery("") TODO
        return null;
    }

    @Override
    public void saveFlags(Region region) {
        Transaction tr = this.pm.currentTransaction();
        tr.begin();
        this.pm.makePersistent(Converter.toDataObject(region.getFlags()));
        tr.commit();
    }

    @Override
    public void saveRegion(Region region) {
        Transaction tr = this.pm.currentTransaction();
        tr.begin();
        this.pm.makePersistent(Converter.toDataObject(region));
        tr.commit();
    }

    @Override
    public String getName() {
        return "MySQL";
    }

    public void init() throws Exception {
        new FlagListDataObject(); //TODO
        new RegionDataObject();

        PersistenceUnitMetaData pumd = new PersistenceUnitMetaData("dynamic-unit", "RESOURCE_LOCAL", null);
        pumd.addClassName(RegionDataObject.class.getName());
        pumd.addClassName(FlagListDataObject.class.getName());
        pumd.setExcludeUnlistedClasses(true);
        pumd.addProperty("javax.jdo.option.ConnectionDriverName", Driver.class.getName());
        pumd.addProperty("javax.jdo.option.ConnectionDriverName", Driver.class.getName());
        pumd.addProperty("javax.jdo.option.ConnectionURL", "jdbc:mysql://" + this.settings.address + ":" + this.settings.port + "/acc?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        pumd.addProperty("javax.jdo.option.ConnectionUserName", this.settings.user);
        pumd.addProperty("javax.jdo.option.ConnectionPassword", this.settings.password);
        pumd.addProperty("datanucleus.autoCreateSchema", "false");
        pumd.addProperty("datanucleus.schema.autoCreateTables", "true");

        this.pm = new JDOPersistenceManagerFactory(pumd, null).getPersistenceManager();
    }

    @Override
    public boolean checkConnection() { //TODO
        return false;
    }

    @Override
    public void removeRegion(String region) {
    }
}
