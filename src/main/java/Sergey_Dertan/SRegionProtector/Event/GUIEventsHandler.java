package Sergey_Dertan.SRegionProtector.Event;

import Sergey_Dertan.SRegionProtector.GUI.GUIInventory;
import Sergey_Dertan.SRegionProtector.GUI.GUIManager;
import Sergey_Dertan.SRegionProtector.Utils.Tags;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.inventory.transaction.action.InventoryAction;

public final class GUIEventsHandler implements Listener {

    //remove fake chest
    @EventHandler
    public void inventoryClose(InventoryCloseEvent e) {
        GUIManager.removeChest(e.getPlayer());
    }

    @EventHandler
    public void inventoryTransaction(InventoryTransactionEvent e) {
        if (e.getTransaction().getInventories().stream().noneMatch(inventory -> inventory instanceof GUIInventory)) {
            return;
        }
        e.setCancelled();
        for (InventoryAction action : e.getTransaction().getActions()) {
            if (action.getSourceItem().getNamedTagEntry(Tags.IS_GUI_ITEM_TAG) != null) {
                GUIManager.handle(e.getTransaction().getSource(), action.getSourceItem());
                return;
            }
            if (action.getTargetItem().getNamedTagEntry(Tags.IS_GUI_ITEM_TAG) != null) {
                GUIManager.handle(e.getTransaction().getSource(), action.getTargetItem());
                return;
            }
        }
    }
}
