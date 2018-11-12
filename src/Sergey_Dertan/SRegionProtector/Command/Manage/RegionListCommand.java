package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RegionListCommand extends SRegionProtectorCommand {

    private RegionManager regionManager;

    public static final String OWNER = "owner";
    public static final String MEMBER = "member";
    public static final String CREATOR = "creator";

    public RegionListCommand(String name, Map<String, String> messages, RegionManager regionManager) {
        super(name, messages);
        this.regionManager = regionManager;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermission(sender)) return false;
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, "in-game");
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
        if (type.equals(MEMBER)) {
            regions = this.regionManager.getPlayerMemberRegions((Player) sender);
        } else if (type.equals(OWNER)) {
            regions = this.regionManager.getOwningRegions((Player) sender);
        } else {
            regions = this.regionManager.getOwningRegions((Player) sender, true);
        }
        List<String> list = new ArrayList<>();
        regions.forEach(region -> list.add(region.getName()));
        if (type.equals(MEMBER)) {
            this.sendMessage(sender, "member-region-list", "@list", String.join(", ", list));
        } else if (type.equals(OWNER)) {
            this.sendMessage(sender, "owner-region-list", "@list", String.join(", ", list));
        } else {
            this.sendMessage(sender, "creator-region-list", "@list", String.join(", ", list));
        }
        return true;
    }
}