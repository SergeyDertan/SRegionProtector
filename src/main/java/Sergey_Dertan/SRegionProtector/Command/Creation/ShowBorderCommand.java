package Sergey_Dertan.SRegionProtector.Command.Creation;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import Sergey_Dertan.SRegionProtector.Region.Selector.SelectorSession;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;

import java.util.HashMap;
import java.util.Map;

public class ShowBorderCommand extends SRegionProtectorCommand {

    private RegionSelector selector;

    public ShowBorderCommand(RegionSelector selector) {
        super("rgshowborder", "show-border");
        this.selector = selector;

        Map<String, CommandParameter[]> parameters = new HashMap<>();
        parameters.put("rgshowborder", new CommandParameter[0]);
        this.setCommandParameters(parameters);
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!(sender instanceof Player)) {
            this.messenger.sendMessage(sender, "command.show-border.in-game");
            return false;
        }
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.show-border.permission");
            return false;
        }
        if (!this.selector.sessionExists((Player) sender)) {
            this.messenger.sendMessage(sender, "command.show-border.no-pos");
            return false;
        }
        SelectorSession session = this.selector.getSession((Player) sender);
        if (session.pos1.level != session.pos2.level) {
            this.messenger.sendMessage(sender, "command.show-border.positions-in-different-worlds");
            return false;
        }
        this.messenger.sendMessage(sender, "command.show-border.success");
        this.selector.showBorders((Player) sender, session.pos1, session.pos2);
        return false;
    }
}