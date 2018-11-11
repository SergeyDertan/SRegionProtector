package Sergey_Dertan.SRegionProtector.Command.Creation;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

import java.util.Map;

public final class GetWandCommand extends SRegionProtectorCommand {

    public GetWandCommand(String name, Map<String, String> messages) {
        super(name, messages);
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, "in-game");
            return false;
        }
        if (!this.testPermission(sender)) return false;
        ((Player) sender).getInventory().addItem(Item.get(ItemID.WOODEN_AXE, 0, 1));
        this.sendMessage(sender, "wand-given");
        return true;
    }
}