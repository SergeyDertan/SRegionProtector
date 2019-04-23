package Sergey_Dertan.SRegionProtector.Command.Manage.Purchase;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Map;

public final class RegionRemoveFromSaleCommand extends SRegionProtectorCommand {

    private final RegionManager regionManager;

    public RegionRemoveFromSaleCommand(RegionManager regionManager) {
        super("rgremovefromsell", "remove-from-sell");
        this.regionManager = regionManager;

        Map<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("rgremovefromsell", new CommandParameter[]{
                new CommandParameter("region", CommandParamType.STRING, false)
        });
        this.setCommandParameters(parameters);
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player)) {
            this.messenger.sendMessage(sender, "command.remove-from-sell.in-game");
            return false;
        }
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.remove-from-sell.permission");
            return false;
        }
        if (args.length < 1) {
            this.messenger.sendMessage(sender, "command.remove-from-sell.usage");
            return false;
        }
        Region region = this.regionManager.getRegion(args[0]);
        if (region == null) {
            this.messenger.sendMessage(sender, "command.remove-from-sell.wrong-target");
            return false;
        }
        if (!sender.hasPermission("sregionprotector.admin") && !region.isCreator(sender.getName())) {
            this.messenger.sendMessage(sender, "command.remove-from-sell.not-creator");
            return false;
        }
        region.setSellFlagState(0L, false);
        this.messenger.sendMessage(sender, "command.remove-from-sell.success", "@region", region.name);
        return false;
    }
}
