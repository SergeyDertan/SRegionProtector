package Sergey_Dertan.SRegionProtector.Provider.DataObject;

import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionFlag;
import Sergey_Dertan.SRegionProtector.Region.Region;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table = "srpflags", detachable = "true")
public final class FlagListDataObject {

    /**
     * JSON serialized boolean array
     *
     * @see com.alibaba.fastjson.JSON#toJSONString(Object)
     */
    @Persistent(name = "state")
    public String state;

    /**
     * region price
     *
     * @see Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionSellFlag
     */
    @Persistent(name = "sell")
    public long sellData;

    /**
     * JSON serialized Map<String, Object>
     *
     * @see com.alibaba.fastjson.JSON#toJSONString(Object)
     * @see Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionTeleportFlag
     * @see Converter#toDataObject(RegionFlag[], String)
     * @see Region#getTeleportFlagPos()
     */
    @Persistent(name = "teleport")
    public String teleportData;

    /**
     * @see Region#getName()
     */
    @PrimaryKey
    @Persistent(name = "region")
    public String region;

    public void setSellData(long sellData) {
        this.sellData = sellData;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setTeleportData(String teleportData) {
        this.teleportData = teleportData;
    }
}
