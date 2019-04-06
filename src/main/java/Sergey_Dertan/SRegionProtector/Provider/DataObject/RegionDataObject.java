package Sergey_Dertan.SRegionProtector.Provider.DataObject;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table = "srpregions")
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
     * @see Sergey_Dertan.SRegionProtector.Region.Region#creator
     */
    @Persistent(name = "creator")
    public String creator;

    /**
     * serialized string array
     *
     * @see Sergey_Dertan.SRegionProtector.Utils.Utils#serializeStringArray(String[])
     * @see Sergey_Dertan.SRegionProtector.Utils.Utils#deserializeStringArray(String)
     */
    @Persistent(name = "owners")
    public String owners;

    /**
     * serialized string array
     *
     * @see Sergey_Dertan.SRegionProtector.Utils.Utils#serializeStringArray(String[])
     * @see Sergey_Dertan.SRegionProtector.Utils.Utils#deserializeStringArray(String)
     */
    @Persistent(name = "members")
    public String members;

    /**
     * @see Sergey_Dertan.SRegionProtector.Region.Region#priority
     */
    @Persistent(name = "priority")
    public int priority;

    public RegionDataObject() {
    }
}
