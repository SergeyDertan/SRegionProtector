package Sergey_Dertan.SRegionProtector.UI.Chest.Page;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import Sergey_Dertan.SRegionProtector.Utils.Tags;
import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public final class RemoveRegionPage implements Page {

    private final RegionManager regionManager = SRegionProtectorMain.getInstance().getRegionManager();

    RemoveRegionPage() {
    }

    @Override
    public Map<Integer, Item> getItems(Region region, int page) {
        Map<Integer, Item> items = new HashMap<>();

        Item item;
        CompoundTag nbt;

        item = Item.get(ItemID.EMERALD).setCustomName(Messenger.getInstance().getMessage("gui.remove.cancel"));
        nbt = item.getNamedTag();
        nbt.putString(Tags.OPEN_PAGE_TAG, MAIN.getName());
        item.setNamedTag(nbt);
        items.put(12, item);

        item = Item.get(BlockID.REDSTONE_BLOCK).setCustomName(Messenger.getInstance().getMessage("gui.remove.apply"));
        nbt = item.getNamedTag();
        nbt.putByte(Tags.REMOVE_REGION_TAG, 1);
        item.setNamedTag(nbt);
        items.put(14, item);

        this.prepareItems(items.values());

        return items;
    }

    @Override
    public boolean handle(Item item, Region region, Player player) {
        if (item.getNamedTagEntry(Tags.REMOVE_REGION_TAG) != null && this.hasPermission(player, region) && this.regionManager.regionExists(region.getName())) {
            this.regionManager.removeRegion(region);
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return "remove-region";
    }

    @Override
    public boolean hasPermission(Player player, Region region) {
        return player.hasPermission("sregionprotector.admin") || region.isCreator(player.getName());
    }
}
