package Sergey_Dertan.SRegionProtector.Command.Manage.Group;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Map;

public final class RemoveMemberCommand extends SRegionProtectorCommand {

    private RegionManager regionManager;

    public RemoveMemberCommand(RegionManager regionManager) {
        super("rgremovemember", "removemember");
        this.regionManager = regionManager;

        Map<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("removemember", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.STRING, false),
                        new CommandParameter("player", CommandParamType.TARGET, false)
                }
        );
        this.setCommandParameters(parameters);
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.removemember.permission");

            return false;
        }
        if (args.length < 2) {
            this.messenger.sendMessage(sender, "command.removemember.usage");
            return false;
        }

        Region region = this.regionManager.getRegion(args[0]);
        if (region == null) {
            this.messenger.sendMessage(sender, "command.removemember.region-doesnt-exists", "@region", args[0]);
            return false;
        }

        String target = args[1];
        if (target.isEmpty()) {
            this.messenger.sendMessage(sender, "command.removemember.usage");
            return false;
        }
        if ((sender instanceof Player && !region.isOwner(sender.getName(), true)) && !sender.hasPermission("sregionprotector.admin")) {
            this.messenger.sendMessage(sender, "command.removemember.permission", "@region", args[0]);
            return false;
        }
        if (!region.isMember(target)) {
            this.messenger.sendMessage(sender, "command.removemember.not-a-member", new String[]{"@region", "@target"}, new String[]{region.getName(), target});
            return false;
        }
        this.regionManager.removeMember(region, target);
        this.messenger.sendMessage(sender, "command.removemember.member-removed", new String[]{"@region", "@target"}, new String[]{region.getName(), target});
        return true;
    }
}