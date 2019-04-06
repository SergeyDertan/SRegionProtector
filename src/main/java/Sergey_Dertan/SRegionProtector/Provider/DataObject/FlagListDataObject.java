package Sergey_Dertan.SRegionProtector.Provider.DataObject;

import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionFlag;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table = "srpflags")
public final class FlagListDataObject {

    /**
     * serialized boolean array
     *
     * @see Sergey_Dertan.SRegionProtector.Utils.Utils#serializeBooleanArray(boolean[])
     */
    @Column(jdbcType = "LONGVARCHAR")
    @Persistent(name = "state")
    public String state;

    /**
     * @see Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionSellFlag
     */
    @Persistent(name = "sell")
    public long sellData; //region price

    /**
     * JSON serialized Map<String, Object>
     *
     * @see com.alibaba.fastjson.JSON#toJSONString(Object)
     * @see Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionTeleportFlag
     * @see Converter#toDataObject(RegionFlag[], String)
     */
    @Persistent(name = "teleport")
    public String teleportData;

    /**
     * @see Sergey_Dertan.SRegionProtector.Region.Region#name
     */
    @PrimaryKey
    @Persistent(name = "region")
    public String region;

    public FlagListDataObject() {
    }
}
