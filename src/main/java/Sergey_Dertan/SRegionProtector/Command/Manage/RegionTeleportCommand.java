package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionTeleportFlag;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

import java.util.Map;

public final class RegionTeleportCommand extends SRegionProtectorCommand {

    private RegionManager regionManager;

    public RegionTeleportCommand(String name, Map<String, String> messages, RegionManager regionManager) {
        super(name, messages);
        this.regionManager = regionManager;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            Messenger.getInstance().sendMessage(sender, "command.teleport.permission");
            return false;
        }
        if (!(sender instanceof Player)) {
            Messenger.getInstance().sendMessage(sender, "command.teleport.in-game");
            return false;
        }
        if (args.length < 1) {
            sender.sendMessage(this.usageMessage);
            return false;
        }
        Region region = this.regionManager.getRegion(args[0].toLowerCase());
        if (region == null) {
            Messenger.getInstance().sendMessage(sender, "command.teleport.region-doesnt-exists");
            return false;
        }
        if (!sender.hasPermission("sregionprotector.admin") && !region.isLivesIn(sender.getName().toLowerCase())) {
            Messenger.getInstance().sendMessage(sender, "command.teleport.permission");
            return false;
        }
        RegionTeleportFlag flag = region.getFlagList().getTeleportFlag();
        if (!flag.state || flag.position == null) {
            Messenger.getInstance().sendMessage(sender, "command.teleport.teleport-disabled");
            return false;
        }
        ((Player) sender).teleport(flag.position);
        Messenger.getInstance().sendMessage(sender, "command.teleport.teleport", "@region", region.getName());
        return true;
    }
}