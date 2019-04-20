package Sergey_Dertan.SRegionProtector.GUI.Chest.Page;

import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionSellFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionTeleportFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Utils.Tags;
import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.Tag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class FlagsPage implements Page {

    private final int[] flagToBlock;

    FlagsPage() {
        this.flagToBlock = new int[RegionFlags.FLAG_AMOUNT];
        Arrays.fill(this.flagToBlock, ItemID.BANNER);

        this.flagToBlock[RegionFlags.FLAG_PLACE] = BlockID.GRASS;
        this.flagToBlock[RegionFlags.FLAG_BREAK] = BlockID.GRASS;
        this.flagToBlock[RegionFlags.FLAG_USE] = BlockID.LEVER;
        this.flagToBlock[RegionFlags.FLAG_PVP] = ItemID.DIAMOND_SWORD;
        this.flagToBlock[RegionFlags.FLAG_EXPLODE] = BlockID.TNT;
        this.flagToBlock[RegionFlags.FLAG_EXPLODE_BLOCK_BREAK] = BlockID.TNT;
        this.flagToBlock[RegionFlags.FLAG_LIGHTER] = ItemID.FLINT_AND_STEEL;
        this.flagToBlock[RegionFlags.FLAG_LEAVES_DECAY] = BlockID.LEAVE;
        this.flagToBlock[RegionFlags.FLAG_ITEM_DROP] = ItemID.STICK;
        this.flagToBlock[RegionFlags.FLAG_MOB_SPAWN] = ItemID.SPAWN_EGG;
        this.flagToBlock[RegionFlags.FLAG_CROPS_DESTROY] = BlockID.FARMLAND;
        this.flagToBlock[RegionFlags.FLAG_REDSTONE] = ItemID.REDSTONE_DUST;
        this.flagToBlock[RegionFlags.FLAG_ENDER_PEARL] = ItemID.ENDER_PEARL;
        this.flagToBlock[RegionFlags.FLAG_FIRE] = BlockID.FIRE;
        this.flagToBlock[RegionFlags.FLAG_LIQUID_FLOW] = BlockID.STILL_WATER;
        this.flagToBlock[RegionFlags.FLAG_CHEST_ACCESS] = BlockID.CHEST;
        this.flagToBlock[RegionFlags.FLAG_SLEEP] = ItemID.BED;
        this.flagToBlock[RegionFlags.FLAG_SMART_DOORS] = ItemID.IRON_DOOR;
        this.flagToBlock[RegionFlags.FLAG_MINEFARM] = BlockID.DIAMOND_ORE;
        this.flagToBlock[RegionFlags.FLAG_POTION_LAUNCH] = ItemID.SPLASH_POTION;
        this.flagToBlock[RegionFlags.FLAG_HEAL] = ItemID.GOLDEN_APPLE;
        this.flagToBlock[RegionFlags.FLAG_NETHER_PORTAL] = BlockID.NETHER_PORTAL;
        this.flagToBlock[RegionFlags.FLAG_SELL] = ItemID.EMERALD;
    }

    @Override
    public Map<Integer, Item> getItems(Region region, int page) {
        Map<Integer, Item> list = new HashMap<>(NAVIGATORS_CACHE);
        int counter = 0;
        for (RegionFlag flag : Arrays.stream(region.getFlags()).skip(page * 18).limit(18).collect(Collectors.toList())) {
            int flagId = counter + page * 18;
            Item item = Item.get(this.flagToBlock[flagId]);

            String name = RegionFlags.getFlagName(flagId);
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            item.setCustomName(name);

            String[] lore = new String[2];
            lore[0] = "Value: " + (region.getFlagState(flagId) == RegionFlags.getStateFromString("allow", flagId) ? "allow" : "deny");
            if (flag instanceof RegionSellFlag) {
                lore[1] = "Price: " + ((RegionSellFlag) flag).price;
            } else if (flag instanceof RegionTeleportFlag) {
                Vector3 pos = ((RegionTeleportFlag) flag).position;
                if (pos != null) {
                    lore[1] = "x: " + Math.round(pos.x) + ", y: " + Math.round(pos.y) + ", z: " + Math.round(pos.z);
                }
            }
            item.setLore(lore[1] == null ? new String[]{lore[0]} : lore);
            CompoundTag nbt = item.getNamedTag();
            nbt.putInt(Tags.FLAG_ID_TAG, flagId);
            list.put(counter, item);
            ++counter;
        }
        this.prepareItems(list.values(), page);
        return list;
    }

    @Override
    public boolean handle(Item item, Region region, Player player) {
        if (!this.hasPermission(player, region)) return false;
        Tag tag = item.getNamedTagEntry(Tags.FLAG_ID_TAG);
        if (!(tag instanceof IntTag)) return false;
        int flagId = ((IntTag) tag).data;
        if (flagId == RegionFlags.FLAG_SELL || flagId == RegionFlags.FLAG_TELEPORT) return false;
        if (!RegionFlags.hasFlagPermission(player, flagId)) return false;
        region.setFlagState(flagId, !region.getFlagState(flagId));
        return true;
    }

    @Override
    public String getName() {
        return "flags";
    }

    @Override
    public boolean hasPermission(Player player, Region region) {
        return player.hasPermission("sregionprotector.admin") || region.isOwner(player.getName(), true);
    }
}
