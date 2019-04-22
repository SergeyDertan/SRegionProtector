package Sergey_Dertan.SRegionProtector.Provider;

import Sergey_Dertan.SRegionProtector.Provider.DataObject.FlagListDataObject;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.RegionDataObject;
import Sergey_Dertan.SRegionProtector.Region.Region;

import java.util.List;

@SuppressWarnings("unused")
public interface DataProvider { //TODO unity flags and region info into one file

    default void saveRegionList(Iterable<Region> regions) {
        regions.forEach(this::save);
    }

    void saveRegion(Region region);

    default void save(Region region) {
        this.saveRegion(region);
        this.saveFlags(region);
    }

    void saveFlags(Region region);

    FlagListDataObject loadFlags(String region);

    RegionDataObject loadRegion(String name);

    List<RegionDataObject> loadRegionList();

    void removeRegion(Region region);

    void removeFlags(Region region);

    default void remove(Region region) {
        this.removeRegion(region);
        this.removeFlags(region);
    }

    Type getType();

    default void close() {
    }

    enum Type {

        YAML,
        MYSQL,
        SQLite,
        POSTGRESQL,
        UNSUPPORTED;

        public static Type fromString(String name) {
            switch (name.toLowerCase()) {
                case "yaml":
                case "yml":
                    return YAML;
                case "mysql":
                    return MYSQL;
                case "sqlite":
                case "sqlite3":
                    return SQLite;
                case "postgresql":
                case "postgres":
                    return POSTGRESQL;
            }
            return UNSUPPORTED;
        }
    }
}
