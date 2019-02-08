package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionGroup;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.Map;
import java.util.Set;

import static Sergey_Dertan.SRegionProtector.Region.RegionGroup.*;

public final class RegionListCommand extends SRegionProtectorCommand {

    private RegionManager regionManager;

    public RegionListCommand(RegionManager regionManager) {
        super("rglist", "list");
        this.regionManager = regionManager;

        Map<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("list-type", new CommandParameter[]
                {
                        new CommandParameter("type", false, new String[]{"owner", "member", "creator"})
                }
        );
        this.setCommandParameters(parameters);
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.list.permission");
            return false;
        }
        if (!(sender instanceof Player)) {
            this.messenger.sendMessage(sender, "command.list.in-game");
            return false;
        }
        if (args.length < 1) {
            this.messenger.sendMessage(sender, "command.list.usage");
            return false;
        }
        RegionGroup type = RegionGroup.get(args[0]);
        if (type == null) {
            this.messenger.sendMessage(sender, "command.list.usage");
            return false;
        }
        Set<Region> regions;
        switch (type) {
            case CREATOR:
            default:
                regions = this.regionManager.getPlayersRegionList((Player) sender, CREATOR);
                break;
            case OWNER:
                regions = this.regionManager.getPlayersRegionList((Player) sender, OWNER);
                break;
            case MEMBER:
                regions = this.regionManager.getPlayersRegionList((Player) sender, MEMBER);
                break;
        }
        Set<String> list = new ObjectArraySet<>();
        regions.forEach(region -> list.add(region.getName()));
        switch (type) {
            case MEMBER:
                this.messenger.sendMessage(sender, "command.list.member-region-list", "@list", String.join(", ", list));
                break;
            case OWNER:
                this.messenger.sendMessage(sender, "command.list.owner-region-list", "@list", String.join(", ", list));
                break;
            case CREATOR:
                this.messenger.sendMessage(sender, "command.list.creator-region-list", "@list", String.join(", ", list));
                break;
        }
        return true;
    }
}
