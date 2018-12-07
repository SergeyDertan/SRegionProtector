package Sergey_Dertan.SRegionProtector.Command.Manage.Group;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

import java.util.Map;

public final class AddOwnerCommand extends SRegionProtectorCommand {

    private RegionManager regionManager;

    public AddOwnerCommand(String name, Map<String, String> messages, RegionManager regionManager) {
        super(name, messages);
        this.regionManager = regionManager;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            Messenger.getInstance().sendMessage(sender, "command.addowner.permission");
            return false;
        }
        if (args.length < 2) {
            sender.sendMessage(this.usageMessage);
            return false;
        }

        Region region = this.regionManager.getRegion(args[0].toLowerCase());
        if (region == null) {
            Messenger.getInstance().sendMessage(sender, "command.addowner.region-doesnt-exists", "@region", args[0]);
            return false;
        }

        String target = args[1].toLowerCase();
        if (target.equals("")) {
            sender.sendMessage(this.usageMessage);
            return false;
        }
        if ((sender instanceof Player && !region.isCreator(sender.getName().toLowerCase())) && !sender.hasPermission("sregionprotector.admin")) {
            Messenger.getInstance().sendMessage(sender, "command.addowner.permission");
            return false;
        }
        if (region.isOwner(target)) {
            Messenger.getInstance().sendMessage(sender, "command.addowner.already-owner", new String[]{"@region", "@target"}, new String[]{region.getName(), target});
            return false;
        }
        this.regionManager.addOwner(region, target);
        Messenger.getInstance().sendMessage(sender, "command.addowner.owner-added", new String[]{"@region", "@target"}, new String[]{region.getName(), target});
        return true;
    }
}