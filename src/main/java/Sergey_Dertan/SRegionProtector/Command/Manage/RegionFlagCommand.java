package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.math.Vector3;

public final class RegionFlagCommand extends SRegionProtectorCommand {

    private RegionManager regionManager;

    public RegionFlagCommand(String name, RegionManager regionManager) {
        super(name);
        this.regionManager = regionManager;
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

        String regionName = args[0].toLowerCase();
        int flag = RegionFlags.getFlagId(args[1].toLowerCase());
        if (flag == RegionFlags.FLAG_INVALID) {
            this.messenger.sendMessage(sender, "command.flag.incorrect-flag");
            return false;
        }

        Region region = this.regionManager.getRegion(regionName);
        if (region == null) {
            this.messenger.sendMessage(sender, "command.flag.region-doesnt-exists");
            return false;
        }
        if (sender instanceof Player && !sender.hasPermission("sregionprotector.admin") && !region.isOwner((sender).getName().toLowerCase(), false)) {
            this.messenger.sendMessage(sender, "command.flag.permission");
            return false;
        }

        if (!RegionFlags.hasFlagPermission(sender, flag)) {
            this.messenger.sendMessage(sender, "command.flag.permission");
            return false;
        }

        boolean state = RegionFlags.getStateFromString(args[2]);
        if (flag == RegionFlags.FLAG_TELEPORT) {
            if (state) {
                if (!(sender instanceof Player)) {
                    this.messenger.sendMessage(sender, "command.flag.teleport-flag-in-game");
                    return false;
                }
                if (!region.level.equalsIgnoreCase(((Player) sender).level.getName()) || !region.intersectsWith(((Player) sender).boundingBox)) {
                    this.messenger.sendMessage(sender, "command.flag.teleport-should-be-in-region");
                    return false;
                }
                region.setTeleportFlag(((Player) sender).getPosition(), true);
            } else {
                region.setTeleportFlag(null, false);
            }
        } else if (flag == RegionFlags.FLAG_SELL) {
            if (state) {
                if (args.length < 4) {
                    this.messenger.sendMessage(sender, "command.flag.sellData-flag-usage");
                    return false;
                }
                if (this.regionManager.checkOverlap(new Vector3(region.minX, region.minY, region.minZ), new Vector3(region.maxX, region.maxY, region.maxZ), region.level, "")) {
                    this.messenger.sendMessage(sender, "command.flag.cant-sell-region-in-region");
                    return false;
                }
                int price = Integer.valueOf(args[3]);
                if (price < 0) {
                    this.messenger.sendMessage(sender, "command.flag.wrong-price");
                    return false;
                }
                region.setSellFlagState(price, true);
                this.messenger.sendMessage(sender, "command.flag.selling-region", new String[]{"@region", "@price"}, new String[]{region.getName(), args[3]});
                return false;
            }
        }
        region.setFlagState(flag, state);
        this.messenger.sendMessage(sender, "command.flag.flag-state-changed", new String[]{"@region", "@flag", "@state"}, new String[]{region.getName(), args[1], (state ? this.messenger.getMessage("region.flag.state.enabled") : this.messenger.getMessage("region.flag.state.disabled"))});
        return true;
    }
}