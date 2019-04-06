package Sergey_Dertan.SRegionProtector.Command.Admin;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Map;

public final class MigrateCommand extends SRegionProtectorCommand {

    private final SRegionProtectorMain main;

    public MigrateCommand(SRegionProtectorMain main) {
        super("rgmigrate", "migrate");
        this.main = main;

        Map<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("srcprovider", new CommandParameter[]{new CommandParameter("from", CommandParamType.STRING, false)});
        parameters.put("targetprovider", new CommandParameter[]{new CommandParameter("to", CommandParamType.STRING, false)});
        this.setCommandParameters(parameters);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.migrate.permission");
            return false;
        }
        if (args.length < 2) {
            this.messenger.sendMessage(sender, "command.migrate.usage");
            return false;
        }
        try {
            int amount = this.main.dataMigration(args[0], args[1]);
            this.messenger.sendMessage(sender, "command.migrate.success", new String[]{"@from", "@to", "@amount"}, new String[]{args[0], args[1], Integer.toString(amount)});
        } catch (RuntimeException e) {
            this.messenger.sendMessage(sender, "command.migrate.error", new String[]{"@from", "@to", "@err"}, new String[]{args[0], args[1], e.getMessage()});
        }
        return false;
    }
}
