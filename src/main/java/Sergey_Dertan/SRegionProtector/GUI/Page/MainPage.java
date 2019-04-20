package Sergey_Dertan.SRegionProtector.GUI.Page;

import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
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

    private final Map<Integer, Item> cache = new HashMap<>();

    MainPage() {
        Item item;
        CompoundTag nbt;

        item = Item.get(ItemID.SKULL, 3).setCustomName(Messenger.getInstance().getMessage("gui.main.go-to-owners"));
        nbt = item.getNamedTag();
        nbt.putString(Tags.OPEN_PAGE_TAG, OWNERS.getName());
        item.setNamedTag(nbt);
        this.cache.put(1, item);

        item = Item.get(ItemID.SKULL, 3).setCustomName(Messenger.getInstance().getMessage("gui.main.go-to-members"));
        nbt = item.getNamedTag();
        nbt.putString(Tags.OPEN_PAGE_TAG, MEMBERS.getName());
        item.setNamedTag(nbt);
        this.cache.put(2, item);

        item = Item.get(ItemID.BANNER).setCustomName(Messenger.getInstance().getMessage("gui.main.go-to-flags"));
        nbt = item.getNamedTag();
        nbt.putString(Tags.OPEN_PAGE_TAG, FLAGS.getName());
        item.setNamedTag(nbt);
        this.cache.put(3, item);

        item = Item.get(BlockID.TNT).setCustomName(Messenger.getInstance().getMessage("gui.main.go-to-remove"));
        nbt = item.getNamedTag();
        nbt.putString(Tags.OPEN_PAGE_TAG, REMOVE_REGION.getName());
        item.setNamedTag(nbt);
        this.cache.put(17, item);

        this.prepareItems(this.cache.values());
    }

    @Override
    public Map<Integer, Item> getItems(Region region, int page) {
        Map<Integer, Item> items = new HashMap<>(this.cache);

        Item item = Item.get(BlockID.EMERALD_BLOCK).setCustomName(Messenger.getInstance().getMessage("gui.main.region-description-item", "@region", region.name));
        item.setLore(Messenger.getInstance().getMessage("gui.main.region-description",
                new String[]{
                        "@level",
                        "@creator",
                        "@priority",
                        "@size"
                },
                new String[]{
                        region.level,
                        region.getCreator(),
                        Integer.toString(region.getPriority()),
                        Long.toString(Math.round((region.maxX - region.minX) * (region.maxY - region.minY) * (region.maxZ - region.minZ)))
                }
        ));
        items.put(0, item);
        this.prepareItem(item, 0);
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
