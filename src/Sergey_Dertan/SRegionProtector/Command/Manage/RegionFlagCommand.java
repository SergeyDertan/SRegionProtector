package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

import java.util.Map;

public final class RegionFlagCommand extends SRegionProtectorCommand {

    private RegionManager regionManager;

    public RegionFlagCommand(String name, Map<String, String> messages, RegionManager regionManager) {
        super(name, messages);
        this.regionManager = regionManager;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermission(sender)) return false;
        if (args.length < 3) {
            sender.sendMessage(this.usageMessage);
            return false;
        }

        String regionName = args[0].toLowerCase();
        int flag = RegionFlags.getFlagId(args[1].toLowerCase());
        if (flag == RegionFlags.FLAG_INVALID) {
            this.sendMessage(sender, "incorrect-flag");
            return false;
        }


        Region region = this.regionManager.getRegion(regionName);
        if (region == null) {
            this.sendMessage(sender, "region-doesnt-exists");
            return false;
        }
        if (sender instanceof Player && !sender.hasPermission("sregionprotector.admin") && !region.isOwner((sender).getName().toLowerCase(), false)) {
            sender.sendMessage(this.getPermissionMessage());
            return false;
        }

        if (!RegionFlags.hasFlagPermission(sender, flag)) {
            this.sendMessage(sender, "permission");
            return false;
        }

        boolean state = RegionFlags.getStateFromString(args[2]);
        if (flag == RegionFlags.FLAG_TELEPORT) {
            if (state) {
                if (!(sender instanceof Player)) {
                    this.sendMessage(sender, "teleport-flag-in-game");
                    return false;
                }
                if (!region.getLevel().equals(((Player) sender).level.getName()) || !region.intersectsWith(((Player) sender).boundingBox)) {
                    this.sendMessage(sender, "teleport-should-be-in-region");
                    return false;
                }
                region.getFlagList().getTeleportFlag().position = ((Player) sender).getPosition();
            } else {
                region.getFlagList().getTeleportFlag().position = null;
            }
        }

        if (flag == RegionFlags.FLAG_SELL) {
            if (state) {
                if (args.length < 4) {
                    this.sendMessage(sender, "sell-flag-usage");
                    return false;
                }
                int price = new Integer(args[3]);
                if (price < 0) {
                    this.sendMessage(sender, "wrong-price");
                    return false;
                }
                region.getFlagList().getSellFlag().price = price;
                this.sendMessage(sender, "selling-region", new String[]{"@region", "@price"}, new String[]{region.getName(), args[3]});
            }
        }
        region.getFlagList().setFlagState(flag, state);
        this.sendMessage(sender, "flag-state-changed", new String[]{"@region", "@flag", "@state"}, new String[]{region.getName(), args[1], (state ? "enabled" : "disabled")}); //TODO
        return true;
    }
}