package Sergey_Dertan.SRegionProtector.UI.Chest.Page;

import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Utils.Tags;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public interface Page {

    Page OWNERS = new OwnersPage();
    Page MEMBERS = new MembersPage();
    Page FLAGS = new FlagsPage();
    Page REMOVE_REGION = new RemoveRegionPage();
    Page MAIN = new MainPage();

    Map<String, Page> PAGES = new LinkedHashMap<String, Page>() {{
        this.put(MAIN.getName(), MAIN);
        this.put(OWNERS.getName(), OWNERS);
        this.put(MEMBERS.getName(), MEMBERS);
        this.put(FLAGS.getName(), FLAGS);
        this.put(REMOVE_REGION.getName(), REMOVE_REGION);
    }};

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
        return PAGES.get(name);
    }

    @SuppressWarnings("unused")
    static void addPage(Page page) {
        PAGES.put(page.getName(), page);
    }

    default Map<Integer, Item> getItems(Region region) {
        return this.getItems(region, 0);
    }

    String getName();

    default void prepareItems(Collection<Item> items, int page) {
        items.forEach(item -> this.prepareItem(item, page));
    }

    /**
     * mark item as a UI item and add current page number and page name tags
     */
    default void prepareItem(Item item, int page) {
        CompoundTag nbt = item.hasCompoundTag() ? item.getNamedTag() : new CompoundTag();
        nbt.putByte(Tags.IS_UI_ITEM_TAG, 1);
        nbt.putInt(Tags.CURRENT_PAGE_NUMBER_TAG, page);
        if (this.getName() != null) nbt.putString(Tags.CURRENT_PAGE_NAME_TAG, this.getName());
        item.setNamedTag(nbt);
    }

    default void prepareItems(Collection<Item> items) {
        this.prepareItems(items, 0);
    }

    /**
     * @return true if page update required
     */
    default boolean handle(Item item, Region region, Player player) {
        return false;
    }

    /**
     * check if player has permission to do action (NOT to open page)
     */
    boolean hasPermission(Player player, Region region);

    /**
     * @param region current selected region
     * @return inventory items, see default pages for examples
     */
    Map<Integer, Item> getItems(Region region, int page);

    /**
     * @return icon which will be displayed on the main page
     */
    Item getIcon();
}
