package Sergey_Dertan.SRegionProtector.Provider.DataObject;

import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionSellFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionTeleportFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Utils.Utils;
import cn.nukkit.math.Vector3;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

import static Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags.*;
import static Sergey_Dertan.SRegionProtector.Utils.Tags.*;

public abstract class Converter {

    private Converter() {
    }

    public static RegionDataObject toDataObject(Region region) {
        return new RegionDataObject(
                region.minX, region.minY, region.minZ,
                region.maxX, region.maxY, region.maxZ,
                region.name, region.level, region.getCreator(),
                Utils.serializeStringArray(region.getOwners().toArray(new String[0])),
                Utils.serializeStringArray(region.getMembers().toArray(new String[0]))
        );
    }

    public static RegionDataObject toRegionDataObject(Map<String, Object> data) { //for the yaml data provider
        return new RegionDataObject(
                ((Number) data.get(MIN_X_TAG)).longValue(), ((Number) data.get(MIN_Y_TAG)).longValue(), ((Number) data.get(MIN_Z_TAG)).longValue(),
                ((Number) data.get(MAX_X_TAG)).longValue(), ((Number) data.get(MAX_Y_TAG)).longValue(), ((Number) data.get(MAX_Z_TAG)).longValue(),
                (String) data.get(NAME_TAG), (String) data.get(LEVEL_TAG), (String) data.get(CREATOR_TAG),
                (String) data.get(OWNERS_TAG), (String) data.get(MEMBERS_TAG)
        );
    }

    public static FlagListDataObject toDataObject(RegionFlag[] flags) {
        boolean[] state = new boolean[flags.length];
        for (int i = 0; i < flags.length; ++i) {
            state[i] = flags[i].state;
        }
        RegionTeleportFlag tpFlag = (RegionTeleportFlag) flags[FLAG_TELEPORT];
        Map<String, Object> teleport = new HashMap<>();
        teleport.put(X_TAG, tpFlag.position.x);
        teleport.put(Y_TAG, tpFlag.position.y);
        teleport.put(Z_TAG, tpFlag.position.z);
        teleport.put(LEVEL_TAG, tpFlag.level);
        return new FlagListDataObject(Utils.serializeBooleanArray(state), JSON.toJSONString(teleport), ((RegionSellFlag) flags[RegionFlags.FLAG_SELL]).price);
    }

    public static Region fromDataObject(RegionDataObject dataObject, RegionFlag[] flags) {
        return new Region(
                dataObject.name, dataObject.creator,
                dataObject.level,
                dataObject.minX, dataObject.minY, dataObject.minZ,
                dataObject.maxX, dataObject.maxY, dataObject.maxZ,
                Utils.deserializeStringArray(dataObject.owners),
                Utils.deserializeStringArray(dataObject.members),
                flags
        );
    }

    public static Region fromDataObject(RegionDataObject dataObject, FlagListDataObject flagsDataObject) {
        return fromDataObject(dataObject, fromDataObject(flagsDataObject));
    }

    @SuppressWarnings("unchecked")
    public static RegionFlag[] fromDataObject(FlagListDataObject dataObject) {
        RegionFlag[] flags = new RegionFlag[RegionFlags.FLAG_AMOUNT];
        boolean[] state = Utils.deserializeBooleanArray(dataObject.state);
        Map<String, Object> teleportData = (Map<String, Object>) JSON.parse(dataObject.teleportData);
        for (int i = 0; i < state.length; ++i) {
            if (i == FLAG_TELEPORT) {
                double x = ((Number) teleportData.get(X_TAG)).doubleValue();
                double y = ((Number) teleportData.get(Y_TAG)).doubleValue();
                double z = ((Number) teleportData.get(Z_TAG)).doubleValue();
                String level = (String) teleportData.get(LEVEL_TAG);
                flags[i] = new RegionTeleportFlag(state[i], new Vector3(x, y, z), level);
                continue;
            }
            if (i == FLAG_SELL) {
                flags[i] = new RegionSellFlag(state[i], dataObject.sellData);
                continue;
            }
            flags[i] = new RegionFlag(state[i]);
        }
        return flags;
    }

    @SuppressWarnings("unchecked")
    public static FlagListDataObject toDataObject(Map<String, Map<String, Object>> data) { //for the yaml data provider
        FlagListDataObject dataObject = new FlagListDataObject();
        boolean[] state = new boolean[RegionFlags.FLAG_AMOUNT];
        for (Map.Entry<String, Map<String, Object>> flag : data.entrySet()) {
            state[getFlagId(flag.getKey())] = (boolean) flag.getValue().get(STATE_TAG);
            if (getFlagId(flag.getKey()) == FLAG_SELL) {
                dataObject.sellData = ((Number) flag.getValue().getOrDefault(PRICE_TAG, -1L)).longValue();
            }
            if (getFlagId(flag.getKey()) == FLAG_TELEPORT) {
                Map<String, Object> teleportData = (Map<String, Object>) flag.getValue().getOrDefault(POSITION_TAG, new HashMap<>());
                Map<String, Object> teleport = new HashMap<>();
                teleport.put(X_TAG, teleportData.getOrDefault(X_TAG, 0));
                teleport.put(Y_TAG, teleportData.getOrDefault(Y_TAG, 0));
                teleport.put(Z_TAG, teleportData.getOrDefault(Z_TAG, 0));
                teleport.put(LEVEL_TAG, teleportData.getOrDefault(LEVEL_TAG, ""));
                dataObject.teleportData = JSON.toJSONString(teleport);
            }
        }
        dataObject.state = Utils.serializeBooleanArray(state);
        return dataObject;
    }
}
