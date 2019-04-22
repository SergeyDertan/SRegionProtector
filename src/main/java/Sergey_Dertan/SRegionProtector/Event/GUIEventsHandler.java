package Sergey_Dertan.SRegionProtector.Event;

import Sergey_Dertan.SRegionProtector.UI.Chest.ChestUIManager;
import Sergey_Dertan.SRegionProtector.UI.Chest.UIInventory;
import Sergey_Dertan.SRegionProtector.UI.Form.FormUIManager;
import Sergey_Dertan.SRegionProtector.UI.Form.Type.UIForm;
import Sergey_Dertan.SRegionProtector.Utils.Tags;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.inventory.transaction.action.InventoryAction;

@SuppressWarnings("unused")
public final class GUIEventsHandler implements Listener {

    public static final int UI_TYPE_CHEST = 0;
    public static final int UI_TYPE_FORM = 1;

    private final int guiType;

    public GUIEventsHandler(int guiType) {
        this.guiType = guiType;
    }

    //remove fake chest, chest UI
    @EventHandler
    public void inventoryClose(InventoryCloseEvent e) {
        ChestUIManager.removeChest(e.getPlayer());
    }

    //form UI
    @EventHandler
    public void playerFormResponded(PlayerFormRespondedEvent e) {
        if (this.guiType != UI_TYPE_FORM) return;
        if (!(e.getWindow() instanceof UIForm) || e.wasClosed()) return;
        FormUIManager.handle(e.getPlayer(), (UIForm) e.getWindow());
    }

    //chest UI
    @EventHandler
    public void inventoryTransaction(InventoryTransactionEvent e) {
        if (this.guiType != UI_TYPE_CHEST) return;
        if (e.getTransaction().getInventories().stream().noneMatch(inventory -> inventory instanceof UIInventory)) {
            return;
        }
        e.setCancelled();
        for (InventoryAction action : e.getTransaction().getActions()) {
            if (action.getSourceItem().getNamedTagEntry(Tags.IS_UI_ITEM_TAG) != null) {
                ChestUIManager.handle(e.getTransaction().getSource(), action.getSourceItem());
                return;
            }
            if (action.getTargetItem().getNamedTagEntry(Tags.IS_UI_ITEM_TAG) != null) {
                ChestUIManager.handle(e.getTransaction().getSource(), action.getTargetItem());
                return;
            }
        }
    }
}
