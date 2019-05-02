package Sergey_Dertan.SRegionProtector.Command.Admin;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import Sergey_Dertan.SRegionProtector.Provider.DataProvider;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MigrateCommand extends SRegionProtectorCommand {

    private final SRegionProtectorMain main;

    public MigrateCommand(SRegionProtectorMain main) {
        super("rgmigrate", "migrate");
        this.main = main;

        List<String> providers = new ArrayList<>();

        for (DataProvider.Type type : DataProvider.Type.values()) {
            providers.add(type.name().toLowerCase());
        }

        providers.remove(DataProvider.Type.UNSUPPORTED.name().toLowerCase());

        Map<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("srcprovider", new CommandParameter[]{new CommandParameter("from", false, providers.toArray(new String[0]))});
        parameters.put("targetprovider", new CommandParameter[]{new CommandParameter("to", false, providers.toArray(new String[0]))});
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
