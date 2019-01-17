package Sergey_Dertan.SRegionProtector.Command;

import cn.nukkit.command.CommandSender;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import java.util.Arrays;

public final class RegionCommand extends SRegionProtectorCommand {

    private Object2ObjectMap<String, SRegionProtectorCommand> commands;

    public RegionCommand(String name) {
        super(name);
        this.commands = new Object2ObjectAVLTreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.region.permission");
            return false;
        }
        if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
            this.messenger.sendMessage(sender, "command.region.available-commands", "@list", String.join(", ", this.commands.keySet()));
            return false;
        }
        if (!this.commands.containsKey(args[0])) {
            this.messenger.sendMessage(sender, "command.region.command-doesnt-exists", "@name", args[0]);
            return false;
        }
        SRegionProtectorCommand cmd = this.commands.get(args[0]);
        args = args.length == 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
        cmd.execute(sender, cmd.getName(), args);
        return false;
    }

    public void registerCommand(SRegionProtectorCommand command) {
        this.commands.put(command.getName().replace("rg", ""), command);
    }
}
