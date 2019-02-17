package Sergey_Dertan.SRegionProtector.Provider;

import Sergey_Dertan.SRegionProtector.Provider.DataObject.FlagListDataObject;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.RegionDataObject;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Settings.MySQLSettings;
import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Logger;
import cn.nukkit.utils.MainLogger;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.metadata.PersistenceUnitMetaData;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import java.util.List;
import java.util.Set;

//import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
//import org.datanucleus.metadata.PersistenceUnitMetaData;
/*import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;*/

public final class MySQLDataProvider extends DataBaseDataProvider {

    private MySQLSettings settings;
    //private PersistenceManager pm;

    public MySQLDataProvider(Logger logger, MySQLSettings settings) {
        super(logger);
        this.settings = settings;
        init();
    }

    @Override
    public FlagListDataObject loadFlags(String region) {
        //Query<FlagListDataObject> query = this.pm.newQuery(FlagListDataObject.class, "region = '" + region + "'");
        //return query.executeResultList(FlagListDataObject.class).iterator().next();
        return null;
    }

    @Override
    public List<RegionDataObject> loadRegionList() {
        /*Query<RegionDataObject> query = this.pm.newQuery(RegionDataObject.class);
        Collection<RegionDataObject> result = query.executeResultList(RegionDataObject.class);
        return new HashSet<>(result);*/
        return null;
    }

    @Override
    public RegionDataObject loadRegion(String name) {
        //TypedQuery<RegionDataObject> result=this.emf.createNamedQuery("") TODO
        return null;
    }

    @Override
    public void saveFlags(Region region) {
        /*Transaction tr = this.pm.currentTransaction();
        tr.begin();
        pm.makePersistent(Converter.toDataObject(region.getFlags()));
        tr.commit();*/
    }

    @Override
    public void saveRegion(Region region) {
        /*Transaction tr = this.pm.currentTransaction();
        tr.begin();
        pm.makePersistent(Converter.toDataObject(region));
        tr.commit();*/
    }

    @Override
    public String getName() {
        return "MySQL";
    }

    @Override
    public boolean init() { //jdbc:mysql://127.0.0.1:3306/?user=root
        PersistenceUnitMetaData pumd = new PersistenceUnitMetaData("dynamic-unit", "RESOURCE_LOCAL", null);
        pumd.addClassName("Sergey_Dertan.SRegionProtector.Provider.DataObject.RegionDataObject");
        pumd.addClassName("Sergey_Dertan.SRegionProtector.Provider.DataObject.ChunkDataObject");
        pumd.addClassName("Sergey_Dertan.SRegionProtector.Provider.DataObject.FlagListDataObject");
        pumd.setExcludeUnlistedClasses(true);
        pumd.addProperty("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory"); //TODO
        pumd.addProperty("javax.jdo.option.ConnectionDriverName", "com.mysql.jdbc.Driver");
        pumd.addProperty("javax.jdo.option.ConnectionURL", "jdbc:mysql://127.0.0.1:3306/acc?useSSL=false"); //"jdbc:h2:mem:mypersistence"
        pumd.addProperty("javax.jdo.option.ConnectionUserName", "root");
        pumd.addProperty("javax.jdo.option.ConnectionPassword", "pass");
        pumd.addProperty("datanucleus.autoCreateSchema", "true");

        Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(new Task() {
            public void onRun(int t) {
                MainLogger.getLogger().info("qqqwwweee");
                PersistenceManager pm = new JDOPersistenceManagerFactory(pumd, null).getPersistenceManager();
                Transaction tr = pm.currentTransaction();
                pm.close();
                MainLogger.getLogger().info("qwe");
            }
        }, 60, 60);
        return false;
    }

    @Override
    public boolean checkConnection() { //TODO
        return false;
    }

    @Override
    public void removeRegion(String region) {
    }
}
