package Sergey_Dertan.SRegionProtector.Command.Manage.Purchase;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Economy.AbstractEconomy;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

public final class BuyRegionCommand extends SRegionProtectorCommand {

    private RegionManager regionManager;
    private AbstractEconomy economy;

    public BuyRegionCommand(RegionManager regionManager, AbstractEconomy economy) {
        super("rgbuy", "buy");
        this.regionManager = regionManager;
        this.economy = economy;

        Object2ObjectMap<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("rgbuy", new CommandParameter[]{
                new CommandParameter("region", CommandParamType.TEXT, false),
                new CommandParameter("price", CommandParamType.INT, false)
        });
        this.setCommandParameters(parameters);
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (this.economy == null) {
            this.messenger.sendMessage(sender, "command.buy.no-economy");
            return false;
        }
        if (!(sender instanceof Player)) {
            this.messenger.sendMessage(sender, "command.buy.in-game");
            return false;
        }
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.buy.permission");
            return false;
        }
        if (args.length < 2) {
            this.messenger.sendMessage(sender, "command.buy.usage");
            return false;
        }
        Region target = this.regionManager.getRegion(args[0]);
        if (target == null) {
            this.messenger.sendMessage(sender, "command.buy.wrong-target", "@region", args[0]);
            return false;
        }
        if (target.getCreator().equalsIgnoreCase(sender.getName())) {
            this.messenger.sendMessage(sender, "command.buy.cant-buy-your-self");
            return false;
        }
        if (!target.isSelling()) {
            this.messenger.sendMessage(sender, "command.buy.doesnt-selling");
            return false;
        }
        long price = target.getSellFlagPrice();
        if (price > this.economy.getMoney((Player) sender)) {
            this.messenger.sendMessage(sender, "command.buy.no-money");
            return false;
        }
        if (price != Long.valueOf(args[1])) {
            this.messenger.sendMessage(sender, "command.buy.wrong-price");
            return false;
        }
        this.economy.addMoney(target.getCreator(), price);
        this.economy.reduceMoney(sender.getName(), price);
        this.regionManager.changeRegionOwner(target, sender.getName());
        this.messenger.sendMessage(sender, "command.buy.success", new String[]{"@region", "@price"}, new String[]{target.getName(), String.valueOf(price)});
        return false;
    }
}
