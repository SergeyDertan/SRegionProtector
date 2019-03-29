package Sergey_Dertan.SRegionProtector.Region.Flags;

import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionSellFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionTeleportFlag;
import Sergey_Dertan.SRegionProtector.Utils.Utils;
import cn.nukkit.Server;
import cn.nukkit.permission.Permissible;
import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.PluginManager;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
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
    public static final int FLAG_MAGIC_ITEM_USE = 6;
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

    public static final int FLAG_AMOUNT = 33;

    public static final RegionFlag[] defaults = new RegionFlag[FLAG_AMOUNT];
    public static final Permission[] permissions = new Permission[FLAG_AMOUNT];

    public static final BiMap<Integer, String> flags;
    public static final Map<String, Integer> aliases;

    static {
        BiMap<Integer, String> flagList = HashBiMap.create(FLAG_AMOUNT);
        flagList.put(FLAG_PLACE, "place");
        flagList.put(FLAG_BREAK, "break");
        flagList.put(FLAG_INTERACT, "interact");
        flagList.put(FLAG_USE, "use");
        flagList.put(FLAG_PVP, "pvp");
        flagList.put(FLAG_EXPLODE, "tnt");
        flagList.put(FLAG_LIGHTER, "lighter");
        flagList.put(FLAG_MAGIC_ITEM_USE, "magic-item");
        flagList.put(FLAG_HEAL, "heal");
        flagList.put(FLAG_INVINCIBLE, "invincible");
        flagList.put(FLAG_TELEPORT, "teleport");
        flagList.put(FLAG_SELL, "sell");
        flagList.put(FLAG_POTION_LAUNCH, "potion-launch");
        flagList.put(FLAG_MOVE, "move");
        flagList.put(FLAG_LEAVES_DECAY, "leaves-decay");
        flagList.put(FLAG_ITEM_DROP, "item-drop");
        flagList.put(FLAG_SEND_CHAT, "send-chat");
        flagList.put(FLAG_RECEIVE_CHAT, "receive-chat");
        flagList.put(FLAG_HEALTH_REGEN, "health-regen");
        flagList.put(FLAG_MOB_DAMAGE, "mob-damage");
        flagList.put(FLAG_MOB_SPAWN, "mob-spawn");
        flagList.put(FLAG_CROPS_DESTROY, "crops-destroy");
        flagList.put(FLAG_REDSTONE, "redstone");
        flagList.put(FLAG_ENDER_PEARL, "ender-pearl");
        flagList.put(FLAG_EXPLODE_BLOCK_BREAK, "explode-block-break");
        flagList.put(FLAG_LIQUID_FLOW, "liquid-flow");
        flagList.put(FLAG_FIRE, "fire");
        flagList.put(FLAG_LIGHTNING_STRIKE, "lightning-strike");
        flagList.put(FLAG_CHEST_ACCESS, "chest-access");
        flagList.put(FLAG_SLEEP, "sleep");
        flagList.put(FLAG_CHUNK_LOADER, "chunk-loader");
        flagList.put(FLAG_SMART_DOORS, "smart-doors");
        flagList.put(FLAG_MINEFARM, "minefarm");
        flags = ImmutableBiMap.copyOf(flagList);

        Map<String, Integer> aAliases = new HashMap<>();
        flagList.forEach((k, v) -> {
            aAliases.put(v.replace("-", "_"), k);
            aAliases.put(v.replace("-", ""), k);
            aAliases.remove(v);
        });
        aliases = ImmutableMap.copyOf(aAliases);
    }

    private RegionFlags() {
    }

    @SuppressWarnings("Duplicates") //TODO where is duplicates actually
    public static void init(boolean[] flagsDefault) {
        defaults[FLAG_PLACE] = new RegionFlag(flagsDefault[FLAG_PLACE]);
        defaults[FLAG_BREAK] = new RegionFlag(flagsDefault[FLAG_BREAK]);
        defaults[FLAG_INTERACT] = new RegionFlag(flagsDefault[FLAG_INTERACT]);
        defaults[FLAG_USE] = new RegionFlag(flagsDefault[FLAG_USE]);
        defaults[FLAG_PVP] = new RegionFlag(flagsDefault[FLAG_PVP]);
        defaults[FLAG_EXPLODE] = new RegionFlag(flagsDefault[FLAG_EXPLODE]);
        defaults[FLAG_LIGHTER] = new RegionFlag(flagsDefault[FLAG_LIGHTER]);
        defaults[FLAG_MAGIC_ITEM_USE] = new RegionFlag(flagsDefault[FLAG_MAGIC_ITEM_USE]);
        defaults[FLAG_HEAL] = new RegionFlag(flagsDefault[FLAG_HEAL]);
        defaults[FLAG_INVINCIBLE] = new RegionFlag(flagsDefault[FLAG_INVINCIBLE]);
        defaults[FLAG_TELEPORT] = new RegionTeleportFlag(flagsDefault[FLAG_TELEPORT]);
        defaults[FLAG_SELL] = new RegionSellFlag(flagsDefault[FLAG_SELL]);
        defaults[FLAG_POTION_LAUNCH] = new RegionFlag(flagsDefault[FLAG_POTION_LAUNCH]);
        defaults[FLAG_MOVE] = new RegionFlag(flagsDefault[FLAG_MOVE]);
        defaults[FLAG_LEAVES_DECAY] = new RegionFlag(flagsDefault[FLAG_LEAVES_DECAY]);
        defaults[FLAG_ITEM_DROP] = new RegionFlag(flagsDefault[FLAG_ITEM_DROP]);
        defaults[FLAG_SEND_CHAT] = new RegionFlag(flagsDefault[FLAG_SEND_CHAT]);
        defaults[FLAG_RECEIVE_CHAT] = new RegionFlag(flagsDefault[FLAG_RECEIVE_CHAT]);
        defaults[FLAG_HEALTH_REGEN] = new RegionFlag(flagsDefault[FLAG_HEALTH_REGEN]);
        defaults[FLAG_MOB_DAMAGE] = new RegionFlag(flagsDefault[FLAG_MOB_DAMAGE]);
        defaults[FLAG_MOB_SPAWN] = new RegionFlag(flagsDefault[FLAG_MOB_SPAWN]);
        defaults[FLAG_CROPS_DESTROY] = new RegionFlag(flagsDefault[FLAG_CROPS_DESTROY]);
        defaults[FLAG_REDSTONE] = new RegionFlag(flagsDefault[FLAG_REDSTONE]);
        defaults[FLAG_ENDER_PEARL] = new RegionFlag(flagsDefault[FLAG_ENDER_PEARL]);
        defaults[FLAG_EXPLODE_BLOCK_BREAK] = new RegionFlag(flagsDefault[FLAG_EXPLODE_BLOCK_BREAK]);
        defaults[FLAG_LIQUID_FLOW] = new RegionFlag(flagsDefault[FLAG_LIQUID_FLOW]);
        defaults[FLAG_LIGHTNING_STRIKE] = new RegionFlag(flagsDefault[FLAG_LIGHTNING_STRIKE]);
        defaults[FLAG_FIRE] = new RegionFlag(flagsDefault[FLAG_FIRE]);
        defaults[FLAG_CHEST_ACCESS] = new RegionFlag(flagsDefault[FLAG_CHEST_ACCESS]);
        defaults[FLAG_SLEEP] = new RegionFlag(flagsDefault[FLAG_SLEEP]);
        defaults[FLAG_CHUNK_LOADER] = new RegionFlag(flagsDefault[FLAG_CHUNK_LOADER]);
        defaults[FLAG_SMART_DOORS] = new RegionFlag(flagsDefault[FLAG_SMART_DOORS]);
        defaults[FLAG_MINEFARM] = new RegionFlag(flagsDefault[FLAG_MINEFARM]);

        PluginManager pluginManager = Server.getInstance().getPluginManager();

        permissions[FLAG_PLACE] = pluginManager.getPermission("sregionprotector.region.flag.place");
        permissions[FLAG_BREAK] = pluginManager.getPermission("sregionprotector.region.flag.break");
        permissions[FLAG_INTERACT] = pluginManager.getPermission("sregionprotector.region.flag.interact");
        permissions[FLAG_USE] = pluginManager.getPermission("sregionprotector.region.flag.use");
        permissions[FLAG_PVP] = pluginManager.getPermission("sregionprotector.region.flag.pvp");
        permissions[FLAG_EXPLODE] = pluginManager.getPermission("sregionprotector.region.flag.explode");
        permissions[FLAG_LIGHTER] = pluginManager.getPermission("sregionprotector.region.flag.lighter");
        permissions[FLAG_MAGIC_ITEM_USE] = pluginManager.getPermission("sregionprotector.region.flag.magic_item_use");
        permissions[FLAG_HEAL] = pluginManager.getPermission("sregionprotector.region.flag.heal");
        permissions[FLAG_INVINCIBLE] = pluginManager.getPermission("sregionprotector.region.flag.invincible");
        permissions[FLAG_TELEPORT] = pluginManager.getPermission("sregionprotector.region.flag.teleport");
        permissions[FLAG_SELL] = pluginManager.getPermission("sregionprotector.region.flag.sell");
        permissions[FLAG_POTION_LAUNCH] = pluginManager.getPermission("sregionprotector.region.flag.potion_launch");
        permissions[FLAG_MOVE] = pluginManager.getPermission("sregionprotector.region.flag.move");
        permissions[FLAG_LEAVES_DECAY] = pluginManager.getPermission("sregionprotector.region.flag.leaves_decay");
        permissions[FLAG_ITEM_DROP] = pluginManager.getPermission("sregionprotector.region.flag.item_drop");
        permissions[FLAG_SEND_CHAT] = pluginManager.getPermission("sregionprotector.region.flag.send_chat");
        permissions[FLAG_RECEIVE_CHAT] = pluginManager.getPermission("sregionprotector.region.flag.receive_chat");
        permissions[FLAG_HEALTH_REGEN] = pluginManager.getPermission("sregionprotector.region.flag.health_regen");
        permissions[FLAG_MOB_DAMAGE] = pluginManager.getPermission("sregionprotector.region.flag.mob_damage");
        permissions[FLAG_MOB_SPAWN] = pluginManager.getPermission("sregionprotector.region.flag.mob_spawn");
        permissions[FLAG_CROPS_DESTROY] = pluginManager.getPermission("sregionprotector.region.flag.crops_destroy");
        permissions[FLAG_REDSTONE] = pluginManager.getPermission("sregionprotector.region.flag.redstone");
        permissions[FLAG_ENDER_PEARL] = pluginManager.getPermission("sregionprotector.region.flag.ender_pearl");
        permissions[FLAG_EXPLODE_BLOCK_BREAK] = pluginManager.getPermission("sregionprotector.region.flag.explode_block_break");
        permissions[FLAG_LIQUID_FLOW] = pluginManager.getPermission("sregionprotector.region.flag.liquid_flow");
        permissions[FLAG_LIGHTNING_STRIKE] = pluginManager.getPermission("sregionprotector.region.flag.lightning_strike");
        permissions[FLAG_FIRE] = pluginManager.getPermission("sregionprotector.region.flag.fire");
        permissions[FLAG_CHEST_ACCESS] = pluginManager.getPermission("sregionprotector.region.flag.chest_access");
        permissions[FLAG_SLEEP] = pluginManager.getPermission("sregionprotector.region.flag.sleep");
        permissions[FLAG_CHUNK_LOADER] = pluginManager.getPermission("sregionprotector.region.flag.chunk_loader");
        permissions[FLAG_SMART_DOORS] = pluginManager.getPermission("sregionprotector.region.flag.smart_doors");
        permissions[FLAG_MINEFARM] = pluginManager.getPermission("sregionprotector.region.flag.minefarm");
    }

    public static RegionFlag[] getDefaultFlagList() {
        return Utils.deepClone(Arrays.asList(defaults)).toArray(new RegionFlag[FLAG_AMOUNT]);
    }

    public static Permission getFlagPermission(int flag) {
        return permissions[flag];
    }

    public static String getFlagName(int flag) {
        return flags.get(flag);
    }

    public static int getFlagId(String name) {
        int id = flags.inverse().getOrDefault(name, FLAG_INVALID);
        if (id == FLAG_INVALID) id = aliases.getOrDefault(name, FLAG_INVALID);
        return id;
    }

    public static boolean getStateFromString(String state) {
        switch (state.toLowerCase()) {
            case "yes":
            case "enable":
            case "enabled":
            case "вкл":
            case "true":
                return true;
            case "no":
            case "disable":
            case "disabled":
            case "выкл":
            case "false":
            default:
                return false;
        }
    }

    public static void fixMissingFlags(RegionFlag[] flags) {
        for (int i = 0; i < FLAG_AMOUNT; ++i) {
            if (flags[i] != null) continue;
            flags[i] = defaults[i].clone();
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
