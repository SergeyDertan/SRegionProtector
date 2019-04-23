package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Map;

public final class RegionRemoveCommand extends SRegionProtectorCommand {

    private final RegionManager regionManager;

    public RegionRemoveCommand(RegionManager regionManager) {
        super("rgremove", "remove");
        this.regionManager = regionManager;

        Map<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("rgremove-rg", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.STRING, false)
                }
        );
        this.setCommandParameters(parameters);
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
        if (!sender.hasPermission("sregionprotector.admin") && (sender instanceof Player && !region.isCreator(sender.getName()))) {
            this.messenger.sendMessage(sender, "command.remove.permission");
            return false;
        }
        this.regionManager.removeRegion(region);
        this.messenger.sendMessage(sender, "command.remove.region-removed", "@region", region.name);
        return true;
    }
}
