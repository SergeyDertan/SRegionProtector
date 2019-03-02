package Sergey_Dertan.SRegionProtector.Command.Manage.Purchase;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

public final class RegionPriceCommand extends SRegionProtectorCommand {

    private final RegionManager regionManager;

    public RegionPriceCommand(RegionManager regionManager) {
        super("rgprice", "price");
        this.regionManager = regionManager;

        Object2ObjectMap<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("rgprice", new CommandParameter[]{new CommandParameter("region", CommandParamType.TEXT, false)});
        this.setCommandParameters(parameters);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.price.permission");
            return false;
        }
        if (args.length == 0) {
            this.messenger.sendMessage(sender, "command.price.usage");
            return false;

        }
        Region region = this.regionManager.getRegion(args[0]);
        if (region == null) {
            this.messenger.sendMessage(sender, "command.price.wrong-target");
            return false;

        }
        if (!region.isSelling()) {
            this.messenger.sendMessage(sender, "command.price.doesnt-selling");
            return false;
        }
        this.messenger.sendMessage(sender, "command.price.success", new String[]{"@region", "@price"}, new String[]{region.name, Long.toString(region.getSellFlagPrice())});
        return false;
    }
}
