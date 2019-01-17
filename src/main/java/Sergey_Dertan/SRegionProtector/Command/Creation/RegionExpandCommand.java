package Sergey_Dertan.SRegionProtector.Command.Creation;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import Sergey_Dertan.SRegionProtector.Region.Selector.SelectorSession;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

public final class RegionExpandCommand extends SRegionProtectorCommand {

    public static final String EXPAND_UP = "up";
    public static final String EXPAND_DOWN = "down";

    private RegionSelector selector;

    public RegionExpandCommand(String name, RegionSelector selector) {
        super(name);
        this.selector = selector;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            this.messenger.sendMessage(sender, "command.expand.in-game");
            return false;
        }
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.expand.in-game");
            return false;
        }
        if (!this.selector.sessionExists((Player) sender)) {
            this.messenger.sendMessage(sender, "command.expand.positions-required");
            return false;
        }
        SelectorSession session = this.selector.getSession((Player) sender);
        if (session.pos1.level != session.pos2.level) {
            this.messenger.sendMessage(sender, "command.expand.positions-in-different-worlds");
            return false;
        }
        if (args.length < 2) {
            this.messenger.sendMessage(sender, "command.expand.usage");
            return false;
        }
        int y = Integer.valueOf(args[0]);
        if (args[1].equalsIgnoreCase(EXPAND_UP)) {
            if (session.pos1.y > session.pos2.y) {
                session.pos1.y += y;
            } else {
                session.pos2.y += y;
            }
        } else if (args[1].equalsIgnoreCase(EXPAND_DOWN)) {
            if (session.pos1.y < session.pos2.y) {
                session.pos1.y -= y;
            } else {
                session.pos2.y -= y;
            }
        } else {
            this.messenger.sendMessage(sender, "command.expand.up-or-down");
            return false;
        }
        if (this.selector.hasBorders((Player) sender)) {
            this.selector.removeBorders((Player) sender);
            this.selector.showBorders((Player) sender, session.pos1, session.pos2);
        }
        this.messenger.sendMessage(sender, "command.expand.success", "@size", String.valueOf(session.calculateRegionSize()));
        return false;
    }
}
