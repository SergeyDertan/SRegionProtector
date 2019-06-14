package Sergey_Dertan.SRegionProtector.Event;

import Sergey_Dertan.SRegionProtector.UI.Chest.ChestUIManager;
import Sergey_Dertan.SRegionProtector.UI.Chest.UIInventory;
import Sergey_Dertan.SRegionProtector.UI.Form.FormUIManager;
import Sergey_Dertan.SRegionProtector.UI.Form.Type.UIForm;
import Sergey_Dertan.SRegionProtector.UI.UIType;
import Sergey_Dertan.SRegionProtector.Utils.Tags;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.ModalFormRequestPacket;
import cn.nukkit.network.protocol.ModalFormResponsePacket;
import cn.nukkit.utils.MainLogger;

@SuppressWarnings("unused")
public final class GUIEventsHandler implements Listener {

    private final UIType guiType;

    public GUIEventsHandler(UIType guiType) {
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
        if (this.guiType != UIType.FORM) return;
        if (!(e.getWindow() instanceof UIForm) || e.wasClosed()) return;
        FormUIManager.handle(e.getPlayer(), (UIForm) e.getWindow());
    }

    //chest UI
    @EventHandler
    public void inventoryTransaction(InventoryTransactionEvent e) {
        if (this.guiType != UIType.CHEST) return;
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

    @EventHandler
    public void datapk(DataPacketSendEvent e) {
        DataPacket pk = e.getPacket();

        if (pk instanceof ModalFormRequestPacket) {
            MainLogger.getLogger().info("request:" + ((ModalFormRequestPacket) pk).data);
        } else if (pk instanceof ModalFormResponsePacket) {
            MainLogger.getLogger().info("response:" + ((ModalFormResponsePacket) pk).data);
        }
    }

    @EventHandler
    public void datapkw(DataPacketReceiveEvent e) {
        DataPacket pk = e.getPacket();

        if (pk instanceof ModalFormRequestPacket) {
            MainLogger.getLogger().info("request:" + ((ModalFormRequestPacket) pk).data);
        } else if (pk instanceof ModalFormResponsePacket) {
            MainLogger.getLogger().info("response:" + ((ModalFormResponsePacket) pk).data);
        }
    }
}
