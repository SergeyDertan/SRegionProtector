package Sergey_Dertan.SRegionProtector.Command;

import Sergey_Dertan.SRegionProtector.Command.Manage.Purchase.BuyRegionCommand;
import Sergey_Dertan.SRegionProtector.UI.Chest.ChestUIManager;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public final class RegionCommand extends SRegionProtectorCommand {

    private final Map<String, Command> commands;
    private final ExecutorService executor;
    private final boolean async;

    public RegionCommand(boolean async, int threads, boolean withNemisys) {
        super("region");

        this.setAliases(new String[]{"rg"});

        this.commands = new Object2ObjectAVLTreeMap<>(String.CASE_INSENSITIVE_ORDER);

        this.async = async;
        if (async) {
            this.executor = Executors.newFixedThreadPool(threads == -1 ? Runtime.getRuntime().availableProcessors() : threads);
            this.messenger.setAsync(true);
            ChestUIManager.setAsync(true);
            this.messenger.setWithNemisys(withNemisys);
        } else {
            this.executor = null;
        }
    }

    @Override
    public CommandDataVersions generateCustomCommandData(Player player) {
        if (!this.testPermission(player)) {
            return null;
        }

        CommandData customData = this.commandData.clone();

        List<String> aliases = new ArrayList<>();
        aliases.add("region");
        aliases.add("rg");

        customData.aliases = new CommandEnum("RegionAliases", aliases);

        customData.description = player.getServer().getLanguage().translateString(this.getDescription());
        this.commandParameters.forEach((key, par) -> {
            if (this.commands.get(key).testPermissionSilent(player)) {
                CommandOverload overload = new CommandOverload();
                overload.input.parameters = par;
                customData.overloads.put(key, overload);
            }
        });
        if (customData.overloads.size() == 0) customData.overloads.put("default", new CommandOverload());
        CommandDataVersions versions = new CommandDataVersions();
        versions.versions.add(customData);
        return versions;
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
        Command cmd = this.commands.get(args[0]);
        if (cmd == null) {
            this.messenger.sendMessage(sender, "command.region.command-doesnt-exists", "@name", args[0]);
            return false;
        }
        String[] newArgs = args.length == 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
        if (this.async && !(cmd instanceof BuyRegionCommand)) { //economy plugin may not support concurrency
            this.executor.execute(() -> cmd.execute(sender, cmd.getName(), newArgs));
        } else {
            cmd.execute(sender, cmd.getName(), newArgs);
        }
        return false;
    }

    private void updateArguments() {
        Map<String, CommandParameter[]> params = new Object2ObjectArrayMap<>();
        this.commands.forEach((k, v) -> {
            List<CommandParameter> p = new ArrayList<>();
            p.add(new CommandParameter(k, false, new String[]{k}));
            v.getCommandParameters().values().forEach(s -> p.addAll(Arrays.asList(s)));
            params.put(k, p.toArray(new CommandParameter[0]));
        });
        this.setCommandParameters(params);
    }

    public void registerCommand(Command command) {
        this.commands.put(command.getName().replace("rg", "").replace("region", "").toLowerCase(), command);
        this.updateArguments();
    }

    public void shutdownExecutor() {
        if (this.executor != null) {
            this.executor.shutdown();
            try {
                this.executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException ignore) {
            }
        }
    }
}
