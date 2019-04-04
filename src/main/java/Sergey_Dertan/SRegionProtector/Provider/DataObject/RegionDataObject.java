package Sergey_Dertan.SRegionProtector.Provider.DataObject;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table = "region")
public final class RegionDataObject {

    @Persistent(name = "min-x")
    public double minX;
    @Persistent(name = "min-y")
    public double minY;
    @Persistent(name = "min-z")
    public double minZ;
    @Persistent(name = "max-x")
    public double maxX;
    @Persistent(name = "max-y")
    public double maxY;
    @Persistent(name = "max-z")
    public double maxZ;
    @Persistent(name = "name")
    public String name;
    @Persistent(name = "level")
    public String level;
    @Persistent(name = "creator")
    public String creator;
    @Persistent(name = "owners")
    public String owners;
    @Persistent(name = "members")
    public String members;
    @Persistent(name = "priority")
    public int priority;
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private long id;

    public RegionDataObject(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, String name, String level, String creator, String owners, String members, int priority) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;

        this.name = name;
        this.level = level;
        this.creator = creator;
        this.owners = owners;
        this.members = members;

        this.priority = priority;
    }

    public RegionDataObject() {
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getId() {
        return this.id;
    }

    public double getMaxX() {
        return this.maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMaxY() {
        return this.maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    public double getMaxZ() {
        return this.maxZ;
    }

    public void setMaxZ(double maxZ) {
        this.maxZ = maxZ;
    }

    public double getMinX() {
        return this.minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMinY() {
        return this.minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMinZ() {
        return minZ;
    }

    public void setMinZ(double minZ) {
        this.minZ = minZ;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMembers() {
        return this.members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwners() {
        return this.owners;
    }

    public void setOwners(String owners) {
        this.owners = owners;
    }
}
