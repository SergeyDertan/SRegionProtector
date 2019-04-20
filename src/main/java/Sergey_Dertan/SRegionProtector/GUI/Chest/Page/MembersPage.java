package Sergey_Dertan.SRegionProtector.GUI.Chest.Page;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import Sergey_Dertan.SRegionProtector.Utils.Tags;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class MembersPage implements Page {

    private final RegionManager regionManager = SRegionProtectorMain.getInstance().getRegionManager();

    MembersPage() {
    }

    @Override
    public Map<Integer, Item> getItems(Region region, int page) {
        Map<Integer, Item> list = new HashMap<>(NAVIGATORS_CACHE);
        int counter = 0;
        for (String member : region.getMembers().stream().skip(page * 18).limit(18).collect(Collectors.toList())) {
            Item item = Item.get(ItemID.SKULL, 3).
                    setCustomName(Messenger.getInstance().getMessage("gui.members.member-name", "@member", member)).
                    setLore(Messenger.getInstance().getMessage("gui.members.member-lore"));
            CompoundTag nbt = item.getNamedTag();
            nbt.putString(Tags.TARGET_NAME_TAG, member);
            item.setNamedTag(nbt);
            list.put(counter, item);
            ++counter;
        }
        this.prepareItems(list.values());
        return list;
    }

    @Override
    public boolean handle(Item item, Region region, Player player) {
        if (!this.hasPermission(player, region)) return false;
        String target = item.getNamedTag().getString(Tags.TARGET_NAME_TAG);
        if (target.isEmpty() || !region.isMember(target)) return false;
        this.regionManager.removeMember(region, target);
        return true;
    }

    @Override
    public String getName() {
        return "members";
    }

    @Override
    public boolean hasPermission(Player player, Region region) {
        return player.hasPermission("sregionprotector.admin") || region.isOwner(player.getName(), true);
    }
}
