package Sergey_Dertan.SRegionProtector.GUI.Page;

import Sergey_Dertan.SRegionProtector.Region.Region;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public final class OwnersPage implements Page {

    @Override
    public Map<Integer, Item> getItems(Region region) {
        Map<Integer, Item> items = this.getListItems(region, 0);
        this.addNavigators(items);
        return items;
    }

    private Map<Integer, Item> getListItems(Region region, int page) {
        Map<Integer, Item> list = new HashMap<>();
        AtomicInteger counter = new AtomicInteger(-1);
        region.getOwners().stream().skip(page * 18).limit(18).forEach(owner -> list.put(counter.incrementAndGet(), Item.get(ItemID.SKULL).setCustomName(owner).setLore("Click to remove")));
        this.prepareItems(list.values());
        return list;
    }

    @Override
    public String getName() {
        return "owners";
    }
}
