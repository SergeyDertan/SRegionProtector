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

public final class AddOwnerCommand extends SRegionProtectorCommand {

    private RegionManager regionManager;

    public AddOwnerCommand(RegionManager regionManager) {
        super("rgaddowner", "addowner");
        this.regionManager = regionManager;

        Map<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("addowner", new CommandParameter[]
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
            this.messenger.sendMessage(sender, "command.addowner.permission");
            return false;
        }
        if (args.length < 2) {
            this.messenger.sendMessage(sender, "command.addowner.usage");
            return false;
        }

        Region region = this.regionManager.getRegion(args[0]);
        if (region == null) {
            this.messenger.sendMessage(sender, "command.addowner.region-doesnt-exists", "@region", args[0]);
            return false;
        }

        String target = args[1];
        if (target.isEmpty()) {
            this.messenger.sendMessage(sender, "command.addowner.usage");
            return false;
        }
        if ((sender instanceof Player && !region.isCreator(sender.getName())) && !sender.hasPermission("sregionprotector.admin")) {
            this.messenger.sendMessage(sender, "command.addowner.permission");
            return false;
        }
        if (region.isOwner(target)) {
            this.messenger.sendMessage(sender, "command.addowner.already-owner", new String[]{"@region", "@target"}, new String[]{region.getName(), target});
            return false;
        }
        this.regionManager.addOwner(region, target);
        this.messenger.sendMessage(sender, "command.addowner.owner-added", new String[]{"@region", "@target"}, new String[]{region.getName(), target});
        return true;
    }
}