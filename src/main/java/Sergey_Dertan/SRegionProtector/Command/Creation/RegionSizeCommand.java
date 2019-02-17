package Sergey_Dertan.SRegionProtector.Command.Creation;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import Sergey_Dertan.SRegionProtector.Region.Selector.SelectorSession;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

public final class RegionSizeCommand extends SRegionProtectorCommand {

    private RegionSelector selector;

    public RegionSizeCommand(RegionSelector selector) {
        super("rgsize", "size");
        this.selector = selector;

        this.setCommandParameters(new Object2ObjectArrayMap<>());
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            this.messenger.sendMessage(sender, "command.size.in-game");
            return false;
        }
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.size.permission");
            return false;
        }
        if (!this.selector.sessionExists((Player) sender)) {
            this.messenger.sendMessage(sender, "command.size.select-first");
            return false;
        }
        SelectorSession session = this.selector.getSession((Player) sender);
        if (session.pos1 == null || session.pos2 == null) {
            this.messenger.sendMessage(sender, "command.size.select-first");
            return false;
        }
        if (session.pos1.level != session.pos2.level) {
            this.messenger.sendMessage(sender, "command.size.positions-in-different-worlds");
            return false;
        }
        this.messenger.sendMessage(sender, "command.size.size", "@size", Long.toString(session.calculateRegionSize()));
        return false;
    }
}
