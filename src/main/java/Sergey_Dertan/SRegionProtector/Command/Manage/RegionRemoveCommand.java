package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

import java.util.Map;

public final class RegionRemoveCommand extends SRegionProtectorCommand {

    private RegionManager regionManager;

    public RegionRemoveCommand(String name, Map<String, String> messages, RegionManager regionManager) {
        super(name, messages);
        this.regionManager = regionManager;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            Messenger.getInstance().sendMessage(sender, "command.remove.permission");
            return false;
        }
        if (args.length < 1) {
            sender.sendMessage(this.usageMessage);
            return false;
        }
        Region region = this.regionManager.getRegion(args[0].toLowerCase());
        if (region == null) {
            Messenger.getInstance().sendMessage(sender, "command.remove.region-doesnt-exists", "@region", args[0]);
            return false;
        }
        if (!sender.hasPermission("sregionprotector.admin") && (sender instanceof Player && !region.isOwner(sender.getName().toLowerCase()))) {
            Messenger.getInstance().sendMessage(sender, "command.remove.permission");
            return false;
        }
        this.regionManager.removeRegion(region);
        Messenger.getInstance().sendMessage(sender, "command.remove.region-removed", "@region", region.getName());
        return true;
    }
}