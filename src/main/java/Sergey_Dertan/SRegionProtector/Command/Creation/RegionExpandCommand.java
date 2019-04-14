package Sergey_Dertan.SRegionProtector.Command.Creation;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import Sergey_Dertan.SRegionProtector.Region.Selector.SelectorSession;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Map;

@SuppressWarnings("WeakerAccess")
public final class RegionExpandCommand extends SRegionProtectorCommand {

    public static final String EXPAND_UP = "up";
    public static final String EXPAND_DOWN = "down";
    public static final String EXPAND_RADIUS = "radius";

    private final RegionSelector selector;

    public RegionExpandCommand(RegionSelector selector) {
        super("rgexpand", "expand");
        this.selector = selector;

        Map<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("rgexpand", new CommandParameter[]{
                new CommandParameter("amount", CommandParamType.INT, false),
                new CommandParameter("up/down/radius", false, new String[]{"up", "down", "radius"})
        });
        this.setCommandParameters(parameters);
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
        long y = Long.parseLong(args[0]);
        if (args[1].equalsIgnoreCase(EXPAND_UP)) {
            session.expandUp(y);
        } else if (args[1].equalsIgnoreCase(EXPAND_DOWN)) {
            session.expandDown(y);
        } else if (args[1].equalsIgnoreCase(EXPAND_RADIUS)) {
            session.expandRadius(y);
        } else {
            this.messenger.sendMessage(sender, "command.expand.up-or-down");
            return false;
        }
        session.fixHeight();
        if (this.selector.hasBorders((Player) sender)) {
            this.selector.removeBorders((Player) sender);
            this.selector.showBorders((Player) sender, session.pos1, session.pos2);
        }
        this.messenger.sendMessage(sender, "command.expand.success", "@size", Long.toString(session.calculateRegionSize()));
        return false;
    }
}
