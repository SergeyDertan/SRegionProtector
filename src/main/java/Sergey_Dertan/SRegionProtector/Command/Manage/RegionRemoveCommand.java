package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

public final class RegionRemoveCommand extends SRegionProtectorCommand {

    private RegionManager regionManager;

    public RegionRemoveCommand(String name, RegionManager regionManager) {
        super(name);
        this.regionManager = regionManager;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.remove.permission");
            return false;
        }
        if (args.length < 1) {
            this.messenger.sendMessage(sender, "command.remove.usage");
            return false;
        }
        Region region = this.regionManager.getRegion(args[0]);
        if (region == null) {
            this.messenger.sendMessage(sender, "command.remove.region-doesnt-exists", "@region", args[0]);
            return false;
        }
        if (!sender.hasPermission("sregionprotector.admin") && (sender instanceof Player && !region.isOwner(sender.getName()))) {
            this.messenger.sendMessage(sender, "command.remove.permission");
            return false;
        }
        this.regionManager.removeRegion(region);
        this.messenger.sendMessage(sender, "command.remove.region-removed", "@region", region.getName());
        return true;
    }
}