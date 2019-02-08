package Sergey_Dertan.SRegionProtector.Command.Creation;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

public final class SetPos1Command extends SRegionProtectorCommand {

    private RegionSelector selector;

    public SetPos1Command(RegionSelector selector) {
        super("pos1");
        this.selector = selector;

        this.setCommandParameters(new Object2ObjectArrayMap<>());
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.pos1.permission");
            return false;
        }
        if (!(sender instanceof Player)) {
            this.messenger.sendMessage(sender, "command.pos1.in-game");
            return false;
        }
        this.selector.getSession((Player) sender).pos1 = ((Player) sender).getPosition();
        this.messenger.sendMessage(sender, "command.pos1.pos-set");
        return false;
    }
}
