package Sergey_Dertan.SRegionProtector.Region.Flags;

import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionSellFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionTeleportFlag;
import Sergey_Dertan.SRegionProtector.Utils.Utils;
import cn.nukkit.level.Position;

import java.util.HashMap;
import java.util.Map;

public final class FlagList {

    private RegionFlag[] flags;

    public FlagList(RegionFlag[] flags) {
        this.flags = flags;
    }

    public void setFlagState(int flag, boolean state) {
        this.flags[flag].state = state;
    }

    public boolean getFlagState(int flag) {
        return this.flags[flag].state;
    }

    public RegionFlag[] getFlags() {
        return this.flags;
    }

    public Map<String, Map<String, Object>> toMap() {

        Map<String, Map<String, Object>> flags = new HashMap<>();

        for (int i = 0; i < this.flags.length; ++i) {
            String name = RegionFlags.getFlagName(i);
            if (name.equals("")) continue;
            Map<String, Object> flagData = new HashMap<>();
            flagData.put("state", this.flags[i].state);
            switch (i) {
                case RegionFlags.FLAG_TELEPORT:
                    Position teleportPos = ((RegionTeleportFlag) this.flags[i]).position;
                    if (teleportPos == null) break;
                    Map<String, Object> pos = new HashMap<>();
                    pos.put("x", teleportPos.x);
                    pos.put("y", teleportPos.y);
                    pos.put("z", teleportPos.z);
                    pos.put("level", teleportPos.level.getName());
                    flagData.put("position", pos);
                    break;
                case RegionFlags.FLAG_SELL:
                    flagData.put("price", ((RegionSellFlag) this.flags[i]).price);
                    break;
            }
            flags.put(name, flagData);
        }

        return flags;
    }

    public RegionFlag getFlag(int flag) {
        return this.flags[flag];
    }

    public RegionSellFlag getSellFlag() {
        return (RegionSellFlag) this.flags[RegionFlags.FLAG_SELL];
    }

    public RegionTeleportFlag getTeleportFlag() {
        return (RegionTeleportFlag) this.flags[RegionFlags.FLAG_TELEPORT];
    }

    @Override
    public String toString() {
        String[] flags = new String[RegionFlags.FLAG_AMOUNT];
        for (int i = 0; i < this.flags.length; ++i) {
            flags[i] = RegionFlags.getFlagName(i) + ": " + (this.flags[i].state ? "enabled" : "disabled");
        }
        return String.join(", ", flags);
    }

    @Override
    public FlagList clone() {
        return new FlagList((RegionFlag[]) Utils.deepClone(this.flags));
    }
}