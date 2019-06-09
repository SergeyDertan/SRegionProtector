package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Map;

public final class RegionSelectCommand extends SRegionProtectorCommand {

    private final RegionManager regionManager;
    private final RegionSelector selector;
    private final long maxBordersAmount;

    public RegionSelectCommand(RegionManager regionManager, RegionSelector selector, long maxBordersAmount) {
        super("rgselect", "select");
        this.regionManager = regionManager;
        this.selector = selector;
        this.maxBordersAmount = maxBordersAmount;

        Map<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("rgselect", new CommandParameter[]{
                new CommandParameter("region", CommandParamType.STRING, false)
        });
        this.setCommandParameters(parameters);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            this.messenger.sendMessage(sender, "command.select.in-game");
            return false;
        }
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.select.permission");
            return false;
        }
        if (args.length < 1 || args[0].isEmpty()) {
            this.messenger.sendMessage(sender, "command.select.usage");
            return false;
        }
        Region region = this.regionManager.getRegion(args[0]);
        if (region == null) {
            this.messenger.sendMessage(sender, "command.select.region-doesnt-exists", "@region", args[0]);
            return false;
        }
        if (!region.isLivesIn(sender.getName()) && !sender.hasPermission("sregionprotector.region.select-other") && !region.isSelling()) {
            this.messenger.sendMessage(sender, "command.select.permission");
            return false;
        }
        if (!region.level.equalsIgnoreCase(((Player) sender).level.getName())) {
            this.messenger.sendMessage(sender, "command.select.different-worlds");
            return false;
        }
        if (this.selector.calculateEdgesLength(region.getMin(), region.getMax()) > this.maxBordersAmount) {
            this.messenger.sendMessage(sender, "command.select.too-long");
            return false;
        }
        this.messenger.sendMessage(sender, "command.select.success");
        this.selector.showBorders((Player) sender, region.getMin(), region.getMax().add(-1, -1, -1));
        return false;
    }
}
