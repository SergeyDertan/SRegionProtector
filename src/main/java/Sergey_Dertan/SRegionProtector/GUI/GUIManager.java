package Sergey_Dertan.SRegionProtector.GUI;

import Sergey_Dertan.SRegionProtector.GUI.Page.Page;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Utils.Tags;
import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteOrder;

public abstract class GUIManager {

    private static final Int2ObjectMap<GUIInventory> inventories = new Int2ObjectArrayMap<>(); //loader id -> gui inventory
    /**
     * async packets should be put directly to the interface
     *
     * @see cn.nukkit.event.server.DataPacketSendEvent
     */
    private static boolean async;

    private GUIManager() {
    }

    public static void handle(Player player, Item item) {
        Inventory inventory = inventories.get(player.getLoaderId());
        if (inventory == null) return;
        Region region = inventories.get(player.getLoaderId()).region;
        if (!Page.MAIN.hasPermission(player, region)) {
            removeChest(player, (Vector3) inventories.remove(player.getLoaderId()).getHolder());
            return;
        }
        CompoundTag nbt = item.getNamedTag();
        Page page;
        //navigators
        page = Page.getPage(nbt.getString(Tags.CURRENT_PAGE_NAME_TAG));
        if (page == null) return;
        if (nbt.contains(Tags.REFRESH_PAGE_TAG)) {
            inventory.setContents(page.getItems(region, nbt.getInt(Tags.CURRENT_PAGE_NUMBER_TAG)));
            return;
        }
        if (nbt.contains(Tags.NEXT_PAGE_TAG)) {
            int pageNumber = nbt.getInt(Tags.CURRENT_PAGE_NUMBER_TAG) + 1;
            inventory.setContents(page.getItems(region, pageNumber));
            return;
        }
        if (nbt.contains(Tags.PREVIOUS_PAGE_TAG)) {
            int pageNumber = nbt.getInt(Tags.CURRENT_PAGE_NUMBER_TAG) - 1;
            pageNumber = pageNumber < 0 ? 0 : pageNumber;
            inventory.setContents(page.getItems(region, pageNumber));
        }
        //page link
        page = Page.getPage(nbt.getString(Tags.OPEN_PAGE_TAG));
        if (page != null) {
            inventory.setContents(page.getItems(region));
            return;
        }
        //page handler
        page = Page.getPage(nbt.getString(Tags.CURRENT_PAGE_NAME_TAG));
        if (page != null) {
            if (page.handle(item, region, player)) {
                inventory.setContents(page.getItems(region, nbt.getInt(Tags.CURRENT_PAGE_NUMBER_TAG)));
            }
        }

    }

    public static void setAsync(boolean async) {
        GUIManager.async = async;
    }

    public static void open(Player player, Region region) {
        Vector3 pos = sendFakeChest(player, region.name);
        if (pos == null) return;
        GUIInventory inventory = new GUIInventory(pos, Page.MAIN.getItems(region), region);
        if (player.addWindow(inventory) == -1) {
            removeChest(player, pos);
        } else {
            inventories.put(player.getLoaderId(), inventory);
        }
    }

    public static void removeChest(Player target) {
        Inventory inventory = inventories.remove(target.getLoaderId());
        if (inventory != null) {
            removeChest(target, ((Vector3) inventory.getHolder()));
        }
    }

    private static Vector3 sendFakeChest(Player target, String region) {
        UpdateBlockPacket pk1 = new UpdateBlockPacket();
        pk1.x = (int) target.x;
        pk1.y = (int) target.y - 1;
        pk1.z = (int) target.z;
        pk1.dataLayer = 0;
        pk1.flags = UpdateBlockPacket.FLAG_NONE;
        pk1.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(BlockID.CHEST, 0);
        if (async && sendAsyncPacket(target, pk1)) {
            return null;
        } else {
            target.dataPacket(pk1);
        }

        BlockEntityDataPacket pk2 = new BlockEntityDataPacket();
        pk2.x = (int) target.x;
        pk2.y = (int) target.y - 1;
        pk2.z = (int) target.z;
        CompoundTag nbt = new CompoundTag();
        nbt.putString(Tags.CUSTOM_NAME_TAG, region);

        try {
            pk2.namedTag = NBTIO.write(nbt, ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException ignore) {
        }
        if (async && sendAsyncPacket(target, pk2)) {
            return null;
        } else {
            target.dataPacket(pk2);
        }
        return new Vector3(target.x, target.y - 1, target.z);
    }

    private static void removeChest(Player target, Vector3 pos) {
        target.level.sendBlocks(new Player[]{target}, new Vector3[]{pos});
    }

    /**
     * @return true if error occurred
     */
    private static boolean sendAsyncPacket(Player target, DataPacket pk) {
        try {
            Field field = target.getClass().getDeclaredField("interfaz");
            boolean accessible = field.isAccessible();
            if (!accessible) {
                field.setAccessible(true);
            }
            SourceInterface interfaz = (SourceInterface) field.get(target);
            interfaz.putPacket(target, pk);
            field.setAccessible(accessible);
            return false;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return true;
        }
    }
}
