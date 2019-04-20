package Sergey_Dertan.SRegionProtector.GUI.Chest.Page;

import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Utils.Tags;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface Page {

    OwnersPage OWNERS = new OwnersPage();
    MembersPage MEMBERS = new MembersPage();
    FlagsPage FLAGS = new FlagsPage();
    RemoveRegionPage REMOVE_REGION = new RemoveRegionPage();
    MainPage MAIN = new MainPage();

    Map<Integer, Item> NAVIGATORS_CACHE = new HashMap<Integer, Item>() {{
        CompoundTag nbt;

        nbt = new CompoundTag();
        nbt.putByte(Tags.PREVIOUS_PAGE_TAG, 1);
        this.put(21, Item.get(ItemID.APPLE).setNamedTag(nbt).setCustomName(Messenger.getInstance().getMessage("gui.navigator.previous-page")));

        nbt = new CompoundTag();
        nbt.putByte(Tags.REFRESH_PAGE_TAG, 1);
        this.put(22, Item.get(ItemID.COOKIE).setNamedTag(nbt).setCustomName(Messenger.getInstance().getMessage("gui.navigator.refresh")));

        nbt = new CompoundTag();
        nbt.putByte(Tags.NEXT_PAGE_TAG, 1);
        this.put(23, Item.get(ItemID.APPLE).setNamedTag(nbt).setCustomName(Messenger.getInstance().getMessage("gui.navigator.next-page")));

        nbt = new CompoundTag();
        nbt.putString(Tags.OPEN_PAGE_TAG, MAIN.getName());
        this.put(26, Item.get(ItemID.SLIMEBALL).setNamedTag(nbt).setCustomName(Messenger.getInstance().getMessage("gui.navigator.back")));
    }};

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
            case "remove-region":
                return REMOVE_REGION;
        }
        return null;
    }

    default Map<Integer, Item> getItems(Region region) {
        return this.getItems(region, 0);
    }

    String getName();

    default void prepareItems(Collection<Item> items, int page) {
        items.forEach(item -> this.prepareItem(item, page));
    }

    default void prepareItem(Item item, int page) {
        CompoundTag nbt = item.hasCompoundTag() ? item.getNamedTag() : new CompoundTag();
        nbt.putByte(Tags.IS_GUI_ITEM_TAG, 1);
        nbt.putInt(Tags.CURRENT_PAGE_NUMBER_TAG, page);
        if (this.getName() != null) nbt.putString(Tags.CURRENT_PAGE_NAME_TAG, this.getName());
        item.setNamedTag(nbt);
    }

    default void prepareItems(Collection<Item> items) {
        this.prepareItems(items, 0);
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
