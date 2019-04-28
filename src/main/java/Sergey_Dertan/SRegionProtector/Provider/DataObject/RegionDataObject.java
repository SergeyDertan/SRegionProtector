package Sergey_Dertan.SRegionProtector.Provider.DataObject;

import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionFlag;
import Sergey_Dertan.SRegionProtector.Region.Region;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table = "srpregions", detachable = "true")
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

    /**
     * @see Sergey_Dertan.SRegionProtector.Region.Region#name
     */
    @PrimaryKey
    @Persistent(name = "name")
    public String name;

    /**
     * @see Sergey_Dertan.SRegionProtector.Region.Region#level
     */
    @Persistent(name = "level")
    public String level;

    /**
     * @see Region#getCreator()
     */
    @Persistent(name = "creator")
    public String creator;

    /**
     * JSON serialized Collection<String>
     *
     * @see com.google.gson.Gson#toJson(Object)
     * @see Converter#toDataObject(Region)
     * @see Converter#fromDataObject(RegionDataObject, RegionFlag[])
     */
    @Persistent(name = "owners")
    public String owners;

    /**
     * JSON serialized Collection<String>
     *
     * @see com.google.gson.Gson#toJson(Object)
     * @see Converter#toDataObject(Region)
     * @see Converter#fromDataObject(RegionDataObject, RegionFlag[])
     */
    @Persistent(name = "members")
    public String members;

    /**
     * @see Region#getPriority()
     */
    @Persistent(name = "priority")
    public int priority;

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public void setOwners(String owners) {
        this.owners = owners;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
