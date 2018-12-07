package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
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
        if (!this.testPermissionSilent(sender)) {
            Messenger.getInstance().sendMessage(sender, "command.flag.permission");
            return false;
        }
        if (args.length < 3) {
            sender.sendMessage(this.usageMessage);
            return false;
        }

        String regionName = args[0].toLowerCase();
        int flag = RegionFlags.getFlagId(args[1].toLowerCase());
        if (flag == RegionFlags.FLAG_INVALID) {
            Messenger.getInstance().sendMessage(sender, "command.flag.incorrect-flag");
            return false;
        }

        Region region = this.regionManager.getRegion(regionName);
        if (region == null) {
            Messenger.getInstance().sendMessage(sender, "command.flag.region-doesnt-exists");
            return false;
        }
        if (sender instanceof Player && !sender.hasPermission("sregionprotector.admin") && !region.isOwner((sender).getName().toLowerCase(), false)) {
            Messenger.getInstance().sendMessage(sender, "command.flag.permission");
            return false;
        }

        if (!RegionFlags.hasFlagPermission(sender, flag)) {
            Messenger.getInstance().sendMessage(sender, "command.flag.permission");
            return false;
        }

        boolean state = RegionFlags.getStateFromString(args[2]);
        if (flag == RegionFlags.FLAG_TELEPORT) {
            if (state) {
                if (!(sender instanceof Player)) {
                    Messenger.getInstance().sendMessage(sender, "command.flag.teleport-flag-in-game");
                    return false;
                }
                if (!region.getLevel().equals(((Player) sender).level.getName()) || !region.intersectsWith(((Player) sender).boundingBox)) {
                    Messenger.getInstance().sendMessage(sender, "command.flag.teleport-should-be-in-region");
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
                    Messenger.getInstance().sendMessage(sender, "command.flag.sell-flag-usage");
                    return false;
                }
                int price = new Integer(args[3]);
                if (price < 0) {
                    Messenger.getInstance().sendMessage(sender, "command.flag.wrong-price");
                    return false;
                }
                region.getFlagList().getSellFlag().price = price;
                Messenger.getInstance().sendMessage(sender, "command.flag.selling-region", new String[]{"@region", "@price"}, new String[]{region.getName(), args[3]});
            }
        }
        region.getFlagList().setFlagState(flag, state);
        Messenger.getInstance().sendMessage(sender, "command.flag.flag-state-changed", new String[]{"@region", "@flag", "@state"}, new String[]{region.getName(), args[1], (state ? "enabled" : "disabled")}); //TODO
        return true;
    }
}