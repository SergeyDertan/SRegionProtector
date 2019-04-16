package Sergey_Dertan.SRegionProtector.GUI.Page;

import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Utils.Tags;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Collection;
import java.util.Map;

public interface Page {

    MainPage MAIN = new MainPage();
    OwnersPage OWNERS = new OwnersPage();
    MembersPage MEMBERS = new MembersPage();
    FlagsPage FLAGS = new FlagsPage();

    static Page getPage(String name) {
        switch (name.toLowerCase()) {
            case "main":
                return MAIN;
            case "owners":
                return OWNERS;
            case "flags":
                return FLAGS;
            case "members":
                return MEMBERS;
        }
        return null;
    }

    default Map<Integer, Item> getItems(Region region) {
        return this.getItems(region, 0);
    }

    String getName();

    default void prepareItems(Collection<Item> items, int page) {
        for (Item item : items) {
            CompoundTag nbt = item.hasCompoundTag() ? item.getNamedTag() : new CompoundTag();
            nbt.putByte(Tags.IS_GUI_ITEM_TAG, 1);
            nbt.putInt(Tags.CURRENT_PAGE_NUMBER_TAG, page);
            if (this.getName() != null) nbt.putString(Tags.CURRENT_PAGE_NAME_TAG, this.getName());
            item.setNamedTag(nbt);
        }
    }

    default void prepareItems(Collection<Item> items) {
        this.prepareItems(items, 0);
    }

    default void addNavigators(Map<Integer, Item> items) {
        CompoundTag nbt;

        nbt = new CompoundTag();
        nbt.putByte(Tags.PREVIOUS_PAGE_TAG, 1);
        items.put(21, Item.get(ItemID.APPLE).setNamedTag(nbt).setCustomName("Previous page")); //TODO item

        nbt = new CompoundTag();
        nbt.putByte(Tags.REFRESH_PAGE_TAG, 1);
        items.put(22, Item.get(ItemID.COOKIE).setNamedTag(nbt).setCustomName("REFRESH"));

        nbt = new CompoundTag();
        nbt.putByte(Tags.NEXT_PAGE_TAG, 1);
        items.put(23, Item.get(ItemID.APPLE).setNamedTag(nbt).setCustomName("Next page")); //TODO item

        nbt = new CompoundTag();
        nbt.putString(Tags.OPEN_PAGE_TAG, MAIN.getName());
        items.put(26, Item.get(ItemID.SLIMEBALL).setNamedTag(nbt).setCustomName("BACK"));
    }

    /**
     * @return true if update required
     */
    default boolean handle(Item item, Region region, Player player) {
        return false;
    }

    boolean hasPermission(Player player, Region region);

    Map<Integer, Item> getItems(Region region, int page);
}
