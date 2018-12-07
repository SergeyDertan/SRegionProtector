package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionGroup;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RegionListCommand extends SRegionProtectorCommand {

    public static final String OWNER = "owner";
    public static final String MEMBER = "member";
    public static final String CREATOR = "creator";
    private RegionManager regionManager;

    public RegionListCommand(String name, Map<String, String> messages, RegionManager regionManager) {
        super(name, messages);
        this.regionManager = regionManager;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            Messenger.getInstance().sendMessage(sender, "command.list.permission");
            return false;
        }
        if (!(sender instanceof Player)) {
            Messenger.getInstance().sendMessage(sender, "command.list.in-game");
            return false;
        }
        if (args.length < 1) {
            sender.sendMessage(this.usageMessage);
            return false;
        }
        String type = args[0].toLowerCase();
        if (!type.equals(OWNER) && !type.equals(MEMBER) && !type.equals(CREATOR)) {
            sender.sendMessage(this.usageMessage);
            return false;
        }
        List<Region> regions;
        switch (type) {
            case CREATOR:
            default:
                regions = this.regionManager.getPlayersRegionList((Player) sender, RegionGroup.CREATOR);
                break;
            case OWNER:
                regions = this.regionManager.getPlayersRegionList((Player) sender, RegionGroup.OWNER);
                break;
            case MEMBER:
                regions = this.regionManager.getPlayersRegionList((Player) sender, RegionGroup.MEMBER);
                break;
        }
        List<String> list = new ArrayList<>();
        regions.forEach(region -> list.add(region.getName()));
        switch (type) {
            case MEMBER:
                Messenger.getInstance().sendMessage(sender, "command.list.member-region-list", "@list", String.join(", ", list));
                break;
            case OWNER:
                Messenger.getInstance().sendMessage(sender, "command.list.owner-region-list", "@list", String.join(", ", list));
                break;
            case CREATOR:
                Messenger.getInstance().sendMessage(sender, "command.list.creator-region-list", "@list", String.join(", ", list));
                break;
        }
        return true;
    }
}