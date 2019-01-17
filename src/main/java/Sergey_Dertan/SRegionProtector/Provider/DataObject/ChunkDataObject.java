package Sergey_Dertan.SRegionProtector.Provider.DataObject;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public final class ChunkDataObject {

    @Persistent(column = "x")
    public long x;
    @Persistent(column = "z")
    public long z;
    @Persistent(column = "regions")
    public String regions;
    @Persistent(column = "level")
    public String level;
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private long id;

    public ChunkDataObject() {
    }

    public ChunkDataObject(long x, long z, String level, String regions) {
        this.x = x;
        this.z = z;
        this.level = level;
        this.regions = regions;
    }

    public long getId() {
        return this.id;
    }

    public long getX() {
        return this.x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public long getZ() {
        return this.z;
    }

    public void setZ(long z) {
        this.z = z;
    }

    public String getRegions() {
        return this.regions;
    }

    public void setRegions(String regions) {
        this.regions = regions;
    }
}
