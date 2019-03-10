package Sergey_Dertan.SRegionProtector.Command.Creation;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Position;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

public final class LPos2Command extends SRegionProtectorCommand {

    private final RegionSelector selector;
    private final int maxRadius;

    public LPos2Command(RegionSelector selector, int maxRadius) {
        super("lpos2");
        this.selector = selector;

        this.maxRadius = maxRadius;

        this.setCommandParameters(new Object2ObjectArrayMap<>());
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.lpos2.permission");
            return false;
        }
        if (!(sender instanceof Player)) {
            this.messenger.sendMessage(sender, "command.lpos2.in-game");
            return false;
        }
        Position pos = ((Player) sender).getTargetBlock(this.maxRadius);
        if (pos != null) {
            this.selector.getSession((Player) sender).pos2 = Position.fromObject(pos, pos.level);
            this.messenger.sendMessage(sender, "command.lpos2.success");
            return true;
        } else {
            this.messenger.sendMessage(sender, "command.lpos2.fail", "@radius", Integer.toString(this.maxRadius));
            return false;
        }
    }
}
