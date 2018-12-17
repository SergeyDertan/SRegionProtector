package Sergey_Dertan.SRegionProtector.Command.Manage.Group;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

public final class AddMemberCommand extends SRegionProtectorCommand {

    private RegionManager regionManager;

    public AddMemberCommand(String name, RegionManager regionManager) {
        super(name);
        this.regionManager = regionManager;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.addmember.permission");
            return false;
        }
        if (args.length < 2) {
            this.messenger.sendMessage(sender, "command.addmember.usage");
            return false;
        }

        Region region = this.regionManager.getRegion(args[0]);
        if (region == null) {
            this.messenger.sendMessage(sender, "command.addmember.region-doesnt-exists", "@region", args[0]);
            return false;
        }

        String target = args[1];
        if (target.isEmpty()) {
            this.messenger.sendMessage(sender, "command.addmember.usage");
            return false;
        }
        if ((sender instanceof Player && !region.isOwner(sender.getName(), true)) && !sender.hasPermission("sregionprotector.admin")) {
            this.messenger.sendMessage(sender, "command.addmember.permission");
            return false;
        }
        if (region.isMember(target)) {
            this.messenger.sendMessage(sender, "command.addmember.already-member", new String[]{"@region", "@target"}, new String[]{region.getName(), target});
            return false;
        }
        this.regionManager.addMember(region, target);
        this.messenger.sendMessage(sender, "command.addmember.member-added", new String[]{"@region", "@target"}, new String[]{region.getName(), target});
        return true;
    }
}