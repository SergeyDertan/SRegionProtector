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

public final class SetPriorityCommand extends SRegionProtectorCommand {

    private final RegionManager regionManager;
    private final boolean prioritySystem;

    public SetPriorityCommand(RegionManager regionManager, boolean prioritySystem) {
        super("rgsetpriority", "set-priority");
        this.regionManager = regionManager;
        this.prioritySystem = prioritySystem;

        Map<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("rgsetpriority", new CommandParameter[]{
                new CommandParameter("region", CommandParamType.STRING, false),
                new CommandParameter("priority", CommandParamType.INT, false)
        });
        this.setCommandParameters(parameters);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.set-priority.permission");
            return false;
        }
        if (args.length < 2) {
            this.messenger.sendMessage(sender, "command.set-priority.usage");
            return false;
        }
        Region region = regionManager.getRegion(args[0]);
        if (region == null) {
            this.messenger.sendMessage(sender, "command.set-priority.wrong-target");
            return false;
        }
        if (sender instanceof Player && !sender.hasPermission("sregionprotector.admin") && region.isCreator(sender.getName())) {
            this.messenger.sendMessage(sender, "command.set-priority.permission");
            return false;
        }
        int priority = Integer.parseInt(args[1]);
        region.setPriority(priority);
        this.messenger.sendMessage(sender, "command.set-priority.success", new String[]{"@region", "@priority"}, new String[]{region.name, Integer.toString(priority)});
        if (!this.prioritySystem) this.messenger.sendMessage(sender, "command.set-priority.warning");
        return true;
    }
}
