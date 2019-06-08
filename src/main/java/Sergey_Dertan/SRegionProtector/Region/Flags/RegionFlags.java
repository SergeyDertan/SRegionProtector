package Sergey_Dertan.SRegionProtector.Region.Flags;

import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionSellFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionTeleportFlag;
import Sergey_Dertan.SRegionProtector.Utils.Utils;
import cn.nukkit.Server;
import cn.nukkit.permission.Permissible;
import cn.nukkit.permission.Permission;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class RegionFlags {

    /**
     * https://github.com/SergeyDertan/SRegionProtector/wiki/Flags
     */
    public static final int FLAG_INVALID = -1;
    public static final int FLAG_PLACE = 0;
    public static final int FLAG_BREAK = 29;
    public static final int FLAG_INTERACT = 1;
    public static final int FLAG_USE = 2;
    public static final int FLAG_PVP = 3;
    public static final int FLAG_EXPLODE = 4;
    public static final int FLAG_LIGHTER = 5;
    public static final int FLAG_MAGIC_ITEM = 6;
    public static final int FLAG_HEAL = 7;
    public static final int FLAG_INVINCIBLE = 8;
    public static final int FLAG_TELEPORT = 9;
    public static final int FLAG_SELL = 10;
    public static final int FLAG_POTION_LAUNCH = 11;
    public static final int FLAG_MOVE = 12;
    public static final int FLAG_LEAVES_DECAY = 13;
    public static final int FLAG_ITEM_DROP = 14;
    public static final int FLAG_SEND_CHAT = 15;
    public static final int FLAG_RECEIVE_CHAT = 16;
    public static final int FLAG_HEALTH_REGEN = 17;
    public static final int FLAG_MOB_DAMAGE = 18;
    public static final int FLAG_MOB_SPAWN = 19;
    public static final int FLAG_CROPS_DESTROY = 20;
    public static final int FLAG_REDSTONE = 21;
    public static final int FLAG_ENDER_PEARL = 22;
    public static final int FLAG_EXPLODE_BLOCK_BREAK = 23;
    public static final int FLAG_LIGHTNING_STRIKE = 24;
    public static final int FLAG_FIRE = 25;
    public static final int FLAG_LIQUID_FLOW = 26; //lava & water spread
    public static final int FLAG_CHEST_ACCESS = 27;
    public static final int FLAG_SLEEP = 28;
    public static final int FLAG_CHUNK_LOADER = 30;
    public static final int FLAG_SMART_DOORS = 31;
    public static final int FLAG_MINEFARM = 32;
    public static final int FLAG_FALL_DAMAGE = 33;
    public static final int FLAG_NETHER_PORTAL = 34;
    public static final int FLAG_FRAME_ITEM_DROP = 35;
    public static final int FLAG_BUCKET_EMPTY = 36;
    public static final int FLAG_BUCKET_FILL = 37;

    public static final int FLAG_AMOUNT = 38;

    public static final RegionFlag[] defaults = new RegionFlag[FLAG_AMOUNT];
    public static final Permission[] permissions = new Permission[FLAG_AMOUNT];

    public static final BiMap<Integer, String> flags; //flags names
    public static final Map<String, Integer> aliases; //flags names aliases
    public static final boolean[] state = new boolean[FLAG_AMOUNT]; //true if "allow" means that flag should be disabled

    static {
        BiMap<Integer, String> flagList = HashBiMap.create(FLAG_AMOUNT);
        for (Field field : RegionFlags.class.getDeclaredFields()) {
            if (field.getType() != int.class || field.getName().equals("FLAG_AMOUNT") || field.getName().equals("FLAG_INVALID")) {
                continue;
            }
            try {
                flagList.put(
                        field.getInt(null),
                        field.getName().toLowerCase().replace("flag_", "").replace("_", "-")
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        flags = ImmutableBiMap.copyOf(flagList);

        Map<String, Integer> aAliases = new HashMap<>(FLAG_AMOUNT);
        flagList.forEach((k, v) -> {
            aAliases.put(v.replace("-", "_"), k);
            aAliases.put(v.replace("-", ""), k);
            aAliases.remove(v);
        });
        aliases = ImmutableMap.copyOf(aAliases);

        flags.forEach((k, v) -> permissions[k] = Server.getInstance().getPluginManager().getPermission("sregionprotector.region.flag." + v.replace("-", "_")));

        Arrays.fill(state, true);
        state[FLAG_HEAL] = false;
        state[FLAG_INVINCIBLE] = false;
        state[FLAG_TELEPORT] = false;
        state[FLAG_SELL] = false;
        state[FLAG_CHUNK_LOADER] = false;
        state[FLAG_SMART_DOORS] = false;
        state[FLAG_MINEFARM] = false;
        state[FLAG_FALL_DAMAGE] = false;
        state[FLAG_EXPLODE_BLOCK_BREAK] = false;
    }

    private RegionFlags() {
    }

    public static void init(boolean[] flagsDefault) {
        for (int i = 0; i < FLAG_AMOUNT; ++i) {
            defaults[i] = new RegionFlag(flagsDefault[i]);
        }
        defaults[FLAG_TELEPORT] = new RegionTeleportFlag(flagsDefault[FLAG_TELEPORT]);
        defaults[FLAG_SELL] = new RegionSellFlag(flagsDefault[FLAG_SELL]);
    }

    public static RegionFlag[] getDefaultFlagList() {
        return Utils.deepClone(Arrays.asList(defaults)).toArray(new RegionFlag[0]);
    }

    public static Permission getFlagPermission(int flag) {
        return permissions[flag];
    }

    public static String getFlagName(int flag) {
        return flags.get(flag);
    }

    public static int getFlagId(String name) {
        name = name.toLowerCase();
        int id = flags.inverse().getOrDefault(name, FLAG_INVALID);
        if (id == FLAG_INVALID) id = aliases.getOrDefault(name, FLAG_INVALID);
        return id;
    }

    public static boolean getStateFromString(String state, int flag) {
        if (state.equalsIgnoreCase("allow")) return !RegionFlags.state[flag];
        if (state.equalsIgnoreCase("deny")) return RegionFlags.state[flag];
        throw new RuntimeException("Wrong state");
    }

    public static void fixMissingFlags(List<RegionFlag> flags) {
        for (int i = flags.size(); i < FLAG_AMOUNT; ++i) {
            flags.add(defaults[i].clone());
        }
    }

    public static boolean hasFlagPermission(Permissible target, int flag) {
        return target.hasPermission(permissions[flag]);
    }

    public static boolean hasFlagPermission(Permissible target, String flag) {
        return hasFlagPermission(target, getFlagId(flag));
    }

    public static boolean getDefaultFlagState(int flag) {
        return defaults[flag].state;
    }

    public static boolean getDefaultFlagState(String flag) {
        return getDefaultFlagState(getFlagId(flag));
    }
}
