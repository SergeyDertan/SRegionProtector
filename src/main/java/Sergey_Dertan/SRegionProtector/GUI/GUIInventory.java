package Sergey_Dertan.SRegionProtector.GUI;

import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;

import java.util.Map;

public final class GUIInventory extends ContainerInventory {

    public GUIInventory(Holder holder, Map<Integer, Item> content) {
        super(holder, InventoryType.CHEST, content);
    }

    public static final class Holder extends Vector3 implements InventoryHolder {

        public Holder(double x, double y, double z) {
            super(x, y, z);
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
