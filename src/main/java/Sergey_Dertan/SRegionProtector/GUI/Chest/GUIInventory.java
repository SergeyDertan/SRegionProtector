package Sergey_Dertan.SRegionProtector.GUI.Chest;

import Sergey_Dertan.SRegionProtector.Region.Region;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;

import java.util.Map;

public final class GUIInventory extends ContainerInventory {

    public final Region region;

    GUIInventory(Vector3 holder, Map<Integer, Item> content, Region region) {
        super(new Holder(holder.x, holder.y, holder.z), InventoryType.CHEST, content);
        this.region = region;
    }

    static final class Holder extends Vector3 implements InventoryHolder {

        private Holder(double x, double y, double z) {
            super(x, y, z);
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
