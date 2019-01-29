package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

public final class RemoveBordersCommand extends SRegionProtectorCommand {

    private RegionSelector selector;

    public RemoveBordersCommand(RegionSelector selector) {
        super("rgremoveborders", "remove-borders");
        this.selector = selector;

        this.setCommandParameters(new Object2ObjectArrayMap<>());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            this.messenger.sendMessage(sender, "command.remove-borders.in-game");
            return false;
        }
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.remove-borders.permission");
            return false;
        }
        this.messenger.sendMessage(sender, "command.remove-borders.success");
        this.selector.removeBorders((Player) sender);
        return false;
    }
}
