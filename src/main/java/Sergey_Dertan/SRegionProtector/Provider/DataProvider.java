package Sergey_Dertan.SRegionProtector.Provider;

import Sergey_Dertan.SRegionProtector.Provider.DataObject.FlagListDataObject;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.RegionDataObject;
import Sergey_Dertan.SRegionProtector.Region.Region;
import cn.nukkit.utils.Logger;

import java.util.Set;

public abstract class DataProvider {

    protected Logger logger;

    public DataProvider(Logger logger) {
        this.logger = logger;
    }

    public final synchronized void saveRegionList(Set<Region> regions) {
        regions.forEach(this::saveRegion);
    }

    public abstract void saveRegion(Region region);

    public abstract String getName();

    public abstract void saveFlags(Region region);

    public abstract FlagListDataObject loadFlags(String region);

    public abstract Set<RegionDataObject> loadRegionList();

    public abstract RegionDataObject loadRegion(String name);

    public abstract void removeRegion(String region);

    public final void removeRegion(Region region) {
        this.removeRegion(region.name);
    }
}
