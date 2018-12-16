package Sergey_Dertan.SRegionProtector.Command.Creation;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

import java.util.Map;

public final class SetPos2Command extends SRegionProtectorCommand {
    private RegionSelector selector;

    public SetPos2Command(String name, Map<String, String> messages, RegionSelector selector) {
        super(name, messages);
        this.selector = selector;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        if (!this.testPermissionSilent(sender)) {
            Messenger.getInstance().sendMessage(sender, "command.pos2.permission");
            return false;
        }
        if (!(sender instanceof Player)) {
            Messenger.getInstance().sendMessage(sender, "command.pos2.in-game");
            return false;
        }
        this.selector.getSession((Player) sender).pos2 = ((Player) sender).getPosition();
        Messenger.getInstance().sendMessage(sender, "command.pos2.pos-set");
        return false;
    }
}