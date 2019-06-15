package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Map;

public final class RegionFlagCommand extends SRegionProtectorCommand {

    private final RegionManager regionManager;
    private final boolean[] flagStatus;

    public RegionFlagCommand(RegionManager regionManager, boolean[] flagStatus) {
        super("rgflag", "flag");
        this.regionManager = regionManager;
        this.flagStatus = flagStatus;

        Map<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("flagdata", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.STRING, false),
                        new CommandParameter("flag", CommandParamType.STRING, false),
                        new CommandParameter("state", false, new String[]{"allow", "deny"})
                }
        );
        this.setCommandParameters(parameters);
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.flag.permission");
            return false;
        }
        if (args.length < 3) {
            this.messenger.sendMessage(sender, "command.flag.usage");
            return false;
        }

        int flag = RegionFlags.getFlagId(args[1]);
        if (flag == RegionFlags.FLAG_INVALID) {
            this.messenger.sendMessage(sender, "command.flag.incorrect-flag");
            return false;
        }

        if (!args[2].equalsIgnoreCase("allow") && !args[2].equalsIgnoreCase("deny")) {
            this.messenger.sendMessage(sender, "command.flag.wrong-state");
            return false;
        }

        Region region = this.regionManager.getRegion(args[0]);
        if (region == null) {
            this.messenger.sendMessage(sender, "command.flag.region-doesnt-exists");
            return false;
        }
        if (sender instanceof Player && !sender.hasPermission("sregionprotector.admin") && !region.isOwner(sender.getName(), true)) {
            this.messenger.sendMessage(sender, "command.flag.permission");
            return false;
        }

        if (!RegionFlags.hasFlagPermission(sender, flag)) {
            this.messenger.sendMessage(sender, "command.flag.permission");
            return false;
        }

        if (flag == RegionFlags.FLAG_SELL) {
            this.messenger.sendMessage(sender, "command.flag.sell");
            return false;
        }

        boolean state = RegionFlags.getStateFromString(args[2], flag);
        if (flag == RegionFlags.FLAG_TELEPORT) {
            if (state) {
                if (!(sender instanceof Player)) {
                    this.messenger.sendMessage(sender, "command.flag.teleport-flag-in-game");
                    return false;
                }
                if (!region.level.equalsIgnoreCase(((Player) sender).level.getName()) || !region.isVectorInside(((Player) sender))) {
                    this.messenger.sendMessage(sender, "command.flag.teleport-should-be-in-region");
                    return false;
                }
                region.setTeleportFlag(((Player) sender).getPosition(), true);
            } else {
                region.setTeleportFlag(null, false);
            }
        }
        region.setFlagState(flag, state);
        if (!this.flagStatus[flag]) {
            this.messenger.sendMessage(sender, "command.flag.disabled-warning");
        }
        this.messenger.sendMessage(sender, "command.flag.flag-" + (state ? "enabled" : "disabled"), new String[]{"@region", "@flag"}, new String[]{region.name, args[1]});
        return true;
    }
}
