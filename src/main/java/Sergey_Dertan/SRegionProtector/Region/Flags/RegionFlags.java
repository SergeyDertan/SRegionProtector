package Sergey_Dertan.SRegionProtector.Region.Flags;

import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionSellFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionTeleportFlag;
import Sergey_Dertan.SRegionProtector.Utils.Utils;
import cn.nukkit.Server;
import cn.nukkit.permission.Permissible;
import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.PluginManager;

import java.util.Arrays;

public abstract class RegionFlags {

    /**
     * https://github.com/SergeyDertan/SRegionProtector/wiki/Flags
     */
    public static final int FLAG_INVALID = -1;
    public static final int FLAG_BUILD = 0;
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

    public static final int FLAG_AMOUNT = 29;

    private static final RegionFlag[] defaults = new RegionFlag[FLAG_AMOUNT];
    private static final Permission[] permissions = new Permission[FLAG_AMOUNT];

    private RegionFlags() {
    }

    @SuppressWarnings("Duplicates") //TODO where is duplicates actually
    public static void init(boolean[] flagsDefault) {
        defaults[FLAG_BUILD] = new RegionFlag(flagsDefault[FLAG_BUILD]);
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

        PluginManager pluginManager = Server.getInstance().getPluginManager();

        permissions[FLAG_BUILD] = pluginManager.getPermission("sregionprotector.region.flag.build");
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
        permissions[FLAG_CHEST_ACCESS] = pluginManager.getPermission("sregionprotector.region.chest_access");
        permissions[FLAG_SLEEP] = pluginManager.getPermission("sregionprotector.region.sleep");
    }

    public static RegionFlag[] getDefaultFlagList() {
        return Utils.deepClone(Arrays.asList(defaults)).toArray(new RegionFlag[0]);
    }

    public static Permission getFlagPermission(int flag) {
        return permissions[flag];
    }

    public static String getFlagName(int flag) {
        switch (flag) {
            default:
                return "";
            case FLAG_BUILD:
                return "build";
            case FLAG_INTERACT:
                return "interact";
            case FLAG_USE:
                return "use";
            case FLAG_PVP:
                return "pvp";
            case FLAG_EXPLODE:
                return "tnt";
            case FLAG_LIGHTER:
                return "lighter";
            case FLAG_MAGIC_ITEM_USE:
                return "magic-item";
            case FLAG_HEAL:
                return "heal";
            case FLAG_INVINCIBLE:
                return "invincible";
            case FLAG_TELEPORT:
                return "teleport";
            case FLAG_SELL:
                return "sell";
            case FLAG_POTION_LAUNCH:
                return "potion-launch";
            case FLAG_MOVE:
                return "move";
            case FLAG_LEAVES_DECAY:
                return "leaves-decay";
            case FLAG_ITEM_DROP:
                return "item-drop";
            case FLAG_SEND_CHAT:
                return "send_chat";
            case FLAG_RECEIVE_CHAT:
                return "receive-chat";
            case FLAG_HEALTH_REGEN:
                return "health-regen";
            case FLAG_MOB_DAMAGE:
                return "mob-damage";
            case FLAG_MOB_SPAWN:
                return "mob-spawn";
            case FLAG_CROPS_DESTROY:
                return "crops-destroy";
            case FLAG_REDSTONE:
                return "redstone";
            case FLAG_ENDER_PEARL:
                return "ender-pearl";
            case FLAG_EXPLODE_BLOCK_BREAK:
                return "explode-block-break";
            case FLAG_LIQUID_FLOW:
                return "liquid-flow";
            case FLAG_FIRE:
                return "fire";
            case FLAG_LIGHTNING_STRIKE:
                return "lightning-strike";
            case FLAG_CHEST_ACCESS:
                return "chest-access";
            case FLAG_SLEEP:
                return "sleep";
        }
    }

    public static int getFlagId(String name) {
        switch (name.toLowerCase()) {
            default:
                return FLAG_INVALID;
            case "build":
                return FLAG_BUILD;
            case "interact":
                return FLAG_INTERACT;
            case "use":
                return FLAG_USE;
            case "pvp":
                return FLAG_PVP;
            case "tnt":
                return FLAG_EXPLODE;
            case "lighter":
                return FLAG_LIGHTER;
            case "magic_item":
            case "magic-item":
            case "magic_item_use":
            case "magic-item-use":
            case "magicitem":
            case "magic":
                return FLAG_MAGIC_ITEM_USE;
            case "heal":
                return FLAG_HEAL;
            case "invincible":
                return FLAG_INVINCIBLE;
            case "teleport":
            case "tp":
                return FLAG_TELEPORT;
            case "sell":
                return FLAG_SELL;
            case "potion_launch":
            case "potion-launch":
                return FLAG_POTION_LAUNCH;
            case "move":
                return FLAG_MOVE;
            case "leave_decay":
            case "leave-decay":
            case "leaves-decay":
            case "leaves_decay":
            case "leavesdecay":
            case "leavedecay":
                return FLAG_LEAVES_DECAY;
            case "item_drop":
            case "itemdrop":
            case "item-drop":
                return FLAG_ITEM_DROP;
            case "send-chat":
            case "send_chat":
            case "sendchat":
                return FLAG_SEND_CHAT;
            case "receive_chat":
            case "receive-chat":
            case "receivechat":
                return FLAG_RECEIVE_CHAT;
            case "health_regen":
            case "health-regen":
            case "healthregen":
                return FLAG_HEALTH_REGEN;
            case "mob-damage":
            case "mob_damage":
            case "mobdamage":
                return FLAG_MOB_DAMAGE;
            case "mob-spawn":
            case "mob_spawn":
            case "mobspawn":
                return FLAG_MOB_SPAWN;
            case "crops-destroy":
            case "crops_destroy":
            case "cropsdestroy":
                return FLAG_CROPS_DESTROY;
            case "redstone":
                return FLAG_REDSTONE;
            case "ender-pearl":
            case "ender_pearl":
            case "enderpearl":
                return FLAG_ENDER_PEARL;
            case "explode-block-break":
            case "explode_block_break":
            case "explodeblockbreak":
                return FLAG_EXPLODE_BLOCK_BREAK;
            case "liquid-flow":
            case "liquid_flow":
            case "liquidflow":
            case "lava-flow":
            case "lava_flow":
            case "lavaflow":
            case "water-flow":
            case "water_flow":
            case "waterflow":
                return FLAG_LIQUID_FLOW;
            case "fire":
                return FLAG_FIRE;
            case "lightning-strike":
            case "lightning_strike":
            case "lightningstrike":
            case "lightning":
                return FLAG_LIGHTNING_STRIKE;
            case "chest-access":
            case "chest_access":
            case "chestaccess":
                return FLAG_CHEST_ACCESS;
            case "sleep":
                return FLAG_SLEEP;
        }
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
