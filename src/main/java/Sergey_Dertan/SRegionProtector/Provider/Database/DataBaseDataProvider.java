package Sergey_Dertan.SRegionProtector.Provider.Database;

import Sergey_Dertan.SRegionProtector.Provider.CloseableProvider;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.Converter;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.FlagListDataObject;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.RegionDataObject;
import Sergey_Dertan.SRegionProtector.Provider.DataProvider;
import Sergey_Dertan.SRegionProtector.Region.Region;
import org.datanucleus.metadata.PersistenceUnitMetaData;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class DataBaseDataProvider implements DataProvider, CloseableProvider {

    protected PersistenceManagerFactory factory;
    protected PersistenceManager pm;

    protected void init(PersistenceUnitMetaData pumd) {
        pumd.addClassName(RegionDataObject.class.getName());
        pumd.addClassName(FlagListDataObject.class.getName());
        pumd.setExcludeUnlistedClasses(true);
        pumd.addProperty("datanucleus.schema.autoCreateTables", "true");
        this.factory = new J(pumd);
        this.pm = this.factory.getPersistenceManager();
        this.pm.setDetachAllOnCommit(true);
    }

    @Override
    public FlagListDataObject loadFlags(String region) {
        Query<FlagListDataObject> query = this.pm.newQuery(FlagListDataObject.class, "region == '" + region + "'");
        Collection result = (Collection) query.execute();
        return result.isEmpty() ? null : ((FlagListDataObject) result.iterator().next());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RegionDataObject> loadRegionList() {
        Query<RegionDataObject> query = this.pm.newQuery(RegionDataObject.class);
        Collection<RegionDataObject> result = ((Collection<RegionDataObject>) query.execute());
        return new ArrayList<>(result);
    }

    @Override
    public RegionDataObject loadRegion(String name) {
        Query<RegionDataObject> query = this.pm.newQuery(RegionDataObject.class, "name == '" + name + "'");
        Collection result = (Collection) query.execute();
        return result.isEmpty() ? null : ((RegionDataObject) result.iterator().next());
    }

    @Override
    public void saveFlags(Region region) {
        Transaction tr = this.pm.currentTransaction();
        tr.begin();

        FlagListDataObject flags = this.loadFlags(region.name);
        if (flags == null) {
            this.pm.makePersistent(Converter.toDataObject(region.getFlags(), region.name));
        } else {
            FlagListDataObject up = Converter.toDataObject(region.getFlags(), region.name);
            if (flags.sellData != up.sellData) flags.setSellData(up.sellData);
            if (!flags.teleportData.equals(up.teleportData)) flags.setTeleportData(up.teleportData);
            if (!flags.state.equals(up.state)) flags.setState(up.state);
        }
        tr.commit();
    }

    @Override
    public void saveRegion(Region region) {
        Transaction tr = this.pm.currentTransaction();
        tr.begin();

        RegionDataObject rdo = this.loadRegion(region.name);
        if (rdo == null) {
            this.pm.makePersistent(Converter.toDataObject(region));
        } else {
            RegionDataObject up = Converter.toDataObject(region);
            if (!rdo.creator.equals(up.creator)) rdo.setCreator(up.creator);
            if (!rdo.owners.equals(up.owners)) rdo.setOwners(up.owners);
            if (!rdo.members.equals(up.members)) rdo.setMembers(up.members);
            if (rdo.priority != up.priority) rdo.setPriority(up.priority);
        }
        tr.commit();
    }

    @Override
    public void removeRegion(Region region) {
        RegionDataObject rdo = this.loadRegion(region.name);
        this.pm.deletePersistent(rdo);
    }

    @Override
    public void removeFlags(Region region) {
        FlagListDataObject fldo = this.loadFlags(region.name);
        this.pm.deletePersistent(fldo);
    }

    @Override
    public String getName() {
        return "MySQL";
    }

    @Override
    public Type getType() {
        return Type.MYSQL;
    }

    @Override
    public void close() {
        this.pm.close();
        this.factory.close();
    }
}
