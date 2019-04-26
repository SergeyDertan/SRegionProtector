package Sergey_Dertan.SRegionProtector.Command.Creation;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.MainLogger;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Map;

public final class Pos1Command extends SRegionProtectorCommand {

    private final RegionSelector selector;

    public Pos1Command(RegionSelector selector) {
        super("pos1");
        this.selector = selector;

        Map<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("target_pos1", new CommandParameter[]{
                new CommandParameter("target", CommandParamType.POSITION, true)
        });
        this.setCommandParameters(parameters);
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.pos1.permission");
            return false;
        }
        if (!(sender instanceof Player)) {
            this.messenger.sendMessage(sender, "command.pos1.in-game");
            return false;
        }
        if (args.length >= 3) {
            try {
                double x = Double.parseDouble(args[0]);
                double y = Double.parseDouble(args[1]);
                double z = Double.parseDouble(args[2]);
                this.selector.getSession((Player) sender).pos1 = Position.fromObject(new Vector3(x, y, z), ((Player) sender).level);
            } catch (NumberFormatException e) {
                this.messenger.sendMessage(sender, "command.pos1.wrong-coordinates");
                return false;
            }
        } else {
            this.selector.getSession((Player) sender).pos1 = ((Player) sender).getPosition();
        }
        this.messenger.sendMessage(sender, "command.pos1.pos-set");
        return false;
    }
}
