package Sergey_Dertan.SRegionProtector.UI.Chest.Page;

import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Region.Region;
import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;

import java.util.HashMap;
import java.util.Map;

public final class MainPage implements Page {

    private static final Map<Integer, Item> icons = new HashMap<>();

    MainPage() {
    }

    @Override
    public Map<Integer, Item> getItems(Region region, int page) {
        if (icons.size() != PAGES.size()) {
            icons.clear();
            int i = -1;
            for (Page pg : PAGES.values()) {
                if (pg instanceof MainPage) continue;
                icons.put(++i, pg.getIcon());
            }
            this.prepareItems(icons.values());
        }
        Map<Integer, Item> items = new HashMap<>(icons);

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
    public Item getIcon() {
        return null;
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
