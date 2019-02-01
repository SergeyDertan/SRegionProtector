package Sergey_Dertan.SRegionProtector.Command;

import Sergey_Dertan.SRegionProtector.Command.Manage.Purchase.BuyRegionCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class RegionCommand extends SRegionProtectorCommand {

    private Object2ObjectMap<String, Command> commands;
    private Executor executor;
    private boolean async;

    public RegionCommand(boolean async) {
        super("region");

        this.setDescription(this.messenger.getMessage("command.region.description"));
        this.setPermission("sregionprotector.command.region");
        this.setAliases(new String[]{"rg"});

        this.commands = new Object2ObjectAVLTreeMap<>(String.CASE_INSENSITIVE_ORDER);

        this.registerCommand(new HelpCommand(this));

        this.async = async;
        if (async) {
            this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            this.messenger.setAsync();
        }
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.region.permission");
            return false;
        }
        if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
            this.messenger.sendMessage(sender, "command.region.available-commands");
            this.commands.forEach((k, v) -> {
                if (sender.hasPermission(v.getPermission())) sender.sendMessage(k + " - " + v.getDescription());
            });
            return false;
        }
        if (!this.commands.containsKey(args[0])) {
            this.messenger.sendMessage(sender, "command.region.command-doesnt-exists", "@name", args[0]);
            return false;
        }
        Command cmd = this.commands.get(args[0]);
        String[] newArgs = args.length == 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
        if (this.async && !(cmd instanceof BuyRegionCommand)) {
            this.executor.execute(() -> cmd.execute(sender, cmd.getName(), newArgs));
        } else {
            cmd.execute(sender, cmd.getName(), newArgs);
        }
        return false;
    }

    private void updateArguments() {
        Map<String, CommandParameter[]> params = new Object2ObjectArrayMap<>();
        this.commands.forEach((k, v) -> {
            List<CommandParameter> p = new ObjectArrayList<>();
            p.add(new CommandParameter(k, false, new String[]{k}));
            v.getCommandParameters().values().forEach(s -> {
                List<CommandParameter> l = new ObjectArrayList<>(s);
                p.addAll(l);
            });
            params.put(k, p.toArray(new CommandParameter[0]));
        });
        this.setCommandParameters(params);
    }

    public void registerCommand(Command command) {
        this.commands.put(command.getName().replace("rg", "").replace("region", "").toLowerCase(), command);
        this.updateArguments();
    }

    class HelpCommand extends Command {
        private RegionCommand mainCMD;

        HelpCommand(RegionCommand mainCMD) {
            super("help");
            this.mainCMD = mainCMD;
            this.setCommandParameters(new Object2ObjectArrayMap<>());
        }

        @Override
        public boolean execute(CommandSender sender, String s, String[] strings) {
            return this.mainCMD.execute(sender, s, new String[0]);
        }
    }
}
