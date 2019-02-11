package Sergey_Dertan.SRegionProtector.Command.Creation;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

public final class LPos2Command extends SRegionProtectorCommand {
    private RegionSelector selector;
    private int maxRadius;

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
        double x = -Math.sin(((Player) sender).yaw / 180 * Math.PI) * Math.cos(((Player) sender).pitch / 180 * Math.PI);
        double y = -Math.sin(((Player) sender).pitch / 180 * Math.PI);
        double z = Math.cos(((Player) sender).yaw / 180 * Math.PI) * Math.cos(((Player) sender).pitch / 180 * Math.PI);
        Vector3 pos = new Vector3(((Player) sender).x, ((Player) sender).y, ((Player) sender).z);
        while (pos.distance(((Vector3) sender)) < this.maxRadius) {
            if (((Player) sender).level.getBlock(pos = pos.add(x, y, z)).getId() == 0) continue;
            this.selector.getSession((Player) sender).pos2 = new Position(Math.floor(pos.x), Math.floor(pos.y), Math.floor(pos.z), ((Player) sender).level);
            this.messenger.sendMessage(sender, "command.lpos2.success");
            return true;
        }
        this.messenger.sendMessage(sender, "command.lpos2.fail", "@radius", Integer.toString(this.maxRadius));
        return false;
    }
}
