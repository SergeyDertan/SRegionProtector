package Sergey_Dertan.SRegionProtector.Provider.DataObject;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(table = "srpflags")
public final class FlagListDataObject {

    @Persistent(name = "state")
    public String state; //serialized boolean array
    @Persistent(name = "sell")
    public long sellData; //region price
    @Persistent(name = "teleport")
    public String teleportData; //json serialized Map<String, Object>
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private long id;

    public FlagListDataObject(String state, String teleport, long sell) {
        this.state = state;
        this.teleportData = teleport;
        this.sellData = sell;
    }

    public FlagListDataObject() {
    }

    public long getId() {
        return this.id;
    }

    public long getSellData() {
        return this.sellData;
    }

    public void setSellData(long sellData) {
        this.sellData = sellData;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTeleportData() {
        return this.teleportData;
    }

    public void setTeleportData(String teleportData) {
        this.teleportData = teleportData;
    }
}
