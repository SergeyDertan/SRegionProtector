package Sergey_Dertan.SRegionProtector.Command.Creation;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

public final class SetPos2Command extends SRegionProtectorCommand {

    private RegionSelector selector;

    public SetPos2Command(RegionSelector selector) {
        super("pos2");
        this.selector = selector;

        this.setCommandParameters(new Object2ObjectArrayMap<>());
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.pos2.permission");
            return false;
        }
        if (!(sender instanceof Player)) {
            this.messenger.sendMessage(sender, "command.pos2.in-game");
            return false;
        }
        this.selector.getSession((Player) sender).pos2 = ((Player) sender).getPosition();
        this.messenger.sendMessage(sender, "command.pos2.pos-set");
        return false;
    }
}
