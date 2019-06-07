package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionGroup;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RegionListCommand extends SRegionProtectorCommand {

    private final RegionManager regionManager;

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
        RegionGroup type;
        try {
            type = RegionGroup.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            this.messenger.sendMessage(sender, "command.list.usage");
            return false;
        }
        List<Region> regions = this.regionManager.getPlayersRegionList((Player) sender, type);
        List<String> list = new ArrayList<>();
        regions.forEach(region -> list.add(region.name));
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
