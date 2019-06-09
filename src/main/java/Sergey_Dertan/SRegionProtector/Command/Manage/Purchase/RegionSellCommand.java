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

public final class RegionSellCommand extends SRegionProtectorCommand {

    private final RegionManager regionManager;

    public RegionSellCommand(RegionManager regionManager) {
        super("rgsell", "sell");
        this.regionManager = regionManager;

        Map<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("rgsell", new CommandParameter[]{
                new CommandParameter("region", CommandParamType.STRING, false),
                new CommandParameter("price", CommandParamType.INT, false)
        });
        this.setCommandParameters(parameters);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            this.messenger.sendMessage(sender, "command.sell.in-game");
            return false;
        }
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.sell.permission");
            return false;
        }
        if (args.length < 2) {
            this.messenger.sendMessage(sender, "command.sell.usage");
            return false;
        }
        Region region = this.regionManager.getRegion(args[0]);
        if (region == null) {
            this.messenger.sendMessage(sender, "command.sell.wrong-target");
            return false;
        }
        if (!sender.hasPermission("sregionprotector.admin") && !region.isCreator(sender.getName())) {
            this.messenger.sendMessage(sender, "command.sell.not-creator");
            return false;
        }
        if (this.regionManager.checkOverlap(region.getMin(), region.getMax(), region.level, "", false, region)) {
            this.messenger.sendMessage(sender, "command.sell.overlap");
            return false;
        }
        long price = Long.parseLong(args[1]);
        if (price < 0) {
            this.messenger.sendMessage(sender, "command.sell.min-price");
            return false;
        }
        region.setSellFlagState(price, true);
        this.messenger.sendMessage(sender, "command.sell.success", new String[]{
                        "@region",
                        "@price"
                },
                new String[]{
                        region.name,
                        Long.toString(price)
                }
        );
        return true;
    }
}
