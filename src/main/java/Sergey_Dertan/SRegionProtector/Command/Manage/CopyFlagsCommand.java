package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Map;

public class CopyFlagsCommand extends SRegionProtectorCommand {

    private final RegionManager regionManager;

    public CopyFlagsCommand(RegionManager regionManager) {
        super("rgcopyflags", "copy-flags");
        this.regionManager = regionManager;

        Map<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("copyflags", new CommandParameter[]
                {
                        new CommandParameter("source", CommandParamType.STRING, false),
                        new CommandParameter("target", CommandParamType.STRING, false)
                }
        );
        this.setCommandParameters(parameters);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.copy-flags.permission");
            return false;
        }
        if (args.length < 2) {
            this.messenger.sendMessage(sender, "command.copy-flags.usage");
            return false;
        }

        Region source = this.regionManager.getRegion(args[0]);
        Region target = this.regionManager.getRegion(args[1]);

        if (source == null) {
            this.messenger.sendMessage(sender, "command.copy-flags.invalid-source");
            return false;
        }
        if (target == null) {
            this.messenger.sendMessage(sender, "command.copy-flags.invalid-target");
            return false;
        }
        for (int i = 0; i < RegionFlags.FLAG_AMOUNT; ++i) {
            target.setFlagState(i, source.getFlagState(i));
        }
        target.setSellFlagState(source.getSellFlagPrice(), source.getFlagState(RegionFlags.FLAG_SELL));
        target.setTeleportFlag(source.getTeleportFlagPos(), source.getFlagState(RegionFlags.FLAG_TELEPORT));

        this.messenger.sendMessage(sender, "command.copy-flags.success", new String[]{"@source", "@target"}, new String[]{source.name, target.name});
        return true;
    }
}
