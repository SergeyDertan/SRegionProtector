package Sergey_Dertan.SRegionProtector.GUI.Page;

import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Utils.Tags;
import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public final class MainPage implements Page {

    MainPage() {
    }

    @Override
    public Map<Integer, Item> getItems(Region region, int page) {
        Map<Integer, Item> items = new HashMap<>();

        Item item;
        CompoundTag nbt;

        item = Item.get(BlockID.EMERALD_BLOCK).setCustomName("Region '" + region.name + "'");
        item.setLore(
                "Level: " + region.level,
                "Creator: " + region.getCreator(),
                "Priority: " + region.getPriority(),
                "Size: " + Math.round((region.maxX - region.minX) * (region.maxY - region.minY) * (region.maxZ - region.minZ))
        );
        items.put(0, item);

        item = Item.get(ItemID.SKULL, 3).setCustomName("Owners");
        nbt = item.getNamedTag();
        nbt.putString(Tags.OPEN_PAGE_TAG, OWNERS.getName());
        item.setNamedTag(nbt);
        items.put(1, item);

        item = Item.get(ItemID.SKULL, 3).setCustomName("Members");
        nbt = item.getNamedTag();
        nbt.putString(Tags.OPEN_PAGE_TAG, MEMBERS.getName());
        item.setNamedTag(nbt);
        items.put(2, item);

        item = Item.get(ItemID.BANNER).setCustomName("Flags");
        nbt = item.getNamedTag();
        nbt.putString(Tags.OPEN_PAGE_TAG, FLAGS.getName());
        item.setNamedTag(nbt);
        items.put(3, item);

        this.prepareItems(items.values());
        return items;
    }

    @Override
    public String getName() {
        return "main";
    }

    @Override
    public boolean hasPermission(Player player, Region region) {
        return false;
    }
}
